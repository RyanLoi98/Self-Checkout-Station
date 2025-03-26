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
import java.util.ArrayList;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.ReusableBagDispenserObserver;
import com.autovend.ReusableBag;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.EmptyException;
import com.autovend.devices.ReusableBagDispenser;

public class PurchaseBagsController implements ReusableBagDispenserObserver {
	public static final BigDecimal BAG_COST = new BigDecimal(0.05);
	public static final String BAG_NAME = "ReusableBag";
	
	private SelfCheckoutStation station;
	private SelfCheckoutStationLogic logic;
	private int numBagsInDispenser = 0;
	
	/* for testing purposes */
	boolean outOfBagsFlag = false;
	boolean bagsAdd = false;
	
	private ReusableBagDispenser bDispenser;
	
    /*
     * Dispenser received must INITIALLY be empty (software cannot figure out initial quantity added before
     * it registered itself as an observer).
     */
	public PurchaseBagsController(SelfCheckoutStation scs, SelfCheckoutStationLogic scsLogic) {
		station = scs;
		logic = scsLogic;
		bDispenser = scs.bagDispenser;
		bDispenser.register(this);
	}
	
    /**
     * Precondition: System is ready to detect weight discrepancies.
     * 
     * Adds reusable bags to the customer's order. Bags are treated
     * as a regular item.
     * 
     * @param quantity: number of bags to purchase
     */
    public void addReusableBags(int quantity) {
    	// get the logic to tell the addItemsController to add a bag; weight Discrepancy
    	// will be handled by the addItem logic as well
        for (int i = 0; i < quantity; i++) {
        	try {
				ReusableBag bag = bDispenser.dispense();
				logic.addBag(BAG_NAME, BAG_COST, bag.getWeight());
			} catch (EmptyException e) {
				/*
				 * WILL NEVER RUN! GUI prevents adding more bags than are in the
				 * dispenser.
				 */
				e.printStackTrace();
			}
        }
    }
    
    /*
     * Getters for unit testing.
     */  
    public int getNumBagsInDispenser() {
    	return numBagsInDispenser;
    }
    
    public boolean getOutOfBagsFlag() {
    	return outOfBagsFlag;
    }

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}

	@Override
	public void bagDispensed(ReusableBagDispenser dispenser) {
		numBagsInDispenser--;
	}

	@Override
	public void outOfBags(ReusableBagDispenser dispenser) {
		outOfBagsFlag = true;
	}

	@Override
	public void bagsLoaded(ReusableBagDispenser dispenser, int count) {
		numBagsInDispenser += count;
		outOfBagsFlag = false;
	}
}

