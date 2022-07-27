package sanchay.server.service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sanchay.server.dao.auth.model.domain.*;
import sanchay.server.dto.auth.model.domain.*;
import sanchay.server.dto.utils.SanchayBeanUtils;
import sanchay.server.mapper.*;
import sanchay.server.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sanchay.server.utils.SanchayServiceUtils;

import javax.transaction.Transactional;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class SanchayUserServiceImpl implements SanchayUserService, UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
//    private final PrivilegeRepo privilegeRepo;
    private final LanguageRepo languageRepo;

    private final OrganisationRepo organisationRepo;

    private final AnnotationLevelRepo annotationLevelRepo;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;
//    private SanchayModelMapper modelMapper;

    @Autowired
    private ObjectMapper plainObjectMapper;

//    @Autowired
//    private ObjectMapper polymorphicObjectMapper;

//    private SanchayDeepModelMapper deepModelMapper;
//
//    public SanchayDeepModelMapper getDeepModelMapper() {
//        return deepModelMapper;
//    }

    public ModelMapper getModelMapper()
//    public SanchayModelMapper getModelMapper()
    {
        return modelMapper;
    }

    public ObjectMapper getPlainObjectMapper()
//    public SanchayModelMapper getModelMapper()
    {
        return plainObjectMapper;
    }

//    public ObjectMapper getPolymorphicObjectMapper()
////    public SanchayModelMapper getModelMapper()
//    {
//        return polymorphicObjectMapper;
//    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        if(deepModelMapper == null)
//        {
//            deepModelMapper = getModelMapperInstance(userRepo, roleRepo, languageRepo,
//                    organisationRepo, annotationLevelRepo);
//        }
//
//        if(modelMapper == null)
//        {
//            modelMapper = SanchayMapperUtils.getModelMapperInstance();
//        }

        SanchayUser user = userRepo.findByUsername(username);
        if(user == null)
        {
            log.error("User {} not found in the database.", username);
            throw new UsernameNotFoundException("User not found in the database.");
        }

        log.info("User {} found in the database with pass word {}.", user.getUsername(), user.getPassword());

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach( (rolename, role) -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    private SanchayUser getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        SanchayUser currentUser = userRepo.findByUsername(username);

        return currentUser;
    }

    private SanchayUserDTO getCurrentUserDTO()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        SanchayUser currentUser = userRepo.findByUsername(username);

        SanchayUserDTO currentUserDTO = modelMapper.map(currentUser, SanchayUserDTO.class);

        return currentUserDTO;
    }

    @Override
    public SanchayUser getUser(String username, boolean serverSide) {

        log.info("Fetching user {}", username);
        SanchayUser user = userRepo.findByUsername(username);

        if(serverSide || SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user)) {

            return user;
        }

        return null;
    }

    @Override
    public SanchayUserDTO getUserDTO(String username, boolean serverSide) {

        log.info("Fetching user {}", username);
        SanchayUser user = userRepo.findByUsername(username);

        if(serverSide || SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user)) {

            SanchayUserDTO userDTO = modelMapper.map(user, SanchayUserDTO.class);

            return userDTO;
        }

        return null;
    }

    @Override
    public Map<String, SanchayUserDTO> getAllUsers() {
        log.info("Fetching all users");

        Map<String, SanchayUser> userMap = SanchayServiceUtils.getAllUsers(userRepo, getCurrentUser());

        Map<String, SanchayUserDTO> userDTOMap = SanchayDeepMapperUtils.convertMap(userMap, SanchayUserDTO.class, modelMapper);

        return userDTOMap;
    }

    @Override
    public Map<String, SanchayRoleDTO> getAllRoles() {
        log.info("Fetching all roles");

        List<SanchayRole> roleList = roleRepo.findAll();

        Map<String, SanchayRole> roleMap = SanchayServiceUtils.listToMap(roleList);

        SanchayRole currentRole = getCurrentUser().getCurrentRole();

        if(currentRole.getName().equals(SanchayRole.VIEWER) || currentRole.getName().equals(SanchayRole.ANNOTATOR))
        {
            roleMap.remove(SanchayRole.VALIDATOR);
            roleMap.remove(SanchayRole.MANAGER);
            roleMap.remove(SanchayRole.ROOT);
        }
        else if(currentRole.getName().equals(SanchayRole.VALIDATOR))
        {
            roleMap.remove(SanchayRole.MANAGER);
            roleMap.remove(SanchayRole.ROOT);
        }
        else if(currentRole.getName().equals(SanchayRole.MANAGER))
        {
            roleMap.remove(SanchayRole.ROOT);
        }

        Map<String, SanchayRoleDTO> roleDTOMap = SanchayDeepMapperUtils.convertMap(roleMap, SanchayRoleDTO.class, modelMapper);

        return roleDTOMap;
    }

    @Override
    public Map<String, SanchayResourceLanguageDTO> getAllLanguages() {
        log.info("Fetching all languages");

        Map<String, SanchayResourceLanguage> languageMap = SanchayServiceUtils.getAllLanguages(languageRepo, getCurrentUser());

        Map<String, SanchayResourceLanguageDTO> languageDTOMap = SanchayDeepMapperUtils.convertMap(languageMap, SanchayResourceLanguageDTO.class, modelMapper);

        return languageDTOMap;
    }

    @Override
    public Map<String, SanchayOrganisationDTO> getAllOrganisations() {
        log.info("Fetching all organisation");

        Map<String, SanchayOrganisation> organisationMap = SanchayServiceUtils.getAllOrganisations(organisationRepo, getCurrentUser());

        Map<String, SanchayOrganisationDTO> organisationDTOMap = SanchayDeepMapperUtils.convertMap(organisationMap, SanchayOrganisationDTO.class, modelMapper);

        return organisationDTOMap;
    }

    @Override
    public Map<String, SanchayAnnotationLevelDTO> getAllAnnotationLevels()
    {
        log.info("Fetching all annotation levels");

        Map<String, SanchayAnnotationLevel> annotationLevelMap = SanchayServiceUtils.getAllAnnotationLevels(annotationLevelRepo, getCurrentUser());

        Map<String, SanchayAnnotationLevelDTO> annotationLevelDTOMap = SanchayDeepMapperUtils.convertMap(annotationLevelMap, SanchayAnnotationLevelDTO.class, modelMapper);

        return annotationLevelDTOMap;

    }

    @Override
    public Map<String, SanchayRoleDTO> getUserRolesDTO(String username)
    {
        SanchayUser user = userRepo.findByUsername(username);

        if(SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user))
        {
            log.info("Fetching all roles for user {}", username);

            Map<String, SanchayRole> roleMap = user.getRoles();

            Map<String, SanchayRoleDTO> roleDTOMap = SanchayDeepMapperUtils.convertMap(roleMap, SanchayRoleDTO.class, modelMapper);

            return roleDTOMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayRole> getUserRoles(String username)
    {
        SanchayUser user = userRepo.findByUsername(username);

        if(SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user))
        {
            log.info("Fetching all roles for user {}", username);

            Map<String, SanchayRole> roleMap = user.getRoles();

            return roleMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayResourceLanguageDTO> getUserLanguages(String username)
    {
        SanchayUser user = userRepo.findByUsername(username);

        if(SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user)) {
            log.info("Fetching all languages for user {}", username);

            Map<String, SanchayResourceLanguage> languageMap = user.getLanguages();

            Map<String, SanchayResourceLanguageDTO> languageDTOMap = SanchayDeepMapperUtils.convertMap(languageMap, SanchayResourceLanguageDTO.class, modelMapper);

            return languageDTOMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayOrganisationDTO> getUserOrganisations(String username)
    {
        SanchayUser user = userRepo.findByUsername(username);

        if(SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user)) {
            log.info("Fetching all organisations for user {}", username);

            Map<String, SanchayOrganisation> organisationMap = user.getOrganisations();

            Map<String, SanchayOrganisationDTO> organisationDTOMap = SanchayDeepMapperUtils.convertMap(organisationMap, SanchayOrganisationDTO.class, modelMapper);

            return organisationDTOMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayAnnotationLevelDTO> getUserAnnotationLevels(String username)
    {
        SanchayUser user = userRepo.findByUsername(username);

        if(SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user)) {
            log.info("Fetching all annotation levels for user {}", username);

            Map<String, SanchayAnnotationLevel> annotationLevelMap = user.getAnnotationLevels();

            Map<String, SanchayAnnotationLevelDTO> annotationLevelDTOMap = SanchayDeepMapperUtils.convertMap(annotationLevelMap, SanchayAnnotationLevelDTO.class, modelMapper);

            return annotationLevelDTOMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayUserDTO> getUsersForRole(String rolename)
    {
        SanchayRole role = roleRepo.findByName(rolename);
        if(SanchayServiceUtils.hasPermissionToAddRole(getCurrentUser(), role))
        {
            log.info("Fetching all users for role {}", rolename);

            List<SanchayUser> userList = new ArrayList<SanchayUser>(roleRepo.findByName(rolename).getUsers().values());

            Map<String, SanchayUser> userMap = SanchayServiceUtils.listToMap(userList);

            Map<String, SanchayUserDTO> userDTOMap = SanchayDeepMapperUtils.convertMap(userMap, SanchayUserDTO.class, modelMapper);

            return userDTOMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayUserDTO> getUsersForLanguage(String languageName)
    {
        SanchayResourceLanguage language = languageRepo.findByName(languageName);

        if(SanchayServiceUtils.hasPermissionToAddLanguage(getCurrentUser(), language)) {
            log.info("Fetching all users for language {}", languageName);

            List<SanchayUser> userList = new ArrayList<SanchayUser>(languageRepo.findByName(languageName).getUsers().values());

            Map<String, SanchayUser> userMap = SanchayServiceUtils.listToMap(userList);

            Map<String, SanchayUserDTO> userDTOMap = SanchayDeepMapperUtils.convertMap(userMap, SanchayUserDTO.class, modelMapper);

            return userDTOMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayUserDTO> getUsersForOrganisation(String organisationName)
    {
        SanchayOrganisation organisation = organisationRepo.findByName(organisationName);

        if(SanchayServiceUtils.hasPermissionToAddOrganisation(getCurrentUser(), organisation)) {
            log.info("Fetching all users for organisation {}", organisationName);

            List<SanchayUser> userList = new ArrayList<SanchayUser>(organisationRepo.findByName(organisationName).getUsers().values());

            Map<String, SanchayUser> userMap = SanchayServiceUtils.listToMap(userList);

            Map<String, SanchayUserDTO> userDTOMap = SanchayDeepMapperUtils.convertMap(userMap, SanchayUserDTO.class, modelMapper);

            return userDTOMap;
        }

        return null;
    }

    @Override
    public Map<String, SanchayUserDTO> getUsersForAnnotationLevel(String annotationLevelName)
    {
        SanchayAnnotationLevel annotationLevel = annotationLevelRepo.findByName(annotationLevelName);

        if(SanchayServiceUtils.hasPermissionToAddAnnotationLevel(getCurrentUser(), annotationLevel)) {
            log.info("Fetching all users for annotation level {}", annotationLevelName);

            List<SanchayUser> userList = new ArrayList<SanchayUser>(annotationLevelRepo.findByName(annotationLevelName).getUsers().values());

            Map<String, SanchayUser> userMap = SanchayServiceUtils.listToMap(userList);

            Map<String, SanchayUserDTO> userDTOMap = SanchayDeepMapperUtils.convertMap(userMap, SanchayUserDTO.class, modelMapper);

            return userDTOMap;
        }

        return null;
    }
    public Map<String, SanchayOrganisationDTO> getOrganisationsForLanguage(String languageName)
    {
        Map<String, SanchayOrganisation> organisationMap = SanchayServiceUtils.getOrganisationsForLanguage(languageRepo.findByName(languageName));

        Map<String, SanchayOrganisationDTO> organisationDTOMap = SanchayDeepMapperUtils.convertMap(organisationMap, SanchayOrganisationDTO.class, modelMapper);

        return organisationDTOMap;
    }
    public Map<String, SanchayAnnotationLevelDTO> getAnnotationLevelsForLanguage(String languageName)
    {
        Map<String, SanchayAnnotationLevel> annotationLevelMap = SanchayServiceUtils.getAnnotationLevelsForLanguage(languageRepo.findByName(languageName));

        Map<String, SanchayAnnotationLevelDTO> annotationLevelDTOMap = SanchayDeepMapperUtils.convertMap(annotationLevelMap, SanchayAnnotationLevelDTO.class, modelMapper);

        return annotationLevelDTOMap;
    }
    public Map<String, SanchayResourceLanguageDTO> getLanguagesForOrganisation(String organisationName)
    {
        Map<String, SanchayResourceLanguage> languageMap = SanchayServiceUtils.getLanguagesForOrganisation(organisationRepo.findByName(organisationName));

        Map<String, SanchayResourceLanguageDTO> languageDTOMap = SanchayDeepMapperUtils.convertMap(languageMap, SanchayResourceLanguageDTO.class, modelMapper);

        return languageDTOMap;
    }
    public Map<String, SanchayAnnotationLevelDTO> getAnnotationLevelsForOrganisation(String organisationName)
    {
        Map<String, SanchayAnnotationLevel> annotationLevelMap = SanchayServiceUtils.getAnnotationLevelsForOrganisation(organisationRepo.findByName(organisationName));

        Map<String, SanchayAnnotationLevelDTO> annotationLevelDTOMap = SanchayDeepMapperUtils.convertMap(annotationLevelMap, SanchayAnnotationLevelDTO.class, modelMapper);

        return annotationLevelDTOMap;
    }
    public Map<String, SanchayOrganisationDTO> getOrganisationsForAnnotationLevels(String levelName)
    {
        Map<String, SanchayOrganisation> organisationMap = SanchayServiceUtils.getOrganisationsForAnnotationLevel(annotationLevelRepo.findByName(levelName));

        Map<String, SanchayOrganisationDTO> organisationDTOMap = SanchayDeepMapperUtils.convertMap(organisationMap, SanchayOrganisationDTO.class, modelMapper);

        return organisationDTOMap;
    }
    public Map<String, SanchayResourceLanguageDTO> getLanguagesForAnnotationLevel(String levelName)
    {
        Map<String, SanchayResourceLanguage> languageMap = SanchayServiceUtils.getLanguagesForAnnotationLevel(annotationLevelRepo.findByName(levelName));

        Map<String, SanchayResourceLanguageDTO> languageDTOMap = SanchayDeepMapperUtils.convertMap(languageMap, SanchayResourceLanguageDTO.class, modelMapper);

        return languageDTOMap;
    }

    public SanchayAnnotationManagementUpdateInfo getAnnotationManagementUpdateInfo()
    {
        SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo = SanchayAnnotationManagementUpdateInfo.builder().build();

        annotationManagementUpdateInfo.setAllUsers(getAllUsers());
        annotationManagementUpdateInfo.setAllRoles(getAllRoles());
        annotationManagementUpdateInfo.setAllOrganisations(getAllOrganisations());
        annotationManagementUpdateInfo.setAllLanguages(getAllLanguages());
        annotationManagementUpdateInfo.setAllLevels(getAllAnnotationLevels());

        annotationManagementUpdateInfo.setAllSlimUsers(SanchayMapperUtils.convertMap(getAllUsers(), SanchayUserSlimDTO.class, modelMapper));
        annotationManagementUpdateInfo.setAllSlimRoles(SanchayMapperUtils.convertMap(getAllRoles(), SanchayRoleSlimDTO.class, modelMapper));
        annotationManagementUpdateInfo.setAllSlimOrganisations(SanchayMapperUtils.convertMap(getAllOrganisations(), SanchayOrganisationSlimDTO.class, modelMapper));
        annotationManagementUpdateInfo.setAllSlimLanguages(SanchayMapperUtils.convertMap(getAllLanguages(), SanchayResourceLanguageSlimDTO.class, modelMapper));
        annotationManagementUpdateInfo.setAllSlimLevels(SanchayMapperUtils.convertMap(getAllAnnotationLevels(), SanchayAnnotationLevelSlimDTO.class, modelMapper));
//
//        annotationManagementUpdateInfo.setAssignedUserRoles(new LinkedHashMap<String, Map<String, SanchayRoleDTO>>());
//        annotationManagementUpdateInfo.setAssignedUserOrganisations(new LinkedHashMap<String, Map<String, SanchayOrganisationDTO>>());
//        annotationManagementUpdateInfo.setAssignedUserLanguages(new LinkedHashMap<String, Map<String, SanchayResourceLanguageDTO>>());
//        annotationManagementUpdateInfo.setAssignedUserLevels(new LinkedHashMap<String, Map<String, SanchayAnnotationLevelDTO>>());
//
//        Map<String, SanchayUserDTO> allUsers = annotationManagementUpdateInfo.getAllUsers();
//
//        allUsers.entrySet()
//                .forEach(
//                        (entry) ->
//                        {
//                            annotationManagementUpdateInfo.getAssignedUserRoles().put(entry.getKey(),
//                                    getUserRoles(entry.getKey())
//                                            .entrySet()
//                                            .stream()
//                                            .collect(Collectors.toMap(
//                                                    (innerEntry) -> innerEntry.getKey(),
//                                                    (innerEntry) -> modelMapper.map(innerEntry.getValue(), SanchayRoleDTO.class)
//                                            )));
//
//                            annotationManagementUpdateInfo.getAssignedUserOrganisations().put(entry.getKey(),
//                                    getUserOrganisations(entry.getKey())
//                                            .entrySet()
//                                            .stream()
//                                            .collect(Collectors.toMap(
//                                                    (innerEntry) -> innerEntry.getKey(),
//                                                    (innerEntry) -> modelMapper.map(innerEntry.getValue(), SanchayOrganisationDTO.class)
//                                            )));
//
//                            annotationManagementUpdateInfo.getAssignedUserLanguages().put(entry.getKey(),
//                                    getUserLanguages(entry.getKey())
//                                            .entrySet()
//                                            .stream()
//                                            .collect(Collectors.toMap(
//                                                    (innerEntry) -> innerEntry.getKey(),
//                                                    (innerEntry) -> modelMapper.map(innerEntry.getValue(), SanchayResourceLanguageDTO.class)
//                                            )));
//
//                            annotationManagementUpdateInfo.getAssignedUserLevels().put(entry.getKey(),
//                                    getUserAnnotationLevels(entry.getKey())
//                                            .entrySet()
//                                            .stream()
//                                            .collect(Collectors.toMap(
//                                                    (innerEntry) -> innerEntry.getKey(),
//                                                    (innerEntry) -> modelMapper.map(innerEntry.getValue(), SanchayAnnotationLevelDTO.class)
//                                            )));
//                        }
//                );

        return annotationManagementUpdateInfo;
    }

    public SanchayAnnotationManagementUpdateInfo saveAnnotationManagementUpdateInfo(SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo)
    {
        LinkedHashMap<String, SanchayRole> roleEntitiesToAdd = shallowAddRoles(annotationManagementUpdateInfo);
        LinkedHashMap<String, SanchayOrganisation> organisationEntitiesToAdd = shallowAddOrganisations(annotationManagementUpdateInfo);
        LinkedHashMap<String, SanchayResourceLanguage> languageEntitiesToAdd = shallowAddLanguages(annotationManagementUpdateInfo);
        LinkedHashMap<String, SanchayAnnotationLevel> levelEntitiesToAdd = shallowAddLevels(annotationManagementUpdateInfo);
        LinkedHashMap<String, SanchayUser> userEntitiesToAdd = shallowAddUsers(annotationManagementUpdateInfo);

        log.info("Started saving roles");

        Map<String, SanchayRoleDTO> allRoles = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllRoles());

        allRoles.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayRoleDTO roleDTO = entry.getValue();

                            if(roleDTO.isToBeAdded())
                            {
                                log.info("Adding roles");

                                deepAddRole(roleDTO, roleEntitiesToAdd.get(roleDTO.getName()));
                            }
                            else if(roleDTO.isToBeDeleted())
                            {
                                log.info("Deleting roles");

                                deepDeleteRole(roleDTO);
                            }
                            else if(roleDTO.isDirty())
                            {
                                log.info("Updating roles");

                                deepUpdateRole(roleDTO);
                            }
                        }
                );

        log.info("Started saving organisations");

        Map<String, SanchayOrganisationDTO> allOrganisations = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllOrganisations());

        allOrganisations.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayOrganisationDTO organisationDTO = entry.getValue();

                            if(organisationDTO.isToBeAdded())
                            {
                                log.info("Adding organisations");

                                deepAddOrganisation(organisationDTO, organisationEntitiesToAdd.get(organisationDTO.getName()));
                            }
                            else if(organisationDTO.isToBeDeleted())
                            {
                                log.info("Deleting organisations");

                                deepDeleteOrganisation(organisationDTO);
                            }
                            else if(organisationDTO.isDirty())
                            {
                                log.info("Updating organisations");

                                deepUpdateOrganisation(organisationDTO);
                            }
                        }
                );

        log.info("Started saving languages");

        Map<String, SanchayResourceLanguageDTO> allLanguages = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllLanguages());

        allLanguages.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayResourceLanguageDTO languageDTO = entry.getValue();

                            if(languageDTO.isToBeAdded())
                            {
                                log.info("Adding languages");

                                deepAddLanguage(languageDTO, languageEntitiesToAdd.get(languageDTO.getName()));
                            }
                            else if(languageDTO.isToBeDeleted())
                            {
                                log.info("Deleting languages");

                                deepDeleteLanguage(languageDTO);
                            }
                            else if(languageDTO.isDirty())
                            {
                                log.info("Updating languages");

                                deepUpdateLanguage(languageDTO);
                            }
                        }
                );

        log.info("Started saving levels");

        Map<String, SanchayAnnotationLevelDTO> allLevels = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllLevels());

        allLevels.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayAnnotationLevelDTO levelDTO = entry.getValue();

                            if(levelDTO.isToBeAdded())
                            {
                                log.info("Adding levels");

                                deepAddAnnotationLevel(levelDTO, levelEntitiesToAdd.get(levelDTO.getName()));
                            }
                            else if(levelDTO.isToBeDeleted())
                            {
                                log.info("Deleting levels");

                                deepDeleteAnnotationLevel(levelDTO);
                            }
                            else if(levelDTO.isDirty())
                            {
                                log.info("Updating levels");

                                deepUpdateAnnotationLevel(levelDTO);
                            }
                        }
                );

        log.info("Started saving users");

        Map<String, SanchayUserDTO> allUsers = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllUsers());

        allUsers.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String username = entry.getKey();
                            SanchayUserDTO userDTO = entry.getValue();

                            if(userDTO.isToBeAdded())
                            {
                                log.info("Adding users");

                                deepAddUser(userDTO, userEntitiesToAdd.get(userDTO.getUsername()));
                            }
                            else if(userDTO.isToBeDeleted())
                            {
                                log.info("Deleting users");

                                deepDeleteUser(userDTO);
                            }
                            else if(userDTO.isDirty())
                            {
                                log.info("Updating users");

                                deepUpdateUser(userDTO);
                            }
                        }
                );

        return annotationManagementUpdateInfo;
    }

    private LinkedHashMap<String, SanchayRole> shallowAddRoles(SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo)
    {
        log.info("Started shallow saving roles");

        Map<String, SanchayRoleDTO> allEntitiesDTO = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllRoles());
        LinkedHashMap<String, SanchayRole> entitiesToAdd = new LinkedHashMap<>();

        allEntitiesDTO.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayRoleDTO entityDTO = entry.getValue();

                            if(entityDTO.isToBeAdded())
                            {
                                log.info("Shallow adding role {}", entityDTO.getName());

                                SanchayRole entity = new SanchayRole();

                                try {
                                    SanchayBeanUtils.copyPropertiesNotNull(entity, entityDTO);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }

                                roleRepo.save(entity);

                                entitiesToAdd.put(entityDTO.getName(), entity);
                            }
                        }
                );

        return entitiesToAdd;
    }

    private LinkedHashMap<String, SanchayOrganisation> shallowAddOrganisations(SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo)
    {
        log.info("Started shallow saving organisations");

        Map<String, SanchayOrganisationDTO> allEntitiesDTO = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllOrganisations());
        LinkedHashMap<String, SanchayOrganisation> entitiesToAdd = new LinkedHashMap<>();

        allEntitiesDTO.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayOrganisationDTO entityDTO = entry.getValue();

                            if(entityDTO.isToBeAdded())
                            {
                                log.info("Shallow adding organisation {}", entityDTO.getName());

                                SanchayOrganisation entity = new SanchayOrganisation();

                                try {
                                    SanchayBeanUtils.copyPropertiesNotNull(entity, entityDTO);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }

                                organisationRepo.save(entity);

                                entitiesToAdd.put(entityDTO.getName(), entity);
                            }
                        }
                );

        return entitiesToAdd;
    }

    private LinkedHashMap<String, SanchayResourceLanguage> shallowAddLanguages(SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo)
    {
        log.info("Started shallow saving languages");

        Map<String, SanchayResourceLanguageDTO> allEntitiesDTO = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllLanguages());
        LinkedHashMap<String, SanchayResourceLanguage> entitiesToAdd = new LinkedHashMap<>();

        allEntitiesDTO.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayResourceLanguageDTO entityDTO = entry.getValue();

                            if(entityDTO.isToBeAdded())
                            {
                                log.info("Shallow adding language {}", entityDTO.getName());

                                SanchayResourceLanguage entity = new SanchayResourceLanguage();

                                try {
                                    SanchayBeanUtils.copyPropertiesNotNull(entity, entityDTO);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }

                                languageRepo.save(entity);

                                entitiesToAdd.put(entityDTO.getName(), entity);
                            }
                        }
                );

        return entitiesToAdd;
    }

    private LinkedHashMap<String, SanchayAnnotationLevel> shallowAddLevels(SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo)
    {
        log.info("Started shallow saving level");

        Map<String, SanchayAnnotationLevelDTO> allEntitiesDTO = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllLevels());
        LinkedHashMap<String, SanchayAnnotationLevel> entitiesToAdd = new LinkedHashMap<>();

        allEntitiesDTO.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayAnnotationLevelDTO entityDTO = entry.getValue();

                            if(entityDTO.isToBeAdded())
                            {
                                log.info("Shallow adding level {}", entityDTO.getName());

                                SanchayAnnotationLevel entity = new SanchayAnnotationLevel();

                                try {
                                    SanchayBeanUtils.copyPropertiesNotNull(entity, entityDTO);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }

                                annotationLevelRepo.save(entity);

                                entitiesToAdd.put(entityDTO.getName(), entity);
                            }
                        }
                );

        return entitiesToAdd;
    }

    private LinkedHashMap<String, SanchayUser> shallowAddUsers(SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo)
    {
        log.info("Started shallow saving user");

        Map<String, SanchayUserDTO> allEntitiesDTO = new LinkedHashMap<>(annotationManagementUpdateInfo.getAllUsers());
        LinkedHashMap<String, SanchayUser> entitiesToAdd = new LinkedHashMap<>();

        allEntitiesDTO.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayUserDTO entityDTO = entry.getValue();

                            if(entityDTO.isToBeAdded())
                            {
                                log.info("Shallow adding user {}", entityDTO.getUsername());

                                SanchayUser entity = new SanchayUser();

                                try {
                                    SanchayBeanUtils.copyPropertiesNotNull(entity, entityDTO);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }

                                userRepo.save(entity);

                                entitiesToAdd.put(entityDTO.getUsername(), entity);
                            }
                        }
                );

        return entitiesToAdd;
    }

    @Override
    public SanchayUser saveUser(SanchayUser user, boolean serverSide) {

        if(serverSide || SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user)) {
            log.info("Saving new user {} to the database", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            return userRepo.saveAndFlush(user);
        }

        return null;
    }

    @Override
    public SanchayRole saveRole(SanchayRole role, boolean serverSide) {

        if(serverSide || SanchayServiceUtils.hasPermissionToAddRole(getCurrentUser(), role)) {

            log.info("Saving new role {} to the database", role.getName());
            return roleRepo.saveAndFlush(role);
        }

        return null;
    }

//    @Override
//    public SanchayPrivilege savePrivilege(SanchayPrivilege privilege) {
//        log.info("Saving new privilege {} to the database", privilege.getName());
//        return privilegeRepo.saveAndFlush(privilege);
//    }

    @Override
    public SanchayResourceLanguage saveLanguage(SanchayResourceLanguage language, boolean serverSide) {

        if(serverSide || SanchayServiceUtils.hasPermissionToAddLanguage(getCurrentUser(), language)) {
            log.info("Saving new language {} to the database", language.getName());

            return languageRepo.saveAndFlush(language);
        }

        return null;
    }

    @Override
    public SanchayOrganisation saveOrganisation(SanchayOrganisation organisation, boolean serverSide) {

        if(serverSide || SanchayServiceUtils.hasPermissionToAddOrganisation(getCurrentUser(), organisation)) {
            log.info("Saving new organisation {} to the database", organisation.getName());

            return organisationRepo.saveAndFlush(organisation);
        }

        return null;
    }

    @Override
    public SanchayAnnotationLevel saveAnnotationLevel(SanchayAnnotationLevel annotationLevel, boolean serverSide) {

        if(serverSide || SanchayServiceUtils.hasPermissionToAddAnnotationLevel(getCurrentUser(), annotationLevel)) {

            log.info("Saving new annotation level {} to the database", annotationLevel.getName());
            return annotationLevelRepo.saveAndFlush(annotationLevel);
        }

        return null;
    }

    public SanchayUser deleteUser(SanchayUser user, boolean serverSide)
    {
        if(serverSide || SanchayServiceUtils.hasPermissionToAddUser(getCurrentUser(), user))
        {
            log.info("Deleting user {} from the database", user.getUsername());

            SanchayServiceUtils.safeDeleteUser(user);

            userRepo.delete(user);
        }

        return user;
    }

    public SanchayRole deleteRole(SanchayRole role, boolean serverSide)
    {
        if(serverSide || SanchayServiceUtils.hasPermissionToAddRole(getCurrentUser(), role))
        {
            log.info("Deleting role {} from the database", role.getName());

            SanchayServiceUtils.safeDeleteRole(role);

            roleRepo.delete(role);
        }

        return role;
    }

    public SanchayResourceLanguage deleteLanguage(SanchayResourceLanguage language, boolean serverSide)
    {
        if(serverSide || SanchayServiceUtils.hasPermissionToAddLanguage(getCurrentUser(), language))
        {
            log.info("Deleting language {} from the database", language.getName());

            SanchayServiceUtils.safeDeleteLanguage(language);

            languageRepo.delete(language);
        }

        return language;
    }

    public SanchayOrganisation deleteOrganisation(SanchayOrganisation organisation, boolean serverSide)
    {
        if(serverSide || SanchayServiceUtils.hasPermissionToAddOrganisation(getCurrentUser(), organisation))
        {
            log.info("Deleting organisation {} from the database", organisation.getName());

            SanchayServiceUtils.safeDeleteOrganisation(organisation);

            organisationRepo.delete(organisation);
        }

        return organisation;
    }

    public SanchayAnnotationLevel deletedAnnotationLevel(SanchayAnnotationLevel annotationLevel, boolean serverSide)
    {
        if(serverSide || SanchayServiceUtils.hasPermissionToAddAnnotationLevel(getCurrentUser(), annotationLevel))
        {
            log.info("Deleting organisation {} from the database", annotationLevel.getName());

            SanchayServiceUtils.safeDeleteAnnotationLevel(annotationLevel);

            annotationLevelRepo.delete(annotationLevel);
        }

        return annotationLevel;
    }

//    @Override
//    public void addRoleToUser(String username, String roleName) {
//        log.info("Adding role {} to user {}", roleName, username);
//        SanchayUser user = userRepo.findByUsername(username);
//        SanchayRole role = roleRepo.findByName(roleName);
//
//        if(SanchayServiceUtils.hasPermissionToAddRoleToUser(getCurrentUser(), user, role)) {
//            user.addRole(role);
//        }
//    }
//
//    @Override
//    public void addLanguageToUser(String username, String languageName) {
//        SanchayUser user = userRepo.findByUsername(username);
//        SanchayResourceLanguage language  = languageRepo.findByName(languageName);
//
//        if(SanchayServiceUtils.hasPermissionToAddLanguageToUser(getCurrentUser(), user, language)) {
//            log.info("Adding language {} to user {}", languageName, username);
//            user.addLanguage(language);
//        }
//    }
//
//    @Override
//    public void addOrganisationToUser(String username, String organisationName) {
//
//        if(getCurrentUser().getCurrentRole().getName().equals(SanchayRole.ROOT)) {
//            log.info("Adding organisation {} to user {}", organisationName, username);
//            SanchayUser user = userRepo.findByUsername(username);
//            SanchayOrganisation organisation = organisationRepo.findByName(organisationName);
//            user.addOrganisation(organisation);
//        }
//    }
//
//    public void addAnnotationLevelToUser(String username, String annotationLevelName)
//    {
//        SanchayUser user = userRepo.findByUsername(username);
//        SanchayAnnotationLevel annotationLevel = annotationLevelRepo.findByName(annotationLevelName);
//
//        if(SanchayServiceUtils.hasPermissionToAddAnnotationLevelToUser(getCurrentUser(), user, annotationLevel))
//        {
//            user.addAnnotationLevel(annotationLevel);
//        }
//    }

//    @Override
//    public void addPrivilegeToRole(String roleName, String privilegeName) {
//        log.info("Adding privilege {} to role {}", privilegeName, roleName);
//        SanchayRole role = roleRepo.findByName(roleName);
//        SanchayPrivilege privilege  = privilegeRepo.findByName(privilegeName);
//        role.getPrivileges().add(privilege);
//    }

    private void deepAddUser(SanchayUserDTO userDTO, SanchayUser user)
    {
        log.info("Adding user {}", userDTO.getUsername());

        if(userDTO.getPassword() != null && !userDTO.getPassword().equals("")
                && userDTO.isChangePassword())
        {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        Map<String, SanchayRoleSlimDTO> roleSlimDTOMap = new LinkedHashMap<>(userDTO.getRoles());

        roleSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayRole role = roleRepo.findByName(name);
                    if(role != null) {
                        user.addRole(role);
                    }
                }
        );

        Map<String, SanchayOrganisationSlimDTO> organisationSlimDTOMap = new LinkedHashMap<>(userDTO.getOrganisations());

        organisationSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayOrganisation organisation = organisationRepo.findByName(name);
                    if(organisation != null) {
                        user.addOrganisation(organisation);
                    }
                }
        );

        Map<String, SanchayResourceLanguageSlimDTO> languageSlimDTOMap = new LinkedHashMap<>(userDTO.getLanguages());

        languageSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayResourceLanguage language = languageRepo.findByName(name);
                    if(language != null) {
                        user.addLanguage(language);
                    }
                }
        );

        Map<String, SanchayAnnotationLevelSlimDTO> levelSlimDTOMap = new LinkedHashMap<>(userDTO.getAnnotationLevels());

        levelSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayAnnotationLevel level = annotationLevelRepo.findByName(name);
                    if(level != null) {
                        user.addAnnotationLevel(level);
                    }
                }
        );

//        userRepo.saveAndFlush(user);
        userRepo.save(user);
    }

    private void deepAddRole(SanchayRoleDTO roleDTO, SanchayRole role)
    {
        log.info("Adding role {}", roleDTO.getName());

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = new LinkedHashMap<>(roleDTO.getUsers());

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        role.addUser(user);
                    }
                }
        );

//        roleRepo.saveAndFlush(role);
        roleRepo.save(role);
    }

    private void deepAddOrganisation(SanchayOrganisationDTO organisationDTO, SanchayOrganisation organisation)
    {
        log.info("Adding organisation {}", organisationDTO.getName());

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = new LinkedHashMap<>(organisationDTO.getUsers());

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        organisation.addUser(user);
                    }
                }
        );

//        organisationRepo.saveAndFlush(organisation);
        organisationRepo.save(organisation);
    }

    private void deepAddLanguage(SanchayResourceLanguageDTO resourceLanguageDTO, SanchayResourceLanguage language)
    {
        log.info("Adding language {}", resourceLanguageDTO.getName());

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = new LinkedHashMap<>(resourceLanguageDTO.getUsers());

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        language.addUser(user);
                    }
                }
        );

//        languageRepo.saveAndFlush(language);
        languageRepo.save(language);
    }

    private void deepAddAnnotationLevel(SanchayAnnotationLevelDTO levelDTO, SanchayAnnotationLevel annotationLevel)
    {
        log.info("Adding levels {}", levelDTO.getName());

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = new LinkedHashMap<>(levelDTO.getUsers());

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        annotationLevel.addUser(user);
                    }
                }
        );

//        annotationLevelRepo.saveAndFlush(annotationLevel);
        annotationLevelRepo.save(annotationLevel);
    }

    private void deepDeleteUser(SanchayUserDTO userDTO)
    {
        SanchayUser user = userRepo.findByUsername(userDTO.getUsername());

        log.info("Deleting user {}", userDTO.getUsername());

        SanchayServiceUtils.safeDeleteUser(user);

        userRepo.delete(user);
    }

    private void deepDeleteRole(SanchayRoleDTO roleDTO)
    {
        SanchayRole role = roleRepo.findByName(roleDTO.getName());

        log.info("Deleting role {}", roleDTO.getName());

        SanchayServiceUtils.safeDeleteRole(role);

        roleRepo.delete(role);
    }

    private void deepDeleteOrganisation(SanchayOrganisationDTO organisationDTO)
    {
        SanchayOrganisation organisation = organisationRepo.findByName(organisationDTO.getName());

        log.info("Deleting organisation {}", organisationDTO.getName());

        SanchayServiceUtils.safeDeleteOrganisation(organisation);

        organisationRepo.delete(organisation);
    }

    private void deepDeleteLanguage(SanchayResourceLanguageDTO resourceLanguageDTO)
    {
        SanchayResourceLanguage language = languageRepo.findByName(resourceLanguageDTO.getName());

        log.info("Deleting language {}", resourceLanguageDTO.getName());

        SanchayServiceUtils.safeDeleteLanguage(language);

        languageRepo.delete(language);
    }

    private void deepDeleteAnnotationLevel(SanchayAnnotationLevelDTO levelDTO)
    {
        SanchayAnnotationLevel annotationLevel = annotationLevelRepo.findByName(levelDTO.getName());

        log.info("Deleting level {}", levelDTO.getName());

        SanchayServiceUtils.safeDeleteAnnotationLevel(annotationLevel);

        annotationLevelRepo.delete(annotationLevel);
    }

    private void deepUpdateUser(SanchayUserDTO userDTO)
    {
        SanchayUser user = userRepo.findByUsername(userDTO.getUsername());

        log.info("Updating user {}", userDTO.getUsername());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(user, userDTO);

            if(userDTO.getPassword() != null && !userDTO.getPassword().equals("")
                    && userDTO.isChangePassword())
            {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayRoleSlimDTO> rolesDeleted = new LinkedHashMap<>(userDTO.getRolesDeleted());

        rolesDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayRole role = roleRepo.findByName(name);

                    if(role != null) {
                        user.removeRole(role);
                    }
                }
        );

        Map<String, SanchayOrganisationSlimDTO> organisationsDeleted = new LinkedHashMap<>(userDTO.getOrganisationsDeleted());

        organisationsDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();

                    SanchayOrganisation organisation = organisationRepo.findByName(name);
                    if(organisation != null) {
                        user.removeOrganisation(organisation);
                    }
                }
        );

        Map<String,SanchayResourceLanguageSlimDTO > languagesDeleted = new LinkedHashMap<>(userDTO.getLanguagesDeleted());

        languagesDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();

                    SanchayResourceLanguage language = languageRepo.findByName(name);
                    if(language != null) {
                        user.removeLanguage(language);
                    }
                }
        );

        Map<String, SanchayAnnotationLevelSlimDTO> levelsDeleted = new LinkedHashMap<>(userDTO.getAnnotationLevelsDeleted());

        levelsDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();

                    SanchayAnnotationLevel level = annotationLevelRepo.findByName(name);
                    if(level != null) {
                        user.removeAnnotationLevel(level);
                    }
                }
        );

        Map<String, SanchayRoleSlimDTO> rolesAdded = new LinkedHashMap<>(userDTO.getRolesAdded());

        rolesAdded.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayRole role = roleRepo.findByName(name);

                    if(role != null) {
                        user.addRole(role);
                    }
                }
        );

        Map<String, SanchayOrganisationSlimDTO> organisationsAdded = new LinkedHashMap<>(userDTO.getOrganisationsAdded());

        organisationsAdded.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();

                    SanchayOrganisation organisation = organisationRepo.findByName(name);
                    if(organisation != null) {
                        user.addOrganisation(organisation);
                    }
                }
        );

        Map<String, SanchayResourceLanguageSlimDTO> languagesAdded = new LinkedHashMap<>(userDTO.getLanguagesAdded());

        languagesAdded.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();

                    SanchayResourceLanguage language = languageRepo.findByName(name);

                    if(language != null) {
                        user.addLanguage(language);
                    }
                }
        );

        Map<String, SanchayAnnotationLevelSlimDTO> levelsAdded = new LinkedHashMap<>(userDTO.getAnnotationLevelsAdded());

        levelsAdded.entrySet().stream().forEach(
                (entry) -> {
                    String name = entry.getKey();

                    SanchayAnnotationLevel level = annotationLevelRepo.findByName(name);

                    if(level != null) {
                        user.addAnnotationLevel(level);
                    }
                }
        );

//        userRepo.saveAndFlush(user);
        userRepo.save(user);
    }

    private void deepUpdateRole(SanchayRoleDTO roleDTO)
    {
        SanchayRole role = roleRepo.findByName(roleDTO.getName());

        log.info("Updating role {}", roleDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(role, roleDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> usersDeleted = new LinkedHashMap<>(roleDTO.getUsersDeleted());

        usersDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        role.removeUser(user);
                    }
                }
        );

        Map<String, SanchayUserSlimDTO> usersAdded = new LinkedHashMap<>(roleDTO.getUsersAdded());

        usersAdded.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        role.addUser(user);
                    }
                }
        );

//        roleRepo.saveAndFlush(role);
        roleRepo.save(role);
    }

    private void deepUpdateOrganisation(SanchayOrganisationDTO organisationDTO)
    {
        SanchayOrganisation organisation = organisationRepo.findByName(organisationDTO.getName());

        log.info("Updating organisation {}", organisationDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(organisation, organisationDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> usersDeleted = new LinkedHashMap<>(organisationDTO.getUsersDeleted());

        usersDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        organisation.removeUser(user);
                    }
                }
        );

        Map<String, SanchayUserSlimDTO> usersAdded = new LinkedHashMap<>(organisationDTO.getUsersAdded());

        usersAdded.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        organisation.addUser(user);
                    }
                }
        );

//        organisationRepo.saveAndFlush(organisation);
        organisationRepo.save(organisation);
    }

    private void deepUpdateLanguage(SanchayResourceLanguageDTO resourceLanguageDTO)
    {
        SanchayResourceLanguage language = languageRepo.findByName(resourceLanguageDTO.getName());

        log.info("Updating language {}", resourceLanguageDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(language, resourceLanguageDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> usersDeleted = new LinkedHashMap<>(resourceLanguageDTO.getUsersDeleted());

        usersDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        language.removeUser(user);
                    }
                }
        );

        Map<String, SanchayUserSlimDTO> usersAdded = new LinkedHashMap<>(resourceLanguageDTO.getUsersAdded());

        usersAdded.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        language.addUser(user);
                    }
                }
        );

//        languageRepo.saveAndFlush(language);
        languageRepo.save(language);
    }

    private void deepUpdateAnnotationLevel(SanchayAnnotationLevelDTO levelDTO)
    {
        SanchayAnnotationLevel annotationLevel = annotationLevelRepo.findByName(levelDTO.getName());

        log.info("Updating level {}", levelDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(annotationLevel, levelDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> usersDeleted = new LinkedHashMap<>(levelDTO.getUsersDeleted());

        usersDeleted.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        annotationLevel.removeUser(user);
                    }
                }
        );

        Map<String, SanchayUserSlimDTO> usersAdded = new LinkedHashMap<>(levelDTO.getUsersAdded());

        usersAdded.entrySet().stream().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        annotationLevel.addUser(user);
                    }
                }
        );

//        annotationLevelRepo.saveAndFlush(annotationLevel);
        annotationLevelRepo.save(annotationLevel);
    }

    public SanchayDeepModelMapper getModelMapperInstance(UserRepo userRepo, RoleRepo roleRepo, LanguageRepo languageRepo,
                                                         OrganisationRepo organisationRepo, AnnotationLevelRepo annotationLevelRepo)
    {
        SanchayDeepModelMapper modelMapper = new SanchayDeepModelMapper(userRepo, roleRepo, languageRepo,
                organisationRepo, annotationLevelRepo);

        new SanchayUserSlimDTOToSanchayUserDTOMap(modelMapper);
        new SanchayUserDTOToSanchayUserMap(modelMapper);
        new SanchayUserSlimDTOToSanchayUserMap(modelMapper);

        new SanchayUserSlimDTOToSanchayUserDTOMap(modelMapper);
        new SanchayRoleDTOToSanchayRoleMap(modelMapper);
        new SanchayRoleSlimDTOToSanchayRoleMap(modelMapper);

        new SanchayOrganisationSlimDTOToSanchayOrganisationDTOMap(modelMapper);
        new SanchayOrganisationDTOToSanchayOrganisationMap(modelMapper);
        new SanchayOrganisationSlimDTOToSanchayOrganisationMap(modelMapper);

        new SanchayLanguageSlimDTOToSanchayLanguageDTOMap(modelMapper);
        new SanchayLanguageDTOToSanchayLanguageMap(modelMapper);
        new SanchayLanguageSlimDTOToSanchayLanguageMap(modelMapper);

        new SanchayALevelSlimDTOToSanchayALevelDTOMap(modelMapper);
        new SanchayALevelDTOToSanchayALevelMap(modelMapper);
        new SanchayALevelSlimDTOToSanchayALevelMap(modelMapper);

        return modelMapper;
    }
}
