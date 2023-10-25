package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADRTag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@CssImport(value = "./themes/adr-workbench/vaadin-components/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class TagSelect extends VerticalLayout{

	private ComboBox<ADRTag> Combobox;
	private HorizontalLayout badges;
	private List<Span> badgeList = new LinkedList<Span>();
	private Set<ADRTag> value = new HashSet<ADRTag>();


	private Set<ADRTag> selectableTags = new HashSet<ADRTag>();
	private Set<ADRTag> allTags = new HashSet<ADRTag>();

	public TagSelect(String name,List<ADRTag> Tags) {
		badges = new HorizontalLayout();
		badges.getStyle().set("flex-wrap","wrap");

		//Do this to actually copy the Taglists in Memory and not reasign the Pointer

		allTags.addAll(Tags);
		selectableTags.addAll(allTags);

		Combobox = new ComboBox<>(name);

		Combobox.setItems(selectableTags);
		Combobox.setItemLabelGenerator(ADRTag::getTagText);

		Combobox.addValueChangeListener(e -> {
			TagBadgeCreateCaller(e.getValue());
			selectableTags.remove(e.getValue());
			Combobox.setItems(selectableTags);
		});
		Combobox.setAllowCustomValue(true);

		Combobox.addCustomValueSetListener(e-> {

			ADRTag Tag = new ADRTag();
			Tag.setTagText(e.getDetail());
			selectableTags.add(Tag);
			Combobox.setItems(selectableTags);
			Combobox.setValue(Tag);
		});
		add(Combobox, badges);
		setPadding(false);
		setSizeUndefined();
	}

	private Span createTagBadge(ADRTag Tag) {
			Button xBtn = new Button(VaadinIcon.CLOSE_SMALL.create());
			xBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
			xBtn.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
			xBtn.getElement().setAttribute("aria-label", "Clear filter: " + Tag.getTagText());
			xBtn.getElement().setAttribute("title", "Clear filter: " + Tag.getTagText());


			Span badge = new Span(new Span(Tag.getTagText()), xBtn);
			badge.getElement().getThemeList().add("badge contrast pill");

			xBtn.addClickListener(event -> {
				badge.getElement().removeFromParent();
				value.removeIf(T -> (T.equals(Tag)));
				selectableTags.add(Tag);
				Combobox.setItems(selectableTags);
			});
			return badge;

	}


	private void TagBadgeCreateCaller(ADRTag Tag){
		if(Tag != null){
			Span TagBadge = createTagBadge(Tag);
			if(value.add(Tag))
				badges.add(TagBadge);

		}


	}


	public List<ADRTag> getValue(){
		return this.value.stream().toList();
	}

	public TagSelect setValue(List<ADRTag> Tags){
		this.clear();

		Tags.forEach(Tag ->{
			this.value.add(Tag);
		});
		value.forEach(Tag -> {
			this.badges.add(createTagBadge(Tag));
		});
		selectableTags.removeAll(value);
		Combobox.setItems(selectableTags);

		return this;
	}

	public TagSelect clear() {
		this.value = new HashSet<ADRTag>();
		this.badges.removeAll();
		selectableTags = new HashSet<ADRTag>();
		selectableTags.addAll(allTags);
		return this;
	}
}
