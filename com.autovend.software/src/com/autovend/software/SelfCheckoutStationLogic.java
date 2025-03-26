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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.autovend.PriceLookUpCode;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.devices.TouchScreen;
import com.autovend.devices.observers.TouchScreenObserver;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

/**
 * Logic of SelfCheckout System
 *
 */
public class SelfCheckoutStationLogic {

	// SelfCheckout Station connected to this software
	private SelfCheckoutStation station;

	private PrintReceiptController receiptController;

	private PayCashController payCashController;

	private AddItemController addItemController;

	private PurchaseBagsController purchaseBagsController;

	private EnterMembership enterMembershipController;

	private BillRecord record;

	private boolean scanning;

	private PayCardController payCardController;

	private AdjustChangeController changeController;

	public enum MethodOfPayment {
		CASH, CREDIT, DEBIT, GIFT
	}

	private MethodOfPayment selectedPayment;

	/**
	 * Constructor for SelfCheckoutStation Logic
	 * 
	 * @param fileName:     filename to read and write membership database to/from.
	 *                      Use: "membershipdatabase.txt"
	 * @param membershipLen
	 * @param scs:          Station software is to be installed on
	 */
	public SelfCheckoutStationLogic(SelfCheckoutStation scs, CardIssuer CreditBank, CardIssuer DebitBank,
			int membershipLen, String fileName,
			int billsMin, int coinsMin) {
		station = scs;
		receiptController = new PrintReceiptController(scs, this);
		payCashController = new PayCashController(scs, receiptController);
		addItemController = new AddItemController(scs);
		changeController = new AdjustChangeController(scs, billsMin, coinsMin);
		purchaseBagsController = new PurchaseBagsController(scs, this);

		payCardController = new PayCardController(scs, receiptController, CreditBank, DebitBank);
		payCashController.blockPayment();

		enterMembershipController = new EnterMembership(scs, membershipLen, fileName);

		scs.printer.register(receiptController);
	}

	public void startUpStation() {
		station.baggingArea.enable();
		station.billInput.enable();
		station.billOutput.enable();
		station.billStorage.enable();
		station.billValidator.enable();
		station.cardReader.enable();
		station.coinSlot.enable();
		station.coinStorage.enable();
		station.coinTray.enable();
		station.coinValidator.enable();
		station.handheldScanner.enable();
		station.mainScanner.enable();
		station.printer.enable();
		station.scale.enable();
		station.screen.enable();

		changeController.disableStationIfLowChange();
	}

	public void shutDownStation() {
		station.baggingArea.disable();
		station.billInput.disable();
		station.billOutput.disable();
		station.billStorage.disable();
		station.billValidator.disable();
		station.cardReader.disable();
		station.coinSlot.disable();
		station.coinStorage.disable();
		station.coinTray.disable();
		station.coinValidator.disable();
		station.handheldScanner.disable();
		station.mainScanner.disable();
		station.printer.disable();
		station.scale.disable();
		station.screen.disable();
	}

	public void preventStationUse() {
		station.baggingArea.disable();
		station.billInput.disable();
		station.cardReader.disable();
		station.coinSlot.disable();
		station.handheldScanner.disable();
		station.mainScanner.disable();
		station.printer.disable();
		station.scale.disable();
		//station.screen.disable();
		addItemController.preventStation();
	}

	public void premitStationUse() {
		station.baggingArea.enable();
		station.billInput.enable();
		station.cardReader.enable();
		station.coinSlot.enable();
		station.handheldScanner.enable();
		station.mainScanner.enable();
		station.printer.enable();
		station.scale.enable();
		//station.screen.enable();
		addItemController.permitStation();

	}

	public void sessionStart() {
		changeController.startSession();
	}

	public boolean isDisabled() {
		return station.baggingArea.isDisabled();
	}

	/**
	 * Customer has notified of a partial payment, this is called while they are
	 * paying
	 */
	public void partialPayment() {
		payCashController.blockPayment();
		addItemController.unBlockScanning();

	}

	/**
	 * Called when Customer I/O notifies that customer wishes to pay with Cash
	 */
	public void CustomerPay() {
		payCashController.unBlockPayment();
		addItemController.blockScanning();
		payCashController.startPay(addItemController.getBillRecord());
	}

	/**
	 * Called when Customer I/O notifies that customer wishes to pay with credit and
	 * its amount
	 */
	public void CustomerCreditPay(BigDecimal amount) {
		payCashController.unBlockPayment();
		addItemController.blockScanning();
		payCardController.customerCreditPay(amount, addItemController.getBillRecord());
	}

	/**
	 * Called when Customer I/O notifies that customer wishes to pay with debit and
	 * its amount
	 */
	public void CustomerDebitPay(BigDecimal amount) {
		payCashController.unBlockPayment();
		payCardController.customerDebitPay(amount, addItemController.getBillRecord());
	}

	/**
	 * Called when Customer I/O notifies that customer wishes to pay with gift card
	 * and its amount
	 */
	public void CustomerGiftPay(BigDecimal amount) {
		payCashController.unBlockPayment();
		payCardController.customerGiftPay(amount, addItemController.getBillRecord());
	}

	public BillRecord getBillRecord() {
		return addItemController.getBillRecord();
	}

	/**
	 * Called when the Attendant I/O approves the customer's request to not bag the
	 * last item added.
	 */
	public void noBaggingRequestApproved() {

		// Option 2
		// Customer I/O: Signals the System about a do-not-bag request (see Do Not Place
		// Item
		// in Bagging Area);

		addItemController.customerDidNotBagTheItem();

	}

	/**
	 *
	 * Called when the Attendant I/O notifies that everything is fine now
	 */
	public void weightDiscprepancyApproval() throws OverloadException {

		// Option 3
		// Attendant I/O: Signals the System of a weight-discrepancy approval

		addItemController.attendantApprovalForWeightDiscrepancy();

	}

	public void blockAddingItems() {

	}

	/**
	 * The Customer or Attendant IO signals that an item has been picked. Throws an
	 * Overload Exception if any scale is overloaded and Simulation Exception if the
	 * Item is not a proper Product.
	 * 
	 * @param item Product chosen.
	 * @throws OverloadException
	 * @throws SimulationException
	 */
	public void itemPicked(Product item) {
		addItemController.startAdding(item);
	}

	/**
	 * The Customer or Attendant IO signals that an item has been picked. Throws an
	 * Overload Exception if any scale is overloaded and Simulation Exception if the
	 * Item is not a proper Product.
	 * 
	 * @param item Product chosen.
	 * @throws OverloadException
	 * @throws SimulationException
	 */
	public void itemPicked(BarcodedProduct item) {
		addItemController.startAdding(item);
	}

	/**
	 * The Customer or Attendant IO signals that a barcoded item has been picked with a given quantity.
	 * Throws an Overload Exception if any scale is overloaded and Simulation Exception if the
	 * Item is not a proper Product.
	 *
	 * @param item Product chosen.
	 * @throws OverloadException
	 * @throws SimulationException
	 */
	public void barcodedItemPicked(BarcodedProduct item, int quantity){addItemController.startAddingBarcodedItem(item, quantity);}

	/**
	 * The Customer or Attendant IO signals that an item has been picked. Throws an
	 * Overload Exception if any scale is overloaded and Simulation Exception if the
	 * Item is not a proper Product.
	 * 
	 * @param item Product chosen.
	 * @throws OverloadException
	 * @throws SimulationException
	 */
	public void itemPicked(PLUCodedProduct item) {
		addItemController.startAdding(item);
	}

	public void enteredPLU(PriceLookUpCode code) {
		addItemController.givenPLU(code);
	}

	/**
	 * Called when Customer I/O notifies that customer wishes to purchase bags.
	 */
	public void CustomerPurchaseBags(int quantity) {
		purchaseBagsController.addReusableBags(quantity);
	}

	/*
	 * Used by the PurchaseBagsController to interact with the AddItemsController;
	 * the PurchaseBagsController has no knowledge of addItemsController, so, it
	 * tells the logic to addBags and the logic acts as a "middleman" between the
	 * purchaseBagsController and AddItemsController.
	 */
	public void addBag(String bagName, BigDecimal bagCost, Double bagWeight) {
		addItemController.addBag(bagName, bagCost, bagWeight);
	}

	/**
	 * Called when Customer I/O notifies that the customer wishes to use their own
	 * bag(s).
	 */
	public void CustomerAddOwnBagsStart() {
		addItemController.addCustomerBagStart();
	}

	/**
	 * Called when attendant I/O notifies that the customers added bags should be
	 * approved.
	 */
	public void CustomerAddOwnBagsApproved() {
		addItemController.addCustomerBagApproved();
	}

	/**
	 * Called when attendant I/O notifies that the customers added bags should not
	 * be approved
	 */
	public void CustomerAddOwnBagsDisapproved() {
		addItemController.addCustomerBagDisapproved();
	}

	/**
	 * Receipt printer notifies of session end
	 */
	public void sessionEnd() {
		System.out.println("called");
		addItemController.resetRecord();
		payCashController.sessionEnd();
		payCardController.sessionEnd();
	}

	/**
	 * Registers printReceiptObserver to receive event notifications from
	 * PrintReceipt logic. Used by Customer I/O to communicate with Logic.
	 * 
	 * @param observer: The observer to be added.
	 */
	public void registerPrintObserver(PrintReceiptObserver observer) {
		receiptController.registerObserver(observer);
	}

	/**
	 * sets the current payment method of the machine
	 * 
	 * @param selectedMethod
	 */
	public void setSelectedPayment(MethodOfPayment selectedMethod) {
		System.out.println(selectedMethod);
		selectedPayment = selectedMethod;
	}

	/**
	 * Gets the current payment method of the machine
	 * 
	 * @return
	 */
	public MethodOfPayment getSelectedPayment() {
		return selectedPayment;
	}

	/**
	 * Registers payCash to receive event notifications from PayCash logic. Used by
	 * Customer I/O to communicate with Logic.
	 * 
	 * @param observer: The observer to be added.
	 */
	public void registerPayCashObserver(PayCashObserver observer) {
		payCashController.registerObserver(observer);
	}

	/**
	 * Registers printReceiptObserver to receive event notifications from
	 * PrintReceipt logic. Used by Customer I/O to communicate with Logic.
	 * 
	 * @param observer: The observer to be added.
	 */
	public void registerPayCardObserver(PayCardObserver observer) {
		payCardController.registerObserver(observer);
	}

	/**
	 * Registers printReceiptObserver to receive event notifications from
	 * PrintReceipt logic. Used by Customer I/O to communicate with Logic.
	 * 
	 * @param observer: The observer to be added.
	 */
	public void registerAddItemObserver(AddItemObserver observer) {
		addItemController.registerObserver(observer);
	}

	/**
	 * Used to simulate taking a PLU code input
	 * 
	 * @param code
	 */
	public void receiveCustomerInputPLU(PriceLookUpCode code) {
		addItemController.givenPLU(code);
	}

	/*
	 * Getters for unit test purposes - TODO: Should be replaced by Dummy classes in final
	 * iteration
	 */
	public PurchaseBagsController getPurchaseBagsController() {
		return purchaseBagsController;
	}

	public AddItemController getAddItemController() {
		return addItemController;
	}

	/**
	 * Registers enterMembershipControllerObserver
	 * 
	 * @param observer: enterMembershipController observer to be registered
	 */
	public void registerEnterMemberObserver(EnterMembershipObserver observer) {
		enterMembershipController.registerObserver(observer);
	}

	/**
	 * deRegisters enterMembershipControllerObserver
	 * 
	 * @param observer: enterMembershipController observer to be deregistered
	 */
	public void deregisterEnterMemberObserver(EnterMembershipObserver observer) {
		enterMembershipController.deregisterObserver(observer);
	}

	// getter to obtain the arraylist of observers
	public ArrayList<EnterMembershipObserver> getObservers() {
		return enterMembershipController.getObservers();
	}

	/**
	 * Inform all observers of points
	 * 
	 * @param points: points to inform observers
	 */
	public void updatePoints(int points) {
		enterMembershipController.updatePoints(points);
	}

	/**
	 * setter to change the valid membership length
	 *
	 * Caution! The membership database will be wiped if this method is used
	 *
	 * Precondition: Membership length must be an integer >= 0 Poscondition:
	 * membershiplen is changed, membership data base is wiped
	 *
	 * @param membershipLen: a positive integer for membership length
	 * @return: 0 if success, -1 if membership length is <= 0
	 */
	public int editMembershipLen(int membershipLen) {
		return enterMembershipController.editMembershipLen(membershipLen);
	}

	/**
	 * setter to change points for individual membership numbers
	 * 
	 * @param membershipNum: a valid membership number to which we will update the
	 *                       points on
	 * @param points:        an integer representing the points that a membership
	 *                       number will now have
	 * @return: 0 if success, -1 if membershipNum is invalid, -2 if points are
	 *          invalid, -3 is membership number is not yet in the memberships
	 *          database
	 *
	 *          Precondition: - membershipNum must be a valid length positive
	 *          integer, and must be currently in the memberships database - points
	 *          must be a positive integer
	 *
	 *          postcondition: a member's points is updated with the new points
	 *          amount
	 */
	public int editIndivPoints(int membershipNum, Integer points) {
		return enterMembershipController.editIndivPoints(membershipNum, points);
	}

	/**
	 * setter to add individual memberships
	 * 
	 * @param membershipNumber: a valid membership number for a member to be added
	 *                          to the memberships database
	 * @param Fname:            first name of the member
	 * @param Lname:            last name of the member
	 * @param points:           integer representing the number of points this
	 *                          member will have
	 * @return: -1 if invalid membership number, -2 if failed due to the membership
	 *          number already being used, -3 for negative points, 0 if success
	 *
	 *          precondition: - the membership number is a valid length, positive
	 *          integer, and is unique (not yet in the database) - points must be a
	 *          positive integer
	 *
	 *          postcondition: a new membership is created and added to the
	 *          database, as per the database rules
	 */
	public int addMembership(int membershipNumber, String Fname, String Lname, Integer points) {
		return enterMembershipController.addMembership(membershipNumber, Fname, Lname, points);
	}

	/**
	 * // setter to remove a membership
	 * 
	 * @param membershipNumber: a valid membership number of a member already in the
	 *                          memberships database
	 * @return: 0 for success, -1 for invalid membership number, -2 for membership
	 *          number not in the memberships database
	 *
	 *          Precondition: membership number must be valid, and membership number
	 *          must be presently in the memberships database
	 */
	public int removeMembership(Integer membershipNumber) {
		return enterMembershipController.removeMembership(membershipNumber);
	}

	/**
	 * getter to obtain the number of points a user has
	 * 
	 * @param membershipNumber: a valid membership number of a member already in the
	 *                          memberships database
	 * @return: upon success the number of points the user has (>=0), else: -1 if
	 *          the membership number is invalid, -2 if the membership number is not
	 *          yet in the memberships database, -3 if the points was not an integer
	 *          (shouldn't ever happen if the preconditions for memberships database
	 *          is followed).
	 *
	 *          Precondition: membership number is valid and a positive integer, and
	 *          in the membership database
	 */
	public int getPoints(int membershipNumber) {
		return enterMembershipController.getPoints(membershipNumber);
	}

	/**
	 * Getter to get a customer's first name
	 * 
	 * @param membershipNumber: a valid membership number of a member already in the
	 *                          memberships database
	 * @return: upon success returns a string of the customers first name, else: "1"
	 *          if invalid membership number, "2" if the membership number is not
	 *          yet in the memberships database.
	 *
	 *          Precondition: membership number is valid and a positive integer, and
	 *          in the membership database
	 */
	public String getFName(int membershipNumber) {
		return enterMembershipController.getFName(membershipNumber);
	}

	/**
	 * Getter to get a customer's last name
	 * 
	 * @param membershipNumber: a valid membership number of a member already in the
	 *                          memberships database
	 * @return: upon success returns a string of the customers last name, else: "1"
	 *          if invalid membership number, "2" if the membership number is not
	 *          yet in the memberships database.
	 *
	 *          Precondition: membership number is valid and a positive integer, and
	 *          in the membership database
	 */
	public String getLName(int membershipNumber) {
		return enterMembershipController.getLName(membershipNumber);
	}

	/**
	 * getter to obtain if the user is in the membership database, true if in the
	 * database, otherwise false
	 * 
	 * @param membershipNumber: a valid membership number of a member already in the
	 *                          memberships database
	 * @return: true if membership number is in the database, else false
	 *
	 *          Precondition: membership number is valid and a positive integer, and
	 *          in the membership database
	 */
	public Boolean isMember(int membershipNumber) {
		return enterMembershipController.isMember(membershipNumber);
	}

	// getter to get the current valid membership length
	public int getMembershipLen() {
		return enterMembershipController.getMembershipLen();
	};

	/**
	 * Supplemental function that determines the number of digits an integer has
	 * 
	 * @param number: an integer
	 * @return: the number of digits an integer number has
	 */
	public int lenOfInt(int number) {
		return enterMembershipController.lenOfInt(number);
	}

	/**
	 * Function to get member number after scan/swipe for test cases Due to random
	 * chance failures, the number can become corrupted, which could cause certain
	 * test cases to inadvertently fail. Logic in the GUI class is implemented to
	 * capture such failures.
	 * 
	 * @return: the swiped/scanned in card number
	 */
	public String getMemberCardNum() {
		return enterMembershipController.getCardNumber();
	}

	/**
	 * Method that obtains the user's input for a membership number and checks to
	 * see if it is valid. Upon success the
	 *
	 *
	 * @return: 0 if success, -1 if the user cancelled membership number input, -2
	 *          if invalid membership number, -3 if the membership number is not in
	 *          the memberships database. -4 (extremely rare error) if points were
	 *          non integers
	 */
	public int validateMembershipNumber(String membershipNumberStr) {
		return enterMembershipController.enterMembershipNumber(membershipNumberStr);
	}

	/**
	 * Setter to update the memberships database with a new one - primarily used for
	 * testing
	 * 
	 * @param memberships: field to act as the membership database, memberships will
	 *                     be stored in a hashmap: key = membership number Value =
	 *                     ArrayList<String>, where the first element is the
	 *                     member's first name, second element is the member's last
	 *                     name, third element is the points a member has - the
	 *                     string representing this must be capable of being
	 *                     converted to an integer.
	 *
	 *                     Precondition: - membership number must be a valid length
	 *                     and a positive integer 1st element in the arraylist
	 *                     associated with the membership number must be the
	 *                     member's first name, the second element must be the
	 *                     member's last name, and the last element must be the
	 *                     points that this member has (this must also be a string
	 *                     capable of being converted to a positive integer).
	 *
	 *
	 */
	public void updateMemberships(HashMap<Integer, ArrayList<String>> memberships) {
		enterMembershipController.updateMemberships(memberships);
	}

	/**
	 * Getter to obtain the memberships database
	 * 
	 * @return: memberships database
	 */
	public HashMap<Integer, ArrayList<String>> getMemberships() {
		return enterMembershipController.getMemberships();
	}

	/**
	 * Informs all Entermembership observers of the status of the memberships
	 * database (if it was loaded correctly or not)
	 * 
	 * @param status: True = membership database is corrupted, false = membership
	 *                database intact
	 */
	public void updateDataBaseStatus(boolean status) {
		enterMembershipController.updateDataBaseStatus(status);
	}

	/**
	 * Setter to update the membership file
	 */
	public void uploadMemberships() {
		enterMembershipController.uploadMemberships();
	}

	/**
	 * Function that loads the membership database from a file upon instantiation of
	 * this class
	 */
	public void loadMemberships() {
		enterMembershipController.loadMemberships();
	}

	public int validateMembershipCode() {
		return enterMembershipController.getValidCode();
	}

	public void registerScreenObserver(TouchScreenObserver obs) {
		station.screen.register(obs);
	}

}
