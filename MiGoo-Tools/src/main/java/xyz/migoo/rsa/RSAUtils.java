package xyz.migoo.rsa;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
* @author xiaomi
 */
public class RSAUtils {
    public static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";

    public static final int KEY_SIZE_2048 = 2048;
    public static final int KEY_SIZE_1024 = 1024;

    private RSAUtils() {
    }

    private static final String ALGORITHM = "RSA";

    public static KeyPair generateKeyPair() {
        return generateKeyPair(KEY_SIZE_1024);
    }

    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Failed to generate key pair!", e);
        }
    }

    public static PublicKey getPublicKey(String base64PublicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(base64PublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key!", e);
        }
    }

    public static PublicKey getPublicKey(BigInteger modulus, BigInteger exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key!", e);
        }
    }

    public static String getBase64PublicKey(PublicKey publicKey) {
        return Base64.encodeBase64String(publicKey.getEncoded());
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(base64PrivateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get private key!", e);
        }
    }

    public static PrivateKey getPrivateKey(BigInteger modulus, BigInteger exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exponent);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get private key!", e);
        }
    }

    public static String getBase64PrivateKey(PrivateKey privateKey) {
        return Base64.encodeBase64String(privateKey.getEncoded());
    }

    public static byte[] encryptAsByteArray(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException("Encrypt failed!", e);
        }
    }

    public static byte[] encryptAsByteArray(String data, String base64PublicKey) {
        return encryptAsByteArray(data, getPublicKey(base64PublicKey));
    }

    public static String encryptAsString(String data, PublicKey publicKey) {
        return Base64.encodeBase64String(encryptAsByteArray(data, publicKey));
    }

    public static String encryptAsString(String data, String base64PublicKey) {
        return Base64.encodeBase64String(encryptAsByteArray(data, getPublicKey(base64PublicKey)));
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            throw new IllegalArgumentException("Decrypt failed!", e);
        }
    }

    public static String decrypt(byte[] data, String base64PrivateKey) {
        return decrypt(data, getPrivateKey(base64PrivateKey));
    }

    public static String decrypt(String data, PrivateKey privateKey) {
        return decrypt(Base64.decodeBase64(data), privateKey);
    }

    public static String decrypt(String data, String base64PrivateKey) {
        return decrypt(Base64.decodeBase64(data), getPrivateKey(base64PrivateKey));
    }

    /**
     * 根据文件路径读取私钥
     *
     * @param filePath 文件路径
     */
    public static String loadPrivateKey(String filePath)
            throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    /**
     * 读取私钥
     *
     * @param merchantNo 私钥文件名
     * @param filePath   文件路径
     */
    public static String loadPrivateKey(String merchantNo, String filePath)
            throws Exception {
        try {
            String path = String.format(filePath + "/%s.pfx", new Object[]{merchantNo});
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    /**
     * 根据文件路径读取公钥
     *
     * @param filePath 文件路径
     */
    public static String loadPublicKey(String filePath)
            throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("公钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 读取公钥
     *
     * @param merchantNo 公钥文件名
     * @param filePath   文件路径
     */
    public static String loadPublicKey(String merchantNo, String filePath)
            throws Exception {
        try {
            String path = String.format(filePath + "/%s.cer", new Object[]{merchantNo});
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("公钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }
}