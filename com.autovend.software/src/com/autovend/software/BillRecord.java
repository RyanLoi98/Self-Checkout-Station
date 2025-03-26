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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



/**
 * Stores and manages information regarding the bill record
 *
 */
public class BillRecord {

	private HashMap<String, Integer> itemsQuantity;
	private HashMap<String, BigDecimal> itemsCost;
	private BigDecimal total;
	private BigDecimal amountPaid;
	private BigDecimal change;
	
	/**
	 * Bill Record Constructor
	 */
	public BillRecord() {
		itemsQuantity = new HashMap<>();
		itemsCost = new HashMap<>();
		total = BigDecimal.valueOf(0.0);
		amountPaid = BigDecimal.valueOf(0.0);
		change = BigDecimal.valueOf(0.0);
	}
	
	
	/**
	 * Adds Item to the bill Record
	 * @param itemName: name of item that was scanned
	 * @param cost: cost of item
	 */
	public void addItemToRecord(String itemName, BigDecimal cost) {
		if (itemsQuantity.containsKey(itemName)) {
			Integer q = itemsQuantity.get(itemName);
			itemsQuantity.put(itemName, q + 1);
		}else {
			itemsCost.put(itemName, cost);
			itemsQuantity.put(itemName, 1);
		}
		CalcTotal();
	}
	/**
	 * Adds Item to the bill Record
	 * @param itemName: name of item that was scanned
	 * @param cost: cost of item
	 * @param quantity: the quantity of the item to be added
	 */
	public void addBarcodedItemToRecord(String itemName, BigDecimal cost, int quantity) {
		if (itemsQuantity.containsKey(itemName)) {
			Integer q = itemsQuantity.get(itemName);
			itemsQuantity.put(itemName, q + quantity);
		}else {
			itemsCost.put(itemName, cost);
			itemsQuantity.put(itemName, quantity);
		}
		CalcTotal();
	}
	

	/**
	 * Removes Items from the bill Record
	 * @param itemName: name of item to be removed
	 * @param cost: cost of item to be removed
	 */
	public void removeItemFromRecord(String itemName) {
		Integer q = itemsQuantity.get(itemName);
		if (q > 1) {
			itemsQuantity.put(itemName, q - 1);
		} else{
			BigDecimal cost = itemsCost.get(itemName);
			itemsCost.remove(itemName);
			itemsQuantity.remove(itemName);
		}
		CalcTotal();
	}
	
	
	/**
	 * Calculates total of all items in Bill Record
	 * @param record: calculates bill record total
	 * @return: total cost
	 */
	private BigDecimal CalcTotal() {
		BigDecimal tempTotal = BigDecimal.valueOf(0.0);
		Set<String> itemSet = this.getItems();
		for (String itemName : itemSet) {
			tempTotal = tempTotal.add(BigDecimal.valueOf(this.getItemQuantity(itemName)).multiply(this.getItemCost(itemName)));
		}
		total = tempTotal;
		return tempTotal;
	}	
	
	/**
	 * Add payment to total amount paid
	 */
	public void addPayment(BigDecimal paymentValue) {
		amountPaid=amountPaid.add(paymentValue);
	}
	
	/**
	 * Gets the amount of money that still needs to be paid.
	 * 
	 * @return The current unpaid total cost due.
	 */
	public BigDecimal getTotalDue() {
		return total.subtract(amountPaid);
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	
	/**
	 * Adds details of payment to Bill record
	 * 
	 * precondition: Customer has Paid in full for their order
	 * 
	 * @param total: total cost of customer's order
	 * @param change: change dispensed to customer
	 */
	public void addPaymentDetails(BigDecimal transactionTotal, BigDecimal transactionChange) {
		total = transactionTotal;
		change = transactionChange;
	}
	
	/**
	 * Get all Items in record
	 * @return: Set of all item names in record

	 */
	public Set<String> getItems() {
		return itemsQuantity.keySet();
	}
	
	/**
	 * Get all Items in record in hashmap form
	 * @return: Set of all item names in record

	 */
	public HashMap<String, BigDecimal> getRecord() {
		return itemsCost;
	}
	
	/**
	 * Returns cost of item if it is in bill record
	 * @param itemName: name of item
	 * @return: cost of item
	 */
	public BigDecimal getItemCost(String itemName) {
		return itemsCost.get(itemName);
	}
	
	public void clearRecord() {
		itemsQuantity = new HashMap<>();
		itemsCost = new HashMap<>();
		total = BigDecimal.valueOf(0.0);
		amountPaid = BigDecimal.valueOf(0.0);
		change = BigDecimal.valueOf(0.0);	
		}
	
	/**
	 * Get Quantity of Item
	 * @param itemName: name of item
	 * @return: integer quantity of item
	 */
	public Integer getItemQuantity(String itemName) {
		return itemsQuantity.get(itemName);
	}
}
