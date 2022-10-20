import javax.swing.JFrame;
import javax.swing.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class JTopFrame extends JFrame implements ActionListener{
    
    static JFrame frame;
    static JMainPanel m;
    JMenuItem exit, open, help;
    final JFileChooser fc = new JFileChooser();
    String folderName = "none";
    
    public static void main(String[] args) {
        JTopFrame a = new JTopFrame();
    }
    
    public JTopFrame() {
        setTitle("Melody Harmoniser");
        setSize(600,400);
        setResizable(false);
        setBounds(100,100,600,400);
        
        m = new JMainPanel();
        getContentPane().add(m, "Center");
        
        JMenuBar menuBar = new JMenuBar();
        // Attach the menu bar to the frame.
        this.setJMenuBar(menuBar);
        
        // creates file menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        // adds menu items for file menu
        open = new JMenuItem("Select Training Data");
        open.addActionListener(this);
        fileMenu.add(open);
        
        exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        fileMenu.add(exit);
        
        // creates help menu
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        
        // adds menu items for help menu
        help = new JMenuItem("How to Operate");
        help.addActionListener(this);
        helpMenu.add(help);
        
        
        addWindowListener(new WindowAdapter()
        { public void windowClosing(WindowEvent we)
          { System.exit(0); }
        } );
        
        setVisible(true);
        show();
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(exit)) {
            System.exit(0);
        }
        
        if(e.getSource().equals(open)) {
            // selecting folder to train from, is set in m, and passed to ChoralesToData
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Select Training Data Folder");
            File file = new File("Training Sets");
            //fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fc.setCurrentDirectory(file);
            fc.setSelectedFile(new File("C:\\Documents and Settings\\Sam Khan\\My Documents\\Bach Project\\Midi Harmoniser\\Training Sets\\All"));
            
            int returnVal = fc.showOpenDialog(this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION){
                String pathName = fc.getSelectedFile().toString();
                
                // test folder
                boolean b = validFolder(pathName);
                
                if(b == false){
                    String badInput = ("Please select a folder containing valid text files");
                    JOptionPane.showMessageDialog(null, badInput);
                }else{
                    m.setFolderName(pathName);
                }
            }
        }
        
        if(e.getSource().equals(help)) {
            
            HelpFrame h = new HelpFrame();
        }
    }
    
    public boolean validFolder(String folderName){
        // checks that the folder contains only valid text files
        try{
            File f = new File(folderName);
            File data[] = f.listFiles();
            if(data.length==0)
                return false;
            for(int i=0;i<data.length;i++){
                if(data[i].isFile()){
                    FileInputStream is = new FileInputStream(data[i]);
                    InputStreamReader in = new InputStreamReader(is);
                    BufferedReader ir = new BufferedReader(in);
                    
                    String first = ir.readLine();
                    first = first.substring(0,7);
                    if(first.equals("Choraln")){}else
                        return false;
                    
                }else
                    return false;
            }
            return true;
            
        }catch (IOException e) {
            System.out.println("An IO error occurred!");
            System.out.println(e);
        }
        catch (NumberFormatException e) {
            System.out.println("Incorrect format for input file!");
            System.out.println(e);
        }
        return true;
    }
}