package game;

import javax.swing.JFrame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.parse4j.Parse;

public class Frame {
	
	static JFrame frame = new JFrame();// initialises JFrame
	static Main panel;
	
	public static void main(String[] args) {
		ImageIcon img = new ImageIcon("Icon.png");
		String name;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("Username.txt"));
			name = br.readLine();
			br.close();

		} catch (Exception e) {
			name = JOptionPane.showInputDialog(null, "Please enter your username", "Welcome",
					JOptionPane.PLAIN_MESSAGE);// gets username


			if (name == null || (name != null && ("".equals(name)))) { // If
																		// cancelled
				
				System.exit(0); // Close program
			}
			
			try {

				Writer wr = new FileWriter("Username.txt");
				wr.write(name);
				wr.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			
		}
		
		
		

		
		panel = new Main(name);// initialises JPanel
		frame.add(panel);
		frame.setSize(1280, 720);// sets frame size
		frame.setLocationRelativeTo(null);
		frame.setTitle("Bouncy Box");
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setIconImage(img.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Parse.initialize("OsBlnIJZzv1diW5vNr9nn2zlJqmK5Rhj75R5qf6t", "LvYemJh5dyfb4pZ9PYmgzTJRNIB7TkFXQ1NMnt1V");// connects
																													// to
																													// database

	}
}
