package top.zrcode.client;

import top.zrcode.entity.Transaction;
import top.zrcode.entity.Wallet;
import top.zrcode.node.Node;
import top.zrcode.util.ECDSAUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/16 16:21
 */
public class Client {
    static Node node;
    static List<Node> nodes;
    static Date creatTime = new Date();
    static Wallet wallet = new Wallet();

    public static void main(String[] args) throws Exception{
        //用于启动区块链网络，
        startNet();

        //连接区块链网络中的0号节点，并定时对其添加交易
        while (true){
            try{
                //生成交易，并用私钥签名
                Transaction t = new Transaction("default_type",
                        Long.toString(System.currentTimeMillis()),
                        Double.toHexString(Math.random()));
                t.setSign(ECDSAUtil.signWithPrivateKey(t.getHash(),wallet.getPrivateKey()));

                //Serializable，向节点中添加交易
                node.addTransactionToNet(t.serializableData(),wallet.getPublicKey());
                System.out.println("add a transaction");
            }catch (Exception ex){
                ex.printStackTrace();;
            }finally {
                Thread.sleep(5000);
            }
        }
    }
    
    /** 
     * @description: 生成4个区块链节点的区块链网络
     * @param:  * @param  
     * @return: void 
     * @author 知日
     * @date: 2021/6/18 15:37
     */
    public static void startNet(){
        nodes = new ArrayList<>();
        for (int i=0;i<4;i++)
            nodes.add(new Node("Node "+i,creatTime));
        node = nodes.get(0);
    }
}
