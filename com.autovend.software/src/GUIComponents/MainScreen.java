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
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;

public class MainScreen {
	private JFrame frame;
	private int frameWidth;
	private int frameHeight;
	private GridBagConstraints gbc = new GridBagConstraints();
	private static DefaultListModel calls;
	private JList callsList;
	private static int index = 1;

	private JButton startupShutdownButton;
	private JButton removeItemsButton;
	private JButton permitPreventButton;
	private JButton addItemByTextSearchButton;
	private JButton adjustBanknotesButton;
	private JButton noBagRequestButton;
	private JButton adjustCoinsButton;
	private JButton weightDiscrepancyButton;
	private JButton logoutButton;
	
	static SupervisionStation station;
	static AttendantCommunicationsController attendantCommunicationsController;

	public MainScreen(SupervisionStation superStation,
			AttendantCommunicationsController attendantCommunicationsController) {
		initialRegistration(superStation, attendantCommunicationsController);

		// top left bottom right
		gbc.insets = new Insets(0, 10, 25, 10);
		frame.setLayout(new GridBagLayout());
		frameWidth = frame.getWidth();
		frameHeight = frame.getHeight();
		
		addMainLabel();
		gbc.insets = new Insets(0, 10, 25, 75);
		addActionsLabel();
		addButtons();
		gbc.insets = new Insets(0, 10, 25, 10);
		addCallboardLabel();
		addCallBoard();

		frame.revalidate();
		frame.repaint();
	}
	
	private class CallItemRenderer extends JLabel implements ListCellRenderer<String> {

		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus) {
			JLabel visualValue = new JLabel(value);
			visualValue.setFont(GUIConstants.SMALL_FONT);
			visualValue.setForeground(Color.BLACK);
			visualValue.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
			
			// shouldn't be able to click on items in customer requests; only visual
			visualValue.setEnabled(false);
			return visualValue;
		}
		
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
		frame = superStation.screen.getFrame();
	}
	
	private void addMainLabel() {
		JLabel screenLabel = new JLabel("Attendant Main Page");
		screenLabel.setFont(GUIConstants.LARGE_FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addActionsLabel() {
		JLabel screenLabel = new JLabel("Actions");
		screenLabel.setFont(GUIConstants.FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY.darker());
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addCallboardLabel() {
		JLabel screenLabel = new JLabel("Call Board");
		screenLabel.setFont(GUIConstants.FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY.darker());
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		frame.add(screenLabel, gbc);
	}
	
	private void addButtons() {
		JPanel buttonPanel = new JPanel();
		// intentionally use same spacing for horizontal and vertical; not a typo
		buttonPanel.setLayout(new GridLayout(5, 2, frameHeight / 30, frameHeight / 30));
		
		addStartupShutdownButton(buttonPanel);
		addRemoveItemsButton(buttonPanel);
		addPermitPreventButton(buttonPanel);
		addItemByTextSearchButton(buttonPanel);
		// addAdjustBanknotesButton(buttonPanel); uncomment if needed
		addNoBagRequestButton(buttonPanel);
		// addAdjustCoinsButton(buttonPanel); uncomment if needed
		addWeightDiscrepancyButton(buttonPanel);
		addLogoutButton(buttonPanel);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.ipady = (frameHeight / 5);
		gbc.ipadx = 0;
		buttonPanel.setOpaque(false);
		frame.add(buttonPanel, gbc);
	}
	
	private void addStartupShutdownButton(JPanel panel) {
		startupShutdownButton = new JButton("Startup / Shutdown Station(s)");
		startupShutdownButton.setFont(GUIConstants.MEDIUM_FONT);
		panel.add(startupShutdownButton);
		
		startupShutdownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				StartupShutdownWindow window = new StartupShutdownWindow(station, attendantCommunicationsController);
			}
		});
	}
	
	private void clearScreen() {
		frame.dispose();
		frame.getContentPane().removeAll();
		frame.revalidate();
		frame.repaint();
	}
	
	private void addRemoveItemsButton(JPanel panel) {
		removeItemsButton = new JButton("Remove Item");
		removeItemsButton.setFont(GUIConstants.MEDIUM_FONT);
		panel.add(removeItemsButton);
		
		removeItemsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				RemoveItem.run(station, attendantCommunicationsController);
			}
		});
	}
	
	private void addPermitPreventButton(JPanel panel) {
		permitPreventButton = new JButton("Permit / Prevent Station(s) Use");
		permitPreventButton.setFont(GUIConstants.MEDIUM_FONT);
		panel.add(permitPreventButton);
		
		permitPreventButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				PreventPermitWindow window = new PreventPermitWindow(station, attendantCommunicationsController);
			}
		});
	}
	
	private void addItemByTextSearchButton(JPanel panel) {
		addItemByTextSearchButton = new JButton("Add Item by Text Search");
		addItemByTextSearchButton.setFont(GUIConstants.MEDIUM_FONT);
		panel.add(addItemByTextSearchButton);
		
		addItemByTextSearchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				AddItemByTextWindow addItemByTextWindow = new AddItemByTextWindow(station, attendantCommunicationsController);
			}
		});
	}
	
	private void addAdjustBanknotesButton(JPanel panel) {
		adjustBanknotesButton = new JButton("Adjust Banknotes for Change");
		adjustBanknotesButton.setFont(GUIConstants.MEDIUM_FONT);
		panel.add(adjustBanknotesButton);
		
		// adjustBanknotesButton.addActionListener(this);
	}
	
	private void addNoBagRequestButton(JPanel panel) {
		noBagRequestButton = new JButton("Bagging Verification");
		noBagRequestButton.setFont(GUIConstants.MEDIUM_FONT);
		panel.add(noBagRequestButton);
		
		noBagRequestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				AttendantNoBagVerification window = new AttendantNoBagVerification(station,
				attendantCommunicationsController);
			}
		});
	}
	
	
	private void addAdjustCoinsButton(JPanel panel) {
		adjustCoinsButton = new JButton("Adjust Coins for Change");
		adjustCoinsButton.setFont(GUIConstants.MEDIUM_FONT);
		panel.add(adjustCoinsButton);
		
		// adjustCoinsButton.addActionListener(this);
	}
	
	private void addWeightDiscrepancyButton(JPanel panel) {
		weightDiscrepancyButton = new JButton("Solve Weight Discrepancy");
		weightDiscrepancyButton.setFont(GUIConstants.MEDIUM_FONT);
		weightDiscrepancyButton.setForeground(Color.BLACK);
		panel.add(weightDiscrepancyButton);
		
		weightDiscrepancyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				AttendantWeightVerification window = new AttendantWeightVerification(station, attendantCommunicationsController);
			}
		});
	}
	
	private void addLogoutButton(JPanel panel) {
		logoutButton = new JButton("Logout");
		logoutButton.setFont(GUIConstants.MEDIUM_FONT);
		logoutButton.setForeground(Color.RED);
		panel.add(logoutButton);
		
		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreen();
				LogoutWindow l = new LogoutWindow(station, attendantCommunicationsController);
			}
		});
	}
	
	private void addCallBoard() {
		if(index == 1) {
			calls =  new DefaultListModel<>();;
		}
		callsList = new JList<String>(calls);
		callsList.setBackground(new Color(218, 216, 255));	// light purple
		callsList.setCellRenderer(new CallItemRenderer());
		callsList.setFont(GUIConstants.SMALL_FONT);
		
		if (index == 1) {
			calls.addElement("Currently no calls.");
			index++;
		}
		
		// if there are too many calls then attendant should be able to scroll through all of them
		JScrollPane listScrollPane = new JScrollPane(callsList);
		
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.ipady = 0;
		gbc.ipadx = (frameWidth / 5);
		frame.add(listScrollPane, gbc);
	}


	// getters for testing

	public JButton getStartupShutdownButton() {
		return startupShutdownButton;
	}

	public JButton getPermitPreventButton() {
		return permitPreventButton;
	}

	public JButton getAdjustBanknotesForChangeButton() {
		return adjustBanknotesButton;
	}

	public JButton getAdjustCoinsForChangeButton() {
		return adjustCoinsButton;
	}

	public JButton getRemoveItemsButton() {
		return removeItemsButton;
	}

	public JButton getAddItemByTextSearchButton() {
		return addItemByTextSearchButton;
	}

	public JButton getNoBagButton() {
		return noBagRequestButton;
	}

	public JButton getLogoutButton() {
		return logoutButton;
	}

	public void addCall(String call) {
		if (calls.getElementAt(0).equals("Currently no calls.")) {
			calls.clear();
		}
		calls.addElement(call);
	}
}
