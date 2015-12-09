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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

	Timer timer = new Timer(13, this);
	Random random = new Random();

	int score = 0;
	int yPosition = 0;
	int xPos = 615;
	int pipe1X = 1280;
	int distPipes = 300;
	int[] pipeX = { pipe1X, pipe1X + distPipes, pipe1X + (2 * distPipes), pipe1X + (3 * distPipes),
			pipe1X + (4 * distPipes) };
	int[] pipeY = new int[10];
	int[] starsX = new int[200];
	int[] starsY = new int[200];
	int[] snowX = new int[100];
	int[] snowY = new int[100];
	int pipeGap = 635;
	int pointAward = 1305;
	int clearIntersectPoint = 640;
	int highScore;
	int onlineScore;

	double cloudX = 20.0;
	double cloudY = random.nextInt(100) + 40;
	double terminalVelocity = 18.0;
	double gravity = 0.34;
	double yVelocity = 0.0;
	double cloudSpeed = 0.3;
	double pipeSpeed = -2.0;

	boolean gameOver = false;
	boolean gameStarted = false;
	boolean gamePaused = false;
	boolean resume = false;
	boolean exit = false;
	boolean saveOptions = false;
	boolean delay = false;

	File pointSFX = new File("Cleared.wav");

	String playerName;
	String worldBestScore;
	String difficultyOption;
	String colourOption;
	String modeOption = "Normal";

	String[] difficulty = { "Easy", "Normal", "Hard" };
	String[] boxColour = { "Black", "Blue", "Green", "Grey", "Orange", "Pink", "Purple", "Red", "Yellow" };
	String[] mode = { "Normal", "Night", "High", "Drunk", "Christmas" };

	JFrame optionsFrame = new JFrame("Options");

	JPanel optionsPanel = new JPanel();

	JLabel difficultyLabel = new JLabel("Difficulty");
	JLabel colourLabel = new JLabel("Colour");
	JLabel modeLabel = new JLabel("Mode");

	JComboBox<String> difficultyBox = new JComboBox<>(difficulty);
	JComboBox<String> colourBox = new JComboBox<>(boxColour);
	JComboBox<String> modeBox = new JComboBox<>(mode);

	JButton saveDifficulty = new JButton("Save");

	Color backgroundColour;
	Color pipeColour;
	Color cloudColour;

	public Main(String name) {
		playerName = name;

		for (int i = 0; i < 10; i += 2) { // Initialise y axis for pipes
			pipeY[i] = random.nextInt(600) - 600;
			pipeY[i + 1] = pipeY[i] + pipeGap;
		}
		
		for (int i = 0; i < 200; i++) {
			starsX[i] = random.nextInt(1280);
			starsY[i] = random.nextInt(720);
		}
		
		for (int i = 0; i < 100; i++) {
			snowX[i] = random.nextInt(1280);
			snowY[i] = random.nextInt(720);
		}

		try { // see if HScore.txt exists

			BufferedReader highScoreReader = new BufferedReader(new FileReader("HScore.txt"));
			String s = highScoreReader.readLine();
			highScore = Integer.parseInt(s);
			System.out.println("Local high score: " + highScore);
			highScoreReader.close();

		} catch (Exception e) {
			highScore = 0;
		}

		try {
			BufferedReader or = new BufferedReader(new FileReader("Options.txt")); // see
																					// if
																					// Options.txt
																					// exists
			colourOption = or.readLine();
			System.out.println("Saved colour: " + colourOption);
			or.close();

		} catch (Exception e2) {
			colourBox.setSelectedItem("Yellow");
			colourOption = (String) colourBox.getSelectedItem();
			System.out.println("Default colour has been set");

		}

		try {
			BufferedReader difficultyReader = new BufferedReader(new FileReader("Difficulty.txt")); // see
																									// if
																									// Difficulty.txt
																									// exists
			difficultyOption = difficultyReader.readLine();
			System.out.println("Saved difficulty: " + difficultyOption);
			difficultyReader.close();

		} catch (Exception e3) {
			difficultyBox.setSelectedItem("Normal");
			difficultyOption = (String) difficultyBox.getSelectedItem();
			System.out.println("Default difficulty has been set");
		}

		onlineCheck();

		addKeyListener(this);
		setFocusable(true);
	}

	public void paintComponent(final Graphics g) {
		Rectangle[] pipe = new Rectangle[10];
		Rectangle[] stars = new Rectangle[200];
		Rectangle[] snow = new Rectangle[100];
		Rectangle box = new Rectangle((int) xPos, (int) yPosition, 35, 35);

		Graphics2D g2d = (Graphics2D) g; // Anti-aliasing the drawn objects
											// (smoothening the edges)
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paintComponent(g2d);

		for (int i = 0; i < 10; i += 2) { // initialise pipe y axis
			while (pipeY[i] < -500) {
				pipeY[i] = random.nextInt(600) - 600;
				pipeY[i + 1] = pipeY[i] + pipeGap;
			}
		}

		for (int i = 0; i < 10; i += 2) { // initialise pipes
			pipe[i] = new Rectangle(pipeX[i / 2], pipeY[i], 50, 500);
			pipe[i + 1] = new Rectangle(pipeX[i / 2], pipeY[i + 1], 50, 1000);
		}

		setMode(g);		

		g.setColor(backgroundColour);
		g.fillRect(0, 0, 1280, 720);

		if ("Night".equals(modeOption)) {
			g.setColor(new Color(240, 240, 240));	
			
			for (int i = 0; i < 200; i++) {
				stars[i] = new Rectangle(starsX[i], starsY[i], 2, 2);
			}
			
			for (Rectangle p : stars) {
				g.fillOval(p.x, p.y, p.width, p.height);
			}
			
			g.fillOval(140, 50, 150, 150);
			g.setColor(Color.BLACK);
			g.fillOval(158, 42, 150, 150);
			
			
			
		}
		
		g.setColor(cloudColour);
		for (int i = 350; i <= 1400; i += 350) { // draw clouds
			paintOvals(g, cloudX, cloudY);
			paintOvals(g, cloudX + i, cloudY);
		}
		
		
		g.setColor(pipeColour);
		for (Rectangle p : pipe) { // draw pipes	
			g.fillRect(p.x, p.y, p.width, p.height);
		}
		
		
		if ("Christmas".equals(modeOption)) {
			for (int i = 0; i < 100; i++) {
				snow[i] = new Rectangle(snowX[i], snowY[i], 10, 10);
			}
			for (Rectangle s : snow) {
				g.fillOval(s.x, s.y, s.width, s.height);
			}
		}
		

		if (gameStarted) { // display box and score
			setColour(g);
			g.fillRect(box.x, box.y, box.width, box.height);
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.PLAIN, 35));
			g.drawString(Integer.toString(score), 635, 30);
		}

		displayMenu(g);

		for (int i = 0; i <= 9; i++) { // checks to see if box has collided with
										// a pipe
			if (box.intersects(pipe[i])) {
				gameOver = true;
				timer.stop();
			}
		}

		if (gamePaused) { // when paused
			g.setColor(Color.RED);
			g.setFont(new Font("Arial ", Font.PLAIN, 45));
			g.drawString("Paused", 545, 250);
			g.drawString("Press enter to resume", 420, 300);
			timer.stop();
		}

		if (gameOver) { // at game end

			onlineCheck();

			if (highScore < score) { // if new high score is achieved
				highScore = score;

				try { // save high score to HScore.txt

					String scoreString = Integer.toString(highScore);

					Writer highScoreWriter = new FileWriter("HScore.txt");
					highScoreWriter.write(scoreString);
					System.out.println("New high score: " + scoreString);
					highScoreWriter.close();

				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}

			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.PLAIN, 45));
			g.drawString("You lose!", 545, 250);
			g.drawString("Local High Score: " + Integer.toString(highScore), 450, 300);
			g.drawString("Press enter to retry or esc to exit", 323, 350);
			g.drawString("Press L to reveal the top scorer", 335, 400);
			g.drawString("Press O to view options", 402, 450);
		}

	}

	public void setMode(Graphics g) { // TODO Set Theme
		switch (modeOption) {
		case "Normal":
			backgroundColour = new Color(48, 179, 255);
			pipeColour = new Color(0, 212, 49);
			cloudColour = new Color(254, 251, 255);
			delay = false;
			break;
		case "Night":
			backgroundColour = Color.BLACK;
			pipeColour = new Color(0, 97, 24);
			cloudColour = new Color(64, 61, 65);
			delay = false;
			break;
		case "High":
			backgroundColour = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
			pipeColour = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
			cloudColour = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
			delay = false;
			break;
		case "Drunk":
			backgroundColour = new Color(48, 179, 255);
			pipeColour = new Color(0, 212, 49);
			cloudColour = new Color(254, 251, 255);
			delay = true;
			break;
		case "Christmas":
			backgroundColour = Color.WHITE;
			pipeColour = new Color(40, 150, 0);
			cloudColour = new Color(240, 240, 240);
			delay = false;
			break;
		}
	}

	public void paintOvals(Graphics g, double cloudX2, double cloudY2) { // draw
																			// a
																			// cloud
		g.fillOval((int) cloudX2 - 10, (int) cloudY2 - 42, 140, 55);
		g.fillOval((int) cloudX2 + 30, (int) (cloudY2 - 70), 150, 65);
		g.fillOval((int) cloudX2 - 32, (int) (cloudY2 - 80), 150, 70);

	}
	
	public void snow(Graphics g, double snowX, double snowY) {
		g.fillOval((int) snowX, (int) snowY, 10, 10);
	}

	public void onlineCheck() {

		final int s1 = score;
		ParseObject object = new ParseObject("Scores");
		object.put("User", playerName);
		object.put("Score", s1);
		object.saveInBackground(); // save score

		ParseQuery<ParseObject> q = ParseQuery.getQuery("Scores"); // Tests if
																	// score is
																	// high
																	// score

		q.getInBackground("PseztczTNY", new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject pObject, ParseException e) {
				if (e == null) {
					if (pObject.getInt("Score") < s1) {
						pObject.put("User", playerName);
						System.out.println("Achieved score: " + score);
						pObject.put("Score", s1);

						try {
							pObject.save();
							JOptionPane.showMessageDialog(Frame.frame, "You have the best high score online!",
									"Congratulations!", JOptionPane.DEFAULT_OPTION);
							worldBestScore = "Online high score: " + pObject.getInt("Score") + "\n" + "User: "
									+ pObject.getString("User");

						} catch (ParseException e1) {

							e1.printStackTrace();
						}
					} else {
						worldBestScore = "Online high score: " + pObject.getInt("Score") + "\n" + "User: "
								+ pObject.getString("User");
						System.out.println(worldBestScore);
						onlineScore = pObject.getInt("Score");
					}
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {

		yVelocity += gravity;

		if (yVelocity >= terminalVelocity) {
			yVelocity = terminalVelocity;
		}
		if (yPosition >= 670 || yPosition <= -75) {
			gameOver = true;
			timer.stop();
		}
		yPosition += yVelocity;

		if (pointAward <= 640) {
			score++;
			pointAward += 300;
			// soundEffect();
		}

		// Pipe things moving etc.
		
		if (delay){
			pipeSpeed = -1;
			cloudSpeed = 0.15;
			gravity = 0.15;
			terminalVelocity = 9.0;
		} else {
			terminalVelocity = 18.0;
			gravity = 0.34;
			cloudSpeed = 0.3;
			pipeSpeed = -2.0;
		}

		pipeX[0] += pipeSpeed;
		pipeX[1] += pipeSpeed;
		pipeX[2] += pipeSpeed;
		pipeX[3] += pipeSpeed;
		pipeX[4] += pipeSpeed;
		pointAward += pipeSpeed;
		cloudX -= cloudSpeed;

		for (int i = 0; i < 5; i++) {
			if (pipeX[i] <= -50) {
				if (i != 0) {
					pipeX[i] = pipeX[i - 1] + 300;
				} else {
					pipeX[0] = pipeX[4] + 300;
				}
				pipeY[2 * i] = random.nextInt(600) - 600;
				pipeY[2 * i + 1] = pipeY[2 * i] + pipeGap;
			}
		}

		if (cloudX <= -185) {
			cloudX = 164.79999999999868;
		}

		repaint();
	}

	public void displayMenu(Graphics g) {
		if (!gameStarted && highScore != 0 && !gameOver) { // at start
			g.setColor(Color.RED);
			g.setFont(new Font("Arial ", Font.PLAIN, 45));
			g.drawString("Bouncy Box", 510, 100);
			g.drawString("Press space to begin", 420, 300);
			g.drawString("Press O to view options", 402, 350);

		} else if (!gameStarted && highScore == 0 && !gameOver) { // at start of
																	// first
																	// ever use
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.PLAIN, 45));
			g.drawString("Bouncy Box", 510, 100);
			g.drawString("Press space to jump", 425, 250);
			g.drawString("Avoid the green pipes", 410, 300);
			g.drawString("Don't touch the ground", 405, 350);
			g.drawString("Press space to begin", 420, 400);
			g.drawString("Press O to view options", 402, 450);
		}
	}

	public void soundEffect() { // method which is called to play a sound when
								// scoring a point

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(pointSFX);
					Clip clip = AudioSystem.getClip();
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {

				}
			}

		}).start();
	}

	public void setColour(Graphics g) { // sets the colour of the box
		switch (colourOption) {
		case "Black":
			g.setColor(Color.BLACK);
			break;
		case "Blue":
			g.setColor(Color.BLUE);
			break;
		case "Green":
			g.setColor(Color.GREEN);
			break;
		case "Grey":
			g.setColor(Color.GRAY);
			break;
		case "Orange":
			g.setColor(Color.ORANGE);
			break;
		case "Pink":
			g.setColor(Color.PINK);
			break;
		case "Purple":
			g.setColor(new Color(174, 0, 255));
			break;
		case "Red":
			g.setColor(Color.RED);
			break;
		case "Yellow":
			g.setColor(Color.YELLOW);
			break;
		}
	}

	public void options() { // TODO Options Menu

		optionsPanel.setLayout(null);
		optionsPanel.setBackground(backgroundColour);

		optionsFrame.setTitle("Options");
		optionsFrame.setSize(220, 220);
		optionsFrame.setLocationRelativeTo(null);
		optionsFrame.setResizable(false);
		optionsFrame.setVisible(true);

		difficultyLabel.setBounds(20, 20, 70, 20);
		difficultyLabel.setForeground(Color.RED);
		colourLabel.setBounds(20, 60, 70, 20);
		colourLabel.setForeground(Color.RED);
		modeLabel.setBounds(20, 100, 70, 20);
		modeLabel.setForeground(Color.RED);

		difficultyBox.setBounds(80, 20, 90, 20);
		difficultyBox.setBackground(Color.WHITE);
		difficultyBox.setForeground(Color.RED);
		colourBox.setBounds(80, 60, 90, 20);
		colourBox.setBackground(Color.WHITE);
		colourBox.setForeground(Color.RED);
		modeBox.setBounds(80, 100, 90, 20);
		modeBox.setBackground(Color.WHITE);
		modeBox.setForeground(Color.RED);

		difficultyBox.setSelectedItem(difficultyOption);
		colourBox.setSelectedItem(colourOption);
		modeBox.setSelectedItem(modeOption);

		saveDifficulty.setBounds(80, 140, 90, 20);
		saveDifficulty.setBackground(Color.WHITE);
		saveDifficulty.setForeground(Color.RED);

		saveDifficulty.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) { // save the settings

				saveOptions = true;

				if (saveOptions) {

					difficultyOption = (String) difficultyBox.getSelectedItem();
					colourOption = (String) colourBox.getSelectedItem();
					modeOption = (String) modeBox.getSelectedItem();

					switch (difficultyOption) {
					case "Easy":
						pipeGap = 650;
						break;
					case "Normal":
						pipeGap = 635;
						break;
					case "Hard":
						pipeGap = 620;
						break;
					}

					try {
						Writer optionsWriter = new FileWriter("Options.txt");
						optionsWriter.write(colourOption);
						optionsWriter.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					try {
						Writer difficultyWriter = new FileWriter("Difficulty.txt");
						difficultyWriter.write(difficultyOption);
						difficultyWriter.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					saveOptions = false;
					optionsFrame.setVisible(false);

				}
			}

		});

		optionsPanel.add(difficultyBox);
		optionsPanel.add(colourBox);
		optionsPanel.add(modeBox);

		optionsPanel.add(colourLabel);
		optionsPanel.add(difficultyLabel);
		optionsPanel.add(modeLabel);

		optionsPanel.add(saveDifficulty);

		optionsFrame.add(optionsPanel);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();

		if (c == KeyEvent.VK_SPACE && gameStarted && !delay) { // bounce
			yVelocity = -7;

			if (yPosition <= 50) {
				yVelocity = -4;
			}
		}
		if (c == KeyEvent.VK_ESCAPE) {
			gamePaused = true;
		}
		if (gamePaused) {
			if (c == KeyEvent.VK_ENTER) {
				timer.start();
				gamePaused = false;
			}
		}

		if (c == KeyEvent.VK_O && !gameStarted) {
			options();
		}

		if (c == KeyEvent.VK_SPACE && !gameStarted) { // start the
														// game
			// animations
			timer.start();
			gameStarted = true;

		}

		if (gameOver) {

			if (c == KeyEvent.VK_ENTER) { // reset the positions of the pipes
											// etc.
				timer.start();

				score = 0;
				gameOver = false;
				yVelocity = 0;
				yPosition = 0;
				pipeX[0] = 1280;
				pipeX[1] = pipeX[0] + 300;
				pipeX[2] = pipeX[1] + 300;
				pipeX[3] = pipeX[2] + 300;
				pipeX[4] = pipeX[3] + 300;
				pipeY[0] = random.nextInt(600) - 600;
				pipeY[2] = random.nextInt(600) - 600;
				pipeY[4] = random.nextInt(600) - 600;
				pipeY[6] = random.nextInt(600) - 600;
				pipeY[8] = random.nextInt(600) - 600;
				pipeY[1] = pipeY[0] + pipeGap;
				pipeY[3] = pipeY[2] + pipeGap;
				pipeY[5] = pipeY[4] + pipeGap;
				pipeY[7] = pipeY[6] + pipeGap;
				pipeY[9] = pipeY[8] + pipeGap;
				pointAward = 1305;
				clearIntersectPoint = 640;
			}
			if (c == KeyEvent.VK_ESCAPE) { // close the program
				System.exit(0);
			}

			if (c == KeyEvent.VK_L) { // shows online top scorer
				JOptionPane.showMessageDialog(Frame.frame, worldBestScore, "Top scorer", JOptionPane.PLAIN_MESSAGE);
			}

			if (c == KeyEvent.VK_O) { // display options menu
				options();

			}

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int c = e.getKeyCode();
		if (delay) {
			if (c == KeyEvent.VK_SPACE) { // bounce
				yVelocity = -4;

				if (yPosition <= 50) {
					yVelocity = -4;
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}