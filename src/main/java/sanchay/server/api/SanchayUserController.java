package sanchay.server.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sanchay.server.dao.auth.model.domain.*;
import sanchay.server.dto.auth.model.domain.*;
import sanchay.server.security.SachayServerSecretKeyManager;
import sanchay.server.service.SanchayUserService;
import sanchay.server.utils.SanchaySecurityUtils;
import sanchay.server.utils.SanchayServerUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class SanchayUserController {


    //    private final MapStructMapper mapstructMapper;;
    private final SanchayUserService userService;

    private static SachayServerSecretKeyManager sachayServerSecretKeyManager = SanchaySecurityUtils.getSachayServerSecretKeyManagerInstace();

//    @Autowired
//    public SanchayUserController(ModelMapper modelMapper) {
//        this.modelMapper = modelMapper;
//        this.modelMapper.addMappings(warehouseFieldMapping);
//        this.modelMapper.addMappings(warehouseMapping);
//    }

    @PostMapping("/current-user")
    public ResponseEntity<SanchayUserDTO> getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
//    public ResponseEntity<String> getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        SanchayUser currentUser = userService.getUser(username, true);

        ObjectMapper mapper = new ObjectMapper();

        try {
            log.info("Current User:");
            log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(currentUser));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        SanchayUserDTO currentUserDTO = userService.getModelMapper().map(currentUser, SanchayUserDTO.class);

        try {
            log.info("Current UserDTO:");
            log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(currentUserDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body(currentUserDTO);


//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(currentUserDTO));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }

    }

    @PostMapping("/user")
    public ResponseEntity<SanchayUser> getUser(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok().body(userService.getUser(request.getParameter("username"), false));
    }

    @PostMapping("/doesUserExist")
    public ResponseEntity<Boolean> doesUserExist(HttpServletRequest request, HttpServletResponse response)
    {
        return ResponseEntity.ok().body(userService.doesUserExist(request.getParameter("username")));
    }

    @PostMapping("/doesEmailExist")
    public ResponseEntity<Boolean> doesEmailExist(HttpServletRequest request, HttpServletResponse response)
    {
        return ResponseEntity.ok().body(userService.doesUserExist(request.getParameter("email")));
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, SanchayUserDTO>> getAllUsers()
//    public ResponseEntity<String> getAllUsers()
//    public ResponseEntity<Map<String, SanchayUser>> getAllUsers()
    {
//        ObjectMapper mapper = new ObjectMapper();

        Map<String, SanchayUserDTO> userDTOMap = userService.getAllUsers(false);

        return ResponseEntity.ok().body(userDTOMap);

//        try {
//            log.info(mapper.writeValueAsString(userMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(userMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping("/roles")
    public ResponseEntity<Map<String, SanchayRoleDTO>> getAllRoles() {
//    public ResponseEntity<String> getAllRoles() {
//        ObjectMapper mapper = new ObjectMapper();

        Map<String, SanchayRoleDTO> roleDTOMap = userService.getAllRoles();

        return ResponseEntity.ok().body(roleDTOMap);

//        try {
//            log.info(mapper.writeValueAsString(roleMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(roleMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().body(userService.getAllRoles());
    }

    @PostMapping("/languages")
    public ResponseEntity<Map<String, SanchayResourceLanguageDTO>> getAllLanguages() {
//    public ResponseEntity<String> getAllLanguages() {
//        ObjectMapper mapper = new ObjectMapper();

        Map<String, SanchayResourceLanguageDTO> languageDTOMap = userService.getAllLanguages();

        return ResponseEntity.ok().body(languageDTOMap);

//        try {
//            log.info(mapper.writeValueAsString(roleMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(roleMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().body(userService.getAllLanguages());
    }

    @PostMapping("/organisations")
    public ResponseEntity<Map<String, SanchayOrganisationDTO>> getAllOrganisations()
    {
//    public ResponseEntity<String> getAllOrganisations() {
//        ObjectMapper mapper = new ObjectMapper();

        Map<String, SanchayOrganisationDTO> organisationDTOMap = userService.getAllOrganisations();

        return ResponseEntity.ok().body(organisationDTOMap);

//        try {
//            log.info(mapper.writeValueAsString(organisationMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(organisationMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        finally {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok().body(userService.getAllOrganisations());
    }

    @PostMapping("/annotation-levels")
    public ResponseEntity<Map<String, SanchayAnnotationLevelDTO>> getAllAnnotationLevels() {
//    public ResponseEntity<String> getAllAnnotationLevels() {
//        ObjectMapper mapper = new ObjectMapper();

        Map<String, SanchayAnnotationLevelDTO> annotationLevelDTOMap = userService.getAllAnnotationLevels();

        return ResponseEntity.ok().body(annotationLevelDTOMap);

//        try {
//            log.info(mapper.writeValueAsString(organisationMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(organisationMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().body(userService.getAllAnnotationLevels());
    }

    @PostMapping("/user/roles")
    public ResponseEntity<Map<String, SanchayRoleDTO>> getUserRoles(@RequestBody String username)
//    public ResponseEntity<String> getUserRoles(@RequestBody String username)
//    public ResponseEntity<String> getUserRoles(@RequestBody RoleToUserForm roleToUserForm)
    {
        Map<String, SanchayRoleDTO> roleDTOMap = userService.getUserRolesDTO(username);

        return ResponseEntity.ok().body(roleDTOMap);

//        ObjectMapper mapper = new ObjectMapper();

//        try {
//            log.info(mapper.writeValueAsString(roleMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(roleMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }

//        return ResponseEntity.ok().body(role);
    }

    @PostMapping("/user/languages")
//    public ResponseEntity<String> getUserLanguages(@RequestBody LanguageToUserForm languageToUserForm)
    public ResponseEntity<Map<String, SanchayResourceLanguageDTO>> getUserLanguages(@RequestBody String username) {
//    public ResponseEntity<String> getUserLanguages(@RequestBody String username) {
        Map<String, SanchayResourceLanguageDTO> languageDTOMap = userService.getUserLanguages(username);

        return ResponseEntity.ok().body(languageDTOMap);

//        ObjectMapper mapper = new ObjectMapper();

//        try {
//            log.info(mapper.writeValueAsString(languageMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(languageMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().body(userService.getUserLanguages(username));
    }

    @PostMapping("/user/organisations")
    public ResponseEntity<Map<String, SanchayOrganisationDTO>> getUserOrganisations(@RequestBody String username)
//    public ResponseEntity<String> getUserOrganisations(@RequestBody String username)
//    public ResponseEntity<String> getUserOrganisations(@RequestBody OrganisationToUserForm organisationToUserForm)
    {
        Map<String, SanchayOrganisationDTO> organisationDTOMap = userService.getUserOrganisations(username);

        return ResponseEntity.ok().body(organisationDTOMap);

//        ObjectMapper mapper = new ObjectMapper();

//        try {
//            log.info(mapper.writeValueAsString(organisationMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(organisationMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().body(userService.getUserOrganisations(username));
    }

    @PostMapping("/user/annotation-levels")
    public ResponseEntity<Map<String, SanchayAnnotationLevelDTO>> getUserAnnotationLevels(@RequestBody String username)
//    public ResponseEntity<String> getUserAnnotationLevels(@RequestBody String username)
//    public ResponseEntity<String> getUserAnnotationLevels(@RequestBody AnnotationLevelToUserForm annotationLevelToUserForm)
    {
        Map<String, SanchayAnnotationLevelDTO> annotationLevelDTOMap = userService.getUserAnnotationLevels(username);

        return ResponseEntity.ok().body(annotationLevelDTOMap);

//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            log.info(mapper.writeValueAsString(annotationLevelMap));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            return ResponseEntity.ok().body(mapper.writeValueAsString(annotationLevelMap));
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.notFound().build();
////            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().body(userService.getUserAnnotationLevels(username));
    }

    @PostMapping("/role/users")
    public ResponseEntity<Map<String, SanchayUserDTO>> getUsersForRole(@RequestBody String rolename) {
        return ResponseEntity.ok().body(userService.getUsersForRole(rolename));
    }

    @PostMapping("/language/users")
    public ResponseEntity<Map<String, SanchayUserDTO>> getUsersForLanguage(@RequestBody String languageName) {
        return ResponseEntity.ok().body(userService.getUsersForLanguage(languageName));
    }

    @PostMapping("/organisation/users")
    public ResponseEntity<Map<String, SanchayUserDTO>> getUsersForOrganisation(@RequestBody String organisationName) {
        return ResponseEntity.ok().body(userService.getUsersForOrganisation(organisationName));
    }

    @PostMapping("/annotation-levels/users")
    public ResponseEntity<Map<String, SanchayUserDTO>> getUsersForAnnotationLevel(@RequestBody String levelName) {
        return ResponseEntity.ok().body(userService.getUsersForAnnotationLevel(levelName));
    }

    @PostMapping("/language/organisations")
    public ResponseEntity<Map<String, SanchayOrganisationDTO>> getOrganisationsForLanguage(@RequestBody String languageName) {
        return ResponseEntity.ok().body(userService.getOrganisationsForLanguage(languageName));
    }

    @PostMapping("/language/annotation-levels")
    public ResponseEntity<Map<String, SanchayAnnotationLevelDTO>> getAnnotationLevelsForLanguage(@RequestBody String languageName) {
        return ResponseEntity.ok().body(userService.getAnnotationLevelsForLanguage(languageName));
    }

    @PostMapping("/organisation/languages")
    public ResponseEntity<Map<String, SanchayResourceLanguageDTO>> getLanguagesForOrganisation(@RequestBody String organisationName) {
        return ResponseEntity.ok().body(userService.getLanguagesForOrganisation(organisationName));
    }

    @PostMapping("/organisation/annotation-levels")
    public ResponseEntity<Map<String, SanchayAnnotationLevelDTO>> getAnnotationLevelsForOrganisation(@RequestBody String organisationName) {
        return ResponseEntity.ok().body(userService.getAnnotationLevelsForOrganisation(organisationName));
    }

    @PostMapping("/annotation-level/organisations")
    public ResponseEntity<Map<String, SanchayOrganisationDTO>> getOrganisationsForAnnotationLevel(@RequestBody String levelName) {
        return ResponseEntity.ok().body(userService.getOrganisationsForAnnotationLevels(levelName));
    }

    @PostMapping("/annotation-level/languages")
    public ResponseEntity<Map<String, SanchayResourceLanguageDTO>> getLanguagesForAnnotationLevel(@RequestBody String levelName) {
        return ResponseEntity.ok().body(userService.getLanguagesForAnnotationLevel(levelName));
    }

    @PostMapping("/annotation-management-info")
//    public ResponseEntity<String> getSanchayAnnotationManagementUpdateInfo()
    public ResponseEntity<SanchayAnnotationManagementUpdateInfo> getSanchayAnnotationManagementUpdateInfo()
    {
        SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo = userService.getAnnotationManagementUpdateInfo();

        String annotationManagementUpdateInfoString = null;

        try {
            annotationManagementUpdateInfoString = userService.getPlainObjectMapper().writeValueAsString(annotationManagementUpdateInfo);
//            annotationManagementUpdateInfoString = userService.getPolymorphicObjectMapper().writeValueAsString(annotationManagementUpdateInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.info("Serialized annotation management info: \n");
        log.info(annotationManagementUpdateInfoString);

//        return ResponseEntity.ok().body(annotationManagementUpdateInfoString);
        return ResponseEntity.ok().body(annotationManagementUpdateInfo);
    }

    @PostMapping("/annotation-management-info/save")
//    public ResponseEntity<String> saveSanchayAnnotationManagementUpdateInfo(@RequestBody String annotationManagementUpdateInfoString)
    public ResponseEntity<SanchayAnnotationManagementUpdateInfo> saveSanchayAnnotationManagementUpdateInfo(@RequestBody SanchayAnnotationManagementUpdateInfo annotationManagementUpdateInfo)
    {
        annotationManagementUpdateInfo = userService.saveAnnotationManagementUpdateInfo(annotationManagementUpdateInfo);

//        log.info("Received annotation management string: \n", annotationManagementUpdateInfoString);

        try {
//            annotationManagementUpdateInfo = userService.getPlainObjectMapper().readValue(annotationManagementUpdateInfoString, SanchayAnnotationManagementUpdateInfo.class);
            String annotationManagementUpdateInfoString = userService.getPlainObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(annotationManagementUpdateInfo);
//            annotationManagementUpdateInfo = userService.getPolymorphicObjectMapper().readValue(annotationManagementUpdateInfoString, SanchayAnnotationManagementUpdateInfo.class);
//            annotationManagementUpdateInfoString = userService.getPolymorphicObjectMapper().writeValueAsString(annotationManagementUpdateInfo);
            log.info("Annotation management info string being sent: \n");
            log.info(annotationManagementUpdateInfoString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        try {
////            log.info(userService.getPlainObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(annotationManagementUpdateInfoString));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

//        return ResponseEntity.ok().body(annotationManagementUpdateInfoString);
            return ResponseEntity.ok().body(annotationManagementUpdateInfo);

//        return ResponseEntity.internalServerError().body(null);
    }

    @PostMapping("/users/save")
    public ResponseEntity<SanchayUser> saveUser(@RequestBody SanchayUser user) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
//        return ResponseEntity.created(uri).body(userService.saveUser(user));
        return ResponseEntity.ok().body(userService.saveUser(user, false));
    }

    @PostMapping("/roles/save")
    public ResponseEntity<SanchayRole> saveRole(@RequestBody SanchayRole role) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
//        return ResponseEntity.created(uri).body(userService.saveRole(role));
        return ResponseEntity.ok().body(userService.saveRole(role, false));
    }

    @PostMapping("/languages/save")
    public ResponseEntity<SanchayResourceLanguage> saveLanguage(@RequestBody SanchayResourceLanguage language) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/language/save").toUriString());
//        return ResponseEntity.created(uri).body(userService.saveLanguage(language));
        return ResponseEntity.ok().body(userService.saveLanguage(language, false));
    }

    @PostMapping("/organisations/save")
    public ResponseEntity<SanchayOrganisation> saveLanguage(@RequestBody SanchayOrganisation organisation) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/organisation/save").toUriString());
//        return ResponseEntity.created(uri).body(userService.saveOrganisation(organisation));
        return ResponseEntity.ok().body(userService.saveOrganisation(organisation, false));
    }

    @PostMapping("/annotation-levels/save")
    public ResponseEntity<SanchayAnnotationLevel> saveAnnotationLevel(@RequestBody SanchayAnnotationLevel annotationLevel) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/organisation/save").toUriString());
//        return ResponseEntity.created(uri).body(userService.saveAnnotationLevel(annotationLevel));
        return ResponseEntity.ok().body(userService.saveAnnotationLevel(annotationLevel, false));
    }

    @PostMapping("/users/delete")
    public ResponseEntity<SanchayUser> deleteUser(SanchayUser user) {
        return ResponseEntity.ok().body(userService.deleteUser(user, false));
    }

    @PostMapping("/roles/delete")
    public ResponseEntity<SanchayRole> deleteRole(SanchayRole role) {
        return ResponseEntity.ok().body(userService.deleteRole(role, false));
    }

    @PostMapping("/languages/delete")
    public ResponseEntity<SanchayResourceLanguage> deleteLanguage(SanchayResourceLanguage language) {
        return ResponseEntity.ok().body(userService.deleteLanguage(language, false));
    }

    @PostMapping("/organisations/delete")
    public ResponseEntity<SanchayOrganisation> deleteOrganisation(SanchayOrganisation organisation) {
        return ResponseEntity.ok().body(userService.deleteOrganisation(organisation, false));
    }

    @PostMapping("/annotation-levels/delete")
    public ResponseEntity<SanchayAnnotationLevel> deletedAnnotationLevel(SanchayAnnotationLevel annotationLevel) {
        return ResponseEntity.ok().body(userService.deletedAnnotationLevel(annotationLevel, false));
    }

//    @PostMapping("/role/addtouser")
//    public ResponseEntity<SanchayRole> addRoleToUser(@RequestBody RoleToUserForm form)
//    {
//        userService.addRoleToUser(form.getUsername(), form.getRoleName());
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/language/addtouser")
//    public ResponseEntity<SanchayResourceLanguage> addLanguageToUser(@RequestBody LanguageToUserForm form)
//    {
//        userService.addLanguageToUser(form.getUsername(), form.getLanguageName());
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/organisation/addtouser")
//    public ResponseEntity<SanchayOrganisation> addLanguageToUser(@RequestBody OrganisationToUserForm form)
//    {
//        userService.addOrganisationToUser(form.getUsername(), form.getOrganisationName());
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
//                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//                Algorithm algorithm = Algorithm.HMAC256(sachayServerSecretKeyManager.getSecretKey().getBytes());
                Algorithm algorithm = Algorithm.HMAC256(SanchayServerUtils.getApplicationProperty(SachayServerSecretKeyManager.getSecretKeyPropertyName()).getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();

                SanchayUser user = userService.getUser(username, true);

                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
//                        .withClaim("roles", user.getRoles().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()))
                        .withClaim("roles", user.getRoles().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);

                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                log.error("Error logging in {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
//                    response.sendError(FORBIDDEN.value());
                response.setStatus(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
//                    tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);

                new ObjectMapper().writeValue(response.getOutputStream(), error);

            }
        } else {
            throw new RuntimeException("Refresh token is missing.");
        }
    }
}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}

@Data
class LanguageToUserForm {
    private String username;
    private String languageName;
}

@Data
class OrganisationToUserForm {
    private String username;
    private String organisationName;
}

@Data
class AnnotationLevelToUserForm {
    private String username;
    private String annotationLevelName;
}
