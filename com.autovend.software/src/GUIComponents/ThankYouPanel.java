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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.autovend.software.CustomerIO;



public class ThankYouPanel {
	
    private JPanel thankYouPanel;
    private JLabel thankYouLabel;
	private JDialog popup;
    private JLabel changeLabel;
    private BigDecimal changeDue;
	private JPanel printPopup;
    private CustomerIO customerIO;
    private JButton welcomeButton;
    
	public ThankYouPanel(JPanel GUIContainer, CustomerIO CIO) {
		customerIO = CIO;
		makePopupFrame();
		makePrintPopup();
		thankYouPanel = new JPanel(new GridBagLayout());
		changeDue = new BigDecimal(0.00);
		GridBagConstraints constraints = new GridBagConstraints();
		thankYouLabel = new JLabel("Thank you for shopping with us!");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.CENTER;
        constraints.anchor = GridBagConstraints.CENTER;
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(10, 10, 10, 10);
        Border compound = new CompoundBorder(line, margin);
        thankYouLabel.setBorder(compound);
        thankYouPanel.add(thankYouLabel, constraints);
        
        changeLabel = new JLabel("Please collect your $" + changeDue);
        constraints.gridx = 0;
        constraints.gridy = 1;
        changeLabel.setBorder(compound);
        thankYouPanel.add(changeLabel, constraints);
        
        
        
        welcomeButton = new JButton("Go to Welcome Screen");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        thankYouPanel.add(welcomeButton, constraints);
        
        welcomeButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Welcome");  
    		}
    	});
	}
	/**
	 * Create the base for the popups
	 */
	private void makePopupFrame() {
		popup = new JDialog(customerIO.getFrame());
		// removes the close window button and makes it impossible for customer to move
		// or resize the popup
		popup.setUndecorated(true);
		popup.setAlwaysOnTop(false);
	}

	
	public JPanel getPanel() {
    	return thankYouPanel;
    }
	
	/**
	 * General purpose popup display method to reduce repetition.
	 * @param contentPanel the panel to be displayed in the popup
	 */
	private void showPopup(JPanel contentPanel) {
		JFrame parentFrame = customerIO.getFrame();
		popup.setPreferredSize(new Dimension(parentFrame.getWidth() / 2, parentFrame.getHeight() / 2));
		popup.setLocation(parentFrame.getWidth() / 4, parentFrame.getHeight() / 4);

		popup.setContentPane(contentPanel);

		popup.pack();
		popup.setVisible(true);
		//popup.

		setEnableAllButtons(false);
	}
	/**
	 * Shows the "Bag your item" popup that tells the user to place their items in
	 * the bagging area. Switches to a "please wait" screen until attendant IO
	 * verifies/rejects the bags.
	 */
	public void showprintPopup() {
		showPopup(printPopup);
	}

	/**
	 * Closes whichever popup is currently displayed.
	 * To be called by attendant IO or in response to an event (e.g., scales have detected necessary weight added)
	 */
	public void closePopup() {
		popup.setVisible(false);
		setEnableAllButtons(true);
	}

	/**
	 * Sets the enabled field of all buttons on the thank-you screen
	 * @param enabled true to enable, false to disable
	 */
	private void setEnableAllButtons(boolean enabled) {
		welcomeButton.setEnabled(enabled);
	}

	/**
	 * Put together the "please wait for attendant" popup. It is impossible for the
	 * customer to close this popup themselves--attendant must do it.
	 */
	private void makePrintPopup() {
		printPopup = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(10, 10, 10, 10);

		JLabel popupTitle = new JLabel("Please Wait, printing error");
		JLabel popupBody = new JLabel("An attendant will help shortly.");

		popupTitle.setFont(GUIConstants.LARGE_FONT);
		popupBody.setFont(GUIConstants.SMALL_FONT);

		printPopup.add(popupTitle, constraints);
		printPopup.add(popupBody, constraints);
	}
	
	public void setChangeDue(BigDecimal changeDue) {
		this.changeDue = changeDue;
		this.changeLabel = new JLabel("Please collect your $" + changeDue);
	}
	
    // Getters for tests:
    public JButton getWelcomeButton(){return welcomeButton;}
    public JLabel getThankYouLabel(){return thankYouLabel;}
    public JLabel getChangeLabel(){return changeLabel;}
}
