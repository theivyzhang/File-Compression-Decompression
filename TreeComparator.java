import java.util.Comparator;

/**
 * tree comparator class that determines the order of the Huffman Type nodes
 * @author ivyzhang for CS10 Spring 2022
 */
public class TreeComparator implements Comparator<BinaryTree<HuffmanType>> {
    @Override
    public int compare(BinaryTree<HuffmanType> t1, BinaryTree<HuffmanType> t2){
        // get the frequencies of the two nodes
        int t1freq = t1.data.getFreq();
        int t2freq = t2.data.getFreq();
        // do comparison
        if (t1freq > t2freq){
            return 1;
        }
        else if (t1freq < t2freq){
            return -1;
        }
        else{
            return 0;
        }
    }
}
