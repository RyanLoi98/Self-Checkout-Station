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

public interface CustomerIOListener {
	void pressedPurchaseBags(CustomerIO customer, SelfCheckoutStationLogic logic, int numberOfBags);
	
	void stationHelp(CustomerIO customer, SelfCheckoutStationLogic logic);

	void pressedNoBaggingRequest(CustomerIO customer, SelfCheckoutStationLogic logic);

	void pressedNeedBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic);

	int typedMembership(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String membershipNumber);
	
	void startCashPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic);

	void setCardPaymentType(CustomerIO customer, SelfCheckoutStationLogic scsLogic, MethodOfPayment selectedMethod);
	
	void setCardPaymentAmount(CustomerIO customer, SelfCheckoutStationLogic scsLogic, BigDecimal amount);
	
	void returnToAddFromPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic);
	
	void attemptCardPayment(CustomerIO customer, SelfCheckoutStationLogic scsLogic, String cardType, BigDecimal amountCustomerWishesToPay);

	/**
	 * The customer indicated they finished adding their personal bags to the
	 * bagging area by clicking "done".
	 * 
	 * @param customer The Station IO where this took place
	 * @param scsLogic The Station logic where this took place
	 */
	void pressedDoneAddingOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic);

	/**
	 * The customer indicated they would like to use their own bags.
	 * 
	 * @param customer The Station IO where this took place
	 * @param scsLogic The Station logic where this took place
	 */
	void pressedUseOwnBags(CustomerIO customer, SelfCheckoutStationLogic scsLogic);

	/**
	 * The customer entered the PLU of their desired item.MIGHT BE IRRELEVANT - we
	 * have to show the customer what item they chose so they can confirm their PLU
	 * before adding.
	 * 
	 * @param customer
	 * @param scsLogic
	 * @param plu
	 */
	void enteredPLU(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PriceLookUpCode plu);

	/**
	 * The customer selected a product with a barcode to add .
	 * @param customer The Station IO where this took place
	 * @param scsLogic The Station logic where this took place
	 * @param product The barcoded product that was chosen.
	 */
	//void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, BarcodedProduct product);

	/**
	 * The customer selected a product with a PLU code to add.
	 * @param customer 	The Station IO where this took place
	 * @param scsLogic The Station logic where this took place
	 * @param product The PLU-coded product that was chosen.
	 */
	//void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, PLUCodedProduct product);

	/**
	 * The customer selected a product to add.
	 * @param customer 	The Station IO where this took place
	 * @param scsLogic The Station logic where this took place
	 * @param product The PLU-coded product that was chosen.
	 */
	void selectedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product);

	/**
	 * The customer selected a barcoded product with a given quantity to add.
	 * @param customer 	The Station IO where this took place
	 * @param scsLogic The Station logic where this took place
	 * @param product The PLU-coded product that was chosen.
	 */
	void selectedBarcodedItem(CustomerIO customer, SelfCheckoutStationLogic scsLogic, Product product, int quantity);

	void resetGUI(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic);
}
