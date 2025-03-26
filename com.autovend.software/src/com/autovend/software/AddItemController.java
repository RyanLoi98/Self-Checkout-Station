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

import com.autovend.Barcode;
import com.autovend.PriceLookUpCode;
import com.autovend.SellableUnit;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.BillSlot;
import com.autovend.devices.CoinSlot;
import com.autovend.devices.ElectronicScale;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BarcodeScannerObserver;
import com.autovend.devices.observers.ElectronicScaleObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

/**
 * Controls the logic and events related to movement and registration of items.
 * Implements {@code BarcodeScannerObserver} and
 * {@code ElectronicScaleObserver}.
 *
 */
public class AddItemController implements BarcodeScannerObserver, ElectronicScaleObserver {
	/*
	 * public Barcode itemBarcode; public BarcodedProduct scannedItem; public
	 * PLUCodedProduct PLUItem;
	 */
	double expectedWeight = 0;
	boolean enableCheckWeight = true;
	boolean expectingItemInBagging = false;
	boolean hasWeightDiscrepancy = false;
	protected ArrayList<AddItemObserver> addObservers = new ArrayList<>();
	double expectedProductWeight;
	double currentBaggingWeight;
	double currentScanningWeight;

	// private SelfCheckoutStationLogic stationLogic;
	public HashMap<String, Double> baggedWeight;
	private SelfCheckoutStation station;

	public SellableUnit item;

	private BillRecord record = new BillRecord();

	/**
	 * Constructor to create addItem controller
	 *
	 * @param scs: station to install on
	 */

	public AddItemController(SelfCheckoutStation scs) {
		station = scs;

		station.mainScanner.register(this);
		station.handheldScanner.register(this);

		station.scale.register(this);
		station.baggingArea.register(this);
		baggedWeight = new HashMap<>();
	}

	/**
	 * Removes products from billRecord
	 *
	 * @param scs: product name to be removed
	 */
	public void remove(String ItemName) throws OverloadException {

		blockStation();
		double removedItemWeight = baggedWeight.get(ItemName);
		record.removeItemFromRecord(ItemName);
		expectedWeight -= removedItemWeight;

		for (AddItemObserver observer : addObservers) {
			observer.updateStation(station);
		}
		enableCheckWeight = true;
	}

	public boolean isEnableCheckWeight() {
		return enableCheckWeight;
	}

	/**
	 * Reset record after session ends
	 */
	public void resetRecord() {
		record.clearRecord();
	}

	/**
	 * Fetches the state of the bagging area.
	 *
	 * @return Whether the current weight matches the expected weight
	 */
	public boolean hasWeightDiscrepancy() {
		return hasWeightDiscrepancy;
	}

	/**
	 * Fetches the state of the system.
	 *
	 * @return Whether or not there is an item expected in the bagging area.
	 */
	public boolean expectingItemInBagging() {
		return expectingItemInBagging;
	}

	/**
	 * Fetches record of items added to current purchase.
	 *
	 * @return The record for the bill.
	 */
	public BillRecord getBillRecord() {
		return record;
	}

	/**
	 * Blocks station from user input regarding scanning, used when partial payment
	 * made.
	 */
	public void blockScanning() {
		station.mainScanner.disable();
		station.scale.disable();
		station.handheldScanner.disable();
	}

	/**
	 * unBlocks station from user input regarding scanning, used when partial
	 * payment made
	 */
	public void unBlockScanning() {
		station.mainScanner.enable();
		station.scale.enable();
		station.handheldScanner.enable();
	}

	/**
	 * Process an item that has a barcode.
	 *
	 * Initially disables the connected components to restrict user input. Fetches
	 * price, expected weight, and description of the item to update system by @code
	 * processItem.
	 *
	 *
	 * @param item - A barcoded item that is to be added to the system.
	 *
	 */
	public void startAdding(Product item) {
		if (item instanceof BarcodedProduct)
			startAdding((BarcodedProduct) item);
		else if (item instanceof PLUCodedProduct)
			startAdding((PLUCodedProduct) item);

	}

	/**
	 * Process an item that has a barcode.
	 *
	 * Initially disables the connected components to restrict user input. Fetches
	 * price, expected weight, and description of the item to update system by @code
	 * processItem.
	 *
	 *
	 * @param item - A barcoded item that is to be added to the system.
	 *
	 */
	public void startAdding(BarcodedProduct item) {
		blockStation();
		baggedWeight.put(item.getDescription(), item.getExpectedWeight());
		processAdd(item.getPrice(), item.getExpectedWeight(), item.getDescription());
	}

	/**
	 * Process a given quantity of items that have barcodes.
	 *
	 * Initially disables the connected components to restrict user input. Fetches
	 * price, expected weight, and description of the item to update system by @code
	 * processItem.
	 *
	 *
	 * @param item - A barcoded item that is to be added to the system.
	 *
	 */
	public void startAddingBarcodedItem(BarcodedProduct item, int quantity) {
		blockStation();
		double amount = quantity;
		baggedWeight.put(item.getDescription(), item.getExpectedWeight() * amount);
		// Call processAdd on the last item
		processAddBarcodedProduct(item.getPrice(), item.getExpectedWeight() * amount, item.getDescription(), quantity);
	}

	/**
	 * Takes relevant info about the barcoded product and quantity
	 *
	 * @param price       - The base price of the products
	 * @param weight      - Total weight of the products
	 * @param description - Description of the same item
	 * @param quantity    - total amount of barcoded products
	 */
	private void processAddBarcodedProduct(BigDecimal price, double weight, String description, int quantity) {
		expectedProductWeight = weight;
		baggedWeight.put(description, weight);
		record.addBarcodedItemToRecord(description, price, quantity);
		expectedWeight += weight;

		expectingItemInBagging = true;
		for (AddItemObserver observer : addObservers) {
			observer.waitingForItem(station);
		}
		enableCheckWeight = true;
	}

	/**
	 * Process an item that has a PLU Code.
	 *
	 * Initially disables the connected components to restrict user input. Fetches
	 * price, item weight, and description of the item to update system by @code
	 * processItem.
	 *
	 *
	 * @param item - A PLUCodedProduct that is to be added to the system.
	 *
	 */
	public void startAdding(PLUCodedProduct item) {
		blockStation();
		double PLUItemWeight = currentScanningWeight;
		baggedWeight.put(item.getDescription(), PLUItemWeight);

		/*
		 * PLU is zero or below scale sensitivity. The scale might not be able to react
		 * to the change in weight if this item is added.
		 */
		if (PLUItemWeight > (station.scale.getSensitivity())) {

			double PLUItemWeightInKg = PLUItemWeight / 1000;
			BigDecimal PLUItemPrice = item.getPrice().multiply(BigDecimal.valueOf(PLUItemWeightInKg));
			processAdd(PLUItemPrice, PLUItemWeight, item.getDescription());
		}
		/*
		 * } else { for (AddItemObserver observer : addObservers) {
		 * observer.notifyPLUWeightZero(scale); } }
		 */
	}

	/**
	 * Find the product with the given PLU and start adding it to the system.
	 *
	 * @param code: PLU code of item processed
	 */
	public void givenPLU(PriceLookUpCode code) {
		PLUCodedProduct PLUItem = ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
		startAdding(PLUItem);
	}

	/**
	 * Takes relevant info about the product and
	 *
	 * @param price
	 * @param weight
	 * @param description
	 */
	public void processAdd(BigDecimal price, double weight, String description) {
		expectedProductWeight = weight;
		baggedWeight.put(description, weight);
		record.addItemToRecord(description, price);
		expectedWeight += weight;

		expectingItemInBagging = true;
		for (AddItemObserver observer : addObservers) {
			observer.waitingForItem(station);
		}
		enableCheckWeight = true;
		/* Test */ System.out.println("Process Add is Finishing");
	}

	/**
	 * Add a reusable bag to the order.
	 *
	 * Initially disables the connected components to restrict user input, then
	 * update expected values And add item to record. The objective is to have
	 * virtually no difference between add bag and add item; they should make the
	 * station behave in the same way.
	 *
	 * Notify observers to place item in bagging area
	 *
	 * @param bagName:   Name of reusable bags in the item database.
	 * @param bagPrice:  Price of a single bag.
	 * @param bagWeight: Expected weight of a single bag.
	 */
	public void addBag(String bagName, BigDecimal bagPrice, double bagWeight) {
		blockStation();
		baggedWeight.put(bagName, bagWeight);
		record.addItemToRecord(bagName, bagPrice);
		expectedWeight += bagWeight;

		/*
		 * Even though this is technically NOT a barcode, the relevant observers should
		 * still receive a notification to place the item in the bagging area.
		 */
		expectingItemInBagging = true;
		for (AddItemObserver observer : addObservers) {
			observer.waitingForItem(station);
		}

	}

	/**
	 * Allows for the customer to add their own bag to the bagging area
	 *
	 * Initially disables some connected components to restrict user input other
	 * than adding a bag to the bagging area. The customer bag is treated as an item
	 * with no cost, as to not interfere with expected weight calculations
	 */
	public void addCustomerBagStart() {
		blockScanning();
		station.billInput.disable();
		station.baggingArea.disable();
		enableCheckWeight = false;

		// Notify observers that bag adding has commenced
		for (AddItemObserver observer : addObservers) {
			observer.addOwnBagsStart(station);
		}

		// Notify customer to add their bags
	}

	/**
	 * Attendant has verified that the customer appropriately added their bags to
	 * the bagging area and may continue with their purchase. The weight expected in
	 * the bagging area is updated.
	 */
	public void addCustomerBagApproved() {

		unBlockStation();

		expectedWeight = currentBaggingWeight;

		enableCheckWeight = true;

		// Notify observers that bag adding process is complete
		for (AddItemObserver observer : addObservers) {
			observer.addOwnBagsComplete(station);
		}
	}

	/**
	 * The attendant has cancelled the Customer's request to add their own bags to
	 * the bagging area.
	 *
	 * It should be noted that since the attendant cannot cancel until the weight
	 * discrepancies have been resolved, this function simply returns the station to
	 * its state from before the customer attempted to add their own bags.
	 */
	public void addCustomerBagDisapproved() {
		unBlockStation();
		enableCheckWeight = true;
		// Notify observers that bag adding process is complete
		// (albeit cancelled, the process is nonetheless concluded)
		for (AddItemObserver observer : addObservers) {
			observer.OwnBagsCancelled(station);
		}
	}

	/**
	 * Blocks station from customer interaction
	 */
	private void blockStation() {
		station.mainScanner.disable();
		station.billInput.disable();
		station.handheldScanner.disable();
		station.coinSlot.disable();
	}

	/**
	 * Unblocks station to allow customer interaction
	 */
	private void unBlockStation() {
		station.mainScanner.enable();
		station.billInput.enable();
		station.handheldScanner.enable();
		station.coinSlot.enable();
	}

	/**
	 * Check the weight on the bagging area scale and compare to expected weight. If
	 * the weight is not correct, observers are notified of a weight discrepancy.
	 * Otherwise, the
	 *
	 * @param aScale    the scale of the bagging area.
	 * @param newWeight
	 */ 
	private void checkWeight(ElectronicScale aScale, double newWeight) {
		if (enableCheckWeight && aScale == station.baggingArea) {
			double scaleSens = aScale.getSensitivity();
			// If weight is within the expected range, plus or minus the scale sensitivity.
			if ((newWeight < (expectedWeight + scaleSens)) && (newWeight > (expectedWeight - scaleSens))) {

				unBlockStation();
				
				//Only tells observers about events that are new.
				if (expectingItemInBagging) {
					expectingItemInBagging = false;
					for (AddItemObserver observer : addObservers) {
						observer.notWaitingForItem(station);
					}
				}
				if (hasWeightDiscrepancy) {
					hasWeightDiscrepancy = false;
					for (AddItemObserver observer : addObservers) {
						observer.weightDiscrepancyResolved(station);
					}
				}
			}
			// If the new weight is not equal to the expected weight, plus or minus scale
			// sensitivity.
			else {
				// blocks the self check out station , which is already blocked in start adding
				// function
				hasWeightDiscrepancy = true;
				// This is to distinguish between weight discrepancy when items added and when
				// items taken away. 
				if (newWeight > expectedWeight-expectedProductWeight) {
					expectingItemInBagging = false;
					for (AddItemObserver observer : addObservers) {
						observer.notWaitingForItem(station);
					}
				}
				// Informing the Customer and the attendant about the discrepancy
				for (AddItemObserver observer : addObservers) {
					observer.weightDiscrepancyOccured(station);
				}
			}
		}
	}

	/**
	 * Customer has selected option to not bag item. enableCheckWeight = false;
	 */
	public void customerDidNotBagTheItem() {
		System.out.println("expected old " + expectedWeight);
		expectedWeight -= expectedProductWeight;
		for (AddItemObserver observer : addObservers) {
			observer.noBagsApproved(station);
		}
		System.out.println("expected new " + expectedWeight);

		checkWeight(station.baggingArea, currentBaggingWeight);
	}

	/**
	 * Attendant has overridden the weight discrepancy, the current weight is
	 * correct.
	 */
	public void attendantApprovalForWeightDiscrepancy() {
		expectedWeight = currentBaggingWeight;
		checkWeight(station.baggingArea, currentBaggingWeight);
	}

	/**
	 * Registers AddbyScanBarcodeObserver to receive event notifications from
	 * AddbyScanBarcode logic. Used by Customer I/O to communicate with Logic.
	 *
	 * @param observer: The observer to be added.
	 */
	public void registerObserver(AddItemObserver observer) {
		addObservers.add(observer);
	}

	/**
	 * Observer calls reacting to various events
	 */

	@Override
	public void reactToBarcodeScannedEvent(BarcodeScanner barcodeScanner, Barcode barcode) {
		/* Test */ System.out.println("Observer Reacting to Barcode Scanned");
		if (!barcodeScanner.isDisabled()) {
			// Avoid attempting scan item process if item not in database (such as
			// membership cards)
			BarcodedProduct scannedItem = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
			if (scannedItem != null) {
				startAdding(scannedItem);
			}
		}
	}

	@Override
	public void reactToWeightChangedEvent(ElectronicScale aScale, double weightInGrams) {
		if (aScale == station.baggingArea) {
			currentBaggingWeight = weightInGrams;
			blockStation();
			checkWeight(aScale, weightInGrams);
		} else if (aScale == station.scale)
			currentScanningWeight = weightInGrams;

	}

	@Override
	public void reactToOverloadEvent(ElectronicScale aScale) {
		blockStation();
		for (AddItemObserver observer : addObservers) {
			observer.scaleOverloaded(aScale);
		}
	}

	@Override
	public void reactToOutOfOverloadEvent(ElectronicScale aScale) {
		unBlockStation();
		for (AddItemObserver observer : addObservers) {
			observer.scaleOverloadedResolved(station);
		}
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		if (device == station.baggingArea)
			checkWeight((ElectronicScale) device, currentBaggingWeight);

	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}


	public double getExpectedMaxWeight() {
		return expectedWeight;
	}

	public void preventStation() {
		for (AddItemObserver observer : addObservers) {
			observer.preventStation(station);
		}
		
	}
	public void permitStation() {
		for (AddItemObserver observer : addObservers) {
			observer.permitStation(station);
		}
		
	}

}
