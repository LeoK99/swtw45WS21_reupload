package com.buschmais.frontend.views.adrVote;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.status.ADRStatusType;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.notifications.VotingPendingNotification;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.backend.voting.ADRReview;
import com.buschmais.backend.voting.UserIsNotInvitedException;
import com.buschmais.backend.voting.VoteType;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.components.*;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.buschmais.frontend.views.adrCreate.ADRRichCreateView;
import com.vaadin.collaborationengine.CollaborationMessageInput;
import com.vaadin.collaborationengine.CollaborationMessageList;
import com.vaadin.collaborationengine.CollaborationMessagePersister;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

//@Route(value = "vote", layout = MainLayout.class)
@CssImport(value = "./themes/adr-workbench/adrVoting/label.css")
@CssImport(value = "./themes/adr-workbench/adrVoting/comment-section.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
@CssImport(value = "./themes/adr-workbench/adrVoting/adr-information.css")
@PageTitle("ADR Details")
public class ADRVoteView extends HorizontalLayout implements HasUrlParameter<String>, BroadcastListener, BeforeEnterObserver, BeforeLeaveObserver {

	/* ADR */
	private String adrId;
	private final ADRDao adrDao;
	private final ADRAccessDao accessGroupService;
	private final UserDao userDao;
	private final ImageDao imageService;

	/* Components */
	private ErrorNotification invalidIdNotification;
	private VoteResultBar voteResultBar;

	// overall layouts
	private VerticalLayout leftLayout;
	private VerticalLayout rightLayout;

	private HorizontalLayout adrInformationLayout;
	private HorizontalLayout adrInformationAuthorStatusLayout;
	private HorizontalLayout adrActionButtonsLayout;

	// information about adr (left side)
	private VerticalLayout adrTopInformationLayout;
	private VerticalLayout titleLayout;
	private VerticalLayout authorLayout;
	private VerticalLayout contextLayout;
	private VerticalLayout decisionLayout;
	private VerticalLayout consequencesLayout;
	private VerticalLayout supersedesLayout;

	// buttons layout
	private VerticalLayout adrButtonsLayout;
	private HorizontalLayout actionButtonsLayout;

	// voting and comments (right side)
	private VerticalLayout voteLayout;
	private HorizontalLayout voteTitleLayout;
	private VerticalLayout commentLayout;
	private VerticalLayout commentMessagesLayout;
	private HorizontalLayout sendCommentButtonLayout;

	private HorizontalLayout voteIconLayout;

	// Label
	private Label statusLabel;
	private Label titleLabel;
	private Label authorLabel;
	private RichTextEditor contextLabel;
	private RichTextEditor decisionLabel;
	private RichTextEditor consequencesLabel;
	private RichTextEditor supersedesLabel;

	private Label separatorAuthorStatus;

	private Label adrInformationLabel;
	private Label titleStringLabel;
	private Label authorStringLabel;
	private Label contextStringLabel;
	private Label decisionStringLabel;
	private Label consequencesStringLabel;
	private Label supersedesStringLabel;

	private Label voteLabel;
	private Label voteNotStartedYet;
	private Label votingSeparatorLabel;
	private Label votingProLabel;
	private Label votingContraLabel;
	private Label votingNotYetVotedLabel;
	private Label votingNotAllowedToVote;

	private Label commentLabel;

	// buttons
	private Button editButton;
	private Button accessGroupsButton;
	private Button sendButton;
	private Button startVoteButton;
	private Button endVoteButton;
	private Button inviteVoterButton;
	private Button proposeDirectlyButton;

	// text areas
	private TextArea sendMessageArea;

	// icons
	private Icon thumbsUpIcon;
	private Icon thumbsDownIcon;

	// divs
	private Div titleDiv;
	private Div authorDiv;
	private Div contextDiv;
	private Div decisionDiv;
	private Div consequencesDiv;
	private Div supersedesDiv;

	// collaboration
	private CollaborationMessageList collabMessageList;
	private CollaborationMessageInput collabMessageInput;
	private final CollaborationMessagePersister collaborationMessagePersister;
	private UserInfo userInfo;

	final String htmlRegEx = "<[^>]*>";

	public ADRVoteView(@Autowired ADRDao adrService, @Autowired ADRAccessDao accessGroupService, @Autowired UserDao userService, @Autowired ImageDao imageService, @Autowired CollaborationMessagePersister collaborationMessagePersister) {
		this.adrDao = adrService;
		this.accessGroupService = accessGroupService;
		this.userDao = userService;
		this.imageService = imageService;
		this.collaborationMessagePersister = collaborationMessagePersister;
		Broadcaster.registerListener(this);
		addDetachListener((DetachEvent e) -> Broadcaster.unregisterListener(this));

	}

	private void setupComponents(ADR adr) {

		User u = this.userDao.getCurrentUser();
		this.userInfo = new UserInfo(u.getId(), u.getUserName());

		// define components
		{
			// layouts
			this.leftLayout = new VerticalLayout();
			this.rightLayout = new VerticalLayout();

			this.adrTopInformationLayout = new VerticalLayout();
			this.adrInformationLayout = new HorizontalLayout();
			this.adrInformationAuthorStatusLayout = new HorizontalLayout();
			this.adrActionButtonsLayout = new HorizontalLayout();

			this.titleLayout = new VerticalLayout();
			this.authorLayout = new VerticalLayout();
			this.contextLayout = new VerticalLayout();
			this.decisionLayout = new VerticalLayout();
			this.consequencesLayout = new VerticalLayout();
			this.supersedesLayout = new VerticalLayout();

			this.adrButtonsLayout = new VerticalLayout();
			this.actionButtonsLayout = new HorizontalLayout();

			this.voteLayout = new VerticalLayout();
			this.voteTitleLayout = new HorizontalLayout();
			this.commentLayout = new VerticalLayout();
			this.commentMessagesLayout = new VerticalLayout();
			this.sendCommentButtonLayout = new HorizontalLayout();

			this.voteIconLayout = new HorizontalLayout();

			//this.adrInformationLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_ADR_INFORMATION);
			this.adrInformationLabel = new Label(adr.getTitle());

			this.separatorAuthorStatus = new Label("\u2012");

			this.titleStringLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_TITEL_STRING);
			this.authorStringLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_AUTHOR_STRING);
			this.contextStringLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_CONTEXT_STRING);
			this.decisionStringLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_DECISION_STRING);
			this.consequencesStringLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_CONSEQUENCES_STRING);
			this.supersedesStringLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_SUPERSEDES_STRING);
			this.commentLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_COMMENTS_STRING);

			this.voteLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_VOTE_STRING);
			this.voteNotStartedYet = new Label(StringConstantsFrontend.ADRVOTEVIEW_VOTING_HAS_NOT_STARTED_YET);
			this.votingSeparatorLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_VOTING_SEPARATOR_STRING);
			this.votingProLabel = new Label();
			this.votingContraLabel = new Label();
			this.votingNotYetVotedLabel = new Label(StringConstantsFrontend.ADRVOTEVIEW_LABEL_NOT_YET_VOTED);
			this.votingNotAllowedToVote = new Label(StringConstantsFrontend.ADRVOTEVIEW_ADR_NOT_ALLOWED_TO_VOTE);

			// buttons
			this.sendButton = new Button(StringConstantsFrontend.ADRVOTEVIEW_BUTTON_SEND_COMMENT);
			this.accessGroupsButton = new Button(StringConstantsFrontend.ADRVOTEVIEW_BUTTON_EDIT_ACCESS_GROUPS_BUTTON);
			this.editButton = new Button(StringConstantsFrontend.ADRVOTEVIEW_LABEL_BUTTON_EDIT_STRING);
			this.startVoteButton = new Button(
					adr.getStatus().getType().equals(ADRStatusType.CREATED)
							? StringConstantsFrontend.ADRVOTEVIEW_LABEL_BUTTON_START_INTERNAL_VOTING
							: StringConstantsFrontend.ADRVOTEVIEW_LABEL_BUTTON_START_VOTING);

			this.proposeDirectlyButton = new Button(StringConstantsFrontend.ADRVOTEVIEW_LABEL_BUTTON_PROPOSE_DIRECTLY);

			this.endVoteButton = new Button(
					adr.getStatus().getType().equals(ADRStatusType.INTERNALLY_PROPOSED)
							? StringConstantsFrontend.ADRVOTEVIEW_LABEL_BUTTON_END_INTERNAL_VOTING
							: StringConstantsFrontend.ADRVOTEVIEW_LABEL_BUTTON_END_VOTING);

			this.inviteVoterButton = new Button(StringConstantsFrontend.ADRVOTEVIEW_LABEL_BUTTON_INVITE_VOTER);

			// text areas
			this.sendMessageArea = new TextArea();
			this.sendMessageArea.setPlaceholder(StringConstantsFrontend.ADRVOTEVIEW_TEXTAREA_COMMENT_PLACEHOLDER);

			// icons
			this.thumbsUpIcon = new Icon(VaadinIcon.THUMBS_UP_O);
			this.thumbsDownIcon = new Icon(VaadinIcon.THUMBS_DOWN_O);

			// divs
			this.titleDiv = new Div();
			this.authorDiv = new Div();
			this.contextDiv = new Div();
			this.decisionDiv = new Div();
			this.consequencesDiv = new Div();
			this.supersedesDiv = new Div();

			// comments
			this.collabMessageList = new CollaborationMessageList(this.userInfo, "adr|" + this.adrId, this.collaborationMessagePersister);
			this.collabMessageInput = new CollaborationMessageInput(this.collabMessageList);
		}

		// methods
		addDescribingLabels();
		//this.updateDescribingLabels();
		setupStatusAndAuthorLabel(adr);

		// setup properties
		{
			this.leftLayout.setSpacing(false);

			this.leftLayout.addClassName("adr-vote-sub-main-layout");
			this.rightLayout.addClassName("adr-vote-sub-main-layout");

			this.adrInformationAuthorStatusLayout.addClassName("adr-information-layout-author-status");

			this.titleLayout.addClassName("adr-vote-information-layout");
			this.authorLayout.addClassName("adr-vote-information-layout");
			this.contextLayout.addClassName("adr-vote-information-layout");
			this.decisionLayout.addClassName("adr-vote-information-layout");
			this.consequencesLayout.addClassName("adr-vote-information-layout");
			this.supersedesLayout.addClassName("adr-vote-information-layout-superseded");

			this.separatorAuthorStatus.addClassName("adr-vote-separator-author-status");

			this.titleStringLabel.addClassName("adr-vote-small-heading");
			this.authorStringLabel.addClassName("adr-vote-small-heading");
			this.contextStringLabel.addClassName("adr-vote-small-heading");
			this.decisionStringLabel.addClassName("adr-vote-small-heading");
			this.consequencesStringLabel.addClassName("adr-vote-small-heading");
			this.supersedesStringLabel.addClassName("adr-vote-small-heading");

			this.titleDiv.addClassName("adr-information-div");
			this.authorDiv.addClassName("adr-information-div");
			this.contextDiv.addClassName("adr-information-div");
			this.decisionDiv.addClassName("adr-information-div");
			this.consequencesDiv.addClassName("adr-information-div");
			this.supersedesDiv.addClassName("adr-information-div-superseded");
			this.adrInformationLabel.addClassName("adr-vote-heading");
			this.voteLabel.addClassName("adr-vote-heading");
			this.voteLabel.getStyle().set("margin-bottom", "15px");

			this.commentLabel.addClassName("adr-vote-heading");

			this.thumbsUpIcon.setSize("35px");
			this.thumbsDownIcon.setSize("35px");
			this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR);
			this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR);
			if (!adr.getStatus().isVotable()) {
				this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR_DISABLED);
				this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR_DISABLED);
			}

			this.votingNotYetVotedLabel.getStyle().set("font-weight", "bold");
			this.votingNotYetVotedLabel.getStyle().set("padding", "10px");

			this.votingProLabel.addClassName("adr-vote-pro-label");
			this.votingContraLabel.addClassName("adr-vote-contra-label");
			this.votingSeparatorLabel.addClassName("adr-vote-separator-label");

			this.sendButton.addClassName("confirm-button");
			this.sendMessageArea.addClassName("adr-vote-message-text-area");

			this.commentMessagesLayout.addClassName("adr-vote-message-layout");

			this.sendCommentButtonLayout.addClassName("adr-vote-send-message-layout");

			this.editButton.addClassName("confirm-button");
			this.accessGroupsButton.addClassName("confirm-button");
			this.startVoteButton.addClassName("confirm-button");
			this.proposeDirectlyButton.addClassName("confirm-button");
			this.endVoteButton.addClassName("confirm-button");

			this.inviteVoterButton.addClassName("confirm-button");
		}

		// listener
		//addSendButtonListener();
		setMessageInputSubmitter();
		addSendButtonShortcut();
		addEditButtonListener();
		addEditAccessGroupsButtonListener();
		addInviteVoterButtonListener();
		addVoteIconsListener();
		addStartVoteButtonListener();
		addEndVoteButtonListener();

	}

	private void setupStatusAndAuthorLabel(ADR adr) {
		this.statusLabel = new Label(adr.getStatus().getType().getName());
		this.statusLabel.setClassName("adr-vote-status-label-" + adr.getStatus().getType().getName().toLowerCase().replaceAll(" ", "-"));
		this.authorLabel.addClassName("adr-vote-information-author-label");
	}

	private void addDescribingLabels() {
		adrDao.findById(adrId).ifPresent(adr -> {
			this.titleDiv.add(this.titleLabel = new Label(adr.getTitle()));
			this.authorDiv.add(this.authorLabel = new Label(adr.getAuthor().isPresent() ? adr.getAuthor().get().getUserName() : StringConstantsFrontend.GENERAL_UNKNOWN_AUTHOR));
			this.contextDiv.add(this.contextLabel = new RichTextEditor(adr.getContext()));
			this.decisionDiv.add(this.decisionLabel = new RichTextEditor(adr.getDecision()));
			this.consequencesDiv.add(this.consequencesLabel = new RichTextEditor(adr.getConsequences()));

			List<ADR> supersededADRs = new LinkedList<>();
			adr.getSupersededIds().forEach(id -> adrDao.findById(id).ifPresent(supersededADRs::add));
			supersededADRs.forEach(supersededAdr -> {
				Span tagSpan = new Span(supersededAdr.getTitle());
				tagSpan.addClassName("adr-vote-information-layout-superseded-adr-span");
				this.supersedesDiv.add(tagSpan);

			});
		});
	}

	private void updateDescribingLabels() {
		adrDao.findById(adrId).ifPresent(adr -> getUI().ifPresent(ui -> ui.access(() -> {
			this.titleDiv.remove(this.titleLabel);
			this.adrInformationAuthorStatusLayout.remove(this.statusLabel);
			this.adrInformationAuthorStatusLayout.remove(this.authorLabel);
			this.contextDiv.remove(this.contextLabel);
			this.decisionDiv.remove(this.decisionLabel);
			this.consequencesDiv.remove(this.consequencesLabel);
			this.supersedesLayout.remove(this.supersedesDiv);

			this.titleLabel.setText(adr.getTitle());
			this.statusLabel.setText(adr.getStatus().getType().getName());
			this.authorLabel.setText(adr.getAuthor().isPresent() ? adr.getAuthor().get().getUserName() : StringConstantsFrontend.GENERAL_UNKNOWN_AUTHOR);
			this.contextLabel = new RichTextEditor(adr.getContext());
			this.decisionLabel = new RichTextEditor(adr.getDecision());
			this.consequencesLabel = new RichTextEditor(adr.getConsequences());
			this.supersedesDiv = new Div();

			List<ADR> supersededADRs = new LinkedList<>();
			adr.getSupersededIds().forEach(id -> adrDao.findById(id).ifPresent(supersededADRs::add));
			supersededADRs.forEach(supersededAdr -> {
				Span tagSpan = new Span(supersededAdr.getTitle());
				tagSpan.addClassName("adr-vote-information-layout-superseded-adr-span");
				this.supersedesDiv.add(tagSpan);
			});

			this.titleDiv.add(this.titleLabel);
			this.adrInformationAuthorStatusLayout.add(this.statusLabel);
			this.adrInformationAuthorStatusLayout.addComponentAsFirst(this.authorLabel);
			this.contextDiv.add(this.contextLabel);
			this.decisionDiv.add(this.decisionLabel);
			this.consequencesDiv.add(this.consequencesLabel);
			this.supersedesLayout.add(this.supersedesDiv);
		})));
	}

	private void setMessageInputSubmitter() {
		this.collabMessageList.setSubmitter(activationContext -> {
			Registration registration = this.sendButton.addClickListener(event -> {
				String tempValue = this.sendMessageArea.getValue().trim();
				if (!tempValue.isEmpty()) {
					activationContext.appendMessage(tempValue);
					this.sendMessageArea.clear();
				}
			});

			return () -> {
				registration.remove();
				this.sendButton.setEnabled(false);
			};
		});
	}

	private synchronized void addComponentsToLayouts(@NonNull final ADR adr) {
		// setup
		setupComponents(adr);
		// add titles to main right and left side layout

		this.adrTopInformationLayout.add(this.adrInformationLayout, this.adrInformationAuthorStatusLayout, this.adrActionButtonsLayout);

		this.adrInformationLayout.add(this.adrInformationLabel);

/*		if (adr.canWrite(this.userDao.getCurrentUser())) {
			this.adrActionButtonsLayout.add(this.editButton);
		}

		if(adr.canEditAccessGroups(this.userDao.getCurrentUser())){
			this.adrActionButtonsLayout.add(this.accessGroupsButton);
		}
*/
		this.leftLayout.add(this.adrTopInformationLayout);

		/* Left side (ADR Information) */
		// adding components to vertical adr information layouts
		this.titleLayout.add(this.titleStringLabel, this.titleDiv);
		this.authorLayout.add(this.authorStringLabel, this.authorDiv);
		this.contextLayout.add(this.contextStringLabel, this.contextDiv);
		this.decisionLayout.add(this.decisionStringLabel, this.decisionDiv);
		this.consequencesLayout.add(this.consequencesStringLabel, this.consequencesDiv);

		// add only if not empty
		if (!adr.getSupersededIds().isEmpty()) {
			this.supersedesLayout.add(this.supersedesStringLabel, this.supersedesDiv);
		}

		// add vertical adr information layouts to main left layout - leftLayout
		//this.leftLayout.add(this.titleLayout, this.authorLayout, this.contextLayout, this.decisionLayout, this.consequencesLayout, this.supersedesLayout, this.adrButtonsLayout);
		this.leftLayout.add(this.contextLayout, this.decisionLayout, this.consequencesLayout, this.supersedesLayout, this.adrButtonsLayout);

		this.adrButtonsLayout.add(this.actionButtonsLayout);

		/* Right side (voting and comments) */
		this.voteTitleLayout.add(this.voteLabel);

		this.voteLayout.add(this.voteTitleLayout);

		this.commentLayout.add(this.commentLabel);

		this.voteLayout.add(this.voteIconLayout);

		this.voteResultBar = new VoteResultBar();
		this.voteLayout.add(this.voteResultBar);

		this.setVariableVotingSectionElements();

		this.commentMessagesLayout.add(this.collabMessageList);
		this.commentLayout.add(this.commentMessagesLayout);

		this.sendCommentButtonLayout.add(this.sendMessageArea, this.sendButton);

		this.commentLayout.add(this.sendCommentButtonLayout);

		this.rightLayout.add(this.voteLayout);
		this.rightLayout.add(this.commentLayout);

		// adding main side layouts to overall main layout
		this.add(this.leftLayout);
		this.add(this.rightLayout);
	}

	private void addSendButtonShortcut() {
		this.sendButton.addClickShortcut(Key.ENTER);
	}

	private void addEditButtonListener() {
		this.editButton.addClickListener(event -> {
			String route = RouteConfiguration.forSessionScope().getUrl(ADRRichCreateView.class, adrId);
			this.getUI().ifPresent((ui) -> ui.getPage().setLocation(route));
		});
	}

	private void addEditAccessGroupsButtonListener() {
		this.accessGroupsButton.addClickListener(event -> {
			AccessGroupDialog dialog = new AccessGroupDialog(this.accessGroupService, this.adrDao, this.adrId);
			//dialog.addDetachListener(event2 -> Broadcaster.broadcastMessage(Event.ADR_ACCESS_GROUPS_CHANGED, adrId, this));
			dialog.open();
		});
	}

	private void addInviteVoterButtonListener() {
		this.inviteVoterButton.addClickListener(event -> adrDao.findById(adrId).ifPresent(adr -> adr.getStatus().adrReviewAsOpt().ifElse(review -> {
					InviteVoterDialog dialog = new InviteVoterDialog(this.userDao, this.adrDao, adr);
					dialog.addDetachListener(event2 -> {
						Broadcaster.broadcastMessage(Event.ADR_REVIEW_STS_CHANGED, adrId, this);
						setVariableVotingSectionElements();
					});
					dialog.open();
				},
				() -> new ErrorNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_USERS_CURRENTLY_NOT_INVITABLE).open())));
	}

	private void addStartVoteButtonListener() {
		this.startVoteButton.addClickListener(event -> {
			synchronized (adrDao) {
				adrDao.findById(adrId).ifPresent((adr) -> {
					if (!adr.canStartVoting(userDao.getCurrentUser())) return;
					adr.propose();
					this.adrDao.save(adr);
					adr.getStatus().adrReviewAsOpt().ifPresent(review ->
							userDao
									.getCurrentUser()
									.pushNotification(new VotingPendingNotification(review, LocalDateTime.now())));
				});
				//this.getUI().ifPresent((ui) -> ui.getPage().reload());
			}
			Broadcaster.broadcastMessage(Event.ADR_REVIEW_STS_CHANGED, adrId, this);
			this.setVariableVotingSectionElements();
		});
		this.proposeDirectlyButton.addClickListener(event -> {
			synchronized (adrDao) {
				adrDao.findById(adrId).ifPresent((adr) -> {
					if (!adr.canPropose(userDao.getCurrentUser())) return;
					adr.propose();
					if (adr.canPropose(userDao.getCurrentUser())) adr.propose();
					this.adrDao.save(adr);
					adr.getStatus().adrReviewAsOpt().ifPresent(review ->
							userDao
									.getCurrentUser()
									.pushNotification(new VotingPendingNotification(review, LocalDateTime.now())));
				});
				this.getUI().ifPresent((ui) -> ui.getPage().reload());
			}
			Broadcaster.broadcastMessage(Event.ADR_REVIEW_STS_CHANGED, adrId, this);
			//this.setVariableVotingSectionElements();
		});
	}

	private void addEndVoteButtonListener() {
		this.endVoteButton.addClickListener(event -> {
			synchronized (adrDao) {
				adrDao.findById(adrId).ifPresent((adr) -> {
					ADR.VoteResult res = adr.endVoting(adrDao);
					if (res.equals(ADR.VoteResult.INTERNALLY_APPROVED)) {
						adr.getStatus().adrReviewAsOpt().ifPresent(review ->
								userDao
										.getCurrentUser()
										.pushNotification(new VotingPendingNotification(review, LocalDateTime.now())));
					}

					this.adrDao.save(adr);
					Broadcaster.broadcastMessage(Event.ADR_REVIEW_STS_CHANGED, adrId, this);
					this.setVariableVotingSectionElements();
				});
			}
		});
	}

	static private final class VoteSts {
		static final int ADR_NOT_PROPOSED = -2, USER_NOT_PERMITTED_TO_VOTE = -1, VOTE_WITHDRAWN = 0, VOTE_GIVEN = 1;
	}

	private void addVoteIconsListener() {
		this.thumbsUpIcon.addClickListener(event -> {
			AtomicInteger voteSuccess = new AtomicInteger(VoteSts.ADR_NOT_PROPOSED);
			synchronized (adrDao) {
				adrDao.findById(adrId).ifPresent((adr) -> adr.getStatus().adrReviewAsOpt().ifPresent(adrReview -> {
					if (adrReview.getUserVote(this.userDao.getCurrentUser()).isEmpty() || !adrReview.getUserVote(this.userDao.getCurrentUser()).get().equals(VoteType.FOR)) {
						try {
							if (adr.getStatus().isVotable()) {
								adrReview.putVote(this.userDao.getCurrentUser(), VoteType.FOR);
								this.adrDao.save(adr);
								voteSuccess.set(VoteSts.VOTE_GIVEN);
							}
						} catch (UserIsNotInvitedException e) {
							voteSuccess.set(VoteSts.USER_NOT_PERMITTED_TO_VOTE);
						}
					} else if (adr.getStatus().isVotable()) {
						try {
							adrReview.removeVote(this.userDao.getCurrentUser());
							this.adrDao.save(adr);
							voteSuccess.set(VoteSts.VOTE_WITHDRAWN);
						} catch (UserIsNotInvitedException e) {
							voteSuccess.set(VoteSts.USER_NOT_PERMITTED_TO_VOTE);
						}
					}
				}));
			}
			switch (voteSuccess.get()) {
				case VoteSts.VOTE_GIVEN:
					Broadcaster.broadcastMessage(Event.ADR_REVIEW_VOTE_CNT_CHANGED, adrId, this);
					new SuccessNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_SUCCESSFULLY_VOTED).open();
					setVariableVotingSectionElements();
					break;
				case VoteSts.VOTE_WITHDRAWN:
					Broadcaster.broadcastMessage(Event.ADR_REVIEW_VOTE_CNT_CHANGED, adrId, this);
					new SuccessNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_VOTE_REFUSED).open();
					setVariableVotingSectionElements();
					break;
				case VoteSts.USER_NOT_PERMITTED_TO_VOTE:
					new ErrorNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_NOT_ALLOWED_TO_VOTE).open();
				default:
					new ErrorNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_NO_LONGER_PROPOSED).open();
			}
		});

		this.thumbsDownIcon.addClickListener(event -> {

			AtomicInteger voteSuccess = new AtomicInteger(VoteSts.ADR_NOT_PROPOSED); //-2 = ADR not proposed; -1 = user not permitted to vote; 0 = vote successfully withdrawn; 1 = vote successfully given
			synchronized (adrDao) {
				adrDao.findById(adrId).ifPresent((adr) ->
						adr.getStatus().adrReviewAsOpt().ifPresent(adrReview -> {
							if (adrReview.getUserVote(this.userDao.getCurrentUser()).isEmpty() ||
									!adrReview.getUserVote(this.userDao.getCurrentUser()).get().equals(VoteType.AGAINST)) {
								try {
									if (adr.getStatus().isVotable()) {
										adrReview.putVote(this.userDao.getCurrentUser(), VoteType.AGAINST);
										this.adrDao.save(adr);
										voteSuccess.set(VoteSts.VOTE_GIVEN);
									}
								} catch (UserIsNotInvitedException e) {
									voteSuccess.set(-1);
								}
							}
							// voted
							else if (adr.getStatus().isVotable()) {
								try {
									adrReview.removeVote(this.userDao.getCurrentUser());
									this.adrDao.save(adr);
									voteSuccess.set(VoteSts.VOTE_WITHDRAWN);
								} catch (UserIsNotInvitedException e) {
									voteSuccess.set(VoteSts.USER_NOT_PERMITTED_TO_VOTE);
								}
							}
						})
				);
			}
			switch (voteSuccess.get()) {
				case VoteSts.VOTE_GIVEN:
					Broadcaster.broadcastMessage(Event.ADR_REVIEW_VOTE_CNT_CHANGED, adrId, this);
					new SuccessNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_SUCCESSFULLY_VOTED).open();
					setVariableVotingSectionElements();
					break;
				case VoteSts.VOTE_WITHDRAWN:
					Broadcaster.broadcastMessage(Event.ADR_REVIEW_VOTE_CNT_CHANGED, adrId, this);
					new SuccessNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_VOTE_REFUSED).open();
					setVariableVotingSectionElements();
					break;
				case VoteSts.USER_NOT_PERMITTED_TO_VOTE:
					new ErrorNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_NOT_ALLOWED_TO_VOTE).open();
				default:
					new ErrorNotification(StringConstantsFrontend.ADRVOTEVIEW_ADR_NO_LONGER_PROPOSED).open();
			}
		});
	}

	private void updateVotingResults() {
		VoteResultBar newBar = new VoteResultBar();
		this.voteLayout.replace(this.voteResultBar, newBar);
		this.voteResultBar = newBar;
	}

	private void updateVotingResults(ADRReview adrReview) {
		VoteResultBar newBar = new VoteResultBar(adrReview.getVoteResult().get(VoteType.FOR), adrReview.getVoteResult().get(VoteType.AGAINST), adrReview.getInvitedVoters().size());
		this.voteLayout.replace(this.voteResultBar, newBar);
		this.voteResultBar = newBar;
	}

	private void setVoteSection(int votedFor, boolean activeVoting) {
		this.voteIconLayout.removeAll();

		if (votedFor == 1) {
			if (activeVoting) {
				this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_UP_COLOR_VALUE);
				this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR);
			} else {
				this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_UP_COLOR_VALUE_DISABLED);
				this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR_DISABLED);
			}
		} else if (votedFor == -1) {
			if (activeVoting) {
				this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DOWN_COLOR_VALUE);
				this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR);
			} else {
				this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DOWN_COLOR_VALUE_DISABLED);
				this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR_DISABLED);
			}
		} else {
			if (activeVoting) {
				this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR);
				this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR);
			} else {
				this.thumbsUpIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR_DISABLED);
				this.thumbsDownIcon.setColor(StringConstantsFrontend.ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR_DISABLED);
			}
		}

		this.voteIconLayout.add(this.thumbsUpIcon);
		this.voteIconLayout.add(this.votingSeparatorLabel);
		this.voteIconLayout.add(this.thumbsDownIcon);
	}

	private void setVariableVotingSectionElements() {
		User user = userDao.getCurrentUser();
		if (user == null) return;

		this.adrDao.findById(adrId).ifPresent(adr -> {
			this.adrInformationAuthorStatusLayout.removeAll();
			this.setupStatusAndAuthorLabel(adr);
			this.adrInformationAuthorStatusLayout.add(this.authorLabel, this.separatorAuthorStatus, this.statusLabel);

			if (adr.canInviteVoters(this.userDao.getCurrentUser())) {
				this.voteTitleLayout.addComponentAsFirst(this.inviteVoterButton);
				this.voteTitleLayout.remove(this.voteLabel);
				this.voteTitleLayout.addComponentAsFirst(this.voteLabel);
			} else {
				this.voteTitleLayout.remove(this.inviteVoterButton);
			}

			if (adr.canEndVoting(this.userDao.getCurrentUser())) {
				this.voteTitleLayout.add(this.endVoteButton);
			} else {
				this.voteTitleLayout.remove(this.endVoteButton);
			}

			if (adr.canStartVoting(this.userDao.getCurrentUser())) {
				this.voteTitleLayout.add(this.startVoteButton);
			} else {
				this.voteTitleLayout.remove(this.startVoteButton);
				if (adr.getStatus().isVotingStartable()) {
					this.voteIconLayout.add(this.voteNotStartedYet);
				} else {
					this.voteIconLayout.remove(this.voteNotStartedYet);
				}
			}

			if (adr.canWrite(user)) {
				this.adrActionButtonsLayout.add(this.editButton);
			} else {
				this.adrActionButtonsLayout.remove(this.editButton);
			}

			if (adr.canEditAccessGroups(user)) {
				this.adrActionButtonsLayout.add(this.accessGroupsButton);
			} else {
				this.adrActionButtonsLayout.remove(this.accessGroupsButton);
			}

			if (adr.canPropose(this.userDao.getCurrentUser())) {
				this.voteTitleLayout.add(this.proposeDirectlyButton);
			} else {
				this.voteTitleLayout.remove(this.proposeDirectlyButton);
			}

			if (adr.getStatus().getType() != ADRStatusType.CREATED) {
				adr.getStatus().adrReviewAsOpt().ifPresent(review -> {
					if (review.userHasVoted(user).isPresent()) {
						if (review.userHasVoted(user).get() && review.getUserVote(user).isPresent()) {
							setVoteSection(review.getUserVote(user).get() == VoteType.FOR ? 1 : -1, adr.getStatus().isVotable());
						} else {
							setVoteSection(0, adr.getStatus().isVotable());
						}
					} else {
						this.voteIconLayout.removeAll();
					}
					updateVotingResults(review);
				});
			} else {
				updateVotingResults();
				this.voteIconLayout.removeAll();
			}
		});
	}

	/* Overrides */
	@Override
	public void setParameter(BeforeEvent beforeEvent, String adrId) {
		this.adrId = adrId;
		Optional<ADR> foundADR = this.adrDao.findById(adrId);
		if (foundADR.isEmpty()) {

			/* Error Message ADR not found */
			this.invalidIdNotification = new ErrorNotification();
			this.invalidIdNotification.setDuration(0);
			this.invalidIdNotification.open();

			Label msg = new Label(StringConstantsFrontend.ADRCREATE_ERROR_ID_NOT_FOUND);

			Button confirm = new Button(StringConstantsFrontend.GENERAL_BUTTON_CONFIRM);
			confirm.getStyle().set("margin-left", "auto");
			confirm.addClickShortcut(Key.ENTER);

			VerticalLayout layout = new VerticalLayout(msg, confirm);

			this.invalidIdNotification.add(layout);

			// confirm button click-listener
			confirm.addClickListener(event -> this.invalidIdNotification.close());

			// redirect user to kanban view
			this.invalidIdNotification.addOpenedChangeListener(event ->
					this.getUI().ifPresent(page ->
							page.getPage().setLocation(StringConstantsFrontend.OVERVIEW_PATH)));
		} else {
			/* constructor */

			addComponentsToLayouts(foundADR.get());
		}

	}

	@Override
	public void receiveBroadcast(Event event, String message) {
		switch (event) {
			case ADR_REVIEW_VOTE_CNT_CHANGED -> {
				if (adrId != null && adrId.equals(message))
					getUI().ifPresent(ui -> ui.access(() -> {
						synchronized (adrDao) {
							adrDao.findById(adrId)
									.flatMap(adr -> adr.getStatus().adrReviewAsOpt().stdOpt())
									.ifPresent(this::updateVotingResults);
						}
					}));
			}
			case ADR_CHANGED -> updateDescribingLabels();
			case ADR_REVIEW_STS_CHANGED -> {
				if (adrId != null && adrId.equals(message)) {
					getUI().ifPresent(ui -> ui.access(this::setVariableVotingSectionElements));
				}
			}
			case ADR_ACCESS_GROUPS_CHANGED -> {
				if (adrId != null && adrId.equals(message)) {
					getUI().ifPresent(ui -> ui.access(() -> {
						synchronized (adrDao) {
							adrDao.findById(adrId).ifPresent(adr -> {
								this.actionButtonsLayout.remove(editButton);
								if(adr.canWrite(userDao.getCurrentUser())){
									this.actionButtonsLayout.addComponentAsFirst(editButton);
								}
							});
						}
					}));
				}
			}
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		adrDao.findById(adrId).ifPresentOrElse(adr -> {
			if (adr.canRead(userDao.getCurrentUser())) beforeEnterEvent.rerouteTo(StringConstantsFrontend.LANDING_PAGE_PATH);
		}, () -> beforeEnterEvent.rerouteTo(StringConstantsFrontend.LANDING_PAGE_PATH));
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
		Broadcaster.unregisterListener(this);
	}
}
