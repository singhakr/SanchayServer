package in.co.sanchay.server.service;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import in.co.sanchay.server.dao.auth.model.domain.*;
import in.co.sanchay.server.dto.auth.model.domain.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SanchayUserService {
//    SanchayDeepModelMapper getDeepModelMapper();
//    SanchayModelMapper getModelMapper();
    boolean isDatabaseEmtpy();
    void clearDatabase();
    ModelMapper getModelMapper();
    ObjectMapper getPlainObjectMapper();
//    ObjectMapper getPolymorphicObjectMapper();
    SanchayUserDTO getUserDTO(String username, boolean serverSide);
    SanchayUser getUser(String username, boolean serverSide);
    Boolean doesUserExist(String username);
    Boolean doesEmailExist(String email);
    Map<String, SanchayUserDTO> getAllUsers(boolean serverSide);
    Map<String, SanchayRoleDTO> getAllRoles();
    Map<String, SanchayResourceLanguageDTO> getAllLanguages();
    Map<String, SanchayOrganisationDTO> getAllOrganisations();
    Map<String, SanchayAnnotationLevelDTO> getAllAnnotationLevels();
    Map<String, SanchayRole> getUserRoles(String username);
    Map<String, SanchayRoleDTO> getUserRolesDTO(String username);
    Map<String, SanchayResourceLanguageDTO> getUserLanguages(String username);
    Map<String, SanchayOrganisationDTO> getUserOrganisations(String username);
    Map<String, SanchayAnnotationLevelDTO> getUserAnnotationLevels(String username);
    Map<String, SanchayUserDTO> getUsersForRole(String rolename);
    Map<String, SanchayUserDTO> getUsersForLanguage(String languageName);
    Map<String, SanchayUserDTO> getUsersForOrganisation(String organisationName);
    Map<String, SanchayUserDTO> getUsersForAnnotationLevel(String annotationLevelName);
    Map<String, SanchayOrganisationDTO> getOrganisationsForLanguage(String languageName);
    Map<String, SanchayAnnotationLevelDTO> getAnnotationLevelsForLanguage(String languageName);
    Map<String, SanchayResourceLanguageDTO> getLanguagesForOrganisation(String organisationName);
    Map<String, SanchayAnnotationLevelDTO> getAnnotationLevelsForOrganisation(String organisationName);
    Map<String, SanchayOrganisationDTO> getOrganisationsForAnnotationLevels(String levelName);
    Map<String, SanchayResourceLanguageDTO> getLanguagesForAnnotationLevel(String levelName);
    SanchayAnnotationManagementUpdateInfo getAnnotationManagementUpdateInfo();
    SanchayAnnotationManagementUpdateInfo saveAnnotationManagementUpdateInfo(SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo);
    SanchayUser saveUser(SanchayUser user, boolean serverSide);
    SanchayRole saveRole(SanchayRole role, boolean serverSide);
//    SanchayPrivilege savePrivilege(SanchayPrivilege privilege);
    SanchayResourceLanguage saveLanguage(SanchayResourceLanguage language, boolean serverSide);
    SanchayOrganisation saveOrganisation(SanchayOrganisation organisation, boolean serverSide);
    SanchayAnnotationLevel saveAnnotationLevel(SanchayAnnotationLevel annotationLevel, boolean serverSide);
    SanchayUser deleteUser(SanchayUser user, boolean serverSide);
    SanchayRole deleteRole(SanchayRole role, boolean serverSide);
    //    SanchayPrivilege savePrivilege(SanchayPrivilege privilege);
    SanchayResourceLanguage deleteLanguage(SanchayResourceLanguage language, boolean serverSide);
    SanchayOrganisation deleteOrganisation(SanchayOrganisation organisation, boolean serverSide);
    SanchayAnnotationLevel deletedAnnotationLevel(SanchayAnnotationLevel annotationLevel, boolean serverSide);
//    void addRoleToUser(String username, String roleName);
//    void addLanguageToUser(String username, String languageName);
//    void addOrganisationToUser(String username, String organisationName);
//    void addAnnotationLevelToUser(String username, String annotationLevelName);
}
