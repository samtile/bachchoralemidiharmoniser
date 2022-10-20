import org.jfugue.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class DataToMidi {
    
    String userMelody;
    String fileName;
    ArrayList melody = new ArrayList();;
    ArrayList harmony = new ArrayList();;
    ArrayList newHarmony = new ArrayList();
    ArrayList allChords;
    
    public DataToMidi(ArrayList a, String[] melodyArray, String[] harmonyArray, String userMelody, String fileName){
        // user melody passed in that form, as array form loses note duration information
        this.userMelody = userMelody;
        allChords = a;
        this.fileName = fileName;
        for(int i=0;i<melodyArray.length;i++){
            melody.add(melodyArray[i]);
        }
        for(int i=0;i<harmonyArray.length;i++){
            harmony.add(harmonyArray[i]);
        }
    }
    
    public void createMidi() {
        
        for(int i=0;i<harmony.size();i++){
            // gets chord numbers in an array, ready for converiosn
            String s= (String)harmony.get(i);
            newHarmony.add(chordFromUnique(s));
        }
        String midiMelody = userMelodyToMidi(userMelody);
        String midiAlto = harmonyToMidiAlto(newHarmony);
        String midiTenor = harmonyToMidiTenor(newHarmony);
        String midiBass = harmonyToMidiBass(newHarmony);
        
        midiAlto = midiAlto.substring(0,midiAlto.length()-1);
        midiTenor = midiTenor.substring(0,midiTenor.length()-1);
        midiBass = midiBass.substring(0,midiBass.length()-1);
        
        midiAlto = convertNoteDuration(midiAlto);
        midiTenor = convertNoteDuration(midiTenor);
        midiBass = convertNoteDuration(midiBass);
        
        StringBuffer restMelody = new StringBuffer();
        StringBuffer restAlto = new StringBuffer();
        StringBuffer restTenor = new StringBuffer();
        StringBuffer restBass = new StringBuffer();
        
        restMelody.append(" R_R_R_");
        restAlto.append(" R_R_R_");
        restTenor.append(" R_R_R_");
        restBass.append(" R_R_R_");
        restMelody.append(midiMelody);
        restAlto.append(midiAlto);
        restTenor.append(midiTenor);
        restBass.append(midiBass);
        midiMelody = restMelody.toString();
        midiAlto = restAlto.toString();
        midiTenor = restTenor.toString();
        midiBass = restBass.toString();
        
        Player player = new Player();
        
        // patterns for melody and harmony
        Pattern melodyPat = new Pattern(midiMelody);
        Pattern altoPat = new Pattern(midiAlto);
        Pattern tenorPat = new Pattern(midiTenor);
        Pattern bassPat = new Pattern(midiBass);
        
        // voices needed so can be played simultaneously
        Pattern voice1 = new Pattern("V0 ");
        voice1.add(melodyPat);
        Pattern voice2 = new Pattern("V1 ");
        voice2.add(altoPat);
        Pattern voice3 = new Pattern("V2 ");
        voice3.add(tenorPat);
        Pattern voice4 = new Pattern("V3 ");
        voice4.add(bassPat);
        
        // voices added to final pattern
        Pattern song = new Pattern();
        song.add(voice1);
        song.add(voice2);
        song.add(voice3);
        song.add(voice4);
        player.save(song,fileName);
        
        String midiComplete = ("File has been saved successfully!");
        JOptionPane.showMessageDialog(null, midiComplete);
    }
    
    public String chordFromUnique(String unique){
        // converts unique chrd number back to chord using allChords
        for(int i=0;i<allChords.size();i++){
            UniqueChordNum u = (UniqueChordNum)allChords.get(i);
            int t = u.getUniqueChordNum();
            String currentChordNum = Integer.toString(t);
            if(unique.equals(currentChordNum)){
                return u.getChordNum();
            }
        }return "999999";
    }
    
    public String userMelodyToMidi(String userMelody){
        // converts user melody into JFugue format
        StringBuffer newMelody = new StringBuffer();
        boolean digitIsOctave = true;
        for(int i=0;i<userMelody.length();i++){
            if(Character.isDigit(userMelody.charAt(i))&&digitIsOctave==true){
                // octave is 4 higher in JFugue
                String tempDigit = Character.toString(userMelody.charAt(i));
                int octave = Integer.parseInt(tempDigit);
                octave = octave + 4;
                tempDigit = String.valueOf(octave);
                newMelody.append(tempDigit);
                digitIsOctave = false;
                
            }else if(userMelody.charAt(i)==':'){
                newMelody.append('/');
                
            }else if(userMelody.charAt(i)==';'){
                return newMelody.toString();
                
            }else if(userMelody.charAt(i)==','){
                digitIsOctave = true;
                newMelody.append('_');
                
            }else if(userMelody.charAt(i)=='B'){
                newMelody.append('B');
                newMelody.append('b');
                
            }else if(userMelody.charAt(i)=='H'){
                newMelody.append('B');
            }else{
                newMelody.append(Character.toString(userMelody.charAt(i)));
            }
        }
        return newMelody.toString();
    }
    
    public String melodyToMidi(ArrayList melody){
        StringBuffer temp = new StringBuffer();
        for(int i=0;i<melody.size();i++){
            String note = (String)melody.get(i);
            note = getNote(note);
            temp.append(note);
            temp.append("_");
        }
        return temp.toString();
    }
    
    public String harmonyToMidiAlto(ArrayList newHarmony){
        StringBuffer temp = new StringBuffer();
        for(int i=0;i<newHarmony.size();i++){
            String chord = (String)newHarmony.get(i);
            String alto = chord.substring(0,2);
            alto = getNote(alto);
            temp.append(alto);
            temp.append("_");
        }
        return temp.toString();
    }
    
    public String harmonyToMidiTenor(ArrayList newHarmony){
        StringBuffer temp = new StringBuffer();
        for(int i=0;i<newHarmony.size();i++){
            String chord = (String)newHarmony.get(i);
            String tenor = chord.substring(2,4);
            tenor = getNote(tenor);
            temp.append(tenor);
            temp.append("_");
        }
        return temp.toString();
    }
    
    public String harmonyToMidiBass(ArrayList newHarmony){
        StringBuffer temp = new StringBuffer();
        for(int i=0;i<newHarmony.size();i++){
            String chord = (String)newHarmony.get(i);
            String bass = chord.substring(4,6);
            bass = getNote(bass);
            temp.append(bass);
            temp.append("_");
        }
        return temp.toString();
    }
    
    public String convertNoteDuration(String melody){
        // converts music string to music string with note durations changed
        ArrayList notes = new ArrayList();
        
        for(int i=0;i<melody.length();i++){
            StringBuffer currentNote = new StringBuffer();
            while(melody.charAt(i)!='_'){
                currentNote.append(melody.charAt(i));
                i++;
                if(i==melody.length())
                    break;
            }
            notes.add(currentNote.toString());
        }
        StringBuffer finalMelody = new StringBuffer();
        String thisNote = "none";
        String nextNoteToAdd = "none";
        int duration = 0;
        
        // 1st note
        nextNoteToAdd = (String)notes.get(0);
        duration = 1;
        
        // the rest of them
        for(int i=1;i<notes.size();i++){
            thisNote = (String)notes.get(i);
            if(i==notes.size()-1 && thisNote.equals(nextNoteToAdd)){
                duration ++;
                finalMelody.append(addNote(nextNoteToAdd, duration));
            }else if(i==notes.size()-1){
                finalMelody.append(addNote(nextNoteToAdd, duration));
                finalMelody.append("_");
                finalMelody.append(addNote(thisNote, 1));
            }else if(thisNote.equals(nextNoteToAdd)){
                duration++;
            }else{
                finalMelody.append(addNote(nextNoteToAdd, duration));
                finalMelody.append("_");
                nextNoteToAdd = (String)notes.get(i);
                duration = 1;
            }
        }
        return finalMelody.toString();
    }
    
    public String addNote(String note, int duration){
        // makes a note length duration and returns it
        StringBuffer returnNote = new StringBuffer();
        returnNote.append(note);
        if(duration==1)
            returnNote.append("/0.125");
        if(duration==2)
            returnNote.append("/0.25");
        if(duration==3)
            returnNote.append("/0.375");
        if(duration==4)
            returnNote.append("/0.5");
        if(duration==5)
            returnNote.append("/0.625");
        if(duration==6)
            returnNote.append("/0.75");
        if(duration==7)
            returnNote.append("/0.875");
        if(duration==8)
            returnNote.append("/1");
        if(duration==9)
            returnNote.append("/1.125");
        if(duration==10)
            returnNote.append("/1.25");
        if(duration==11)
            returnNote.append("/1.375");
        if(duration==12)
            returnNote.append("/1.5");
        if(duration==13)
            returnNote.append("/1.625");
        if(duration==14)
            returnNote.append("/1.75");
        if(duration==15)
            returnNote.append("/1.875");
        if(duration==16)
            returnNote.append("/2");
        if(duration==17)
            returnNote.append("/2.125");
        if(duration==18)
            returnNote.append("/2.25");
        if(duration==19)
            returnNote.append("/2.375");
        if(duration==20)
            returnNote.append("/2.5");
        if(duration==21)
            returnNote.append("/2.625");
        if(duration==22)
            returnNote.append("/2.75");
        if(duration==23)
            returnNote.append("/2.875");
        if(duration==24)
            returnNote.append("/3");
        
        return returnNote.toString();
    }
    
    public String getNote(String note){
        if(note.equals("01")){return "A2";}
        if(note.equals("02")){return "A#2";}
        if(note.equals("03")){return "B2";}
        if(note.equals("04")){return "C3";}
        if(note.equals("05")){return "C#3";}
        if(note.equals("06")){return "D3";}
        if(note.equals("07")){return "D#3";}
        if(note.equals("08")){return "E3";}
        if(note.equals("09")){return "F3";}
        if(note.equals("10")){return "F#3";}
        if(note.equals("11")){return "G3";}
        if(note.equals("12")){return "G#3";}
        if(note.equals("13")){return "A3";}
        if(note.equals("14")){return "A#3";}
        if(note.equals("15")){return "B3";}
        if(note.equals("16")){return "C4";}
        if(note.equals("17")){return "C#4";}
        if(note.equals("18")){return "D4";}
        if(note.equals("19")){return "D#4";}
        if(note.equals("20")){return "E4";}
        if(note.equals("21")){return "F4";}
        if(note.equals("22")){return "F#4";}
        if(note.equals("23")){return "G4";}
        if(note.equals("24")){return "G#4";}
        if(note.equals("25")){return "A4";}
        if(note.equals("26")){return "A#4";}
        if(note.equals("27")){return "B4";}
        if(note.equals("28")){return "C5";}//middle c
        if(note.equals("29")){return "C#5";}
        if(note.equals("30")){return "D5";}
        if(note.equals("31")){return "D#5";}
        if(note.equals("32")){return "E5";}
        if(note.equals("33")){return "F5";}
        if(note.equals("34")){return "F#5";}
        if(note.equals("35")){return "G5";}
        if(note.equals("36")){return "G#5";}
        if(note.equals("37")){return "A5";}
        if(note.equals("38")){return "A#5";}
        if(note.equals("39")){return "B5";}
        if(note.equals("40")){return "C6";}
        if(note.equals("41")){return "C#6";}
        if(note.equals("42")){return "D6";}
        if(note.equals("43")){return "D#6";}
        if(note.equals("44")){return "E6";}
        if(note.equals("45")){return "F6";}
        if(note.equals("46")){return "F#6";}
        if(note.equals("47")){return "G6";}
        if(note.equals("48")){return "G#6";}
        if(note.equals("49")){return "A6";}
        if(note.equals("50")){return "A#6";}
        if(note.equals("51")){return "B6";}
        if(note.equals("52")){return "C7";}
        if(note.equals("53")){return "C#7";}
        if(note.equals("54")){return "D7";}
        if(note.equals("55")){return "D#7";}
        if(note.equals("56")){return "E7";}
        if(note.equals("57")){return "F7";}
        if(note.equals("58")){return "F#7";}
        if(note.equals("59")){return "G7";}
        if(note.equals("60")){return "G#7";}
        if(note.equals("61")){return "R";}
        return "99";
    }
}