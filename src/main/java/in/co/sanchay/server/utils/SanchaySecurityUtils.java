package in.co.sanchay.server.utils;

import in.co.sanchay.server.security.SachayServerSecretKeyManager;

public class SanchaySecurityUtils {

    public static SachayServerSecretKeyManager getSachayServerSecretKeyManagerInstace()
    {
        return new SachayServerSecretKeyManager();
    }

}
