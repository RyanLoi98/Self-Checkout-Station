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

package com.autovend.software;

import GUIComponents.*;

import com.autovend.devices.*;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.software.SelfCheckoutStationLogic.MethodOfPayment;

import GUIComponents.WelcomePanel;
import GUIComponents.AddByBrowsingPanel;
import GUIComponents.AddItemByPLUPanel;
import GUIComponents.CreditDebitPanel;
import GUIComponents.PaymentPanel;
import GUIComponents.ProceedPaymentPanel;
import GUIComponents.PurchaseBagsPanel;
import GUIComponents.ThankYouPanel;
import GUIComponents.EnterMembershipPanel;
import GUIComponents.InsufficientChangePanel;

import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;

public class CustomerIO implements EnterMembershipObserver, PayCardObserver, PayCashObserver, AddItemObserver, PrintReceiptObserver,TouchScreenObserver {
	public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 20);
	public static final Font FONT = new Font("Arial", Font.PLAIN, 40);
	public static Font LARGE_FONT = new Font("Arial", Font.PLAIN, 60);

	private ArrayList<CustomerIOListener> listeners;
	private JFrame frame;
	private JPanel panelManager;
	private WelcomePanel welcomePanel;
	private ItemsListPanel itemsListPanel;
	private AddItemsPanel addItemsPanel;
	private PaymentPanel PaymentPanel;
	private CreditDebitPanel creditDebitPanel;
	private ProceedPaymentPanel ProceedPaymentPanel;
	private PurchaseBagsPanel purchaseBagsPanel;
	private EnterMembershipPanel enterMembershipPanel;
	private ThankYouPanel thankYou;
	private InsufficientChangePanel insufficientChangePanel;
	private JPanel suspendedPanel;
	private JPanel disabledPanel;
	private CustomerIO cIo;

	private JPanel GUIContainer;
	private int currentPanel;
	private String[] panels;

	private JLabel welcomeLabel;
	private JLabel itemLabel;

	private JTextField quantityField;
	private JTextField priceField;
	private JButton scanButton;
	private JButton startButton;
	private JButton payButton;
	private JButton exitButton;

	private int frameWidth = 800;
	private int frameHeight = 600;

	private JMenu ChooseLanguage;

	private TouchScreen touchScreen;
	protected int paymentPanelStage;
	private SelfCheckoutStationLogic scsLogic;

	public SelfCheckoutStationLogic getScsLogic() {
		return scsLogic;
	}


	private AddByBrowsingPanel addByBrowsingPanel;
	private AddItemByPLUPanel addItemByPLUPanel;

	// entermembership observer fields
	private boolean databaseCorrupt;
	private int Cpoints;

	private boolean memberCardUsed = false;
	private String memberCardNum = "";
	private boolean swipeAttemptFlag = false;

	public BigDecimal amountDue = new BigDecimal(0.00);

	public CustomerIO(SelfCheckoutStationLogic scsLogic) {
		cIo = this;
		this.databaseCorrupt = false;
		paymentPanelStage = 0;
		this.scsLogic = scsLogic;
		
		GUIContainer = new JPanel(new CardLayout());

		makeWelcomePanel();
		makeItemListPanel();
		makeAddPanel();
		makeAddByBrowsingPanel();
		makeAddItemByPLUPanel();
		makePurchaseBagsPanel();
		makeEnterMembershipPanel();
		makePaymentPanel();
		makeCreditDebitPanel();
		makeProceedPaymentPanel(MethodOfPayment.CASH);
		makeThankYouPanel();
		makeInsufficientChangePanel();
		makeSuspendedPanel();
		makeDisabledPanel();

		panels = new String[] { "Welcome", "Adding", "AddByBrowsing", "AddItemByPLU", "PurchaseBags", "EnterMembership",
				"Payment", "CreditDebit", "ProceedPayment", "InsufficientChange", "Suspended", "Disabled" };

		touchScreen = new TouchScreen();
//    	JMenuBar mb = new JMenuBar();
//    	mb.add(ChooseLanguage);
		frame = touchScreen.getFrame();
		frame.setSize(frameWidth, frameHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      centers the frame on the screen
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(GUIContainer);
		// frame.setJMenuBar(mb);

		frame.setVisible(true);
		listeners = new ArrayList<CustomerIOListener>();

		currentPanel = getVisiblePanel();
	}

	public void registerSelf() {
		scsLogic.registerAddItemObserver(this);
		scsLogic.registerEnterMemberObserver(this);
		scsLogic.registerPayCardObserver(this);
		scsLogic.registerPayCashObserver(this);
		scsLogic.registerPrintObserver(this);
		scsLogic.registerScreenObserver(this);
		scsLogic.loadMemberships();
		CommunicationsController.getCommunicator().registerOntoCustomers(this);
	}
	
	public void makeWelcomePanel() {
		welcomePanel = new WelcomePanel(this, frameWidth, frameHeight);
		GUIContainer.add(welcomePanel, "Welcome");
	}

	public void makeItemListPanel() {
		itemsListPanel = new ItemsListPanel(this);

		itemsListPanel.getHelpButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (CustomerIOListener communicator : listeners) {
					communicator.stationHelp(cIo , scsLogic);
				}
		     JOptionPane.showMessageDialog(null, "Attendant is on the way", "Help Needed", JOptionPane.INFORMATION_MESSAGE);

				// TODO: CONNECT ATTENDANT HELP USE CASE
			}
		});
		itemsListPanel.getMembershipButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "EnterMembership");
				currentPanel = getVisiblePanel();
			}
		});
	}

	public void makeAddPanel() {
		addItemsPanel = new AddItemsPanel(this, scsLogic);
		GUIContainer.add(addItemsPanel, "Adding");
		// since this screen very interconnected with other use cases, made button
		// action setting in the common class
		addItemsPanel.getPLUButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "AddItemByPLU");
				currentPanel = getVisiblePanel();
			}
		});
		addItemsPanel.getBrowseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "AddByBrowsing");
				currentPanel = getVisiblePanel();
			}
		});
		addItemsPanel.getOwnBagsButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (CustomerIOListener listener : listeners) {
					listener.pressedUseOwnBags(CustomerIO.this, scsLogic);
				}
				addItemsPanel.showAddOwnBagsPopup();
			}
		});
		addItemsPanel.getBuyBagsButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "PurchaseBags");
				currentPanel = getVisiblePanel();
			}
		});
		addItemsPanel.getPayButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Payment");
				currentPanel = getVisiblePanel();
				updatePaymentPanelBill(); // Update the payment panel when we enter the screen
			}
		});
		addItemsPanel.getNoBaggingButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (CustomerIOListener listener : listeners) {
					listener.pressedNoBaggingRequest(CustomerIO.this, scsLogic);
				}
				addItemsPanel.showBagItemPopup();
			}
		});
	}

	public void makePurchaseBagsPanel() {
		purchaseBagsPanel = new PurchaseBagsPanel(this, frameWidth, frameHeight);
		GUIContainer.add(purchaseBagsPanel, "PurchaseBags");
	}

	/*
	 * To hide dialogs during unit tests.
	 */
	public void makeCustomPurchaseBagsPanel(PurchaseBagsPanel pbp) {
		purchaseBagsPanel = pbp;
		GUIContainer.add(pbp, "PurchaseBags");
	}

	public void makeAddByBrowsingPanel() {
		addByBrowsingPanel = new AddByBrowsingPanel(this, GUIContainer, scsLogic);
		GUIContainer.add(addByBrowsingPanel.getAddByBrowsingPanel(), "AddByBrowsing");
	}

	public void makeAddItemByPLUPanel() {
		addItemByPLUPanel = new AddItemByPLUPanel(GUIContainer, this);
		GUIContainer.add(addItemByPLUPanel.getAddingByPLUPanel(), "AddItemByPLU");
	}

	public void makeEnterMembershipPanel() {
		enterMembershipPanel = new EnterMembershipPanel(this, scsLogic, frameWidth, frameHeight);
		GUIContainer.add(enterMembershipPanel, "EnterMembership");
	}

	public void makePaymentPanel() {
		PaymentPanel = new PaymentPanel(this, scsLogic, GUIContainer);
		GUIContainer.add(PaymentPanel.getPanel(), "Payment");
	}

	public void makeCreditDebitPanel() {
		creditDebitPanel = new CreditDebitPanel(this, scsLogic, GUIContainer);
		GUIContainer.add(creditDebitPanel.getPanel(), "CreditDebit");
	}

	public void makeProceedPaymentPanel(MethodOfPayment paymentType) {
		ProceedPaymentPanel = new ProceedPaymentPanel(GUIContainer, (MethodOfPayment) paymentType);
		GUIContainer.add(ProceedPaymentPanel.getPanel(), "ProceedPayment");
	}

	public void makeThankYouPanel() {
		thankYou = new ThankYouPanel(GUIContainer, this);
		GUIContainer.add(thankYou.getPanel(), "ThankYou");
	}

	public void makeInsufficientChangePanel() {
		insufficientChangePanel = new InsufficientChangePanel(GUIContainer, cIo);
		GUIContainer.add(insufficientChangePanel.getPanel(), "InsufficientChange");
	}

	public void makeSuspendedPanel() {
		suspendedPanel = new JPanel(new GridLayout(2, 1));
		JLabel suspendedLabel = new JLabel("Station Suspended");
		JLabel detailLabel = new JLabel("Please use next available station.");
		suspendedLabel.setFont(LARGE_FONT);
		detailLabel.setFont(FONT);
		suspendedLabel.setHorizontalAlignment(JLabel.CENTER);
		detailLabel.setHorizontalAlignment(JLabel.CENTER);
		suspendedPanel.add(suspendedLabel);
		suspendedPanel.add(detailLabel);
		GUIContainer.add(suspendedPanel, "Suspended");
	}

	public void makeDisabledPanel() {
		disabledPanel = new JPanel();
		disabledPanel.setBackground(Color.BLACK);
		GUIContainer.add(disabledPanel, "Disabled");
	}

	public void bagsPurchased(int quantity) {
		for (CustomerIOListener communicator : listeners) {
			communicator.pressedPurchaseBags(this, scsLogic, quantity);
		}
	}

	public void register(CustomerIOListener communicator) {
		listeners.add(communicator);
	}

	/**
	 * Closes any popups and resets lingering customer selections/entered numbers.
	 * Does not reset any of the logic or session information.
	 */
	private void resetGUI() {
		// close any popups if one happens to be visible
		addItemsPanel.closePopup();
		thankYou.closePopup();
		// reset selections and entered numbers for panels that don't zero on entry
		addByBrowsingPanel.clearSelection();
		addItemByPLUPanel.resetEncounter();
		creditDebitPanel.resetAmountToPayField();
		enterMembershipPanel.getMembershipNumber().setText("");

		for (CustomerIOListener communicator : listeners) {
			communicator.resetGUI(this, scsLogic);
		}
	}

	/**
	 * Resets GUI and displays the welcome screen.
	 */
	public void revertToWelcome() {
		resetGUI();
		setShownPanel("Welcome");
	}

	/**
	 * Resets GUI and displays a "station suspended" screen
	 */
	public void suspendIO() {
		//resetGUI();
		setShownPanel("Suspended");
	}

	/**
	 * Resets GUI and displays a black screen that cannot be interacted with.
	 */
	public void disableIO() {
		resetGUI();
		setShownPanel("Disabled");
	}

	public static void main(String[] args) {
	}

	public void setShownPanel(String panelName) {
		((CardLayout) GUIContainer.getLayout()).show(GUIContainer, panelName);
		currentPanel = getVisiblePanel();
	}

	private int getVisiblePanel() {
		int i = 0;
		for (Component card : GUIContainer.getComponents()) {
			if (card.isVisible()) {
				return i;
			}
			i += 1;

		}
		return -1;
	}

	public int getNumBagsInDispenser() {
		return scsLogic.getPurchaseBagsController().getNumBagsInDispenser();
	}

	public void bagsPurchasedComplete(int quantity) {
		JOptionPane.showMessageDialog(null, "Your purchase of " + quantity + " bag(s) was a success!", "Success!",
				JOptionPane.PLAIN_MESSAGE);
	}

	public void showAddPanel() {
		((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Adding");
	}

	public void notifyAttendantNeedBags() {
		for (CustomerIOListener communicator : listeners) {
			communicator.pressedNeedBags(this, scsLogic);
		}
	}
	public BillRecord getRecord() {
		return scsLogic.getBillRecord();
	}

	public JFrame getFrame() {
		return frame;
	}

	public WelcomePanel getWelcomePanel() {
		return welcomePanel;
	}

	public AddItemsPanel getAddItemsPanel() {
		return addItemsPanel;
	}

	public AddByBrowsingPanel getAddByBrowsingPanel() {
		return addByBrowsingPanel;
	}

	public PurchaseBagsPanel getPurchaseBagsPanel() {
		return purchaseBagsPanel;
	}

	public ItemsListPanel getItemsListPanel() {
		return itemsListPanel;
	}

	public ProceedPaymentPanel getProceedPaymentPanel() {
		return ProceedPaymentPanel;
	}

	public PaymentPanel getPaymentPanel() {
		return PaymentPanel;
	}

	public EnterMembershipPanel getEnterMembershipPanel() {
		return enterMembershipPanel;
	}

	public ThankYouPanel getThankYouPanel() {
		return thankYou;
	}

	public InsufficientChangePanel getInsufficientChangePanel() {
		return insufficientChangePanel;

	}

	public JPanel getDisabledPanel() {
		return disabledPanel;
	}
	
	public AddItemByPLUPanel getAddItemByPLUPanel() {
		return addItemByPLUPanel;
	}

	// observer methods for enter membership
	@Override
	public void updatePoints(int points) {
		Cpoints = points;
	}

	public int getPoints() {
		return Cpoints;
	}

	@Override
	public void isDataBaseCorrupt(boolean status) {
		this.databaseCorrupt = status;
	}

	public boolean getDataBaseStatus() {
		return this.databaseCorrupt;
	}

	public ArrayList<CustomerIOListener> getListeners() {
		return listeners;
	}

	// Update card number and set flag to confirm member card has been scanned/swipe without a hardware error
	@Override
	public void memberCardEvent(String cardNumber) {
		memberCardUsed = true;
		memberCardNum = cardNumber;

	}

	// Check that swipe card has started, to be used to check if a magnetic stripe faliure occurs
	@Override
	public void swipeAttemptEvent() {
		swipeAttemptFlag = true;
	}

	// Getters and setters for membership card interactions
	public boolean getMemberCardUsed() {
		return memberCardUsed;
	}

	public String getMemberCardNum() {
		return memberCardNum;
	}

	public void setMemberCardNum(String s) {
		memberCardNum = s;
	}

	public void setMemberCardUsed(boolean flag) {
		memberCardUsed = flag;
	}

	public boolean getSwipeAttemptFlag() {
		return swipeAttemptFlag;
	}

	public void setSwipeAttemptFlag(boolean flag) {
		swipeAttemptFlag = flag;
	}

	@Override
	public void updatedAmount(SelfCheckoutStation station, BigDecimal amount) {
		System.out.println("Payment Receieved. Amount remaining = " + amount);
		itemsListPanel.refreshCart(scsLogic.getBillRecord());
		amountDue = amount;
		updatePaymentPanelBill(); // Update the payment screen text
		// assumes station can dispense the change.
		if (BigDecimal.valueOf(0.00).compareTo(amountDue) >= 0) {
			thankYou.setChangeDue(amountDue.abs());
			((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "ThankYou");
			currentPanel = getVisiblePanel();
			/**
			ActionListener delayListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (getVisiblePanel() == currentPanel) {
						((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Welcome");
						currentPanel = getVisiblePanel();
					}
				}
			};
			Timer delayTimer = new Timer(7000, delayListener);
			delayTimer.setRepeats(false);
			delayTimer.start();*/
		} else { // amountDue remaining so return to payment screen
			((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Payment");
		}

	}

	@Override
	public void insufficientChange(SelfCheckoutStation station, BigDecimal change) {
		this.amountDue = change;
		makeInsufficientChangePanel();
		((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "InsufficientChange");

	}

	@Override
	public void notWaitingForItem(SelfCheckoutStation station) {
		notWaitingToBagItem();

	}

	@Override
	public void waitingForItem(SelfCheckoutStation station) {
		itemsListPanel.refreshCart(scsLogic.getBillRecord());
		addItemsPanel.showBagItemPopup();
	}

	@Override
	public void weightDiscrepancyOccured(SelfCheckoutStation station) {
		addItemsPanel.showExceptionPopup("Weight Discrepancy Detected");
	}

	@Override
	public void weightDiscrepancyResolved(SelfCheckoutStation station) {
		addItemsPanel.closePopup();
}

	@Override
	public void scaleOverloaded(ElectronicScale scale) {
		addItemsPanel.showExceptionPopup("Scale Overloaded");
	}

	@Override
	public void scaleOverloadedResolved(SelfCheckoutStation station) {
		addItemsPanel.closePopup();
	}

	@Override
	public void noBagsApproved(SelfCheckoutStation station) {
		addItemsPanel.closePopup();		
	}

	@Override
	public void addOwnBagsStart(SelfCheckoutStation station) {
		addItemsPanel.showAddOwnBagsPopup();
	}

	@Override
	public void addOwnBagsComplete(SelfCheckoutStation station) {
		addItemsPanel.closePopup();
	}

	@Override
	public void OwnBagsCancelled(SelfCheckoutStation station) {
		addItemsPanel.closePopup();
	}

	/**
	 * Notifies all listeners that the customer has finished adding their own bags
	 * to the bagging area, and is now waiting for the attendant response.
	 */
	public void notifyOwnBagsAdded() {
		for (CustomerIOListener listener : listeners) {
			listener.pressedDoneAddingOwnBags(this, scsLogic);
		}

	}

	/**
	 * Notifies all listeners that the customer has selected the option to not bag
	 * this item and is now waiting for the attendant response.
	 */
	public void notifyNoBaggingPressed() {
		for (CustomerIOListener listener : listeners) {
			//listener.pressedNoBaggingRequest(this, scsLogic);
		}
	}

	
	/**
	 * Protected enough? TODO: determine best way to close error screens
	 * Closes the "Bag item" popup.
	 */
	public void notWaitingToBagItem() {
		this.addItemsPanel.closePopup();
		
	}

	public void updatePaymentPanelBill(){
		PaymentPanel.updateBill();
	}
	@Override
	public void sessionComplete(SelfCheckoutStation station) {	
		itemsListPanel.refreshCart(scsLogic.getBillRecord());
	}

	@Override
	public void requiresMaintenance(SelfCheckoutStation station, String message) {
		thankYou.showprintPopup();
	}

	@Override
	public void lowPaper(SelfCheckoutStation station, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowInk(SelfCheckoutStation station, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStation(SelfCheckoutStation station) {
		itemsListPanel.refreshCart(scsLogic.getBillRecord());		
		updatePaymentPanelBill(); // Update the payment screen text
	}

	
	public BigDecimal getAmountDue() {
		return this.amountDue;
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		if (device instanceof TouchScreen) {
			PaymentPanel.updateBill();
			itemsListPanel.refreshCart(scsLogic.getBillRecord());
			revertToWelcome();
		}
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		if (device instanceof TouchScreen) {
			disableIO();
		}		
	}

	@Override
	public void preventStation(SelfCheckoutStation station) {
		suspendIO();
		
	}

	@Override
	public void permitStation(SelfCheckoutStation station) {
		PaymentPanel.updateBill();
		itemsListPanel.refreshCart(scsLogic.getBillRecord());	
		setShownPanel("Adding");

	}
}