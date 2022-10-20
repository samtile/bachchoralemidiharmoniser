import java.io.*;
import java.util.*;
import javax.swing.*;

public class ChoralesToData{
    
    StringBuffer melody = new StringBuffer();
    StringBuffer harmony = new StringBuffer();
    StringBuffer uniqueChordNums = new StringBuffer();
    int totalFiles = 0;
    String previousMelody = "00";
    String previousAlto = "00";
    String previousTenor = "00";
    String previousBass = "00";
    int chordCount = 0;
    int melodyCount = 0;
    int chordCount2 = 0;
    int uniqueChords = 0;
    int currentUniqueCount=0;
    ArrayList allChords = new ArrayList();
    String melodies[] = new String[100000];
    String chords[] = new String[100000];
    int uniques[] = new int[100000];
    int arrayEntry = 0;
    int fileCount = 0;
    boolean flag = false;
    int desiredKeyNum;
    int currentKeyNum = 0;
    int measures = 0;
    double ll = 0;
    
    boolean transposeAll;
    char melodyKey;
    char melodyKey2;
    char melodyPolarity;
    String fileName;
    String userMelody;
    String folderName;
    
    public ChoralesToData(String folderName){
        this.folderName = folderName;
    }
    
    public void harmanise(String mel, String key1, String key2, String pol, int trans, String fName) {
        userMelody = convertFromGerman(mel);
        melodyKey = key1.charAt(0);
        melodyKey2 = key2.charAt(0);
        melodyPolarity = pol.charAt(0);
        if(trans == 1) {
            transposeAll = true;
        }
        else {
            transposeAll = false;
        }
        fileName = fName;
        if(melodyPolarity == 'M')
            melodyPolarity = 'd';
        
        desiredKeyNum = findKeyNum(melodyKey,melodyKey2);
        
        try{
            File f = new File(folderName);
            File data[] = f.listFiles();
            
            int cont = 0;
            if(data.length>100 && transposeAll == true){
                // can crash if too much training data
                String badInput = ("Warning! May be out of memory if training set is greater than 100. Continue?");
                cont = JOptionPane.showConfirmDialog(null, badInput);
            }
            
            if(cont == 0){
                // goes through each choral
                for(int i=0;i<data.length;i++){
                    
                    if(data[i].isFile()){
                        FileInputStream is = new FileInputStream(data[i]);
                        InputStreamReader in = new InputStreamReader(is);
                        BufferedReader ir = new BufferedReader(in);
                        FileInputStream is2 = new FileInputStream(data[i]);
                        InputStreamReader in2 = new InputStreamReader(is2);
                        BufferedReader ir2 = new BufferedReader(in2);
                        ir.readLine();
                        ir.readLine();
                        ir2.readLine();
                        ir2.readLine();
                        ir2.readLine();
                        String keyInfo = ir.readLine();
                        String keyInfo2;
                        char keyNote = keyInfo.charAt(9);
                        char keyNote2 = keyInfo.charAt(10);
                        char piecePolarity = keyInfo.charAt(11);
                        char piecePolarity2 = keyInfo.charAt(12);
                        currentKeyNum = findKeyNum(keyNote,keyNote2);
                        // m is minor, d is major
                        if(((piecePolarity==melodyPolarity || piecePolarity2==melodyPolarity)
                        && desiredKeyNum == currentKeyNum && transposeAll==false)
                        || (piecePolarity==(melodyPolarity) || piecePolarity2==(melodyPolarity))
                        && transposeAll==true){
                            totalFiles++;
                            chords[arrayEntry] = "0";
                            melodies[arrayEntry] = "0";
                            uniques[arrayEntry] = 0;
                            fileCount++;
                            arrayEntry++;
                            for(int k=0;k<6;k++){
                                ir.readLine();
                                ir2.readLine();
                            }
                            // next line is opening line of chorale
                            String currentLine = ir.readLine();
                            String endOfFile = ir2.readLine();
                            int lineCount = 0;
                            
                            while(endOfFile!=null){
                                ir2.readLine();
                                endOfFile = ir2.readLine();
                                lineCount++;
                            }
                            // gets all of melody and chord
                            for(int x=0;x<lineCount-3;x++){
                                String addMelody = getMelody(currentLine);
                                String addHarmony = getChord(currentLine);
                                addMelody = transposeMelody(addMelody);
                                addHarmony = transposeHarmony(addHarmony);
                                int addUChordNo = getUniqueChordNum(allChords,addHarmony);
                                melodies[arrayEntry] = addMelody;
                                chords[arrayEntry] = addHarmony;
                                uniques[arrayEntry] = addUChordNo;
                                arrayEntry++;
                                // reads every 2nd line
                                ir.readLine();
                                currentLine = ir.readLine();
                            }
                            uniqueChords = noOfUniqueChords(chords);
                        }
                    }
                }
                
                if(totalFiles ==0 ){
                    String noData = ("No chorales fitting the critria were found in training set");
                    JOptionPane.showMessageDialog(null, noData);
                }else{
                    int[] notes = convertUserInputToArray(userMelody);
                    
                    int[] melodiesInt = new int[melodies.length];
                    for(int i=0;i<arrayEntry;i++){
                        melodiesInt[i] = Integer.parseInt(melodies[i]);
                    }
                    measures = chordCount + fileCount;
                    
                    // trains off the data and uses viterbi algorithm
                    Train train = new Train(uniqueChords,measures,melodiesInt,uniques,notes);
                    int[] harmony = train.train();
                    ll = train.getLL();
                    String[] harmony2 = new String[harmony.length];
                    for(int i=0;i<harmony.length;i++){
                        harmony2[i] = Integer.toString(harmony[i]);
                    }
                    String[] notes2 = new String[notes.length];
                    for(int i=0;i<notes.length;i++){
                        notes2[i] = Integer.toString(notes[i]);
                    }
                    
                    // creates final midi
                    DataToMidi dM = new DataToMidi(allChords,notes2,harmony2,userMelody,fileName);
                    dM.createMidi();
                }
            }else{}
        }
        catch (IOException e) {
            System.out.println("An IO error occurred!");
            System.out.println(e);
        }
        catch (NumberFormatException e) {
            System.out.println("Incorrect format for input file!");
            System.out.println(e);
        }
    }
    
    public String getMelody(String currentLine){
        // returns melody note in 2 digit format
        String note;
        melodyCount++;
        if(Character.isLetter(currentLine.charAt(18))){
            note = getNote(currentLine,18);
            // incase there is no melody on next beat
            previousMelody = note;
            return note;
        }else{
            return previousMelody;
        }
    }
    
    public String getChord(String currentLine){
        // returns chord in 6 digit format
        // creates an array entry of chord with it's unique number (if chord
        // already in array, then uses old unique number
        String alto;
        String tenor;
        String bass;
        chordCount++;
        
        if(Character.isLetter(currentLine.charAt(25))) {
            alto = getNote(currentLine,25);
        }
        else
            alto = previousAlto;
        if(Character.isLetter(currentLine.charAt(32))) {
            tenor = getNote(currentLine,32);
        }else
            tenor = previousTenor;
        if(Character.isLetter(currentLine.charAt(38))){
            bass = getNote(currentLine,38);
        }else{
            if(Character.isLetter(currentLine.charAt(39))){
                bass = getNote(currentLine,39);
            }else
                bass = previousBass;
        }
        
        StringBuffer chord = new StringBuffer();
        chord.append(alto);
        chord.append(tenor);
        chord.append(bass);
        previousAlto = alto;
        previousTenor = tenor;
        previousBass = bass;
        // incase chord does not change next beat
        
        return chord.toString();
    }
    
    public String getNote(String currentLine, int charNum){
        // returns exact note to either getMelody or getChord method
        StringBuffer note = new StringBuffer();
        note.append(currentLine.charAt(charNum));
        // check for #
        if(Character.getType(currentLine.charAt(charNum+1))==24){
            note.append(currentLine.charAt(charNum+1));
        }else{
            // check for b
            if(Character.getType(currentLine.charAt(charNum+1))==2){
                note.append(currentLine.charAt(charNum+1));
            }
        }
        // negative octave case (ie. found '-')
        if(Character.getType(currentLine.charAt(charNum+2))==20){
            note.append(currentLine.charAt(charNum+2));
            if(Character.isDigit(currentLine.charAt(charNum+3))){
                note.append(currentLine.charAt(charNum+3));
            }
            // non-negative octave
        }else{
            if(Character.isDigit(currentLine.charAt(charNum+2))){
                note.append(currentLine.charAt(charNum+2));
            }
        }
        String numberNote = convertToNumber(note.toString());
        return numberNote;
    }
    
    public int noOfUniqueChords(String[] chords){
        // returns amount of unique chords in string
        ArrayList strings = new ArrayList();
        for(int i=0;i<arrayEntry;i++){
            String string = chords[i];
            if(strings.contains(string)){
                // already contains chord, no action
            }else{
                strings.add(string);
            }
        }
        // -1 accounts for 'x' being counted as a chord
        return strings.size()-1;
    }
    
    public int getUniqueChordNum(ArrayList allChords, String addHarmony){
        // returns chords unique number
        chordCount2 ++;
        for(int i=0;i<allChords.size();i++){
            UniqueChordNum temp = (UniqueChordNum)allChords.get(i);
            if(temp.getChordNum().equals(addHarmony)==true){
                UniqueChordNum newChord = new UniqueChordNum(addHarmony,temp.getUniqueChordNum());
                allChords.add(newChord);
                return temp.getUniqueChordNum();
            }
        }
        UniqueChordNum newChord = new UniqueChordNum(addHarmony,currentUniqueCount+1);
        allChords.add(newChord);
        currentUniqueCount++;
        return currentUniqueCount ;
    }
    
    public int findKeyNum(char key,char key2){
        // finds number related to key
        
        if (key == 'A' && (key2 != '#'&&key2!='b')){return -5;}
        if (key == 'B' || (key == 'A' && key2 == '#')){return -4;}
        if (key == 'H' && (key2 != '#'&&key2!='b')){return -3;}
        if (key == 'C' && (key2 != '#'&&key2!='b')){return -2;}
        if ((key == 'C' && key2 == '#') || (key == 'D' && key2 == 'b')){return -1;}
        if (key == 'D'&& (key2 != '#'&&key2!='b')){return 0;}
        if ((key == 'D' && key2 == '#') || (key == 'E' && key2 == 'b')){return 1;}
        if (key == 'E' && (key2 != '#'&&key2!='b')){return 2;}
        if (key == 'F' && (key2 != '#'&&key2!='b')){return 3;}
        if ((key == 'F' && key2 == '#') || (key == 'G' && key2 == 'b')){return 4;}
        if (key == 'G' && (key2 != '#'&&key2!='b')){return 5;}
        if ((key == 'G' && key2 == '#') || (key == 'A' && key2 == 'b')){return 6;}
        return 10;
    }
    
    public String transposeMelody(String melody){
        // transposes melodies into melodyKey
        int difference = 99;
        int melodyInt = Integer.parseInt(melody);
        if(melodyInt==61)
            // is a rest
            return melody;
        if(desiredKeyNum == currentKeyNum)
            // already correct key - no transpose needed
            return melody;
        
        difference = desiredKeyNum - currentKeyNum;
        
        if(difference>0){
            if(difference<7){
                melodyInt = melodyInt + difference;
            }else{
                melodyInt = melodyInt - (12-difference);
            }
        }
        if(difference<0){
            if(difference<7){
                melodyInt = melodyInt + difference;
            }else{
                melodyInt = melodyInt + (12+difference);
            }
        }
        if(melodyInt<4)
            melodyInt = melodyInt + 12;
        if(melodyInt<10){
            StringBuffer temp = new StringBuffer();
            temp.append('0');
            temp.append(Integer.toString(melodyInt));
            return temp.toString();
        }
        return Integer.toString(melodyInt);
    }
    
    public String transposeHarmony(String harmony){
        // transposes harmonies into melodyKey
        String alto = harmony.substring(0,2);
        String tenor = harmony.substring(2,4);
        String bass = harmony.substring(4,6);
        
        alto = transposeMelody(alto);
        tenor = transposeMelody(tenor);
        bass = transposeMelody(bass);
        
        StringBuffer chord = new StringBuffer();
        chord.append(alto);
        chord.append(tenor);
        chord.append(bass);
        
        return chord.toString();
    }
    
    public int[] convertUserInputToArray(String userMelody){
        // converts the user supplied melody into an int array used for training
        int[] allNotes = new int[500];
        int currentNotePosition = 0;
        
        for(int i=0;i<userMelody.length();i++){
            StringBuffer currentNote = new StringBuffer();
            while(userMelody.charAt(i)!=':'){
                currentNote.append(userMelody.charAt(i));
                i++;
            }
            String intString = convertToNumber(currentNote.toString());
            int intNote = Integer.parseInt(intString);
            i++;
            StringBuffer noteLength = new StringBuffer();
            while(userMelody.charAt(i)!=','&&userMelody.charAt(i)!=';'){
                noteLength.append(userMelody.charAt(i));
                i++;
            }
            int duration;
            if(noteLength.toString().equals("0.125")) duration = 1;else
                if(noteLength.toString().equals("0.25")) duration = 2;else
                    if(noteLength.toString().equals("0.375")) duration = 3;else
                        if(noteLength.toString().equals("0.5")) duration = 4;else
                            if(noteLength.toString().equals("0.625")) duration = 5;else
                                if(noteLength.toString().equals("0.75")) duration = 6;else
                                    if(noteLength.toString().equals("0.875")) duration = 7;else
                                        if(noteLength.toString().equals("1")) duration = 8;else
                                            if(noteLength.toString().equals("1.125")) duration = 9;else
                                                if(noteLength.toString().equals("1.25")) duration = 10;else
                                                    if(noteLength.toString().equals("1.375")) duration = 11;else
                                                        if(noteLength.toString().equals("1.5")) duration = 12;else
                                                            if(noteLength.toString().equals("1.625")) duration = 13;else
                                                                if(noteLength.toString().equals("1.75")) duration = 14;else
                                                                    if(noteLength.toString().equals("1.875")) duration = 15;else
                                                                        if(noteLength.toString().equals("2")) duration = 16;else
                                                                            if(noteLength.toString().equals("2.125")) duration = 17;else
                                                                                if(noteLength.toString().equals("2.25")) duration = 18;else
                                                                                    if(noteLength.toString().equals("2.375")) duration = 19;else
                                                                                        if(noteLength.toString().equals("2.5")) duration = 20;else
                                                                                            if(noteLength.toString().equals("2.625")) duration = 21;else
                                                                                                if(noteLength.toString().equals("2.75")) duration = 22;else
                                                                                                    if(noteLength.toString().equals("2.875")) duration = 23;else
                                                                                                        if(noteLength.toString().equals("3")) duration = 24;else
                                                                                                            duration=99;
            
            for(int j=0;j<duration;j++){
                allNotes[currentNotePosition] = intNote;
                currentNotePosition++;
            }
        }
        int[] allNotes2 = new int[currentNotePosition];
        
        for(int i=0;i<currentNotePosition;i++)
            allNotes2[i] = allNotes[i];
        
        return allNotes2;
    }
    
    public String convertToNumber(String note){
        // converts chorale note form into matlab 2 digit note
        if(note.equals("A-2")){return "01";}
        if(note.equals("A#-2")||note.equals("B-2")){return "02";}
        if(note.equals("H-2")){return "03";}
        if(note.equals("C-1")||note.equals("H#-2")){return "04";}
        if(note.equals("C#-1")||note.equals("Db-1")){return "05";}
        if(note.equals("D-1")){return "06";}
        if(note.equals("D#-1")||note.equals("Eb-1")){return "07";}
        if(note.equals("E-1")){return "08";}
        if(note.equals("F-1")||note.equals("E#-1")){return "09";}
        if(note.equals("F#-1")||note.equals("Gb-1")){return "10";}
        if(note.equals("G-1")){return "11";}
        if(note.equals("G#-1")||note.equals("Ab-1")){return "12";}
        if(note.equals("A-1")){return "13";}
        if(note.equals("A#-1")||note.equals("B-1")){return "14";}
        if(note.equals("H-1")){return "15";}
        if(note.equals("C0")||note.equals("H#-1")){return "16";}
        if(note.equals("C#0")||note.equals("Db0")){return "17";}
        if(note.equals("D0")){return "18";}
        if(note.equals("D#0")||note.equals("Eb0")){return "19";}
        if(note.equals("E0")){return "20";}
        if(note.equals("F0")||note.equals("E#0")){return "21";}
        if(note.equals("F#0")||note.equals("Gb0")){return "22";}
        if(note.equals("G0")){return "23";}
        if(note.equals("G#0")||note.equals("Ab0")){return "24";}
        if(note.equals("A0")){return "25";}
        if(note.equals("A#0")||note.equals("B0")){return "26";}
        if(note.equals("H0")){return "27";}
        if(note.equals("C1")||note.equals("H#0")){return "28";}
        if(note.equals("C#1")||note.equals("Db1")){return "29";}
        if(note.equals("D1")){return "30";}
        if(note.equals("D#1")||note.equals("Eb1")){return "31";}
        if(note.equals("E1")){return "32";}
        if(note.equals("F1")||note.equals("E#1")){return "33";}
        if(note.equals("F#1")||note.equals("Gb1")){return "34";}
        if(note.equals("G1")){return "35";}
        if(note.equals("G#1")||note.equals("Ab1")){return "36";}
        if(note.equals("A1")){return "37";}
        if(note.equals("A#1")||note.equals("B1")){return "38";}
        if(note.equals("H1")){return "39";}
        if(note.equals("C2")||note.equals("H#1")){return "40";}
        if(note.equals("C#2")||note.equals("Db2")){return "41";}
        if(note.equals("D2")){return "42";}
        if(note.equals("D#2")||note.equals("Eb2")){return "43";}
        if(note.equals("E2")){return "44";}
        if(note.equals("F2")||note.equals("E#2")){return "45";}
        if(note.equals("F#2")||note.equals("Gb2")){return "46";}
        if(note.equals("G2")){return "47";}
        if(note.equals("G#2")||note.equals("Ab2")){return "48";}
        if(note.equals("A2")){return "49";}
        if(note.equals("A#2")||note.equals("B2")){return "50";}
        if(note.equals("H2")){return "51";}
        if(note.equals("C3")||note.equals("H#2")){return "52";}
        if(note.equals("C#3")||note.equals("Db3")){return "53";}
        if(note.equals("D3")){return "54";}
        if(note.equals("D#3")||note.equals("Eb3")){return "55";}
        if(note.equals("E3")){return "56";}
        if(note.equals("F3")||note.equals("E#3")){return "57";}
        if(note.equals("F#3")||note.equals("Gb3")){return "58";}
        if(note.equals("G3")){return "59";}
        if(note.equals("G#3")||note.equals("Ab3")){return "60";}
        if(note.equals("p")||note.equals("P")){return "61";}
        return "99";
    }
    
    public String convertFromGerman(String mel){
        // converts from German notation
        StringBuffer mel2 = new StringBuffer();
        
        for(int i=0;i<mel.length();i++){
            if(mel.charAt(i)=='B' && mel.charAt(i+1)=='b'){
                mel2.append("B");i++;
            }else if(mel.charAt(i)=='B'){
                mel2.append("H");
            }else{
                mel2.append(mel.charAt(i));
            }
        }
        return mel2.toString();
    }
    public int getTotalFiles(){
        return totalFiles;
    }
    
    public int getMeasures(){
        return measures;
    }
    
    public int getUniqueChords(){
        return uniqueChords;
    }
    
    public double getLL(){
        return ll;
    }
}