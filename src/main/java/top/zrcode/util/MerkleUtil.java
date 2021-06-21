package top.zrcode.util;

import top.zrcode.entity.Hashable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 知日
 * @version 1.0
 * @date 2021/6/17 11:19
 */
public class MerkleUtil {

    private static Integer treeSize(int size){
        int n=size-1;
        n |= n>>> 1;
        n |= n>>> 2;
        n |= n>>> 4;
        n |= n>>> 8;
        n |= n>>> 16;
        return (n<0)?1:n+1;
    }

    public static String genMerkleTreeRoot(List<Hashable> list){
        if ((list.size() & (list.size()-1))!=0 ){
            for (int i=list.size();i<treeSize(list.size());i++){
                list.add(list.get(list.size()-1));
            }
        }
        List<String> preHash = new ArrayList<>();
        for (Hashable h:list){
            preHash.add(h.getHash());
        }
        while (preHash.size() != 1){
            List<String> curHash = new ArrayList<>();
            for (int i=0;i<preHash.size();i+=2){
                curHash.add(HashUtil.getInstance(preHash.get(i)).add(preHash.get(i + 1)).getHash());
            }
            preHash = curHash;
        }
        return preHash.get(0);
    }
}
