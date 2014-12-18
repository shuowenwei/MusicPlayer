/**
 * @author Shuowen Wei
 */

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
import javax.swing.JTextField;
//import javax.swing.SwingUtilities;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

public class Music implements Runnable{

    private File file;
    private boolean running, pause, restart, stop; 
    private final int byteChunkSize = 1024;  //number of bytes to read at one time
    long tStart, tEnd, tDelta, currentSongPosition;
	double percentage;
	String filePath, ShowPercentage;
	ArrayList<String> playlist = new ArrayList<String>();
	File[] filesInDirectory;
    Thread t;
    JFrame textframe = new JFrame();
    JTextField textfiled = new JTextField();
    int currentplaying; 
    //JPanel textpanel = new JPanel();  
    
    /**
     * Declares default variable values.
     */
    public Music(){
        file = null;
        running = false;
        pause = false;
        restart = false;
        stop = false;
        percentage = 0; 
        filePath = null; 
        ShowPercentage = null;
        currentplaying = 0;
    }

    /**
     * show the menu with user options, and the MP3 files in the selected folder 
     */
    public void ShowMenu(JFileChooser fileChooser)
    {
		filesInDirectory = fileChooser.getCurrentDirectory().listFiles();
		for (File file : filesInDirectory ) {
			if(file.getName().toLowerCase().endsWith(".mp3")){
				playlist.add(file.toString());
			}
		}
		
		if(!playlist.isEmpty()){		// check whether MP3 files are included in the selected folder 
			System.out.println("All MP3 files in current are: ");
			int i = 1;
			for (File file : filesInDirectory) {
				if(file.getName().toLowerCase().endsWith(".mp3")){
					System.out.println(i+": "+file.getName());
					i = i+1;
				}
			}
			currentplaying = 0;
			filePath = playlist.get(0);
			System.out.println("Now is playing song 1: "+filesInDirectory[0].getName());
			loadFile(filePath);
			play();
		}
    	else{
        	System.err.println("There're no MP3 files in the folder "+fileChooser.getCurrentDirectory()+" !!!");
        	System.exit(0);	
    	}
		
        String input;
        Scanner inn = new Scanner(System.in);
        System.out.println("\nPlease select the following options: ");

        do{
            System.out.println("1. Pause/Play"+"  "+"2. Restart"+"     "+"3. Shuffle");
            System.out.println("4. Stop"+"        "+"5. Exit");
            System.out.print("***Select option >> ");
            input = inn.nextLine();

            if(input.equals("1")){
                pause();
            }
            else if(input.equals("2")){
                restart();
            }
            else if(input.equals("3")){ 
            	stop();
            	shuffle();
            }
            else if(input.equals("4")){
            	stop();
            	System.out.println("\n***************************************Song playing Stopped!***************************************");
            	int j = 1;
        		for (File file : filesInDirectory) {
        			System.out.println(j+": "+file.getName());
        			j = j+1;
        		}
        		System.out.println("Please select a song to play (1-"+playlist.size()+") >> ");
                Scanner select_2 = new Scanner(System.in);
                String SongIndex_2 = select_2.nextLine();
                if(Integer.parseInt(SongIndex_2) >=0 && Integer.parseInt(SongIndex_2) <= (playlist.size()+1)){
                	filePath = playlist.get(Integer.parseInt(SongIndex_2)-1);
                	System.out.println("\nNow is playing song "+Integer.parseInt(SongIndex_2)+": "+filesInDirectory[Integer.parseInt(SongIndex_2)-1].getName());
                	loadFile(filePath);
                	play();
                }else
                {
                	System.err.println("Invalid Input! Please restart the program and input a number from 1 to"+(playlist.size()+1)+"!!");
                	System.exit(0);	
                }
            }
            else if(input.equals("5")){
            	System.out.println("-----------Thanks for using. Goodbye! :) ");
            	System.exit(0);
            }
            else{
                System.err.println("\nInvalid entry!!!");
            }
        }while(!input.equals("5"));
        System.exit(0);
    }

    /**
     * Creates a file object. If the file path exists on the system, the given file is an mp3, and
     * a song is not currently playing in this instance of the program, true is returned.
     */ 
    public void loadFile(String filePath){ 	
    	running = false; 
    	pause = true;
    	stop = true;
        file = new File(filePath);
        /*
        if(file.exists() && file.getName().toLowerCase().endsWith(".mp3") && !running){
        	 // running = false;
            return true;
        }
        else{
            file = null;
            return false;
        }
        */
    }
   
    /**
     * Starts playing the audio in a new thread.
     */
    public void play(){
        if(file != null ) { //&& !running){
        	running = true; //running = true; 
        	pause = false;//pause = false;
            try{
                t = new Thread(this);
            	t.start();
            }catch(Exception e){
                System.err.println("Could not start new thread for audio!");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Pauses the audio at its current place. Calling this method once pauses the audio stream, calling it
     * again unpauses the audio stream.
     */
    public void pause(){ 
        if(file != null){
            if(pause){
                pause = false;
            }
            else{
                pause = true;
            }
        }
    }
    
    /**
     * shuffle the songs
     */
    public void shuffle(){
    	Random random = new Random() ;
        int randomNumber = random.nextInt(playlist.size());
        currentplaying = randomNumber;
    	filePath = playlist.get(randomNumber);
    	System.out.println("\nNow is playing song "+(randomNumber+1)+": "+file.getName());
    	loadFile(filePath);
    	play();  	    	
    }
    
    /**
     * Closes the audio stream. This method takes some time to execute, and as such you should never call
     * .stop() followed immediately by .play(). If you need to restart a song, use .restart().
     */
    public void stop(){
        if(file != null){
        	stop = true;
        	running = false;
        	pause = false; 
        	textframe.setVisible(false);
        	t.stop();
        }
    }
    /**
     * Restarts the current song. Always use this method to restart a song and never .stop() followed
     * by .play(), which is not safe.
     */
    public void restart(){
        restart = true;
        pause = false;
    }
    
    /**
     * Retrieves the audio stream information and starts the stream. When the stream ends, this method
     * checks to see if it should loop and start again.
     */
    public void run() { // overload the method run() in Runnable
        try{
            do{
                restart = false;
                AudioInputStream in = AudioSystem.getAudioInputStream(file);
                AudioInputStream din = null;
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                            baseFormat.getSampleRate(),
                                                            16,
                                                            baseFormat.getChannels(),
                                                            baseFormat.getChannels() * 2,
                                                            baseFormat.getSampleRate(),
                                                            false);
                din = AudioSystem.getAudioInputStream(decodedFormat, in);
                stream(decodedFormat, din);
                in.close();
            }while(restart && running);
            running = false;
        }catch(Exception e){
            System.err.println("Problem getting audio stream!");
            e.printStackTrace();
        }
    }

    /**
     * Small sections of audio bytes are read off, watching for a call to stop, pause, restart the audio.
     */
    private void stream(AudioFormat targetFormat, AudioInputStream din){
        try{
        	
            byte[] data = new byte[byteChunkSize];
            SourceDataLine line = getLine(targetFormat);
            if(line != null){
                line.start();
                int nBytesRead = 0;
                AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
                Map properties = baseFileFormat.properties();
                //textframe.getContentPane().add(textpanel);
                
                textframe.setBounds(200, 200, 250, 250);
                textframe.setTitle("The elapsed time is");
                textframe.setVisible(true);
                
                DecimalFormat df = new DecimalFormat("#.##");
                
                //JTextField textfiled
                
                currentSongPosition = (Long)properties.get("duration"); // this is the length of the song
                tStart = System.currentTimeMillis();
                //System.out.print("Elapsed percentage is ");
                while(nBytesRead != -1 && running && !restart){	
                	nBytesRead = din.read(data, 0, data.length);
                    if(nBytesRead != -1){
                    	tEnd = System.currentTimeMillis();
                        tDelta = tEnd - tStart;                    
                        percentage = tDelta * 100000.0 / currentSongPosition;
                       
                        ShowPercentage = df.format(percentage);                        
                        textfiled.setText("The elapsed time is "+ShowPercentage+"%.");
                        textframe.add(textfiled);

                        //SwingUtilities.UpdateComponentTreeUI(textframe);
                        //textframe.setContentPane(percentage);
                  
                        //textfiled.setText("percentage");
                        
                        //JFrame textframe = new JFrame();
                        //JTextField textfiled = new JTextField();                     
                        
                        //JLabel windowlabel = new JLabel("Elapsed percentage is ");
                        //JTextField textBoxToEnterName = new JTextField(21);
                        
                    	//System.out.println("Elapsed percentage is "+String.format("%.02f", percentage)+"%. *****( this part can be commented out in Music.java, please do so to the see the console panel!)*****");
                        //System.out.print("\r     \r"+String.format("%.02f", percentage)+"%.");
                        //System.out.flush();
                        //************************************
                    	line.write(data, 0, nBytesRead);
                    }         
                    /*
                    if(percentage >= 99.1)
                	{
                		running = true;
                		pause = false;
                		stop = true;
                		currentplaying = (currentplaying+1)%(playlist.size());
                		filePath = playlist.get(currentplaying);
                    	System.out.println("\nNow is playing song "+(currentplaying+1)+": "+filesInDirectory[currentplaying].getName());
                    	loadFile(filePath);
                    	play();
                    	percentage = 0;
                	}
                	*/
                    while(pause && running){
                        Thread.sleep(15);
                    }
                }
                line.drain();
                line.stop();
                line.close();
                din.close();
            }
        }catch(Exception e){
            System.err.println("Problem playing audio!");
            e.printStackTrace();
        }
    }
    /**
     * Gets the line of audio.
     */
    private SourceDataLine getLine(AudioFormat audioFormat){
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try{
            res = (SourceDataLine) AudioSystem.getLine(info);
            res.open(audioFormat);
        }catch(Exception e){
            System.err.println("\n*****Could not get audio line!");
            e.printStackTrace();
        }
        return res;
    }
}