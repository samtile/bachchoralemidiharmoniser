import java.math.*;

public class Train {
    
    int uniqueChords;
    int measures;
    int melody[];
    int uniqueHarmony[];
    
    int notes[];   // given
    int chords[];  // to be found
    double ll;
    double p0[];
    double T[][];
    double H[][];
    int noOfStartingChords;
    boolean startingChord;
    
    public Train(int uniqueChords, int measures, int[] melody, int[] uniqueHarmony, int[] notes) {
        
        this.uniqueChords = uniqueChords;
        this.measures = measures;
        this.melody = melody;
        this.uniqueHarmony = uniqueHarmony;
        this.notes = notes;
        ll=-10000000;
        
    }
    
    public int[] train(){
        // trains a mm on data found from text files, and returns harmony
        
        p0 = new double[uniqueChords];
        T = new double[uniqueChords][uniqueChords];
        H = new double[61][uniqueChords];
        noOfStartingChords = 0;
        
        startingChord = false;
        
        // transitions T
        for(int i=0;i<measures;i++){
            // for starting chords (p0)
            if(startingChord == true){
                p0[uniqueHarmony[i]-1] = p0[uniqueHarmony[i]-1] + 1;
                noOfStartingChords++;
                startingChord = false;
            }else{
                if(uniqueHarmony[i] == 0){
                    startingChord = true;
                }else{
                    T[uniqueHarmony[i-1]-1][uniqueHarmony[i]-1]++;
                }
            }
        }
        // emmisions H
        for(int j=0;j<measures;j++){
            if(uniqueHarmony[j]==0){}
            
            else
                H[melody[j]-1][uniqueHarmony[j]-1]++;
        }
        
        // normalise T
        for(int k=0;k<uniqueChords;k++){
            int chordOccurance = 0;
            for(int m=0;m<uniqueChords;m++){
                chordOccurance = chordOccurance + (int)T[k][m];
            }
            for(int g=0;g<uniqueChords;g++){
                if(chordOccurance > 0){
                    T[k][g] = T[k][g]/chordOccurance;
                }
            }
        }
       
        // normalise H
        for(int j=0;j<uniqueChords;j++){
            int chordOccurance = 0;
            for(int m=0;m<61;m++){
                if(H[m][j]>0){
                    chordOccurance = chordOccurance + (int)H[m][j];
                }
            }
            for(int o=0;o<60;o++){
                H[o][j] = H[o][j]/chordOccurance;
            }
        }
        
        // normalise p0
        for(int s=0;s<uniqueChords;s++){
            p0[s] = p0[s]/noOfStartingChords;
        }
        int[] harmony = viterbix();
        return harmony;
    }
    
    public int[] viterbix(){
        // finds the most likely state path (harmony)
        // following code adapted from Matlab library by Sam Roweis (2005)
        // http://www.cs.toronto.edu/~roweis/code.html
        int melodyLength = notes.length;
        int tau = notes.length;
        int M = 61;
        int kk = uniqueChords;
        
        // initialise space
        double delta[][] = new double[kk][tau];
        int psi[][] = new int[kk][tau];
        int qq[] = new int[tau];
        
        for(int i=0;i<kk;i++){
            for(int j=0;j<tau;j++){
                delta[i][j] = 0;
                psi[i][j] = 0;
            }
        }
        for(int i=0;i<tau;i++)
            qq[i] = 0;
        
        // compute bb
        double bbIn[][] = new double [melodyLength][uniqueChords];
        double bb[][] = new double[uniqueChords][melodyLength];
        
        for(int i=0;i<melodyLength;i++){
            int currentNote = notes[i];
            for(int j=0;j<uniqueChords;j++){
                bbIn[i][j] = H[notes[i]-1][j];
            }
        }
        // now inverse it
        for(int i=0;i<melodyLength;i++){
            for(int j=0;j<uniqueChords;j++){
                bb[j][i] = bbIn[i][j];
            }
        }
        
        // take logs of parameters for numerical ease, then use addition
        double eps = 2.2204e-016;
        for(int i=0;i<uniqueChords;i++){
            p0[i] = Math.log(p0[i]+eps);
        }
        for(int j=0;j<uniqueChords;j++){
            for(int m=0;m<uniqueChords;m++){
                T[j][m] = Math.log(T[j][m]+eps);
            }
        }
        for(int s=0;s<uniqueChords;s++){
            for(int p=0;p<melodyLength;p++){
                bb[s][p] = Math.log(bb[s][p]+eps);
            }
        }
        for(int i=0;i<kk;i++){
            delta[i][0] = p0[i]+bb[i][0];
            psi[i][0] = 0;
        }
        
        for(int tt=1;tt<tau;tt++){
            double[] deltaCol = new double[uniqueChords];
            double[][] deltaOnes = new double[uniqueChords][uniqueChords];
            double[][] inverse = new double[uniqueChords][uniqueChords];
            
            // find column
            for(int i=0;i<uniqueChords;i++)
                deltaCol[i] = delta[i][tt-1];
            //multiply by ones
            for(int i=0;i<uniqueChords;i++){
                for(int j=0;j<uniqueChords;j++){
                    deltaOnes[j][i] = deltaCol[j];
                }
            }
            // add on T
            for(int i=0;i<uniqueChords;i++){
                for(int j=0;j<uniqueChords;j++){
                    deltaOnes[i][j] = deltaOnes[i][j] + T[i][j];
                }
            }
            // inverse it
            for(int i=0;i<uniqueChords;i++){
                for(int j=0;j<uniqueChords;j++){
                    inverse[i][j] = deltaOnes[j][i];
                }
            }
            
            // now find max values
            double[] deltaMax = new double[uniqueChords];
            int[] psiColumn = new int[uniqueChords];
            
            double maxOfRow;
            int maxPosition;
            for(int i=0;i<uniqueChords;i++){
                maxOfRow = -10000000;
                maxPosition = -10000000;
                for(int j=0;j<uniqueChords;j++){
                    if(inverse[i][j]>maxOfRow){
                        maxOfRow = inverse[i][j];
                        maxPosition = j;
                    }
                    deltaMax[i] = maxOfRow;
                    psiColumn[i] = maxPosition;
                }
            }
            for(int i=0;i<uniqueChords;i++){
                delta[i][tt] = deltaMax[i];
                psi[i][tt] = psiColumn[i];
            }
            
            for(int i=0;i<uniqueChords;i++){
                delta[i][tt] = delta[i][tt] + bb[i][tt];
            }
        }
        
        for(int i=0;i<uniqueChords;i++){
            if(delta[i][tau-1]>ll){
                ll = delta[i][tau-1];
                qq[tau-1] = i;
            }
        }
        ll = ll/tau;
        
        for(int tt=(tau-2);tt>-1;tt--){
            qq[tt] = psi[qq[tt+1]][tt+1];
        }
        // put states back up to go from 1
        for(int i=0;i<qq.length;i++){
            qq[i]++;
        }
        return qq;
    }
    
    public double getLL(){
        return ll;
    }
}