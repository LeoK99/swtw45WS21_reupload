package com.buschmais.frontend.views.user;

import com.buschmais.backend.image.ImageDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.frontend.broadcasting.BroadcastListener;
import com.buschmais.frontend.broadcasting.Broadcaster;
import com.buschmais.frontend.components.ErrorNotification;
import com.buschmais.frontend.components.SuccessNotification;
import com.buschmais.frontend.vars.NumberConstantsFrontend;
import com.buschmais.frontend.vars.StringConstantsFrontend;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Route(value = StringConstantsFrontend.USER_PATH, layout = MainLayout.class)
@CssImport(value = "./themes/adr-workbench/vaadin-components/buttons.css")
@CssImport(value = "./themes/adr-workbench/user/user.css")
@CssImport(value = "./themes/adr-workbench/vaadin-components/vaadin-text-field-styles.css", themeFor = "vaadin-text-field vaadin-password-field")
@PageTitle("User")
public class UserView extends VerticalLayout implements BroadcastListener {

	// TODO: Check about all passwords attributes (Password Hash - clear password), beautifying and error handling

	/* Property enum */
	private enum Property {
		USER_NAME("Benutzername"),
		PASSWORD("Passwort"),
		PROFILE_PICTURE("Profilbild");

		private final String name;

		private final static List<Property> changedProperties = new ArrayList<>();

		private boolean hasChanged = false;
		private boolean isReadyToSave = false;

		Property(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public boolean hasChanged() {
			return this.hasChanged;
		}

		public boolean isReadyToSave() {
			return this.isReadyToSave;
		}

		public void setHasChanged(boolean hasChanged) {
			this.hasChanged = hasChanged;
			if (hasChanged) {
				changedProperties.add(this);
			} else {
				changedProperties.remove(this);
			}
		}

		public void setReadyToSave(boolean readyToSave) {
			this.isReadyToSave = readyToSave;
		}
	}

	// buttons
	private Button editButton;
	private Button saveButton;
	private Button cancelButton;

	// text-fields
	private TextField userNameTextField;
	private PasswordField newPasswordField;
	private PasswordField currentPasswortField;

	private Label changePasswordLabel;

	// layouts
	private VerticalLayout userNameLayout;
	private VerticalLayout changePasswordLayout;
	private VerticalLayout profilePictureLayout;
	private VerticalLayout buttonLayout;

	// confirm dialog
	private com.buschmais.frontend.components.ConfirmDialog cancelButtonConfirmDialog;

	// other class instances
	private final UserDao userDao;
	private final ImageDao imageDao;
	private final ImageCtx imageCtx;

	public UserView(@Autowired UserDao userDao,
					@Autowired ImageDao imageDao) {
		this.userDao = userDao;
		this.imageDao = imageDao;

		synchronized (this.userDao){
			Optional<User> found_user = userDao.findByUserName(userDao.getCurrentUser().getUserName());
			found_user.ifPresent(userDao::setCurrentUser);

			imageCtx = new ImageCtx(this.userDao, this.imageDao);
			Property.changedProperties.clear();

			setupComponents();
			createDefaultLayout();

			// Attach listeners for broadcasting and detachment
			Broadcaster.registerListener(this);
			addDetachListener((DetachEvent e) -> Broadcaster.unregisterListener(this));
		}
	}

	/**
	 * This method creates the instances of the components and adds classnames to them.
	 * Additionally it changes properties of a component.
	 */
	private void setupComponents() {

		/* Create Instances */
		//labels
		Label userNameLabel;
		Label profilePictureLabel;
		{
			// username-field, password field
			this.userNameTextField = new TextField();
			this.newPasswordField = new PasswordField("Neues Passwort");
			this.currentPasswortField = new PasswordField("Aktuelles Passwort");

			// all buttons
			this.editButton = new Button(StringConstantsFrontend.USERVIEW_BUTTON_EDIT_PROFILE);
			this.saveButton = new Button(StringConstantsFrontend.GENERAL_DIALOG_BUTTON_SAVE);
			this.cancelButton = new Button(StringConstantsFrontend.GENERAL_DIALOG_BUTTON_CANCEL);

			// labels
			userNameLabel = new Label(StringConstantsFrontend.GENERAL_USERNAME_TEXT);
			this.changePasswordLabel = new Label(StringConstantsFrontend.USERVIEW_CHANGE_PASSWORD);
			profilePictureLabel = new Label(StringConstantsFrontend.USERVIEW_PROFILE_PICTURE);

			// layouts
			this.userNameLayout = new VerticalLayout(userNameLabel, this.userNameTextField);
			this.changePasswordLayout = new VerticalLayout();

			this.profilePictureLayout = new VerticalLayout(profilePictureLabel, imageCtx.img);
			this.buttonLayout = new VerticalLayout(this.editButton);
		}

		/* Set Properties for components */
		{
			// username-field, password-field
			this.userNameTextField.addClassName("user-name-text-field");
			this.userNameTextField.setValue(userDao.getCurrentUser().getUserName());

			userNameLabel.addClassName("sub-heading");

			this.changePasswordLabel.addClassName("sub-heading");

			// buttons
			this.editButton.addClassName("confirm-button");
			this.saveButton.addClassName("confirm-button");
			this.cancelButton.addClassName("cancel-button");

			// profile-picture, upload
			profilePictureLabel.addClassName("sub-heading");
		}

		/* Add Listener */
		addClickListener();
		addValueChangeListener();

		imageCtx.loadImage();
	}

	/**
	 * This method adds a ClickListener to the buttons.
	 */
	private void addClickListener() {

		// edit-button
		this.editButton.addClickListener(event -> changeToEditMode());

		// cancel-button
		this.cancelButton.addClickListener(event -> {


			StringBuilder unsavedChanges = new StringBuilder(StringConstantsFrontend.USERVIEW_UNSAVED_CHANGES + " (");

			for (Property property : Property.changedProperties) {
				unsavedChanges.append(property.getName()).append(", ");
			}

			unsavedChanges = new StringBuilder(unsavedChanges.substring(0, unsavedChanges.length() - 2)); //remove last, and space letter
			unsavedChanges.append("). " + StringConstantsFrontend.USERVIEW_UNSAVED_CHANGES_WILL_GET_LOST + " " + StringConstantsFrontend.USERVIEW_UNSAVED_CHANGES_REALLY_ABORT);

			if (!Property.changedProperties.isEmpty()) {

				// confirm message dialog
				this.cancelButtonConfirmDialog = new com.buschmais.frontend.components.ConfirmDialog(
						StringConstantsFrontend.USERVIEW_UNSAVED_CHANGES_REALLY_ABORT,
						unsavedChanges.toString(),
						StringConstantsFrontend.USERVIEW_CONFIRM_DIALOG_BUTTON_LABEL_DISCARD_CHANGES,
						confirmEvent -> {this.cancelButtonConfirmDialog.close(); changeToReadOnlyMode();},
						StringConstantsFrontend.USERVIEW_CONFIRM_DIALOG_BUTTON_LABEL_CONTINUE_EDITING,
						cancelEvent -> this.cancelButtonConfirmDialog.close());

				// open dialog
				this.cancelButtonConfirmDialog.open();

			} else {
				changeToReadOnlyMode();
			}
		});

		// save-button
		this.saveButton.addClickListener(event -> {
			synchronized (userDao){
				String changedUsername = this.userNameTextField.getValue().trim();
				String newPassword = this.newPasswordField.getValue().trim();

				boolean dataSaveSuccess = true;

				/* Check if changed */
				// username
				if (Property.USER_NAME.hasChanged()) {
					Property.USER_NAME.setReadyToSave(checkUsername(changedUsername));

				}

				// password
				if (Property.PASSWORD.hasChanged()) {
					Property.PASSWORD.setReadyToSave(checkPassword(
							newPassword, this.currentPasswortField.getValue().trim(), this.userDao.getCurrentUser()));
				}

				// profile-picture
				Property.PROFILE_PICTURE.setReadyToSave(Property.PROFILE_PICTURE.hasChanged());


				/* Save Data */
				if (Property.USER_NAME.hasChanged() && Property.USER_NAME.isReadyToSave() && Property.PASSWORD.hasChanged() && Property.PASSWORD.isReadyToSave()) {

					User user = this.userDao.getCurrentUser();
					if (user != null) {
						user.setUserName(changedUsername);
						user.changePassword(newPassword);

						userDao.setCurrentUser(this.userDao.save(user));

						new SuccessNotification(StringConstantsFrontend.USERVIEW_SUCCESS_USERNAME_AND_PASSWORD_CHANGED).open();

					} else {
						// else
						new ErrorNotification(StringConstantsFrontend.USERVIEW_ERROR_SOMETHING_WENT_WRONG).open();
						dataSaveSuccess = false;
					}


				} else if (Property.USER_NAME.hasChanged() && Property.USER_NAME.isReadyToSave) {

					User user = this.userDao.getCurrentUser();
					if (user != null) {
						user.setUserName(changedUsername);
						userDao.setCurrentUser(this.userDao.save(user));

						new SuccessNotification(StringConstantsFrontend.USERVIEW_SUCCESS_USERNAME_CHANGED).open();
					} else {
						new ErrorNotification(StringConstantsFrontend.USERVIEW_ERROR_SOMETHING_WENT_WRONG).open();
						dataSaveSuccess = false;
					}

				} else if (Property.PASSWORD.hasChanged() && Property.PASSWORD.isReadyToSave()) {

					User user = this.userDao.getCurrentUser();
					if (user != null) {

						this.userDao.getCurrentUser().changePassword(newPassword);
						this.userDao.save(user);

						new SuccessNotification(StringConstantsFrontend.USERVIEW_SUCCESS_PASSWORD_CHANGED).open();
					} else {
						new ErrorNotification(StringConstantsFrontend.USERVIEW_ERROR_SOMETHING_WENT_WRONG).open();
						dataSaveSuccess = false;
					}
				}

				// profile picture has been changed
				if (Property.PROFILE_PICTURE.hasChanged() && Property.PROFILE_PICTURE.isReadyToSave()) {

					User user = this.userDao.getCurrentUser();
					if (user != null) {

						/* Save Image Data to DataBase */
						com.buschmais.backend.image.Image img = imageDao.save(
								new com.buschmais.backend.image.Image(imageCtx.getSrc()),
								imageCtx.getInputStream()
						);


						com.buschmais.backend.image.Image old_img = user.getProfilePicture();
						if (old_img != null)
							imageDao.delete(old_img);

						user.setProfilePicture(img);
						userDao.setCurrentUser(userDao.save(user));
						Broadcaster.broadcastMessage(Event.USER_CHANGED, user.getId(), this);
						new SuccessNotification(StringConstantsFrontend.USERVIEW_SUCCESS_IMAGE_SAVED).open();
					}
				}

				/* Recreate Layout */
				if (dataSaveSuccess || Property.changedProperties.isEmpty()) {
					changeToReadOnlyMode();
				}
			}
		});

	}

	private void addValueChangeListener() {
		this.userNameTextField.addValueChangeListener(event ->
				Property.USER_NAME.setHasChanged(!event.getValue().trim().equals(userDao.getCurrentUser().getUserName())));

		this.newPasswordField.addValueChangeListener(event ->
				Property.PASSWORD.setHasChanged(!event.getValue().trim().isEmpty()));
	}

	private void createDefaultLayout() {
		disableComponents();
		add(this.userNameLayout);
		add(this.profilePictureLayout);
		add(this.buttonLayout);
	}

	private void changeToReadOnlyMode() {

		disableComponents();
		this.userNameTextField.setValue(userDao.getCurrentUser().getUserName());
		this.profilePictureLayout.remove(imageCtx.upload);
		this.buttonLayout.removeAll();
		this.buttonLayout.add(this.editButton);

		remove(this.changePasswordLayout);

		Property.USER_NAME.setHasChanged(false);
		Property.PASSWORD.setHasChanged(false);
		Property.PROFILE_PICTURE.setHasChanged(false);

		imageCtx.loadImage();
	}

	/**
	 * This method changes the layout and components to edit-mode.
	 * In fact, it enables components and adds / removes components for editing.
	 */
	private void changeToEditMode() {
		enableComponents();
		removeAll();
		this.changePasswordLayout.add(this.changePasswordLabel);
		this.changePasswordLayout.add(this.newPasswordField);
		this.changePasswordLayout.add(this.currentPasswortField);
		this.profilePictureLayout.add(imageCtx.resetUpload());
		this.buttonLayout.remove(this.editButton);
		this.buttonLayout.add(new HorizontalLayout(this.saveButton, this.cancelButton));

		add(this.userNameLayout);
		add(this.changePasswordLayout);
		add(this.profilePictureLayout);
		add(this.buttonLayout);
	}

	/**
	 * This method disables all components, in fact it sets the read-only flag for them
	 */
	private void disableComponents() {
		this.userNameTextField.setReadOnly(true);
	}

	/**
	 * This method disables all components, in fact it sets the read-only flag to false
	 */
	private void enableComponents() {
		this.userNameTextField.setReadOnly(false);
	}

	/**
	 * This method checks, if the username fulfills all requirements
	 *
	 * @param newUsername changed username
	 * @return true if requirements fulfilled, otherwise false
	 */
	private boolean checkUsername(String newUsername) {
		if (!newUsername.matches("[A-Za-z0-9-_]+")) {
			new ErrorNotification(StringConstantsFrontend.USERVIEW_ERROR_USERNAME_CONTAINS_FORBIDDEN_CHARACTERS).open();
			return false;
		}

		if (this.userDao.findByUserName(newUsername).isPresent()) {
			new ErrorNotification(StringConstantsFrontend.USERVIEW_ERROR_USERNAME_ALREADY_EXISTS).open();
			return false;
		}

		return true;
	}

	/**
	 * This method checks, if the password fulfills all requirements
	 *
	 * @param newPassword changed password
	 * @return true if requirements fulfilled, otherwise false
	 */
	private boolean checkPassword(String newPassword, String currentPassword, User user) {
		if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) { //Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
			new ErrorNotification(StringConstantsFrontend.USERVIEW_ERROR_PASSWORD_NOT_MEETS_REQUIREMENTS).open();
			return false;
		}
		if (!user.checkPassword(currentPassword)) {
			new ErrorNotification(StringConstantsFrontend.USERVIEW_ERROR_WRONG_CURRENT_PASSWORD).open();
			return false;
		}

		return true;
	}

	static class ImageCtx {
		private Upload upload;
		private final Image img;
		private final UserDao userService;
		private final ImageDao imgService;
		public ImageCtx(@NonNull UserDao userService, @NonNull ImageDao imgService){
			this.userService = userService;
			this.imgService = imgService;
			upload = resetUpload();
			img = new Image();
			img.addClassName("profile-picture");
		}
		public InputStream getInputStream(){ return ((FileBuffer)upload.getReceiver()).getInputStream();}
		public String getSrc(){ return img.getSrc();}
		public Upload resetUpload(){
			Upload res = new Upload();
			res.setReceiver(new FileBuffer());
			res.setMaxFileSize(NumberConstantsFrontend.USERVIEW_PROFILE_PICTURE_MAX_FILE_SIZE);
			res.setAutoUpload(true);
			res.setAcceptedFileTypes("image/jpeg", "image/png");
			res.setDropLabel(new Label(StringConstantsFrontend.USERVIEW_UPLOAD_DROP_LABEL));
			res.setUploadButton(new Button(StringConstantsFrontend.USERVIEW_UPLOAD_BUTTON_LABEL));
			res.addSucceededListener((event) -> {
				this.img.setSrc(new StreamResource("db-stream", this::getInputStream));
				this.img.setAlt(((FileBuffer)upload.getReceiver()).getFileName());
				Property.PROFILE_PICTURE.setHasChanged(true);
			});
			res.getElement().addEventListener("upload-abort",
					(DomEventListener) domEvent -> {
					loadImage();
					Property.PROFILE_PICTURE.setHasChanged(false);
					});
			upload = res;
			return res;
		}

		public void loadImage(){
			upload.interruptUpload();
			User user = this.userService.getCurrentUser();

			if (user.getProfilePicture() == null){
				img.setSrc("https://dummyimage.com/600x400/000/fff");
				img.setAlt("DummyImage");
			}else{
				Optional<InputStream> s = this.imgService.getInputStream(user.getProfilePicture());
				if (s.isEmpty()){
					img.setSrc("https://dummyimage.com/600x400/000/fff");
					img.setAlt("DummyImage");
					return;
				}

				this.img.setAlt(user.getProfilePicture().getName());
				StreamResource sr = new StreamResource("db-stream", s::get);
				this.img.setSrc(sr);
			}
		}
	}


	@Override
	public synchronized void receiveBroadcast(Event event, String message) {
		if (event == Event.USER_CHANGED) {
			getUI().ifPresent(ui -> ui.access(() -> {

				synchronized (userDao) {
					// Update current user on UI if id is same
					if(userDao.getCurrentUser().getId() != null && userDao.getCurrentUser().getId().equals(message))
						ui.getPage().reload();
				}
			}));
		}
	}
}
