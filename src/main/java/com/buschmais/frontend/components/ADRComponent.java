package com.buschmais.frontend.components;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.voting.VoteType;
import com.buschmais.frontend.vars.OtherConstantsFrontend;
import com.buschmais.frontend.views.adrVote.ADRVoteView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouteConfiguration;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@CssImport(value = "./themes/adr-workbench/kanban/adr-component.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
public class ADRComponent extends Div {

	// adr
	private final ADR adr;

	// user
	private final User user;

	// components
	private Label titleLabel;

	private Label authorLabelText;
	private Label authorLabel;

	private Label voteLabelText;
	private Label voteLabel;

	private Label detailsLabelText;
	private Button detailsButton;

	private VerticalLayout mainLayout;
	private HorizontalLayout authorLayout;
	private HorizontalLayout voteLayout;

	private VerticalLayout authorLabelTextLayout;
	private VerticalLayout authorLabelLayout;

	private VerticalLayout voteLabelTextLayout;
	private VerticalLayout voteLabelLayout;

	private Div separatorLine;

	private Icon statusLockIcon;

	private Div tags;

	private VoteResultBar voteResultBar;

	public ADRComponent(@NotNull ADR adr, @NotNull User user, @NonNull ADRDao adrService) {
		this.adr = adr;
		this.user = user;
		init();
	}

	private void setupComponents() {

		/* define components */
		{
			this.titleLabel = new Label(this.adr.getTitle());
			this.separatorLine = new Div();

			this.mainLayout = new VerticalLayout();
			this.authorLayout = new HorizontalLayout();
			this.voteLayout = new HorizontalLayout();

			this.authorLabelText = new Label("Autor");
			// this.authorLabel = new Label(this.adr.getAuthor());
			this.authorLabel = new Label(this.adr.getAuthor().orElse(OtherConstantsFrontend.DEFAULT_DELETED_USER).getUserName()); // TODO: MAx 10 characters

			this.authorLabelTextLayout = new VerticalLayout();
			this.authorLabelLayout = new VerticalLayout();

			this.voteLabelText = new Label("Voting");

			this.voteLabel = new Label();

			this.voteLabelTextLayout = new VerticalLayout();
			this.voteLabelLayout = new VerticalLayout();

			this.detailsLabelText = new Label("Details");
			this.detailsButton = new Button("Details");

			this.tags = new Div();

			this.statusLockIcon = new Icon(VaadinIcon.UNLOCK);

			if (!this.adr.getStatus().isWritable()) {
				this.voteResultBar = new VoteResultBar(0);
				this.adr.getStatus().adrReviewAsOpt().ifPresent(review -> {
					if(this.adr.getStatus().isVotable()){
						Optional<Boolean> userHasVoted = review.userHasVoted(this.user);
						if (userHasVoted.isPresent() && userHasVoted.get()) {
							this.voteResultBar = new VoteResultBar(
									review.getVoteResult().get(VoteType.FOR),
									review.getVoteResult().get(VoteType.AGAINST),
									review.getInvitedVoters().size());
						} else if(userHasVoted.isPresent()){
							this.voteLabel.setText("Stimme jetzt ab");
						}
						else {
							this.voteLabel.setText("nicht eingeladen");
						}
					}else{
						this.voteResultBar = new VoteResultBar(
								review.getVoteResult().get(VoteType.FOR),
								review.getVoteResult().get(VoteType.AGAINST),
								review.getInvitedVoters().size());
					}
				});
			} else {
				this.voteLabel.setText("---");
			}

		}

		/* set properties */
		{
			this.authorLayout.setSpacing(false);
			this.voteLayout.setSpacing(false);

			this.authorLabelTextLayout.setWidth("40%");
			this.voteLabelTextLayout.setWidth("40%");

			this.titleLabel.addClassName("adr-component-title");

			this.separatorLine.addClassName("adr-separator-line");

			this.authorLabelText.addClassName("adr-component-sub-heading");
			this.voteLabelText.addClassName("adr-component-sub-heading");

			this.authorLabelTextLayout.addClassName("author-label-text-layout");
			this.authorLabelLayout.addClassName("author-label-layout");
			this.voteLabelTextLayout.addClassName("vote-label-text-layout");
			this.voteLabelLayout.addClassName("vote-label-layout");
			this.mainLayout.addClassName("adr-main-layout");
			this.authorLayout.addClassName("adr-author-layout");
			this.voteLayout.addClassName("adr-vote-layout");


			// Check if user has permission
			if (this.adr.canRead(this.user)) {
				this.detailsButton.setEnabled(false);
				this.detailsButton.addClassName("adr-details-button-disabled");
			} else {
				this.detailsButton.addClassName("adr-details-button-enabled");
			}
		}

		// listener
		addClickListener();
	}

	private void addComponents() {
		this.add(this.titleLabel);
		this.add(this.separatorLine);

		this.authorLabelTextLayout.add(this.authorLabelText);
		this.authorLabelLayout.add(this.authorLabel);

		this.voteLabelTextLayout.add(this.voteLabelText);
		if (!this.adr.getStatus().isWritable()) {
			if(this.adr.getStatus().isVotable()){
				this.adr.getStatus().adrReviewAsOpt().ifPresent(review -> {
					if (review.userHasVoted(this.user).orElse(false)) {
						this.voteLabelLayout.add(this.voteResultBar);
					} else {
						this.voteLabelLayout.add(this.voteLabel);
					}
				});
			}else{
				this.voteLabelLayout.add(this.voteResultBar);
			}
		} else {
			this.voteLabelLayout.add(this.voteLabel);
		}

		this.authorLayout.add(this.authorLabelTextLayout, this.authorLabelLayout);
		this.voteLayout.add(this.voteLabelTextLayout, this.voteLabelLayout);

		this.mainLayout.add(this.authorLayout);
		this.mainLayout.add(this.voteLayout);

		this.add(this.mainLayout);

		this.add(new HorizontalLayout(this.detailsButton));
	}

	private void init() {
		/* properties of div */
		this.addClassName("adr-main-div");

		// switch colors
		switch (this.adr.getStatus().getType()) {
			case CREATED, INTERNALLY_PROPOSED -> this.addClassName("adr-component-status-created");
			case PROPOSED -> this.addClassName("adr-component-status-proposed");
			case APPROVED -> this.addClassName("adr-component-status-approved");
			case REFUSED -> this.addClassName("adr-component-status-refused");
			case SUPERSEDED -> this.addClassName("adr-component-status-superseded");
		}

		// setup included components
		setupComponents();
		addComponents();
	}

	private void addClickListener() {
		this.detailsButton.addClickListener(event -> {
			/* Routing */
			String route = RouteConfiguration.forSessionScope().getUrl(ADRVoteView.class, this.adr.getId());
			this.getUI().ifPresent(page -> page.navigate(route));
		});
	}

	public ADR getAdr() {
		return adr;
	}

	public Button getDetailsButton() {
		return detailsButton;
	}

	public VerticalLayout getMainLayout() {
		return mainLayout;
	}
}
