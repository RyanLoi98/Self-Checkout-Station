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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.SelfCheckoutStation;

import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BillDispenserObserver;
import com.autovend.devices.observers.CoinDispenserObserver;

/**
 * 
 * Class for the use cases of:
 * 		Adjust coins for change
 * 		Adjust banknotes for change
 *
 */
public class AdjustChangeController implements BillDispenserObserver, CoinDispenserObserver {
	private SelfCheckoutStation station;
	private final int MINIMUM_BILL_THRESHOLD; // minimum bill threshold
	private final int MINIMUM_COIN_THRESHOLD;
	
	private Map<BillDispenser, Integer> billsLeft;
	private Map<CoinDispenser, Integer> coinsLeft;
	
	private boolean sessionInProgress;
	
	private ArrayList<AdjustChangeListener> listeners;
	
	/**
	 * Constructor for AdjustChangeController
	 * @param scs		self checkout station device
	 * @param billsMin	minimum bill threshold
	 * @param coinsMin	minimum coin threshold
	 */
	public AdjustChangeController(SelfCheckoutStation scs, int billsMin, int coinsMin) {
		this.station = scs;
		this.MINIMUM_BILL_THRESHOLD = billsMin;
		this.MINIMUM_COIN_THRESHOLD = coinsMin;
		
		billsLeft = new HashMap<BillDispenser, Integer>();
		coinsLeft = new HashMap<CoinDispenser, Integer>();
		
		// register each billDispenser, and add the
		for (BillDispenser bd: scs.billDispensers.values()) {
			bd.register(this);
			billsLeft.put(bd, 0);
		}
		// register each coinDispenser
		for (CoinDispenser cd: scs.coinDispensers.values()) {
			cd.register(this);
			coinsLeft.put(cd, 0);
		}
		
		sessionInProgress = false;
		
		listeners = new ArrayList<AdjustChangeListener>();
	}
	
	public void register(AdjustChangeListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Method to start the adjust change session
	 */
	public void startSession() {
		sessionInProgress = true;
	}
	
	/**
	 * Method to end the adjust change session
	 */
	public void endSession() {
		sessionInProgress = false;
		if (!isBillAboveThreshold() || !isCoinAboveThreshold()) {
			// TODO: shutdown station
			disableStationIfLowChange();
		}
	}
	
	/**
	 * Method to get if session is currently in progress
	 */
	public boolean getSessionInProgress() {
		return sessionInProgress;
	}
	
	public void disableStationIfLowChange() {
		if (!isBillAboveThreshold()) {
			for (AdjustChangeListener listener: listeners) {
				listener.reactToLowBanknotes(station);
			}
			disableStation();
		}
		else if (!isCoinAboveThreshold()) {
			for (AdjustChangeListener listener: listeners) {
				listener.reactToLowCoins(station);
			}
			disableStation();
		}
	}
	
	public void disableStation() {
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
		station.bagDispenser.disable();
		for (BillDispenser b : station.billDispensers.values()) {
			b.disable();
		}
		for (CoinDispenser c : station.coinDispensers.values()) {
			c.disable();
		}
	}
	
	public void enableStation() {
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
		station.bagDispenser.enable();
		for (BillDispenser b : station.billDispensers.values()) {
			b.enable();
		}
		for (CoinDispenser c : station.coinDispensers.values()) {
			c.enable();
		}
	}

	/** 
	 * Implement BillDispenserObserver for AdjustChangeController ( Adjust Banknotes for Change )
	 * 
	 */
	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}

	@Override
	public void reactToBillsFullEvent(BillDispenser dispenser) {

	}

	@Override
	public void reactToBillsEmptyEvent(BillDispenser dispenser) {
		if (sessionInProgress) {
			// TODO: notify the attendant again
			for (AdjustChangeListener listener: listeners) {
				listener.reactToEmptyBanknotes(station);
			}
		}
	}

	/**
	 * note: never used
	 */
	@Override
	public void reactToBillAddedEvent(BillDispenser dispenser, Bill bill) {
		billsLeft.put(dispenser, billsLeft.get(dispenser) + 1);
	}

	/**
	 * note : never used
	 */
	@Override
	public void reactToBillRemovedEvent(BillDispenser dispenser, Bill bill) {
		billsLeft.put(dispenser, billsLeft.get(dispenser) - 1);
		if (billsLeft.get(dispenser) < MINIMUM_BILL_THRESHOLD) {
			for (AdjustChangeListener listener: listeners) {
				listener.reactToLowBanknotes(station);
			}	
		}
	}

	@Override
	public void reactToBillsLoadedEvent(BillDispenser dispenser, Bill... bills) {
		billsLeft.put(dispenser, billsLeft.get(dispenser) + bills.length);
	}

	@Override
	public void reactToBillsUnloadedEvent(BillDispenser dispenser, Bill... bills) {
		billsLeft.put(dispenser, billsLeft.get(dispenser) - bills.length);
		this.reactToBillsEmptyEvent(dispenser);
		if (billsLeft.get(dispenser) < MINIMUM_BILL_THRESHOLD) {
			for (AdjustChangeListener listener: listeners) {
				listener.reactToLowBanknotes(station);
			}	
		}
	}

	
	/**
	 * Implement CoinDispenserObserver for AdjustChangeController (Adjust Coins for Change)
	 */
	@Override
	public void reactToCoinsFullEvent(CoinDispenser dispenser) {

	}

	@Override
	public void reactToCoinsEmptyEvent(CoinDispenser dispenser) {
		if (sessionInProgress) {
			// TODO: notify the attendant again
			for (AdjustChangeListener listener: listeners) {
				listener.reactToEmptyCoins(station);
			}
		}
	}

	
	/**
	 * note : never used
	 */
	@Override
	public void reactToCoinAddedEvent(CoinDispenser dispenser, Coin coin) {
		coinsLeft.put(dispenser, coinsLeft.get(dispenser) + 1);
	}

	/**
	 * note : never used
	 */
	@Override
	public void reactToCoinRemovedEvent(CoinDispenser dispenser, Coin coin) {
		coinsLeft.put(dispenser, coinsLeft.get(dispenser) - 1);
		if (coinsLeft.get(dispenser) < MINIMUM_COIN_THRESHOLD) {
			for (AdjustChangeListener listener: listeners) {
				listener.reactToLowCoins(station);
			}		
		}
	}

	@Override
	public void reactToCoinsLoadedEvent(CoinDispenser dispenser, Coin... coins) {
		coinsLeft.put(dispenser, coinsLeft.get(dispenser) + coins.length);
	}

	@Override
	public void reactToCoinsUnloadedEvent(CoinDispenser dispenser, Coin... coins) {
		coinsLeft.put(dispenser, coinsLeft.get(dispenser) - coins.length);
		if (coinsLeft.get(dispenser) == 0) {
			this.reactToCoinsEmptyEvent(dispenser);
		}
		if (coinsLeft.get(dispenser) < MINIMUM_COIN_THRESHOLD) {
			for (AdjustChangeListener listener: listeners) {
				listener.reactToLowCoins(station);
			}
		}
	}
	
	/**
	 * Method to check if all bill dispensers are above the threshold
	 * @return 	false if a bill dispenser is below the minimum threshold
	 * 			true otherwise
	 */
	private boolean isBillAboveThreshold() {
		for (int bl: billsLeft.values()) {
			if (bl < MINIMUM_BILL_THRESHOLD) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Method to check if all coin dispensers are above the threshold
	 * @return 	false if a coin dispenser is below the minimum threshold
	 * 			true otherwise
	 */
	private boolean isCoinAboveThreshold() {
		for (int cl: coinsLeft.values()) {
			if (cl < MINIMUM_COIN_THRESHOLD) {
				return false;
			}
		}
		return true;
	}
}
