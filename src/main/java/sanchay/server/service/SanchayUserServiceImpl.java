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

//    @Autowired
//    private ObjectMapper plainObjectMapper;

    @Autowired
    private ObjectMapper polymorphicObjectMapper;

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

//    public ObjectMapper getPlainObjectMapper()
////    public SanchayModelMapper getModelMapper()
//    {
//        return plainObjectMapper;
//    }

    public ObjectMapper getPolymorphicObjectMapper()
//    public SanchayModelMapper getModelMapper()
    {
        return polymorphicObjectMapper;
    }

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
        Map<String, SanchayUserDTO> allUsers = annotationManagementUpdateInfo.getAllUsers();

        allUsers.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String username = entry.getKey();
                            SanchayUserDTO userDTO = entry.getValue();

                            if(userDTO.isToBeAdded())
                            {
                                deepAddUser(userDTO);
                            }
                            else if(userDTO.isToBeDeleted())
                            {
                                deepDeleteUser(userDTO);
                            }
                            else if(userDTO.isDirty())
                            {
                                deepUpdateUser(userDTO);
                            }
                        }
                );

        Map<String, SanchayRoleDTO> allRoles = annotationManagementUpdateInfo.getAllRoles();

        allRoles.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayRoleDTO roleDTO = entry.getValue();

                            if(roleDTO.isToBeAdded())
                            {
                                deepAddRole(roleDTO);
                            }
                            else if(roleDTO.isToBeDeleted())
                            {
                                deepDeleteRole(roleDTO);
                            }
                            else if(roleDTO.isDirty())
                            {
                                deepUpdateRole(roleDTO);
                            }
                        }
                );

        Map<String, SanchayOrganisationDTO> allOrganisations = annotationManagementUpdateInfo.getAllOrganisations();

        allOrganisations.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayOrganisationDTO organisationDTO = entry.getValue();

                            if(organisationDTO.isToBeAdded())
                            {
                                deepAddOrganisation(organisationDTO);
                            }
                            else if(organisationDTO.isToBeDeleted())
                            {
                                deepDeleteOrganisation(organisationDTO);
                            }
                            else if(organisationDTO.isDirty())
                            {
                                deepUpdateOrganisation(organisationDTO);
                            }
                        }
                );

        Map<String, SanchayResourceLanguageDTO> allLanguages = annotationManagementUpdateInfo.getAllLanguages();

        allLanguages.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayResourceLanguageDTO languageDTO = entry.getValue();

                            if(languageDTO.isToBeAdded())
                            {
                                deepAddLanguage(languageDTO);
                            }
                            else if(languageDTO.isToBeDeleted())
                            {
                                deepDeleteLanguage(languageDTO);
                            }
                            else if(languageDTO.isDirty())
                            {
                                deepUpdateLanguage(languageDTO);
                            }
                        }
                );

        Map<String, SanchayAnnotationLevelDTO> allLevels = annotationManagementUpdateInfo.getAllLevels();

        allLevels.entrySet()
                .forEach(
                        (entry) ->
                        {
                            String name = entry.getKey();
                            SanchayAnnotationLevelDTO levelDTO = entry.getValue();

                            if(levelDTO.isToBeAdded())
                            {
                                deepAddAnnotationLevel(levelDTO);
                            }
                            else if(levelDTO.isToBeDeleted())
                            {
                                deepDeleteAnnotationLevel(levelDTO);
                            }
                            else if(levelDTO.isDirty())
                            {
                                deepUpdateAnnotationLevel(levelDTO);
                            }
                        }
                );

        return annotationManagementUpdateInfo;
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

    private void deepAddUser(SanchayUserDTO userDTO)
    {
        SanchayUser user = new SanchayUser();

        try {
            SanchayBeanUtils.copyPropertiesNotNull(user, userDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayRoleSlimDTO> roleSlimDTOMap = userDTO.getRoles();

        roleSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayRole role = roleRepo.findByName(name);
                    if(role != null) {
                        user.addRole(role);
                    }
                }
        );

        Map<String, SanchayOrganisationSlimDTO> organisationSlimDTOMap = userDTO.getOrganisations();

        organisationSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayOrganisation organisation = organisationRepo.findByName(name);
                    if(organisation != null) {
                        user.addOrganisation(organisation);
                    }
                }
        );

        Map<String, SanchayResourceLanguageSlimDTO> languageSlimDTOMap = userDTO.getLanguages();

        languageSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayResourceLanguage language = languageRepo.findByName(name);
                    if(language != null) {
                        user.addLanguage(language);
                    }
                }
        );

        Map<String, SanchayAnnotationLevelSlimDTO> levelSlimDTOMap = userDTO.getAnnotationLevels();

        levelSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String name = entry.getKey();
                    SanchayAnnotationLevel level = annotationLevelRepo.findByName(name);
                    if(level != null) {
                        user.addAnnotationLevel(level);
                    }
                }
        );

        userRepo.saveAndFlush(user);
    }

    private void deepAddRole(SanchayRoleDTO roleDTO)
    {
        SanchayRole role = new SanchayRole();

        try {
            SanchayBeanUtils.copyPropertiesNotNull(role, roleDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = roleDTO.getUsers();

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        role.addUser(user);
                    }
                }
        );

        roleRepo.saveAndFlush(role);
    }

    private void deepAddOrganisation(SanchayOrganisationDTO organisationDTO)
    {
        SanchayOrganisation organisation = new SanchayOrganisation();

        try {
            SanchayBeanUtils.copyPropertiesNotNull(organisation, organisationDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = organisationDTO.getUsers();

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        organisation.addUser(user);
                    }
                }
        );

        organisationRepo.saveAndFlush(organisation);
    }

    private void deepAddLanguage(SanchayResourceLanguageDTO resourceLanguageDTO)
    {
        SanchayResourceLanguage language = new SanchayResourceLanguage();

        try {
            SanchayBeanUtils.copyPropertiesNotNull(language, resourceLanguageDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = resourceLanguageDTO.getUsers();

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        language.addUser(user);
                    }
                }
        );

        languageRepo.saveAndFlush(language);
    }

    private void deepAddAnnotationLevel(SanchayAnnotationLevelDTO levelDTO)
    {
        SanchayAnnotationLevel annotationLevel = new SanchayAnnotationLevel();

        try {
            SanchayBeanUtils.copyPropertiesNotNull(annotationLevel, levelDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, SanchayUserSlimDTO> userSlimDTOMap = levelDTO.getUsers();

        userSlimDTOMap.entrySet().forEach(
                (entry) -> {
                    String username = entry.getKey();
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        annotationLevel.addUser(user);
                    }
                }
        );

        annotationLevelRepo.saveAndFlush(annotationLevel);
    }

    private void deepDeleteUser(SanchayUserDTO userDTO)
    {
        SanchayUser user = userRepo.findByUsername(userDTO.getUsername());

//        SanchayServiceUtils.safeDeleteUser(user);

        userRepo.delete(user);
    }

    private void deepDeleteRole(SanchayRoleDTO roleDTO)
    {
        SanchayRole role = roleRepo.findByName(roleDTO.getName());

//        SanchayServiceUtils.safeDeleteRole(role);

        roleRepo.delete(role);
    }

    private void deepDeleteOrganisation(SanchayOrganisationDTO organisationDTO)
    {
        SanchayOrganisation organisation = organisationRepo.findByName(organisationDTO.getName());

//        SanchayServiceUtils.safeDeleteOrganisation(organisation);

        organisationRepo.delete(organisation);
    }

    private void deepDeleteLanguage(SanchayResourceLanguageDTO resourceLanguageDTO)
    {
        SanchayResourceLanguage language = languageRepo.findByName(resourceLanguageDTO.getName());

//        SanchayServiceUtils.safeDeleteLanguage(language);

        languageRepo.delete(language);
    }

    private void deepDeleteAnnotationLevel(SanchayAnnotationLevelDTO levelDTO)
    {
        SanchayAnnotationLevel annotationLevel = annotationLevelRepo.findByName(levelDTO.getName());

//        SanchayServiceUtils.safeDeleteAnnotationLevel(annotationLevel);

        annotationLevelRepo.delete(annotationLevel);
    }

    private void deepUpdateUser(SanchayUserDTO userDTO)
    {
        SanchayUser user = userRepo.findByUsername(userDTO.getUsername());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(user, userDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Set<String> rolesDeleted = userDTO.getRolesDeleted();

        rolesDeleted.stream().forEach(
                (entry) -> {
                    String name = entry;
                    SanchayRole role = roleRepo.findByName(name);

                    if(role != null) {
                        user.removeRole(role);
                    }
                }
        );

        Set<String> organisationsDeleted = userDTO.getOrganisationsDeleted();

        organisationsDeleted.stream().forEach(
                (entry) -> {
                    String name = entry;

                    SanchayOrganisation organisation = organisationRepo.findByName(name);
                    if(organisation != null) {
                        user.removeOrganisation(organisation);
                    }
                }
        );

        Set<String> languagesDeleted = userDTO.getLanguagesDeleted();

        languagesDeleted.stream().forEach(
                (entry) -> {
                    String name = entry;

                    SanchayResourceLanguage language = languageRepo.findByName(name);
                    if(language != null) {
                        user.removeLanguage(language);
                    }
                }
        );

        Set<String> levelsDeleted = userDTO.getAnnotationLevelsDeleted();

        levelsDeleted.stream().forEach(
                (entry) -> {
                    String name = entry;

                    SanchayAnnotationLevel level = annotationLevelRepo.findByName(name);
                    if(level != null) {
                        user.removeAnnotationLevel(level);
                    }
                }
        );

        Set<String> rolesAdded = userDTO.getRolesAdded();

        rolesAdded.stream().forEach(
                (entry) -> {
                    String name = entry;
                    SanchayRole role = roleRepo.findByName(name);

                    if(role != null) {
                        user.addRole(role);
                    }
                }
        );

        Set<String> organisationsAdded = userDTO.getOrganisationsAdded();

        organisationsAdded.stream().forEach(
                (entry) -> {
                    String name = entry;

                    SanchayOrganisation organisation = organisationRepo.findByName(name);
                    if(organisation != null) {
                        user.addOrganisation(organisation);
                    }
                }
        );

        Set<String> languagesAdded = userDTO.getLanguagesAdded();

        languagesAdded.stream().forEach(
                (entry) -> {
                    String name = entry;

                    SanchayResourceLanguage language = languageRepo.findByName(name);

                    if(language != null) {
                        user.addLanguage(language);
                    }
                }
        );

        Set<String> levelsAdded = userDTO.getAnnotationLevelsAdded();

        levelsAdded.stream().forEach(
                (entry) -> {
                    String name = entry;

                    SanchayAnnotationLevel level = annotationLevelRepo.findByName(name);

                    if(level != null) {
                        user.addAnnotationLevel(level);
                    }
                }
        );

        userRepo.saveAndFlush(user);
    }

    private void deepUpdateRole(SanchayRoleDTO roleDTO)
    {
        SanchayRole role = roleRepo.findByName(roleDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(role, roleDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Set<String> usersDeleted = roleDTO.getUsersDeleted();

        usersDeleted.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        role.removeUser(user);
                    }
                }
        );

        Set<String> usersAdded = roleDTO.getUsersAdded();

        usersAdded.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        role.addUser(user);
                    }
                }
        );

        roleRepo.saveAndFlush(role);
    }

    private void deepUpdateOrganisation(SanchayOrganisationDTO organisationDTO)
    {
        SanchayOrganisation organisation = organisationRepo.findByName(organisationDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(organisation, organisationDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Set<String> usersDeleted = organisationDTO.getUsersDeleted();

        usersDeleted.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        organisation.removeUser(user);
                    }
                }
        );

        Set<String> usersAdded = organisationDTO.getUsersAdded();

        usersAdded.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        organisation.addUser(user);
                    }
                }
        );

        organisationRepo.saveAndFlush(organisation);
    }

    private void deepUpdateLanguage(SanchayResourceLanguageDTO resourceLanguageDTO)
    {
        SanchayResourceLanguage language = languageRepo.findByName(resourceLanguageDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(language, resourceLanguageDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Set<String> usersDeleted = resourceLanguageDTO.getUsersDeleted();

        usersDeleted.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        language.removeUser(user);
                    }
                }
        );

        Set<String> usersAdded = resourceLanguageDTO.getUsersAdded();

        usersAdded.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        language.addUser(user);
                    }
                }
        );

        languageRepo.saveAndFlush(language);
    }

    private void deepUpdateAnnotationLevel(SanchayAnnotationLevelDTO levelDTO)
    {
        SanchayAnnotationLevel annotationLevel = annotationLevelRepo.findByName(levelDTO.getName());

        try {
            SanchayBeanUtils.copyPropertiesNotNull(annotationLevel, levelDTO);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Set<String> usersDeleted = levelDTO.getUsersDeleted();

        usersDeleted.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        annotationLevel.removeUser(user);
                    }
                }
        );

        Set<String> usersAdded = levelDTO.getUsersAdded();

        usersAdded.stream().forEach(
                (entry) -> {
                    String username = entry;
                    SanchayUser user = userRepo.findByUsername(username);
                    if(user != null) {
                        annotationLevel.addUser(user);
                    }
                }
        );

        annotationLevelRepo.saveAndFlush(annotationLevel);
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
