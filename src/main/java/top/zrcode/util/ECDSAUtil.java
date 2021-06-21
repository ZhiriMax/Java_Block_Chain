package top.zrcode.util;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/17 9:57
 */
public class ECDSAUtil {
    private static final String ALGORITHM = "EC";
    private static final String SECP256K1 = "secp256k1";
    private static final String SIGN_ALGORITHMS_ECDSA = "SHA256withECDSA";

    public static byte[] signWithPrivateKey(String data, PrivateKey key){
        try {
            Signature signaturePrivate = Signature.getInstance(SIGN_ALGORITHMS_ECDSA);
            signaturePrivate.initSign(key);
            signaturePrivate.update(data.getBytes("UTF-8"));
            byte[] sign = signaturePrivate.sign();
            return sign;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean checkSign(String data, byte[] sign, PublicKey key){
        try {
            Signature signaturePublic = Signature.getInstance(SIGN_ALGORITHMS_ECDSA);
            signaturePublic.initVerify(key);
            signaturePublic.update(data.getBytes("UTF-8"));
            return signaturePublic.verify(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static KeyPair genKeyPair(){
        try{
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(SECP256K1);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(ecSpec,new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
