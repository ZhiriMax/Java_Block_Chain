package top.zrcode.entity;

import lombok.Data;
import top.zrcode.util.HashUtil;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 15:27
 */
@Data
public class Receipt implements Hashable{
    String blockHash;
    Integer blockNumber;
    String transactionHash;
    Integer transactionIndex;
    String from;
    String to;
    String contractAddress;

    public Receipt(String transactionHash,Integer transactionIndex){
        this.transactionHash = transactionHash;
        this.transactionIndex = transactionIndex;
    }

    @Override
    public String getHash() {
        return HashUtil.getInstance(transactionHash)
                .add(transactionIndex)
                .getHash();
    }
}
