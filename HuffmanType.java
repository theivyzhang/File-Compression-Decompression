/**
 * Storage types for Huffman type nodes (Character:Frequency)
 * @author ivyzhang for Spring 2022 CS10
 */

public class HuffmanType{
    public Character character;
    public int frequency;

    // an instance of the huffman node
    public HuffmanType(Character character, int frequency){
        this.character = character;
        this.frequency = frequency;
    }

    // method to get the character
    public char getChar(){
        return character;
    }

    // method to ge the character's frequency
    public int getFreq(){
        return frequency;
    }

    // the toString method that creates a string showing character and corresponding frequency
    public String toString(){
        return ("Character "+character+" : "+ "Frequency "+frequency);
    }
}
