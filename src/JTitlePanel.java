import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class JTitlePanel extends JPanel{
    
    Image title;
    
    public JTitlePanel(){
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        title = kit.getImage("resources\\img\\title.jpg");
        
        // mt waits for image to load before continuing
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(title,0);
        
        try {
            mt.waitForID(0);
        }
        catch(InterruptedException e) {
        }
        this.setVisible(true);
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g){
        
        boolean a = g.drawImage(title, 0, 0, null);
    }
}