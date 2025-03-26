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
import com.autovend.software.PayCashObserver;
import com.autovend.software.PrintReceiptObserver;


/**
 * Stub to test Attendant I/O
 *
 */
public class AttendantStub implements PrintReceiptObserver, PayCashObserver, AddItemObserver{

	private boolean maintaince_required = false;
	private boolean insufficientChange = false;
	private boolean discrepnancyOccured = false;
	private boolean lowInkFlag = false;
	private boolean lowPaperFlag = false;
	
	public boolean isDiscrepnancyOccured() {
		return discrepnancyOccured;
	}

	@Override
	public void sessionComplete(SelfCheckoutStation station) {}

	@Override
	public void requiresMaintenance(SelfCheckoutStation station, String message) {
		maintaince_required = true;		
	}

	public boolean isMaintaince_required() {
		return maintaince_required;
	}

	@Override
	public void updatedAmount(SelfCheckoutStation station, BigDecimal amount) {}

	@Override
	public void insufficientChange(SelfCheckoutStation station, BigDecimal change) {
		insufficientChange = true;		
	}

	public boolean isInsufficientChange() {
		return insufficientChange;
	}

	@Override
	public void lowPaper(SelfCheckoutStation station, String message) {
		lowPaperFlag = true;
		
	}

	@Override
	public void lowInk(SelfCheckoutStation station, String message) {
		lowInkFlag = true;		
	}

	public void placeItemInBagging(SelfCheckoutStation station) {}

	@Override
	public void waitingForItem(SelfCheckoutStation station) {}

	@Override
	public void notWaitingForItem(SelfCheckoutStation station) {}

	@Override
	public void weightDiscrepancyOccured(SelfCheckoutStation station) {
			discrepnancyOccured = true;

	}

	@Override
	public void weightDiscrepancyResolved(SelfCheckoutStation station) {
			discrepnancyOccured = false;
	}

	public void displayVisualCatalogue(SelfCheckoutStation station) {}

	public void scaleOverloaded(SelfCheckoutStation station) {}

	@Override
	public void scaleOverloadedResolved(SelfCheckoutStation station) {}

	@Override
	public void noBagsApproved(SelfCheckoutStation station) {

	}

	@Override
	public void addOwnBagsStart(SelfCheckoutStation station) {}

	public void addOwnBagsAdded(SelfCheckoutStation station) {}

	@Override
	public void addOwnBagsComplete(SelfCheckoutStation station) {}

	@Override
	public void OwnBagsCancelled(SelfCheckoutStation station) {}

	@Override
	public void updateStation(SelfCheckoutStation station) {}

	public boolean isLowInk() {
		return lowInkFlag;
	}
	
	public boolean isLowPaper() {
		return lowPaperFlag ;
	}

	@Override
	public void scaleOverloaded(ElectronicScale scale) {
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
