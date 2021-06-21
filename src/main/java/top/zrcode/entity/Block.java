package top.zrcode.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 14:48
 */
@Data
public class Block implements Hashable{
    private final Integer blockSize = 2;

    private BlockHeader blockHeader;

    private BlockBody blockBody;

    public Block(){
        this(null,null);
    }

    public Block(String parentHash){
        this(parentHash,null);
    }

    /** 
     * @description:  对于初创块，拟定父hash以及生成时间
     * @param:  * @param parentHash
     * @param createTime 初创块的生成时间
     * @return:  
     * @author 知日
     * @date: 2021/6/18 15:07
     */
    public Block(String parentHash, Date createTime){
        blockHeader = createTime == null?new BlockHeader():new BlockHeader(createTime);
        blockBody = new BlockBody();
        blockHeader.setParentHash(parentHash);
        blockHeader.setTransactionRoot(parentHash);
        blockHeader.setReceiptsRoot(parentHash);
    }

    /** 
     * @description: 对transaction用merkle树获取merkle根，并返回block
     *               Header除了nonce以外的hash值
     * @param:  * @param  
     * @return: java.lang.String 
     * @author 知日
     * @date: 2021/6/18 15:09
     */
    @Override
    public String getHash() {
        if (blockHeader.getTransactionRoot().length() == 0){
            genTransactionRoot();
        }
        if (blockHeader.getReceiptsRoot().length() == 0){
            genReceiptRoot();
        }
        return blockHeader.getHash();
    }

    public void setTransactions(List<Transaction> tList){
        blockBody.setTransactions(tList);
    }

    public void setReceipts(List<Receipt> rList){
        blockBody.setReceipts(rList);
    }

    /** 
     * @description: 利用Merkle树计算交易的merkle根 
     * @param:  * @param  
     * @return: void 
     * @author 知日
     * @date: 2021/6/18 15:11
     */
    public void genTransactionRoot(){
        blockHeader.setTransactionRoot(
                blockBody.getTransactionRoot()
        );
    }

    public void genReceiptRoot(){
        blockHeader.setReceiptsRoot(
                blockBody.getReceiptRoot()
        );
    }

    public void setParentHash(String parentHash){
        blockHeader.setParentHash(parentHash);
    }

    public void setNonce(Long nonce){
        blockHeader.setNonce(nonce);
    }
}
