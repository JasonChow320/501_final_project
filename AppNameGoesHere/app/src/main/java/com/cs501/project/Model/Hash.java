package com.cs501.project.Model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* https://www.baeldung.com/sha-256-hashing-java */
public class Hash {
    public static String sha256(String s) {

        try {
            // Create SHA256 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA256");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
