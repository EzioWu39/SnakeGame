
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;


public class Game extends JFrame implements ActionListener,KeyListener{

	private JPanel gameBoard;
	enum Direction{up,down,left,right};
	private Food food;
	private Snake snake;
	private int score=0;	
	private Timer timer;	
	private final int speedSlow=100;	
	private final int speedFast=30;	
	private final int gamePanelWidth=875;	
	private final int gamePanelHeight=700;	
	private boolean isGameStart=false;	
	private boolean isGameRunning=false;
	private boolean isGameFailed=false;
	private ImageIcon fail;	
	private ImageIcon pause;	
	private JLabel label_score;
	private DefaultTableModel tableModel;
	private ScoreBoard dlg_ScoreBoard;
	
	Socket socket;
	BufferedReader reader;
	PrintWriter writer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Game frame = new Game();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Game() {
		this.setSize(gamePanelWidth,gamePanelHeight+70);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout(0, 0));
		
		label_score = new JLabel("Score: 0");
		label_score.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(label_score, BorderLayout.CENTER);
		
		gameBoard = new gamePanel();
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(topPanel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		topPanel.add(panel, BorderLayout.EAST);
		JButton btn_newGame = new JButton("New Game");
		panel.add(btn_newGame);
		
		JButton btn_ranking = new JButton("Rank");
		btn_ranking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openScoreBoard(tableModel);
			}
		});
		panel.add(btn_ranking);
		btn_newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isGameStart && !isGameFailed && !isGameRunning) {	//
					gameBoard.requestFocus();	
					timer.start();
					isGameRunning=true;
				}else {
					gameBoard.requestFocus();
					snake.reborn();
					food.reborn();
					score=0;
					timer.start();
					isGameStart=true;
					isGameRunning=true;
					isGameFailed=false;
				}
			}
		});
		// Build a connection between client and server
		try {
			socket = new Socket("localhost", 9527);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			tableModel = new DefaultTableModel(){
	            public boolean isCellEditable(int row, int column){
	                return false;
	            }
	         };
	         initDTM();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		getContentPane().add(gameBoard, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation (screenSize.width / 2 - (this.getWidth() / 2), 
						screenSize.height / 2 - (this.getHeight() / 2));
		init();
		
	}
	
	//initialize table data
	private void initDTM() {
		try {
			String request = "0";
			writer.println(request);
			writer.flush();
			
			int rows = Integer.parseInt(reader.readLine());
			String [][]data = new String[rows][2];
			for(int i = 0; i < rows; i++) {
				data[i] =  reader.readLine().split(":");
			}
			String[] col_name = {"Name", "Score"};
			tableModel.setDataVector(data, col_name);
			tableModel.fireTableDataChanged();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void init() {
		snake=new Snake();
		food=new Food();
		score=0;
		gameBoard.setFocusable(true);
		gameBoard.addKeyListener(this);
		timer=new Timer(speedSlow,this);
		fail=new ImageIcon("pic/fail.png");
		pause=new ImageIcon("pic/pause.png");
	}
	
	//Main Gaming panel
	class gamePanel extends JPanel{
		public void paint(Graphics g) {
			super.paint(g);
			
			//background
			g.setColor(new Color(250,250,250));
			for(int i=0;i<gamePanelWidth;i+=25)
				g.drawLine(i, 0, i, gamePanelHeight);
			for(int i=0;i<gamePanelHeight;i+=25)
				g.drawLine(0, i, gamePanelWidth, i);
			
			food.food.paintIcon(this, g, food.x-(food.food.getIconWidth()/2-25/2), food.y-(food.food.getIconHeight()/2-25/2));
			
			for(int i=1;i<snake.len;i++) {
				snake.body.paintIcon(this, g, snake.x[i], snake.y[i]);
			}

			switch(snake.direction) {
			case up:snake.up.paintIcon(this, g, snake.x[0]-(snake.up.getIconWidth()/2-25/2),snake.y[0]-(snake.up.getIconHeight()-25)-3);break;
			case down:snake.down.paintIcon(this, g, snake.x[0]-(snake.down.getIconWidth()/2-25/2),snake.y[0]+3);break;
			case left:snake.left.paintIcon(this, g, snake.x[0]-(snake.left.getIconWidth()-25)-3,snake.y[0]-(snake.left.getIconHeight()/2-25/2));break;
			case right:snake.right.paintIcon(this, g, snake.x[0]+3,snake.y[0]-(snake.right.getIconHeight()/2-25/2));break;
			}
			
			if(isGameFailed) {
				fail.paintIcon(this, g, 0, 150);
			}
			
			if(isGameStart && !isGameRunning) {
				pause.paintIcon(this, g, 0, 150);
			}
		}
	}
	
	private void openScoreBoard(DefaultTableModel tableModel) {
		if(dlg_ScoreBoard!= null)dlg_ScoreBoard.setVisible(false);
		initDTM();
		dlg_ScoreBoard = new ScoreBoard(tableModel);
		dlg_ScoreBoard.setVisible(true);
	}
	
	//handle failure progress
	private void failed() {
		isGameStart=false;
		isGameRunning=false;
		isGameFailed=true;
		gameBoard.repaint();
		GameEnding dlg = new GameEnding(this, score,socket);
		dlg.setModal(true);
		dlg.show();
	}
	
	private class Snake{
		int[] x=new int [gamePanelWidth*gamePanelHeight];
		int[] y=new int [gamePanelWidth*gamePanelHeight];
		int len;
		Direction direction;
		ImageIcon up=new ImageIcon("pic/up.png");
		ImageIcon down = new ImageIcon("pic/down.png");
	    ImageIcon left = new ImageIcon("pic/left.png");
	    ImageIcon right = new ImageIcon("pic/right.png");
	    ImageIcon body = new ImageIcon("pic/body.png");
	    
	    Snake(){
	        x[2]=0;x[1]=25;x[0]=50;
	        y[2]=y[1]=y[0]=25;
	        len=3;
	        direction=direction.right;
	    }
	    
	    public void reborn() {
	    	x[2]=0;x[1]=25;x[0]=50;
	        y[2]=y[1]=y[0]=25;
	        len=3;
	        direction=direction.right;
	    }   
	}
	
	private class Food{
		Random random=new Random();
		ImageIcon food=new ImageIcon("pic/food.png");
		int x;
		int y;
		
		Food(){
			reborn();
		}
		
		public void reborn() {
			x=random.nextInt(875/25)*25;
			y=random.nextInt(700/25)*25;
			
			for(int k=0;k<5;k++) {
				boolean flag=true;
				for(int i=0;i<snake.len;i++) {
					if(x==snake.x[i] && y==snake.y[i]) {
						x=random.nextInt(875/25)*25;
						y=random.nextInt(700/25)*25;
						flag=false;
						break;
					}
				}
				if(flag)
					break;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int keyCode = e.getKeyCode();
		
		if(isGameRunning) {
			switch(keyCode) {
			case KeyEvent.VK_UP:snake.direction=(snake.direction==Direction.down)?Direction.down:Direction.up;break;
			case KeyEvent.VK_DOWN:snake.direction=(snake.direction==Direction.up)?Direction.up:Direction.down;break;
			case KeyEvent.VK_LEFT:snake.direction=(snake.direction==Direction.right)?Direction.right:Direction.left;break;
			case KeyEvent.VK_RIGHT:snake.direction=(snake.direction==Direction.left)?Direction.left:Direction.right;break;
			}
			
			if(keyCode==KeyEvent.VK_SHIFT)
				timer.setDelay(speedFast);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int keyCode = e.getKeyCode();
		

		if(keyCode==KeyEvent.VK_SHIFT)
			timer.setDelay(speedSlow);

		
		if(isGameStart) {
			if(keyCode==KeyEvent.VK_SPACE){
				if(timer.isRunning()) {
					repaint();
					timer.stop();
					isGameRunning=false;
				}
				else {
					timer.start();
					isGameRunning=true;
				}
					
			}
		}
		
		if(keyCode==KeyEvent.VK_R) {
			gameBoard.requestFocus();
			snake.reborn();
			food.reborn();
			score=0;
			timer.start();
			isGameStart=true;
			isGameRunning=true;
			isGameFailed=false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		for(int i=snake.len;i>0;i--) {
			snake.x[i]=snake.x[i-1];
			snake.y[i]=snake.y[i-1];
		}
		
		switch(snake.direction) {
		case up:snake.y[0]-=25;break;
		case down:snake.y[0]+=25;break;
		case left:snake.x[0]-=25;break;
		case right:snake.x[0]+=25;break;
		}
		
		if(snake.x[0]==food.x && snake.y[0]==food.y) {
			score++;
			snake.len++;
			food.reborn();
		}
		
		for(int i=1;i<snake.len;i++) {
			if(snake.x[0]==snake.x[i] && snake.y[0]==snake.y[i]) {
				failed();
				timer.stop();
			}
		}
		
		if(snake.x[0]<0 || snake.x[0]>gamePanelWidth-25 || snake.y[0]<0 || snake.y[0]>gamePanelHeight-25) {
			failed();
			timer.stop();
		}
		
		label_score.setText("Score:"+score);
		gameBoard.repaint();
	}

}
