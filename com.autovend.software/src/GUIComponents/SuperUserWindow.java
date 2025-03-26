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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import com.autovend.software.SelfCheckoutStationLogic;

public class SuperUserWindow implements ActionListener, KeyboardObserver {
	public static boolean isSuperUser = false;
	
	private JFrame frame;
	private int frameWidth;
	private int frameHeight;
	private GridBagConstraints gbc = new GridBagConstraints();
	
	private JButton addUserButton = new JButton("Add User");
	private JButton logoutButton = new JButton("Logout");
	private JButton membershipButton = new JButton("Update Membership Length");
	private JTextField userId;
	private JPasswordField password;
	
	private JTextField membershipLength;
	private SupervisionStation station;
	private AttendantCommunicationsController attendantCommunicationsController;
	
	// Booleans for testing
	boolean userIdSelected = false;
	boolean passwordSelected = false;
	boolean mSelected = false;
	boolean userTyped = false;
	boolean pswdTyped = false;
	boolean userExistsError = false;
	boolean loggedOut = false;

	public SuperUserWindow(SupervisionStation superStation,
			AttendantCommunicationsController attendantCommunicationsController) {

		initialRegistration(superStation, attendantCommunicationsController);
		
		// top left bottom right
		frame.setLayout(new GridBagLayout());
		frameWidth = frame.getWidth();
		frameHeight = frame.getHeight();
		
		gbc.insets = new Insets(0, 10, 50, 10);
		addSuperUserLabel();
		gbc.insets = new Insets(0, 10, 25, 10);
		addUserIDLabel();
		addUserIDField();
		addPasswordLabel();
		addPasswordField();
		addMembershipLengthLabel();
		addMembershipLengthField();
		addAddUserButton();
		addMembershipLengthButton();
		gbc.insets = new Insets(50, 10, 25, 10);
		addLogoutButton();

		frame.revalidate();
		frame.repaint();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/*
	 * Purpose: Allow the attendant to login to a station.
	 */
	private void initialRegistration(SupervisionStation superStation, AttendantCommunicationsController attendantCommunicationsController) {
		/*
		 * Get the touchscreen of the station, add the user and the ability to type in using a keyboard.
		 */
		station = superStation;
		this.attendantCommunicationsController = attendantCommunicationsController;
		superStation.keyboard.register(this);
		frame = superStation.screen.getFrame();
	}
	
	private void addSuperUserLabel() {
		JLabel screenLabel = new JLabel("Superuser Settings (Caution)");
		screenLabel.setFont(GUIConstants.LARGE_FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addUserIDLabel() {
		JLabel screenLabel = new JLabel("USER ID", SwingConstants.RIGHT);
		screenLabel.setFont(GUIConstants.MEDIUM_FONT);
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
		userId.setFont(GUIConstants.MEDIUM_FONT);			
		userId.setHorizontalAlignment(JTextField.CENTER);
		userId.setBorder(new LineBorder(Color.BLACK, 2));
			
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.ipady = (frameHeight / 30);
		gbc.ipadx = (frameWidth / 7);
		frame.add(userId, gbc);
	}
	
	private void addPasswordLabel() {
		JLabel screenLabel = new JLabel("PASSWORD");
		screenLabel.setFont(GUIConstants.MEDIUM_FONT);
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
		password.setFont(GUIConstants.MEDIUM_FONT);			
		password.setHorizontalAlignment(JTextField.CENTER);
		password.setBorder(new LineBorder(Color.BLACK, 2));
			
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.ipady = (frameHeight / 30);
		gbc.ipadx = (frameWidth / 7);
		frame.add(password, gbc);
	}
	
	private void addAddUserButton() {
		addUserButton = new JButton("Add User");
		addUserButton.setFont(GUIConstants.SMALL_FONT);
		addUserButton.setForeground(Color.GREEN.brighter());
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.ipady = (frameHeight / 30);
		gbc.ipadx = (frameWidth / 15);
		frame.add(addUserButton, gbc);
		
		addUserButton.addActionListener(this);
	}
	
	private void addMembershipLengthLabel() {
		JLabel screenLabel = new JLabel("LENGTH");
		screenLabel.setFont(GUIConstants.MEDIUM_FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY.darker());
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addMembershipLengthField() {
		membershipLength = new JTextField();
		membershipLength.setFont(GUIConstants.MEDIUM_FONT);			
		membershipLength.setHorizontalAlignment(JTextField.CENTER);
		membershipLength.setBorder(new LineBorder(Color.BLACK, 2));
			
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.ipady = (frameHeight / 30);
		gbc.ipadx = (frameWidth / 7);
		frame.add(membershipLength, gbc);
	}
	
	private void addMembershipLengthButton() {
		membershipButton = new JButton("Update Membership Length");
		membershipButton.setFont(GUIConstants.SMALL_FONT);
		membershipButton.setForeground(Color.BLUE);
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.ipady = (frameHeight / 30);
		gbc.ipadx = (frameWidth / 15);
		frame.add(membershipButton, gbc);
		
		membershipButton.addActionListener(this);
	}
	
	private void addLogoutButton() {
		logoutButton = new JButton("LOGOUT");
		logoutButton.setFont(GUIConstants.SMALL_FONT);
		logoutButton.setForeground(Color.RED);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 4;
		gbc.ipady = (frameHeight / 30);
		gbc.ipadx = (frameWidth / 15);
		frame.add(logoutButton, gbc);
		
		logoutButton.addActionListener(this);
	}
	
	private void userAddedPopup() {
		JOptionPane.showMessageDialog(
				frame, ("A user with ID: " + userId.getText() + " and password: ___ was created. "), "Success!",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	private void userAlreadyExistsPopup() {
		userExistsError = true;
		JOptionPane.showMessageDialog(
				frame, "A user with the given user id already exists.", "Duplicate Entry!",
				JOptionPane.ERROR_MESSAGE);
	}
	
	private void logoutSuperUser() {
		loggedOut = true;
		isSuperUser = true;
		
		frame.getContentPane().removeAll();
		frame.revalidate();
		frame.repaint();
		LogoutWindow l = new LogoutWindow(station, attendantCommunicationsController);
	}
	
	private void updateMembershipLength() {
		String memLength = membershipLength.getText();
		// this will hold the integer version of the membership length
		int imemLength;
		// use try incase conversion to an int fails (when the user puts in an
		// alphabetical character)
		try {
			imemLength = Integer.parseInt(memLength);
			if (imemLength <= 0) {
				membershipLength.setText("Error: length must be an int > 0");
			} else {
				ArrayList<SelfCheckoutStationLogic> logicList = attendantCommunicationsController.getStationLogicList();
				// Loop through each scsLogic in the list, and check if the length value is different than the input
				for (int i = 0; i < logicList.size(); i++) {
					SelfCheckoutStationLogic scslogic = logicList.get(i);
					// If the same, notify the supervisor that it is the same
					if (imemLength == scslogic.getMembershipLen()) {
						String sameLen = "Length already " + imemLength +"!";
						membershipLength.setText(sameLen);
					}
					// Else, update the membership length of each station's logic.
					// Note this update will clear the membership database, as the old numbers
					// are no longer of a valid length
					else {
						try {
							scslogic.editMembershipLen(imemLength);
							membershipLength.setText("Success! Old database wiped.");
						} catch(Exception err1) {
							membershipLength.setText("Error wiping old database.");
						}
					}
				}
				
			}
			// throws an error message if the user entered in a character and thus the
			// software cannot convert it to an int
		} catch (Exception err) {
			membershipLength.setText("Error: length must be an int > 0");
		}
	}
	
	

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addUserButton) {
			if (!LoginWindow.users.containsKey(userId.getText())) {
				LoginWindow.addUser(userId.getText(), String.valueOf(password.getPassword()));
				userAddedPopup();
			} else {
				userAlreadyExistsPopup();
			}
		} else if (e.getSource() == logoutButton) {
			logoutSuperUser();
		} else if (e.getSource() == membershipButton) {
			updateMembershipLength();
		}
	}
	
	// Getters for testing
	public JButton getAddButton() {
		return addUserButton;
	}
	
	public JButton getLogoutButton() {
		return logoutButton;
	}
	
	public JButton getMembershipButton() {
		return membershipButton;
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
	
	public boolean getLoggedout() {
		return loggedOut;
	}
	
	public boolean getUserExists() {
		return userExistsError;
	}
	
	public void selectMembership() {
		mSelected = true;
	}
	
	public void deSelectMembership() {
		mSelected = false;
	}
	
	public String getMembershipMessage() {
		return membershipLength.getText();
	}
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
		else if (mSelected) {
			membershipLength.setText(userId.getText() + c);
		}

	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

}
