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

import java.math.BigDecimal;

import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.software.AddItemObserver;
import com.autovend.software.EnterMembershipObserver;
import com.autovend.software.PayCashObserver;
import com.autovend.software.PrintReceiptObserver;


/**
 * Stub to test Customer I/O
 *
 */
public class CustomerStub implements PayCashObserver, AddItemObserver, PrintReceiptObserver, EnterMembershipObserver{

	private boolean wait_item_Flag = false;
	private boolean place_item_Flag = false;
	private BigDecimal billAmount = BigDecimal.valueOf(0.0);
	private boolean sessionOver = false;
	private boolean bagsStarted = false;
	
	private boolean weightDiscrepancyFlag = false;
	private boolean scaleOverloadFlag = false;
	private boolean bagsAdded  = false;
	private boolean bagsComplete = false;
	private boolean bagsCancelled  = false;

	private boolean databaseCorrupt = false;
	private int Cpoints  = 0;




	public void placeItemInBagging(SelfCheckoutStation station) {
		place_item_Flag = true;
	}

	@Override
	public void updatedAmount(SelfCheckoutStation station, BigDecimal amount) {
		billAmount = amount;
	}

	@Override
	public void sessionComplete(SelfCheckoutStation station) {
		sessionOver = true;
	}

	@Override
	public void requiresMaintenance(SelfCheckoutStation station, String message) {
		// TODO Auto-generated method stub
		
	}
	public boolean isWaitingItemFlag() {
		return wait_item_Flag;
	}

	public boolean isWeightDiscrepancy() {
		return weightDiscrepancyFlag;
	}
	
	public boolean isScaleOverloaded() {
		return scaleOverloadFlag;
	}

	public boolean isPlace_item_Flag() {
		return place_item_Flag;
	}

	public BigDecimal getBillAmount() {
		return billAmount;
	}

	public boolean isSessionOver() {
		return sessionOver;
	}

	@Override
	public void insufficientChange(SelfCheckoutStation station, BigDecimal change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowPaper(SelfCheckoutStation station, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lowInk(SelfCheckoutStation station, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void waitingForItem(SelfCheckoutStation station) {
			wait_item_Flag = true;
	}

	@Override
	public void notWaitingForItem(SelfCheckoutStation station) {
			wait_item_Flag = false;		
	}

	@Override
	public void weightDiscrepancyOccured(SelfCheckoutStation station) {
		weightDiscrepancyFlag = true;				
	}

	@Override
	public void weightDiscrepancyResolved(SelfCheckoutStation station) {
		weightDiscrepancyFlag = false;		
	}

	public void displayVisualCatalogue(SelfCheckoutStation station) {
		// TODO Auto-generated method stub
		
	}

	public void scaleOverloaded(SelfCheckoutStation station) {
		scaleOverloadFlag = true;		
	}

	@Override
	public void scaleOverloadedResolved(SelfCheckoutStation station) {
		scaleOverloadFlag = false;		
	}

	@Override
	public void noBagsApproved(SelfCheckoutStation station) {

	}

	@Override
	public void addOwnBagsStart(SelfCheckoutStation station) {
		bagsStarted =true;
		bagsComplete = false;
		bagsAdded = false;
	}

	public void addOwnBagsAdded(SelfCheckoutStation station) {
		bagsAdded =true;
	}

	@Override
	public void addOwnBagsComplete(SelfCheckoutStation station) {
		bagsComplete = true;
	}

	public boolean isBagsAdded() {
		return bagsAdded;
	}

	public boolean isBagsComplete() {
		return bagsComplete;
	}

	public boolean isBagsStarted() {
		return bagsStarted;
	}


	public boolean isBagsCancelled() {
		return bagsCancelled;
	}

	@Override
	public void OwnBagsCancelled(SelfCheckoutStation station) {
		bagsCancelled = true;
		
	}

	@Override
	public void updatePoints(int points) {
		Cpoints = points;
	}

	@Override
	public void isDataBaseCorrupt(boolean status) {
		this.databaseCorrupt = status;
	}

	public boolean getDataBaseStatus(){return this.databaseCorrupt;}

	public int getPoints() {
		return Cpoints;
	}

	@Override
	public void memberCardEvent(String cardNumber) {
		
	}

	@Override
	public void swipeAttemptEvent() {
		
	}

	@Override
	public void scaleOverloaded(ElectronicScale scale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStation(SelfCheckoutStation station) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preventStation(SelfCheckoutStation station) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void permitStation(SelfCheckoutStation station) {
		// TODO Auto-generated method stub
		
	}
	
	

}
