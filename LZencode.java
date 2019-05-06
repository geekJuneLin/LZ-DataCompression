import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.io.File;
import java.io.*;
class LZencode {
    //Create the root of the Trie
    private static Trie root = new Trie();
    private static Trie curr = root;
    private static Trie parent;
    private static int phraseNum = 0;
    private static int trieKey = 1;
    private static byte b_input;
    public static void main (String args[]){
        BufferedInputStream bis = new BufferedInputStream(System.in); // Read in the file
        try {
            int i;
            while ((i=bis.read())!=-1){   // IF it is not the end of the file
                b_input = (byte)i;     //Get the current byte
                int index = curr.find (b_input);  // Call the find method in the trie to get the current input byte
                if (index == -1){   // IF it is not found
                    if (curr == root){    // IF it is at the root position, the phraseNum equals to 1
                        phraseNum = 0;
                    }
                    printOut(phraseNum,b_input);  //Output the phraseNum and mismatch as a pair
                    curr.add(trieKey, b_input);   // Call the add method in the trie to add the current byte into the trie at the
                    trieKey++;   // Increment the key number by 1
                    parent = curr; //Move the parent node to the curr node position
                    curr = root;  // Point back to the root
                }
                else {
                    parent = curr;        //IF it is found,
                    phraseNum = curr.GetIndex(); // phraseNum now becomes to the index number of the found trie
                    curr = curr.GetList().get(index); // Curr points to the found trie
                }
            }
            if(curr != root){     //After finishing reading the file, if curr is not at root, it means that the last byte not output yet
                phraseNum = parent.GetKey();  // The phraseNum will be the key number of the parent trie
                printOut (phraseNum,b_input);
            }
            bis.close();  //Close the input stream
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    // Output the result as a pair in the terminal
    private static void printOut(int phraseNum, byte mismatch){
        System.out.print(phraseNum);
        System.out.write(mismatch);
        System.out.print("\n");
    }
}
