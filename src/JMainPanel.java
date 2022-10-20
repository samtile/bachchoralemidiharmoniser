import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class JMainPanel extends JPanel implements ActionListener{
    JTitlePanel t;
    JTextField mel, fName;
    JComboBox key1, pol, key2;
    JCheckBox trans;
    JButton go;
    JLabel melLab, keyLab, transLab, fileNameLab, blank, titleLab, uniqueLab, measureLab, fileLab, probLab, uniqueLab2, measureLab2, fileLab2, probLab2;
    JPanel outputPanel = new JPanel();
    String folderName = "none";
    
    JPanel measurePanel = new JPanel();
    JPanel uniquePanel = new JPanel();
    JPanel probPanel = new JPanel();
    JPanel filePanel = new JPanel();
    
    ChoralesToData chor;
    int measures=0, files=0, uniqueChords=0;
    double ll=0, probability=0;
    GridLayout mainLayout = new GridLayout(3,1);
    
    public JMainPanel(){
        
        this.folderName = folderName;
        Color back = new Color(240, 240, 240);
        setLayout(mainLayout);
        setBackground(back);
        
        // creating input things before adding them
        titleLab = new JLabel("title here");
        melLab = new JLabel("Melody:");
        keyLab = new JLabel("Key:");
        transLab = new JLabel("Transpose?");
        fileNameLab = new JLabel("Save File Name:");
        blank = new JLabel("");
        
        mel = new JTextField("");
        fName = new JTextField("");
        
        String[] key1S = {"A", "B", "C", "D", "E", "F", "G"};
        key1 = new JComboBox(key1S);
        key1.setSelectedItem("C");
        String[] polS = {"M", "m"};
        pol = new JComboBox(polS);
        String[] key2S = {"#", "N", "b"};
        key2 = new JComboBox(key2S);
        key2.setSelectedItem("N");
        
        trans = new JCheckBox();
        trans.setSelected(false);
        
        fileLab = new JLabel("Number of files in training set:");
        measureLab = new JLabel("Number of time points in training set:");
        uniqueLab = new JLabel("Number of unique chords in training set:");
        probLab = new JLabel("Probability of melody given the chord sequence:");
        
        go = new JButton("Run");
        go.addActionListener(this);
        
        outputPanel.setLayout(new GridLayout(4,2));
        
        
        // top section of main grid layout - title
        t = new JTitlePanel();
        add(t);
        
        // middle section of main grid layout - input
        JPanel inputSection = new JPanel();
        inputSection.setLayout(new GridLayout(5,2));
        
        // part of middle section
        JPanel keyPan = new JPanel();
        keyPan.setLayout(new GridLayout(1,3));
        keyPan.add(key1);
        keyPan.add(key2);
        keyPan.add(pol);
        key1.setBackground(back);
        key2.setBackground(back);
        pol.setBackground(back);
        
        inputSection.add(melLab);
        inputSection.add(mel);
        inputSection.add(keyLab);
        inputSection.add(keyPan);
        inputSection.add(transLab);
        inputSection.add(trans);
        inputSection.add(fileNameLab);
        inputSection.add(fName);
        inputSection.add(blank);
        inputSection.add(go);
        trans.setBackground(back);
        go.setBackground(back);
        
        add(inputSection);
        inputSection.setBackground(back);
        
        // bottom section of main grid layout - output information
        // must add panels for output bit, as labels not decided until actionPerformed method
        outputPanel.add(fileLab);
        outputPanel.add(filePanel);
        outputPanel.add(measureLab);
        outputPanel.add(measurePanel);
        outputPanel.add(uniqueLab);
        outputPanel.add(uniquePanel);
        outputPanel.add(probLab);
        outputPanel.add(probPanel);
        filePanel.setBackground(back);
        measurePanel.setBackground(back);
        uniquePanel.setBackground(back);
        probPanel.setBackground(back);
        add(outputPanel);
        outputPanel.setBackground(back);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(go)) {
            chor = new ChoralesToData(folderName);
            int tr = 0;
            if(trans.isSelected()) {
                tr = 1;
            }
            boolean validString = melodyValid(mel.getText());
            if(validString == false){
                String badInput = ("Please enter a valid melody sequence");
                JOptionPane.showMessageDialog(null, badInput);
                
            }else if(fileNameValid(fName.getText())==false){
                String badFileName = ("Please save as file type midi ('.mid')");
                JOptionPane.showMessageDialog(null, badFileName);
                
            }else if(folderName.equals("none")){
                String noFolder = ("Please select a folder containing training data");
                JOptionPane.showMessageDialog(null, noFolder);
                
            }else{
                chor.harmanise(mel.getText(), (String)key1.getSelectedItem(),
                (String)key2.getSelectedItem(),(String)pol.getSelectedItem(), tr, fName.getText());
                
                // get output data
                files = chor.getTotalFiles();
                measures = chor.getMeasures();
                uniqueChords = chor.getUniqueChords();
                ll = chor.getLL();
                probability = Math.exp(ll);
                addOutput();
            }
        }
    }
    
    public void addOutput(){
        // shows the output variables in bottom panel to the left
        
        // in case of no training data
        if(probability == 1 && files == 0){}
        else{
            // round down probability
            if (probability < 0.001)
                probability = 0; // when is less than 0.001
            String roundDown = Double.toString(probability);
            if(roundDown.length()>5)
                roundDown = roundDown.substring(0,5);
            if(probability == 0)
                roundDown = "<0.000";
            fileLab2 = new JLabel(Integer.toString(files));
            measureLab2 = new JLabel(Integer.toString(measures));
            uniqueLab2 = new JLabel(Integer.toString(uniqueChords));
            probLab2 = new JLabel(roundDown);
            
            filePanel.removeAll();
            measurePanel.removeAll();
            uniquePanel.removeAll();
            probPanel.removeAll();
            
            filePanel.add(fileLab2);
            measurePanel.add(measureLab2);
            uniquePanel.add(uniqueLab2);
            probPanel.add(probLab2);
            
            revalidate();
        }
    }
    
    public boolean fileNameValid(String fileName){
        // checks if file name ends in '.mid', returns false if not
        if(fileName.length()<5)
            return false;
        String lastFour = fileName.substring(fileName.length()-4);
        
        if(lastFour.equals(".mid")==false){
            return false;
        }else
            return true;
    }
    
    public boolean melodyValid(String melody){
        // checks if the melody input is a valid one, returns false if not
        for(int i=0;i<melody.length();i++){
            if(i == melody.length()-1)return false;
            
            if(melody.charAt(melody.length()-1)!=';')
                return false;
            
            if(melody.charAt(i)=='A'){}else
                if(melody.charAt(i)=='B'){}else
                    if(melody.charAt(i)=='C'){}else
                        if(melody.charAt(i)=='D'){}else
                            if(melody.charAt(i)=='E'){}else
                                if(melody.charAt(i)=='F'){}else
                                    if(melody.charAt(i)=='G'){}else
                                        if(melody.charAt(i)=='H'){}else{
                                            return false;
                                        }
            i++;
            if(i == melody.length()-1) return false;
            
            if(melody.charAt(i)=='#')
                i++;
            else if(melody.charAt(i)=='b')
                i++;
            if(i == melody.length()-1)return false;
            
            if(melody.charAt(i)=='0'){}else
                if(melody.charAt(i)=='1'){}else
                    if(melody.charAt(i)=='2'){}else{
                        return false;
                    }
            i++;
            if(i == melody.length()-1)return false;
            
            if(melody.charAt(i)==':'){}else{
                return false;
            }
            i++;
            if(i == melody.length()-1)return false;
            
            if(melody.charAt(i)=='0'){}else
                if(melody.charAt(i)=='1'){}else
                    if(melody.charAt(i)=='2'){}else{
                        {return false;}
                    }
            
            i++;
            if(melody.charAt(i-1)=='1'&&melody.charAt(i)==';')
                return true;
            else if(melody.charAt(i-1)=='2'&&melody.charAt(i)==';')
                return true;
            if(i == melody.length()-1){return false;}
            
            if(melody.charAt(i)=='.'){
                i++;
                if(i == melody.length()-1)return false;
                // must now check for 125,25,375,5,625,75,875
                if(melody.charAt(i)=='1'){
                    i++;
                    if(i == melody.length()-1){return false;}
                    if(melody.charAt(i)=='2'){
                        i++;
                        if(i == melody.length()-1){return false;}
                        if(melody.charAt(i)=='5'){
                            i++;
                        }else {return false;}
                    }else {return false;}
                    
                }else if(melody.charAt(i)=='2'){
                    i++;
                    if(i == melody.length()-1){return false;}
                    if(melody.charAt(i)=='5'){
                        i++;
                    }else {return false;}
                    
                }else if(melody.charAt(i)=='3'){
                    i++;
                    if(i == melody.length()-1){return false;}
                    if(melody.charAt(i)=='7'){
                        i++;
                        if(i == melody.length()-1){return false;}
                        if(melody.charAt(i)=='5'){
                            i++;
                        }else {return false;}
                    }else {return false;}
                    
                }else if(melody.charAt(i)=='5'){
                    i++;
                    
                }else if(melody.charAt(i)=='6'){
                    i++;
                    if(i == melody.length()-1){return false;}
                    if(melody.charAt(i)=='2'){
                        i++;
                        if(i == melody.length()-1){return false;}
                        if(melody.charAt(i)=='5'){
                            i++;
                        }else {return false;}
                    }else {return false;}
                    
                }else if(melody.charAt(i)=='7'){
                    i++;
                    if(i == melody.length()-1){return false;}
                    if(melody.charAt(i)=='5'){
                        i++;
                    }else {return false;}
                    
                }else if(melody.charAt(i)=='8'){
                    i++;
                    if(i == melody.length()-1){return false;}
                    if(melody.charAt(i)=='7'){
                        i++;
                        if(i == melody.length()-1){return false;}
                        if(melody.charAt(i)=='5'){
                            i++;
                        }else {return false;}
                    }else {return false;}
                }else return false;
            }
            if(i == melody.length()-1 && melody.charAt(i)==';')
                return true;
            else if(i == melody.length()-1)
            {return false;}
            else if(melody.charAt(i)==','){}else
            {return false;}
        }{return false;}
    }
    
    public void setFolderName(String fN){
        folderName = fN;
    }
}