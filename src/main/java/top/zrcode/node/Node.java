package top.zrcode.node;

import com.alibaba.fastjson.JSON;
import top.zrcode.entity.Block;
import top.zrcode.entity.Receipt;
import top.zrcode.entity.Transaction;
import top.zrcode.util.HashUtil;

import java.security.PublicKey;
import java.util.*;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 15:54
 */
public class Node {
    private final static Integer blockSize= 2;
    private final static Integer poolSize=32;
    private final static Integer difficulty = 6;

    //区块链节点网络
    public static List<Node> nodeNet = new ArrayList<>();

    //节点名称
    private String nodeName;

    //当前节点的存储的区块链
    private List<Block> blockChain;

    //当前节点的交易池
    private volatile TransactionPool transactionPool = new TransactionPool(blockSize,poolSize);

    //根据交易hash获取交易回执
    private Map<String, Receipt> receiptMap = new HashMap<>();

    //当前节点正在生成的区块
    private Block curBlock;

    /**
     * @description: 初始化节点信息，新建创世区块，且新建sealer进程进行打包区块
     *               同时将节点加入区块链节点网络中
     * @param:  * @param nodeName
     * @return:
     * @author 知日
     * @date: 2021/6/18 15:17
     */
    public Node(String nodeName, Date creatTime){
        this.nodeName = nodeName;
        blockChain = new ArrayList<>();
        //创世区块
        blockChain.add(new Block("0",creatTime));
        Thread sealerThread = new Thread(() -> {
            try {
                sealer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sealerThread.start();
        nodeNet.add(this);
    }

    /**
     * @description: sealer进程，监听当前节点的交易池，取一定量
     *               的j交易打包进区块，执行交易以及pow工作量证明
     *               同时对其他节点已经打包好的区块进行共识
     * @param:
     * @return: void
     * @author 知日
     * @date: 2021/6/18 15:19
     */
    public void sealer(){
        while (true){
            try {
                if (transactionPool.getSize() >= blockSize){
                    System.out.println("Node: " + nodeName + " sealer start.");
                    synchronized (this){
                        //同步节点区块链，选择最长链
                        synchronizeChain();

                        //从交易池中取一定量的交易，打包进区块
                        curBlock = new Block();
                        Block parentBlock = blockChain.get(blockChain.size()-1);
                        curBlock.setParentHash(HashUtil.getInstance(parentBlock.getHash())
                                .add(parentBlock.getBlockHeader().getNonce())
                                .getHash());
                        List<Transaction> tList = transactionPool.getTransaction(blockSize);
                        curBlock.setTransactions(tList);

                        //执行交易
                        List<Receipt> receipts = Execution.exeTransaction(curBlock);

                        //更新区块头信息
                        curBlock.setReceipts(receipts);
                        curBlock.genTransactionRoot();
                        curBlock.genReceiptRoot();
                    }

                    //进行pow工作量证明，同时监听其他节点打包的区块并进行共识
                    curBlock = mine(curBlock.getHash());
                    if (curBlock == null) continue;

                    //落盘存储交易、区块等数据
                    storage(curBlock);

                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void addTransactionToNet(String t, PublicKey key){
        this.addTransactionToNet(JSON.parseObject(t,Transaction.class),key);
    }

    /** 
     * @description: 对client开发的sdk，用于添加交易
     *               同时广播给其他节点
     * @param:  * @param t
     * @return: void 
     * @author 知日
     * @date: 2021/6/18 15:25
     */
    public void addTransactionToNet(Transaction t, PublicKey key){
        addTransaction(t,key);
        for (Node n:nodeNet){
            if (n != this){
                n.addTransaction(t,key);
            }
        }
    }

    /** 
     * @description: 向交易池添加交易
     * @param:  * @param t
     * @return: void 
     * @author 知日
     * @date: 2021/6/18 15:25
     */
    public void addTransaction(Transaction t, PublicKey key){
        transactionPool.addTransaction(t,key);
    }

    /** 
     * @description: 删除交易池中的交易，落盘存储交易、区块等数据，打印日志
     * @param:  * @param  
     * @return: void 
     * @author 知日
     * @date: 2021/6/18 15:26
     */
    public void storage(Block block){

        synchronized (this){

            if (block == null)
                throw new RuntimeException("Block is null.");
            //获得最后一个区块的hash值
            Block parentBlock = blockChain.get(blockChain.size()-1);
            String parentHash = HashUtil.getInstance(parentBlock.getHash())
                    .add(parentBlock.getBlockHeader().getNonce()).getHash();
            String currentBlockHash = HashUtil.getInstance(block.getHash())
                    .add(block.getBlockHeader().getNonce())
                    .getHash();
            String target = new String(new char[difficulty]).replace('\0','0');
            //hash校验错误
            if (!parentHash.equals(block.getBlockHeader().getParentHash()) ||
                !currentBlockHash.substring(0,difficulty).equals(target))
                throw new RuntimeException("Block insert error.");

            transactionPool.remove(block.getBlockBody().getTransactions());
            blockChain.add(block);

            for (Receipt r:block.getBlockBody().getReceipts()){
                r.setBlockHash(currentBlockHash);
                r.setBlockNumber(blockChain.size()-1);
                receiptMap.put(r.getTransactionHash(),r);
            }

            System.out.println("Node: " + nodeName + " add a block.\n"
                    + "Block Chain Size: " + blockChain.size() + "\n"
                    + "Parent Block Hash: " + block.getBlockHeader().getParentHash() + "\n"
                    + "Current Block Hash: " + currentBlockHash + "\n"
                    + block.getBlockBody().getTransactions() + "\n"
                    + block.getBlockBody().getReceipts() + "\n");
        }
    }

    /** 
     * @description: pow挖矿，并进行共识
     * @param:  * @param lastHash 
     * @return: top.zrcode.entity.Block 
     * @author 知日
     * @date: 2021/6/18 15:26
     */
    public Block mine(String lastHash){
        String target = new String(new char[difficulty]).replace('\0','0');
        Long nonce = Math.round(Math.random()*Integer.MAX_VALUE);
        String newHash = HashUtil.getInstance(lastHash).add(nonce).getHash();
        Integer blockLen = blockChain.size();
        while(true){

            if (blockChain.size()!=blockLen){
                return null;
            }

            //持续计算新的hash值，以确定是否完成计算结果
            //计算完成则将区块进行广播共识
            if (!newHash.substring(0,difficulty).equals(target)){
                ++nonce;
                newHash = HashUtil.getInstance(lastHash).add(nonce).getHash();
            }else{
                curBlock.setNonce(nonce);
                //进行共识
                consensus(curBlock);
                return curBlock;
            }

        }
    }

    /**
     * @description: 对于待共识的区块进行共识
     * @param:  * @param block
     * @return: boolean
     * @author 知日
     * @date: 2021/6/18 15:31
     */
    public boolean verify(Block block){
        //target为前置0的数量
        String target = new String(new char[difficulty]).replace('\0','0');
        String hash = HashUtil.getInstance(block.getHash()).add(block.getBlockHeader().getNonce()).getHash();

        //计算当前区块链中最后一个区块的hash
        Block parentBlock = blockChain.get(blockChain.size()-1);
        String parentHash = HashUtil.getInstance(parentBlock.getHash())
                .add(parentBlock.getBlockHeader().getNonce()).getHash();
        if (parentHash.equals(block.getBlockHeader().getParentHash())
                && hash.substring(0,difficulty).equals(target)){
            return true;
        }else {
            return false;
        }
    }

    //提供接口给其他节点传输待共识的区块
    public void transferFromNodeNet(Block block){
        if (verify(block)){
            new Thread(()->{
                storage(block);
            }).start();
        }
    }

    //对待共识的区块传输到其他节点
    public void consensus(Block block){
        for (Node n:nodeNet){
            if (n != this){
                n.transferFromNodeNet(block);
            }
        }
    }

    public int getBlockChainLen(){
        return blockChain.size();
    }

    public List<Block> getBlockChain(){
        return blockChain;
    }

    //同步区块链网络中的最长链
    public void synchronizeChain(){
        int maxLen = blockChain.size();
        Node node = null;
        for (Node n:nodeNet){
            if (n != this && n.getBlockChainLen() > maxLen){
                node = n;
                maxLen = n.getBlockChainLen();
            }
        }
        if (node!=null){
            this.blockChain = new LinkedList<>(node.getBlockChain());
        }
    }
}
