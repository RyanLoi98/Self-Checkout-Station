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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Bill;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.devices.DisabledException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.SelfCheckoutStationLogic;

/**
 * Test Suite for add Item Class
 *
 */
public class AddItemTest {
	SelfCheckoutStation scs;
	Currency c1 = Currency.getInstance(Locale.CANADA);
	Currency c2 = Currency.getInstance(Locale.ITALY);
	int[] billdenominations = { 5, 10, 15, 20, 50 };
	BigDecimal[] coindenominations = { new BigDecimal("1") };

	Barcode barcodeMilk = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
	BarcodedProduct milkBarcodedProduct = new BarcodedProduct(barcodeMilk, "Milk", new BigDecimal("20"), 2.5);
	BarcodedUnit milkWeightCorrect = new BarcodedUnit(barcodeMilk, 2.5);
	BarcodedUnit milkWeighsDouble = new BarcodedUnit(barcodeMilk, 5);

	Barcode barcodeEggs = new Barcode(Numeral.nine, Numeral.one, Numeral.two, Numeral.three);
	BarcodedProduct eggsBarcodedProduct = new BarcodedProduct(barcodeEggs, "Eggs", new BigDecimal("5"), 2.5);
	BarcodedUnit eggsWeightCorrect = new BarcodedUnit(barcodeEggs, 2.5);

	Barcode barcodeCheese = new Barcode(Numeral.nine, Numeral.one, Numeral.two, Numeral.three);
	BarcodedProduct cheeseBarcodedProduct = new BarcodedProduct(barcodeCheese, "Cheese", new BigDecimal("20"), 3);
	BarcodedUnit cheeseUnitWeightDif_5Tenths = new BarcodedUnit(barcodeCheese, 2.5);
	BarcodedUnit cheeseUnitWeightCorrect = new BarcodedUnit(barcodeCheese, 3);
	BarcodedUnit cheeseUnitWeightDif_3 = new BarcodedUnit(barcodeCheese, 6);

	PriceLookUpCode pluApple = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four);
	PLUCodedProduct applePLUProduct = new PLUCodedProduct(pluApple, "Apple", new BigDecimal("20"));
	PriceLookUpCodedUnit appleWeighsFive = new PriceLookUpCodedUnit(pluApple, 5);

	PriceLookUpCode pluOrange = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.five);
	PLUCodedProduct orangePLUProduct = new PLUCodedProduct(pluOrange, "Orange", new BigDecimal("20"));
	PriceLookUpCodedUnit orangeWeighsTen = new PriceLookUpCodedUnit(pluOrange, 10);
	PriceLookUpCodedUnit orangeWeighsThree = new PriceLookUpCodedUnit(pluOrange, 3);

	Bill bill5 = new Bill(5, c1);
	Bill bill20 = new Bill(20, c1);
	Bill bill50 = new Bill(50, c1);
	Bill bill5DiffCurrency = new Bill(5, c2);

	AttendantStub attendantStub;
	CustomerStub customerStub;
	SelfCheckoutStationLogic logic;

	BarcodedUnit demoBag;

	// Setup: AttendantStub and CustomerStub are created, and two products are added
	// to the product database.
	// SelfCheckoutStation object is created.
	@Before
	public void Setup() {
		attendantStub = new AttendantStub();
		customerStub = new CustomerStub();
		scs = new SelfCheckoutStation(c1, billdenominations, coindenominations, 20, 1);
		logic = new SelfCheckoutStationLogic(scs, null, null, 0, null, 10, 10);
		registerStubs(logic);

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodeMilk, milkBarcodedProduct);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodeEggs, eggsBarcodedProduct);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcodeCheese, cheeseBarcodedProduct);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluApple, applePLUProduct);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluOrange, orangePLUProduct);

		// Create a demo bag to represent the weight increase on the scale
		Barcode demoBarcode = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
		demoBag = new BarcodedUnit(demoBarcode, 20.00);
	}

	// Registers customerStub to PayObserver, PrintObserver, AddItemObserver
	// Registers attendantStub to PrintObserver
	private void registerStubs(SelfCheckoutStationLogic scsl) {
		scsl.registerPayCashObserver(customerStub);
		// scsl.registerPayCardObserver(customerStub);
		scsl.registerPrintObserver(customerStub);
		scsl.registerAddItemObserver(customerStub);

		scsl.registerPrintObserver(attendantStub);

	}

	// *******CHECK SCANNED/BARCODED ITEM CONTROL************//

	/**
	 * Note, the assert depends on if the item was successfully scanned or not,
	 * Since if the scan failed, them customer would not be asked to place it.
	 */

	@Test
	public void addItem_NotifyCustomerToPlace_ifScanned() throws OverloadException {
		scs.printer.addInk(50);
		scs.printer.addPaper(50);
		boolean scanned = scs.mainScanner.scan(milkWeightCorrect);
		assertEquals(customerStub.isWaitingItemFlag(), scanned);
	}

	/*
	 * Item scanned in, but not added to bagging area. Customer should be blocked
	 * from interacting with anything other than the scale. Another attempt to scan
	 * should throw disabled exception.
	 * 
	 */
	@Test(expected = DisabledException.class)
	public void addItem_stationBlockedScanItem_disabledException() throws OverloadException {
		boolean scanned = scs.mainScanner.scan(milkWeightCorrect);
		while (scanned != true) { // Until it scans
			scanned = scs.mainScanner.scan(milkWeightCorrect);
		}
		assertTrue(scs.mainScanner.isDisabled());
		assertTrue(scs.handheldScanner.isDisabled());
		assertTrue(scs.coinSlot.isDisabled());
		assertTrue(scs.billInput.isDisabled());
		assertFalse(scs.baggingArea.isDisabled());
		scs.mainScanner.scan(eggsWeightCorrect);
	}

	/*
	 * Item scanned in, but not added to bagging area. Customer should be blocked
	 * from interacting with anything other than the scale. An attempt to scan
	 * should through disabled exception.
	 * 
	 */
	@Test(expected = DisabledException.class)
	public void addItem_stationBlockedAddMoney_disabledException() throws DisabledException, OverloadException {
		boolean scanned = scs.mainScanner.scan(milkWeightCorrect);
		while (scanned != true) { // Until it scans
			scanned = scs.mainScanner.scan(milkWeightCorrect);
		}
		assertTrue(scs.mainScanner.isDisabled());
		assertTrue(scs.handheldScanner.isDisabled());
		assertTrue(scs.coinSlot.isDisabled());
		assertTrue(scs.billInput.isDisabled());
		assertFalse(scs.baggingArea.isDisabled());
		scs.billInput.accept(bill50);
	}

	/**
	 * Item scanned with an expected weight of 3, but an actual weight of 6.
	 */
	@Test
	public void testAddBarcodeProduct_WeightDifference_OverScaleSensitivity() {
		boolean scanned = scs.mainScanner.scan(milkWeightCorrect);
		while (scanned != true) { // Until it scans
			scanned = scs.mainScanner.scan(milkWeightCorrect);
		}
		assertTrue("Should be waiting for item to be bagged", customerStub.isWaitingItemFlag());
		scs.baggingArea.add(milkWeightCorrect);

		assertFalse("There should not be a weight discrepancy", customerStub.isWeightDiscrepancy());

		boolean scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_3);
		while (scanned2 != true) { // Until it scans
			scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_3);
		}
		scs.baggingArea.add(cheeseUnitWeightDif_3);

		assertTrue("There should be a weight discrepancy", customerStub.isWeightDiscrepancy());
	}

	/**
	 * Item scanned with an expected weight of 3, but an actual weight of 2.5.
	 */
	@Test
	public void testAddBarcodeProduct_WeightDifference_UnderScaleSensitivity() {
		assertFalse("There should not be a weight discrepancy before any items added",
				customerStub.isWeightDiscrepancy());

		boolean scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_5Tenths);
		while (scanned2 != true) { // Until it scans
			scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_5Tenths);
		}
		assertTrue("The system should be expecting the item", customerStub.isWaitingItemFlag());
		scs.baggingArea.add(cheeseUnitWeightDif_5Tenths);

		assertFalse("There should not be a weight discrepancy", customerStub.isWeightDiscrepancy());
		assertFalse("The system should not be waiting for an item", customerStub.isWaitingItemFlag());
	}

	/**
	 * Item scanned with an expected weight of 3, but an actual weight of 4
	 */
	@Test
	public void testAddBarcodeProduct_WeightDifference_EqualsScaleSensitivity() {
		BarcodedUnit cheeseUnitWeightDif_OneGram = new BarcodedUnit(barcodeCheese, 4);
		assertFalse("There should not be a weight discrepancy before any items added",
				customerStub.isWeightDiscrepancy());

		boolean scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_OneGram);
		while (scanned2 != true) { // Until it scans
			scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_OneGram);
		}
		assertTrue("The system should be expecting the item", customerStub.isWaitingItemFlag());
		scs.baggingArea.add(cheeseUnitWeightDif_OneGram);

		assertTrue("There should be a weight discrepancy detected", customerStub.isWeightDiscrepancy());
		assertFalse("The system should not be waiting for an item", customerStub.isWaitingItemFlag());
	}

	/**
	 * Barcoded product added to bagging with weight bigger than scale limit.
	 */
	@Test
	public void additemtoBagging_andOverloadScale() {
		Barcode bigBarcode = new Barcode(Numeral.seven, Numeral.one, Numeral.four, Numeral.three);
		BarcodedProduct bigBarcodedProduct = new BarcodedProduct(bigBarcode, "chicken", new BigDecimal("1"),
				scs.baggingArea.getWeightLimit());
		BarcodedUnit bigBarcodedUnit = new BarcodedUnit(bigBarcode, scs.baggingArea.getWeightLimit());

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(bigBarcode, bigBarcodedProduct);

		boolean scannedb = scs.mainScanner.scan(bigBarcodedUnit);
		while (scannedb != true) { // Until it scans
			scannedb = scs.mainScanner.scan(bigBarcodedUnit);
		}

		assertFalse("Scale should not be overloaded when empty", customerStub.isScaleOverloaded());
		scs.baggingArea.add(bigBarcodedUnit);
		assertFalse("Scale should be at max weight", customerStub.isScaleOverloaded());

		boolean scanned = scs.mainScanner.scan(milkWeightCorrect);
		while (scanned != true) { // Until it scans
			scanned = scs.mainScanner.scan(milkWeightCorrect);
		}
		scs.baggingArea.add(milkWeightCorrect);
		assertTrue("Scale should be above max weight", customerStub.isScaleOverloaded());

		scs.baggingArea.remove(milkWeightCorrect);
		assertFalse("Scale should be at max weight", customerStub.isScaleOverloaded());

	}

	/**
	 * PLU product added to scanning area with weight bigger than scale limit.
	 */
	@Test
	public void additemtoScanning_andOverloadScale() {
		PriceLookUpCode bigPLU = new PriceLookUpCode(Numeral.seven, Numeral.one, Numeral.four, Numeral.three);
		PriceLookUpCodedUnit bigPLUUnit = new PriceLookUpCodedUnit(bigPLU, scs.scale.getWeightLimit());

		assertFalse("Scale should not be overloaded when empty", customerStub.isScaleOverloaded());
		scs.scale.add(bigPLUUnit);
		assertFalse("Scale should be at max weight", customerStub.isScaleOverloaded());

		scs.scale.add(appleWeighsFive);
		assertTrue("Scale should be above max weight", customerStub.isScaleOverloaded());

		scs.scale.remove(appleWeighsFive);
		assertFalse("Scale should be at max weight", customerStub.isScaleOverloaded());

	}

	/**
	 * Item scanned, added to bagging area and then removed from bagging area
	 * without requesting it from the system.
	 */

	@Test
	public void additem_itemRemoved_WeightDiscrepancy() {
		boolean scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_5Tenths);
		while (scanned2 != true) { // Until it scans
			scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_5Tenths);
		}
		scs.baggingArea.add(cheeseUnitWeightDif_5Tenths);

		scs.baggingArea.remove(cheeseUnitWeightDif_5Tenths);

		assertTrue("There should be a weight discrepancy", customerStub.isWeightDiscrepancy());
	}

	/**
	 * Barcoded Item added to bagging area without scanning. Weight Discrepancy
	 * should be resolved by removing the item.
	 */

	@Test
	public void additem_itemNotExpected_WeightDiscrepancy() {
		scs.baggingArea.add(milkWeightCorrect);
		assertTrue("There should be a weight discrepancy", customerStub.isWeightDiscrepancy());

		scs.baggingArea.remove(milkWeightCorrect);
		assertFalse("Weight discrepancy should be resolved by removing the item", customerStub.isWeightDiscrepancy());

	}

	/**
	 * Milk is scanned, but is not added to bagging area. Instead, Customer
	 * presumably requested not to bag the heavy milk, and attendant has approved
	 * this request.
	 */
	@Test
	public void waitingItem_AttendantApprovesNoBagging() throws OverloadException {
		boolean scanned = scs.mainScanner.scan(milkWeightCorrect);
		while (scanned != true) { // Until it scans
			scanned = scs.mainScanner.scan(milkWeightCorrect);
		}
		assertFalse("There should not be a weight discrepancy before item is added",
				customerStub.isWeightDiscrepancy());
		assertTrue("Should be waiting for item to be bagged", customerStub.isWaitingItemFlag());

		logic.noBaggingRequestApproved();

		assertFalse("There should not be a weight discrepancy", customerStub.isWeightDiscrepancy());
		assertFalse("Should not be waiting for item to be bagged after attendant approves no bagging request",
				customerStub.isWaitingItemFlag());
	}

	/**
	 * Cheese is scanned. Its expected weight is 3, but the unit weight is 6. A
	 * weight discrepancy should be resolved by the attendant's action "weight
	 * discrepancy approval"
	 */
	@Test
	public void weightDiscrepancy_AttendantApprovesWeight() throws OverloadException {
		boolean scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_3);
		while (scanned2 != true) { // Until it scans
			scanned2 = scs.mainScanner.scan(cheeseUnitWeightDif_3);
		}
		scs.baggingArea.add(cheeseUnitWeightDif_3);

		assertTrue("Should be a weight discrepancy before attendant approves weight",
				customerStub.isWeightDiscrepancy());

		logic.weightDiscprepancyApproval();
		assertFalse("Should not have a weight discrepancy after attendant approves weight",
				customerStub.isWeightDiscrepancy());

	}
	
	/**
	 * TODO: Awaiting method that calls remove item in SelfCheckoutStationLogic
	 * 
	 * Cheese is added to the bagging area without first listing it in the system. A
	 * weight discrepancy should occur, and be resolved by the customer removing the item.
	 */
	@Test
	public void weightDiscrepancy_CustomerRemovesExtraItem() throws OverloadException {
		scs.baggingArea.add(cheeseUnitWeightCorrect);

		assertTrue("Should be a weight discrepancy before attendant approves weight",
				customerStub.isWeightDiscrepancy());

		//logic.remove(cheeseBarcodedProduct);
		assertFalse("Should not have a weight discrepancy after removing the extra weight",
				customerStub.isWeightDiscrepancy());

	}

	/**
	 * Apple added to scale, the logic is given the apple PLU, and the apple bagged.
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void addItemByPLUCode() throws OverloadException {
		scs.scale.add(appleWeighsFive);
		logic.receiveCustomerInputPLU(pluApple);
		scs.baggingArea.add(appleWeighsFive);

		assertFalse("Should not be waiting for item after it is added to bagging area",
				customerStub.isWaitingItemFlag());
		assertFalse("Should not have a weight discrepancy", customerStub.isWaitingItemFlag());
	}

	/**
	 * Apple added to scale, logic is given apple PLU, and an apple with a weight 5
	 * grams higher is bagged.
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void addItemByPLUCode_baggedItemTooHeavy_WeightDiscrepancy() throws OverloadException {
		scs.scale.add(appleWeighsFive);
		logic.receiveCustomerInputPLU(pluApple);
		scs.baggingArea.add(orangeWeighsTen);
		assertEquals(customerStub.isWeightDiscrepancy(), true);
	}

	/**
	 * Logic was passed the product "Milk". This may be from the Attendant's text
	 * search or a browser search.
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void addItemNormal() throws OverloadException {
		logic.itemPicked(milkBarcodedProduct);
		assertTrue("Should be waiting for item selected by IO", customerStub.isWaitingItemFlag());
		scs.baggingArea.add(milkWeightCorrect);
		assertFalse("Should not have a weight discrepancy", customerStub.isWeightDiscrepancy());
	}

	/**
	 * 
	 * Barcoded passed to logic from GUI, presumably from browsing or text search
	 * selection. The item expected weight is 3, and actual weight is 6.
	 * 
	 * @throws OverloadException
	 */

	@Test
	public void addItemByBrowsing_and_weight_discrepancy_occured() throws OverloadException {
		logic.itemPicked(milkBarcodedProduct);
		BarcodedUnit unit6 = new BarcodedUnit(barcodeMilk, 6);
		scs.baggingArea.add(unit6);

		assertTrue("Should have notified weight discrepancy", customerStub.isWeightDiscrepancy());
		assertTrue("Scanner should be disabled", scs.mainScanner.isDisabled());
	}

	/**
	 * Orange (PLU) is added to scale in scanning area. Orange is then passed to
	 * logic from GUI, presumably from browsing or text search selection. The item
	 * expected weight is 10g, and actual weight is 10g.
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void addItemBySearch_PLU() throws OverloadException {
		scs.scale.add(orangeWeighsTen);
		logic.itemPicked(orangePLUProduct);
		assertTrue("System should be expecting item in bagging area", customerStub.isWaitingItemFlag());

		scs.baggingArea.add(orangeWeighsTen);

		assertFalse("Should have item placed", customerStub.isWaitingItemFlag());
		assertFalse("Should have no weight discrepancy", customerStub.isWeightDiscrepancy());
	}

	/**
	 * Milk (barcoded product)is passed to logic from GUI, presumably from browsing
	 * or text search selection. The item expected weight is 10g, and actual weight
	 * is 10g.
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void addItemBySearch_Barcoded() throws OverloadException {
		logic.itemPicked(milkBarcodedProduct);
		assertTrue("System should be expecting item in bagging area", customerStub.isWaitingItemFlag());

		scs.baggingArea.add(milkWeightCorrect);

		assertFalse("Should have item placed", customerStub.isWaitingItemFlag());
		assertFalse("Should have no weight discrepancy", customerStub.isWeightDiscrepancy());

	}

	/**
	 * Barcoded Product Milk x3 is passed to logic from GUI, presumably from browsing
	 * or text search selection. The item expected weight is 10g, and actual weight
	 * is 10g.
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void addMultipleItemBySearch_Barcoded() throws OverloadException {
		BarcodedUnit milkWeightCorrect2 = new BarcodedUnit(barcodeMilk, 2.5);
		BarcodedUnit milkWeightCorrect3 = new BarcodedUnit(barcodeMilk, 2.5);
		logic.barcodedItemPicked(milkBarcodedProduct,3);
		Integer numMilk = 3;
		assertEquals(numMilk,logic.getBillRecord().getItemQuantity("Milk"));
		
		assertTrue("System should be expecting items in bagging area", customerStub.isWaitingItemFlag());
		scs.baggingArea.add(milkWeightCorrect);
		scs.baggingArea.add(milkWeightCorrect2);
		scs.baggingArea.add(milkWeightCorrect3);

		assertFalse("Should have all items placed", customerStub.isWaitingItemFlag());
		assertFalse("Should have no weight discrepancy", customerStub.isWeightDiscrepancy());

	}
	/**
	 * Barcoded product "Milk" is passed to logic, presumably from GUI. Double that weight is then added to the bagging area.
	 * 
	 * @throws OverloadException
	 */

	@Test
	public void addItemBySearch_BarcodeItem_weightDiscrepancy() throws OverloadException {
		logic.itemPicked(milkBarcodedProduct);
		assertTrue("System should be expecting item in bagging area", customerStub.isWaitingItemFlag());

		scs.baggingArea.add(milkWeighsDouble);

		assertFalse("Should have placed item", customerStub.isWaitingItemFlag());
		assertTrue("Should have a weight discrepancy", customerStub.isWeightDiscrepancy());

	}

	/**
	 * Orange (PLU product) added to scale and has a weight of 10g. Orange PLU is
	 * passed to logic, presumably from GUI. An orange with a weight of 3g is then
	 * added to the bagging area.
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void addItemBySearch_PLUitem_weightDiscrepancy() throws OverloadException {
		scs.scale.add(orangeWeighsTen);
		logic.itemPicked(orangePLUProduct);
		assertTrue("System should be expecting item in bagging area", customerStub.isWaitingItemFlag());

		scs.baggingArea.add(orangeWeighsThree);

		assertFalse("Should have placed item", customerStub.isWaitingItemFlag());
		assertTrue("Should have weight discrepancy", customerStub.isWeightDiscrepancy());
	}

	/**
	 * Tests basic functionality of adding own bags, assuming attendant approves
	 * bags
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void testBagsApproved() throws OverloadException {
		logic.CustomerAddOwnBagsStart();
		assertEquals(true, customerStub.isBagsStarted());
		scs.baggingArea.add(demoBag);
		logic.CustomerAddOwnBagsApproved();
		assertEquals(true, customerStub.isBagsComplete());
		assertEquals(20.00, scs.baggingArea.getCurrentWeight(), 0.001);
		assertEquals(false, attendantStub.isDiscrepnancyOccured());
	}

	/**
	 * Tests basic functionality of adding own bags, assuming attendant does not
	 * approve bags
	 * 
	 * @throws OverloadException
	 */
	@Test
	public void testBagsDisapproved() throws OverloadException {
		logic.CustomerAddOwnBagsStart();
		assertEquals(true, customerStub.isBagsStarted());

		scs.baggingArea.add(demoBag);
		logic.CustomerAddOwnBagsDisapproved();
		assertEquals(true, customerStub.isBagsCancelled());
		assertEquals(false, customerStub.isBagsComplete());

		assertEquals(false, attendantStub.isDiscrepnancyOccured());

	}
	
	@Test
	public void testUnblockStation() {
		logic.CustomerPay();
		assertTrue("Handheld scanner should be disabled", scs.handheldScanner.isDisabled());
		assertTrue("Main scanner should be disabled", scs.mainScanner.isDisabled());
	
		logic.partialPayment();
		assertFalse("Handheld scanner should be disabled", scs.handheldScanner.isDisabled());
		assertFalse("Main scanner should be disabled", scs.mainScanner.isDisabled());
		
	}
	
	@Test
	public void testAddBags() {
		String bagName = "Charlie";
		BigDecimal bagCost = new BigDecimal("20");
		logic.addBag(bagName, bagCost, 3.0);
		Integer numBags = 1;
		
		assertEquals(numBags,logic.getBillRecord().getItemQuantity(bagName));
	}

}