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

import GUIComponents.AddByBrowsingPanel;
import GUIComponents.AddItemsPanel;
import GUIComponents.EnterMembershipPanel;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.CommunicationsController;
import com.autovend.software.CustomerIO;
import com.autovend.software.SelfCheckoutStationLogic;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

/**
 * Test suite for the customer IO GUI reset functions.
 */
public class CIOResetGUITest {
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

	@Before
	public void setup() {
		CAD = Currency.getInstance(Locale.CANADA);
		billDenominations = new int[] {5, 10, 15, 20, 50, 100};
		coinDenominations = new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"),
				new BigDecimal("0.25"), new BigDecimal(1), new BigDecimal(2)};
		maxWeight = 25;
		sensitivity = 1;

		station = new SelfCheckoutStation(CAD, billDenominations, coinDenominations, maxWeight, sensitivity);

		PriceLookUpCode PLUCode1 = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four);
		PLUCodedProduct product4 = new PLUCodedProduct(PLUCode1, "Apple", new BigDecimal("20"));
		PriceLookUpCodedUnit unit4 = new PriceLookUpCodedUnit(PLUCode1, 5);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode1, product4);

		memLen = 5;
		memFilename = "membershipdatabase.txt";
		logic = new SelfCheckoutStationLogic(station, null, null, memLen, memFilename, 10, 10);

		cIO = new CustomerIO(logic);
	}

	@After
	public void teardown() {
		station = null;
		logic = null;
		cIO = null;
	}

	/**
	 * Test to confirm that the welcome screen is successfully displayed
	 */
	@Test public void testWelcomeScreen() {
		// show a panel other than the welcome panel to confirm that it transitions as expected
		cIO.setShownPanel("Adding");
		Assert.assertTrue("Add items panel should be visible initially",
				cIO.getAddItemsPanel().isVisible());
		cIO.revertToWelcome();
		Assert.assertFalse("Add items panel should no longer be visible",
				cIO.getAddItemsPanel().isVisible());
		Assert.assertTrue("Welcome panel should now be visible",
				cIO.getWelcomePanel().isVisible());
	}

	/**
	 * Test to confirm that the disabled screen is successfully displayed
	 */
	@Test public void testDisabledScreen() {
		Assert.assertTrue("Welcome panel should be visible initially",
				cIO.getWelcomePanel().isVisible());
		cIO.disableIO();
		Assert.assertTrue("Disabled panel should now be visible",
				cIO.getDisabledPanel().isVisible());
	}

	// tests to confirm that resetGUI clears all necessary fields/selections
	@Test public void testAddByBrowseSelection() {
		cIO.setShownPanel("AddByBrowsing");
		AddByBrowsingPanel testedPanel = cIO.getAddByBrowsingPanel();
		testedPanel.getSelection().setSelectedIndex(0);
		Assert.assertFalse("Add by browsing item selection should not be empty yet",
				testedPanel.getSelection().isSelectionEmpty());
		Assert.assertTrue("Add by browsing panel should now be visible",
				testedPanel.getAddByBrowsingPanel().isVisible());
		cIO.revertToWelcome();
		cIO.setShownPanel("AddByBrowsing");
		Assert.assertTrue("Add by browsing item selection should be empty",
				testedPanel.getSelection().isSelectionEmpty());
	}

	@Test public void testEnterMembershipField() {
		cIO.setShownPanel("EnterMembership");
		EnterMembershipPanel testedPanel = cIO.getEnterMembershipPanel();
		testedPanel.getMembershipNumber().setText("some input");
		Assert.assertTrue("Enter membership panel should now be visible",
				testedPanel.isVisible());
		cIO.revertToWelcome();
		cIO.setShownPanel("EnterMembership");
		Assert.assertEquals("Enter membership field should be an empty string",
				"", testedPanel.getMembershipMessage());
	}

	// TODO: tests that confirm that resetGUI closes all necessary popups/dialogs
}
