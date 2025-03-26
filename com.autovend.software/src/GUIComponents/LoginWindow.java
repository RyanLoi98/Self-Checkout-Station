/**
 SENG300 Iteration 3 Group 1 Members
 Prempreet Brar (30112576)
 Justin Yee (30113485)
 Saad Abdullah (30142511)
 Jinze Xu (10106091)
 George Vassilev (30123526)
 Carter Tam (30146065)
 Filip Cotra (30086750)
 Nicole Maurer (30077223)
 Vita Vysochina (30118374)
 Alex Tran (30075197)
 Ryan Loi (30019520)
 Aaron St. Omer (30144511)
 Fares Senjar (30113420)
 Arian Safari (30161346)
 Taylor Diegel (30110147)
 Jamie MacDonald (10174719)
 Darren Roszell (30163669)
 Mankaranpreet Pandher (30154733)
 Ba Khanh Tung Nguyen (30116961)
 Sornali Banik (30143087)
 Adrian Llonor (30018780)
 Farbod Moghaddam (30115199)
 Namit Aneja (30146188)
 Ramin Kahidi (30110218)
 Abdullah Zubair (30113730)
 **/

package GUIComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.Keyboard;
import com.autovend.devices.SupervisionStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.KeyboardObserver;
import com.autovend.software.AttendantCommunicationsController;
import com.autovend.software.CustomerIO;

public class LoginWindow implements ActionListener, KeyboardObserver {
	// Administrator credentials only employees should know
	private String adminUserID = "admin";
	private String adminPassword = "admin";

	// For expedited testing
	private String testUserID = "test";
	private String testPassword = "test";

	// These booleans are for testing, allows for simulated selection of textFields
	boolean userIdSelected = false;
	boolean passwordSelected = false;
	boolean userTyped = false;
	boolean pswdTyped = false;
	boolean loginSuccess = false;
	boolean loginFailed = false;

	static HashMap<String, String> users = new HashMap<String, String>();

	private JFrame frame;
	private int frameWidth;
	private int frameHeight;
	
	private GridBagConstraints gbc = new GridBagConstraints();
	private JButton verifyButton = new JButton("Verify");
	private JTextField userId;
	private JPasswordField password;
	private JLabel label;
	private JLabel label2;
	private JLabel label3;
	static SupervisionStation station;
	static AttendantCommunicationsController attendantCommunicationsController;
	private MainScreen screen;

	public LoginWindow(SupervisionStation superStation,
			AttendantCommunicationsController attendantCommunicationsController) {	
		initialRegistration(superStation, attendantCommunicationsController);
		this.attendantCommunicationsController = attendantCommunicationsController;
		
		gbc.insets = new Insets(0, 10, 25, 10);
		frame.setLayout(new GridBagLayout());
		frameWidth = frame.getWidth();
		frameHeight = frame.getHeight();
		
		addLoginLabel();
		addUserIDLabel();
		addUserIDField();
		addPasswordLabel();
		addPasswordField();
		addVerifyButton();
		
		frame.revalidate();
		frame.repaint();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/*
	 * Purpose: Allow the attendant to login to a station.
	 */
	private void initialRegistration(SupervisionStation superStation, 
			AttendantCommunicationsController attendantCommunicationsController) {
		/*
		 * Get the touchscreen of the station, add the user and the ability to type in using a keyboard.
		 */
		station = superStation;
		this.attendantCommunicationsController = attendantCommunicationsController;
		superStation.keyboard.register(this);
		frame = superStation.screen.getFrame();
		addUser(testUserID, testPassword);
		
		// we want the login screen to show up first
		frame.revalidate();
		frame.repaint();
	}
	
	private void addLoginLabel() {
		JLabel screenLabel = new JLabel("Login");
		screenLabel.setFont(GUIConstants.LARGE_FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addUserIDLabel() {
		JLabel screenLabel = new JLabel("USER ID", SwingConstants.RIGHT);
		screenLabel.setFont(GUIConstants.FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY.darker());
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addUserIDField() {
		userId = new JTextField();
		userId.setFont(GUIConstants.FONT);			
		userId.setHorizontalAlignment(JTextField.CENTER);
		userId.setBorder(new LineBorder(Color.BLACK, 2));
			
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.ipady = (frameHeight / 15);
		gbc.ipadx = (frameWidth / 3);
		frame.add(userId, gbc);
	}
	
	private void addPasswordLabel() {
		JLabel screenLabel = new JLabel("PASSWORD");
		screenLabel.setFont(GUIConstants.FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY.darker());
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addPasswordField() {
		password = new JPasswordField();
		password.setFont(GUIConstants.FONT);			
		password.setHorizontalAlignment(JTextField.CENTER);
		password.setBorder(new LineBorder(Color.BLACK, 2));
			
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.ipady = (frameHeight / 15);
		gbc.ipadx = (frameWidth / 3);
		frame.add(password, gbc);
	}
	
	private void addVerifyButton() {
		verifyButton = new JButton("VERIFY");
		verifyButton.setFont(GUIConstants.SMALL_FONT);
		verifyButton.setForeground(Color.GREEN.brighter());
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		frame.add(verifyButton, gbc);
		
		verifyButton.addActionListener(this);
	}
	
	private void showIncorrectLabel() {
		JOptionPane.showMessageDialog(
				frame, "The user id and/or password entered are incorrect.", "Invalid Credentials",
				JOptionPane.ERROR_MESSAGE);
	}
	
	private void startUpSuperUser() {
		SuperUserWindow.isSuperUser = true;
		frame.getContentPane().removeAll();
		frame.revalidate();
		frame.repaint();
		SuperUserWindow s = new SuperUserWindow(station, attendantCommunicationsController);
	}
	
	private void startUpRegularAttendant() {
		SuperUserWindow.isSuperUser = false;
		loginSuccess = true;
		frame.getContentPane().removeAll();
		frame.revalidate();
		frame.repaint();
		screen = new MainScreen(station, attendantCommunicationsController);
	}
	
	private void tellUserLoginFailed() {
		loginFailed = true;
		showIncorrectLabel();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// If userID and password match, allow use
		// Otherwise, deny access and display message
		if (e.getSource() == verifyButton) {
			if (userId.getText().equals(adminUserID) && String.valueOf(password.getPassword()).equals(adminPassword)) {
				startUpSuperUser();
			} 
			else if (users.containsKey(userId.getText())
					&& users.get(userId.getText()).equals(String.valueOf(password.getPassword()))) 
			{
				startUpRegularAttendant();
			} else {
				tellUserLoginFailed();
			}
		}

	}

	public static void addUser(String user, String pswd) {
		users.put(user, pswd);
	}

	// getters for testing
	public JButton getVerifyButton() {
		return verifyButton;
	}

	public String getUsertext() {
		return userId.getText();
	}

	public String getPswdtext() {
		return String.valueOf(password.getPassword());
	}

	public void selectUserID() {
		userIdSelected = true;
	}

	public void deSelectUserID() {
		userIdSelected = false;
	}

	public void selectPassword() {
		passwordSelected = true;
	}

	public void deSelectPassword() {
		passwordSelected = false;
	}

	public boolean getUserTyped() {
		return userTyped;
	}

	public boolean getPswdTyped() {
		return pswdTyped;
	}

	public boolean getLoginSuccess() {
		return loginSuccess;
	}

	public boolean getLoginFailed() {
		return loginFailed;
	}

	public static HashMap<String, String> getUsers() {
		return users;
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// Not Used
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// Not Used
	}

	/**
	 * Simulates typing during automated JUnit tests
	 */
	@Override
	public void reactToKeyPressedEvent(Keyboard k, char c) {
		if (userIdSelected) {
			userTyped = true;
			userId.setText(userId.getText() + c);
		}

		else if (passwordSelected) {
			pswdTyped = true;
			password.setText(String.valueOf(password.getPassword()) + c);
		}
	}

	public void sendCall(String call) {
		screen.addCall(call);

	}
}