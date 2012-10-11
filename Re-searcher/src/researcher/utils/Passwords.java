package researcher.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import researcher.exceptions.SystemUnavailableException;
import sun.misc.BASE64Encoder;

public class Passwords {

    private static synchronized String encryptOnce(String plaintext)
            throws SystemUnavailableException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new SystemUnavailableException(e);
        }
        try {
            md.update(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new SystemUnavailableException(e.getMessage());
        }
        byte raw[] = md.digest();
        String hash = (new BASE64Encoder()).encode(raw);
        return hash;
    }

    private static synchronized String encrypt(String plaintext) throws SystemUnavailableException {
        String hash = encryptOnce(plaintext);
        return hash;
    }

    public static synchronized String constructEncryptedPassword(String username, String password)
            throws SystemUnavailableException {
        return encrypt(username + password);
    }

}
