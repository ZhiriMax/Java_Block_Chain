package top.zrcode.node;

import top.zrcode.entity.Block;
import top.zrcode.entity.Receipt;
import top.zrcode.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 16:03
 */
public class Execution {

    /** 
     * @description: 执行交易并返回回执 
     * @param:  * @param b 
     * @return: java.util.List<top.zrcode.entity.Receipt> 
     * @author 知日
     * @date: 2021/6/18 15:10
     */
    public static List<Receipt> exeTransaction(Block b){
        List<Receipt> result = new ArrayList<>();
        int idx = 0;
        for (Transaction t:b.getBlockBody().getTransactions()){
            result.add(genReceipt(t,idx++));
        }
        return result;
    }

    private static Receipt genReceipt(Transaction t,Integer tranIdx){
        return new Receipt(t.getHash(),tranIdx);
    }
}
