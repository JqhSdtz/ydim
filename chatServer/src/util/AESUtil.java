package util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法
    private static final String tokenKey = "jsdfJFDS79FSDAJK";
    private static final String chatKey = "5ds4af65sEa6X7FF";

    public static String getTokenKey() {
        return tokenKey;
    }

    public static String getChatKey() {
        return chatKey;
    }

    public static String encrypt(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(byteContent);// 加密
            return Base64.getEncoder().encodeToString(result);//通过Base64转码返回
        } catch (Exception ex) {
            //e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String content, String key) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
            //执行操作
            byte[] base64 = Base64.getDecoder().decode(content);
            byte[] result = cipher.doFinal(base64);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }
}
