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

package com.autovend.demo;

import com.autovend.BarcodedUnit;
import com.autovend.Bill;
import com.autovend.Card;
import com.autovend.Coin;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.devices.DisabledException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;

public class customerActions {

	// Simulates scanning an item
	public void scanItem(SelfCheckoutStation station, BarcodedUnit item) {
		System.out.println("Scanning Item: " + item);
		while(station.mainScanner.scan(item) == false);
	}
	// Simulates bagging an item
	public void bagItem(SelfCheckoutStation station, BarcodedUnit item) {
		station.baggingArea.add(item);
	}
	// Simulates removing an item
	public void removeItem(SelfCheckoutStation station, BarcodedUnit item) {
		station.baggingArea.remove(item);
	}
	// Simulates bagging a PLU item
	public void bagPLUItem(SelfCheckoutStation station, PriceLookUpCodedUnit item) {
		station.baggingArea.add(item);
	}
	// Simulates bagging a PLU item
	public void placePLUItem(SelfCheckoutStation station, PriceLookUpCodedUnit item) {
		station.scale.add(item);
	}
	// Simulates removing a PLU item from bagging area
	public void removePLUItem(SelfCheckoutStation station, PriceLookUpCodedUnit item) {
		station.baggingArea.remove(item);
	}
	// Simulates inserting a card of some kind
	public void insertCard(SelfCheckoutStation station, Card card) {
		try {
			station.cardReader.insert(card,  "4321");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	// Simulates inserting a bill of some kind
	public void insertBill(SelfCheckoutStation station, Bill bill) {
		try {
			station.billInput.accept(bill);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Simulates inserting a coin of some kind
	public void insertCoin(SelfCheckoutStation station, Coin coin) {
		try {
			station.coinSlot.accept(coin);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
