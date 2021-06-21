package top.zrcode.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import top.zrcode.util.HashUtil;
import top.zrcode.util.MerkleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 15:08
 */
@Data
@NoArgsConstructor
public class BlockBody{
    /**
     * @description: transactions用于存储区块中包含的交易
     * @param:
     * @return:
     * @author 知日
     * @date: 2021/6/18 15:10
     */
    List<Transaction> transactions;

    List<Receipt> receipts;

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }

    public void setReceipts(List<Receipt> receipts){
        this.receipts = new ArrayList<>(receipts);
    }

    public String getTransactionRoot() {
        return MerkleUtil.genMerkleTreeRoot(
                new ArrayList<Hashable>(transactions)
        );
    }

    public String getReceiptRoot() {
        return MerkleUtil.genMerkleTreeRoot(new ArrayList<Hashable>(receipts));
    }
}
