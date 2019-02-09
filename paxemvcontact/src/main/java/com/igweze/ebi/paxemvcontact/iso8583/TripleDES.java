package com.igweze.ebi.paxemvcontact.iso8583;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author josepholaoye
 */
public class TripleDES {

    String key;

    public TripleDES(String myEncryptionKey) {
        key = myEncryptionKey;
    }

    /**
     * Method To Encrypt The String
     *
     * @param
     * @return encrpted string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String harden(String key, String toEncrypt) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] tmp = hexToBytes(key);
        byte[] keyBytes = new byte[24];
        System.arraycopy(tmp, 0, keyBytes, 0, 16);
        System.arraycopy(tmp, 0, keyBytes, 16, 8);
        SecretKey sk = new SecretKeySpec(keyBytes, "DESede");
        // create an instance of cipher
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sk);

        // enctypt!
        byte[] encrypted = cipher.doFinal(hexToBytes(toEncrypt));
        return bytesToHex(encrypted);
    }

    /**
     * Method To Decrypt An Ecrypted String
     *
     * @param
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String soften(String key, String encrypted) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if(encrypted == null) {
            return "";
        }

        /*byte[] message = HexCodec.hexDecode(encrypted);

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digestOfPassword = md.digest(HexCodec.hexDecode(key));
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        for (int j = 0, k = 16; j < 8;) {
            keyBytes[k++] = keyBytes[j++];
        }

        SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");

        Cipher decipher = Cipher.getInstance("DESede/ECB/NoPadding");
        decipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] plainText = decipher.doFinal(message);*/

        /*L.fine("Key = > " + key);
        L.fine("Value => " + encrypted);
        byte[] tmp = hexToBytes(key);
        byte[] keyBytes = new byte[24];
        System.arraycopy(tmp, 0, keyBytes, 0, 8);
        //System.arraycopy(tmp, 0, keyBytes, 16, 8);
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "DESede"));
        byte[] plaintext = cipher.doFinal(hexToBytes(encrypted));
        return bytesToHex(plaintext);*/

        byte[] tmp = hexToBytes(key);
        byte[] keyBytes = new byte[24];
        System.arraycopy(tmp, 0, keyBytes, 0, 16);
        System.arraycopy(tmp, 0, keyBytes, 16, 8);
        SecretKey sk = new SecretKeySpec(keyBytes, "DESede");

        // do the decryption with that key
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decrypted = cipher.doFinal(hexToBytes(encrypted));
        return bytesToHex(decrypted);
    }

    public static byte[] hexToBytes(String hex) {
        if ((hex.length() & 0x01) == 0x01)
            throw new IllegalArgumentException();

        byte[] bytes = new byte[hex.length() / 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = Character.digit((int) hex.charAt(idx * 2), 16);
            int lo = Character.digit((int) hex.charAt(idx * 2 + 1), 16);
            if ((hi < 0) || (lo < 0))
                throw new IllegalArgumentException();
            bytes[idx] = (byte) ((hi << 4) | lo);
        }

        return bytes;
    }

    public static String bytesToHex(byte[] bytes) {

        char[] hex = new char[bytes.length * 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = (bytes[idx] & 0xF0) >>> 4;
            int lo = (bytes[idx] & 0x0F);
            hex[idx * 2] = (char) (hi < 10 ? '0' + hi : 'A' - 10 + hi);
            hex[idx * 2 + 1] = (char) (lo < 10 ? '0' + lo : 'A' - 10 + lo);
        }
        return new String(hex);
    }

    public static String getMac(String seed, byte[] macDataBytes) throws Exception{

        byte [] keyBytes = hexToBytes(seed);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(keyBytes, 0, keyBytes.length);
        digest.update(macDataBytes, 0, macDataBytes.length);
        byte[] hashedBytes = digest.digest();
        String hashText = bytesToHex(hashedBytes);
        hashText = hashText.replace(" ", "");

        if (hashText.length() < 64) {
            int numberOfZeroes = 64 - hashText.length();
            String zeroes = "";
            String temp = hashText.toString();
            for (int i = 0; i < numberOfZeroes; i++)
                zeroes = zeroes + "0";
            temp = zeroes + temp;
            return temp;
        }

        return hashText;
    }


}
