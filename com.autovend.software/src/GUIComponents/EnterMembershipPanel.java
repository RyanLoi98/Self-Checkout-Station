package GUIComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;
import com.autovend.software.SelfCheckoutStationLogic;

public class EnterMembershipPanel extends JPanel {
	private int frameWidth;
	private int frameHeight;
	private CustomerIO customerIO;
	private GridBagConstraints gbc = new GridBagConstraints();
	
	private JTextField membershipNumber;
	private JButton backButton;
	private JButton enterButton;
	private JButton oneButton;
	private JButton twoButton;
	private JButton threeButton;
	private JButton fourButton;
	private JButton fiveButton;
	private JButton sixButton;
	private JButton sevenButton;
	private JButton eightButton;
	private JButton nineButton;
	private JButton zeroButton;
	private JButton delButton;
	private JButton clearButton;

	private SelfCheckoutStationLogic scsLogic;
	
	public EnterMembershipPanel(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic, int frameWidth, int frameHeight) {
		setLayout(new GridBagLayout());
		this.customerIO = customerIO;
		this.scsLogic = scsLogic;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;

		
		// top, left, bottom, right padding
		gbc.insets = new Insets(0, 0, 20, 0);
		addScreenLabel();
		gbc.insets = new Insets(0, 0, 0, 0);
		addScanOrSwipeLabel();
		addEnterBelowLabel();
		gbc.insets = new Insets(30, 0, 0, 0);
		addMembershipFieldLabel();
		gbc.insets = new Insets(0, 0, 10, 0);
		addMembershipField();
		addKeyboard();
		addEnterButton();
		addBackButton();
	}
	
	private void addScreenLabel() {
		JLabel screenLabel = new JLabel("Enter Membership Number");
		screenLabel.setFont(GUIConstants.LARGE_FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		add(screenLabel, gbc);
	}
	
	private void addScanOrSwipeLabel() {
		JLabel scanOrSwipeLabel = new JLabel("You may scan or swipe your membership card.");
		scanOrSwipeLabel.setFont(GUIConstants.FONT);
		scanOrSwipeLabel.setForeground(Color.LIGHT_GRAY.darker());
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		add(scanOrSwipeLabel, gbc);
	}

	private void addEnterBelowLabel() {

			JLabel enterBelowLabel = new JLabel("You may also enter your membership number in below.");
			enterBelowLabel.setFont(GUIConstants.FONT);
			enterBelowLabel.setForeground(Color.LIGHT_GRAY.darker());
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.ipady = 0;
			gbc.ipadx = 0;
			add(enterBelowLabel, gbc);
	}
	
	private void addMembershipFieldLabel() {
		JLabel membershipFieldLabel = new JLabel("Membership #");
		membershipFieldLabel.setFont(GUIConstants.FONT);
		membershipFieldLabel.setForeground(Color.DARK_GRAY);
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		add(membershipFieldLabel, gbc);
	}
	
	private void addMembershipField() {
		membershipNumber = new JTextField("");
		membershipNumber.setEditable(false);
		membershipNumber.setFont(GUIConstants.FONT);
		membershipNumber.setHorizontalAlignment(JTextField.CENTER);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth);
		add(membershipNumber, gbc);
	}
	
	private void addKeyboard() {
		JPanel keyboardPanel = new JPanel(new GridLayout(4, 10, 1, 1));
		keyboardPanel.setPreferredSize(new Dimension(frameWidth, frameHeight));
		String[] keyLabels = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "DEL", "CLR"};
		
		// Loop to create each key
		for (String label : keyLabels) {
			JButton keyButton = new JButton(label);
			keyButton.setPreferredSize(new Dimension(20, 0));
			keyButton.setFont(new Font("MV Boli", Font.PLAIN, 15));
			keyButton.setFocusable(false);
			
			/* Change the text in the membership field according to the letter pressed. */
			keyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(enterFlag){
						membershipNumber.setText("");
						membershipNumber.setFont(GUIConstants.FONT);
						enterFlag = false;
					}

					// Set functionaility for delete key
					if (keyButton.getText().equals("DEL")) {
						if (!membershipNumber.getText().isEmpty()) {
							membershipNumber.setText(membershipNumber.getText().substring(0, membershipNumber.getText().length() - 1));
						}
					} 
					// Set functionality for clear key
					else if(keyButton.getText().equals("CLR")) {
						membershipNumber.setText("");
					}
					// Set functionality for numbered keys
					else {
						membershipNumber.setText(membershipNumber.getText() + keyButton.getText());
					}
				}
			});
			
			// Added buttons to external buttons to be used in getters to allow for test pressing
			if (label.equals("1")) {
				oneButton = keyButton;
				keyboardPanel.add(oneButton);
			}
			
			if (label.equals("2")) {
				twoButton = keyButton;
				keyboardPanel.add(twoButton);
			}
			
			if (label.equals("3")) {
				threeButton = keyButton;
				keyboardPanel.add(threeButton);
			}
			
			if (label.equals("4")) {
				fourButton = keyButton;
				keyboardPanel.add(fourButton);
			}
			
			if (label.equals("5")) {
				fiveButton = keyButton;
				keyboardPanel.add(fiveButton);
			}
			
			if (label.equals("6")) {
				sixButton = keyButton;
				keyboardPanel.add(sixButton);
			}
			
			if (label.equals("7")) {
				sevenButton = keyButton;
				keyboardPanel.add(sevenButton);
			}
			
			if (label.equals("8")) {
				eightButton = keyButton;
				keyboardPanel.add(eightButton);
			}
			
			if (label.equals("9")) {
				nineButton = keyButton;
				keyboardPanel.add(nineButton);
			}
			
			if (label.equals("0")) {
				zeroButton = keyButton;
				keyboardPanel.add(zeroButton);
			}
			
			if (label.equals("DEL")) {
				delButton = keyButton;
				keyboardPanel.add(delButton);
			}
			
			if (label.equals("CLR")) {
				clearButton = keyButton;
				keyboardPanel.add(clearButton);
			}
			
		}
		
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		add(keyboardPanel, gbc);
	}
	
	private void addBackButton() {
		backButton = new JButton("Go Back");
		backButton.setFont(GUIConstants.SMALL_FONT);
		backButton.setForeground(Color.RED.darker());
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.ipady = (frameHeight / 50);
		gbc.ipadx = (frameWidth / 50);
		add(backButton, gbc);	
		
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				membershipNumber.setText("");
				membershipNumber.setFont(GUIConstants.FONT);
				enterFlag = false;
				customerIO.showAddPanel();
			}
		});
	}

	// Flag to check if enter button pressed
	private boolean enterFlag = false;
	
    
	// Getters for GUI buttons
    public JButton getEnterButton() {
    	return enterButton;
    }
    
    public JButton getBackButton() {
    	return backButton;
    }
    
    public String getMembershipMessage() {
    	return membershipNumber.getText();
    }

	public JTextField getMembershipNumber() {
		return membershipNumber;
	}

    public JButton getOneButton() {
    	return oneButton;
    }
    
    public JButton getTwoButton() {
    	return twoButton;
    }
    
    public JButton getThreeButton() {
    	return threeButton;
    }
    
    public JButton getFourButton() {
    	return fourButton;
    }
    
    public JButton getFiveButton() {
    	return fiveButton;
    }
    
    public JButton getSixButton() {
    	return sixButton;
    }
    
    public JButton getSevenButton() {
    	return sevenButton;
    }
    
    public JButton getEightButton() {
    	return eightButton;
    }
    
    public JButton getNineButton() {
    	return nineButton;
    }
    
    public JButton getZeroButton() {
    	return zeroButton;
    }
    
    public JButton getDelButton() {
    	return delButton;
    }
    
    public JButton getClearButton() {
    	return clearButton;
    }

    /*
     * Method to create the enter button
     */
	private void addEnterButton() {
		enterButton = new JButton("Enter");
		enterButton.setFont(GUIConstants.FONT);
		enterButton.setForeground(Color.GREEN.darker());
		gbc.gridx = 2;
		gbc.gridy = 6;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		add(enterButton, gbc);	
		
		// Logic for enter button click
		enterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enterFlag = true;

				// Check if database was corrupt on upload
				if (customerIO.getDataBaseStatus()) {
					membershipNumber.setFont(new Font("Arial", Font.PLAIN, 20));
					membershipNumber.setText("Error: membership database was unable to be loaded");
				} else {
					
					// Check if member card was scanned or swiped in successfully
						if(customerIO.getMemberCardUsed()) {
							// If so, load the member card number into the textfield for verification
							membershipNumber.setText(customerIO.getMemberCardNum());
						}
					
					String membershipStr = membershipNumber.getText();

					// Check if a swipe was attempted but did not succeed (magnetic stripe failure/corruption)
					if (customerIO.getSwipeAttemptFlag() && !customerIO.getMemberCardUsed()) {
						membershipNumber.setText("Card swipe error, please try again");
						// Reset swipe attempt flag to false for future swipe events
						customerIO.setSwipeAttemptFlag(false);
					} 
					// In all other situations, the member number will be validated
					else {
						// Reset member card variables to defaults to allow for subsequent scans/swipes
						customerIO.setMemberCardUsed(false);
						customerIO.setMemberCardNum("");
						customerIO.setSwipeAttemptFlag(false);
						
						// Validate member number
					for (CustomerIOListener communicator : customerIO.getListeners()) {
						int statusCode = communicator.typedMembership(customerIO, scsLogic, membershipStr);
						// Status code 0 means member exists and no issues noted
						if (statusCode == 0) {
							// Attempt to display member name and their points
							try {
								membershipNumber.setFont(new Font("Arial", Font.PLAIN, 20));
								membershipNumber.setText("Welcome back " + scsLogic.getFName(Integer.parseInt(membershipStr))
										+ " " + scsLogic.getLName(Integer.parseInt(membershipStr)) + "!             Points total: " + customerIO.getPoints());
							} 
							// Note this should be unreachable, as any corruption in the database should return status code -4
							// instead of status code 0, and -4 displays the appropriate message, see further down for details
							catch (Exception conversionErr) {
								membershipNumber.setFont(new Font("Arial", Font.PLAIN, 20));
								membershipNumber.setText("Error: membership database corrupted");
							}

						} // Status code -2 if inputted member number is not the correct length
						else if (statusCode == -2) {
							membershipNumber.setFont(new Font("Arial", Font.PLAIN, 20));
							membershipNumber.setText("Error: membership number is invalid. A valid membership number is " + scsLogic.getMembershipLen() + " digits long");

						} // Status code -3 is member number is valid length, but does not exist in database
						else if (statusCode == -3) {
							membershipNumber.setFont(new Font("Arial", Font.PLAIN, 20));
							membershipNumber.setText("Error: membership number is not in the database");

						} // There are no other status codes after -4, so this will cover everything
						// Status code -4 is a corruption in the data was found when retrieving the member information
						else if (statusCode == -4) {
							membershipNumber.setFont(new Font("Arial", Font.PLAIN, 20));
							membershipNumber.setText("Error: membership database corrupted");
						}
					}
					}
				}
			}
		});
	}
}