package game;

import javax.swing.*;

import org.parse4j.Parse;


public class Frame {
public static void main (String[] args){
	
	String name = JOptionPane.showInputDialog("Yo name brah", JOptionPane.PLAIN_MESSAGE);//gets user's name
	
	JFrame frame = new JFrame();//initialises jframe
	Main panel = new Main(name);//initialises jpanel
	frame.setSize(1280, 720);//sets frame size
	frame.setLocationRelativeTo(null);
	frame.setTitle("Bouncy Box");
	frame.add(panel);
	frame.setVisible(true);
	frame.setResizable(false);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Parse.initialize("OsBlnIJZzv1diW5vNr9nn2zlJqmK5Rhj75R5qf6t", "LvYemJh5dyfb4pZ9PYmgzTJRNIB7TkFXQ1NMnt1V");//connects to database
	
	
}
}
