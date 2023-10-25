package com.buschmais.frontend.views.adrCreate;

import com.buschmais.Application;
import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.ADRTag;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.status.ADRStatusCreated;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.components.*;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.buschmais.frontend.views.adrVote.ADRVoteView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Route(value="ADREdit", layout = MainLayout.class)
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
@PageTitle(StringConstantsFrontend.ADRCREATE_EDITADR)
public class ADRRichCreateView extends VerticalLayout implements HasUrlParameter<String>, BeforeEnterObserver {

	//name of new ADR
	private final TextField name = new TextField(StringConstantsFrontend.ADRCREATE_NAME);
	//title of new ADR
	private final TextField title = new TextField(StringConstantsFrontend.ADRCREATE_TITLE);
	//context, decision and consequences of new ADR

	private final Label contextLabel = new Label(StringConstantsFrontend.ADRCREATE_CONTEXT);
	private final RichTextEditor context = new RichTextEditor();
	private final Label decisionLabel = new Label(StringConstantsFrontend.ADRCREATE_DECISION);
	private final RichTextEditor decision = new RichTextEditor();
	private final Label consequencesLabel = new Label(StringConstantsFrontend.ADRCREATE_CONSEQUENCES);
	private final RichTextEditor consequences = new RichTextEditor();
	private final TextField id = new TextField();

	private ErrorNotification errNot;
	private SuccessNotification succNot;

	//Scroller so that we can fit everything
	private Scroller scroller;

	//Contents of the Scroller
	private final VerticalLayout mainContent = new VerticalLayout();
	//Title of the Scroller
	H2 editADR = new H2();
	private final Header header = new Header();

	private TagSelect tagSelect;

	private StringSelect supersededSelect;

	private final ADRDao adrDao;
	private final UserDao userDao;

	private ADR adr;

	private boolean newadr = true;


	Binder<ADR> binder = new Binder<>(ADR.class);

	public ADRRichCreateView(@Autowired ADRDao adrDao, @Autowired UserDao userDao) {
		this.userDao = userDao;
		this.adrDao = adrDao;
		setSizeFull();

		//setClassNames();

		context.setWidthFull();
		context.setHeight("300px");

		//context.setValueChangeMode(ValueChangeMode.EAGER);
		//context.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 100));

		decision.setWidthFull();
		decision.setHeight("300px");
		//decision.setValueChangeMode(ValueChangeMode.EAGER);
		//decision.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 50));

		consequences.setWidthFull();
		consequences.setHeight("300px");
		//consequences.setValueChangeMode(ValueChangeMode.EAGER);
		//consequences.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 80));


		//create the Scroller with the Content of the Editor
		scroller = new Scroller(mainContentCreate());
		scroller.setSizeFull();
		scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

		//add the Title of the Page to the Header
		header.add(editADR);

		add(header, scroller);

		configureBinder();

	}

	//buttonsview to create
	private HorizontalLayout buttonCreate() {

		Button save = new Button(StringConstantsFrontend.GENERAL_DIALOG_BUTTON_SAVE);
		save.addClassName("confirm-button");
		Button cancel = new Button(StringConstantsFrontend.GENERAL_DIALOG_BUTTON_CANCEL);
		cancel.addClassName("cancel-button");
		Button create = new Button(StringConstantsFrontend.ADRCREATE_CREATE);
		create.addClassName("blue-button");
		Button load = new Button(StringConstantsFrontend.ADRCREATE_LOAD);
		load.addClassName("blue-button");

		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

		save.addClickListener((event -> {
			boolean success = false;
			if (newadr) {
				adr = new ADR(
						Application.root,
						name.getValue(),
						title.getValue(),
						userDao.getCurrentUser(),
						context.getValue(), consequences.getValue(), decision.getValue());
				adr.setStatus(new ADRStatusCreated());

				adr.getTags().clear();
				tagSelect.getValue().forEach(tag -> adr.addTag(tag));

				synchronized (adrDao) {
					List<String> supersededTitles = supersededSelect.getSelected();
					Set<String> supersededIds = new HashSet<>();
					for (String name : supersededTitles) {
						supersededIds.addAll(adrDao.findAllByTitle(name).stream().map(ADR::getId).collect(Collectors.toSet()));
					}
					adr.setSupersededIds(supersededIds);

					adr = adrDao.save(adr);
				}

				success = true;
			} else {
				synchronized (adrDao) {
					adr = adrDao.findById(adr.getId()).orElse(null);
					if (adr != null) {
						adr.setTitle(title.getValue());
						adr.setName(name.getValue());
						adr.setContext(context.getValue());
						adr.setConsequences(consequences.getValue());
						adr.setDecision(decision.getValue());

						adr.getTags().clear();
						tagSelect.getValue().forEach(tag -> adr.addTag(tag));

						List<String> supersededTitles = supersededSelect.getSelected();
						Set<String> supersededIds = new HashSet<>();
						for (String name : supersededTitles) {
							supersededIds.addAll(adrDao.findAllByTitle(name).stream().map(ADR::getId).collect(Collectors.toSet()));
						}
						adr.setSupersededIds(supersededIds);

						try {
							adrDao.save(adr);
							success = true;
						} catch (Exception e) {
							System.out.println(Arrays.toString(e.getStackTrace()));
						}
					}
				}
			}

			if (success) {
				succNot = new SuccessNotification(StringConstantsFrontend.ADRCREATE_SUCC);
				succNot.open();
				Broadcaster.broadcastMessage(BroadcastListener.Event.ADR_CHANGED, adr.getId(), this);
				//Change URL to include adrId
				String route = RouteConfiguration.forSessionScope().getUrl(ADRVoteView.class, this.adr.getId());    //TODO: Eric hier ^^
				this.getUI().ifPresent(ui -> ui.navigate(route));
			} else {
				errNot = new ErrorNotification(StringConstantsFrontend.ADRCREATE_ERROR_GENERAL);
				errNot.open();
			}
		}));

		cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancel.addClickListener((event -> {
		}));


		create.addClickListener((event -> {
			newadr = true;
			loadVals("", "", new LinkedList<>(), Stream.empty(), "", "", "");

			String route = RouteConfiguration.forSessionScope().getUrl(ADRRichCreateView.class, "");
			this.getUI().ifPresent(page -> page.navigate(route));
		}));


		load.addClickListener((event -> {

			Notification not = new Notification();
			TextField txt = new TextField();
			Div diag = new Div(new Text("Id to Load (Temp):"));
			Button conf = new Button(StringConstantsFrontend.ADRCREATE_LOAD);
			conf.addClickListener((event1 -> {
				ADR adr1 = adrDao.findById(txt.getValue()).orElse(null);
				if (adr1 != null) {

					loadADR(adr1);

					String route = RouteConfiguration.forSessionScope().getUrl(ADRRichCreateView.class, this.adr.getId());
					this.getUI().ifPresent(page -> page.navigate(route));

				} else {
					errNot = new ErrorNotification(StringConstantsFrontend.ADRCREATE_ERROR_ID_NOT_FOUND);
					errNot.open();
					//Notification errnot = Notification.show("ID not Found");
				}
				not.close();
			}));
			txt.setWidth("300px");

			HorizontalLayout layout = new HorizontalLayout(diag, txt, conf);
			layout.setAlignItems(Alignment.CENTER);


			not.add(layout);
			not.setPosition(Notification.Position.BOTTOM_CENTER);
			not.open();

		}));
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.add(save, cancel, create);
		return buttons;


	}

	private VerticalLayout mainContentCreate() {
		synchronized (adrDao) {
			tagSelect = new TagSelect(StringConstantsFrontend.ADR_TAGS, adrDao.findAllTagsMatchRegex(".*"));
			supersededSelect = new StringSelect(StringConstantsFrontend.ADRCREATE_SUPERSEEDES_FOLLOWING_ADRS);

			List<ADR> supersededADRs = new LinkedList<>();
			if (adr != null) adr.getSupersededIds().forEach(id -> adrDao.findById(id).ifPresent(supersededADRs::add));
			supersededSelect.load(adrDao.findAll().stream()
							.filter(adr -> !adr.isSuperseded())
							.map(ADR::getTitle)
							.filter(n -> this.adr == null || !n.equals(this.adr.getTitle())),
					supersededADRs.stream().map(ADR::getTitle));
		}
		mainContent.add(name, title, tagSelect, supersededSelect, contextLabel, context, decisionLabel, decision, consequencesLabel, consequences, buttonCreate());
		mainContent.setSizeUndefined();
		return mainContent;
	}


	public void loadVals(String name,
						 String title,
						 List<ADRTag> Tags,
						 Stream<String> superseded,
						 String context,
						 String decision,
						 String consequences) {
		this.name.setValue(name);
		this.title.setValue(title);
		this.tagSelect.setValue(Tags);
		this.context.load(context);
		this.decision.load(decision);
		this.consequences.load(consequences);
		this.supersededSelect.load(adrDao.findAll().stream()
						.filter(adr -> !adr.isSuperseded())
						.map(ADR::getTitle)
						.filter(n -> !n.equals(name)),
				superseded);
	}

	public void loadADR(ADR adr) {
		newadr = false;
		this.adr = adr;
		List<ADR> supersededADRs = new LinkedList<>();
		if (adr != null) adr.getSupersededIds().forEach(id -> adrDao.findById(id).ifPresent(supersededADRs::add));
		loadVals(adr.getName(), adr.getTitle(), adr.getTags(), supersededADRs.stream().map(ADR::getTitle), adr.getContext(), adr.getDecision(), adr.getConsequences());

	}

	private void configureBinder() {
		binder.forField(name)
				.withValidator(
						name -> !name.isEmpty(),
						StringConstantsFrontend.ADRCREATE_NAME + StringConstantsFrontend.ADRCREATE_ERROR_FIELD_EMPTY)
				.bind(ADR::getName, ADR::setName);
		binder.bind(title, ADR::getTitle, ADR::setTitle);
		binder.bind(context, ADR::getContext, ADR::setContext);
		binder.bind(decision, ADR::getDecision, ADR::setDecision);
		binder.bind(consequences, ADR::getConsequences, ADR::setConsequences);

	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		ADR adr1 = null;
		if (parameter != null) {
			this.editADR.setText(StringConstantsFrontend.ADRCREATE_EDITADR);
			adr1 = adrDao.findById(parameter).orElse(null);
		}else{
			this.editADR.setText(StringConstantsFrontend.ADRCREATE_CREATEADR);
		}

		if (adr1 != null) {
			loadADR(adr1);
		}//else{
		//Notification errnot = Notification.show(StringConstantsFrontend.ADRCREATE_ERROR_ID_NOT_FOUND);
		//}
	}


	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if (adr == null || adr.getId() == null) return;
		adrDao.findById(this.adr.getId()).ifPresentOrElse(adr -> {
			if (!adr.canWrite(userDao.getCurrentUser()))
				beforeEnterEvent.rerouteTo(StringConstantsFrontend.LANDING_PAGE_PATH);
		}, () -> beforeEnterEvent.rerouteTo(StringConstantsFrontend.LANDING_PAGE_PATH));
	}

}