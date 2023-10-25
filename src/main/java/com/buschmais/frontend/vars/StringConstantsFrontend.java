package com.buschmais.frontend.vars;

/**
 * This class should provide all necessary string-constants
 * View-dependent strings should start with the view-class,
 * e.g. String in LoginView: LOGIN_ ...
 * In addition, after view-name there should be the type of the string,
 * e.g. String for an error in LoginView: LOGIN_ERROR_...
 */

public class StringConstantsFrontend {

	/*Default Constants*/
	public static final String DEFAULT_DELETED_USER_NAME = "gelöschter Nutzer";

	/*Menu Titles*/
	public static final String MENU_TITLE_LOGIN = "Login";
	public static final String MENU_TITLE_OVERVIEW = "ADR Überblick";
	public static final String MENU_TITLE_USER = "Account";
	public static final String MENU_TITLE_CREATE_ADR = "ADR Erstellen";
	public static final String MENU_TITLE_USER_CONTROL = "Berechtigungsverwaltung";
	public static final String MENU_TITLE_EXPLORER = "ADR-Explorer";

	/* Paths */
	public static final String ADR_CREATE_PATH = "adrCreate";
	public static final String ADR_DETAILS_PATH = "adrDetails";
	public static final String LANDING_PAGE_PATH = "";
	public static final String LANDING_PAGE_PATH_ALIAS = "welcome";
	public static final String LOGIN_PATH = "login";
	public static final String LOGOUT_PATH = "logout";
	public static final String OVERVIEW_PATH = "overview";
	public static final String USER_PATH = "account";
	public static final String USER_CONTROL_PATH = "access-control";
	public static final String GROUP_INFORMATION_PATH = "groupinformation";
	public static final String NOTI_PATH = "notifications";
	public static final String EXPL_PATH="explorer";

	/* General */
	public static final String GENERAL_USERNAME_TEXT = "Benutzername";
	public static final String GENERAL_PASSWORD_TEXT = "Passwort";
	public static final String GENERAL_PLACEHOLDER = "Platzhalter";
	public static final String GENERAL_UNKNOWN_AUTHOR = "Unbekannt";
	public static final String GENERAL_BUTTON_CONFIRM = "Bestätigen";

	/* Login */
	public static final String LOGIN_TEXT = "Login";
	public static final String LOGIN_ERROR_USER_NOT_FOUND = "Der Benutzername wurde nicht gefunden!";
	public static final String LOGIN_ERROR_WRONG_PASSWORD = "Es wurde keine Kombination aus Benutzername und Passwort gefunden!";

	// Error
	public static final String LOGIN_ERROR_LOGIN_UNAVAILABLE = "Einloggen zurzeit nicht möglich!";

	/* ADRCreateView */
	public static final String ADRCREATE_EDITADR = "ADR bearbeiten";
	public static final String ADRCREATE_CREATEADR = "ADR erstellen";
	public static final String ADRCREATE_CONTENT = "Inhalt";
	public static final String ADRCREATE_CONTEXT = "Kontext";
	public static final String ADRCREATE_DECISION = "Entscheidung";
	public static final String ADRCREATE_NAME = "Name";
	public static final String ADRCREATE_TITLE = "Titel";
	public static final String ADRCREATE_SUPERSEEDES_FOLLOWING_ADRS = "Abgelöste ADRs";
	public static final String ADRCREATE_CREATE = "Erstellen";
	public static final String ADRCREATE_LOAD = "Laden";
	public static final String ADRCREATE_CONSEQUENCES = "Konsequenzen";
	public static final String ADRCREATE_STATUS = "Status";
	public static final String ADRCREATE_TAGS = "Tags";
	public static final String ADRCREATE_SUCC = "ADR erfolgreich gespeichert!";
	public static final String ADRCREATE_ERROR_FIELD_EMPTY = " darf nicht leer sein!";
	public static final String ADRCREATE_ERROR_ID_NOT_FOUND = "Ein ADR mit dieser ID wurde nicht gefunden!";
	public static final String ADRCREATE_ERROR_GENERAL = "Irgendwas ist schiefgelaufen!";
	public static final String ADRCREATE_AUTHOR = "Autor";

	/* ADRDetailsView */
	public static final String ADR_TAGS = "Tags";

	/* UserControlView */
	public static final String USER_FINDER = "Benutzer suchen";
	public static final String GROUP_READABLE = "Lesen";
	public static final String GROUP_WRITABLE = "Schreiben";
	public static final String GROUP_VOTABLE = "Automatische Votingeinladungen";
	public static final String GROUP_UNREADABLE = "Nicht Lesen";
	public static final String GROUP_UNWRITABLE = "Nicht Schreiben";
	public static final String GROUP_UNVOTABLE = "Keine Votingeinladungen";
	public static final String GROUP_MORE = "...";
	public static final String GROUP_NAME = "Zugeteilte Nutzer";
	public static final String GROUP_CANMANAGEUSER = "Kann Nutzer verwalten";
	public static final String GROUP_CANMANAGEVOTES = "Kann Votings verwalten";
	public static final String GROUP_CANSEEALL = "Kann alle ADRs sehen";
	public static final String GROUP_CANMANAGEACCESSRIGHT = "Kann Nutzergruppen zuteilen";
	public static final String GROUP_CAN = "Erlauben";
	public static final String GROUP_CANNOT = "Nicht Erlauben";
	public static final String DELETE_USER_BUTTON = "Nutzer löschen";
	public static final String GROUP_DELETE = "Löschen";
	public static final String GROUP_GOT_DELETED = "Gruppe wurde gelöscht";

	/* UserView */
	public static final String USERVIEW_ERROR_USERNAME_ALREADY_EXISTS = "Der Benutzername existiert bereits!";
	public static final String USERVIEW_ERROR_USERNAME_CONTAINS_FORBIDDEN_CHARACTERS = "Username enthält verbotene Zeichen";
	public static final String USERVIEW_ERROR_WRONG_CURRENT_PASSWORD = "Falsches aktuelles Passwort!";
	public static final String USERVIEW_ERROR_PASSWORD_NOT_MEETS_REQUIREMENTS = "Passwort erfüllt nicht die entsprechenden Anforderungen";
	public static final String USERVIEW_ERROR_SOMETHING_WENT_WRONG = "Es ist ein Fehler aufgetreten!";

	public static final String USERVIEW_CHANGE_PASSWORD = "Passwort ändern";
	public static final String USERVIEW_BUTTON_EDIT_PROFILE = "Profil bearbeiten";

	public static final String USERVIEW_PROFILE_PICTURE = "Profilbild";

	public static final String USERVIEW_SUCCESS_USERNAME_CHANGED = "Der Benutzername wurde erfolgreich geändert!";
	public static final String USERVIEW_SUCCESS_PASSWORD_CHANGED = "Das Passwort wurde erfolgreich geändert!";
	public static final String USERVIEW_SUCCESS_USERNAME_AND_PASSWORD_CHANGED = "Benutzername und Passwort wurden erfolgreich geändert!";
	public static final String USERVIEW_SUCCESS_IMAGE_SAVED = "Profilbild wurde aktualisiert!";


	public static final String USERVIEW_UPLOAD_DROP_LABEL = "Datei hier ablegen";
	public static final String USERVIEW_UPLOAD_BUTTON_LABEL = "Datei hochladen";

	public static final String USERVIEW_UNSAVED_CHANGES = "Sie haben nicht gespeicherte Änderungen!";
	public static final String USERVIEW_UNSAVED_CHANGES_WILL_GET_LOST = "Die Änderungen werden dabei verloren gehen.";
	public static final String USERVIEW_UNSAVED_CHANGES_REALLY_ABORT = "Wirklich abbrechen?";
	public static final String USERVIEW_CONFIRM_DIALOG_BUTTON_LABEL_DISCARD_CHANGES = "Wirklich abbrechen";
	public static final String USERVIEW_CONFIRM_DIALOG_BUTTON_LABEL_CONTINUE_EDITING = "Weiter bearbeiten";

	/* ADRVoteView */
	public static final String ADRVOTEVIEW_LABEL_TITEL_STRING = "Titel";
	public static final String ADRVOTEVIEW_LABEL_AUTHOR_STRING = "Autor";
	public static final String ADRVOTEVIEW_LABEL_CONTEXT_STRING = "Kontext";
	public static final String ADRVOTEVIEW_LABEL_DECISION_STRING = "Entscheidung";
	public static final String ADRVOTEVIEW_LABEL_CONSEQUENCES_STRING = "Konsequenzen";
	public static final String ADRVOTEVIEW_LABEL_SUPERSEDES_STRING = "Ersetzt";

	public static final String ADRVOTEVIEW_LABEL_BUTTON_EDIT_STRING = "Bearbeiten";
	public static final String ADRVOTEVIEW_BUTTON_EDIT_ACCESS_GROUPS_BUTTON = "AccessGroups bearbeiten";
	public static final String ADRVOTEVIEW_LABEL_BUTTON_START_VOTING = "Internes Voting starten";
	public static final String ADRVOTEVIEW_LABEL_BUTTON_START_INTERNAL_VOTING = "Internes Voting starten";
	public static final String ADRVOTEVIEW_LABEL_BUTTON_PROPOSE_DIRECTLY = "Finales Voting starten";
	public static final String ADRVOTEVIEW_LABEL_BUTTON_END_VOTING = "Voting beenden";
	public static final String ADRVOTEVIEW_LABEL_BUTTON_END_INTERNAL_VOTING = "Internes Voting beenden";
	public static final String ADRVOTEVIEW_LABEL_BUTTON_INVITE_VOTER = "Nutzer bearbeiten";

	public static final String ADRVOTEVIEW_LABEL_VOTE_STRING = "Voting";
	public static final String ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR = "#000000";
	public static final String ADRVOTEVIEW_ICON_THUMBS_UP_COLOR_VALUE = "#65b32e";
	public static final String ADRVOTEVIEW_ICON_THUMBS_DOWN_COLOR_VALUE = "#990000";
	public static final String ADRVOTEVIEW_ICON_THUMBS_DEFAULT_COLOR_DISABLED = "#808080";
	public static final String ADRVOTEVIEW_ICON_THUMBS_UP_COLOR_VALUE_DISABLED = "#85934e";
	public static final String ADRVOTEVIEW_ICON_THUMBS_DOWN_COLOR_VALUE_DISABLED = "#884444";
	public static final String ADRVOTEVIEW_VOTING_HAS_NOT_STARTED_YET = "Das Voting hat noch nicht begonnen!";
	public static final String ADRVOTEVIEW_ADR_SUCCESSFULLY_VOTED = "ADR erfolgreich abgestimmt!";
	public static final String ADRVOTEVIEW_ADR_VOTE_REFUSED = "Bewertung erfolgreich zurückgezogen!";
	public static final String ADRVOTEVIEW_ADR_NOT_ALLOWED_TO_VOTE = "Du darfst zurzeit nicht abstimmen!";
	public static final String ADRVOTEVIEW_ADR_NO_LONGER_PROPOSED = "Die Abstimmung läuft nicht mehr!";
	public static final String ADRVOTEVIEW_ADR_USERS_CURRENTLY_NOT_INVITABLE = "Zurzeit können keine Nutzer eingeladen werden!";

	public static final String ADRVOTEVIEW_VOTING_SEPARATOR_STRING = "\u2012";
	public static final String ADRVOTEVIEW_LABEL_NOT_YET_VOTED = "Noch nicht abgestimmt";

	public static final String ADRVOTEVIEW_LABEL_COMMENTS_STRING = "Kommentare";
	public static final String ADRVOTEVIEW_BUTTON_SEND_COMMENT = "Senden";
	public static final String ADRVOTEVIEW_TEXTAREA_COMMENT_PLACEHOLDER = "Lass andere deine Meinung wissen...";

	/*User Menu*/
	public static final String USERMENU_LOGOUT = "Abmelden";
	public static final String USERMENU_NOTI = "Benachrichtigungen";

	/*UserControlView*/
	public static final String USERMANAGEVIEW_CREATE_USER = "Benutzer erstellen";
	public static final String USERMANAGEVIEW_DELETE_USER = "Benutzer löschen";
	public static final String USERMANAGEVIEW_SPECIFY_USER = "Bitte Nutzer auswählen!";
	public static final String USERMANAGEVIEW_DELETE_USER_CONFIRM = "Den Benutzer \"[UserName]\" wirklich löschen?";
	public static final String USERNAMANAGEVIEW_USER_DELETED_SUCCESSFULLY = "Der Nutzer \"[UserName]\" wurde erfolgreich gelöscht!";
	public static final String USERMANAGEVIEW_ERROR_USER_ALREADY_DELETED_MESSAGE = "Der Benutzer wurde gelöscht!";
	public static final String USERMANAGEVIEW_ERROR_DELETING_USER = "Es gab einen Fehler beim Löschen des Nutzers!";
	public static final String USERMANAGEVIEW_CREATENEWGROUP = "Neue Gruppe erstellen";

	/* Notifications */
	// Error
	public static final String ERROR_NOTIFICATION_ADR_NOT_AVAILABLE = "Das ADR ist zurzeit nicht verfügbar!";

	/* COMPONENTS */

	// General Dialogs
	public static final String GENERAL_DIALOG_BUTTON_SAVE = "Speichern";
	public static final String GENERAL_DIALOG_BUTTON_CANCEL = "Abbrechen";
	public static final String GENERAL_DIALOG_SAVED_CHANGES_SUCCESSFULLY = "Änderungen wurden erfolgreich gespeichert!";
	public static final String GENERAL_DIALOG_SAVED_CHANGES_ERROR = "Es trat ein Fehler beim Speichern der Änderungen auf!";

	// InviteVoterDialog
	public static final String INVITEVOTERDIALOG_TITLE = "Nutzer bearbeiten";
	public static final String INVITEVOTERDIALOG_INFORMATION_TEXT = "Von dir eingeladene Nutzer können dein ADR bewerten und kommentieren.";
	public static final String INVITEVOTERDIALOG_COMBO_BOX_TITLE= "Nutzer auswählen";
	public static final String INVITEVOTERDIALOG_VOTE_INVITE_SUCCESSFUL = "Nutzer wurden erfolgreich geändert!";

	public static final String INVITEVOTERDIALOG_VOTER_ALREADY_REMOVED = "Der Nutzer \"[UserName]\" ist bereits ausgeladen worden!";
	public static final String INVITEVOTERDIALOG_VOTER_ALREADY_VOTED = "Der Nutzer \"[Username]\" hat bereits abgestimmt und konnte daher nicht entfernt werden!";

	// Change Group User Dialog
	public static final String CHANGEGROUPUSERSDIALOG_TITLE = "Nutzergruppe bearbeiten";
	public static final String CHANGEGROUPUSERSDIALOG_INFORMATION_TEXT = "Fügen sie Nutzer der Nutzergruppe hinzu.";
	public static final String CHANGEGROUPUSERSDIALOG_GROUP_REMOVED_SUCCESSFULLY = "Die Nutzergruppe wurde erfolgreich gelöscht";
	public static final String CHANGEGROUPUSERSDIALOG_USER_ALREADY_REMOVED = "Der Nutzer \"[UserName]\" ist bereits entfernt worden!";
	public static final String CHANGEGROUPUSERSDIALOG_COMBO_BOX_TITLE = "Nutzer auswählen";
	public static final String CHANGEGROUPUSERSDIALOG_ACCESS_CHANGE_SUCCESSFUL = "Nutzergruppen wurden erfolgreich geändert!";

	// AccessGroupDialog
	public static final String ACCESS_GROUP_DIALOG_TITLE = "AccessGroups hinzufügen";
	public static final String ACCESS_GROUP_DIALOG_INFORMATION_TEXT = "Füge AccessGroups zum ADR hinzu.";
	public static final String ACCESS_GROUP_DIALOG_COMBO_BOX_TITLE= "AccessGroup auswählen";

	public static final String ACCESS_GROUP_DIALOG_ACCESS_GROUP_ALREADY_REMOVED = "Die AccessGroup \"[AccessGroupName]\" ist bereits entfernt worden!";

	/* CreateNewGroupDialog */
	public static final String CREATENEWGROUP_USERS = "Nutzer hinzufügen";
	public static final String CREATENEWGROUP_RIGHTS = "Rechte";
	public static final String CREATENEWGROUP_NAME = "Name";

	/* Create User Dialog */
	public static final String CREATEUSERDIALOG_LABEL_TITLE = "Neuen Nutzer erstellen";
	public static final String CREATEUSERDIALOG_LABEL_DESCRIPTION = "Erstelle hier einen neuen Benutzer mit einem Benutzernamen und Passwort.";
	public static final String CREATEUSERDIALOG_BUTTON_CONFIRM = "Nutzer erstellen";
	public static final String CREATEUSERDIALOG_NOTIFICATION_SUCCESS = "Der Nutzer \"[UserName]\" wurde erfolgreich erstellt!";
	public static final String CREATEUSERDIALOG_NOTIFICATION_ERROR = "Der Nutzer \"[UserName]\" existiert bereits!";
}
