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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.CardIssuer;
import com.autovend.software.AttendantCommunicationsController;
import com.autovend.software.SelfCheckoutStationLogic;

import GUIComponents.LoginWindow;
import GUIComponents.SuperUserWindow;


public class SuperUserTest {
	SupervisionStation su;
	AttendantCommunicationsController at;
	SuperUserWindow superUser;
	
	// Variables to use to build self checkout station logics
	int numOfStations;
	
	int membershipLen;
    private HashMap<Integer, ArrayList<String>> memberships;

    // create a currency (Canadian dollars)
    private Currency CAD;
    // create denominations of bills
    int[] billDenominations;
    // create denominations of coins
    BigDecimal[] coinDenominations;

    int maxWeight;
    int sensitivity;
    
    private CardIssuer CreditBank;
	private CardIssuer DebitBank;
    
    ReusableBagDispenser bagDispenser;
    
    ArrayList<SelfCheckoutStationLogic> logicList;
	
	@Before
	public void Setup() {
		su = new SupervisionStation();
		at = new AttendantCommunicationsController();
		superUser = new SuperUserWindow(su, at);
		
		// Set up 15 stations
		numOfStations = 15;
		// Set valid membership length to 5
		membershipLen = 5;
		// Set up membership hashmap
        this.memberships = new HashMap<>();
        memberships.put(12345, new ArrayList<String>(List.of("Peter", "Parker", "100")));
        memberships.put(56789, new ArrayList<String>(List.of("Mary", "Jane", "100")));

        // initialize the currency
        CAD = Currency.getInstance("CAD");
        // initialize denominations of money
        billDenominations = new int[]{5, 10, 20, 50, 100};
        // initialize denominations of coins
        coinDenominations = new BigDecimal[]{BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.25),
                BigDecimal.valueOf(1), BigDecimal.valueOf(2)};
        // initialize maxWeight
        maxWeight = 50;
        // initialize sensitivity
        sensitivity = 1;
        
        CreditBank = new CardIssuer("TD");
        DebitBank = new CardIssuer("CIBC");
        
        at.initializeSelfCheckoutStationLogics(numOfStations, CAD, billDenominations, coinDenominations, maxWeight, 
        		sensitivity, CreditBank, DebitBank, membershipLen, "membershipdatabase.txt");
        
        logicList = at.getStationLogicList();
	}
	
	/**
	 * Ensures users are properly added
	 */
	@Test
	public void testAdd () {
		superUser.selectUserID();
		su.keyboard.type("i");
		superUser.deSelectUserID();
		superUser.selectPassword();
		su.keyboard.type("i");
		superUser.deSelectPassword();
		superUser.getAddButton().doClick();
		assertEquals(superUser.getUsertext(), "i");
		assertEquals(superUser.getPswdtext(), "i");
		assertTrue(superUser.getUserTyped());
		assertTrue(superUser.getPswdTyped());
		assertTrue(LoginWindow.getUsers().containsKey("i"));
	}
	
	/**
	 * Ensures users cannot be added more than once
	 */
	@Test
	public void testAddExists() {
		superUser.selectUserID();
		su.keyboard.type("h");
		superUser.deSelectUserID();
		superUser.selectPassword();
		su.keyboard.type("h");
		superUser.deSelectPassword();
		superUser.getAddButton().doClick();
		superUser.getAddButton().doClick();
		assertTrue(LoginWindow.getUsers().containsKey("h"));
		assertTrue(superUser.getUserExists());
	}
	
	/**
	 * Ensures logout button is working as intended
	 */
	@Test
	public void testSuperUserLogout() {
		superUser.getLogoutButton().doClick();
		assertTrue(superUser.getLoggedout());
	}
	
	/*
	 * Test edit membership length to same length as before, which was 5. Result should notify supervisor that 
	 * no changes were made. Expect no changes to the member database valid length.
	 */
	@Test
	public void testEditMemberLengthUnchanged() {
		// Load in membership database for each logic
        for (int i = 0; i< logicList.size(); i++) {
        	logicList.get(i).loadMemberships();
        }
		// Type in 5 then press the update button.
		superUser.selectMembership();
		su.keyboard.type("5");
		superUser.getMembershipButton().doClick();
		String message = superUser.getMembershipMessage();
		assertEquals("Length already 5!", message);
		
		// Check that the valid membership length of each station logic is still 5.
		for (int i=0; i<logicList.size(); i++) {
			SelfCheckoutStationLogic scsl = logicList.get(i);
			assertEquals(5, scsl.getMembershipLen());
		}
	}
	
	/*
	 * Test edit membership length, attempt to set the length to 0. Expect error message to pop up.
	 * Expect no changes to the member database valid length.
	 */
	@Test
	public void testEditMemberLengthZero() {
		// Load in membership database for each logic
        for (int i = 0; i< logicList.size(); i++) {
        	logicList.get(i).loadMemberships();
        }
        // Type in 0 then press the update button.
		superUser.selectMembership();
		su.keyboard.type("0");
		superUser.getMembershipButton().doClick();
		String message = superUser.getMembershipMessage();
		assertEquals("Error: length must be an int > 0", message);
		
		// Check that the valid membership length of each station logic is still 5.
		for (int i=0; i<logicList.size(); i++) {
			SelfCheckoutStationLogic scsl = logicList.get(i);
			assertEquals(5, scsl.getMembershipLen());
		}
	}
	
	/*
	 * Test edit membership length, attempt to set the length to lower value than current 5. 
	 * Expect success message, and lengths to be updated to 4.
	 */
	@Test
	public void testEditMemberLengthFour() {
		// Load in membership database for each logic
        for (int i = 0; i< logicList.size(); i++) {
        	logicList.get(i).loadMemberships();
        }
        // Type in 4 then press the update button.
		superUser.selectMembership();
		su.keyboard.type("4");
		superUser.getMembershipButton().doClick();
		String message = superUser.getMembershipMessage();
		assertEquals("Success! Old database wiped.", message);
		
		// Check that the valid membership length of each station logic is now 4.
		for (int i=0; i<logicList.size(); i++) {
			SelfCheckoutStationLogic scsl = logicList.get(i);
			assertEquals(4, scsl.getMembershipLen());
		}
	}
	
	/*
	 * Test edit membership length, attempt to set the length to higher value than current 5. 
	 * Expect success message, and lengths to be updated to 6.
	 */
	@Test
	public void testEditMemberLengthSix() {
		// Load in membership database for each logic
        for (int i = 0; i< logicList.size(); i++) {
        	logicList.get(i).loadMemberships();
        }
        
        // Type in 6 then press the update button.
		superUser.selectMembership();
		su.keyboard.type("6");
		superUser.getMembershipButton().doClick();
		String message = superUser.getMembershipMessage();
		assertEquals("Success! Old database wiped.", message);
		
		// Check that the valid membership length of each station logic is now 4.
		for (int i=0; i<logicList.size(); i++) {
			SelfCheckoutStationLogic scsl = logicList.get(i);
			assertEquals(6, scsl.getMembershipLen());
		}
	}
	
	/*
	 * Test edit membership length, attempt to set the length when database was never uploaded.
	 * Expect error message that database could not be wiped, but valid length should be changed before 
	 * wipe is attempted, so valid length should now be 4 instead of 5.
	 */
	@Test
	public void testEditMemberNoDB() {
		// Try to set the length to 4, but haven't loaded in previous databases.
		// Type in 4 then press the update button.
		superUser.selectMembership();
		su.keyboard.type("4");
		superUser.getMembershipButton().doClick();
		String message = superUser.getMembershipMessage();
		assertEquals("Error wiping old database.", message);
		
		// Check that the valid membership length of each station logic is now 4.
		for (int i=0; i<logicList.size(); i++) {
			SelfCheckoutStationLogic scsl = logicList.get(i);
			assertEquals(4, scsl.getMembershipLen());
		}
	}
	
	/*
	 * Test edit membership length, attempt to set the length to 'abc'. Expect error message to pop up.
	 * Expect no changes to the member database valid length since non-integer entered.
	 */
	@Test
	public void testEditMemberLengthCharacters() {
		// Load in membership database for each logic
        for (int i = 0; i< logicList.size(); i++) {
        	logicList.get(i).loadMemberships();
        }
        // Type in 0 then press the update button.
		superUser.selectMembership();
		su.keyboard.type("abc");
		superUser.getMembershipButton().doClick();
		String message = superUser.getMembershipMessage();
		assertEquals("Error: length must be an int > 0", message);
		
		// Check that the valid membership length of each station logic is still 5.
		for (int i=0; i<logicList.size(); i++) {
			SelfCheckoutStationLogic scsl = logicList.get(i);
			assertEquals(5, scsl.getMembershipLen());
		}
	}
	
	@After
	public void Teardown() {
		su = null;
		at = null;
		superUser = null;
	}
}
