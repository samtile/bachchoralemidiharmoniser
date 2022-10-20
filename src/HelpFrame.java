import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;


public class HelpFrame extends JFrame{
    
    JFrame frame;
    JPanel j;
    JLabel mel, key, trans, save, melEx, mel2, mel3, sel, blank, blank2, blank3, blank4;
    Color back = new Color(240, 240, 240);
    public HelpFrame() {
        setTitle("Help");
        setSize(900,300);
        setResizable(false);
        
        j = new JPanel();
        j.setBackground(back);
        getContentPane().add(j, "Center");
        j.setLayout(new GridLayout(12,1));
        setBounds(100,200,900,300);
        
        
        mel = new JLabel(" MELODY:  The melody of piece, in the correct format");
        melEx = new JLabel("                     e.g. A1:0.25,B1:0.25,C#2:1.25;");
        mel2 = new JLabel("                     Where first the note is specified, then the octave (1 being middle C), then a ':', then the duration of that note.");
        mel3 = new JLabel("                     Duration may be between 0.125 and 3, in 0.125 gaps. 0.125 represents a 1/8th note. After this a ',' denotes the next note, and a ';' ends the melody");
        key = new JLabel(" KEY:  The key of the melody to be harmonised, including its note and polarity.");
        trans = new JLabel(" TRANSPOSE:  Whether to train from pieces purely in the same key as melody, or transpose pieces not in the same key.");
        save = new JLabel(" SAVE FILE NAME:  The name to give the final midi file.");
        sel = new JLabel(" FILE > SELECT TRAINING DATA:  Select a folder containing valid training data. Note that when transposing, no more than 100 chorales should be used!");
        
        blank = new JLabel("");
        blank2 = new JLabel("");
        blank3 = new JLabel("");
        blank4 = new JLabel("");
        
        j.add(mel);
        j.add(melEx);
        j.add(mel2);
        j.add(mel3);
        j.add(blank);
        j.add(key);
        j.add(blank2);
        j.add(trans);
        j.add(blank3);
        j.add(save);
        j.add(blank4);
        j.add(sel);
        
        addWindowListener(new WindowAdapter()
        { public void windowClosing(WindowEvent we)
          { setVisible(false); }
        } );
        
        setVisible(true);
        show();
    }
}