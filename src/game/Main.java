package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.callback.GetCallback;

public class Main extends JPanel implements ActionListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int score = 0;
	JLabel points = new JLabel();
	Timer t = new Timer(10, this);
	Random r = new Random();

	double tvel = 9 * 2;
	double g = 0.17 * 2;
	double speed = 0;
	double y = 0;
	double x = 615;
	int pipe1 = 1280;
	int pipestep = 300;
	int[] pipex = { pipe1, pipe1 + pipestep, pipe1 + (2 * pipestep), pipe1 + (3 * pipestep), pipe1 + (4 * pipestep) };
	int[] pipey = new int[10];
	int gap = 635;
	int sped = -2;
	int clear = 1305;
	int lin = 640;
	int hscore;
	double cloudX = 20;
	double cloudY = r.nextInt(100);
	double cloudSpeed = 0.3;
	boolean gOver = false;
	boolean next = false;
	boolean plus = false;
	boolean start = false;
	boolean pause = false;
	File file = new File("loop1.wav");

	String playerName;

	public Main(String name) {

		for (int i = 0; i < 10; i += 2) { // initalise y axis for pipes
			pipey[i] = r.nextInt(600) - 600;
			pipey[i + 1] = pipey[i] + gap;
		}

		playerName = name;
		addKeyListener(this);
		setFocusable(true);
		try {
			BufferedReader br = new BufferedReader(new FileReader("HScore.txt"));
			hscore = br.read();
			System.out.println(hscore);
			br.close();

		} catch (Exception e) {
			hscore = 0;
		}

		points.setBounds(20, 20, 100, 50);
		points.setForeground(Color.RED);
		points.setFont(new Font("Arial", Font.PLAIN, 30));
		add(points);

		// music();
	}

	public void paintComponent(final Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paintComponent(g2d);

		for (int i = 0; i < 10; i += 2) { // initialise pipe y axis
			while (pipey[i] < -500) {
				pipey[i] = r.nextInt(600) - 600;
				pipey[i + 1] = pipey[i] + gap;
			}
		}

		Rectangle[] pipe = new Rectangle[10];
		for (int i = 0; i < 10; i +=2) { // initialise pipes
				pipe[i] = new Rectangle(pipex[i/2], pipey[i], 50, 500);
				pipe[i + 1] = new Rectangle(pipex[i/2], pipey[i + 1], 50, 1000);
		}

		Rectangle o1 = new Rectangle((int) x, (int) y, 35, 35);

		g.setColor(new Color(48, 179, 255));
		g.fillRect(0, 0, 1280, 720);
		
		g.setColor(Color.WHITE);
		for (int i = 300; i <= 1200; i += 300) { // draw clouds
			paintOvals(g, cloudX, cloudY);
			paintOvals(g, cloudX + i, cloudY + 30);
		}
		
		g.setColor(new Color(0, 212, 49));
		for (Rectangle p : pipe) { // draw pipes
			g.fillRect(p.x, p.y, p.width, p.height);
		}
		 

		g.setColor(new Color(48, 179, 255));
		if (start == true) {
			g.setColor(Color.YELLOW);
		}
		g.fillRect(o1.x, o1.y, o1.width, o1.height);

		if (start == false && hscore != 0) { // at start
			g.setColor(Color.RED);
			g.setFont(new Font("Arial ", Font.PLAIN, 45));
			g.drawString("Bouncy Box", 510, 100);
			g.drawString("Press space to begin", 420, 300);

		}
		else if (start == false && hscore == 0) {
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.PLAIN, 45));
			g.drawString("Bouncy Box", 510, 100);
			g.drawString("Press space to jump", 425, 250);
			g.drawString("Avoid the green pipes", 400, 300);
			g.drawString("Don't touch the ground", 385, 350);
			g.drawString("Press space to begin", 420, 400);
		}
		if (o1.intersects(pipe[0]) || o1.intersects(pipe[1]) || o1.intersects(pipe[2]) || o1.intersects(pipe[3]) // hit detection
				|| o1.intersects(pipe[4]) || o1.intersects(pipe[5]) || o1.intersects(pipe[6]) || o1.intersects(pipe[7])
				|| o1.intersects(pipe[8]) || o1.intersects(pipe[9])) {
			t.stop();
			gOver = true;
		}

		if (gOver == true) { //at game end

			final int s1 = score;
			ParseObject object = new ParseObject("Scores");
			object.put("User", playerName);
			object.put("Score", s1);
			object.saveInBackground(); // save score

			ParseQuery<ParseObject> q = ParseQuery.getQuery("Scores"); //tests to see if score is high score
			q.getInBackground("PseztczTNY", new GetCallback<ParseObject>() {

				@Override
				public void done(ParseObject ob, ParseException e) {
					if (e == null) {
						if (ob.getInt("Score") < s1) {
							ob.put("User", playerName);
							System.out.println(score);
							ob.put("Score", s1);

							try {
								ob.save();
								g.drawString("You're the best", 400, 300);

							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							System.out.println("Highscore: " + ob.getInt("Score") + " by " + ob.getString("User"));
						}
					}
				}
			});

			
			  if (hscore < score) { hscore = score;
			  
			  try {
			  
			  Writer wr = new FileWriter("HScore.txt"); wr.write(hscore);
			  wr.close();
			  
			  } catch (IOException e) { e.printStackTrace(); }
			  
			  }
			 
			g.setColor(Color.RED);
			g.setFont(new Font("Arial ", Font.PLAIN, 45));
			g.drawString("You lose!", 545, 250);
			g.drawString("Local High Score: " + Integer.toString(hscore), 500, 300);
			g.drawString("Press space to retry or esc to exit", 295, 350);
			score = 0;

		}
		if (pause == true) {

		}

	}

	public void paintOvals(Graphics g, double cloudX2, double cloudY2) {
		g.fillOval((int) cloudX2, (int) cloudY2 - 40, 150, 75);
		g.fillOval((int) cloudX2 + 30, (int) (cloudY2 - 70), 150, 75);
		g.fillOval((int) cloudX2 - 50, (int) (cloudY2 - 95), 150, 75);
	}

	public void actionPerformed(ActionEvent e) {

		speed = speed + g;

		if (speed >= tvel) {
			speed = tvel;
		}
		if (y >= 670) {
			y = 640;
			gOver = true;
			t.stop();
		}
		if (y <= -45) {
			gOver = true;
			t.stop();
		}
		y = y + speed;

		if (clear <= 640) {
			score = score + 100;
			points.setText(Integer.toString(score));
			clear = clear + 300;
		}

		// Pipe things moving etc.
		pipex[0] = pipex[0] + sped;
		pipex[1] = pipex[1] + sped;
		pipex[2] = pipex[2] + sped;
		pipex[3] = pipex[3] + sped;
		pipex[4] = pipex[4] + sped;
		clear = clear + sped;
		cloudX = cloudX - cloudSpeed;
		
		for(int i = 0; i < 5; i++){
			if(pipex[i] <= -50){
				if(i != 0){
					pipex[i] = pipex[i-1] + 300;
				} else {
					pipex[0] = pipex[4] + 300;
				}
				pipey[2*i] = r.nextInt(600) - 600;
				pipey[2*i + 1] = pipey[2*i] + gap;
			}
		}

		repaint();
	}

	public void music() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					AudioInputStream is = AudioSystem.getAudioInputStream(file);
					Clip clip = AudioSystem.getClip();
					clip.open(is);
					clip.loop(Clip.LOOP_CONTINUOUSLY);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();
		if (c == KeyEvent.VK_SPACE) {
			speed = -8;

			if (y <= 100) {
				speed = -4;
			}
		}
		if (c == KeyEvent.VK_ESCAPE) {
			t.stop();
			pause = true;
		}
		if (c == KeyEvent.VK_SPACE) {
			t.start();
			start = true;

			points.setText(Integer.toString(score));
		}
		if (gOver == true) {

			if (c == KeyEvent.VK_SPACE) {
				t.start();

				gOver = false;
				score = 0;
				speed = 0;
				y = 0;
				pipex[0] = 1280;
				pipex[1] = pipex[0] + 300;
				pipex[2] = pipex[1] + 300;
				pipex[3] = pipex[2] + 300;
				pipex[4] = pipex[3] + 300;
				pipey[0] = r.nextInt(600) - 600;
				pipey[2] = r.nextInt(600) - 600;
				pipey[4] = r.nextInt(600) - 600;
				pipey[6] = r.nextInt(600) - 600;
				pipey[8] = r.nextInt(600) - 600;
				pipey[1] = pipey[0] + gap;
				pipey[3] = pipey[2] + gap;
				pipey[5] = pipey[4] + gap;
				pipey[7] = pipey[6] + gap;
				pipey[9] = pipey[8] + gap;
				clear = 1305;
				lin = 640;
			}
			if (c == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}