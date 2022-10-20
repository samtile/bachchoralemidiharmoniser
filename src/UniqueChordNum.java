import java.util.*;

public class UniqueChordNum {
   // used so that chords can be checked if they have already occured or not
    String chordNum = "888888";
    int uniqueChordNum = 0;
    
    public UniqueChordNum(String cN, int uCN) {
        chordNum = cN;
        uniqueChordNum = uCN;
    }
    public String getChordNum(){
        return chordNum;
    }
     public int getUniqueChordNum(){
        return uniqueChordNum;
    }
}