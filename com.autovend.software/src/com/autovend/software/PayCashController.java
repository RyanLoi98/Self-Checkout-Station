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

//boom boom
package com.autovend.software;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.BillSlot;
import com.autovend.devices.BillStorage;
import com.autovend.devices.BillValidator;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.CoinStorage;
import com.autovend.devices.CoinValidator;
import com.autovend.devices.DisabledException;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BillSlotObserver;
import com.autovend.devices.observers.BillStorageObserver;
import com.autovend.devices.observers.BillValidatorObserver;
import com.autovend.devices.observers.CoinDispenserObserver;
import com.autovend.devices.observers.CoinValidatorObserver;


import com.autovend.devices.observers.CoinStorageObserver;


/**
 * PayCash Functionality Controller
 *
 */
public class PayCashController implements BillValidatorObserver, BillStorageObserver, CoinValidatorObserver, BillSlotObserver, CoinDispenserObserver{
	
	private SelfCheckoutStation station;
	private Currency current_Currency;
	private BigDecimal currentBill_value;
	private BigDecimal currentCoin_value;	// change to big decimal
	private List<BigDecimal> coinDenominations;
	private int[] billDenominations;
	private List<Integer> billsToDispense;

	
	private ArrayList<PayCashObserver> observers = new ArrayList<>();

	private Bill bill;
	
	private BigDecimal totalBill;		//change to big decimal
	private BigDecimal totalBillPaid = BigDecimal.valueOf(0.0); //change to big decimal
		
	private PrintReceiptController receipt;
	
	private BillRecord billRecord;
	
	private Map<Integer, BillDispenser> BillDispensers;
	private Map<BigDecimal, CoinDispenser> coinDispensers;



	/**
	 * Initiates PayCash controller
	 * @param scs: station which it is installed on
	 * @param printer: printReceipt Controller, which it notifies to call.
	 */
	public PayCashController (SelfCheckoutStation scs, PrintReceiptController printer) {
		scs.billValidator.register(this);
		scs.billStorage.register(this);
		scs.billOutput.register(this);
		scs.coinValidator.register(this);
		
		BillDispensers = scs.billDispensers;
		coinDispensers = scs.coinDispensers;
		billDenominations = scs.billDenominations;
		coinDenominations = scs.coinDenominations;
		
		for (BigDecimal disp : coinDispensers.keySet()) {
			coinDispensers.get(disp).register(this);;
		}
		
		Arrays.sort(billDenominations);
		Collections.sort(coinDenominations);
		
		receipt = printer;

		station = scs;
	}
	
//	/**
//	 * Returns total bill paid
//	 */
//	public BigDecimal getTotalBillPaid() {	//change to big decimal
//		return totalBillPaid;
//	}
	/**
	 * Main Logic tells PayCash that the Customer wishes to pay
	 * @param record: Bill record of customers items
	 */
	public void startPay(BillRecord record) {
		billRecord = record;
//		if(cardPaid.compareTo(totalBillPaid) > 0) {
//			totalBillPaid = cardPaid;
//		}
	}
	/**
	 * Session has ended, so PayCash can reset its totals and counts
	 */
	public void sessionEnd() {
		totalBillPaid = BigDecimal.valueOf(0.0);
		totalBill = BigDecimal.valueOf(0.0);
	}
	
	/**
	 * Blocks station from user input regarding payment, used when partial payment made
	 */
	public void blockPayment() {
		station.billInput.disable();
		station.coinSlot.disable();
	}
	
	/**
	 * unBlocks station from user input regarding
	 */
	public void unBlockPayment() {
		station.billInput.enable();
		station.coinSlot.enable();
	}
	
	
	/**
	 * Once a bill or coin has been validated and stored, it is taken and used in customer Transactions
	 * @param value: Value of bill or coin inserted/Stored
	 */
	private void takeMoney(BigDecimal value){	//change param to big decimal
		totalBill = billRecord.getTotal();
		billRecord.addPayment(value);
		BigDecimal totalBillLeft = billRecord.getTotalDue();
		
//		totalBillPaid = totalBillPaid.add(value);
//		BigDecimal totalBillLeft = totalBill.subtract(totalBillPaid);
		
		for(PayCashObserver observer : observers) {
			if (totalBillLeft.compareTo(BigDecimal.valueOf(0.0)) < 0) {
				observer.updatedAmount(station, BigDecimal.valueOf(0.0));
			}else {
				observer.updatedAmount(station, totalBillLeft);
			}
		}
		if (totalBillLeft.compareTo(BigDecimal.valueOf(0.0)) == 0) {
			receipt.startPrint(billRecord, totalBill, BigDecimal.valueOf(0.0));		
		} else if (totalBillLeft.compareTo(BigDecimal.valueOf(0.0)) < 0) {
			billsToDispense = new ArrayList<>();
			if(makeChange(totalBillLeft)) {
				dispenseBill();
			}
		}
	}
	
	/**
	 * Dispenses Bill one at a time, after the previous bill was removed by customer
	 */
	private void dispenseBill() {
		if (billsToDispense.size() > 0) {			
			int denomination = billsToDispense.get(0);
			BillDispenser billdispenser = BillDispensers.get(denomination);
			try {
				billdispenser.emit();
			} catch (DisabledException | EmptyException | OverloadException e) {
					//error in dispensing, should never occur
			}
			billsToDispense.remove(0);
		}else {
			receipt.startPrint(billRecord, totalBill, BigDecimal.valueOf(0.0).subtract(billRecord.getTotalDue()));								
		}
		
		if (billsToDispense.size() == 0) { //If we only had one bill to dispense then move on
			receipt.startPrint(billRecord, totalBill, BigDecimal.valueOf(0.0).subtract(billRecord.getTotalDue()));								
		}
	}
	/**
	 * Calculate and return change. 
	 * @param totalDue: total amount due, which should be negative
	 */
	private boolean makeChange(BigDecimal totalDue) {
		BigDecimal changeDue = totalDue.abs();
		boolean success = true;
		while(changeDue.compareTo(BigDecimal.valueOf(0.0)) != 0) {
			BigDecimal initialChange = changeDue; 
			for (int i = billDenominations.length -1; i >= 0; i--) {
				int bDenom = billDenominations[i];
				if (changeDue.compareTo(BigDecimal.valueOf(bDenom)) >= 0) {
					System.out.println("change" + changeDue);
					System.out.println("bill" + bDenom);
					int billCount = changeDue.divide(BigDecimal.valueOf(bDenom), MathContext.DECIMAL128).intValue();
					int billsdispensed = BillsAvailable(bDenom, billCount);
					changeDue = changeDue.subtract(BigDecimal.valueOf(billsdispensed * bDenom));
				}				
			}
			if (changeDue.compareTo(BigDecimal.valueOf(0.0)) > 0) {
				for (int i = coinDenominations.size() - 1; i >= 0; i--) {
					Double cDenom = coinDenominations.get(i).doubleValue();
					if (changeDue.compareTo(BigDecimal.valueOf(cDenom)) >= 0) {
						int coinCount = changeDue.divide(BigDecimal.valueOf(cDenom)).intValue();
						int CoinDispensed = dispenseCoin(cDenom, coinCount);
						changeDue = changeDue.subtract(BigDecimal.valueOf(CoinDispensed * cDenom));	
					}				
				}
			}
			if (initialChange.compareTo(changeDue) == 0) {
				for(PayCashObserver observer : observers) {
					System.out.println("reaches");
					observer.insufficientChange(station, changeDue);
				}
				suspendStation();
				success = false;
				break;
			}
		}
		return success;		
	}
	
    /**
     * Suspends station from customer interaction, waiting for maintaince
     */
    private void suspendStation() {
    	station.handheldScanner.disable();
    	station.mainScanner.disable();
    	station.billInput.disable();
    	station.baggingArea.disable();
    	station.coinSlot.disable();
    	station.scale.disable();
	}
	/**
	 * Dispense bill from given denom
	 * @param denom
	 * @return number of bills dispensed
	 */
	/**
	 * WORK IN PROGRESS
	 * @param denomination
	 * @param quantity
	 * @return
	 */
	private int BillsAvailable(int denomination, int quantity) {
		BillDispenser billdispenser = BillDispensers.get(denomination);
		int billsLeft = billdispenser.size();
		if (billsLeft < quantity){
			for (int i = 0; i < billsLeft; i++){
				billsToDispense.add(denomination);
			}
			return billsLeft;
		}
		else{
			for (int i = 0; i < quantity; i++){
				billsToDispense.add(denomination);
				}
			return quantity;
		}
	}
	
	/**
	 * Dispense coin from given denom
	 * @param denomination
	 * @return number of coin dispensed
	 */
	private int dispenseCoin(double denomination, int coinsToDispense) {	//chnage to big decimal
		BigDecimal bDecimal = new BigDecimal(denomination);
		CoinDispenser coinDispenser = coinDispensers.get(bDecimal);
		int coinsLeft = coinDispenser.size();
		if (coinsLeft < coinsToDispense){
			for (int i = 0; i < coinsLeft; i++){
				try {
					coinDispenser.emit();
				} catch (DisabledException | OverloadException | EmptyException e1) {
					//should never occur 
				}
			}
			return coinsLeft;
		}
		else{
			for (int i = 0; i < coinsToDispense; i++){
				try {
					coinDispenser.emit();
				} catch (DisabledException | OverloadException | EmptyException e1) {
					//should never occur 
				}
			}
			return coinsToDispense;
		}
	}
	
	/**
	 * Calculates total of all items in Bill Record
	 * @param record: calculates bill record total
	 * @return: total cost
	 */
	private BigDecimal CalcTotal(BillRecord record) {
		BigDecimal total = BigDecimal.valueOf(0.0);
		Set<String> itemSet = record.getItems();
		for (String itemName : itemSet) {
			total = total.add(record.getItemCost(itemName).multiply(BigDecimal.valueOf(record.getItemQuantity(itemName))));
		}
		
		return total;
	}
	
	/**
	 * Registers payCashObserver to receive event notifications from PrintReceipt logic.
	 * Used by Customer I/O to communicate with Logic.
	 * 
	 * @param observer: The observer to be added.
	 */
	public void registerObserver(PayCashObserver observer) {
		observers.add(observer);
	}

	/**
	 * Called when bill has been stored and PayCash notified
	 */
	private void BillStored() {
		takeMoney(currentBill_value);
	}

	/**
	 * Called when coin has been stored and PayCash notified
	 */
	private void CoinStored() {
		takeMoney(currentCoin_value);
	}
	/**
	 * Observer to see if inserted bill was rejected
	 */
	@Override
	public void reactToBillsFullEvent(BillStorage unit) {
		//currentBill_value = 0;
		//current_Currency = null;
}
	/**
	 * Observer to see if inserted bill was accepted
	 */
	@Override
	public void reactToBillAddedEvent(BillStorage unit) {
		BillStored();
	}
	/**
	 * Observer to receive details of inserted bill
	 */
	@Override
	public void reactToValidBillDetectedEvent(BillValidator validator, Currency currency, int value) {
		current_Currency = currency;
		currentBill_value = BigDecimal.valueOf(value);
	}
	/**
	 * Observer to receive details of inserted coin
	 */
	@Override
	public void reactToValidCoinDetectedEvent(CoinValidator validator, BigDecimal value) {
		currentCoin_value = value;
	}
	/**
	 * Observer to see if inserted coin was accepted
	 */
	@Override
	public void reactToCoinAddedEvent(CoinDispenser dispenser, Coin coin) {
		CoinStored();
	}
	/**
	 * Observer to see if inserted coin was rejected
	 */
	@Override
	public void reactToCoinsFullEvent(CoinDispenser dispenser) {
		//currentCoin_value = 0;
}
	/**
	 * Observer to see if bill was removed by the customer so we can dispense another
	 * @param slot
	 */
	@Override
	public void reactToBillRemovedEvent(BillSlot slot) {
		dispenseBill();
	}
	@Override
	public void reactToInvalidBillDetectedEvent(BillValidator validator) {}
	@Override
	public void reactToBillsLoadedEvent(BillStorage unit) { }
	@Override
	public void reactToBillsUnloadedEvent(BillStorage unit) {}
	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}
	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}
	
	@Override
	public void reactToInvalidCoinDetectedEvent(CoinValidator validator) {}
	@Override
	public void reactToBillInsertedEvent(BillSlot slot) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reactToBillEjectedEvent(BillSlot slot) {}

	@Override
	public void reactToCoinsEmptyEvent(CoinDispenser dispenser) {}
	@Override
	public void reactToCoinRemovedEvent(CoinDispenser dispenser, Coin coin) {}
	@Override
	public void reactToCoinsLoadedEvent(CoinDispenser dispenser, Coin... coins) {}
	@Override
	public void reactToCoinsUnloadedEvent(CoinDispenser dispenser, Coin... coins) {}

	
}
