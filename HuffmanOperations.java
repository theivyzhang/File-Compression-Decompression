import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
* @author Ivy Aiwei Zhang
*/

public class HuffmanOperations {
    /**
     * Creates the frequency table: characters as keys and frequencies as value
     *
     * @param filename the input file we want to use to create the frequency table
     * @return frequency map that maps each character to the number of times it appears in the file
     */
    public static HashMap<Character, Integer> createMap(String filename) {
        HashMap<Character, Integer> freqMap = new HashMap<Character, Integer>(); // initialize the frequency hashmap
        // wrap around try catch
        try {
            // read the input file
            BufferedReader name = new BufferedReader(new FileReader(filename));

            int currChar = name.read();             // the current character
            // while the input file is not empty, set the first character as current character, then continue to the next character, and the next etc
            while (currChar != -1) {
                char char1 = (char) currChar;
                // if the map already contains the character, increment the character count, otherwise add the new character
                if (freqMap.containsKey(char1)) {
                    freqMap.put(char1, freqMap.get(char1) + 1);
                } else {
                    freqMap.put(char1, 1);
                }
                currChar = name.read(); // increment (continue reading to) the next character
            }
            return freqMap;
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
        return null;
    }

    /**
     * Priority queue that contains initial trees for each character
     *
     * @param map hashmap created from the frequency table
     * @return Priority queue that contains initial trees for each character
     */
    public static PriorityQueue<BinaryTree<HuffmanType>> putInPQ(HashMap<Character, Integer> map) {
        // initialize the priority queue that contains initial tree (individual nodes) for each character
        PriorityQueue<BinaryTree<HuffmanType>> treePQList = new PriorityQueue<BinaryTree<HuffmanType>>(map.size(), new TreeComparator());
        // for each key in mapset, create the tree with the key as the key of the node
        for (Character key : map.keySet()) {
            // edge case: if there is only one character in the file, then we need to create a tree whose left and right is the node itself
            // as such, we need to create two other individual trees --> so that tree can be created in createTree underneath
            if (map.keySet().size() == 1) {
                HuffmanType node = new HuffmanType(key, map.get(key));
                BinaryTree<HuffmanType> tree1 = new BinaryTree<HuffmanType>(node);
                BinaryTree<HuffmanType> tree2 = new BinaryTree<HuffmanType>(node);
                treePQList.add(tree1);
                treePQList.add(tree2);
            }
            // in most cases, add the individual node tree to the priority queue
            HuffmanType node = new HuffmanType(key, map.get(key));
            BinaryTree<HuffmanType> tree = new BinaryTree<HuffmanType>(node);
            treePQList.add(tree);
        }
        return treePQList;
    }

    /**
     * Huffman encoding techniques
     * Creates the tree with the lowest frequency characters be deepest in the tree (longest bit codes)
     *
     * @param filename file to be read
     * @return the tree with the lowest frequency characters be deepest in the tree
     */
    public static BinaryTree<HuffmanType> createTree(String filename) {
        // instantiate the priority queue from which you want to do Huffman encoding
        PriorityQueue<BinaryTree<HuffmanType>> treeList = putInPQ(createMap(filename)); // essentially the treePQList
        // as long as there are more than one initial tree in the priority queue, remove the first two,
        // then use the sum of their frequencies as the root of the two, then add it to the priority queue treeList
        while (treeList.size() > 1) {
            BinaryTree<HuffmanType> t1 = treeList.remove();
            BinaryTree<HuffmanType> t2 = treeList.remove();
            int totalFreq = t1.data.getFreq() + t2.data.getFreq();
            BinaryTree<HuffmanType> tree = new BinaryTree<HuffmanType>(new HuffmanType(null, totalFreq), t1, t2);
            treeList.add(tree);
            // edge case: if there is only one tree (given only one character in the input file),
            // then we create a tree with itself as the left and right
            if (treeList.size() == 1) {
                BinaryTree<HuffmanType> treePQ = treeList.remove(); // remove the tree first
                return new BinaryTree<HuffmanType>(new HuffmanType(null, treePQ.data.getFreq()), treePQ, treePQ);
            }
        }
        return treeList.remove();
    }

    /**
     * this method creates a map that pairs characters with their code words
     *
     * @param tree the tree to traverse (encodess all thhe information about the code for eaach character)
     * @return a map that pairs characters with their code words
     * @throws Exception
     */
    public static HashMap<Character, String> codeRetrieval(BinaryTree<HuffmanType> tree) throws Exception {
        HashMap<Character, String> treeMap = new HashMap<Character, String>();
        // as long as the tree to traverse is not empty, we can call the helper function to generate map with each character
        // mapped to a sequence code of 0 and/or 1
        if (tree != null) {
            codeRetrievalHelper(tree, "", treeMap);
        }
        System.out.println("CODE Map: " + treeMap + "\n");  // prints the code map when compression is called
        return treeMap;
    }

    /**
     * code retrieval helper function that determines whether we traverse left (0) or right (1)
     *
     * @param tree       the tree to traverse
     * @param pathString the pathString we want to generate that's a series of code
     * @param map        the map to insert the character and pathSring
     */
    public static void codeRetrievalHelper(BinaryTree<HuffmanType> tree, String pathString, HashMap map) {
        // if the tree node is a leaf, we put it to the map
        if (tree.isLeaf()) {
            map.put(tree.data.getChar(), pathString);
        } else {
            // if the tree has a left child, we traverse down while updating the path string
            if (tree.hasLeft()) {
                codeRetrievalHelper(tree.getLeft(), pathString + "0", map);
            }
            // if the tree has a right child, we traverse down while updating the path string
            if (tree.hasRight()) {
                codeRetrievalHelper(tree.getRight(), pathString + "1", map);
            }
        }
    }

    /**
     * Compression: compress into bit codes
     *
     * @param pathName                 the file we want to compress
     * @param compressedOutputPathName the destination (output) file that has the compressed content
     * @throws Exception
     */
    public static void Compression(String pathName, String compressedOutputPathName) throws Exception {
        // initialize the reader to read the file
        BufferedReader input = new BufferedReader(new FileReader(pathName));
        // create the output file
        BufferedBitWriter bitOutput = new BufferedBitWriter(compressedOutputPathName);
        try {
            // the map to refer to for code
            HashMap<Character, String> treeMap = codeRetrieval(createTree(pathName));
            int currChar;

            // as long as the input file is not empty, we need to get the code word associated with the character,
            // and continue for every character;
            while ((currChar = input.read()) != -1) {
                String code = treeMap.get((char) currChar);
                for (int i = 0; i < code.length(); i++) {
                    if ((int) code.charAt(i) == 48) {
                        bitOutput.writeBit(false); // bits = boolean; false if corresponding to code character "0"
                    } else if ((int) code.charAt(i) == 49) {
                        bitOutput.writeBit(true); // bits = boolean; true if corresponding to code character "1"
                    }
                }
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        } finally {
            // close all files
            input.close();
            bitOutput.close();
        }
    }

    /**
     * Decompression uses bit reader to read compressed file, decode, then write decoded content into a new file
     *
     * @param compressedInputPathName    the file that was previously compressed (in Compression method)
     * @param decompressedOutputPathName the output file (destination) from compressedPathName
     * @param pathName                   the file used to compress previously
     * @throws Exception
     */
    public static void Decompression(String compressedInputPathName, String decompressedOutputPathName, String pathName) throws Exception {
        // initialize the bit reader to read the compressed file
        BufferedBitReader bitInput = new BufferedBitReader(compressedInputPathName);
        // create the output file
        BufferedWriter output = new BufferedWriter(new FileWriter(decompressedOutputPathName));

        try {
            BinaryTree<HuffmanType> treeMap = createTree(pathName); // the reference map for frequencies of characters from the original file
            BinaryTree<HuffmanType> current = treeMap; // create a copy of treeMap for traversal purposes so that
            // the original treeMap is not altered

            // as long as there is still a character afterwards
            while (bitInput.hasNext()) {
                boolean bit = bitInput.readBit();  // the bit that is being read
                if (bit) { // if (bit) corresponds to 1, so traverse down the right of the tree
                    current = current.getRight();
                } else { // otherwise, traverse down the left
                    current = current.getLeft();
                }
                // if we reach a leaf, simply write that to the output file
                if (current.isLeaf()) {
                    output.write(current.data.getChar());
                    current = treeMap;
                }
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        } finally {
            // close all files
            bitInput.close();
            output.close();
        }


    }


    // test cases
    public static void main(String[] args) throws Exception {

        // TODO: TEST CASE: THE US CONSTITUTION
        System.out.println("THE US CONSTITUTION TEST CASE \n");
        String filename = "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/USConstitution.txt";
        // testing the frequency table(map) with each character's frequency for the US constitution
        HashMap<Character, Integer> mapTest = createMap(filename);
        System.out.println("The Frequency Map: \n" + mapTest);
        System.out.println("--------------------------------------------------");

        // visualizing the Huffman encoded tree for the input file
        BinaryTree<HuffmanType> treeTest = createTree(filename);
        System.out.println("The Tree: \n" + treeTest);

        // let's compress the input file
        Compression(filename, "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/compressedConstitution.txt");
        // let's decompress it
        Decompression("/Users/ivyzhang/IdeaProjects/REALcs10/PS3/compressedConstitution.txt", "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/decompressedConstitution.txt", filename);


        // TODO: SMALL TEST CASE
        System.out.println("THE SECOND SMALL TEST CASE \n");
        String filename2 = "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/testText.txt";
        // testing the frequency table(map) with each character's frequency for the small test case
        HashMap<Character, Integer> mapTest2 = createMap(filename2);
        System.out.println("The Frequency Map of the small test case: \n" + mapTest2);
        System.out.println("--------------------------------------------------");

        // visualizing the Huffman encoded tree for the input file
        BinaryTree<HuffmanType> treeTest2 = createTree(filename2);
        System.out.println("The tree for my small test case: \n" + treeTest2);

        // let's compress the input file
        Compression(filename2, "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/compressedTestCase.txt");
        // let's decompress it
        Decompression("/Users/ivyzhang/IdeaProjects/REALcs10/PS3/compressedTestCase.txt", "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/decompressedTestCase.txt", filename2);

        // TODO: BOUNDARY CASE OF ONLY ONE CHARACTER
        System.out.println("THE BOUNDARY CASE CASE \n");
        String filename3 = "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/boundary.txt";
        // testing the frequency table(map) with each character's frequency for the boundary case
        HashMap<Character, Integer> mapTest3 = createMap(filename3);
        System.out.println("The Frequency Map of the boundary case: \n" + mapTest3);
        System.out.println("--------------------------------------------------");

        // visualizing the Huffman encoded tree for the input file
        BinaryTree<HuffmanType> treeTest3 = createTree(filename3);
        System.out.println("The tree for my small test case: \n" + treeTest3);

        // let's compress the input file
        Compression(filename3, "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/compressedBoundaryCase.txt");
        // let's decompress it
        Decompression("/Users/ivyzhang/IdeaProjects/REALcs10/PS3/compressedBoundaryCase.txt", "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/decompressedBoundaryCase.txt", filename3);

        // Compressed War and peace
//        String filename4 = "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/WarAndPeace.txt";
//        Compression(filename4, "/Users/ivyzhang/IdeaProjects/REALcs10/PS3/compressedWarAndPeace.txt");
    }
}
