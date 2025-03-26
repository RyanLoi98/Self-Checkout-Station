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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.Bill;
import com.autovend.Coin;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.DisabledException;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.software.AdjustChangeController;
import com.autovend.software.AdjustChangeListener;
import com.autovend.software.SelfCheckoutStationLogic;

/**
 * 
 * Test suite for AdjustChangeController
 * 
 * 		Use cases: 	Adjust Coins for Change
 * 					Adjust Banknotes for Change
 *
 */
public class AdjustChangeTest {
	
	SelfCheckoutStation scs;
	Currency c1 = Currency.getInstance(Locale.CANADA);
	Currency c2 = Currency.getInstance(Locale.ITALY);
	int[] billdenominations = { 5, 10, 15, 20, 50 };
	BigDecimal[] coindenominations = { new BigDecimal("1"), new BigDecimal("2"), 
										new BigDecimal("0.25") };
	Bill bill5 = new Bill(5, c1);
	Bill bill20 = new Bill(20, c1);
	Bill bill50 = new Bill(50, c1);
	Coin coin1 = new Coin(new BigDecimal("1"), c1);
	Coin coin2 = new Coin(new BigDecimal("2"), c1);
	Coin coin025 = new Coin(new BigDecimal("0.25"), c1);
	Bill bill5DiffCurrency = new Bill(5, c2);
	AttendantStub attendantStub;
	CustomerStub customerStub;
	SelfCheckoutStationLogic logic;
	BillDispenser billDispenser1;
	CoinDispenser coinDispenser1;
	AdjustChangeController adjustChange;
	AdjustChangeListenerStub stub;
	
	@Before
	public void setup() {
		attendantStub = new AttendantStub();
		customerStub = new CustomerStub();
		scs = new SelfCheckoutStation(c1, billdenominations, coindenominations, 20, 1);
		adjustChange = new AdjustChangeController(scs, 10, 10);
		stub = new AdjustChangeListenerStub();
		adjustChange.register(stub);
		adjustChange.startSession();
		
		try {
			for (int j = 0; j < 20; j++) {
				for (int bd : scs.billDenominations) {
					scs.billDispensers.get(bd).load(new Bill(bd, c1));
				}
				for (BigDecimal cd : scs.coinDenominations) {
					scs.coinDispensers.get(cd).load(new Coin(cd, c1));
				}
			}
		} catch (Exception e) {}
	}
	
	@After
	public void shutdown() {
		attendantStub = null;
		customerStub = null;
		scs = null;
		logic = null;
		billDispenser1 = null;
		coinDispenser1 = null;
		adjustChange = null;
		stub = null;
	}
	
	@Test
	public void startSessionTest() {
		adjustChange.startSession();
		assertTrue(adjustChange.getSessionInProgress());
	}
	
	@Test
	public void endSessionTest() {
		adjustChange.endSession();
		assertFalse(adjustChange.getSessionInProgress());
	}
	
	@Test
	public void endSessionLowBanknotesEmit() {
		for (BillDispenser b : scs.billDispensers.values()) {
			for (int i = 0; i < 11; i++) {
				try {
					scs.billOutput.removeDanglingBill();
					b.emit();
				}
				catch (Exception e) {
					assertTrue(false);
				}
			}
		}
		adjustChange.endSession();
		assertFalse(adjustChange.getSessionInProgress());
		assertTrue(stub.notifiedLowBanknotes);
	}
	
	@Test
	public void emptyBanknotesMidSession() {
		for (BillDispenser b : scs.billDispensers.values()) {
			for (int i = 0; i < 20; i++) {
				try {
					scs.billOutput.removeDanglingBill();
					b.emit();
				}
				catch (Exception e) {
					assertTrue(false);
				}
			}
		}
		assertTrue(stub.notifiedLowBanknotes);
		assertTrue(stub.notifiedEmptyBanknotes);
		adjustChange.endSession();
	}
	
	@Test
	public void endSessionLowBanknotes() {
		for (BillDispenser b : scs.billDispensers.values()) {
			b.unload();
		}
		adjustChange.endSession();
		assertFalse(adjustChange.getSessionInProgress());
		assertTrue(stub.notifiedLowBanknotes);
	}

	
	@Test
	public void endSessionLowCoins() {
		for (CoinDispenser c : scs.coinDispensers.values()) {
			c.unload();
		}
		adjustChange.endSession();
		assertFalse(adjustChange.getSessionInProgress());
		assertTrue(stub.notifiedLowCoins);
	}
	
	@Test
	public void emptyCoinsMidSession() {
		for (CoinDispenser c : scs.coinDispensers.values()) {
			for (int i = 0; i < 20; i++) {
				try {
					scs.coinTray.collectCoins();
					c.emit();
				}
				catch (Exception e) {
					assertTrue(false);
				}
			}
		}
		assertTrue(stub.notifiedLowCoins);
		assertTrue(stub.notifiedEmptyCoins);
		adjustChange.endSession();
	}
	
	@Test
	public void endSessionLowCoinsEmit() {
		for (CoinDispenser c : scs.coinDispensers.values()) {
			for (int i = 0; i < 11; i++) {
				try {
					scs.coinTray.collectCoins();
					c.emit();
				}
				catch (Exception e) {
					assertTrue(false);
				}
			}
		}
		adjustChange.endSession();
		assertFalse(adjustChange.getSessionInProgress());
		assertTrue(stub.notifiedLowCoins);
	}
	
	@Test
	public void disableStationTest() {
		adjustChange.disableStation();
		assertTrue(scs.baggingArea.isDisabled());
		assertTrue(scs.billInput.isDisabled());
		assertTrue(scs.billOutput.isDisabled());
		assertTrue(scs.billStorage.isDisabled());
		assertTrue(scs.billValidator.isDisabled());
		assertTrue(scs.cardReader.isDisabled());
		assertTrue(scs.coinSlot.isDisabled());
		assertTrue(scs.coinStorage.isDisabled());
		assertTrue(scs.coinTray.isDisabled());
		assertTrue(scs.coinValidator.isDisabled());
		assertTrue(scs.handheldScanner.isDisabled());
		assertTrue(scs.mainScanner.isDisabled());
		assertTrue(scs.scale.isDisabled());
		assertTrue(scs.printer.isDisabled());
		assertTrue(scs.screen.isDisabled());
		assertTrue(scs.bagDispenser.isDisabled());
		for (BillDispenser b : scs.billDispensers.values()) {
			assertTrue(b.isDisabled());
		}
		for (CoinDispenser c : scs.coinDispensers.values()) {
			assertTrue(c.isDisabled());
		}
	}
	
	@Test
	public void enableStationTest() {
		adjustChange.enableStation();
		assertFalse(scs.baggingArea.isDisabled());
		assertFalse(scs.billInput.isDisabled());
		assertFalse(scs.billOutput.isDisabled());
		assertFalse(scs.billStorage.isDisabled());
		assertFalse(scs.billValidator.isDisabled());
		assertFalse(scs.cardReader.isDisabled());
		assertFalse(scs.coinSlot.isDisabled());
		assertFalse(scs.coinStorage.isDisabled());
		assertFalse(scs.coinTray.isDisabled());
		assertFalse(scs.coinValidator.isDisabled());
		assertFalse(scs.handheldScanner.isDisabled());
		assertFalse(scs.mainScanner.isDisabled());
		assertFalse(scs.scale.isDisabled());
		assertFalse(scs.printer.isDisabled());
		assertFalse(scs.screen.isDisabled());
		assertFalse(scs.bagDispenser.isDisabled());
		for (BillDispenser b : scs.billDispensers.values()) {
			assertFalse(b.isDisabled());
		}
		for (CoinDispenser c : scs.coinDispensers.values()) {
			assertFalse(c.isDisabled());
		}
	}
	

	
	public class AdjustChangeListenerStub implements AdjustChangeListener {
		
		public boolean notifiedLowBanknotes = false;
		public boolean notifiedLowCoins = false;
		public boolean notifiedEmptyBanknotes = false;
		public boolean notifiedEmptyCoins = false;

		
		@Override
		public void reactToLowBanknotes(SelfCheckoutStation scs) {
			notifiedLowBanknotes = true;
		}
	
		@Override
		public void reactToLowCoins(SelfCheckoutStation scs) {
			notifiedLowCoins = true;
		}
	
		@Override
		public void reactToEmptyBanknotes(SelfCheckoutStation scs) {
			notifiedEmptyBanknotes = true;
		}
	
		@Override
		public void reactToEmptyCoins(SelfCheckoutStation scs) {
			notifiedEmptyCoins = true;
		}

	
	}

}
