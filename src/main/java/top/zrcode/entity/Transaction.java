package top.zrcode.entity;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import top.zrcode.util.HashUtil;


/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 15:11
 */
@Data
@AllArgsConstructor
public class Transaction implements Hashable{
    String type;
    Integer nonce;
    String value;
    String receiveAddress;
    String data;
    byte[] sign;

    public Transaction(String type,String value,String data){
        this.setType(type);
        this.setValue(value);
        this.setData(data);
    }

    public String serializableData(){
        return JSON.toJSONString(this);
    }

    @Override
    public String getHash(){
        return HashUtil.getInstance(type)
                .add(nonce)
                .add(value)
                .add(receiveAddress)
                .add(data)
                .getHash();
    }
}
