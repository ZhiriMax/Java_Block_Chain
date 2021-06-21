package top.zrcode.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 14:58
 */
public class HashUtil{

    public static class HashBuilder{
        String current;

        public HashBuilder(){}

        public HashBuilder(String code){
            current = code;
        }

        public HashBuilder add(String code){
            current = current + code;
            return this;
        }

        public HashBuilder add(Integer code){
            if (code == null) code = 0;
            current = current + code.toString();
            return this;
        }

        public HashBuilder add(Long code){
            if (code == null) code = 0l;
            current = current + code.toString();
            return this;
        }

        public String getHash(){
            return DigestUtils.sha256Hex(current);
        }

    }
    public static String SHA256(String code){
        return DigestUtils.sha256Hex(code);
    }

    public static byte[] SHA256(byte[] code){
        return DigestUtils.sha256(code);
    }

    public static HashBuilder getInstance(){
        return new HashBuilder();
    }

    public static HashBuilder getInstance(String code){
        return new HashBuilder(code);
    }
}
