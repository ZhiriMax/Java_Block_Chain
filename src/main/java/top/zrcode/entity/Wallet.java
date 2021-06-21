package top.zrcode.entity;

import lombok.Data;
import top.zrcode.util.ECDSAUtil;

import java.security.*;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/17 8:51
 */
@Data
public class Wallet {

    /** 
     * @description: 客户端的公私钥对 
     * @param:  * @param null 
     * @return:  
     * @author 知日
     * @date: 2021/6/18 16:52
     */
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public Wallet(){
        KeyPair keyPair=ECDSAUtil.genKeyPair();
        if (keyPair != null){
            publicKey=keyPair.getPublic();
            privateKey=keyPair.getPrivate();
        }
    }
}
