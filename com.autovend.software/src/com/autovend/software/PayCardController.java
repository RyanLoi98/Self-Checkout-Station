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

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.autovend.Bill;
import com.autovend.Card;
import com.autovend.Coin;
import com.autovend.Card.CardData;
import com.autovend.ChipFailureException;
import com.autovend.GiftCard.GiftCardInsertData;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BillDispenser;
import com.autovend.devices.BillSlot;
import com.autovend.devices.BillStorage;
import com.autovend.devices.BillValidator;
import com.autovend.devices.CardReader;
import com.autovend.devices.CoinDispenser;
import com.autovend.devices.CoinValidator;
import com.autovend.devices.DisabledException;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BillSlotObserver;
import com.autovend.devices.observers.BillStorageObserver;
import com.autovend.devices.observers.BillValidatorObserver;
import com.autovend.devices.observers.CardReaderObserver;
import com.autovend.devices.observers.CoinDispenserObserver;
import com.autovend.devices.observers.CoinStorageObserver;
import com.autovend.devices.observers.CoinValidatorObserver;
import com.autovend.external.CardIssuer;


/**
 * Pay Card Controller, includes Pay with Cash(bills or coins), Credit, Debit
 * @author mankaranpandher
 *
 */
public class PayCardController implements CardReaderObserver{
	private SelfCheckoutStation scs;
	private PrintReceiptController receipt;
	private CardIssuer creditBank;
	private CardIssuer debitBank;
	private BigDecimal payAmount;
	private BillRecord record;
	private BigDecimal total;
	private BigDecimal totalBillPaid = BigDecimal.valueOf(0.0);
	private CardReader cr;
	private ArrayList<PayCardObserver> observers = new ArrayList<>();
	public boolean cardInserted = false;
	private int cardHoldTimeout = 20000;
	/**
	 * Pay card constructor
	 * @param station: station to install on
	 * @param printer: printer to print receipt
	 * @param ccreditBank: bank for credit cards
	 * @param dcreditBank: bank for debit cards
	 */
	public PayCardController(SelfCheckoutStation station, PrintReceiptController printer, CardIssuer ccreditBank, CardIssuer dcreditBank) {
		scs = station;
		receipt = printer;
		scs.cardReader.register(this);
		creditBank = ccreditBank;
		debitBank = dcreditBank;
	}

	/**
	 * Notified by customer I/O that customer is paying with credit and it's amount
	 * @param amount
	 * @param br
	 */
	public void customerCreditPay(BigDecimal amount, BillRecord br) {
//		if (cashTotal.compareTo(totalBillPaid) > 0) {
//			totalBillPaid = cashTotal;
//		}
		scs.cardReader.enable();
		payAmount = amount;
		record = br;
//		total = record.getTotal();
	}
//	/**
//	 * Returns total bill paid
//	 */
//	public BigDecimal getTotalBillPaid() {
//		return totalBillPaid;
//	}
//	
	/**
	 * Session has ended, so PayCash can reset its totals and counts
	 */
	public void sessionEnd() {
		totalBillPaid = BigDecimal.valueOf(0.0);
		total = BigDecimal.valueOf(0.0);
	}
	/**
	 * Notified by customer I/O that customer is paying with debit and it's amount
	 * @param amount
	 * @param br
	 */
	public void customerDebitPay(BigDecimal amount, BillRecord br) {
//		if (cashTotal.compareTo(totalBillPaid) > 0) {
//			totalBillPaid = cashTotal;
//		}
		scs.cardReader.enable();
		payAmount = amount;
		record = br;
//		total = record.getTotal();
	}
	
	/**
	 * Notified by customer I/O that customer is paying with gift card and it's amount
	 * @param amount
	 * @param br
	 */
	public void customerGiftPay(BigDecimal amount, BillRecord br) {
//		if (cashTotal.compareTo(totalBillPaid) > 0) {
//			totalBillPaid = cashTotal;
//		}
		scs.cardReader.enable();
		payAmount = amount;
		record = br;
//		total = record.getTotal();
	}
	
	/**
	 * Places a hold and processes on the customers Credit Card
	 */
	public void placeCreditHold(CardData cardData) {
		processHoldValidation(creditBank, payAmount, cardData, cardHoldTimeout);
	}
	
	/**
	 * Places a hold and processes on the customers Debit Card
	 */
	public void placeDebitHold(CardData cardData) {
		processHoldValidation(debitBank, payAmount, cardData, cardHoldTimeout);
	}
	
	public void attemptGiftPayment(CardData data) {
		GiftCardInsertData cardData = (GiftCardInsertData) data;
		try {
			cardData.deduct(payAmount);
		} catch (ChipFailureException e) {
			e.printStackTrace();
		}
		
//		The payment is done
	   	record.addPayment(payAmount);
	   	takeMoney();
//		Possibly call to customer IO
	   	endPayment();
		return;
		
	}
	
	public void processHoldValidation(CardIssuer cardIssuer, BigDecimal payment, CardData cardData, int validationTimeoutWait) {
		int holdNumber = cardIssuer.authorizeHold(cardData.getNumber(), payment);
		if (holdNumber <0){
//			Will replace later with proper customerIO call
//			stationLogic.notifyCustomerIO("Card declined.");
			endPayment();
			return;
		}
//		With this code, the post transaction status will be attempted up to 5 times, and cut off if it goes over 20 seconds. 
		for (int i = 0; i < 5; i++) {
			AtomicBoolean transactionStatus = new AtomicBoolean(false);
			
			Thread t = new Thread(() -> {
				   long startTime = System.currentTimeMillis();
				   boolean result = cardIssuer.postTransaction(cardData.getNumber(), holdNumber, payment);
				   long endTime = System.currentTimeMillis();
				   long elapsedTime = endTime - startTime;

//				   This code currently never runs
				   if (elapsedTime > validationTimeoutWait) { // 20,000 milliseconds = 20 seconds
				      Thread.currentThread().interrupt(); // Terminate the thread if the function takes longer than 20 seconds
				   }else {
//					   System.out.println(elapsedTime);
					   transactionStatus.set(result);
				   }
				});

				t.start();
				
				try {
				   t.join(); // Wait for the thread to finish executing or get interrupted
				   if(transactionStatus.get()) {
//						The payment is done
					   	record.addPayment(payment);
					   	takeMoney();
//						Possibly call to customer IO
						return;
					}else {
						break;
					}
				} catch (InterruptedException e) {

				}
					
		}	
//		If the Bank determines that the transaction should not be authorized, the system will be informed
//		and it will not reduce the remaining amount due.
		cardIssuer.releaseHold(cardData.getNumber(), holdNumber);
		endPayment();
		
	}
	
	public void endPayment() {
		for(PayCardObserver observer : observers) {
			//System.out.println(record.getTotalDue());
			observer.updatedAmount(scs, record.getTotalDue());
		}
		if(cardInserted == true) {
			scs.cardReader.remove();
		}
		scs.cardReader.disable();
	}
	
	/**
	 * Once a payment has been validated and received, it is taken and used in customer Transactions
	 */
	public void takeMoney(){
		if (record.getTotalDue().compareTo(BigDecimal.ZERO) == 0) {
			// modify change parameter when we merge
		   	endPayment();
			receipt.startPrint(record, total, BigDecimal.valueOf(0.0));		
		}else{
			for(PayCardObserver observer : observers) {
				//System.out.println(record.getTotalDue());
				observer.updatedAmount(scs, record.getTotalDue());
			}
		}
	}
	
	
	
	/**
	 * Registers observer with PayCardController so Attendant and customer can be notified.
	 * @param observer
	 */
	public void registerObserver(PayCardObserver observer) {
		observers.add(observer);
	}

	/**
	 * Called by CardReader when card data is read
	 */
	@Override
	public void reactToCardDataReadEvent(CardReader reader, CardData data){
		if (data.getType() == "credit") {
			placeCreditHold(data);
		}else if (data.getType() == "debit") {
			placeDebitHold(data);
		} else if (data.getType() == "gift") {
			attemptGiftPayment(data);
		}
	}
	
	


	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}

	@Override
	public void reactToCardInsertedEvent(CardReader reader) {
		cardInserted = true;
	}
	

	@Override
	public void reactToCardRemovedEvent(CardReader reader) {}

	@Override
	public void reactToCardTappedEvent(CardReader reader) {
	}

	@Override
	public void reactToCardSwipedEvent(CardReader reader) {
	}


	
}
