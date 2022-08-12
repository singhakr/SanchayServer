package sanchay.server.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//@Component
public class SachayServerSecretKeyManager {

//    @Value("${app.jwt.secret}")
    private static String secretKeyPropertyName = "app.jwt.secret";

    public static String getSecretKeyPropertyName()
    {
        return secretKeyPropertyName;
    }
}
