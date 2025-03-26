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

import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.SellableUnit;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.SelfCheckoutStationLogic.MethodOfPayment;

import GUIComponents.AttendantIO;

public class CommunicationsController {
	private static CommunicationsController communicator = new CommunicationsController();
	private static AttendantIO attendant;

	private CommunicationsController() {
	}

	/*
	 * This communications controller will listen to ALL customer GUIs and the
	 * single attendant GUI. It is a singleton class; there is only ever one
	 * communications controller, and it listens to EVERYTHING.
	 * 
	 * The purpose of the class is to listen for events from the GUIs AND invoke
	 * methods on the GUIs. It has two-way communication with CIO and two-way
	 * communication with AIO, AND, acts as a middleman between CIO and AIO.
	 */
	public static CommunicationsController getCommunicator() {
		return communicator;
	}

	public void registerOntoCustomers(CustomerIO... customers) {
		CListener cListener = new CListener();
		for (CustomerIO customer : customers) {
			customer.register(cListener);
		}
	}

	public void registerOntoAttendant(AttendantIO att) {
		AListener aListener = new AListener();
		//att.register(aListener);
		attendant = att;
	}

	/*
	 * For any "communication" you want the customerIO to do, simply have it
	 * announce an event on observers, and then react to that event in this
	 * listener. This listener will "invoke" a method call or some sort of
	 * communication with the main SelfCheckoutStationLogic. Is it redundant? Sure.
	 * But, it keeps the GUI classes from doing too much. We are
	 * "dividing and conquering."
	 */
	private class CListener implements CustomerIOListener {
		@Override
		public void pressedPurchaseBags(CustomerIO customer, SelfCheckoutStationLogic logic, int numberOfBags) {
			logic.CustomerPurchaseBags(numberOfBags);
			customer.bagsPurchasedComplete(numberOfBags);
		}

		@Override
		public void pressedNoBaggingRequest(CustomerIO customer, SelfCheckoutStationLogic logic) {
			System.out.println("works");
			//logic.noBaggingRequestApproved();
			attendant.noBaggingRequestInProgress(customer, logic);
		}

		@Override
		public void pressedNeedBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
			attendant.stationNeedsBags(customerIO, scsLogic);
		}

		@Override
		public void attemptCardPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String cardType,
				BigDecimal amount) {
			System.out.println("Notifying PayCardController of customer payment attempt using " + cardType
					+ " card and amount = " + amount);
			if (cardType.equals("credit")) {
				scsLogic.CustomerCreditPay(amount);
			} else if (cardType.equals("debit")) {
				scsLogic.CustomerDebitPay(amount);

			} else if (cardType.equals("gift")) {
				scsLogic.CustomerGiftPay(amount);
			}

		}

		@Override
		public void pressedDoneAddingOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
			attendant.ownBagsVerify(scsLogic);
			// attendant.validateOwnBags(customer, scsLogic); -- TO BE CREATED: Attendant
			// prompted to check bagging area and either confirm or deny continuation of
			// current session.
		}

		@Override
		public void pressedUseOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
			scsLogic.CustomerAddOwnBagsStart();

		}

		@Override
		public void enteredPLU(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PriceLookUpCode plu) {
			scsLogic.enteredPLU(plu);
		}

		/**
		 * Deals with events when customers enter in their membership number by typing
		 * 
		 * @param customer: the customer io that had a membership number entered in by
		 *                  typing
		 * @param scsLogic: the self checkout station logic to deal with this membership
		 *                  number
		 * @return: status code on operation
		 */
		@Override
		public int typedMembership(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String membershipNumber) {
			return scsLogic.validateMembershipNumber(membershipNumber);
		}

		@Override
		public void startCashPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
			scsLogic.setSelectedPayment(MethodOfPayment.CASH);
			scsLogic.CustomerPay();
		}

		@Override
		public void setCardPaymentType(CustomerIO customer, SelfCheckoutStationLogic scsLogic,
				MethodOfPayment selectedMethod) {
			scsLogic.setSelectedPayment(selectedMethod);
			System.out.println(scsLogic.getSelectedPayment());
		}

		@Override
		public void setCardPaymentAmount(CustomerIO customer, SelfCheckoutStationLogic scsLogic, BigDecimal amount) {

			if (scsLogic.getSelectedPayment() == MethodOfPayment.CREDIT) {
				scsLogic.CustomerCreditPay(amount);
			} else if (scsLogic.getSelectedPayment() == MethodOfPayment.DEBIT) {
				scsLogic.CustomerDebitPay(amount);
			} else {
				scsLogic.CustomerGiftPay(amount);
			}
		}

		@Override
		public void returnToAddFromPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic) {
			scsLogic.partialPayment();
		}

		//@Override
		//public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, BarcodedProduct product) {
		//	scsLogic.itemPicked(product);

		//}

		//@Override
		//public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PLUCodedProduct product) {
		//	scsLogic.itemPicked(product);

		//}
		@Override
		public void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product) {
			if (product instanceof PLUCodedProduct) {
				scsLogic.itemPicked(product);
			} else if (product instanceof BarcodedProduct) {
				scsLogic.itemPicked(product);
			}
		}

		@Override
		public void selectedBarcodedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product, int quantity) {
			scsLogic.barcodedItemPicked((BarcodedProduct) product, quantity);
		}

		@Override
		public void stationHelp(CustomerIO customer, SelfCheckoutStationLogic logic) {
			attendant.stationHelp(logic); 
		}

		@Override
		public void resetGUI(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
			scsLogic.sessionEnd();
			scsLogic.sessionStart();
			
		}

	}

	private class AListener implements AttendantIOListener {
		@Override
		public void noBaggingRequestApproved(CustomerIO customer, SelfCheckoutStationLogic logic) {
			logic.noBaggingRequestApproved();
			customer.notWaitingToBagItem();
		}

		@Override
		public void ownBagsApproved(CustomerIO customer, SelfCheckoutStationLogic logic) {
			logic.CustomerAddOwnBagsApproved();
		}

		@Override
		public void selectedItemByText(CustomerIO customer, SelfCheckoutStationLogic logic, BarcodedProduct product) {
			logic.itemPicked(product);
		}

		@Override
		public void selectedItemByText(CustomerIO customer, SelfCheckoutStationLogic logic, PLUCodedProduct product) {
			logic.itemPicked(product);

		}

	}

}