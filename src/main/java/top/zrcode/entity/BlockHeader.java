package top.zrcode.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.zrcode.util.HashUtil;

import java.util.Date;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 15:08
 */
@Data
@AllArgsConstructor
public class BlockHeader implements Hashable{
    /**
     * @description:
     * @param parentHash 用于存储上一个区块的hash值
     * @param transactionRoot 用于存储当前区块的交易merkle根
     * @param timestamp 用于存储当前区块的生成时间
     * @param nonce 用于存储当前区块计算量证明的nonce值
     * @return:
     * @author 知日
     * @date: 2021/6/18 15:13
     */
    String parentHash;
    String stateRoot;
    String transactionRoot;
    String receiptsRoot;
    Date timestamp;
    Long nonce;

    public BlockHeader(){
        timestamp = new Date();
    }

    public BlockHeader(Date createTime){
        timestamp = createTime;
    }

    @Override
    public String getHash() {
        return HashUtil.getInstance(parentHash)
                .add(transactionRoot)
                .add(receiptsRoot)
                .add(Long.toString(timestamp.getTime()))
                .getHash();
    }
}
