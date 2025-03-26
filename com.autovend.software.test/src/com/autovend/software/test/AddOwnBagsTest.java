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

import GUIComponents.AddItemsPanel;
import GUIComponents.AttendantIO;
import com.autovend.*;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Test suite for Use Own Bags use case GUI connection
 */
public class AddOwnBagsTest {
	Currency CAD;
	int[] billDenominations;
	BigDecimal[] coinDenominations;
	int maxWeight;
	int sensitivity;

	int memLen;
	String memFilename;

	SelfCheckoutStation station;
	SelfCheckoutStationLogic logic;
	CustomerIO cIO;
	AttendantIOStub aIO;
	CustomerIOListenerStub ioListenerStub;
	AddItemsPanel itemsPanel;

	@Before
	public void setup() {
		CAD = Currency.getInstance(Locale.CANADA);
		billDenominations = new int[] {5, 10, 15, 20, 50, 100};
		coinDenominations = new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"),
				new BigDecimal("0.25"), new BigDecimal(1), new BigDecimal(2)};
		maxWeight = 25;
		sensitivity = 1;

		station = new SelfCheckoutStation(CAD, billDenominations, coinDenominations, maxWeight, sensitivity);

		memLen = 5;
		memFilename = "membershipdatabase.txt";

		logic = new SelfCheckoutStationLogic(station, null, null, memLen, memFilename, 10, 10);

		cIO = new CustomerIO(logic);
		cIO.registerSelf();

		aIO = new AttendantIOStub(logic);
		CommunicationsController.getCommunicator().registerOntoAttendant(aIO);

		ioListenerStub = new CustomerIOListenerStub();
		cIO.register(ioListenerStub);

		itemsPanel = cIO.getAddItemsPanel();
	}

	@After
	public void teardown() {
		station = null;
		logic = null;
		cIO = null;
		itemsPanel = null;
	}

	/**
	 * Test to confirm that the popup with correct content shows up when button is clicked
	 */
	@Test public void testPopupAppears() {
		itemsPanel.getOwnBagsButton().doClick();
		JPanel expectedContent = itemsPanel.getAddOwnBagsPopup();
		JPanel popupContent = (JPanel) itemsPanel.getPopup().getContentPane();
		Assert.assertTrue("Popup should show up", itemsPanel.getPopup().isVisible());
		Assert.assertEquals("Popup content should be the 'add own bags' panel", popupContent, expectedContent);
	}

	/**
	 * Test to confirm that the popup contents have correctly transitioned to the waiting panel
	 */
	@Test public void testPopupTransition() {
		itemsPanel.getOwnBagsButton().doClick();
		itemsPanel.getDoneButton().doClick();
		JPanel expectedContent = itemsPanel.getWaitBagsPopup();
		JPanel popupContent = (JPanel) itemsPanel.getPopup().getContentPane();
		Assert.assertTrue("Popup should show up", itemsPanel.getPopup().isVisible());
		Assert.assertEquals("Popup content should be the 'wait for bag verification' panel", popupContent, expectedContent);
	}

	/**
	 * Test to confirm that the closePopup method successfully closes the popup on the
	 * 'please place bag in bagging area' popup screen
	 */
	@Test public void testCloseFirstPopup() {
		itemsPanel.getOwnBagsButton().doClick();
		Assert.assertTrue("Popup should show up", itemsPanel.getPopup().isVisible());
		itemsPanel.closePopup();
		Assert.assertFalse("Popup should be no longer visible", itemsPanel.getPopup().isVisible());
	}

	/**
	 * Test to confirm that the closePopup method successfully closes the popup on the
	 * 'wait for bag verification' popup screen
	 */
	@Test public void testCloseSecondPopup() {
		itemsPanel.getOwnBagsButton().doClick();
		itemsPanel.getDoneButton().doClick();
		Assert.assertTrue("Popup should show up", itemsPanel.getPopup().isVisible());
		itemsPanel.closePopup();
		Assert.assertFalse("Popup should be no longer visible", itemsPanel.getPopup().isVisible());
	}

	/**
	 * Tests two random buttons on each side of the screen to confirm they're disabled when popup shown.
	 */
	@Test public void testPopupDisablesButtons() {
		itemsPanel.getOwnBagsButton().doClick();

		Assert.assertFalse("Background button on right side should be disabled",
				itemsPanel.getPayButton().isEnabled());
		Assert.assertFalse("Background button on left side should be disabled",
				cIO.getItemsListPanel().getMembershipButton().isEnabled());
	}

	/**
	 * Tests same two buttons on each side of the screen to confirm they're re-enabled when popup closes.
	 */
	@Test public void testPopupReEnablesButtons() {
		itemsPanel.getOwnBagsButton().doClick();
		itemsPanel.closePopup();

		Assert.assertTrue("Background button on right side should be re-enabled",
				itemsPanel.getPayButton().isEnabled());
		Assert.assertTrue("Background button on left side should be re-enabled",
				cIO.getItemsListPanel().getMembershipButton().isEnabled());
	}

	/**
	 * Test to confirm the listener is notified of button presses
	 */
	@Test public void testOwnBagsListenerNotified() {
		Assert.assertFalse("Listener shouldn't think customer already pressed 'use own bags' button",
				ioListenerStub.knowsCustomerUsingOwnBags);

		itemsPanel.getOwnBagsButton().doClick();
		Assert.assertTrue("Listener should know customer wants to use own bags",
				ioListenerStub.knowsCustomerUsingOwnBags);
		Assert.assertFalse("Listener shouldn't think customer is using done placing bags yet",
				ioListenerStub.knowsCustomerDonePlacingBags);

		itemsPanel.getDoneButton().doClick();
		Assert.assertTrue("Listener should know that customer is done placing bags",
				ioListenerStub.knowsCustomerDonePlacingBags);
	}

	/**
	 * Test to confirm that the listener can interact with add item controller
	 */
	@Test public void testListenerNotifyController() {
		Assert.assertTrue("Controller should initially have weight checking enabled",
				logic.getAddItemController().isEnableCheckWeight());

		itemsPanel.getOwnBagsButton().doClick();
		Assert.assertTrue("Listener should know customer wants to use own bags",
				ioListenerStub.knowsCustomerUsingOwnBags);

		Assert.assertFalse("Controller should no longer have weight checking enabled",
				logic.getAddItemController().isEnableCheckWeight());
	}

	/**
	 * Test to confirm that listener response (assuming acceptance of bags) to event can
	 * close popup in response
	 */
	@Test public void testOwnBagsConfirmed() {
		itemsPanel.getOwnBagsButton().doClick();
		itemsPanel.getDoneButton().doClick();

		Assert.assertTrue("Listener should know that customer wants to use own bags",
				ioListenerStub.knowsCustomerUsingOwnBags);
		Assert.assertTrue("Listener should know that customer is done placing bags",
				ioListenerStub.knowsCustomerDonePlacingBags);

		ioListenerStub.acceptOwnBags(logic);

		Assert.assertFalse("Listener's acceptance should close the popup",
				itemsPanel.getPopup().isVisible());
	}

	/**
	 * Test to confirm that listener response (assuming rejection of bags) to event can
	 * close popup in response
	 */
	@Test public void testOwnBagsRejected() {
		itemsPanel.getOwnBagsButton().doClick();
		itemsPanel.getDoneButton().doClick();

		Assert.assertTrue("Listener should know that customer wants to use own bags",
				ioListenerStub.knowsCustomerUsingOwnBags);
		Assert.assertTrue("Listener should know that customer is done placing bags",
				ioListenerStub.knowsCustomerDonePlacingBags);

		ioListenerStub.rejectOwnBags(logic);

		Assert.assertFalse("Listener's rejection should close the popup", itemsPanel.getPopup().isVisible());
	}

	/**
	 * Stub required to test connection to CommunicationsController
	 */
	private class AttendantIOStub extends AttendantIO {
		SelfCheckoutStationLogic logic;

		public AttendantIOStub() {}
		public AttendantIOStub(SelfCheckoutStationLogic logic) {
			this.logic = logic;
		}

		/**
		 * Passes the info along to station logic
		 * @param station
		 *            The device whose session is in progress.
		 */
		@Override
		public void addOwnBagsStart(SelfCheckoutStation station) {
			logic.CustomerAddOwnBagsStart();
		}

		// no action required here because the listener stub is the one being used
		// to test responses in this suite
		@Override
		public void addOwnBagsComplete(SelfCheckoutStation station) {}
		@Override
		public void OwnBagsCancelled(SelfCheckoutStation station) {}

		// not relevant
		@Override
		public AttendantCommunicationsController getAttendantCommunicationsController() {
			return null;
		}
		@Override
		public JButton getLoginButton() {
			return null;
		}
		@Override
		public void actionPerformed(ActionEvent e) {}
		@Override
		public void stationHelp(SelfCheckoutStationLogic logic) {}
		@Override
		public void noBaggingRequestInProgress(CustomerIO customer, SelfCheckoutStationLogic logic) {}
		@Override
		public void updatedAmount(SelfCheckoutStation station, BigDecimal amount) {}
		@Override
		public void insufficientChange(SelfCheckoutStation station, BigDecimal change) {}
		@Override
		public void sessionComplete(SelfCheckoutStation station) {}
		@Override
		public void requiresMaintenance(SelfCheckoutStation station, String message) {}
		@Override
		public void lowPaper(SelfCheckoutStation station, String message) {}
		@Override
		public void lowInk(SelfCheckoutStation station, String message) {}
		@Override
		public void notWaitingForItem(SelfCheckoutStation station) {}
		@Override
		public void waitingForItem(SelfCheckoutStation station) {}
		@Override
		public void weightDiscrepancyOccured(SelfCheckoutStation station) {}
		@Override
		public void weightDiscrepancyResolved(SelfCheckoutStation station) {}
		@Override
		public void scaleOverloaded(ElectronicScale scale) {}
		@Override
		public void scaleOverloadedResolved(SelfCheckoutStation station) {}
		@Override
		public void noBagsApproved(SelfCheckoutStation station) {}
		@Override
		public void updateStation(SelfCheckoutStation station) {}
		@Override
		public void ownBagsVerify(SelfCheckoutStationLogic scsLogic) {}
		@Override
		public void stationNeedsBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {}
	}

	private class CustomerIOListenerStub implements CustomerIOListener {
		private boolean knowsCustomerUsingOwnBags = false;
		private boolean knowsCustomerDonePlacingBags = false;
		private boolean hasAcceptedBags = false;
		private boolean hasRejectedBags = false;

		public void acceptOwnBags(SelfCheckoutStationLogic logic) {
//			logic.CustomerAddOwnBagsApproved();
			logic.getAddItemController().addCustomerBagApproved();
			hasAcceptedBags = true;
		}

		private void rejectOwnBags(SelfCheckoutStationLogic logic) {
			logic.CustomerAddOwnBagsDisapproved();
			hasRejectedBags = true;
		}

		@Override
		public void pressedDoneAddingOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
			knowsCustomerDonePlacingBags = true;
		}

		@Override
		public void pressedUseOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
			knowsCustomerUsingOwnBags = true;
			logic.CustomerAddOwnBagsStart();
		}

		@Override
		public void pressedPurchaseBags(CustomerIO customer, SelfCheckoutStationLogic logic, int numberOfBags) {}

		@Override
		public void stationHelp(CustomerIO customer, SelfCheckoutStationLogic logic) {}

		@Override
		public void pressedNoBaggingRequest(CustomerIO customer, SelfCheckoutStationLogic logic) {}

		@Override
		public void pressedNeedBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {}

		@Override
		public int typedMembership(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String membershipNumber) {
			return 0;
		}

		@Override
		public void startCashPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {}

		@Override
		public void setCardPaymentType(CustomerIO customer, SelfCheckoutStationLogic scsLogic, SelfCheckoutStationLogic.MethodOfPayment selectedMethod) {}

		@Override
		public void setCardPaymentAmount(CustomerIO customer, SelfCheckoutStationLogic scsLogic, BigDecimal amount) {}

		@Override
		public void returnToAddFromPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {}

		@Override
		public void attemptCardPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String cardType, BigDecimal amountCustomerWishesToPay) {}

		@Override
		public void enteredPLU(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PriceLookUpCode plu) {}

		public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, BarcodedProduct product) {}

		public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PLUCodedProduct product) {}

		@Override
		public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product) {}

		@Override
		public void selectedBarcodedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product, int quantity) {}

		@Override
		public void resetGUI(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {

		}
	}
}
