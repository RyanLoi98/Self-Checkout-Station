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

package com.autovend.software.test;
import com.autovend.PriceLookUpCode;
import com.autovend.ReusableBag;
import com.autovend.SellableUnit;
import com.autovend.devices.*;
import com.autovend.external.CardIssuer;
import com.autovend.products.Product;

import GUIComponents.AttendantIO;
import com.autovend.software.BillRecord;
import com.autovend.software.CommunicationsController;
import com.autovend.software.SelfCheckoutStationLogic;
import com.autovend.software.SelfCheckoutStationLogic.MethodOfPayment;

import GUIComponents.PurchaseBagsPanel;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;
import com.autovend.software.PurchaseBagsController;


/**
* Test Suite to test Purchase bags
*/
public class PurchaseBagsTest {
	 // Variables for SelfCheckoutStation
	    private Currency currency;
	    private int[] denominations;
	    private BigDecimal[] coinDenominations;
	    private int scaleMaxWeight;
	    private int scaleSensitivity;
	    private ReusableBagDispenser bagDispenser;
		private CardIssuer CreditBank;
		private CardIssuer DebitBank;
	    
	    private SelfCheckoutStation scs;
	    private SelfCheckoutStationLogic scsLog;
	    private CustomerIO cio;
	    
	    private int numPurchasedBagNotifications;
		private int numPurchasedBagCompleteNotifications;
		private int numNeedBagsNotifications;
		private boolean needsBagMessageSentToAttendant;
		private boolean wentBackToAddScreen;
		private boolean customerNotifiedCompletion;

	    @Before
	    public void setup() throws OverloadException {
	        // Initialize the SelfCheckoutStation
	        currency = Currency.getInstance(Locale.CANADA);
	        denominations = new int[]{5, 10, 20};
	        coinDenominations = new BigDecimal[]{BigDecimal.ONE};
	        scaleMaxWeight = 50;
	        scaleSensitivity = 1;
			CreditBank = new CardIssuer("TD");
			DebitBank = new CardIssuer("RBC");
	        scs = new SelfCheckoutStation(currency, denominations, coinDenominations, scaleMaxWeight, scaleSensitivity);

	        bagDispenser = new ReusableBagDispenser(10);
	        // Install the SelfCheckoutStation logic
	        bagDispenser = new ReusableBagDispenser(10);
	        scsLog = new SelfCheckoutStationLogic(scs, CreditBank, DebitBank, 10, "membershipdatabase.txt", 10, 10);
	    	cio = new CustomerIODialogStub(scsLog);
	    	cio.getFrame().setVisible(false);
	        
	    	ReusableBag bag1 = new ReusableBag();
	    	ReusableBag bag2 = new ReusableBag();
	    	bagDispenser.load(bag1, bag2);
	    	numPurchasedBagNotifications = 0;
	    	numPurchasedBagCompleteNotifications = 0;
	    	numNeedBagsNotifications = 0;
	    	needsBagMessageSentToAttendant = false;
	    }

	    @After
	    public void tearDown(){
	        currency = null;
	        denominations = null;
	        coinDenominations = null;
	        bagDispenser = null;
	        scaleMaxWeight = 0;
	        scaleSensitivity = 0;
	        CreditBank = null;
	        DebitBank = null;
	        scs = null;
	        scsLog = null;
	    }
	    
	    /*
	     * The communications controller will still register and listen to events accordingly. 
	     * However, these stubs are also here to "check" that ONLY the appropriate methods are called.
	     */
		private class CListenerNoEventsStub implements CustomerIOListener {
			@Override
			public void pressedPurchaseBags(CustomerIO customer, SelfCheckoutStationLogic logic, int numberOfBags) {
				fail();
			}

			@Override
			public void pressedNeedBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public int typedMembership(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String membershipNumber) {
				return 0;
			}

			@Override
			public void stationHelp(CustomerIO customer, SelfCheckoutStationLogic logic) {
				fail();
			}

			@Override
			public void pressedNoBaggingRequest(CustomerIO customer, SelfCheckoutStationLogic logic) {
				fail();
			}

			@Override
			public void startCashPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void setCardPaymentType(CustomerIO customer, SelfCheckoutStationLogic scsLogic,
					MethodOfPayment selectedMethod) {
				fail();
			}

			@Override
			public void setCardPaymentAmount(CustomerIO customer, SelfCheckoutStationLogic scsLogic,
					BigDecimal amount) {
				fail();
			}

			@Override
			public void returnToAddFromPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void attemptCardPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String cardType,
					BigDecimal amountCustomerWishesToPay) {
				fail();
			}

			@Override
			public void pressedDoneAddingOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void pressedUseOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void enteredPLU(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PriceLookUpCode plu) {
				fail();
			}

			@Override
			public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product) {
				fail();
			}

			@Override
			public void selectedBarcodedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product,
					int quantity) {
				fail();
			}

			@Override
			public void resetGUI(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
				fail();
			}
		}
	    
		private class CListenerPurchaseBagsStub implements CustomerIOListener {
			@Override
			public void pressedPurchaseBags(CustomerIO customer, SelfCheckoutStationLogic logic, int numberOfBags) {
				numPurchasedBagNotifications++;
			}

			@Override
			public void pressedNeedBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public int typedMembership(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String membershipNumber) {
				fail();
				return 0;
			}

			@Override
			public void stationHelp(CustomerIO customer, SelfCheckoutStationLogic logic) {
				fail();
			}

			@Override
			public void pressedNoBaggingRequest(CustomerIO customer, SelfCheckoutStationLogic logic) {
				fail();
			}

			@Override
			public void startCashPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void setCardPaymentType(CustomerIO customer, SelfCheckoutStationLogic scsLogic,
					MethodOfPayment selectedMethod) {
				fail();
			}

			@Override
			public void setCardPaymentAmount(CustomerIO customer, SelfCheckoutStationLogic scsLogic,
					BigDecimal amount) {
				fail();
			}

			@Override
			public void returnToAddFromPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void attemptCardPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String cardType,
					BigDecimal amountCustomerWishesToPay) {
				fail();
			}

			@Override
			public void pressedDoneAddingOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void pressedUseOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void enteredPLU(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PriceLookUpCode plu) {
				fail();
			}

			@Override
			public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product) {
				fail();
			}

			@Override
			public void selectedBarcodedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product,
					int quantity) {
				fail();
			}

			@Override
			public void resetGUI(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
				fail();
			}
		}
		
		private class CListenerNeedBagsStub implements CustomerIOListener {
			@Override
			public void pressedPurchaseBags(CustomerIO customer, SelfCheckoutStationLogic logic, int numberOfBags) {
				fail();
			}

			@Override
			public void pressedNeedBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
				numNeedBagsNotifications++;
			}

			@Override
			public int typedMembership(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String membershipNumber) {
				fail();
				return 0;
			}

			@Override
			public void stationHelp(CustomerIO customer, SelfCheckoutStationLogic logic) {
				fail();
			}

			@Override
			public void pressedNoBaggingRequest(CustomerIO customer, SelfCheckoutStationLogic logic) {
				fail();	
			}

			@Override
			public void startCashPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void setCardPaymentType(CustomerIO customer, SelfCheckoutStationLogic scsLogic,
					MethodOfPayment selectedMethod) {
				fail();
			}

			@Override
			public void setCardPaymentAmount(CustomerIO customer, SelfCheckoutStationLogic scsLogic,
					BigDecimal amount) {
				fail();
			}

			@Override
			public void returnToAddFromPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void attemptCardPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String cardType,
					BigDecimal amountCustomerWishesToPay) {
				fail();
			}

			@Override
			public void pressedDoneAddingOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void pressedUseOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
				fail();
			}

			@Override
			public void enteredPLU(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PriceLookUpCode plu) {
				fail();
			}

			@Override
			public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product) {
				fail();
			}

			@Override
			public void selectedBarcodedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product,
					int quantity) {
				fail();
			}

			@Override
			public void resetGUI(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
				fail();
			}
		}
		
		private class AttendantIOStub extends AttendantIO {
			public void stationNeedsBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
				needsBagMessageSentToAttendant = true;
			}
		}
		
		/*
		 * Prevents the need for the user to have to interact with the unit test by programmatically
		 * closing the dialog that opens up.
		 */
		private class PurchasePanelDialogStub extends PurchaseBagsPanel {
			public PurchasePanelDialogStub(CustomerIO customerIO, int frameWidth, int frameHeight) {
				super(cio, 500, 500);
			}
			
			protected int getUserConfirmation() {
				return JOptionPane.YES_OPTION;
			}
			
			protected void attendantNotifiedPopup() {
				return;
			}
		}
		
		private class PurchasePanelDialogConfirmNoStub extends PurchasePanelDialogStub {	
			public PurchasePanelDialogConfirmNoStub(CustomerIO customerIO, int frameWidth, int frameHeight) {
				super(customerIO, frameWidth, frameHeight);
			}

			protected int getUserConfirmation() {
				return JOptionPane.NO_OPTION;
			}
		}
		
		
		/*
		 * Prevents the need for the user to have to interact with the unit test by programmatically
		 * PREVENTING the dialog from opening up.
		 */
		private class CustomerIODialogStub extends CustomerIO {
			public CustomerIODialogStub(SelfCheckoutStationLogic scsLogic) {
				super(scsLogic);
			}
			
			public void bagsPurchasedComplete(int quantity) {
				numPurchasedBagCompleteNotifications++;
			}
			
			public void showAddPanel() {
				wentBackToAddScreen = true;
			}
		}

	    @Test
	    public void testCustomerCannotAddMoreBagsThanInDispenser() throws OverloadException{
	    	cio.register(new CListenerNoEventsStub());
	    	JButton addButton = cio.getPurchaseBagsPanel().getAddBagButton();
	    	// since only two bags are in the dispenser, even after clicking this 3 times,
	    	// the number of bags shown on screen should only be 2
	    	addButton.doClick();
	    	addButton.doClick();
	    	addButton.doClick();
	    	assertEquals(2, cio.getPurchaseBagsPanel().getNumBags());
	    }
	    
	    @Test
	    public void testCustomerCannotHaveNegativeBags() throws OverloadException{
	    	cio.register(new CListenerNoEventsStub());
	    	JButton removeButton = cio.getPurchaseBagsPanel().getRemoveBagButton();
	    	// initially the GUI starts off with numBags 0
	    	assertEquals(0, cio.getPurchaseBagsPanel().getNumBags());
	    	// even after decrementing multiple times, we should still have 0 bags on the GUI
	    	// the numBags customer is purchasing should not become a negative
	    	removeButton.doClick();
	    	removeButton.doClick();
	    	removeButton.doClick();
	    	assertEquals(0, cio.getPurchaseBagsPanel().getNumBags());
	    }
	    
	    @Test
	    public void testCustomerConfirmsPurchase_BagsAddedToOrder() throws OverloadException {
	    	double oldWeight = scsLog.getAddItemController().getExpectedMaxWeight();
	    	
	    	CommunicationsController.getCommunicator().registerOntoCustomers(cio);
	    	cio.register(new CListenerPurchaseBagsStub());
	    	cio.makeCustomPurchaseBagsPanel(new PurchasePanelDialogStub(cio, 500, 500));
	    	
	    	JButton addButton = cio.getPurchaseBagsPanel().getAddBagButton();
	    	addButton.doClick();
	    	addButton.doClick();
	    	cio.getPurchaseBagsPanel().getPurchaseButton().doClick();
	    	
	    	String bagName = scsLog.getPurchaseBagsController().BAG_NAME;
	    	BillRecord billRecord = scsLog.getAddItemController().getBillRecord();
	    	
	    	// check that two bags were added to the order
	    	assertEquals(0, scsLog.getPurchaseBagsController().getNumBagsInDispenser());
	    	assertEquals(0, cio.getPurchaseBagsPanel().getNumBags());
	    	
	    	assertTrue(billRecord.getItems().contains(bagName));
	    	assertEquals((int) billRecord.getItemQuantity(bagName), 2);
	    	assertEquals(PurchaseBagsController.BAG_COST, billRecord.getItemCost(bagName));
	    	assertEquals((oldWeight + (new ReusableBag()).getWeight()) * 2, scsLog.getAddItemController().getExpectedMaxWeight(), 0.0000001);
	    	
	    	assertEquals(1, numPurchasedBagNotifications);
	    	assertEquals(1, numPurchasedBagCompleteNotifications);
	    }
	    
	    @Test
	    public void testCustomerDoesntConfirmPurchase_BagsNOTAddedToOrder() throws OverloadException {
	    	double oldWeight = scsLog.getAddItemController().getExpectedMaxWeight();
	    	CommunicationsController.getCommunicator().registerOntoCustomers(cio);
	    	cio.register(new CListenerNoEventsStub());
	    	cio.makeCustomPurchaseBagsPanel(new PurchasePanelDialogConfirmNoStub(cio, 500, 500));
	    	
	    	JButton addButton = cio.getPurchaseBagsPanel().getAddBagButton();
	    	addButton.doClick();
	    	addButton.doClick();
	    	cio.getPurchaseBagsPanel().getPurchaseButton().doClick();
	    	
	    	String bagName = scsLog.getPurchaseBagsController().BAG_NAME;
	    	BillRecord billRecord = scsLog.getAddItemController().getBillRecord();
	    	
	    	// Since the customer clicked no when asked to confirm the purchase, the bags SHOULD NOT 
	    	// have been added to the order
	    	assertEquals(2, scsLog.getPurchaseBagsController().getNumBagsInDispenser());
	    	assertEquals(2, cio.getPurchaseBagsPanel().getNumBags());
	    	
	    	assertTrue(!billRecord.getItems().contains(bagName));
	    	assertTrue(billRecord.getItemQuantity(bagName) == null);
	    	assertTrue(billRecord.getItemCost(bagName) == null);
	    	assertEquals(oldWeight, scsLog.getAddItemController().getExpectedMaxWeight(), 0.0000001);
	    	
	    	assertEquals(0, numPurchasedBagNotifications);
	    	assertEquals(0, numPurchasedBagCompleteNotifications);
	    }
	    
	    @Test
	    public void testCustomerAsksForHelp_AttendantNotified() {
	    	CommunicationsController.getCommunicator().registerOntoCustomers(cio);
	    	CommunicationsController.getCommunicator().registerOntoAttendant(new AttendantIOStub());
	    	cio.register(new CListenerNeedBagsStub());
	    	cio.makeCustomPurchaseBagsPanel(new PurchasePanelDialogStub(cio, 500, 500));
	    	
	    	JButton needMoreBagsButton = cio.getPurchaseBagsPanel().getNeedMoreBagsButton();
	    	needMoreBagsButton.doClick();
	    	
	    	// check that CIOListener received this message
	    	assertEquals(1, numNeedBagsNotifications);
	    	// communicationsController should have sent this message to the attendant
	    	assertTrue(needsBagMessageSentToAttendant);
	    }
	    
	    /*
	     * White Box Test: Check that clicking the "back" button actually takes you to the previous
	     * page.
	     */
	    @Test
	    public void testClickingBackButtonTakesBackToAddScreen() {
	    	JButton backButton = cio.getPurchaseBagsPanel().getBackButton();
	    	backButton.doClick();
	    	assertTrue(wentBackToAddScreen);
	    }
	    
	    @Test
	    public void testControllerSetsOutOfBagsIfCustomerDispensesLastOne() {
	    	CommunicationsController.getCommunicator().registerOntoCustomers(cio);
	    	cio.makeCustomPurchaseBagsPanel(new PurchasePanelDialogStub(cio, 500, 500));
	    	
	    	JButton addButton = cio.getPurchaseBagsPanel().getAddBagButton();
	    	addButton.doClick();
	    	addButton.doClick();
	    	cio.getPurchaseBagsPanel().getPurchaseButton().doClick();
	    	
	    	assertTrue(scsLog.getPurchaseBagsController().getOutOfBagsFlag());
	    }
	    
	    @Test
	    public void testCustomerCanPurchase_AfterDispenserReloaded() throws OverloadException {
	    	CommunicationsController.getCommunicator().registerOntoCustomers(cio);
	    	cio.makeCustomPurchaseBagsPanel(new PurchasePanelDialogStub(cio, 500, 500));
	    	
	    	JButton addButton = cio.getPurchaseBagsPanel().getAddBagButton();
	    	addButton.doClick();
	    	addButton.doClick();
	    	cio.getPurchaseBagsPanel().getPurchaseButton().doClick();
	    	
	    	// at this point, the number of bags in the dispenser should be empty
	    	assertEquals(0, scsLog.getPurchaseBagsController().getNumBagsInDispenser());
	    	
	    	// see if the customer's machine updates to reflect the new bags added
	    	bagDispenser.load(new ReusableBag());
	    	addButton.doClick();
	    	assertEquals(1, cio.getPurchaseBagsPanel().getNumBags());
	    }
	    
	    @Test
	    public void testSoftwareUpdatesBagCountWhenNewBagsLoaded() throws OverloadException {
	    	// initially, the bag dispenser should have 2 bags
	    	assertTrue(!scsLog.getPurchaseBagsController().getOutOfBagsFlag());
	    	assertEquals(2, scsLog.getPurchaseBagsController().getNumBagsInDispenser());
	    	
	    	bagDispenser.load(new ReusableBag());
	    	
	    	// check if the software updated its bag count accordingly
	    	assertEquals(3, scsLog.getPurchaseBagsController().getNumBagsInDispenser());
	    }
}
