package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.ADRTag;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.status.ADRStatusType;
import com.buschmais.backend.image.Image;
import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.vars.OtherConstantsFrontend;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@UIScope
@Component
public class ADRExplorerComponent extends Grid<ADR> {

	private final ADRDao adrDao;
	private final UserDao usrDao;
	private final ImageDao imgDao;


	Set<ADRTag> TagFilters;
	List<ADR> ToShowList =  new LinkedList();
	GridListDataView<ADR> dataView;
	private Column<ADR> titleColumn, statusColumn, tagColumn, authorColumn;
	private Filter filter;




	public ADRExplorerComponent(@Autowired ADRDao adrDao, @Autowired UserDao usrDao, @Autowired ImageDao imgDao){

		this.adrDao = adrDao;
		this.usrDao = usrDao;
		this.imgDao = imgDao;

		ToShowList = adrDao.findAll();

		addAttachListener(event -> {getDataProvider().refreshAll();});



		//Column<ADR> nameColumn = addColumn(ADR::getName).setHeader(StringConstantsFrontend.ADRCREATE_NAME).setSortable(true).setAutoWidth(true);
		titleColumn = addColumn(ADR::getTitle).setHeader(StringConstantsFrontend.ADRCREATE_TITLE).setAutoWidth(true).setSortable(true);

		authorColumn = addColumn(new ComponentRenderer<>(adr -> {

			User usr = adr.getAuthor().get();

			Avatar av = new Avatar(usr.getUserName());

			Image tempImg = usr.getProfilePicture();
			if(tempImg != null) {
				this.imgDao.getInputStream(tempImg).ifPresent(inputStream -> {
					av.setImageResource(new StreamResource("db-stream", () -> inputStream));
					
				});
			}

			HorizontalLayout lay = new HorizontalLayout();
			lay.add(av,  new  Span(usr.getUserName()));
			return new Span(lay);

		})).setHeader(StringConstantsFrontend.ADRCREATE_AUTHOR)
				.setSortable(true).setComparator(new Comparator<ADR>() {
					@Override
					public int compare(ADR o1, ADR o2) {
						return o1.getAuthor().orElse(OtherConstantsFrontend.DEFAULT_DELETED_USER).getUserName().compareTo(o2.getAuthor().orElse(OtherConstantsFrontend.DEFAULT_DELETED_USER).getUserName());
					}
				})
		.setAutoWidth(true);

		statusColumn = addColumn(createStatusComponentRenderer()).setHeader(StringConstantsFrontend.ADRCREATE_STATUS).setSortable(true)
				.setComparator(new Comparator<ADR>() {
					@Override
					public int compare(ADR o1, ADR o2) {
						return o1.getStatusType().ordinal()-o2.getStatusType().ordinal();
					}
				})
		.setAutoWidth(true);

		tagColumn = addColumn(createTagComponentRenderer()).setHeader(StringConstantsFrontend.ADRCREATE_TAGS).setAutoWidth(true);

		dataView = setItems(ToShowList);
		Filter filter = new Filter(dataView);

		getHeaderRows().clear();
		HeaderRow headerRow = appendHeaderRow();
		headerRow.getCell(titleColumn).setComponent(createFilterHeaderText(StringConstantsFrontend.ADRCREATE_TITLE, filter::setTitle));
		headerRow.getCell(authorColumn).setComponent(createFilterHeaderText(StringConstantsFrontend.ADRCREATE_AUTHOR,filter::setAuthor));
		headerRow.getCell(statusColumn).setComponent(createFilterHeaderComboBox(StringConstantsFrontend.ADRCREATE_STATUS, filter::setStatus));
		headerRow.getCell(tagColumn).setComponent(createFilterHeaderTagSelect(StringConstantsFrontend.ADRCREATE_TAGS,filter::setTags));




	}


	private static final SerializableBiConsumer<Span,ADR> TagComponentUpdater = (span,adr) -> {
		List<ADRTag> tagList = adr.getTags();

		tagList.forEach(Tag -> {
			Span TagSpan = new Span(Tag.getTagText());
			TagSpan.getElement().getThemeList().add("badge pill");
			span.add(TagSpan);
		});
	};


	private static SerializableBiConsumer<Span,ADR> StatusComponentUpdater = (span, adr) -> {
			ADRStatusType Status = adr.getStatusType();
			switch (Status){
				case CREATED:
					span.getElement().setAttribute("theme","badge");
					span.setText("  " + ADRStatusType.CREATED.getName().toUpperCase());
					break;
				case PROPOSED:
					span.getElement().setAttribute("theme","badge primary");
					span.setText("  " + ADRStatusType.PROPOSED.getName().toUpperCase());
					break;
				case APPROVED:
					span.getElement().setAttribute("theme","badge success primary");
					span.addClassName("confirm-button");
					span.setText("  " + ADRStatusType.APPROVED.getName().toUpperCase());
					break;
				case REFUSED:
					span.getElement().setAttribute("theme","badge error primary");
					span.addClassName("cancel-button");
					span.setText("  " + ADRStatusType.REFUSED.getName().toUpperCase());
					break;
				case SUPERSEDED:
					span.getElement().setAttribute("theme","badge contrast primary");
					span.setText("  " + ADRStatusType.SUPERSEDED.getName().toUpperCase());
					break;
				case INTERNALLY_PROPOSED:
					span.getElement().setAttribute("theme", "badge error");
					span.setText("  " + ADRStatusType.INTERNALLY_PROPOSED.getName().toUpperCase());
					break;
				default:
					span.getElement().setAttribute("theme", "badge error");
					span.setText("Easteregg :) \n -Leo");
				break;
			}
	};


	private static ComponentRenderer<Span,ADR> createStatusComponentRenderer(){
		return new ComponentRenderer<>(Span::new,StatusComponentUpdater);
	}

	private static ComponentRenderer<Span,ADR> createTagComponentRenderer(){
		return new ComponentRenderer<>(Span::new,TagComponentUpdater);
	}



	private com.vaadin.flow.component.Component createFilterHeaderText(String LabelText, Consumer<String> filterChangeConsumer){
		Label label = new Label(LabelText);
		label.getStyle().set("padding-top", "var(--lumo-space-m)")
				.set("font-size", "var(--lumo-font-size-xs)");
		TextField textField = new TextField();
		textField.setValueChangeMode(ValueChangeMode.EAGER);
		textField.setClearButtonVisible(true);
		textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		textField.setWidthFull();
		textField.getStyle().set("max-width", "100%");
		textField.addValueChangeListener(
				e -> filterChangeConsumer.accept(e.getValue()));
		VerticalLayout layout = new VerticalLayout(label, textField);
		layout.getThemeList().clear();
		layout.getThemeList().add("spacing-xs");

		return layout;
	}


	private com.vaadin.flow.component.Component createFilterHeaderComboBox(String LabelText, Consumer<ADRStatusType> filterChangeConsumer){
		Label label = new Label(LabelText);
		label.getStyle().set("padding-top", "var(--lumo-space-m)")
				.set("font-size", "var(--lumo-font-size-xs)");
		ComboBox<ADRStatusType> comboBox = null;
		comboBox = new ComboBox<>();
		comboBox.setItems(ADRStatusType.values());
		comboBox.setClearButtonVisible(true);
		comboBox.setWidthFull();
		comboBox.getStyle().set("max-width", "100%");
		comboBox.addValueChangeListener(
				e -> filterChangeConsumer.accept(e.getValue()));
		VerticalLayout layout = new VerticalLayout(label, comboBox);
		layout.getThemeList().clear();
		layout.getThemeList().add("spacing-xs");

		return layout;
	}

	private com.vaadin.flow.component.Component createFilterHeaderTagSelect(String LabelText, Consumer<Set<ADRTag>> filterChangeConsumer){
		Set<ADRTag> SelectedTags = new HashSet<>();
		Label label = new Label(LabelText);
		label.getStyle().set("padding-top", "var(--lumo-space-m)")
				.set("font-size", "var(--lumo-font-size-xs)");
		ComboBox<ADRTag> comboBox = new ComboBox<>();
		comboBox.setItemLabelGenerator(ADRTag::getTagText);
		comboBox.setItems(adrDao.findAllTagsMatchRegex(".*").stream().collect(Collectors.toSet()));
		comboBox.setClearButtonVisible(true);
		comboBox.setWidthFull();
		comboBox.getStyle().set("max-width", "100%");

		HorizontalLayout badges = new HorizontalLayout();
		badges.getStyle().set("flex-wrap", "wrap");

		comboBox.addValueChangeListener(event -> {
				ADRTag Tag=event.getValue();
				if(Tag!=null && !SelectedTags.contains(Tag)) {
					Button xBtn = new Button(VaadinIcon.CLOSE_SMALL.create());
					xBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
					xBtn.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
					xBtn.getElement().setAttribute("aria-label", "Clear filter: " + Tag.getTagText());
					xBtn.getElement().setAttribute("title", "Clear filter: " + Tag.getTagText());


					Span badge = new Span(new Span(Tag.getTagText()), xBtn);
					badge.getElement().getThemeList().add("badge contrast pill");

					xBtn.addClickListener(event2 -> {
						badge.getElement().removeFromParent();
						SelectedTags.removeIf(T -> (T.equals(Tag)));
						filterChangeConsumer.accept(SelectedTags);
						comboBox.clear();
					});

					SelectedTags.add(Tag);
					badges.add(badge);
				}
			filterChangeConsumer.accept(SelectedTags);
		});

		VerticalLayout layout = new VerticalLayout(label, comboBox,badges);
		layout.getThemeList().clear();
		layout.getThemeList().add("spacing-xs");

		return layout;
	}




	private class Filter {
		private final GridListDataView<ADR> dataView;

		private String Author, Title, TagEx;
		private ADRStatusType Status;
		private List<ADRTag> Tags;

		public Filter(GridListDataView<ADR> dataView){
			this.dataView = dataView;
			this.dataView.addFilter(this::dataFilter);
		}

		public void setAuthor(String Author){
			this.Author = Author;
			this.dataView.refreshAll();
		}

		public void setTitle(String Title){
			this.Title = Title;
			this.dataView.refreshAll();
		}

		public void setStatus(ADRStatusType Status){
			this.Status = Status;
			this.dataView.refreshAll();
		}

		public void setTags(Set<ADRTag> FilterTags){
			this.Tags=FilterTags.stream().toList();
			this.dataView.refreshAll();
		}

		private boolean dataFilter(ADR adr) {
			boolean matchesAuthor = match(adr.getAuthor().orElse(OtherConstantsFrontend.DEFAULT_DELETED_USER).getUserName(),Author);
			boolean matchesTitle = match(adr.getTitle(), Title);
			boolean matchesStatus = matchStatus(adr.getStatusType(), Status);
			boolean matchesTags = matchTag(adr.getTags(),Tags);

			return matchesAuthor && matchesTitle && matchesStatus && matchesStatus && matchesTags;
		}

		private boolean matchTag(List<ADRTag> tags, List<ADRTag> tarTags) {
			if(tarTags == null || tarTags.isEmpty() || tarTags.contains(null))
				return true;
			boolean ret = true;
			for (ADRTag tarTag : tarTags) {
				if(!tags.contains(tarTag)){
					ret = false;
					break;
				}

			}

			return ret;
		}

		private boolean matchStatus(ADRStatusType adrStatus, ADRStatusType status) {
			return adrStatus.equals(status) || status == null;
		}

		private boolean match(String value, String searchTerm) {
			return searchTerm == null || searchTerm.isEmpty() || value.toLowerCase().contains(searchTerm.toLowerCase());
		}



	}





}
