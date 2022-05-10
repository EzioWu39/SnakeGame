import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Server extends JFrame implements Runnable{

	private JTextArea textArea;
	private Sql sql;
	PreparedStatement getRanking;
	public Server() {
		// TODO Auto-generated constructor stub
		super("Game Server");
		this.setSize(400, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sql = new Sql();
		
		//clientList = new ArrayList<>();
		textArea = new JTextArea(10,10);
		textArea.setEditable(false);
		JScrollPane sp = new JScrollPane(textArea);
		this.add(sp);
		this.setVisible(true);
		Thread t = new Thread(this);
		t.start();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server gameServer = new Server();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket serverSocket = new ServerSocket(9527);
			textArea.append("Game Server started at" + new Date() + "\n");
			
			while(true) {
				//Listen to a new connection request
				Socket socket = serverSocket.accept();
				
				//Create and start a new thread for the connection
				new Thread(new HandleClient(socket)).start();
			}
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}
	
	class HandleClient implements Runnable{
		private Socket socket;
		BufferedReader reader;
		PrintWriter writer;
		
		public HandleClient(Socket socket) {
			this.socket = socket;
			try {
				this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.writer = new PrintWriter(socket.getOutputStream(), true);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String[] clientMsg = new String[2];
				ResultSet res = null;
				while(true) {
					clientMsg = reader.readLine().split(":");
					if(clientMsg[0].equals("0")) {	//GetRanking
						try {
							res = sql.executeQuery("Select Name,Score from SCORE order by Score Desc");
							String count_sql = "Select count(Name) from SCORE";
							ResultSet count = sql.executeQuery(count_sql);
							count.next();
							writer.println(count.getString(1));
							writer.flush();
							while(res.next()) {
								writer.println(res.getString(1) + ":" + res.getString(2));
								writer.flush();
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						//insert player score
						sql.executeUpdate(clientMsg[1]);
						textArea.append(clientMsg[1] + '\n');
						//res = sql.executeQuery("Select Name,Score from SCORE order by Score Desc");
					}
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}

		
	}

}
