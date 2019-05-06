import java.io.*;
import java.util.*;

class LZdecode{
    private static ArrayList<Integer> arrayList;
    private static int b;
    private static ArrayList<Integer> phraseNumList;
    private static ArrayList<Byte> mismatchList;
    private static ArrayList<Byte> outputList;
    private static int key;
    private static byte[] outputArray;
    private static ArrayList<Byte>  findByteList;
    public static void main(String agrs []){
        try{
            // Declare all the variables
            int curr = 0;
            int next = 0;
            int phraseNum = 0;
            byte mismatch = (byte) 0;
            int arraySize = 0;
            BufferedInputStream bis = new BufferedInputStream(System.in);
            arrayList = new ArrayList<Integer> ();
            String s = "";
            String str = "";
            phraseNumList = new ArrayList<Integer>(); //Store the phrase numbers
            phraseNumList.add(0);
            mismatchList = new ArrayList<Byte> (); // Store the mismatches
            mismatchList.add((byte)0);
            outputList = new ArrayList<Byte> ();
            findByteList = new ArrayList<Byte> (); // Store the found bytes when calling the find() method
            int tmp, lastElement= 0;
            while ((curr=bis.read())!=-1){  //Read one byte each time and if it is not the end of the file
                b = curr;
                arrayList.add(b);    //Add the current byte into the arrayList
                if(curr == 10){      //IF it meets '\n'
                    s = "";
                    if ((next=bis.read())!=-1){   // read the next byte
                        int nextByte = next;
                        arrayList.add(nextByte);  // Add it into the arrayList
                        arraySize = arrayList.size();
                        if (nextByte == 10){      //IF the next input is also '\n', it means that the mismatch byte is '\n'
                            for(int i = 0; i < (arraySize - 2);i++){
                                int c = arrayList.get(i);
                                s = s+(char)c;   //Get the phraseNum string
                            }
                            tmp = arrayList.get(arraySize-2);  //Get the mismatch from the arrayList
                            mismatch = (byte) tmp;
                            arrayList.clear();      // Clear the arraylist
                        }
                        else{    //IF the next byte is a number, which means a new line starts
                            lastElement = arrayList.get(arraySize-1);   //Store the last element from the arraylist
                            for(int i = 0; i < (arraySize - 3);i++){
                                int c = arrayList.get(i);
                                s = s+(char)c;      //Get the phraseNum string
                            }
                            tmp = arrayList.get(arraySize-3);
                            mismatch = (byte) tmp;    //Get the mismatch from the arrayList
                        }
                        arrayList.clear();           // Clear the arraylist
                        arrayList.add(lastElement);  // Add the last element into the empty arraylist as the first element of next round
                    }else break;  // IF it is the end of the file, then break
                    phraseNum = Integer.parseInt (s);  // Convert the phraseNum string into an integer
                    key = phraseNum;       //Copy the phraseNum into another integer named key
                    if (key !=0){          // IF key is not 0, call the find() method
                        for(int i = find().size()-1;i>=0;i--){   //Loop through the list
                            outputList.add(find().get(i));   // Add the current byte into the outputList
                        }
                        findByteList.clear();
                    }
                    outputList.add(mismatch);  // Add the mismatch into the outputList
                    phraseNumList.add(phraseNum);   // Record the phraseNum
                    mismatchList.add(mismatch);   // Record the mismatch
                    if (mismatch == 10){
                        arrayList.clear();
                    }
                    phraseNum = 0;
                }
            }
            arraySize = arrayList.size();
            s="";
            if(arraySize != 0 ){              // After finishing reading the file, if the arraySize is not 0, which means there is still something which has not been output yet
                tmp = arrayList.get(arraySize-2);
                if (tmp < 48 || tmp > 57){    // If the second last element is not an integer, which means it is a mismatch
                    for(int i = 0; i < arraySize - 2 ;i++){
                        int c = arrayList.get(i);
                        s = s+(char)c;        //Get the phraseNum string
                    }
                }
                else {   // IF the second last element is an integer, then the last element is the mismatch
                    for(int i = 0; i < arraySize - 1 ;i++){
                        int c = arrayList.get(i);
                        s = s+(char)c;         //Get the phraseNum string
                    }
                    tmp = arrayList.get(arraySize - 1);
                }
                phraseNum = Integer.parseInt (s);  // Convert the phraseNum string into an integer
                key = phraseNum;
                mismatch = (byte)tmp;
                for(int i =  findByteList.size() -1;i>=0;i--){    //Loop through the list
                    outputList.add(find().get(i));  // Add the current byte into the outputList
                }
                outputList.add(mismatch);   //Add the mismatch to the outputList
            }
            outputArray = getArray();       //Get the final output array
            System.out.write(outputArray);  //Output the byte array
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    // This method returns a byte arraylist.
    public static ArrayList<Byte> find (){
        byte b = mismatchList.get(key);
        findByteList.add(b);        //Add the byte of the current key into the list.
        key = phraseNumList.get(key);  //Move the key to the next position
        while (key != 0){              //While the key is not 0, call the find() method
            find ();
        }
        return  findByteList;
    }
    
    // This method returns a byte array
    public static byte[] getArray() {
        byte[] bytes = new byte[outputList.size()];  //Create a byte array as the same size as outputList
        for (int i = 0; i< outputList.size();i++){
            bytes[i] = outputList.get(i);             //Copy all the bytes from outputList to the byte array
        }
        return bytes;
    }
}
