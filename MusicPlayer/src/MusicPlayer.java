import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MusicPlayer extends JFrame { 

	public static void main(String args[]) { 
		Music player = new Music();
		JFileChooser fileChooser;	// object for choosing files from hard drive
		int result;
		
		// Initialize needed variables and objects
		fileChooser = new JFileChooser();   // Create an object to select songs through a GUI
		
		//fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  // Tell object to select only files		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);  // Tell object to select only files

		// Tell user to select a song
		JOptionPane.showMessageDialog(null,"Please select a song","Song Selection", JOptionPane.INFORMATION_MESSAGE);

		// Pop up the file chooser dialog
		result = fileChooser.showOpenDialog(null);  // HINT: This method returns one of the 
                                                    //  following integers: 
													//		JFileChooser.CANCEL_OPTION
													//		JFileChooser.APPROVE_OPTION
													//		JFileChooser.ERROR_OPTION if an 
                                                    //      error occurs or the dialog is dismissed
		// add the mp3 files in the file or folder to playlist
		player.ShowMenu(fileChooser);
	}
}