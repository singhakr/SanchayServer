package sanchay.server.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;
import sanchay.common.SanchaySpringServerEndPoints;
import sanchay.server.security.SachayServerSecretKeyManager;
import sanchay.server.utils.SanchaySecurityUtils;
import sanchay.server.utils.SanchayServerUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class SanchayAuthorizationFilter extends OncePerRequestFilter {

    private static SachayServerSecretKeyManager sachayServerSecretKeyManager = SanchaySecurityUtils.getSachayServerSecretKeyManagerInstace();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals(SanchaySpringServerEndPoints.AUTH_BASE + SanchaySpringServerEndPoints.LOGIN)
                || request.getServletPath().equals(SanchaySpringServerEndPoints.AUTH_BASE + SanchaySpringServerEndPoints.TOKEN_REFRESH)) {
            filterChain.doFilter(request, response);
//            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        }
        else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
//                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//                    Algorithm algorithm = Algorithm.HMAC256(sachayServerSecretKeyManager.getSecretKey().getBytes());
                    Algorithm algorithm = Algorithm.HMAC256(SanchayServerUtils.getApplicationProperty(SachayServerSecretKeyManager.getSecretKeyPropertyName()).getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority((role)));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    filterChain.doFilter(request, response);
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
            }
            else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
