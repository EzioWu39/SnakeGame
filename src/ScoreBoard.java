import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;

public class ScoreBoard extends JDialog {
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScoreBoard frame = new ScoreBoard();
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
	public ScoreBoard() {
		setBounds(100, 100, 450, 300);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.NORTH);
		
		table = new JTable();
		getContentPane().add(table, BorderLayout.CENTER);

	}
	
	public ScoreBoard(DefaultTableModel tableModel) {
		//setBounds(200, 200, 200, 300);
		setBounds(100, 100, 450, 300);
		this.setResizable(false);
		table = new JTable();
		table.setModel(tableModel);
		table.setVisible(true);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.add(table);
		scrollPane.setViewportView(table);
		add(scrollPane);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation (screenSize.width / 2 - (this.getWidth() / 2), 
						screenSize.height / 2 - (this.getHeight() / 2));
	}

}
