package sanchay.server.utils;

import sanchay.server.security.SachayServerSecretKeyManager;

public class SanchaySecurityUtils {

    public static SachayServerSecretKeyManager getSachayServerSecretKeyManagerInstace()
    {
        return new SachayServerSecretKeyManager();
    }

}
