import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;
import java.lang.Math;

class LZpack{
    public static void main(String args []){
        BufferedInputStream bis = new BufferedInputStream(System.in);
        ArrayList <Byte> ba = new ArrayList<Byte>();
        try{
            FileOutputStream fos = new FileOutputStream(new File("packed.txt"));
            int i;   //used for storing the data read from file
            byte b;   //used for storing the data that converted from the i
            int packNum = 0;   //used for packing phrase number and mismatch char
            int pn = 0, misnum = 0;   //used for storing phrase number and mismatch char
            int lenInPackNum = 0;   //used for tracking the length of the packNum
            int lenpn = 0;   //used for storing the length of the phrase number
            int numInTrie = 0;  //tracking how many entries in trie currently
            //start reading data
            while((i = bis.read()) != -1){
                b = (byte)i;
                //store the data into the array list
                ba.add(b);
                System.out.println((char)b);
                //when the current data is \n then read the next one after it, if it's still \n, meaning last \n is mismatch, then extract the phrase number and mismatch
                if(i == 10){
                    i = bis.read();
                    //if the next one after last \n is \n
                    if(i == 10){
                        System.out.println("New Line 1");
                        ba.add((byte)i);
                        //get phrase number
                        pn = extractNum(ba);
                        System.out.println("Phrase num: " + pn);
                        //calculate how many bits phrase number needs
                        if(numInTrie < 2){
                            lenpn = 1;
                        }else{
                            lenpn = log(numInTrie) + 1;
                        }
                        System.out.println("*******BITS SHOULD SHIFT: " + lenpn);
                        //bit-wise OR with the packNum
                        if(lenInPackNum == 0){
                            packNum |= pn;
                            lenInPackNum += lenpn;
                            System.out.println("After packing pn with packNum: " + packNum + " Length: " + lenInPackNum);
                        }else if((lenpn + lenInPackNum) > 31){
                            output(fos, packNum, 2);
                            lenInPackNum -= 16;
                            packNum = packNum >> 16;
                            //if after outputting 2 bytes, the int still cannot fit the phrase number in, then output one more byte
                            if((lenpn + lenInPackNum) > 31){
                                System.out.println("EXCEED THE MAXMIUM LENGTH: " + lenInPackNum);
                                output(fos, packNum, 1);
                                lenInPackNum -= 8;
                                packNum >>= 8;
                                pn <<= lenInPackNum;
                                packNum |= pn;
                                lenInPackNum += lenpn;
                            }else{
                                pn = pn << lenInPackNum;
                                packNum |= pn;
                                lenInPackNum += lenpn;
                            }
                            System.out.println("Output right-most 2 bytes: " + packNum + " Length: " + lenInPackNum);
                        }else{
                            pn = pn << lenInPackNum;
                            packNum |= pn;
                            lenInPackNum += lenpn;
                            System.out.println("After packing pn with packNum: " + packNum + " Length: " + lenInPackNum);
                        }
                        
                        //get mismatch char
                        misnum = extractChar(ba);
                        System.out.println("Mismatch: " + misnum);
                        //shift the mismatch along to left by the length of phrase num and put mismatch in
                        if((8 + lenInPackNum) > 31){
                            output(fos, packNum, 2);
                            lenInPackNum -= 16;
                            packNum = packNum >> 16;
                            misnum = misnum << lenInPackNum;
                            packNum |= misnum;
                            lenInPackNum += 8;
                            System.out.println("Output right-most 2 bytes: " + packNum + " Length: " + lenInPackNum);
                        }else{
                            misnum = misnum << lenInPackNum;
                            packNum |= misnum;
                            lenInPackNum += 8;
                            System.out.println("After packing mismatch with packNum: " + packNum + " Length: " + lenInPackNum);
                        }
                        numInTrie++;
                        ba.clear();
                    }else if(i == -1){   //next data after \n is the end of the file or data, then break the loop
                        System.out.println("End of file");
                        break;
                    }else{   //next data it not either \n nor end of the file
                        System.out.println("New line 2");
                        //get phrase number first
                        pn = extractNum(ba);
                        System.out.println("Phrase num: " + pn);
                        if(numInTrie < 2){
                            lenpn = 1;
                        }else{
                            lenpn = log(numInTrie) + 1;
                        }
                        System.out.println("******BITS SHOULD SHIFT: " + lenpn);
                        //lenpn = shiftNum(pn);
                        if(lenInPackNum == 0){
                            packNum |= pn;
                            lenInPackNum += lenpn;
                            System.out.println("After packing pn with packNum: " + packNum + " Length: " + lenInPackNum);
                        }else if((lenpn + lenInPackNum) > 31){
                            output(fos, packNum, 2);
                            lenInPackNum -= 16;
                            packNum = packNum >> 16;
                            //if after outputting 2 bytes, the int still cannot fit the phrase number in, then output one more byte
                            if((lenpn + lenInPackNum) > 31){
                                System.out.println("EXCEED THE MAXMIUM LENGTH: " + lenInPackNum);
                                output(fos, packNum, 1);
                                lenInPackNum -= 8;
                                packNum >>= 8;
                                pn <<= lenInPackNum;
                                packNum |= pn;
                                lenInPackNum += lenpn;
                            }else{
                                pn = pn << lenInPackNum;
                                packNum |= pn;
                                lenInPackNum += lenpn;
                            }
                            System.out.println("Output right-most 2 bytes: " + packNum + " Length: " + lenInPackNum);
                        }else{
                            pn = pn << lenInPackNum;
                            packNum |= pn;
                            lenInPackNum += lenpn;
                            System.out.println("After packing pn with packNum: " + packNum + " Length: " + lenInPackNum);
                        }
                        //get mismatch char
                        misnum = extractChar(ba);
                        System.out.println("Mismatch: " + misnum);
                        if((8 + lenInPackNum) > 31){
                            output(fos, packNum, 2);
                            lenInPackNum -= 16;
                            packNum = packNum >> 16;
                            misnum = misnum << lenInPackNum;
                            packNum |= misnum;
                            lenInPackNum += 8;
                            System.out.println("Output right-most 2 bytes: " + packNum + " Length: " + lenInPackNum);
                        }else{
                            misnum = misnum << lenInPackNum;
                            packNum |= misnum;
                            lenInPackNum += 8;
                            System.out.println("After packing mismatch with packNum: " + packNum + " Length: " + lenInPackNum);
                        }
                        numInTrie++;
                        //need to bit pack the phrase num and mismatch
                        ba.clear();
                        ba.add((byte)i);
                        System.out.println("Next line first char: " + (char)(byte)i);
                    }
                }
            }
            //print out all the bytes in the array list
            if(ba.size() != 0){
                //extract the phrase number
                if(ba.get(ba.size() - 2) >= 48 && ba.get(ba.size() - 2) <= 57){
                    pn = 0;
                    int len = ba.size();
                    int a = 2;
                    for(int x=0; x<ba.size()-1; x++){
                        if(len > 0){
                            System.out.println(String.valueOf((char)ba.get(x).byteValue()));
                            pn += Integer.parseInt(String.valueOf((char)ba.get(x).byteValue())) * Math.pow(10, ba.size() - a);
                            a++;
                            len--;
                        }
                    }
                    System.out.println("Phrase num: " + pn);
                    //pack the phrase number
                    if((lenpn + lenInPackNum) > 31){
                        output(fos, packNum, 2);
                        lenInPackNum -= 16;
                        packNum = packNum >> 16;
                        pn = pn << lenInPackNum;
                        packNum |= pn;
                        lenInPackNum += lenpn;
                        System.out.println("1 Len: " + lenInPackNum);
                    }else{
                        pn = pn << lenInPackNum;
                        packNum |= pn;
                        lenInPackNum += lenpn;
                        System.out.println("2 Len: " + lenInPackNum);
                    }
                }else{
                    //extract the phrase number
                    pn = extractNum(ba);
                    System.out.println("Phrase num: " + pn);
                    //pack the phrase number
                    if((lenpn + lenInPackNum) > 31){
                        output(fos, packNum, 2);
                        lenInPackNum -= 16;
                        packNum = packNum >> 16;
                        pn = pn << lenInPackNum;
                        packNum |= pn;
                        lenInPackNum += lenpn;
                        System.out.println("1 Len: " + lenInPackNum);
                    }else{
                        pn = pn << lenInPackNum;
                        packNum |= pn;
                        lenInPackNum += lenpn;
                        System.out.println("2 Len: " + lenInPackNum);
                    }
                    //extract the mismatch
                    misnum = extractChar(ba);
                    System.out.println("Mismatch: " + misnum);
                    //pack the mismatch
                    if((8 + lenInPackNum) > 31){
                        output(fos, packNum, 2);
                        lenInPackNum -= 16;
                        packNum = packNum >> 16;
                        misnum = misnum << lenInPackNum;
                        packNum |= misnum;
                        lenInPackNum += 8;
                        System.out.println("1 mis Len: " + lenInPackNum);
                    }else{
                        misnum = misnum << lenInPackNum;
                        packNum |= misnum;
                        lenInPackNum += 8;
                        System.out.println("2 mis Len:: " + lenInPackNum);
                    }
                }
            }
            System.out.println("Len in packNum: " + lenInPackNum);
            //print all the remaining bits in the int out
            while(lenInPackNum > 0){
                output(fos, packNum, 2);
                lenInPackNum -= 16;
                packNum = packNum >> 16;
            }
            fos.close();
            //////////////////////For debugging
            System.out.println("bits number: " + log(4));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    //extracting the phrase number
    private static int extractNum(ArrayList <Byte> ar){
        System.out.println("Length: " + ar.size());
        int pkbit = 0;
        int len = ar.size() - 2;
        int a = 3;
        for(int x=0; x<ar.size() - 2; x++){   //loop through all the items in array except \n & mismatch
            if(len > 0){
                pkbit += Integer.parseInt(String.valueOf((char)ar.get(x).byteValue())) * Math.pow(10, ar.size() - a);
                a++;
                len--;
            }
        }
        return pkbit;
    }
    
    //extracting the mismatch
    private static int extractChar(ArrayList <Byte> ar){
        return (int)ar.get(ar.size() - 2).byteValue();
    }
    
    //get how many bits needed to shift
    private static int shiftNum(int num){
        int len = 0;
        if(num == 0){
            return len = 1;
        }
        while(num != 0){
            num /= 2;
            len++;
        }
        return len;
    }
    
    //get how many bits needed to shift
    private static int log(int num){
        return (int)(Math.log(num) / Math.log(2));
    }
    
    //output the bytes
    private static void output(FileOutputStream writer, int text, int times) throws Exception{
        int index = 0;
        byte [] array = new byte[times];
        while(index < times){
            array[index] = (byte)text;
            text = text >> 8;
            index++;
        }
        writer.write(array);
    }
}
