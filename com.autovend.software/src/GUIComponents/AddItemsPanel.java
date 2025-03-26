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

import com.autovend.software.CustomerIO;
import com.autovend.software.SelfCheckoutStationLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddItemsPanel extends JPanel {
	private CustomerIO customerIO;
	private SelfCheckoutStationLogic scsLogic;

	private JDialog popup;
	private JPanel addOwnBagsPopup;
	private JPanel waitBagsPopup;
	private JPanel bagItemPopup;
	private JPanel verifyNoBagPopup;

	private JButton addBrowseButton;
	private JButton addPLUButton;
	private JButton ownBagsButton;
	private JButton buyBagsButton;
	private JButton payButton;
	private JButton doneButton;
	private JButton noBaggingButton;

	public AddItemsPanel(CustomerIO customerIO, SelfCheckoutStationLogic logic) {
		this.customerIO = customerIO;
		scsLogic = logic;

		makePopupFrame();
		makeAddOwnBagsPopup();
		makeWaitBagsPopup();
		makeBagItemsPopup();
		makeWaitNoBagPopup();

		setLayout(new GridLayout(1, 2));

		add(customerIO.getItemsListPanel()); // add left half of the screen

		// make components of right half of the screen
		JPanel right = new JPanel();
		/*
		 * components added from right to left. needed for Add Own Bags and Purchase
		 * Bags buttons to be laid out together vertically beside Finish and Pay
		 */
		right.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		right.setLayout(new GridBagLayout());
		// default constraints used to specify characteristics like fill-to-width,
		// relative size for GridBagLayout
		GridBagConstraints constraints = new GridBagConstraints();

		addBrowseButton = new JButton("Add Item by Browsing");
		addPLUButton = new JButton("Add Item by PLU");
		ownBagsButton = new JButton("Add Own Bags");
		buyBagsButton = new JButton("Purchase Bags");
		payButton = new JButton("Finish and Pay");


		addBrowseButton.setFont(GUIConstants.LARGE_FONT);
		addPLUButton.setFont(GUIConstants.LARGE_FONT);
		ownBagsButton.setFont(GUIConstants.FONT);
		buyBagsButton.setFont(GUIConstants.FONT);
		payButton.setFont(GUIConstants.LARGE_FONT);
		noBaggingButton.setFont(GUIConstants.FONT);

		Insets insets = new Insets(10, 10, 10, 10);
		constraints.insets = insets; // set spacing of elements

		constraints.gridwidth = GridBagConstraints.REMAINDER; // mark as final item in the row
		// set to fill to width and height. fills each button relative to the others
		// based on
		// the weighty and weightx of each one
		// (note: prioritizes making the button text fit before relative sizes set this
		// way)
		constraints.fill = GridBagConstraints.BOTH;
		// sets the relative proportions for x- and y-axis
		constraints.weightx = 1;
		constraints.weighty = 2;

		// add buttons with above constraints
		right.add(addBrowseButton, constraints);
		right.add(addPLUButton, constraints);

		// mark as second-last item in row (will be placed on right bc of specified
		// component direction above)
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		constraints.gridheight = 2; // make it take up 2 grid rows in height
		constraints.weighty = 1; // since height is 2, to match above buttons proportionally, weight must be 1
		constraints.weightx = 2; // make it wider than the buttons that will follow
		right.add(payButton, constraints);

		constraints.gridwidth = GridBagConstraints.REMAINDER; // mark as final item in the row
		constraints.gridheight = 1; // return number of grid rows it takes up to default
		constraints.weightx = 1; // make it less wide than payment button
		right.add(ownBagsButton, constraints);
		right.add(buyBagsButton, constraints);

		add(right);
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

	/**
	 * Puts together the "add own bag" popup. The customer is unable to close this
	 * popup (we do not have support for cancellation in Use Own Bags), pressing the
	 * button simple transitions the popup to the "please wait" screen.
	 */
	private void makeAddOwnBagsPopup() {
		addOwnBagsPopup = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(10, 10, 10, 10);
		
		JLabel popupTitle = new JLabel("Please place your bags");
		JLabel popupTitleSecondHalf = new JLabel("in the bagging area.");
		JLabel popupBody = new JLabel("Touch 'Done' when finished.");
		doneButton = new JButton("Done");

		popupTitle.setFont(GUIConstants.LARGE_FONT);
		popupTitleSecondHalf.setFont(GUIConstants.LARGE_FONT);
		popupBody.setFont(GUIConstants.SMALL_FONT);
		doneButton.setFont(GUIConstants.FONT);

		// cancel out the top padding; we don't want the second part of the statement
		// to be spaced from the first. It should look like one sentence.
		constraints.insets = new Insets(10, 10, -10, 10);
		addOwnBagsPopup.add(popupTitle, constraints);
		constraints.insets = new Insets(10, 10, 10, 10);
		addOwnBagsPopup.add(popupTitleSecondHalf, constraints);
		addOwnBagsPopup.add(popupBody, constraints);
		addOwnBagsPopup.add(doneButton, constraints);

		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerIO.notifyOwnBagsAdded();
				popup.setContentPane(waitBagsPopup);
				popup.pack();
			}
		});

	}

	/**
	 * Put together the "please wait for attendant" popup. It is impossible for the
	 * customer to close this popup themselves--attendant must do it.
	 */
	private void makeWaitBagsPopup() {
		waitBagsPopup = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(10, 10, 10, 10);

		JLabel popupTitle = new JLabel("Please Wait");
		JLabel popupBody = new JLabel("An attendant will verify your bags shortly.");

		popupTitle.setFont(GUIConstants.LARGE_FONT);
		popupBody.setFont(GUIConstants.SMALL_FONT);

		waitBagsPopup.add(popupTitle, constraints);
		waitBagsPopup.add(popupBody, constraints);
	}

	/**
	 * Put together the "Please bag item" popup. It is impossible for the customer
	 * to close this popup themselves--attendant must do it.
	 */
	private void makeBagItemsPopup() {
		bagItemPopup = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(10, 10, 10, 10);

		JLabel popupTitle = new JLabel("Waiting");
		JLabel popupBody = new JLabel("Please place the item in the bagging area.");
		noBaggingButton = new JButton("I don't want to bag this item");

		popupTitle.setFont(GUIConstants.LARGE_FONT);
		popupBody.setFont(GUIConstants.SMALL_FONT);
		noBaggingButton.setFont(GUIConstants.FONT);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		bagItemPopup.add(popupTitle, constraints);
		constraints.fill = GridBagConstraints.NONE;
		bagItemPopup.add(popupBody, constraints);
		bagItemPopup.add(this.noBaggingButton, constraints);

		noBaggingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerIO.notifyNoBaggingPressed();
				popup.setContentPane(verifyNoBagPopup);
				popup.pack();
			}
		});

	}

	/**
	 * Maker for the "please wait for attendant" popup. It is impossible for the
	 * customer to close this popup themselves-- either they must add weight to the
	 * bagging area or request "Do not bag item".
	 */
	private void makeWaitNoBagPopup() {
		verifyNoBagPopup = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(10, 10, 10, 10);

		JLabel popupTitle = new JLabel("Please Wait");
		JLabel popupBody = new JLabel("An attendant will verify your items shortly.");

		popupTitle.setFont(GUIConstants.LARGE_FONT);
		popupBody.setFont(GUIConstants.SMALL_FONT);

		verifyNoBagPopup.add(popupTitle, constraints);
		verifyNoBagPopup.add(popupBody, constraints);
	}

	/**
	 * Put together and display an error message popup (e.g., in case of scale overload, etc.)
	 */
	public void showExceptionPopup(String exceptionDescription) {
		JPanel exceptionPopup = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(10,10,10,10);

		JLabel popupTitle = new JLabel("<html><body style='text-align:center'>" + exceptionDescription + "</body></html>");
		JLabel popupBody = new JLabel("Please wait for an attendant.");

		popupTitle.setFont(GUIConstants.LARGE_FONT);
		popupBody.setFont(GUIConstants.SMALL_FONT);

		exceptionPopup.add(popupTitle, constraints);
		exceptionPopup.add(popupBody, constraints);

		showPopup(exceptionPopup);
	}

	/**
	 * Shows the "Bag your item" popup that tells the user to place their items in
	 * the bagging area. Switches to a "please wait" screen until attendant IO
	 * verifies/rejects the bags.
	 */
	public void showBagItemPopup() {
		showPopup(bagItemPopup);
	}

	/**
	 * Shows the add-own-bags popup that tells the user to place their bags in the
	 * bagging area. Switches to a "please wait" screen until attendant IO
	 * verifies/rejects the bags.
	 */
	public void showAddOwnBagsPopup() {
		showPopup(addOwnBagsPopup);
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

		setEnableAllButtons(false);
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
	 * Sets the enabled field of all buttons on the add items screen
	 * @param enabled true to enable, false to disable
	 */
	private void setEnableAllButtons(boolean enabled) {
		ItemsListPanel leftScreen = customerIO.getItemsListPanel();
		JButton[] buttons = {leftScreen.getHelpButton(), leftScreen.getMembershipButton(),
				addBrowseButton, addPLUButton, ownBagsButton, buyBagsButton, payButton};
		for (JButton button : buttons) {
			button.setEnabled(enabled);
		}
	}

	// getters for setting button behaviour and testing

	public JButton getBrowseButton() {
		return addBrowseButton;
	}

	public JButton getPLUButton() {
		return addPLUButton;
	}

	public JButton getOwnBagsButton() {
		return ownBagsButton;
	}

	public JButton getBuyBagsButton() {
		return buyBagsButton;
	}

	public JButton getPayButton() {
		return payButton;
	}

	public JButton getDoneButton() {
		return doneButton;
	}

	public JButton getNoBaggingButton() {
		return noBaggingButton;
	}

	public JDialog getPopup() {
		return popup;
	}

	public JPanel getAddOwnBagsPopup() {
		return addOwnBagsPopup;
	}

	public JPanel getWaitBagsPopup() {
		return waitBagsPopup;
	}

	public JPanel getBagItemPopup() {
		return bagItemPopup;
	}

	public JPanel getVerifyNoBagPopup() {
		return verifyNoBagPopup;
	}

	public JButton getHelpButton(){return getHelpButton();}
}
