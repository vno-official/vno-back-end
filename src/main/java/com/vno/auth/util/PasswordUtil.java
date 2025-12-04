package com.vno.auth.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int WORK_FACTOR = 12;

    public static String hashPassword(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verifyPassword(String plaintext, String hash) {
        if (plaintext == null || hash == null) {
            return false;
        }
        return BCrypt.checkpw(plaintext, hash);
    }
}
