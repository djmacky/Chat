import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;


public class Driver extends JFrame{

	/**
	 * @param args
	 */
	
	static JFrame frame;
	static JPanel panel;
	static JPanel bottom;
	static JLabel question;
	static JRadioButton server;
	static JRadioButton client;
	static JButton ok;
	static JButton close;
	static ButtonGroup buttonGroup;
	static Dimension dim;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		init();
		
	}
	
	private static void init() {
		// create instances
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		frame = new JFrame("New Instance");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(100,200);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(new EmptyBorder(new Insets(20, 0, 20, 40)));
		
		question = new JLabel("Please select one of the following options:");
		server = new JRadioButton("Server instance");
		client = new JRadioButton("Client instance");
		ok = new JButton("OK");
		close = new JButton("Close");
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(server);
		buttonGroup.add(client);
		
		panel.add(question);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(server);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		panel.add(client);
		panel.add(Box.createRigidArea(new Dimension(0, 15)));
		
		bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		bottom.add(Box.createHorizontalGlue());
		bottom.add(ok);
		bottom.add(Box.createRigidArea(new Dimension(5, 0)));
		bottom.add(close);
		bottom.add(Box.createRigidArea(new Dimension(15, 0)));
	
		panel.add(bottom);
		frame.getContentPane().add(panel); // add to a container
		server.setSelected(true); // set state
		ok.setMnemonic(KeyEvent.VK_N);
		displayWindow();
	}
	
	private int getSelectedOption() {
		int retval = -1;
		// check state
		if (server.isSelected()) {
		 
		    // do something...
		 
		} else {
		 
		    // do something else...
		 
		}

		
		
		return retval;
	}
	
	private static void displayWindow() { 
		// show the window
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.pack();
		frame.setVisible(true);
	}

}
