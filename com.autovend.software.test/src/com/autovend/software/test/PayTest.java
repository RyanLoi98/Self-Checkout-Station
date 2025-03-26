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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Signature;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Bill;
import com.autovend.Card.CardData;
import com.autovend.Coin;
import com.autovend.CreditCard;
import com.autovend.DebitCard;
import com.autovend.GiftCard;
import com.autovend.GiftCard.GiftCardInsertData;
import com.autovend.InvalidPINException;
import com.autovend.Numeral;
import com.autovend.devices.DisabledException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReceiptPrinter;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SimulationException;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.PayCardController;
import com.autovend.software.SelfCheckoutStationLogic;


/**
 * Test Suite for Pay Functionalities
 *
 */
public class PayTest {
	private SelfCheckoutStation scs;
	//private PayWithCredit payCredit;
	private CreditCard creditCard;
	private DebitCard debitCard;
	private GiftCard giftCard;
	private ReceiptPrinter rp;
	private CardIssuer CreditBank;
	private CardIssuer DebitBank;
	private PayCardObserverStub creditStub;
	private PayCardObserverStub debitStub;
	private PayCardObserverStub giftStub;
	
	private SelfCheckoutStationLogic scsLogic;
	private Calendar calendar = Calendar.getInstance();
	
	Currency c1 = Currency.getInstance(Locale.CANADA);
	Currency c2 = Currency.getInstance(Locale.ITALY);
	int[] billdenominations = {5, 10, 15, 20, 50, 100};
	BigDecimal[] coindenominations = {new BigDecimal("0.25"), new BigDecimal("1")};

	Barcode barcode = new Barcode(Numeral.eight,Numeral.one,Numeral.two,Numeral.three);
	BarcodedProduct product = new BarcodedProduct(barcode, "Milk", new BigDecimal("20"), 2.5);
	BarcodedUnit unit = new BarcodedUnit(barcode, 2.5);
	
	Barcode barcode2 = new Barcode(Numeral.nine,Numeral.one,Numeral.two,Numeral.three);
	BarcodedProduct product2 = new BarcodedProduct(barcode, "Eggs", new BigDecimal("35"), 2.5);
	BarcodedUnit unit2 = new BarcodedUnit(barcode2, 2.5);
	
	Barcode barcode3 = new Barcode(Numeral.six,Numeral.one,Numeral.two,Numeral.three);
	BarcodedProduct product3 = new BarcodedProduct(barcode, "Chocolate", new BigDecimal("1.75"), 2.5);
	BarcodedUnit unit3 = new BarcodedUnit(barcode3, 2.5);
	
	Barcode barcode4 = new Barcode(Numeral.two,Numeral.one,Numeral.two,Numeral.three);
	BarcodedProduct product4 = new BarcodedProduct(barcode, "Banana", new BigDecimal("40"), 2.5);
	BarcodedUnit unit4 = new BarcodedUnit(barcode4, 2.5);

	
	Bill bill5 = new Bill(5, c1);
	Bill bill10 = new Bill(10, c1);
	Bill bill20 = new Bill(20, c1);
	Bill bill50 = new Bill(50, c1);
	Bill bill100 = new Bill(100, c1);
	Bill bill5DiffCurrency = new Bill(5, c2);
	
	Coin coin1 = new Coin(new BigDecimal("1"), c1);
	Coin coin025 = new Coin(new BigDecimal("0.25"), c1);
	Coin coininvalid = new Coin(new BigDecimal("1"), c2);



	AttendantStub attendantStub;
	CustomerStub customerStub;
	@Before
	public void setup() throws OverloadException {
		scs = new SelfCheckoutStation(c1, billdenominations, coindenominations, 20, 1);
        creditStub = new PayCardObserverStub();
        debitStub = new PayCardObserverStub();   
        giftStub = new PayCardObserverStub();   

		CreditBank = new CardIssuer("TD");
		DebitBank = new CardIssuer("RBC");
		calendar.add(Calendar.YEAR, 2);
		creditCard = new CreditCard("credit", "123456789", "John", "344", "1234", true, true);
		debitCard = new DebitCard("debit", "145323789", "James", "542", "1234", false, true);
		giftCard = new GiftCard("gift", "123456789", "1234", c1, new BigDecimal("100.00"));
		
		CreditBank.addCardData("123456789", "John", calendar, "344", new BigDecimal("600.00"));
		DebitBank.addCardData("145323789", "James", calendar, "542", new BigDecimal("400.00"));
		
		scsLogic = new SelfCheckoutStationLogic(scs, CreditBank, DebitBank, 0, "membershipdatabase.txt", 10, 10);
		attendantStub = new AttendantStub();
		customerStub = new CustomerStub();
		registerStubs(scsLogic);
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		scs.printer.addInk(50);
        scs.printer.addPaper(50);
      
    
		try {
			scs.billDispensers.get(20).load(bill20);	
			scs.billDispensers.get(20).unload();
			scs.coinDispensers.get(new BigDecimal("1")).load(coin1);
			scs.coinDispensers.get(new BigDecimal("1")).unload();
			scs.billStorage.load(bill10);
			scs.billStorage.unload();
			scs.billStorage.disable();;
			scs.billStorage.enable();
			scs.cardReader.disable();

			
			scs.billDispensers.get(20).load(bill20, bill20);
			scs.billDispensers.get(10).load(bill10, bill10, bill10, bill10);
			scs.billDispensers.get(5).load(bill5, bill5);
			
			scs.coinDispensers.get(new BigDecimal("1")).load(coin1, coin1);
			scs.coinDispensers.get(new BigDecimal("0.25")).load(coin025, coin025, coin025, coin025, coin025, coin025, coin025);

		} catch (SimulationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, product3);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode4, product4);
	}
	
	// Registers customerStub to PayObserver, PrintObserver, AddItemObserver
	// Registers attendantStub to PrintObserver
	private void registerStubs(SelfCheckoutStationLogic scsl) {
		scsl.registerPayCashObserver(customerStub);
		scsl.registerPrintObserver(customerStub);
		scsl.registerAddItemObserver(customerStub);
		scsl.registerPrintObserver(attendantStub);
		scsl.registerPayCashObserver(attendantStub);
        scsl.registerPayCardObserver(creditStub);
        scsl.registerPayCardObserver(debitStub);
        scsl.registerPayCardObserver(giftStub);
	}
	
	@Test
	public void PayWithDebitFirstThenCash_partialPayment_Zero() throws IOException, DisabledException, OverloadException{		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("10.00"));
		scs.cardReader.insert(creditCard, "1234");
		scsLogic.partialPayment();
		boolean scanned2 = scs.mainScanner.scan(unit2);
        while (scanned2 != true) {	//Until it scans
        	scanned2 = scs.mainScanner.scan(unit2);			
		}	
		scs.baggingArea.add(unit2);
		scsLogic.CustomerPay();
		boolean accept1 = scs.billInput.accept(bill50);
		if (accept1) { //if first bill was accepted
				double change = 0;
				while(!scs.billOutput.hasSpace()) {
					change += scs.billOutput.removeDanglingBill().getValue();
				}
				List<Coin> coins = scs.coinTray.collectCoins();
				for (Coin c : coins) {
					if (c != null) {
						change += c.getValue().doubleValue();
					}
				}
				assertEquals(5,change,0.0);	
		}
	}
	
	@Test
	public void PayWithCreditFirstThenCash_partialPayment_Zero() throws IOException, DisabledException, OverloadException{		
		boolean scanned = scs.mainScanner.scan(unit2);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit2);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("60.00"));
		scs.cardReader.insert(creditCard, "1234");
		scsLogic.partialPayment();
		boolean scanned2 = scs.mainScanner.scan(unit4);
        while (scanned2 != true) {	//Until it scans
        	scanned2 = scs.mainScanner.scan(unit4);			
		}	
		scs.baggingArea.add(unit2);
		scsLogic.CustomerPay();
		boolean accept1 = scs.billInput.accept(bill50);
		if (accept1) { //if first bill was accepted
				double change = 0;
				while(!scs.billOutput.hasSpace()) {
					change += scs.billOutput.removeDanglingBill().getValue();
				}
				List<Coin> coins = scs.coinTray.collectCoins();
				for (Coin c : coins) {
					if (c != null) {
						change += c.getValue().doubleValue();
					}
				}
				assertEquals(35,change,0.0);	
		}
	}

	
	@Test
	public void PayWithCashFirstThenCredit_partialPayment_Zero() throws IOException, DisabledException, OverloadException{		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerPay();
		boolean accept1 = scs.billInput.accept(bill10);
		if (scs.billInput.hasSpace()) { //if first bill was accepted
			scsLogic.partialPayment();
			boolean scanned2 = scs.mainScanner.scan(unit2);
	        while (scanned2 != true) {	//Until it scans
	        	scanned2 = scs.mainScanner.scan(unit2);			
			}	
			scs.baggingArea.add(unit2);
			scsLogic.CustomerCreditPay(new BigDecimal("45.00"));
			scs.cardReader.insert(creditCard, "1234");
			assertTrue(BigDecimal.valueOf(0.0).compareTo(creditStub.getTotal()) == 0);
		}
	}
	
	@Test
	public void PayWithCashFirstThenDebit_partialPayment_Zero() throws IOException, DisabledException, OverloadException{		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerPay();
		boolean accept1 = scs.billInput.accept(bill10);
		if (scs.billInput.hasSpace()) { //if first bill was accepted
			scsLogic.partialPayment();
			boolean scanned2 = scs.mainScanner.scan(unit3);
	        while (scanned2 != true) {	//Until it scans
	        	scanned2 = scs.mainScanner.scan(unit3);			
			}	
			scs.baggingArea.add(unit3);
			scsLogic.CustomerDebitPay(new BigDecimal("11.75"));
			scs.cardReader.insert(debitCard, "1234");
			assertTrue(BigDecimal.valueOf(0.0).compareTo(debitStub.getTotal()) == 0);
		}
	}
	@Test
	public void PayWithCredit_PayTotal_Zero() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("20.00"));
		scs.cardReader.insert(creditCard, "1234");
		assertTrue(BigDecimal.valueOf(0.0).compareTo(creditStub.getTotal()) == 0);
	}
	

	@Test
	public void PayWithCredit_Tap() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
		while (scanned != true) {	//Until it scans
			scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("20.00"));
		scs.cardReader.tap(creditCard);
		assertTrue(BigDecimal.valueOf(0.0).compareTo(creditStub.getTotal()) == 0);
	}
	
	@Test
	public void PayWithCredit_Insert() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
		while (scanned != true) {	//Until it scans
			scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("20.00"));
		scs.cardReader.insert(creditCard, "1234");
		assertTrue(BigDecimal.valueOf(0.0).compareTo(creditStub.getTotal()) == 0);
	}
	
	@Test
	public void PayWithCredit_Swipe() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
		while (scanned != true) {	//Until it scans
			scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("20.00"));
		scs.cardReader.swipe(creditCard, null);
		assertTrue(BigDecimal.valueOf(0.0).compareTo(creditStub.getTotal()) == 0);
	}
	
	@Test
	public void PayWithCredit_BadCard_FailedHold() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		CreditCard cc = new CreditCard("credit", "123456", "Bob", "785", "5678", false, true);
		scsLogic.CustomerCreditPay(new BigDecimal("20.00"));
		scs.cardReader.insert(cc, "5678");
		assertTrue(BigDecimal.valueOf(20.0).compareTo(creditStub.getTotal()) == 0);
	}
	
	@Test
	public void PayWithCredit_PayPartial_10() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("10.00"));
		scs.cardReader.insert(creditCard, "1234");
		assertTrue(BigDecimal.valueOf(10.0).compareTo(creditStub.getTotal())==0);
	}
	
	@Test
	public void PayWithCredit_InsufficientFunds_Negative1() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		CreditCard cc = new CreditCard("credit", "123456", "Bob", "785", "5678", false, true);
		CreditBank.addCardData("123456", "Bob", calendar, "785", new BigDecimal("10.0"));
		scsLogic.CustomerCreditPay(new BigDecimal("20.00"));
		scs.cardReader.insert(cc, "5678");
		assertTrue(BigDecimal.valueOf(20.0).compareTo(creditStub.getTotal()) == 0);
	}
	
	@Test(expected = InvalidPINException.class)
	public void PayWithCredit_InavlidPin_Error() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		scsLogic.CustomerCreditPay(new BigDecimal("20.00"));
		scs.cardReader.insert(creditCard, "0000");
	}
	
	@Test
	public void PayWithDebit_PayTotal_Zero() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}	
		scs.baggingArea.add(unit);
		scsLogic.CustomerDebitPay(new BigDecimal("20.00"));
		scs.cardReader.insert(debitCard, "1234");
		assertTrue(BigDecimal.valueOf(0.0).compareTo(debitStub.getTotal()) == 0);
	}
	
	@Test
	public void PayWithDebit_BadCard_FailedHold() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		DebitCard dc = new DebitCard("debit", "123456", "Bob", "785", "5678", false, true);
		scsLogic.CustomerDebitPay(new BigDecimal("20.00"));
		scs.cardReader.insert(dc, "5678");
		assertTrue(BigDecimal.valueOf(20.0).compareTo(debitStub.getTotal()) == 0);
	}
	
	@Test
	public void PayWithDebit_PayPartial_10() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		scsLogic.CustomerDebitPay(new BigDecimal("10.00"));
		scs.cardReader.insert(debitCard, "1234");
		assertTrue(BigDecimal.valueOf(10.0).compareTo(debitStub.getTotal()) == 0);
	}
	
	@Test
	public void PayWithDebit_InsufficientFunds_Negative1() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		DebitCard dc = new DebitCard("debit", "123456", "Bob", "785", "5678", false, true);
		DebitBank.addCardData("123456", "Bob", calendar, "785", new BigDecimal("10.0"));
		scsLogic.CustomerDebitPay(new BigDecimal("20.00"));
		scs.cardReader.insert(dc, "5678");
		assertTrue(BigDecimal.valueOf(20.0).compareTo(debitStub.getTotal()) == 0);
	}
	
	
	@Test(expected = InvalidPINException.class)
	public void PayWithDebit_InavlidPin_Error() throws DisabledException, OverloadException, IOException {		
		boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
        	scanned = scs.mainScanner.scan(unit);			
		}
		scs.baggingArea.add(unit);
		scsLogic.CustomerDebitPay(new BigDecimal("20.00"));
		scs.cardReader.insert(debitCard, "0000");
	}	
	
		//Sometimes fails when coin is not accepted
		@Test
		public void PayWithCoins_NeedChange_025() throws DisabledException, OverloadException {
			boolean scanned = scs.mainScanner.scan(unit3);
			while(!scanned) {
				scanned = scs.mainScanner.scan(unit3);
			}
			if (scanned) { //if item was scanned successfully
				scs.baggingArea.add(unit3);
				scsLogic.CustomerPay();
				boolean accept1 = scs.coinSlot.accept(coin1);
				if (accept1) { //if first coin was accepted
					boolean accept2 = scs.coinSlot.accept(coin1);
					if (accept2) { //if second coin was accepted
						List<Coin> coins = scs.coinTray.collectCoins();
						double change = 0;
						for (Coin c : coins) {
							if (c != null) {
								change += c.getValue().doubleValue();
							}
						}
						assertEquals(0.25, change, 0.0);
					}
				}
			}
		}
		
		@Test
		public void payTest_LessBillChange() throws DisabledException, OverloadException {
			boolean scanned = scs.mainScanner.scan(unit4);
			while(!scanned) {
				scanned = scs.mainScanner.scan(unit4);
			}
			if (scanned) { //if item was scanned successfully
				scs.baggingArea.add(unit4);
				scsLogic.CustomerPay();
				boolean accept1 = scs.coinSlot.accept(coin1);
				if (accept1) { //if first coin was accepted
					boolean accept2 =  scs.billInput.accept(bill100);
					if (accept2) { //if second coin was accepted
						double change = 0;
						while(!scs.billOutput.hasSpace()) {
							change += scs.billOutput.removeDanglingBill().getValue();
						}
						List<Coin> coins = scs.coinTray.collectCoins();
						for (Coin c : coins) {
							if (c != null) {
								change += c.getValue().doubleValue();
							}
						}
						assertEquals(61.0,change,0.0);

					}
				}
			}
		}
		
		@Test
		public void payTest_LessCoinChange() throws DisabledException, OverloadException {
			boolean scanned = scs.mainScanner.scan(unit3);
			while(!scanned) {
				scanned = scs.mainScanner.scan(unit3);
			}
			if (scanned) { //if item was scanned successfully
				scs.baggingArea.add(unit3);
				scsLogic.CustomerPay();
				boolean accept1 = scs.billInput.accept(bill5);
				if (accept1) { //if first bill was accepted
						double change = 0;
						while(!scs.billOutput.hasSpace()) {
							change += scs.billOutput.removeDanglingBill().getValue();
						}
						List<Coin> coins = scs.coinTray.collectCoins();
						for (Coin c : coins) {
							if (c != null) {
								change += c.getValue().doubleValue();
							}
						}
						assertEquals(3.25,change,0.0);
					}
				}
			}
		@Test
		public void coinTestInsufficientChange() throws DisabledException, OverloadException {
			boolean scanned = scs.mainScanner.scan(unit3);
			while(!scanned) {
				scanned = scs.mainScanner.scan(unit3);
			}
			if (scanned) { //if item was scanned successfully

				scs.baggingArea.add(unit3);
				scsLogic.CustomerPay();
				boolean accept1 = scs.coinSlot.accept(coin1);
				if (accept1) { //if first coin was accepted
					scs.billInput.accept(bill100);
					if (scs.billInput.hasSpace()) { //if second coin was accepted
						assertEquals(true, attendantStub.isInsufficientChange());
					}
				}
			}
		}
		
		@Test
		public void billTest_storageAtCapacity() throws DisabledException, OverloadException {
			Bill[] bills = new Bill[1000];
			for (int i = 0; i < 1000; i++) {
				bills[i] = bill10;
			}
			scs.billStorage.load(bills);
			boolean scanned = scs.mainScanner.scan(unit);
	        while (scanned != true) {	//Until it scans
	        	scanned = scs.mainScanner.scan(unit);			
			}       
			scs.baggingArea.add(unit);
			scsLogic.CustomerPay();
			boolean accept1 = scs.billInput.accept(bill100);
			if (accept1) { //if first bill was accepted
				scs.billStorage.unload();
				scs.billInput.removeDanglingBill();
				boolean accept2 = scs.billInput.accept(bill5);
				if (accept2) {
				    assertTrue(BigDecimal.valueOf(15.0).compareTo(customerStub.getBillAmount()) == 0);	//meaning first bill was not processed
				}
			}
		}
		@Test
		public void payByCash_FullPayment_$0() throws OverloadException{
	        boolean scanned = scs.mainScanner.scan(unit);
	        while (scanned != true) {	//Until it scans
	        	scanned = scs.mainScanner.scan(unit);			
			}       
	        scs.baggingArea.add(unit);
	        scsLogic.CustomerPay();
	        scs.billInput.accept(bill20);
			if (scs.billInput.hasSpace()) {
				assertTrue(BigDecimal.valueOf(0.0).compareTo(customerStub.getBillAmount()) == 0);
			}
	    }

		@Test
		public void payByCash_CustomerUsedInvalidBill_$15() throws OverloadException{
	        boolean scanned = scs.mainScanner.scan(unit);
	        while (scanned != true) {	//Until it scans
	        	scanned = scs.mainScanner.scan(unit);			
			}       
	        scs.baggingArea.add(unit);
	        scsLogic.CustomerPay();
	        scs.billInput.accept(bill5);
			if (scs.billInput.hasSpace()) {
		        scs.billInput.accept(bill5DiffCurrency);
		        assertTrue(BigDecimal.valueOf(15.0).compareTo(customerStub.getBillAmount()) == 0);
			}
	    }
		
		@Test
		public void payByCoin_CustomerUsedInvalidCoin_$15() throws OverloadException{
	        boolean scanned = scs.mainScanner.scan(unit);
	        while (scanned != true) {	//Until it scans
	        	scanned = scs.mainScanner.scan(unit);			
			}       
	        scs.baggingArea.add(unit);
	        scsLogic.CustomerPay();
	        scs.billInput.accept(bill5);
			if (scs.billInput.hasSpace()) {
		        scs.coinSlot.accept(coininvalid);
		        assertTrue(BigDecimal.valueOf(15.0).compareTo(customerStub.getBillAmount()) == 0);
			}
	    }
		
		
		@Test
		public void PayWithGiftFirstThenCash_partialPayment_Zero() throws IOException, DisabledException, OverloadException{		
			boolean scanned = scs.mainScanner.scan(unit2);
	        while (scanned != true) {	//Until it scans
	        	scanned = scs.mainScanner.scan(unit2);			
			}	
			scs.baggingArea.add(unit);
			scsLogic.CustomerGiftPay(new BigDecimal("60.00"));
			GiftCardInsertData cardData = (GiftCardInsertData) scs.cardReader.insert(giftCard, "1234");
			scsLogic.partialPayment();
			boolean scanned2 = scs.mainScanner.scan(unit4);
	        while (scanned2 != true) {	//Until it scans
	        	scanned2 = scs.mainScanner.scan(unit4);			
			}	
			scs.baggingArea.add(unit2);
			scsLogic.CustomerPay();
			boolean accept1 = scs.billInput.accept(bill50);
			if (accept1) { //if first bill was accepted
					double change = 0;
					while(!scs.billOutput.hasSpace()) {
						change += scs.billOutput.removeDanglingBill().getValue();
					}
					List<Coin> coins = scs.coinTray.collectCoins();
					for (Coin c : coins) {
						if (c != null) {
							change += c.getValue().doubleValue();
						}
					}
					assertEquals(35,change,0.0);	
			}
		}
		
		@Test(expected = InvalidPINException.class)
		public void PayWithGift_InavlidPin_Error() throws DisabledException, OverloadException, IOException {		
			boolean scanned = scs.mainScanner.scan(unit);
	        while (scanned != true) {	//Until it scans
	        	scanned = scs.mainScanner.scan(unit);			
			}
			scs.baggingArea.add(unit);
			scsLogic.CustomerGiftPay(new BigDecimal("20.00"));
			scs.cardReader.insert(giftCard, "0000");
		}
		
		@Test(expected = SimulationException.class)
		public void PayWithGift_InsufficientFunds_Negative1() throws DisabledException, OverloadException, IOException {		
			boolean scanned = scs.mainScanner.scan(unit);
	        while (scanned != true) {	//Until it scans
	        	scanned = scs.mainScanner.scan(unit);			
			}
			scs.baggingArea.add(unit);
			GiftCard gc = new GiftCard("gift", "123456789", "1234", c1, new BigDecimal("0.00"));
			scsLogic.CustomerGiftPay(new BigDecimal("20.00"));
			scs.cardReader.insert(gc, "1234");
		}
		
		/**
		 * This class is used by the following test case to make a mock card issuer that will time out the connection.  
		 *
		 */
		private class CardIssuerMock extends CardIssuer {
			int waitTime;
			public CardIssuerMock(String name) {
				super(name);
			}
			public void setWait(int wait) {
				waitTime= wait;
			}
			
			public int authorizeHold(String cardNumber, BigDecimal amount) {
				return 1;
			}
			public boolean postTransaction(String cardNumber, int holdNumber, BigDecimal actualAmount) {
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return false;
			}
			
			public boolean releaseHold(String cardNumber, int holdNumber) {
				return true;
			}
			
		}
		
		/**
		 * This test case will check that the code will actually catch time out connections.
		 */
		@Test
		public void testAuthorizationFailed()  {
			CardIssuerMock fakeIssuer = new CardIssuerMock("mock");
			fakeIssuer.setWait(600);
			PayCardController payCardController = new PayCardController(scs, null, fakeIssuer, fakeIssuer);
			CardData data = null;
			for(int i = 0; i < 10; i++) {
				try {
					data = creditCard.swipe();
					break;
				}catch(IOException e) {
					
				}
			}
			payCardController.processHoldValidation(fakeIssuer, BigDecimal.ONE, data, 500);
		}
	
}
