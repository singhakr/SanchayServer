package sanchay.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sanchay.common.types.AnnotationLevelType;
import sanchay.server.dao.auth.model.domain.*;
import sanchay.server.service.FileStorageService;
import sanchay.server.service.SanchayUserService;
import sanchay.server.utils.SanchayServerUtils;

import java.util.Properties;

@EnableConfigurationProperties
@SpringBootApplication
@Slf4j
public class SanchayServerApplication extends SpringBootServletInitializer {

	public static String SANCHAY_CONFIG_PATH;
	public static String SANCHAY_CONFIG_FILENAME = "sanchay-server-config.txt";
	public static Properties SANCHAY_CONFIG_PROPERTIES;

	public static void main(String[] args) {
		SpringApplication.run(SanchayServerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SanchayServerApplication.class);
	}
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(SanchayUserService userService, FileStorageService fileStorageService)
	{
		return  args -> {

			SanchayServerUtils.printEnvironmentVariables();

			SANCHAY_CONFIG_PATH = System.getenv("SANCHAY_CONFIG") + "/" + SANCHAY_CONFIG_FILENAME;
			SANCHAY_CONFIG_PROPERTIES = SanchayServerUtils.loadPropertiesFile(SANCHAY_CONFIG_PATH);

			log.info("Loaded properties file {}.", SANCHAY_CONFIG_PATH);
			log.info("Sanchay annotation shared folder is: {}", SANCHAY_CONFIG_PROPERTIES.getProperty("SHARED_ANNOTATION_FOLDER"));

			if(userService.isDatabaseEmtpy())
			{
				userService.clearDatabase();

//				initSampleData(userService);
				initDefaultData(userService);
			}

//			initSampleData(userService);
//			initSampleDataExt(userService);
 		};
	}

	private void initDefaultData(SanchayUserService userService)
	{
//		SanchayResourceLanguage language = new SanchayResourceLanguage();
//		language.setName("Assamese");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Bengali");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Gujarati");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Dogri");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Kannada");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Kashmiri");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Konkani");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Malayalam");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Manipuri");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Marathi");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Nepali");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Odiya");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Punjabi");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Sanskrit");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Sindhi");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Tamil");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Telugu");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Urdu");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Bodo");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Santhali");
//		userService.saveLanguage(language, true);
//
//		language = new SanchayResourceLanguage();
//		language.setName("Maithili");
//		userService.saveLanguage(language, true);

		SanchayResourceLanguage language = new SanchayResourceLanguage();
		language.setName("Hindi");
		userService.saveLanguage(language, true);

		SanchayOrganisation organisation = new SanchayOrganisation();
		organisation.setName("IIIT-H");
		organisation.setLongName("IIIT-Hyderabad");
		userService.saveOrganisation(organisation, true);

//		SanchayAnnotationLevel annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.RAW_TEXT.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.TOKENIZED_TEXT.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);

		SanchayAnnotationLevel annotationLevel = new SanchayAnnotationLevel();
		annotationLevel.setName(AnnotationLevelType.POS_TAGGED.toString());
		userService.saveAnnotationLevel(annotationLevel, true);

//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.MORPH_ANALYZED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.CHUNK_TAGGED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.PS_TREE_ANNOTATED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.DEPENDENCY_ANNOTATED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.ANAPHORA_ANNOTATED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.WORD_SENSE_TAGGED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.NAMED_ENTITY_TAGGED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);
//
//		annotationLevel = new SanchayAnnotationLevel();
//		annotationLevel.setName(AnnotationLevelType.DISCOURSE_TAGGED.toString());
//		userService.saveAnnotationLevel(annotationLevel, true);

		SanchayRole viewerRole = new SanchayRole();
		viewerRole.setName(SanchayRole.VIEWER);
		userService.saveRole(viewerRole, true);

		SanchayRole annotatorRole = new SanchayRole();
		annotatorRole.setName(SanchayRole.ANNOTATOR);
		userService.saveRole(annotatorRole, true);

		SanchayRole validatorRole = new SanchayRole();
		validatorRole.setName(SanchayRole.VALIDATOR);
		userService.saveRole(validatorRole, true);

		SanchayRole managerRole = new SanchayRole();
		managerRole.setName(SanchayRole.MANAGER);
		userService.saveRole(managerRole, true);

		SanchayRole rootRole = new SanchayRole();
		rootRole.setName(SanchayRole.ROOT);
		userService.saveRole(rootRole, true);

		SanchayUser user = new SanchayUser();
		user.setUsername("admin");
		user.setFirstName("Pruthwik");
		user.setLastName("Mishra");
		user.setPassword("1234");
		user.addRole(rootRole);
		user.addRole(managerRole);
		user.addRole(validatorRole);
		user.addRole(annotatorRole);
		user.addRole(viewerRole);
		user.addLanguage(language);
		user.addOrganisation(organisation);
		user.addAnnotationLevel(annotationLevel);
		user.setCurrentRoleName(SanchayRole.ROOT);
		user.setCurrentLanguageName(language.getName());
		user.setCurrentOrganisationName(organisation.getName());
		user.setCurrentAnnotationLevelName(annotationLevel.getName());
		userService.saveUser(user, true);
	}

	private void initSampleDataExt(SanchayUserService userService)
	{
		SanchayAnnotationLevel annotationLevel = new SanchayAnnotationLevel();
		annotationLevel.setName(AnnotationLevelType.CHUNK_TAGGED.toString());
		userService.saveAnnotationLevel(annotationLevel, true);

		annotationLevel = new SanchayAnnotationLevel();
		annotationLevel.setName(AnnotationLevelType.MORPH_ANALYZED.toString());
		userService.saveAnnotationLevel(annotationLevel, true);

		annotationLevel = new SanchayAnnotationLevel();
		annotationLevel.setName(AnnotationLevelType.DEPENDENCY_ANNOTATED.toString());
		userService.saveAnnotationLevel(annotationLevel, true);
	}

	private void initSampleData(SanchayUserService userService)
	{
		SanchayResourceLanguage language = new SanchayResourceLanguage();
		language.setName("Hindi");
		userService.saveLanguage(language, true);

		SanchayOrganisation organisation = new SanchayOrganisation();
		organisation.setName("IIIT-H");
		organisation.setLongName("IIIT-Hyderabad");
		userService.saveOrganisation(organisation, true);

		SanchayAnnotationLevel annotationLevel = new SanchayAnnotationLevel();
		annotationLevel.setName(AnnotationLevelType.POS_TAGGED.toString());
		userService.saveAnnotationLevel(annotationLevel, true);

//			SanchayPrivilege annotationPrivilege = new SanchayPrivilege();
//			annotationPrivilege.setName(SanchayPrivilege.ANNOTATION);

		SanchayRole annotatorRole = new SanchayRole();
		annotatorRole.setName(SanchayRole.ANNOTATOR);
//			annotatorRole.setPrivileges(new HashSet<>());
//			annotatorRole.getPrivileges().add(annotationPrivilege);
		userService.saveRole(annotatorRole, true);

//			SanchayPrivilege validationPrivilege = new SanchayPrivilege();
//			validationPrivilege.setName(SanchayPrivilege.VALIDATION);

		SanchayRole validatorRole = new SanchayRole();
		validatorRole.setName(SanchayRole.VALIDATOR);
//			validatorRole.setPrivileges(new HashSet<>());
//			validatorRole.getPrivileges().add(annotationPrivilege);
		userService.saveRole(validatorRole, true);

//			SanchayPrivilege managementPrivilege = new SanchayPrivilege();
//			managementPrivilege.setName(SanchayPrivilege.MANAGEMENT);

		SanchayRole managerRole = new SanchayRole();
		managerRole.setName(SanchayRole.MANAGER);
//			managerRole.setPrivileges(new HashSet<>());
//			managerRole.getPrivileges().add(managementPrivilege);
		userService.saveRole(managerRole, true);

//			SanchayPrivilege rootPrivilege = new SanchayPrivilege();
//			rootPrivilege.setName(SanchayPrivilege.EVERYTHING);

		SanchayRole rootRole = new SanchayRole();
		rootRole.setName(SanchayRole.ROOT);
//			rootRole.setPrivileges(new HashSet<>());
//			rootRole.getPrivileges().add(rootPrivilege);
		userService.saveRole(rootRole, true);

		SanchayUser user = new SanchayUser();
		user.setUsername("plato");
		user.setFirstName("Plato");
		user.setLastName("Greek");
		user.setPassword("1234");
//			user.setRoles(new HashSet<>());
		user.addRole(annotatorRole);
		user.addLanguage(language);
		user.addOrganisation(organisation);
		user.addAnnotationLevel(annotationLevel);
		user.setCurrentRoleName(annotatorRole.getName());
		user.setCurrentLanguageName(language.getName());
		user.setCurrentOrganisationName(organisation.getName());
		user.setCurrentAnnotationLevelName(annotationLevel.getName());
		userService.saveUser(user, true);

		user = new SanchayUser();
		user.setUsername("democritus");
		user.setFirstName("Democritus");
		user.setLastName("Greek");
		user.setPassword("1234");
		user.addRole(validatorRole);
		user.addLanguage(language);
		user.addOrganisation(organisation);
		user.addAnnotationLevel(annotationLevel);
		user.setCurrentRoleName(validatorRole.getName());
		user.setCurrentLanguageName(language.getName());
		user.setCurrentOrganisationName(organisation.getName());
		user.setCurrentAnnotationLevelName(annotationLevel.getName());
		userService.saveUser(user, true);

		user = new SanchayUser();
		user.setUsername("aristotle");
		user.setFirstName("Aristotle");
		user.setLastName("Greek");
		user.setPassword("1234");
		user.addRole(managerRole);
		user.addLanguage(language);
		user.addOrganisation(organisation);
		user.addAnnotationLevel(annotationLevel);
		user.setCurrentRoleName(managerRole.getName());
		user.setCurrentLanguageName(language.getName());
		user.setCurrentOrganisationName(organisation.getName());
		user.setCurrentAnnotationLevelName(annotationLevel.getName());
		userService.saveUser(user, true);

		user = new SanchayUser();
		user.setUsername("socrates");
		user.setFirstName("Socrates");
		user.setLastName("Greek");
		user.setPassword("1234");
		user.addRole(rootRole);
		user.addLanguage(language);
		user.addOrganisation(organisation);
		user.addAnnotationLevel(annotationLevel);
		user.setCurrentRoleName(rootRole.getName());
		user.setCurrentLanguageName(language.getName());
		user.setCurrentOrganisationName(organisation.getName());
		user.setCurrentAnnotationLevelName(annotationLevel.getName());
		userService.saveUser(user, true);

//			userService.saveUser(new SanchayUser(null, "Aristotle", "aristotle", "1234",
//					new ArrayList<>()));
//			userService.saveUser(new SanchayUser(null, "Socrates", "socrates", "1234",
//					new ArrayList<>()));
//			userService.saveUser(new SanchayUser(null, "Democritus", "democritus", "1234",
//					new ArrayList<>()));
//
//			userService.addRoleToUser("plato", "ROLE_USER");
//			userService.addRoleToUser("aristotle", "ROLE_USER");
//			userService.addRoleToUser("aristotle", "ROLE_MANAGER");
//			userService.addRoleToUser("democritus", "ROLE_USER");
//			userService.addRoleToUser("democritus", "ROLE_MANAGER");
//			userService.addRoleToUser("democritus", "ROLE_ADMIN");
//			userService.addRoleToUser("socrates", "ROLE_USER");
//			userService.addRoleToUser("socrates", "ROLE_MANAGER");
//			userService.addRoleToUser("socrates", "ROLE_ADMIN");
//			userService.addRoleToUser("socrates", "SOLE_SUPER_ADMIN");
	}

// Remove if the above works
//	private void initSampleData(SanchayUserService userService)
//	{
//		SanchayResourceLanguage language = new SanchayResourceLanguage();
//		language.setName("Hindi");
//		userService.saveLanguage(language);
//
//		SanchayOrganisation organisation = new SanchayOrganisation();
//		language.setName("IIIT-H");
//		userService.saveLanguage(language);
//
////			SanchayPrivilege annotationPrivilege = new SanchayPrivilege();
////			annotationPrivilege.setName(SanchayPrivilege.ANNOTATION);
//
//		SanchayRole annotatorRole = new SanchayRole();
//		annotatorRole.setName(SanchayRole.ANNOTATOR);
////			annotatorRole.setPrivileges(new HashSet<>());
////			annotatorRole.getPrivileges().add(annotationPrivilege);
//		userService.saveRole(annotatorRole);
//
////			SanchayPrivilege validationPrivilege = new SanchayPrivilege();
////			validationPrivilege.setName(SanchayPrivilege.VALIDATION);
//
//		SanchayRole validatorRole = new SanchayRole();
//		validatorRole.setName(SanchayRole.VALIDATOR);
////			validatorRole.setPrivileges(new HashSet<>());
////			validatorRole.getPrivileges().add(annotationPrivilege);
//		userService.saveRole(validatorRole);
//
////			SanchayPrivilege managementPrivilege = new SanchayPrivilege();
////			managementPrivilege.setName(SanchayPrivilege.MANAGEMENT);
//
//		SanchayRole managerRole = new SanchayRole();
//		managerRole.setName(SanchayRole.MANAGER);
////			managerRole.setPrivileges(new HashSet<>());
////			managerRole.getPrivileges().add(managementPrivilege);
//		userService.saveRole(managerRole);
//
////			SanchayPrivilege rootPrivilege = new SanchayPrivilege();
////			rootPrivilege.setName(SanchayPrivilege.EVERYTHING);
//
//		SanchayRole rootRole = new SanchayRole();
//		rootRole.setName(SanchayRole.ROOT);
////			rootRole.setPrivileges(new HashSet<>());
////			rootRole.getPrivileges().add(rootPrivilege);
//		userService.saveRole(rootRole);
//
//		SanchayUser user = new SanchayUser();
//		user.setUsername("plato");
//		user.setFirstName("Plato");
//		user.setPassword("1234");
////			user.setRoles(new HashSet<>());
//		user.getRoles().add(annotatorRole);
//		user.getLanguages().add(language);
//		userService.saveUser(user);
//
//		user = new SanchayUser();
//		user.setUsername("Democritus");
//		user.setFirstName("democritus");
//		user.setPassword("1234");
//		user.setRoles(new HashSet<>());
////			user.getRoles().add(validatorRole);
//		user.getLanguages().add(language);
//		userService.saveUser(user);
//
//		user = new SanchayUser();
//		user.setUsername("Aristotle");
//		user.setFirstName("aristotle");
//		user.setPassword("1234");
////			user.setRoles(new HashSet<>());
//		user.getRoles().add(managerRole);
//		user.getLanguages().add(language);
//		userService.saveUser(user);
//
//		user = new SanchayUser();
//		user.setUsername("Socrates");
//		user.setFirstName("socrates");
//		user.setPassword("1234");
////			user.setRoles(new HashSet<>());
//		user.getRoles().add(rootRole);
//		user.getLanguages().add(language);
//		userService.saveUser(user);
//
////			userService.saveUser(new SanchayUser(null, "Aristotle", "aristotle", "1234",
////					new ArrayList<>()));
////			userService.saveUser(new SanchayUser(null, "Socrates", "socrates", "1234",
////					new ArrayList<>()));
////			userService.saveUser(new SanchayUser(null, "Democritus", "democritus", "1234",
////					new ArrayList<>()));
////
////			userService.addRoleToUser("plato", "ROLE_USER");
////			userService.addRoleToUser("aristotle", "ROLE_USER");
////			userService.addRoleToUser("aristotle", "ROLE_MANAGER");
////			userService.addRoleToUser("democritus", "ROLE_USER");
////			userService.addRoleToUser("democritus", "ROLE_MANAGER");
////			userService.addRoleToUser("democritus", "ROLE_ADMIN");
////			userService.addRoleToUser("socrates", "ROLE_USER");
////			userService.addRoleToUser("socrates", "ROLE_MANAGER");
////			userService.addRoleToUser("socrates", "ROLE_ADMIN");
////			userService.addRoleToUser("socrates", "SOLE_SUPER_ADMIN");
//	}
}
