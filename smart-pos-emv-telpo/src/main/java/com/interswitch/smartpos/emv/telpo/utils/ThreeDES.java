package com.interswitch.smartpos.emv.telpo.utils;

/**
 * Created by yemiekai on 2016/12/20 0020.
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * 项目名称：****
 * 类名称：ThreeDES
 * 类描述： 3des 加密工具类
 *
 * @version 1.0
 */
public class ThreeDES {

    private static final String Algorithm = "DESede"; //定义 加密算法,可用 DES,DESede,Blowfish
    //key 根据实际情况对应的修改
    private final byte[] keybyte = "0123456789123456".getBytes(); //keybyte为加密密钥，长度为16字节
    private SecretKey deskey;

    ///生成密钥
    public ThreeDES() {
        deskey = new SecretKeySpec(keybyte, Algorithm);
    }

    //加密
    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            //加密失败，打日志
            ex.printStackTrace();
        }
        return null;
    }

    //解密
    public byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            //解密失败，打日志
            ex.printStackTrace();
        }
        return null;
    }

}
