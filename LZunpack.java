import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.Math;
import java.util.*;

class LZunpack{
    public static void main(String args[]){
        try{
            FileInputStream fis = new FileInputStream(new File("packed.txt"));  //open the file that stored all packed data
            int i, result, next = 0;  //i for storing data read from file, result for storing number to be used to mask bits, next for reading data from file when special case occurred
            int lenInI = 0;  //for indicating how many bits already occupied in int
            int numInTrie = 0;  //tracking how many entries in the trie currently
            int pn, mismatch;  //pn for phrase number, mismatch for mis match char
            int bits = 0;  //calculating how many bits for the phrase number should be extracted
            while((i = fis.read()) != -1){
                //extract the right-most 8 bits
                if(lenInI != 0){
                    i = next | ((i & 255) << lenInI);
                }else{
                    i = i & 255;
                }
                //increment the lengh by 8
                lenInI += 8;
                //check whether the amount of entries is less than 2, if so, then the bit we need to mask is 1, other wise need to use logarithm to calculate
                if(numInTrie < 2){
                    //extract the phrase number
                    pn = i & 1;
                    lenInI -= 1;
                    //if the length in the in is less than 8, then need to read more data from the file
                    if(lenInI < 8){
                        next = fis.read();
                        if(next == -1){
                            break;
                        }else{
                            i >>= 1;
                            i |= (next << 7);
                            lenInI += 8;
                        }
                    }else{
                        i >>= 1;
                    }
                    //extract the mis match char
                    mismatch = i & 255;
                    next = (i >> 8);
                    lenInI -= 8;
                    System.out.println(pn + "" + (char)(byte)mismatch);
                    //increment the number of the entry in the trie
                    numInTrie++;
                }else{
                    //calculate how many bits we need to extract
                    bits = log(numInTrie) + 1;
                    //checking the lenght in the int whether is less the bits we need to mask
                    if(lenInI < bits){
                        //if so, read more data
                        next = fis.read();
                        if(next == -1){
                            break;
                        }else{
                            next <<= lenInI;
                            i |= next;
                            lenInI += 8;
                            //this is a special case, when the length in the int is still less the bits we need to extract after reading one more byte, so we need to read one more
                            if(lenInI < bits){
                                next = fis.read();
                                if(next == -1)
                                    break;
                                next <<= lenInI;
                                i |= next;
                                lenInI += 8;
                            }
                        }
                    }
                    //extract the phrase number
                    pn = i & maskNum(bits);
                    //decrement the length in the int
                    lenInI -= bits;
                    //shift the int to the right
                    i >>= bits;
                    
                    //checking the length in the int whether is less than the 8-bits
                    if(lenInI < 8){
                        next = fis.read();
                        if(next == -1){
                            break;
                        }else{
                            i |= next << lenInI;
                            lenInI += 8;
                        }
                    }
                    //extract the mismatch char
                    mismatch = i & 255;
                    //shift the int to the right by 8-bits and assign the value to next so that i can be used to read new data without overridding the value in i
                    next = (i >> 8);
                    //decrement the length in int by 8
                    lenInI = lenInI - 8;
                    //this is a special case, after extracting the mismatch, there could the case that the length in the int is 0, then we need to read some data and store it into int to avoid that error
                    if(lenInI == 0){
                        i = fis.read();
                        if(i == -1){
                            break;
                        }
                        i = i & 255;
                        lenInI += 8;
                        next = i;
                    }
                    //print the extracted phrase number and mis match char out
                    System.out.println(pn + "" + (char)(byte)mismatch);
                    //increment the number of entries by 1
                    numInTrie++;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //calculate what integer should be used to get according bits number
    private static int maskNum(int bits){
        int result = 0;
        for(int i=0; i<bits; i++){
            result += Math.pow(2, i);
        }
        return result;
    }
    
    //logarithm for log2()
    private static int log(int num){
        return (int)(Math.log(num) / Math.log(2));
    }
}
