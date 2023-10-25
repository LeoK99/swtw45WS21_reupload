package com.buschmais;

import com.buschmais.backend.adr.ADR;
import com.buschmais.backend.adr.ADRContainer;
import com.buschmais.backend.adr.ADRContainerRepository;
import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.status.ADRStatusApproved;
import com.buschmais.backend.adr.status.ADRStatusProposed;
import com.buschmais.backend.adr.status.ADRStatusSuperseded;
import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.adrAccess.AccessRights;
import com.buschmais.backend.adrAccess.dataAccess.ADRAccessDao;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.dataAccess.UserDao;
import com.buschmais.backend.voting.ADRReview;
import com.buschmais.backend.voting.UserIsNotInvitedException;
import com.buschmais.backend.voting.VoteType;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Set;


/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "swt21w45")
@PWA(name = "swt21w45", shortName = "swt21w45", offlineResources = {"images/logo.png"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Push
@EnableMongoRepositories
public class Application extends SpringBootServletInitializer implements AppShellConfigurator, CommandLineRunner {

	@Autowired
	UserDao userRepo;

	@Autowired
	ADRDao adrRepo;

	@Autowired
	ADRAccessDao accessDao;

	@Autowired
	ADRContainerRepository adrContainerRepository;

	public static ADRContainer root = new ADRContainer("root"); //just for testing this will be removed later

    public static void main(String[] args) {
        /*LaunchUtil.launchBrowserInDevelopmentMode(*/SpringApplication.run(Application.class, args);
    }

	@Override
	public void run(String... args) {

		userRepo.deleteAll();
		adrRepo.deleteAll();
		accessDao.deleteAll();


		//ADRContainer root
		root = adrContainerRepository.save(root);

		/* User */
		User frode = new User("Frode", "frodemeier");
		User eric = new User("Eric", "ericmeyer");
		User leo = new User("Leonard", "leonardkahl");
		User mahmoud = new User("Mahmoud", "mahmoudelnashar");
		User yujia = new User("Yujia", "yujiahu");

		eric.getRights().setCanManageUsers(true);
		eric.getRights().setCanManageVoting(true);

		frode = userRepo.save(frode);
		eric = userRepo.save(eric);
		leo = userRepo.save(leo);
		mahmoud = userRepo.save(mahmoud);
		yujia = userRepo.save(yujia);

		/* Log4J ADR) */
		ADR log4J = new ADR(root, "log4J", "Logging-Bibliothek Log4J nutzen", leo,
				"Wir benötigen einen Logger, um alle Meldungen dokumentieren zu können.",
				"Dafür sollten wir die Logging-Bibliothek Log4J nutzen, da sie gut getestet wurde und weltweit mit am häufigsten eingesetzt wird (umfangreicher Support).",
				"Jeder Mitarbeiter muss die Bibliothek in seinen Klassen implementieren.");

		log4J = adrRepo.save(log4J);

		ADRReview log4JReview = new ADRReview();
		log4JReview.addVoter(mahmoud);
		log4JReview.addVoter(yujia);
		log4JReview.addVoter(frode);

		try {
			log4JReview.putVote(mahmoud, VoteType.FOR);
			log4JReview.putVote(yujia, VoteType.AGAINST);
			log4JReview.putVote(frode, VoteType.FOR);
		} catch (UserIsNotInvitedException e) {
			e.printStackTrace();
		}

		log4J.setStatus(new ADRStatusApproved(log4JReview));

		log4J = adrRepo.save(log4J);

		/* MYSQL */
		ADR mysql = new ADR(root, "mysql", "MySQL für Persistenz nutzen", leo,
				"Wir benötigen ein relationales Datenbankmodell zum abspeichern der Nutzer",
				"Für die Persistierung sollten wir MYSQL verwenden, da es Objekte gut in Beziehung setzt.",
				"Es müssen Methoden geschrieben, um Nutzer abzuspeichern, welche von anderen Mitarbeitern aufgerufen werden."
				);

		mysql = adrRepo.save(mysql);

		ADRReview mysqlReview = new ADRReview();
		mysqlReview.addVoter(mahmoud);
		mysqlReview.addVoter(yujia);
		mysqlReview.addVoter(eric);

		try {
			mysqlReview.putVote(mahmoud, VoteType.AGAINST);
			mysqlReview.putVote(yujia, VoteType.FOR);
			mysqlReview.putVote(eric, VoteType.FOR);
		} catch (UserIsNotInvitedException e) {
			e.printStackTrace();
		}

		mysql.setStatus(new ADRStatusApproved(mysqlReview));

		mysql = adrRepo.save(mysql);

		/* MongoDB */
		ADR mongoDB = new ADR(root, "mongoDB", "MongoDb für Speicherung", frode,
				"Eine Datenbank ist notwendig, damit wir die Nutzer abspeichern können. (MYSQL zu umständlich)",
				"Daher sollten wir MongoDB verwenden, da das abspeichern sehr einfach ist (JSON-ähnlich)",
				"Jeder Mitarbeiter muss berücksichtigen, dass die Daten nun als Dokumente abgespeichert werden."
		);

		mongoDB = adrRepo.save(mongoDB);

		mongoDB.setStatus(new ADRStatusProposed());

		ADRReview mongoDBReview = mongoDB.getStatus().getAdrReview();
		mongoDBReview.addVoter(eric);
		mongoDBReview.addVoter(frode);
		mongoDBReview.addVoter(yujia);

		try {
			mongoDBReview.putVote(eric, VoteType.FOR);
			mongoDBReview.putVote(frode, VoteType.FOR);
			mongoDBReview.putVote(yujia, VoteType.FOR);
		} catch (UserIsNotInvitedException e) {
			e.printStackTrace();
		}

		mongoDB.setSupersededIds(Set.of(mysql.getId()));

		mongoDB.setStatus(new ADRStatusApproved(mongoDBReview));

		mongoDB = adrRepo.save(mongoDB);

		/* AccessGroup admin */
		AccessRights loggerRights = new AccessRights();
		loggerRights.setVotable(true);
		loggerRights.setWritable(true);
		loggerRights.setReadable(true);

		AccessGroup logger_developer = new AccessGroup("Logger Developer", loggerRights, Set.of(yujia, leo));

		logger_developer = accessDao.save(logger_developer);

		/* AccessGroup developer1 */
		AccessRights datenbankDeveloperRights = new AccessRights();
		datenbankDeveloperRights.setVotable(false);
		datenbankDeveloperRights.setWritable(true);
		datenbankDeveloperRights.setReadable(true);

		AccessGroup datenbankDeveloper = new AccessGroup("Datenbank Developer", datenbankDeveloperRights, Set.of(frode, mahmoud));

		datenbankDeveloper = accessDao.save(datenbankDeveloper);

		/* AccessGroup developer2 */
		AccessRights frontendDeveloperRights = new AccessRights();
		frontendDeveloperRights.setVotable(false);
		frontendDeveloperRights.setWritable(false);
		frontendDeveloperRights.setReadable(false);

		AccessGroup developer2 = new AccessGroup("Frontend Developer", frontendDeveloperRights, Set.of(eric, yujia, leo));

		developer2 = accessDao.save(developer2);

		/* Set access groups */
		log4J.setAccessGroups(Set.of(logger_developer, developer2));
		mongoDB.setAccessGroups(Set.of(datenbankDeveloper, developer2));
		mysql.setAccessGroups(Set.of(logger_developer, datenbankDeveloper, developer2));

		// set mysql superseded
		mysql.setStatus(new ADRStatusSuperseded(mysqlReview, mongoDB));

		// save
		log4J = adrRepo.save(log4J);
		mongoDB = adrRepo.save(mongoDB);
		mysql = adrRepo.save(mysql);
	}

}
