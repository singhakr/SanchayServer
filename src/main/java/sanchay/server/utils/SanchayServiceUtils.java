package sanchay.server.utils;

import sanchay.server.dao.auth.model.domain.*;
import sanchay.server.repo.AnnotationLevelRepo;
import sanchay.server.repo.LanguageRepo;
import sanchay.server.repo.OrganisationRepo;
import sanchay.server.repo.UserRepo;

import java.util.*;

public class SanchayServiceUtils
{
    public static Map<String, SanchayOrganisation> getOrganisationsForLanguage(SanchayResourceLanguage sanchayResourceLanguage)
    {
        Map<String, SanchayOrganisation> organisations = new LinkedHashMap<>();

        Map<String, SanchayUser> usersForLanguage = sanchayResourceLanguage.getUsers();

        Iterator<String> itr = usersForLanguage.keySet().iterator();

        while (itr.hasNext())
        {
            String username = itr.next();
            SanchayUser user = usersForLanguage.get(username);

            Map<String, SanchayOrganisation> organisationsForUser = user.getOrganisations();

            Iterator<String> innerItr = organisationsForUser.keySet().iterator();

            while (innerItr.hasNext())
            {
                String orgname = innerItr.next();
                SanchayOrganisation organisation = organisationsForUser.get(orgname);

                organisations.put(orgname, organisation);
            }
        }

        return organisations;
    }

    public static Map<String, SanchayAnnotationLevel> getAnnotationLevelsForLanguage(SanchayResourceLanguage sanchayResourceLanguage)
    {
        Map<String, SanchayAnnotationLevel> annotationLevels = new LinkedHashMap<>();

        Map<String, SanchayUser> usersForLanguage = sanchayResourceLanguage.getUsers();

        Iterator<String> itr = usersForLanguage.keySet().iterator();

        while (itr.hasNext())
        {
            String username = itr.next();
            SanchayUser user = usersForLanguage.get(username);

            Map<String, SanchayAnnotationLevel> annotationLevelsForUser = user.getAnnotationLevels();

            Iterator<String> innerItr = annotationLevelsForUser.keySet().iterator();

            while (innerItr.hasNext())
            {
                String alname = innerItr.next();
                SanchayAnnotationLevel annotationLevel = annotationLevelsForUser.get(alname);

                annotationLevels.put(alname, annotationLevel);
            }
        }

        return annotationLevels;
    }

    public static Map<String, SanchayResourceLanguage> getLanguagesForOrganisation(SanchayOrganisation sanchayOrganisation)
    {
        Map<String, SanchayResourceLanguage> languages = new LinkedHashMap<>();

        Map<String, SanchayUser> usersForOrganisation = sanchayOrganisation.getUsers();

        Iterator<String> itr = usersForOrganisation.keySet().iterator();

        while (itr.hasNext())
        {
            String username = itr.next();
            SanchayUser user = usersForOrganisation.get(username);

            Map<String, SanchayResourceLanguage> languagesForUser = user.getLanguages();

            Iterator<String> innerItr = languagesForUser.keySet().iterator();

            while (innerItr.hasNext())
            {
                String language = innerItr.next();
                SanchayResourceLanguage sanchayResourceLanguage = languagesForUser.get(language);

                languages.put(language, sanchayResourceLanguage);
            }
        }

        return languages;
    }

    public static Map<String, SanchayAnnotationLevel> getAnnotationLevelsForOrganisation(SanchayOrganisation sanchayOrganisation)
    {
        Map<String, SanchayAnnotationLevel> annotationLevels = new LinkedHashMap<>();

        Map<String, SanchayUser> usersForOrganisation = sanchayOrganisation.getUsers();

        Iterator<String> itr = usersForOrganisation.keySet().iterator();

        while (itr.hasNext())
        {
            String username = itr.next();
            SanchayUser user = usersForOrganisation.get(username);

            Map<String, SanchayAnnotationLevel> annotationLevelsForUser = user.getAnnotationLevels();

            Iterator<String> innerItr = annotationLevelsForUser.keySet().iterator();

            while (innerItr.hasNext())
            {
                String alname = innerItr.next();
                SanchayAnnotationLevel annotationLevel = annotationLevelsForUser.get(alname);

                annotationLevels.put(alname, annotationLevel);
            }
        }

        return annotationLevels;
    }

    public static Map<String, SanchayResourceLanguage> getLanguagesForAnnotationLevel(SanchayAnnotationLevel annotationLevel)
    {
        Map<String, SanchayResourceLanguage> languages = new LinkedHashMap<>();

        Map<String, SanchayUser> usersForAnnotationLevel = annotationLevel.getUsers();

        Iterator<String> itr = usersForAnnotationLevel.keySet().iterator();

        while (itr.hasNext())
        {
            String username = itr.next();
            SanchayUser user = usersForAnnotationLevel.get(username);

            Map<String, SanchayResourceLanguage> languagesForUser = user.getLanguages();

            Iterator<String> innerItr = languagesForUser.keySet().iterator();

            while (innerItr.hasNext())
            {
                String language = innerItr.next();
                SanchayResourceLanguage sanchayResourceLanguage = languagesForUser.get(language);

                languages.put(language, sanchayResourceLanguage);
            }
        }

        return languages;
    }

    public static Map<String, SanchayOrganisation> getOrganisationsForAnnotationLevel(SanchayAnnotationLevel annotationLevel)
    {
        Map<String, SanchayOrganisation> organisations = new LinkedHashMap<>();

        Map<String, SanchayUser> usersForAnnotationLevel = annotationLevel.getUsers();

        Iterator<String> itr = usersForAnnotationLevel.keySet().iterator();

        while (itr.hasNext())
        {
            String username = itr.next();
            SanchayUser user = usersForAnnotationLevel.get(username);

            Map<String, SanchayOrganisation> organisationsForUser = user.getOrganisations();

            Iterator<String> innerItr = organisationsForUser.keySet().iterator();

            while (innerItr.hasNext())
            {
                String orgname = innerItr.next();
                SanchayOrganisation sanchayOrganisation = organisationsForUser.get(orgname);

                organisations.put(orgname, sanchayOrganisation);
            }
        }

        return organisations;
    }

    private static List<String> getKeysFromList(List list)
    {
        List<String> keyList = new ArrayList<>();

        list.forEach(
                (item) -> {
                    String key = null;

                    if(item instanceof SanchayUser) {
                        key = ((SanchayUser) item).getUsername();
                    }
                    else if(item instanceof SanchayRole) {
                        key = ((SanchayRole) item).getName();
                    }
                    else if(item instanceof SanchayResourceLanguage) {
                        key = ((SanchayResourceLanguage) item).getName();
                    }
                    else if(item instanceof SanchayOrganisation) {
                        key = ((SanchayOrganisation) item).getName();
                    }

                    keyList.add(key);
                }
        );

        return keyList;
    }

    public static String getKey(Object authObject)
    {
        String key = null;

        if(authObject instanceof SanchayUser) {
            key = ((SanchayUser) authObject).getUsername();
        }
        else if(authObject instanceof SanchayRole) {
            key = ((SanchayRole) authObject).getName();
        }
        else if(authObject instanceof SanchayResourceLanguage) {
            key = ((SanchayResourceLanguage) authObject).getName();
        }
        else if(authObject instanceof SanchayOrganisation) {
            key = ((SanchayOrganisation) authObject).getName();
        }
        else if(authObject instanceof SanchayAnnotationLevel) {
            key = ((SanchayAnnotationLevel) authObject).getName();
        }

        return key;
    }

    // Only for authentication and authorisation objects
    public static Map listToMap(List userList)
    {
        Map map = new LinkedHashMap<>();

        userList.forEach(
                (user) -> { map.put(getKey(user), user); }
        );

        return map;
    }

    public static Map<String, SanchayUser> getAllUsers(UserRepo userRepo, SanchayUser user)
    {
        Map<String, SanchayUser> userMap = new LinkedHashMap<>();

        SanchayRole role = user.getCurrentRole();

        if(role.getName().equals(SanchayRole.VIEWER) || role.getName().equals(SanchayRole.ANNOTATOR))
        {
            userMap.put(user.getUsername(), user);
        }
        else if(role.getName().equals(SanchayRole.VALIDATOR))
        {
            SanchayResourceLanguage userLanguage = user.getCurrentLanguage();
            Map<String, SanchayUser> usersForLanguage = userLanguage.getUsers();

            SanchayAnnotationLevel userAnnotationLevels = user.getCurrentAnnotationLevel();
            Map<String, SanchayUser> usersForAnnotationLevel = userAnnotationLevels.getUsers();

            userMap = (Map<String, SanchayUser>) getMapIntersection(usersForLanguage, usersForAnnotationLevel);

            SanchayOrganisation userOrganisation = user.getCurrentOrganisation();
            Map<String, SanchayUser> usersForOrganisation = userOrganisation.getUsers();

            userMap = (Map<String, SanchayUser>) getMapIntersection(userMap, usersForOrganisation);
        }
        else if(role.getName().equals(SanchayRole.MANAGER))
        {
            SanchayOrganisation userOrganisation = user.getCurrentOrganisation();
            Map<String, SanchayUser> usersForOrganisation = userOrganisation.getUsers();

            userMap = usersForOrganisation;
        }
        else if(role.getName().equals(SanchayRole.ROOT))
        {
            userMap = (Map<String, SanchayUser>) listToMap(userRepo.findAll());
        }

        return userMap;
    }

    public static Map<String, SanchayResourceLanguage> getAllLanguages(LanguageRepo languageRepo, SanchayUser user)
    {
        Map<String, SanchayResourceLanguage> languageMap = new LinkedHashMap<>();

        SanchayRole role = user.getCurrentRole();

        if(role.getName().equals(SanchayRole.VIEWER) || role.getName().equals(SanchayRole.ANNOTATOR)
                || role.getName().equals(SanchayRole.VALIDATOR))
        {
            languageMap.putAll(user.getLanguages());
        }
        else if(role.getName().equals(SanchayRole.MANAGER))
        {
            languageMap.putAll(getLanguagesForOrganisation(user.getCurrentOrganisation()));
        }
        else if(role.getName().equals(SanchayRole.ROOT))
        {
            languageMap = (Map<String, SanchayResourceLanguage>) listToMap(languageRepo.findAll());
        }

        return languageMap;
    }

    public static Map<String, SanchayOrganisation> getAllOrganisations(OrganisationRepo organisationRepo, SanchayUser user)
    {
        Map<String, SanchayOrganisation> organisationMap = new LinkedHashMap<>();

        SanchayRole role = user.getCurrentRole();

        if(role.getName().equals(SanchayRole.VIEWER) || role.getName().equals(SanchayRole.ANNOTATOR)
                || role.getName().equals(SanchayRole.VALIDATOR)
                || role.getName().equals(SanchayRole.MANAGER))
        {
            organisationMap.putAll(user.getOrganisations());
        }
        else if(role.getName().equals(SanchayRole.ROOT))
        {
            organisationMap = (Map<String, SanchayOrganisation>) listToMap(organisationRepo.findAll());
        }

        return organisationMap;
    }

    public static Map<String, SanchayAnnotationLevel> getAllAnnotationLevels(AnnotationLevelRepo annotationLevelRepo, SanchayUser user)
    {
        Map<String, SanchayAnnotationLevel> annotationLevelMap = new LinkedHashMap<>();

        SanchayRole role = user.getCurrentRole();

        if(role.getName().equals(SanchayRole.VIEWER) || role.getName().equals(SanchayRole.ANNOTATOR)
                || role.getName().equals(SanchayRole.VALIDATOR))
        {
            annotationLevelMap.putAll(user.getAnnotationLevels());
        }
        else if(role.getName().equals(SanchayRole.MANAGER))
        {
            annotationLevelMap.putAll(getAnnotationLevelsForOrganisation(user.getCurrentOrganisation()));
        }
        else if(role.getName().equals(SanchayRole.ROOT))
        {
            annotationLevelMap = (Map<String, SanchayAnnotationLevel>) listToMap(annotationLevelRepo.findAll());
        }

        return annotationLevelMap;
    }

    public static Map getMapIntersection(Map map1, Map map2)
    {
        Map<String, Object> mapIntersection = new LinkedHashMap<>();

        Iterator<String> itr = map1.keySet().iterator();

        while (itr.hasNext()) {
            String key1 = itr.next();
            Object value1 = map1.get(key1);

            if(map2.containsKey(key1))
            {
                mapIntersection.put(key1, value1);
            }
        }

        return mapIntersection;
    }

    public static boolean hasPermissionToAddUser(SanchayUser currentUser, SanchayUser userToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        if(role.getName().equals(SanchayRole.MANAGER)
                && currentUser.getCurrentOrganisation().getName().equals(userToAdd.getCurrentOrganisation().getName()))
            return true;

        return false;
    }

    public static boolean hasPermissionToAddRole(SanchayUser currentUser, SanchayRole roleToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        if(role.getName().equals(SanchayRole.MANAGER)
                && !roleToAdd.getName().equals(SanchayRole.ROOT))
            return true;

        return false;
    }

    public static boolean hasPermissionToAddRoleToUser(SanchayUser currentUser, SanchayUser user, SanchayRole roleToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        if(role.getName().equals(SanchayRole.MANAGER)
                && !roleToAdd.getName().equals(SanchayRole.ROOT)
                && currentUser.getCurrentOrganisation().getName().equals(user.getCurrentOrganisation().getName()))
            return true;

        return false;
    }

    public static boolean hasPermissionToAddLanguageToUser(SanchayUser currentUser, SanchayUser user, SanchayResourceLanguage languageToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        if(role.getName().equals(SanchayRole.MANAGER)
                && currentUser.getCurrentOrganisation().getName().equals(user.getCurrentOrganisation().getName())
                && getLanguagesForOrganisation(currentUser.getCurrentOrganisation()).containsKey(languageToAdd.getName()))
            return true;

        return false;
    }
    public static boolean hasPermissionToAddAnnotationLevelToUser(SanchayUser currentUser, SanchayUser user, SanchayAnnotationLevel levelToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        if(role.getName().equals(SanchayRole.MANAGER)
                && currentUser.getCurrentOrganisation().getName().equals(user.getCurrentOrganisation().getName())
                && getAnnotationLevelsForOrganisation(currentUser.getCurrentOrganisation()).containsKey(levelToAdd.getName()))
            return true;

        return false;
    }

    public static boolean hasPermissionToAddLanguage(SanchayUser currentUser, SanchayResourceLanguage languageToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        return false;
    }

    public static boolean hasPermissionToAddOrganisation(SanchayUser currentUser, SanchayOrganisation organisationToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        return false;
    }

    public static boolean hasPermissionToAddAnnotationLevel(SanchayUser currentUser, SanchayAnnotationLevel levelToAdd)
    {
        SanchayRole role = currentUser.getCurrentRole();

        if(role.getName().equals(SanchayRole.ROOT))
            return true;

        return false;
    }

    public static SanchayUser safeDeleteUser(SanchayUser user)
    {
        Map<String, SanchayRole> roleMap = new LinkedHashMap<>(user.getRoles());
        Map<String, SanchayResourceLanguage> languageMap = new LinkedHashMap<>(user.getLanguages());
        Map<String, SanchayOrganisation> organisationMap = new LinkedHashMap<>(user.getOrganisations());
        Map<String, SanchayAnnotationLevel> annotationLevelMap = new LinkedHashMap<>(user.getAnnotationLevels());

        for(Map.Entry <String, SanchayRole> entry: roleMap.entrySet())
        {
            SanchayRole role = user.getRoles().get(entry.getKey());

            role.removeUser(user);
//            entry.getValue().removeUser(user);
        }

        for(Map.Entry <String, SanchayResourceLanguage> entry: languageMap.entrySet())
        {
            SanchayResourceLanguage language = entry.getValue();

            user.getLanguages().get(language.getName());
//            entry.getValue().removeUser(user);
        }

        for(Map.Entry <String, SanchayOrganisation> entry: organisationMap.entrySet())
        {
            SanchayOrganisation organisation = entry.getValue();

            user.getOrganisations().get(organisation.getName());
//            entry.getValue().removeUser(user);
        }

        for(Map.Entry <String, SanchayAnnotationLevel> entry: annotationLevelMap.entrySet())
        {
            SanchayAnnotationLevel level = entry.getValue();

            user.getAnnotationLevels().get(level.getName());
//            entry.getValue().removeUser(user);
        }

        return user;
    }

    public static SanchayRole safeDeleteRole(SanchayRole role)
    {
        Map<String, SanchayUser> userMap = new LinkedHashMap<>(role.getUsers());

        for(Map.Entry <String, SanchayUser> entry: userMap.entrySet())
        {
            SanchayUser user = entry.getValue();

//            user.getRoles().get(user.getName());
//            entry.getValue().removeRole(user);
        }

        return role;
    }

    public static SanchayResourceLanguage safeDeleteLanguage(SanchayResourceLanguage language)
    {
        Map<String, SanchayUser> userMap = language.getUsers();

        for(Map.Entry <String, SanchayUser> entry: userMap.entrySet())
        {
            entry.getValue().removeLanguage(language);
        }

        return language;
    }

    public static SanchayOrganisation safeDeleteOrganisation(SanchayOrganisation organisation)
    {
        Map<String, SanchayUser> userMap = organisation.getUsers();

        for(Map.Entry <String, SanchayUser> entry: userMap.entrySet())
        {
            entry.getValue().removeOrganisation(organisation);
        }

        return organisation;
    }

    public static SanchayAnnotationLevel safeDeleteAnnotationLevel(SanchayAnnotationLevel annotationLevel)
    {
        Map<String, SanchayUser> userMap = annotationLevel.getUsers();

        for(Map.Entry <String, SanchayUser> entry: userMap.entrySet())
        {
            entry.getValue().removeAnnotationLevel(annotationLevel);
        }

        return annotationLevel;
    }
}
