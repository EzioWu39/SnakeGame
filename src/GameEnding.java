import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

public class GameEnding extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private Socket socket;
	private int score;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
//		try {
//			GameEnding dialog = new GameEnding();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Create the dialog.
	 */
	
	public GameEnding(JFrame parent, int score, Socket socket) {
		super(parent, "Summary", false);
		this.socket = socket;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new GridLayout(0, 1, 0, 0));
			{
				JLabel lblUploadYourScore = new JLabel("upload your score");
				lblUploadYourScore.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(lblUploadYourScore);
			}
			{
				JLabel label_score = new JLabel("Score: " + score);
				this.score = score;
				label_score.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(label_score);
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				{
					JLabel label_name = new JLabel("Name:");
					panel_1.add(label_name);
				}
				{
					textField = new JTextField();
					panel_1.add(textField);
					textField.setColumns(10);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btn_upload = new JButton("OK");
				btn_upload.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						upload();
					}
				});
				btn_upload.setActionCommand("OK");
				buttonPane.add(btn_upload);
				getRootPane().setDefaultButton(btn_upload);
			}
			{
				JButton btn_exit = new JButton("Cancel");
				btn_exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						quit();
					}
				});
				btn_exit.setActionCommand("Cancel");
				buttonPane.add(btn_exit);
			}
		}
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation (screenSize.width / 2 - (this.getWidth() / 2), 
						screenSize.height / 2 - (this.getHeight() / 2));
	}
	
	private void quit() {
		this.setVisible(false);
	}
	
	private void upload() {
		try {
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			String name = textField.getText().trim();
			if(name.equals("")) {
				JOptionPane.showMessageDialog(null, "Name can't be empty or only contain spaces", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else {
				String sql = "1:Insert into SCORE(Name,Score) values(" + "'" + name + "', "+ score + ")";
				writer.println(sql);
				writer.flush();
				this.setVisible(false);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
