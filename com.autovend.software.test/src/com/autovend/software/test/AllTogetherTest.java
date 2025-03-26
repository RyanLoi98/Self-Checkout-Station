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


import com.autovend.demo.hardwareSim;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.CreditCard;
import com.autovend.DebitCard;
import com.autovend.GiftCard;
import com.autovend.MagneticStripeFailureException;
import com.autovend.MembershipCard;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.AttendantCommunicationsController;
import com.autovend.software.CommunicationsController;
import com.autovend.software.CustomerIO;
import com.autovend.software.SelfCheckoutStationLogic;

import GUIComponents.AddByBrowsingPanel;
import GUIComponents.AddItemByPLUPanel;
import GUIComponents.AddItemsPanel;
import GUIComponents.EnterMembershipPanel;
import GUIComponents.InsufficientChangePanel;
import GUIComponents.ItemsListPanel;
import GUIComponents.PaymentPanel;
import GUIComponents.ProceedPaymentPanel;
import GUIComponents.PurchaseBagsPanel;
import GUIComponents.ThankYouPanel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;

public class AllTogetherTest {
	
	// Initialize membership variables
	int membershipLen;
    private HashMap<Integer, ArrayList<String>> memberships;
    
    // create a currency (Canadian dollars)
    private Currency CAD;
    // create denominations of bills
    int[] billDenominations;
    // create denominations of coins
    BigDecimal[] coinDenominations;

    // Create scale variables
    int maxWeight;
    int sensitivity;
    
    // create bags
    private CardIssuer CreditBank;
	private CardIssuer DebitBank;
	
	// Create payment cards
	private CreditCard creditCard;
	private DebitCard debitCard;
	private GiftCard giftCard;
	
	// Create membership card
	private MembershipCard memberCard;

	// Create date info for cards
	private Calendar calendar;
	
	// Create attendant station and related classes
	private SupervisionStation supervisionStation;
	private AttendantCommunicationsController attendantCommController;
	
	
	// Create 15 self checkout stations to hook into supervision station
	private SelfCheckoutStation scs1;
	private SelfCheckoutStation scs2;
	private SelfCheckoutStation scs3;
	private SelfCheckoutStation scs4;
	private SelfCheckoutStation scs5;
	private SelfCheckoutStation scs6;
	private SelfCheckoutStation scs7;
	private SelfCheckoutStation scs8;
	private SelfCheckoutStation scs9;
	private SelfCheckoutStation scs10;
	private SelfCheckoutStation scs11;
	private SelfCheckoutStation scs12;
	private SelfCheckoutStation scs13;
	private SelfCheckoutStation scs14;
	private SelfCheckoutStation scs15;
	
	// Create the logic of each station
	private SelfCheckoutStationLogic scsl1;
	private SelfCheckoutStationLogic scsl2;
	private SelfCheckoutStationLogic scsl3;
	private SelfCheckoutStationLogic scsl4;
	private SelfCheckoutStationLogic scsl5;
	private SelfCheckoutStationLogic scsl6;
	private SelfCheckoutStationLogic scsl7;
	private SelfCheckoutStationLogic scsl8;
	private SelfCheckoutStationLogic scsl9;
	private SelfCheckoutStationLogic scsl10;
	private SelfCheckoutStationLogic scsl11;
	private SelfCheckoutStationLogic scsl12;
	private SelfCheckoutStationLogic scsl13;
	private SelfCheckoutStationLogic scsl14;
	private SelfCheckoutStationLogic scsl15;

	// Create the customerIO of each station
	private CustomerIO cio1;
	private CustomerIO cio2;
	private CustomerIO cio3;
	private CustomerIO cio4;
	private CustomerIO cio5;
	private CustomerIO cio6;
	private CustomerIO cio7;
	private CustomerIO cio8;
	private CustomerIO cio9;
	private CustomerIO cio10;
	private CustomerIO cio11;
	private CustomerIO cio12;
	private CustomerIO cio13;
	private CustomerIO cio14;
	private CustomerIO cio15;
	
	// Number of self checkout stations
	private int stationCount;
	
	// Arraylist holding all station logics and stations
	
	private ArrayList<SelfCheckoutStationLogic> logicList;
	private ArrayList<SelfCheckoutStation> stationList;
	
	// Array holding all customerIOs
	private CustomerIO[] cioList = {cio1, cio2, cio3, cio4, cio5, cio6, cio7, cio8,
			cio9, cio10, cio11, cio12, cio13, cio14, cio15};
	
	// GUI panels for two stations for test purposes
	private AddItemsPanel addItemsPanel1;
	private PurchaseBagsPanel purchaseBagsPanel1;
	private ItemsListPanel itemsListPanel1;
	private ProceedPaymentPanel proceedPaymentPanel1;
	private PaymentPanel paymentPanel1;
	private EnterMembershipPanel enterMembershipPanel1;
	private ThankYouPanel thankYouPanel1;
	private InsufficientChangePanel insufficientChangePanel1;
	private AddItemByPLUPanel addItemByPLUPanel1;
	private AddByBrowsingPanel addByBrowsingPanel1;
	
	private AddItemsPanel addItemsPanel2;
	private PurchaseBagsPanel purchaseBagsPanel2;
	private ItemsListPanel itemsListPanel2;
	private ProceedPaymentPanel proceedPaymentPanel2;
	private PaymentPanel paymentPanel2;
	private EnterMembershipPanel enterMembershipPanel2;
	private ThankYouPanel thankYouPanel2;
	private InsufficientChangePanel insufficientChangePanel2;
	private AddItemByPLUPanel addItemByPLUPanel2;
	private AddByBrowsingPanel addByBrowsingPanel2;

	// sim panel for customer 1
	private hardwareSim hardwareSimPanel1;
	// sim panel for customer 2
	private hardwareSim hardwareSimPanel2;
	
	// Create sample grocery items 
	Barcode barcode = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
	BarcodedProduct product = new BarcodedProduct(barcode, "Milk", new BigDecimal("20"), 2.5);
	BarcodedUnit unit = new BarcodedUnit(barcode, 2.5);
	
	Barcode barcode2 = new Barcode(Numeral.nine, Numeral.one, Numeral.two, Numeral.three);
	BarcodedProduct product2 = new BarcodedProduct(barcode, "Eggs", new BigDecimal("5"), 2.5);
	BarcodedUnit unit2 = new BarcodedUnit(barcode2, 2.5);

	Barcode barcode3 = new Barcode(Numeral.nine, Numeral.one, Numeral.two, Numeral.three);
	BarcodedProduct product3 = new BarcodedProduct(barcode, "Milk", new BigDecimal("20"), 3);
	BarcodedUnit unit3 = new BarcodedUnit(barcode3, 2.5);
	
	PriceLookUpCode PLUCode1 = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four);
	PLUCodedProduct product4 = new PLUCodedProduct(PLUCode1, "Apple", new BigDecimal("20"));
	PriceLookUpCodedUnit unit4 = new PriceLookUpCodedUnit(PLUCode1, 5);
	
	PriceLookUpCode PLUCode2 = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.five);
	PLUCodedProduct product5 = new PLUCodedProduct(PLUCode2, "Orange", new BigDecimal("20"));
	PriceLookUpCodedUnit unit5 = new PriceLookUpCodedUnit(PLUCode2, 10);
	PriceLookUpCodedUnit unit6 = new PriceLookUpCodedUnit(PLUCode2, 3);
			
	
	/**
     * Initialize all the variables before each test
     */
    @Before
    public void setUp() throws Exception {
    	 // initialize the currency
        CAD = Currency.getInstance("CAD");
        // initialize denominations of money
        billDenominations = new int[]{5, 10, 20, 50, 100};
        // initialize denominations of coins
        coinDenominations = new BigDecimal[]{BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.25),
                BigDecimal.valueOf(1), BigDecimal.valueOf(2)};
        // initialize maxWeight
        maxWeight = 50;
        // initialize sensitivity
        sensitivity = 1;
        // Set number of self checkout stations
        stationCount = 15;
        // initialize self-checkout stations
 
        // Initialize valid membership length
        membershipLen = 5;
        // Initialize banks
        CreditBank = new CardIssuer("TD");
		DebitBank = new CardIssuer("RBC");
		
		// Initialize payment cards
		calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 2);
		creditCard = new CreditCard("credit", "123456789", "John", "344", "1234", false, true);
		debitCard = new DebitCard("debit", "145323789", "James", "542", "1234", false, true);
		giftCard = new GiftCard("gift", "123456789", "1234", CAD, new BigDecimal("100.00"));
		
		// Add payment cards to corresponding banks
		CreditBank.addCardData("123456789", "John", calendar, "344", new BigDecimal("600.00"));
		DebitBank.addCardData("145323789", "James", calendar, "542", new BigDecimal("400.00"));
		
		// Create a test membership card to use
        memberCard = new MembershipCard("membership", "12345", "Peter Parker", false);
        
        
		attendantCommController = new AttendantCommunicationsController();
		supervisionStation = attendantCommController.getSupervisionStation();
        
        attendantCommController.initializeSelfCheckoutStationLogics(stationCount, CAD, billDenominations, coinDenominations, maxWeight, 
        		sensitivity, CreditBank, DebitBank, membershipLen, "membershipdatabase.txt");
        
        logicList = attendantCommController.getStationLogicList();
        
        stationList = attendantCommController.getStationList();
        
        // Create customerIO instances for each station
		for (int i= 0; i < stationCount; i++) {
			cioList[i] = new CustomerIO(logicList.get(i));
			cioList[i].getFrame().setVisible(false);
			logicList.get(i).registerEnterMemberObserver(cioList[i]);
			logicList.get(i).registerPayCashObserver(cioList[i]);
			logicList.get(i).registerPayCardObserver(cioList[i]);
			logicList.get(i).registerAddItemObserver(cioList[i]);
			CommunicationsController.getCommunicator().registerOntoCustomers(cioList[i]);
		}
		
		// Get customer IO panels for two stations for testing purposes
		addItemsPanel1 = cio1.getAddItemsPanel();
		purchaseBagsPanel1 = cio1.getPurchaseBagsPanel();
		itemsListPanel1 = cio1.getItemsListPanel();
		proceedPaymentPanel1 = cio1.getProceedPaymentPanel();
		paymentPanel1 = cio1.getPaymentPanel();
		enterMembershipPanel1 = cio1.getEnterMembershipPanel();
		thankYouPanel1 = cio1.getThankYouPanel();
		insufficientChangePanel1 = cio1.getInsufficientChangePanel();
		// adding a hardware sim panel
		hardwareSimPanel1 = new hardwareSim(scs1, CreditBank, DebitBank);

		
		addItemsPanel2 = cio2.getAddItemsPanel();
		purchaseBagsPanel2 = cio2.getPurchaseBagsPanel();
		itemsListPanel2 = cio2.getItemsListPanel();
		proceedPaymentPanel2 = cio2.getProceedPaymentPanel();
		paymentPanel2 = cio2.getPaymentPanel();
		enterMembershipPanel2 = cio2.getEnterMembershipPanel();
		thankYouPanel2 = cio2.getThankYouPanel();
		insufficientChangePanel2 = cio2.getInsufficientChangePanel();
		// adding a hardware sim panel
		hardwareSimPanel2 = new hardwareSim(scs2, CreditBank, DebitBank);
		
		
		// Add sample grocery items to respective databases
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, product3);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode1, product4);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode2, product5);
    }
    
    /**
     * Tear down all the variables after each test
     */
    @After
    public void tearDown() throws Exception {
    	// Loop through each station to reset
    	for (int i= 0; i < stationCount; i++) {
			cioList[i] = null;
		}
    	
    	// Reset variables used in stations
    	CAD = null;
        billDenominations = null;
        coinDenominations = null;
        maxWeight =0;
        sensitivity = 0;
        stationCount = 0;
        calendar = null;
        CreditBank = null;
        DebitBank = null;
        memberCard = null;
        debitCard = null;
        creditCard = null;
        giftCard = null;
        
        attendantCommController = null;
        supervisionStation = null;
		logicList = null;
		logicList = null;
		
		addItemsPanel1 = null;
		purchaseBagsPanel1 = null;
		itemsListPanel1 = null;
		proceedPaymentPanel1 = null;
		paymentPanel1 = null;
		enterMembershipPanel1 = null;
		thankYouPanel1 = null;
		insufficientChangePanel1 = null;
		
		addItemsPanel2 = null;
		purchaseBagsPanel2 = null;
		itemsListPanel2 = null;
		proceedPaymentPanel2 = null;
		paymentPanel2 = null;
		enterMembershipPanel2 = null;
		thankYouPanel2 = null;
		insufficientChangePanel2 = null;
    }
    
    // Note for these all together tests, the individual components have been tested separately, so
    // we will be testing the entire process, and not specific pieces of each use case as they have
    // already been unit tested elsewhere
    
    /*
     * Test where purchasing a reusable bag, adding item by all methods, entering membership info by scanning card, and then
     * paying with card swipe. Expect the receipt to reflect this series of transactions
     */
    @Test
    public void addReusableBagAddItemsEnterMembershipPayWithCard() {
    	
    	
    	JButton bagPlusButton = purchaseBagsPanel1.getAddBagButton();
    	JButton bagPurchaseButton = purchaseBagsPanel1.getPurchaseButton();
    	JButton bagBackButton = purchaseBagsPanel1.getBackButton();
    	// Customer get to bag purchase screen and buys a bag
    	bagPlusButton.doClick();
    	bagPurchaseButton.doClick();
    	bagBackButton.doClick();
    	
    	JButton pluDeleteButton = addItemByPLUPanel1.getDelete();
    	JButton pluEnterButton = addItemByPLUPanel1.getEnter();
    	JButton pluAddButton = addItemByPLUPanel1.getAddToBill();
    	JButton pluFiveButton = addItemByPLUPanel1.getFiveButton();
    	JButton pluExitButton = addItemByPLUPanel1.getExitButton();
    	// Customer then add items to their bill using scan code, then enter PLU code, then by browsing
    	stationList.get(0).mainScanner.scan(unit);
    	pluFiveButton.doClick();
    	stationList.get(0).scale.add(unit5);
    	pluAddButton.doClick();
    	
    	JButton memberEnterButton = enterMembershipPanel1.getEnterButton();
    	JButton memberBackButton = enterMembershipPanel1.getBackButton();
    	// Customer then enters their membership information by scanning their member card
    	stationList.get(0).mainScanner.scan(memberCard);
    	memberEnterButton.doClick();
    	memberBackButton.doClick();
    	
    	// BUTTONS TO BE ADDED
		JButton payButton1 = addItemsPanel1.getPayButton();
		JButton payWithCard1 = paymentPanel1.getPayCreditButton();
		// swiping card can be potentially be done via the hardwaresimpanel1, check the usecase to see the correct button to click

    	// Customer then selects payment method and uses pay with credit card, and swipes their card.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Expected that receipt will be printed and contain this information.
    	// CODE BE ADDED
    
    }
    
    /*
     * Test where adding an item, partially paying for it with cash, then adding another item
     * and finishing payment with a gift card. Expect the receipt to reflect this series of transactions
     */
    @Test
    public void addItemPartialPayCashAddAnotherItemPayGiftCard() {
    	
    	// BUTTONS TO BE ADDED
    	// Customer buys an item by scanning it.
    	// CODE BE ADDED
    	
    	
    	// BUTTONS TO BE ADDED
		JButton paymentButton = addItemsPanel1.getPayButton();
		JButton paywithcash =  paymentPanel1.getPayCashButton();
		JButton keepScanning = paymentPanel1.getKeepScanningButton();
    	// Customer then pays a portion of their bill with cash.
    	// CODE BE ADDED


    	// BUTTONS TO BE ADDED
    	// Customer then decides to add another item by scanning it.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
		JButton paywithgiftcard = paymentPanel1.getPayGiftButton();
    	// Customer then selects payment method and uses pay with gift card.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Expected that receipt will be printed and contain this information.
    	// CODE BE ADDED
    }
    
    /*
     * Test where entering membership first by typing, then using own bag, then adding items, and then
     * paying with card tap. Expect the receipt to reflect this series of transactions
     */
    @Test
    public void enterMembershipUseOwnBagAddItemsPayCard() {
    	
    	JButton memberOneButton = enterMembershipPanel1.getOneButton();
    	JButton memberTwoButton = enterMembershipPanel1.getTwoButton();
    	JButton memberThreeButton = enterMembershipPanel1.getThreeButton();
    	JButton memberFourButton = enterMembershipPanel1.getFourButton();
    	JButton memberFiveButton = enterMembershipPanel1.getFiveButton();
    	JButton memberSixButton = enterMembershipPanel1.getSixButton();
    	JButton memberSevenButton = enterMembershipPanel1.getSevenButton();
    	JButton memberEightButton = enterMembershipPanel1.getEightButton();
    	JButton memberNineButton = enterMembershipPanel1.getNineButton();
    	JButton memberZeroButton = enterMembershipPanel1.getZeroButton();
    	JButton memberDelButton = enterMembershipPanel1.getDelButton();
    	JButton memberEnterButton = enterMembershipPanel1.getEnterButton();
    	JButton memberBackButton = enterMembershipPanel1.getBackButton();
    	// Customer starts by typing in their membership number.
    	memberOneButton.doClick();
    	memberTwoButton.doClick();
    	memberThreeButton.doClick();
    	memberFourButton.doClick();
    	memberFiveButton.doClick();
    	memberEnterButton.doClick();
    	memberBackButton.doClick();

    	// BUTTONS TO BE ADDED
		JButton addOwnBagButton = addItemsPanel1.getOwnBagsButton();
		JButton bagItemsDoneButton = addItemsPanel1.getDoneButton();
		JButton attendantNoBagVerButton;

    	// Customer then brings their own bag to use, and gets approval from the attendant.


    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
		// can potentially use the hardwareSimPanel to add item

    	// Customer then scans a few items, and enters a PLU code for an item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
		JButton paymentButton = addItemsPanel1.getPayButton();
		JButton payDebitCard = paymentPanel1.getPayDebitButton();
		// can likely tap debit card though the hardwaresim Panel

    	// Customer then selects payment method and uses pay with debit card, and taps their card.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Expected that receipt will be printed and contain this information.
    	// CODE BE ADDED
    }
    
    /*
     * Test where multiple stations are running simultaneously, to ensure correct transactions are linked to 
     * correct station. Expect the receipt of each to only contain related transactions.
     */
    @Test
    public void twoStationsSimulatenousNoInterventions() throws IOException {
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 scans an item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 scans an item.
    	// CODE BE ADDED
    	
    	JButton memberEnterButton = enterMembershipPanel1.getEnterButton();
    	JButton memberBackButton = enterMembershipPanel1.getBackButton();
    	// Customer 1 enters their membership number by swiping card.
    	
    	boolean swipeError = false;
    	// Repeat until a bad swipe achieved
    	while(!swipeError) {
	    	try {
		    	stationList.get(0).cardReader.swipe(memberCard, null);
		    	memberEnterButton.doClick();
	    	 } catch(MagneticStripeFailureException e) {
		    		// On swipe error, break from loop to pass in swipe error message
		    		break;
		    	}
    	}
    	memberBackButton.doClick();
    	
    	// BUTTONS TO BE ADDED
		JButton addByPluButton2 = addItemsPanel2.getPLUButton();
    	// Customer 2 adds another item by PLU code.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
		JButton paymentButton2 = addItemsPanel2.getPayButton();
		JButton payCashButton2 = paymentPanel2.getPayCashButton();
    	// Customer 2 makes a partial payment with cash.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 scans another item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
		JButton keepScanning2 = paymentPanel2.getKeepScanningButton();
    	// Customer 2 scans another item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
		JButton paymentButton1 = addItemsPanel1.getPLUButton();
		JButton payDebit1 = paymentPanel1.getPayDebitButton();
    	// Customer 1 pays in full with credit card insert.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 receives their receipt.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 pays remaining with cash.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 receives their receipt.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Expected that each receipt will be printed and contain only their relevant information.
    	// CODE BE ADDED
  
    }
    
    /*
     * Test where multiple stations are running simultaneously, to ensure correct transactions are linked to 
     * correct station. This time, one station will require attendant assistance, and the other will continue
     * its transaction. Expect the receipt of each to only contain related transactions, and not be stalled by
     * the attendant resolving issues at other stations.
     */
    @Test
    public void twoStationsSimulatenousOneStationBlocked() throws IOException {
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 scans an item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 scans an item.
    	// CODE BE ADDED
    	
    	JButton memberEnterButton = enterMembershipPanel1.getEnterButton();
    	JButton memberBackButton = enterMembershipPanel1.getBackButton();
    	// Customer 1 enters their membership number by swiping card.
    	
    	boolean swipeError = false;
    	// Repeat until a bad swipe achieved
    	while(!swipeError) {
	    	try {
		    	stationList.get(0).cardReader.swipe(memberCard, null);
		    	memberEnterButton.doClick();
	    	 } catch(MagneticStripeFailureException e) {
		    		// On swipe error, break from loop to pass in swipe error message
		    		break;
		    	}
    	}
    	memberBackButton.doClick();
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 adds another item with attendant assistance request.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 scans another item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Attendant assists Customer 2 with the add item and unblocks station.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 pays in full with credit card insert.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 receives their receipt.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 scans another item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 pays in full with cash.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 receives their receipt.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Expected that each receipt will be printed and contain only their relevant information.
    	// CODE BE ADDED
    
    }
    
    /*
     * Test where multiple stations are running simultaneously, to ensure correct transactions are linked to 
     * correct station. This time, both stations will require attendant assistance for different reasons.
     * Ensure that the messages are communicated to the attendant correctly for each station, and that 
     * each one can continue to operate after its issue has been resolved.
     */
    @Test
    public void twoStationsSimulatenousBothStationsBlocked() {
    	
    	// BUTTONS TO BE ADDED
		//customer 1's buttons
		JButton addOwnBagButton1 = addItemsPanel1.getOwnBagsButton();
		JButton bagItemsDoneButton1 = addItemsPanel1.getDoneButton();

    	// Customer 1 wants to use their own bag and waits for assistance.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED

    	// Customer 2 scans an item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 adds another item with attendant assistance request.
		JButton attendanthelp2 = addItemsPanel2.getHelpButton();

    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Attendant assists Customer 1 with the own bag request and unblocks station.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 scans an item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 scans their membership card to enter the details.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Attendant assists Customer 2 with the add item request and unblocks station.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 pays in full with credit card tap.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 1 receives their receipt.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 scans another item.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
		JButton finishAndPayButton2 = addItemsPanel2.getPayButton();
		// Money buttons can be inserted using hardwareSimPanel2.getaddButton5/6/7 (each of these buttons adds different amounts of money - so look at the harware sim class for more info)

    	// Customer 2 pays in full with cash.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Customer 2 receives their receipt.
    	// CODE BE ADDED
    	
    	// BUTTONS TO BE ADDED
    	// Expected that each receipt will be printed and contain only their relevant information.
    	// CODE BE ADDED
    
    }

}
