package top.zrcode.node;

import com.alibaba.fastjson.JSON;
import top.zrcode.entity.Transaction;
import top.zrcode.util.ECDSAUtil;

import java.security.PublicKey;
import java.util.*;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 15:55
 */
public class TransactionPool {
    private Integer poolSize;
    private Integer blockSize;

    List<Transaction> transactions;
    Map<String,Boolean> check = new HashMap<>();

    volatile Integer size;

    /** 
     * @description: 使用单区块交易数、交易池大小初始化交易池 
     * @param:  * @param blockSize
     * @return:  
     * @author 知日
     * @date: 2021/6/18 15:33
     */
    public TransactionPool(Integer blockSize,Integer poolSize){
        this.poolSize = poolSize;
        this.blockSize = blockSize;
        transactions = new ArrayList<>(poolSize);
    }

    /** 
     * @description: 解码交易
     * @param:  * @param serializableData
     * @return: void 
     * @author 知日
     * @date: 2021/6/18 15:33
     */
    public void addTransaction(String serializableData,PublicKey publicKey){
        addTransaction(JSON.parseObject(serializableData,Transaction.class),publicKey);
    }

    /** 
     * @description: 将交易加入交易池中
     * @author 知日
     * @date: 2021/6/18 15:34
     */
    public void addTransaction(Transaction t, PublicKey publicKey){
        synchronized (this){
            //判断交易池是否已满
            if (transactions.size() == poolSize){
                throw new RuntimeException("The transaction pool is full.");
            }
            //判断该交易是否已经存在
            if (check.containsKey(t.getHash())){
                throw new RuntimeException("The transaction is existed.");
            }
            //验签，确认该交易未被篡改
            if (!ECDSAUtil.checkSign(t.getHash(),t.getSign(),publicKey)){
                throw new RuntimeException("The verify ie error.");
            }
            transactions.add(t);
            check.put(t.getHash(),true);
        }
    }


    /** 
     * @description: 一次性获得一个区块交易数的交易列表 
     * @param:  * @param num 
     * @return: java.util.List<top.zrcode.entity.Transaction> 
     * @author 知日
     * @date: 2021/6/18 15:35
     */
    public List<Transaction> getTransaction(int num){
        synchronized (this){
            if (num > transactions.size()){
                throw new RuntimeException("Out of length.");
            }
            return transactions.subList(0,0+num);
        }
    }

    public Integer getSize(){
        return transactions.size();
    }

    /** 
     * @description: 将对应列表的交易从交易池中删除
     * @param:  * @param tList 
     * @return: void 
     * @author 知日
     * @date: 2021/6/18 15:35
     */
    public void remove(List<Transaction> tList){
        synchronized (this){
            transactions.removeAll(tList);
        }
    }

}
