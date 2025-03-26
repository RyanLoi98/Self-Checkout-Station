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

import com.autovend.devices.ElectronicScale;
import com.autovend.devices.SelfCheckoutStation;


/**
 * Observer for addByScanBarcode
 */
public interface AddItemObserver {

	
	/**
	 * Announces that the item was placed in bagging area.
	 * 
	 * @param station The device whose session is in progress.
	 */

	void notWaitingForItem(SelfCheckoutStation station);
	
	/**
	 * Announces that system is waiting for item in bagging area.
	 * 
	 * @param station The device whose session is in progress.
	 */
	void waitingForItem(SelfCheckoutStation station);
	
	/**
	 * Announces that the WeightDiscrepancy occurred to both the customer and the
	 * attendant
	 * 
	 * @param station
	 */
	
	void weightDiscrepancyOccured(SelfCheckoutStation station);
	
	
	/**
	 * Announces that WeightDiscrepancy state is resolved
	 * @param station
	 */
	
	void weightDiscrepancyResolved(SelfCheckoutStation station);

	/**
	 * Announces that the scale is overloaded.
	 * @param scale
	 */
	
	void scaleOverloaded(ElectronicScale scale);
	
	/**
	 * Announces that the scale overload has been resolved
	 * @param station
	 */
	
	void scaleOverloadedResolved(SelfCheckoutStation station);
	
	/**
	 * Announces that the customer's request to add own bags is approved
	 *
	 * @param station
	 *            The device whose session is in progress.
	 */
	
	void noBagsApproved(SelfCheckoutStation station);

		
	/**
	 * Announces that the customer is adding their own bags
	 *
	 * @param station
	 *            The device whose session is in progress.
	 */
	
	void addOwnBagsStart(SelfCheckoutStation station);
	

	/**
	 * Announces that the bag adding process is complete.
	 *
	 * @param station
	 * 			  The device with the session in progress.
	 */
	
	void addOwnBagsComplete(SelfCheckoutStation station);

	
	/**
	 * Announces that the bag adding process is cancelled.
	 *
	 * @param station
	 * 			  The device with the session in progress.
	 */

	void OwnBagsCancelled(SelfCheckoutStation station);

	void updateStation(SelfCheckoutStation station);

	void preventStation(SelfCheckoutStation station);
	void permitStation(SelfCheckoutStation station);



	//void notifyPLUWeightZero(ElectronicScale scale);

}
