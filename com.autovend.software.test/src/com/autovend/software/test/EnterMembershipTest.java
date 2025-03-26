package com.autovend.software.test;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.CreditCard;
import com.autovend.GiftCard;
import com.autovend.MagneticStripeFailureException;
import com.autovend.MembershipCard;
import com.autovend.Numeral;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.CardIssuer;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.CommunicationsController;
import com.autovend.software.CustomerIO;
import com.autovend.software.EnterMembership;
import com.autovend.software.EnterMembershipObserver;
import com.autovend.software.SelfCheckoutStationLogic;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;

import static org.junit.Assert.*;


/**
 * Test class for the EnterMembership use case
 */
public class EnterMembershipTest {
    // variables
    int membershipLen;
    private HashMap<Integer, ArrayList<String>> memberships;
    // memberships2 will be used solely for save file restoration back to default conditions
    private HashMap<Integer, ArrayList<String>> memberships2;

    CustomerStub customer;

    // creating a selfcheckout station logic to use for our testing, as all future communications
    // will be through the selfcheckout station logic
    SelfCheckoutStationLogic enterMembership;

    SelfCheckoutStation station;

    // create a currency (Canadian dollars)
    private Currency CAD;
    // create denominations of bills
    int[] billDenominations;
    // create denominations of coins
    BigDecimal[] coinDenominations;

    int maxWeight;
    int sensitivity;
    
    MembershipCard memberCard;
    
    private CardIssuer CreditBank;
	private CardIssuer DebitBank;
	
    private SelfCheckoutStationLogic scsl;
    private CustomerIO cio;

    /**
     * Initialize all the variables before each test
     */
    @Before
    public void setUp() throws Exception {
        membershipLen = 5;
        this.memberships = new HashMap<>();
        memberships.put(12345, new ArrayList<String>(List.of("Peter", "Parker", "100")));
        memberships.put(56789, new ArrayList<String>(List.of("Mary", "Jane", "100")));

        // memberships2 will be used solely for save file restoration back to default conditions
        this.memberships2 = new HashMap<>();
        memberships2.put(12345, new ArrayList<String>(List.of("Peter", "Parker", "100")));
        memberships2.put(56789, new ArrayList<String>(List.of("Mary", "Jane", "100")));

        customer = new CustomerStub();

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
        // initialize self-checkout stations
        station = new SelfCheckoutStation(CAD, billDenominations, coinDenominations, maxWeight, sensitivity);
        
        
        // Create credit bank and credit card for use in 100% instruction coverage
        CreditBank = new CardIssuer("TD");
        
        enterMembership = new SelfCheckoutStationLogic(station, CreditBank, null, membershipLen, "membershipdatabase.txt", 0, 0);

        // initialize our self check out station logic
        //enterMembership.createEnterMembership(membershipLen, memberships);
        enterMembership.registerEnterMemberObserver(customer);
        
        
        // Create a test membership card to use
        memberCard = new MembershipCard("membership", "12345", "Peter Parker", false);
        
        cio = new CustomerIO(enterMembership);
        cio.getFrame().setVisible(false);
        
        CommunicationsController.getCommunicator().registerOntoCustomers(cio);
        
        enterMembership.loadMemberships();
    }

    /**
     * Tear down all the variables after each test
     */
    @After
    public void tearDown() throws Exception {
        // resetting the file back to original conditions using membership2
        enterMembership.updateMemberships(memberships2);
        memberships2 = null;
        membershipLen = 0;
        memberships = null;
        enterMembership = null;
        customer = null;
        CAD = null;
        billDenominations = null;
        coinDenominations = null;
        maxWeight =0;
        sensitivity = 0;

        station = null;
        scsl = null;
        cio = null;
        
    }

    /**
     * Test to see if register observers is able to add a new customer2
     */
    @Test
    public void registerObserver() {
        CustomerStub customer2 = new CustomerStub();
        assertFalse(enterMembership.getObservers().contains(customer2));
        enterMembership.registerEnterMemberObserver(customer2);
        assertTrue(enterMembership.getObservers().contains(customer2));
    }

    /**
     * Test to see if deregister observers is able to remove customer
     */
    @Test
    public void deregisterObserver() {
        assertTrue(enterMembership.getObservers().contains(customer));
        enterMembership.deregisterEnterMemberObserver(customer);
        assertFalse(enterMembership.getObservers().contains(customer));
    }

    /**
     * Test alerting the observer with 100 points
     */
    @Test
    public void updatePoints() {
        enterMembership.updatePoints(100);
        assertEquals(100, customer.getPoints());
    }

    /**
     * Test changing membership length to 5, the returned value should also be 0 for operation success
     */
    @Test
    public void editMembershipLen() {
        int result = enterMembership.editMembershipLen(6);
        assertEquals(6, enterMembership.getMembershipLen());
        assertEquals(0, result);
    }

    /**
     * Test changing membership length to 0 (should fail), the returned value should also be -1 for operation failure
     */
    @Test
    public void editMembershipLen2() {
        int result = enterMembership.editMembershipLen(0);
        // membership length should stay the same
        assertEquals(5, enterMembership.getMembershipLen());
        assertEquals(-1, result);
    }

    /**
     * Test changing membership length to -1 (should fail), the returned value should also be -1 for operation failure
     */
    @Test
    public void editMembershipLen3() {
        int result = enterMembership.editMembershipLen(-1);
        // membership length should stay the same
        assertEquals(5, enterMembership.getMembershipLen());
        assertEquals(-1, result);
    }

    /**
     * Test if editMembershipLen also wipes the database
     */
    @Test
    public void editMembershipLen4() {
        int result = enterMembership.editMembershipLen(10);
        assertEquals(10, enterMembership.getMembershipLen());
        assertEquals(0, result);
        assertEquals(0, enterMembership.getMemberships().size());
    }


    /**
     * Test updating Peter parker's (member id 12345) points from 100 to -, returned value should be 0 for operation success
     */
    @Test
    public void editIndivPoints() {
        assertEquals(100,enterMembership.getPoints(12345));
        int result = enterMembership.editIndivPoints(12345, 0);
        assertEquals(0,enterMembership.getPoints(12345));
        assertEquals(0, result);
    }


    /**
     * Test updating Peter parker's (member id 12345) points from 100 to 200, returned value should be 0 for operation success
     */
    @Test
    public void editIndivPoints1() {
        assertEquals(100,enterMembership.getPoints(12345));
        int result = enterMembership.editIndivPoints(12345, 200);
        assertEquals(200,enterMembership.getPoints(12345));
        assertEquals(0, result);
    }

    /**
     * Test updating points but with an invalid membership number
     * which is less than the required 5 digits. The returned value should be -1 for invalid membership
     */
    @Test
    public void editIndivPoints2() {
        int result = enterMembership.editIndivPoints(1234, 200);
        assertEquals(-1, result);
    }

    /**
     * Test updating points but with an invalid membership number
     * which is greater than the required 5 digits. The returned value should be -1 for invalid membership
     */
    @Test
    public void editIndivPoints3() {
        int result = enterMembership.editIndivPoints(123456, 200);
        assertEquals(-1, result);
    }

    /**
     * Test updating points but with an invalid membership number
     * which is a negative number. The returned value should be -1 for invalid membership
     */
    @Test
    public void editIndivPoints4() {
        int result = enterMembership.editIndivPoints(-12345, 200);
        assertEquals(-1, result);
    }

    /**
     * Test updating Peter parker's (member id 12345) points from 100 with a negative points amount returned value should
     * be -2 for invalid points
     */
    @Test
    public void editIndivPoints5() {
        assertEquals(100,enterMembership.getPoints(12345));
        int result = enterMembership.editIndivPoints(12345, -100);
        // points shouldn't change
        assertEquals(100,enterMembership.getPoints(12345));
        assertEquals(-2, result);
    }

    /**
     * Test updating Points for a membership id which is not in the system, result should be -3 returned
     * for membership isn't in the database.
     */
    @Test
    public void editIndivPoints6() {
        int result = enterMembership.editIndivPoints(11111, 100);
        assertEquals(-3, result);
    }

    /**
     * Test adding a new membership with all valid fields, result should be 0 for operational success
     */
    @Test
    public void addMembership() {
        assertFalse(enterMembership.isMember(12121));
        int result = enterMembership.addMembership(12121, "Eddie", "Brock", 1000);
        // test to see if added successfully
        assertTrue(enterMembership.isMember(12121));
        assertEquals(0, result);
    }

    /**
     * Test adding a new membership with all valid fields (0 points), result should be 0 for operational success
     */
    @Test
    public void addMembership1() {
        assertFalse(enterMembership.isMember(12121));
        int result = enterMembership.addMembership(12121, "Eddie", "Brock", 0);
        // test to see if added successfully
        assertTrue(enterMembership.isMember(12121));
        assertEquals(0, result);
    }

    /**
     * Test adding a new membership with invalid membership number (too short), result should be -1 for
     * invalid membership number
     */
    @Test
    public void addMembership2() {
        assertFalse(enterMembership.isMember(1222));
        int result = enterMembership.addMembership(1222, "Eddie", "Brock", 1000);
        // testing to see if member was not added due to invalid membership number
        assertFalse(enterMembership.isMember(1222));
        assertEquals(-1, result);
    }

    /**
     * Test adding a new membership with invalid membership number (too long), result should be -1 for
     * invalid membership number
     */
    @Test
    public void addMembership3() {
        assertFalse(enterMembership.isMember(122111));
        int result = enterMembership.addMembership(122111, "Eddie", "Brock", 1000);
        // testing to see if member was not added due to invalid membership number
        assertFalse(enterMembership.isMember(122111));
        assertEquals(-1, result);
    }

    /**
     * Test adding a new membership with invalid membership number (negative), result should be -1 for
     * invalid membership number
     */
    @Test
    public void addMembership4() {
        assertFalse(enterMembership.isMember(-11221));
        int result = enterMembership.addMembership(-11221, "Eddie", "Brock", 1000);
        // testing to see if member was not added due to invalid membership number
        assertFalse(enterMembership.isMember(-11221));
        assertEquals(-1, result);
    }

    /**
     * Test adding a new membership with non-unique membership number (already in database), result should be -2 for
     * already in the system. We will use Peter Parker's membership number (12345)
     */
    @Test
    public void addMembership5() {
        assertTrue(enterMembership.isMember(12345));
        int result = enterMembership.addMembership(12345, "Eddie", "Brock", 1000);
        assertEquals(-2, result);
    }

    /**
     * Test adding a new membership with negative points, result should be -3 for
     * error due to negative points. All other fields will be valid
     */
    @Test
    public void addMembership6() {
        assertFalse(enterMembership.isMember(12245));
        int result = enterMembership.addMembership(12245, "Eddie", "Brock", -1);
        // testing to see if member was not added due to invalid points
        assertFalse(enterMembership.isMember(12245));
        assertEquals(-3, result);
    }

    /**
     * Test removing a membership, we will remove Peter Parker (membership number 12345), expected result 0 for success
     */
    @Test
    public void removeMembership() {
        assertTrue(enterMembership.isMember(12345));
        int result = enterMembership.removeMembership(12345);
        // testing for membership which should be removed
        assertFalse(enterMembership.isMember(12345));
        assertEquals(0, result);
    }

    /**
     * Test removing a membership, we will use an invalid membership number (too short), expected result
     * -1 for invalid membership number
     */
    @Test
    public void removeMembership2() {
        int result = enterMembership.removeMembership(1234);
        assertEquals(-1, result);
    }

    /**
     * Test removing a membership, we will use an invalid membership number (too long), expected result
     * -1 for invalid membership number
     */
    @Test
    public void removeMembership3() {
        int result = enterMembership.removeMembership(123456);
        assertEquals(-1, result);
    }

    /**
     * Test removing a membership, we will use an invalid membership number (negative), expected result
     * -1 for invalid membership number
     */
    @Test
    public void removeMembership4() {
        int result = enterMembership.removeMembership(-11234);
        assertEquals(-1, result);
    }

    /**
     * Test removing a membership, we will use a membership number that isn't in the database, expected result
     * -2 for membership number not yet in the database
     */
    @Test
    public void removeMembership5() {
        int result = enterMembership.removeMembership(12334);
        assertEquals(-2, result);
    }

    /**
     *Test to see if get points returns Peter Parker's points (member number 12345, and 100 points)
     * result should be 100 for his points
     */
    @Test
    public void getPoints() {
        assertEquals(100,enterMembership.getPoints(12345));
    }

    /**
     *Test to get points with invalid membership number (too short)
     * result should be -1 for invalid membership number
     */
    @Test
    public void getPoints2() {
        assertEquals(-1,enterMembership.getPoints(1234));
    }

    /**
     *Test to get points with invalid membership number (too long)
     * result should be -1 for invalid membership number
     */
    @Test
    public void getPoints3() {
        assertEquals(-1,enterMembership.getPoints(123456));
    }

    /**
     *Test to get points with invalid membership number (negative number)
     * result should be -1 for invalid membership number
     */
    @Test
    public void getPoints4() {
        assertEquals(-1,enterMembership.getPoints(-11234));
    }

    /**
     *Test to get points with membership number that isn't in the system
     * result should be -1 for invalid membership number
     */
    @Test
    public void getPoints5() {
        assertEquals(-2,enterMembership.getPoints(12333));
    }

    /**
     *Test to get points when somehow there are invalid points (points are characters) this should
     * be impossible if the preconditions are met
     * result should be -3 for invalid points
     */
    @Test
    public void getPoints6() {
        // create invalid points entry
        memberships.put(99999, new ArrayList<String>(List.of("Mary", "Jane", "abc")));
        enterMembership.updateMemberships(memberships);
        assertEquals(-3, enterMembership.getPoints(99999));
    }

    /**
     * Get Peter Parker's first name (membership number 12345), result should be Peter
     */
    @Test
    public void getFName() {
        assertEquals("Peter", enterMembership.getFName(12345));
    }

    /**
     * Test getFName with a membership number that is not in the database, result should be 2 for
     * that error condition.
     */
    @Test
    public void getFName2() {
        assertEquals("2", enterMembership.getFName(12333));
    }

    /**
     * Test getFName with an invalid membership number (too short) result
     * should be 1 for invalid membership number
     */
    @Test
    public void getFName3() {
        assertEquals("1", enterMembership.getFName(1233));
    }

    /**
     * Test getFName with an invalid membership number (too long) result
     * should be 1 for invalid membership number
     */
    @Test
    public void getFName4() {
        assertEquals("1", enterMembership.getFName(123333));
    }

    /**
     * Test getFName with an invalid membership number (negative) result
     * should be 1 for invalid membership number
     */
    @Test
    public void getFName5() {
        assertEquals("1", enterMembership.getFName(-12333));
    }

    /**
     * Test getting Peter Parker's (membership number 12345) last name, result should be Parker
     */
    @Test
    public void getLName() {
        assertEquals("Parker", enterMembership.getLName(12345));
    }

    /**
     * Test getLName with a membership number that is not in the database, result should be 2 for
     * that error condition.
     */
    @Test
    public void getLName2() {
        assertEquals("2", enterMembership.getLName(12333));
    }

    /**
     * Test getLName with an invalid membership number (too short) result
     * should be 1 for invalid membership number
     */
    @Test
    public void getLName3() {
        assertEquals("1", enterMembership.getLName(1233));
    }

    /**
     * Test getLName with an invalid membership number (too long) result
     * should be 1 for invalid membership number
     */
    @Test
    public void getLName4() {
        assertEquals("1", enterMembership.getLName(121133));
    }

    /**
     * Test getLName with an invalid membership number (negative) result
     * should be 1 for invalid membership number
     */
    @Test
    public void getLName5() {
        assertEquals("1", enterMembership.getLName(-11133));
    }

    /**
     * Test is member with membership id in the system (12345), result should be true
     */
    @Test
    public void isMember() {
        assertTrue(enterMembership.isMember(12345));
    }

    /**
     * Test is member with membership id not in the system (22345), result should be false
     */
    @Test
    public void isMember2() {
        assertFalse(enterMembership.isMember(22345));
    }

    /**
     * Test getMembershipLen, result should be 5 as that is the current setting
     */
    @Test
    public void getMembershipLen() {
        assertEquals(5, enterMembership.getMembershipLen());
    }

    /**
     * Test function that calculates the number of digits in an integer, we will use 12345, result should
     * be 5 for the 5 digits
     */
    @Test
    public void lenOfInt() {
        assertEquals(5, enterMembership.lenOfInt(12345));
    }
    

    /**
     * Test entering a valid membership number string ("12345") that belongs to peter parker
     * result should be 0, and observer has peter's 100 points
     */
    @Test
    public void enterMembershipNumber() {
        assertEquals(0, enterMembership.validateMembershipNumber("12345"));
        assertEquals(100,customer.getPoints());
    }

    /**
     * Test entering a cancel into entermembership number, result should be -1
     */
    @Test
    public void enterMembershipNumber1() {
        assertEquals(-1, enterMembership.validateMembershipNumber("cancel"));
    }

    /**
     * Test entering a ninvalid membership number containing character into entermembership number, result should be -2
     * for invalid membership number
     */
    @Test
    public void enterMembershipNumber2() {
        assertEquals(-2, enterMembership.validateMembershipNumber("12a35"));
    }

    /**
     * Test entering an invalid membership number (too short) into entermembership number, result should be -2
     * for invalid membership number
     */
    @Test
    public void enterMembershipNumber3() {
        assertEquals(-2, enterMembership.validateMembershipNumber("1234"));
    }

    /**
     * Test entering an invalid membership number (too long) into entermembership number, result should be -2
     * for invalid membership number
     */
    @Test
    public void enterMembershipNumber4() {
        assertEquals(-2, enterMembership.validateMembershipNumber("123456"));
    }

    /**
     * Test entering an invalid membership number (negative) into entermembership number, result should be -2
     * for invalid membership number
     */
    @Test
    public void enterMembershipNumber5() {
        assertEquals(-2, enterMembership.validateMembershipNumber("-11233"));
    }

    /**
     * Test entering a valid membership number but not in the database into entermembership number, result should be -3
     * for not in database
     */
    @Test
    public void enterMembershipNumber6() {
        assertEquals(-3, enterMembership.validateMembershipNumber("12334"));
    }


    /**
     * Test entering a valid membership number but the rare occurence of points being invalid (contains a character)
     * this should never happen in practice if the database preconditions are met. Result should be -4
     */
    @Test
    public void enterMembershipNumber7() {
        // create invalid points entry
        memberships.put(99999, new ArrayList<String>(List.of("Mary", "Jane", "abc")));
        enterMembership.updateMemberships(memberships);
        assertEquals(-4, enterMembership.validateMembershipNumber("99999"));
    }
    
    /**
     * Test entering an invalid membership number (negative) into entermembership number, that is also an invalid length, 
     * result should be -2 for invalid membership number
     */
    @Test
    public void enterMembershipNumber8() {
        assertEquals(-2, enterMembership.validateMembershipNumber("-112333"));
        
    }

    /**
     * Testing the update memberships setter
     */
    @Test
    public void testUpdateMemberships(){
        // create a new memberships database
        HashMap<Integer, ArrayList<String>> membership2 = new HashMap<>();
        membership2.put(12345, new ArrayList<String>(List.of("Mary", "Jane", "123")));

        // test updating the memberships database
        enterMembership.updateMemberships(membership2);

        assertEquals(membership2, enterMembership.getMemberships());
    }

    /**
     * Testing getMemberships getter
     */
    @Test
    public void testGetMemberships(){
        assertEquals(memberships, enterMembership.getMemberships());
    }

    /**
     * Test if update database status to true works
     */
    @Test
    public void testUpdateDatabaseStatus(){
        enterMembership.updateDataBaseStatus(true);
        assertTrue(customer.getDataBaseStatus());
    }

    /**
     * Test if update database status to false works
     */
    @Test
    public void testUpdateDatabaseStatus2(){
        enterMembership.updateDataBaseStatus(false);
        assertFalse(customer.getDataBaseStatus());
    }

    /**
     * Test load membership method with a non existent file, data base should be empty and database flag should be set to true
     */
    @Test
    public void testLoadMembership(){
        SelfCheckoutStationLogic testObj = new SelfCheckoutStationLogic(station, null, null, membershipLen, "notrealfile.txt", 0, 0);
        CustomerStub testCustomer = new CustomerStub();
        testObj.registerEnterMemberObserver(testCustomer);
        testObj.loadMemberships();
        assertEquals(0, testObj.getMemberships().size());
        assertTrue(testCustomer.getDataBaseStatus());
    }

    /**
     * Test load membership method with a file containing an invalid mem length, database should be empty, flag raised
     */
    @Test
    public void testLoadMembership2(){
        SelfCheckoutStationLogic testObj = new SelfCheckoutStationLogic(station, null, null, membershipLen, "invalidMemLen.txt", 0, 0);
        CustomerStub testCustomer = new CustomerStub();
        testObj.registerEnterMemberObserver(testCustomer);
        testObj.loadMemberships();
        assertEquals(0, testObj.getMemberships().size());
        assertTrue(testCustomer.getDataBaseStatus());
    }

    /**
     * Test load membership method with a file containing an invalid membership data (missing names, points), database should be empty, flag raised
     */
    @Test
    public void testLoadMembership3(){
        SelfCheckoutStationLogic testObj = new SelfCheckoutStationLogic(station, null, null, membershipLen, "invalidMemData.txt", 0, 0);
        CustomerStub testCustomer = new CustomerStub();
        testObj.registerEnterMemberObserver(testCustomer);
        testObj.loadMemberships();
        assertEquals(0, testObj.getMemberships().size());
        assertTrue(testCustomer.getDataBaseStatus());
    }


    /**
     * Testing to see if upload memberships is able to execute without error - and it should
     * Data base status flag should be false as there shouldn't be an issue
     */
    @Test
    public void testUploadMembership(){
        enterMembership.uploadMemberships();
        assertFalse(customer.getDataBaseStatus());
    }

    /**
     * Test to see if it will be able to write a database with only the membership length
     * Data base flag should be false as there are no issues
     */
    @Test
    public void testUploadMembership2(){
        SelfCheckoutStationLogic testObj = new SelfCheckoutStationLogic(station, null, null, membershipLen, "nodata.txt", 0, 0);
        CustomerStub testCustomer = new CustomerStub();
        testObj.registerEnterMemberObserver(testCustomer);
        testObj.updateMemberships(new HashMap<>());
        testObj.uploadMemberships();
        assertFalse(testCustomer.getDataBaseStatus());
    }

    /**
     * Test to see if it will be able to write a database when a membership is corrupt (missing first name)
     * The database flag should be true for an issue is present
     */
    @Test
    public void testUploadMembership3(){
        SelfCheckoutStationLogic testObj = new SelfCheckoutStationLogic(station, null, null, membershipLen, "corruptwrite.txt", 0, 0);
        CustomerStub testCustomer = new CustomerStub();
        testObj.registerEnterMemberObserver(testCustomer);
        testObj.updateMemberships(new HashMap<>());
        testObj.getMemberships().put(9999, new ArrayList<String>(List.of("Jane", "abc")));
        testObj.uploadMemberships();
        assertTrue(testCustomer.getDataBaseStatus());
    }

    /**
     * Test scanning a membership card for a valid number that is in the database
     */
    @Test
    public void scanMembershipValidNumInDB() {
    	while (!enterMembership.getMemberCardNum().equals("12345")) {
    		station.mainScanner.scan(memberCard);
    	}
        assertEquals(0, enterMembership.validateMembershipCode());
    }
    
    /**
     * Test scanning a membership card for a valid number that is not in the database
     */
    @Test
    public void scanMembershipValidNumNotInDB() {
    	MembershipCard unknownMemberCard = new MembershipCard("membership", "12334", "Peter Parker", false);
    	while (!enterMembership.getMemberCardNum().equals("12334")) {
    		station.mainScanner.scan(unknownMemberCard);
    	}
        assertEquals(-3, enterMembership.validateMembershipCode());
    }
    
    /**
     * Test scanning a membership card for a number of invalid length
     */
    @Test
    public void scanMembershipInvalidLengthNum() {
    	MembershipCard badMemberCard = new MembershipCard("membership", "123456", "Peter Parker", false);
    	while (!enterMembership.getMemberCardNum().equals("123456")) {
    		station.mainScanner.scan(badMemberCard);
    	}
        assertEquals(-2, enterMembership.validateMembershipCode());
    }
    
    /**
     * Test scanning a grocery product instead of a membership card
     */
    @Test
    public void scanMembershipGroceryItem() {
    	Barcode barcode = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
    	BarcodedProduct product = new BarcodedProduct(barcode, "Milk", new BigDecimal("20"), 2.5);
    	BarcodedUnit unit = new BarcodedUnit(barcode, 2.5);
    	station.handheldScanner.scan(unit);
        assertEquals(-2, enterMembership.validateMembershipCode());
    }
    
    /**
     * Test swiping a membership card for a valid number that is in the database
     * @throws IOException
     */
    @Test
    public void swipeMembershipValidNumInDB() throws IOException {
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = true;
    	// Repeat until a successful swipe achieved
    	while(swipeError) {
	    	try {
		    	station.cardReader.swipe(memberCard, null);
		    	// After a successful swipe, set flag to false to exit loop
		    	swipeError = false;
		    } catch(MagneticStripeFailureException e) {
	    		// On swipe error, try again
	    		continue;
	    	}
    	}
        assertEquals(0, enterMembership.validateMembershipCode());
    }
    
    /**
     * Test swiping a membership card for a valid number that is not in the database
     * @throws IOException
     */
    @Test
    public void swipeMembershipValidNumNotInDB() throws IOException {
    	MembershipCard unknownMemberCard = new MembershipCard("membership", "12334", "Peter Parker", false);
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = true;
    	// Repeat until a successful swipe achieved
    	while(swipeError) {
	    	try {
		    	station.cardReader.swipe(unknownMemberCard, null);
		    	swipeError = false;
	    	} catch(MagneticStripeFailureException e) {
	    		// On swipe error, try again
	    		continue;
	    	}
    	}
        assertEquals(-3, enterMembership.validateMembershipCode());
    }
    
    /**
     * Test swiping a membership card for a number of invalid length
     * @throws IOException 
     */
    @Test
    public void swipeMembershipInvalidLengthNum() throws IOException {
    	MembershipCard badMemberCard = new MembershipCard("membership", "123456", "Peter Parker", false);
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = true;
    	// Repeat until a successful swipe achieved
    	while(swipeError) {
	    	try {
	    		station.cardReader.swipe(badMemberCard, null);
	    		swipeError = false;
	    	} catch(MagneticStripeFailureException e) {
	    		// On swipe error, try again
	    		continue;
	    	}
    	}
        assertEquals(-2, enterMembership.validateMembershipCode());
    }
    
    //GUI tests
    /**
     * Test scanning a membership card for a valid number that is in the database. result should
     * display welcome message for peter parket and display his points
     */
    @Test
    public void scanMembershipValidNumInDBGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// For test purposes, loop until card scan did not yield a random failure
    	while (!enterMembership.getMemberCardNum().equals("12345")) {
    		station.mainScanner.scan(memberCard);
    	}
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Welcome back Peter Parker!             Points total: 100", message);
    }
    
    /**
     * Test swiping a non-membership card. result should
     * display a message stating that a card swipe error occurred, since membership card data won't be read
     * from the card
     * @throws IOException 
     */
    @Test
    public void swipeNonMemberCard() throws IOException {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Create a credit card to be swiped
    	CreditCard creditCard = new CreditCard("fakeCredit", "123456789", "John", "344", "1234", false, true);
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = true;
    	// Repeat until a successful swipe achieved
    	while(swipeError) {
	    	try {
		    	station.cardReader.swipe(creditCard, null);
		    	// After a successful swipe, set flag to false to exit loop
		    	swipeError = false;
		    } catch(MagneticStripeFailureException e) {
	    		// On swipe error, try again
	    		continue;
	    	} 
    	}
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Card swipe error, please try again", message);
    }
    
    /**
     * Test scanning a membership card for a valid number that is in the database. but scan failure 
     * causes barcode number to be misread, resulting in a number that is too short
     */
    @Test
    public void scanMembershipValidNumScanFailureGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// For test purposes, loop until card scan fails
    	while (enterMembership.getMemberCardNum().equals("12345")) {
    		station.mainScanner.scan(memberCard);
    		
    	}
    	// Upon failure, there would be no card information passed into GUI logic, so an invalid length message
    	// would be displayed as the card was not read
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership number is invalid. A valid membership number is 5 digits long", message);
    }
    
    /**
     * Test scanning a membership card for a valid number that is not in the database. result should
     * display error message indicating not in database
     */
    @Test
    public void scanMembershipValidNumNotInDBGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	MembershipCard unknownMemberCard = new MembershipCard("membership", "12334", "Peter Parker", false);
    	// For test purposes, loop until card scan did not yield a random failure
    	while (!enterMembership.getMemberCardNum().equals("12334")) {
    		station.mainScanner.scan(unknownMemberCard);
    	}
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership number is not in the database", message);
    }
    
    /**
     * Test scanning a membership card for a number of invalid length. result should
     * display error message indicating invalid length
     */
    @Test
    public void scanMembershipInvalidLengthNumGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	MembershipCard badMemberCard = new MembershipCard("membership", "123456", "Peter Parker", false);
    	// For test purposes, loop until card scan did not yield a random failure
    	while (!enterMembership.getMemberCardNum().equals("123456")) {
    		station.mainScanner.scan(badMemberCard);
    	}
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership number is invalid. A valid membership number is 5 digits long", message);
    }
    
    /**
     * Test scanning a grocery product instead of a membership card. result should
     * display error message indicating invalid length
     */
    @Test
    public void scanMembershipGroceryItemGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	Barcode barcode = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
    	BarcodedProduct product = new BarcodedProduct(barcode, "Milk", new BigDecimal("20"), 2.5);
    	BarcodedUnit unit = new BarcodedUnit(barcode, 2.5);
    	station.mainScanner.scan(unit);
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership number is invalid. A valid membership number is 5 digits long", message);
    }
    
    /**
     * Test swiping a membership card for a valid number that is in the database. Result should welcome
     * peter parket and display his points
     * @throws IOException
     */
    @Test
    public void swipeMembershipValidNumInDBGUI() throws IOException {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = true;
    	// Repeat until a successful swipe achieved
    	while(swipeError) {
	    	try {
		    	station.cardReader.swipe(memberCard, null);
		    	// After a successful swipe, set flag to false to exit loop
		    	swipeError = false;
		    } catch(MagneticStripeFailureException e) {
	    		// On swipe error, try again
	    		continue;
	    	}
    	}
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Welcome back Peter Parker!             Points total: 100", message);
    }
    
    /**
     * Test swiping a membership card for a valid number that is not in the database. result should
     * display error message indicating not in database.
     * @throws IOException
     */
    @Test
    public void swipeMembershipValidNumNotInDBGUI() throws IOException {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	MembershipCard unknownMemberCard = new MembershipCard("membership", "12334", "Peter Parker", false);
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = true;
    	// Repeat until a successful swipe achieved
    	while(swipeError) {
	    	try {
		    	station.cardReader.swipe(unknownMemberCard, null);
		    	swipeError = false;
	    	} catch(MagneticStripeFailureException e) {
	    		// On swipe error, try again
	    		continue;
	    	}
    	}
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership number is not in the database", message);
    }
    
    /**
     * Test swiping a membership card for a number of invalid length result should
     * display error message indicating invalid length
     * 
     * @throws IOException 
     */
    @Test
    public void swipeMembershipInvalidLengthNumGUI() throws IOException {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	MembershipCard badMemberCard = new MembershipCard("membership", "123456", "Peter Parker", false);
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = true;
    	// Repeat until a successful swipe achieved
    	while(swipeError) {
	    	try {
	    		station.cardReader.swipe(badMemberCard, null);
	    		swipeError = false;
	    	} catch(MagneticStripeFailureException e) {
	    		// On swipe error, try again
	    		continue;
	    	}
    	}
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership number is invalid. A valid membership number is 5 digits long", message);
    }
    
    /**
     * Test swiping a membership card for a valid number that is in the database. Result should welcome
     * peter parket and display his points
     * @throws IOException
     */
    @Test
    public void swipeMembershipSwipeErrorGUI() throws IOException {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Catch magnetic stripe failure exceptions, which occur 1% of the time
    	boolean swipeError = false;
    	// Repeat until a bad swipe achieved
    	while(!swipeError) {
	    	try {
		    	station.cardReader.swipe(memberCard, null);
		    	// Hit enter button to reset swipe attempt flags after each successful swipe
		    	enterButton.doClick();
		    	// After a successful swipe, loop again
		    } catch(MagneticStripeFailureException e) {
	    		// On swipe error, break from loop to pass in swipe error message
	    		break;
	    	}
    	}
    	// Hit enter button after the swipe failure to achieve error message
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Card swipe error, please try again", message);
    }
    
    /**
     * Test entering a valid membership number string ("12345") that belongs to peter parker
     * result should welcome peter parker and display his points
     */
    @Test
    public void typeMembershipNumberGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Type "12345" which is a valid member
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fiveButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Welcome back Peter Parker!             Points total: 100", message);
    }
    
    /**
     * Test entering a valid membership number string ("12345") that belongs to peter parker
     * but accidentally add an extra number at the end and then delete that extra
     * result should welcome peter parker and display his points
     */
    @Test
    public void typeMembershipNumberExtraThenDeleteGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Type "123456" which is too long, then "DEL" to get rid of the last number
    	// that was accidentally typed, to get "12345" which is valid
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fiveButton.doClick();
    	sixButton.doClick();
    	delButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Welcome back Peter Parker!             Points total: 100", message);
    }
    
    /**
     * Test entering a valid membership number string ("12345") that belongs to peter parker
     * but accidentally type "DEL" before starting
     * result should welcome peter parker and display his points
     */
    @Test
    public void typeMembershipNumberDeleteThenNumbersGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Type "DEL" first then "12345" which is valid
    	delButton.doClick();
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fiveButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Welcome back Peter Parker!             Points total: 100", message);
    }
    
    /**
     * Test entering a valid membership number string ("12345") that belongs to peter parker
     * but hit cancel before finishing then try again to make sure it has cleared out the cancelled values
     * result should welcome peter parker and display his points
     */
    @Test
    public void typeMembershipNumberCancelInBetwenGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	JButton backButton = cio.getEnterMembershipPanel().getBackButton();
    	// Type "12345", then cancel it, then type it again.
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fiveButton.doClick();
    	backButton.doClick();
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fiveButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Welcome back Peter Parker!             Points total: 100", message);
    }

    /**
     * Test entering an invalid membership number (too short) into entermembership number, result should
     * display error message indicating invalid length
     */
    @Test
    public void typeMembershipNumberShortGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Type "1234" which is too short
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
    	assertEquals("Error: membership number is invalid. A valid membership number is 5 digits long", message);
    }

    /**
     * Test entering an invalid membership number (too long) into entermembership number, result should
     * display error message indicating invalid length
     */
    @Test
    public void typeMembershipNumberLongGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Type "123456" which is too short
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fiveButton.doClick();
    	sixButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
    	assertEquals("Error: membership number is invalid. A valid membership number is 5 digits long", message);
    }


    /**
     * Test entering a valid membership number but not in the database into entermembership number, result should
     * display error message indicating member number not found in database
     */
    @Test
    public void typeMembershipNumberNotInDBGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	// Type "12344" which is not in the database
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fourButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
    	assertEquals("Error: membership number is not in the database", message);
    }
    
    /**
     * Test entering a valid membership number "12345" for peter parker first, then use clear button
     * and enter another valid membership number "56789" for mary jane second
     */
    @Test
    public void typeMembershipNumberClearAnotherOneGUI() {
    	// Register the cio as an observer to get member card events passed through
    	enterMembership.registerEnterMemberObserver(cio);
    	// Stub out checking for corrupt database, since valid database has been loaded in
    	cio.isDataBaseCorrupt(false);
    	// Get required buttons to be clicked programmatically for test case
    	JButton oneButton = cio.getEnterMembershipPanel().getOneButton();
    	JButton twoButton = cio.getEnterMembershipPanel().getTwoButton();
    	JButton threeButton = cio.getEnterMembershipPanel().getThreeButton();
    	JButton fourButton = cio.getEnterMembershipPanel().getFourButton();
    	JButton fiveButton = cio.getEnterMembershipPanel().getFiveButton();
    	JButton sixButton = cio.getEnterMembershipPanel().getSixButton();
    	JButton sevenButton = cio.getEnterMembershipPanel().getSevenButton();
    	JButton eightButton = cio.getEnterMembershipPanel().getEightButton();
    	JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
    	JButton zeroButton = cio.getEnterMembershipPanel().getZeroButton();
    	JButton delButton = cio.getEnterMembershipPanel().getDelButton();
    	JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
    	JButton clearButton = cio.getEnterMembershipPanel().getClearButton();
    	// Type "12345" which is valid for peter parker
    	oneButton.doClick();
    	twoButton.doClick();
    	threeButton.doClick();
    	fourButton.doClick();
    	fiveButton.doClick();
    	enterButton.doClick();
    	String message = cio.getEnterMembershipPanel().getMembershipMessage();
    	assertEquals("Welcome back Peter Parker!             Points total: 100", message);
    	// Clear text before next entry
    	clearButton.doClick();
    	
    	// Type "56789" which is valid for mary jane
    	fiveButton.doClick();
    	sixButton.doClick();
    	sevenButton.doClick();
    	eightButton.doClick();
    	nineButton.doClick();
    	enterButton.doClick();
    	message = cio.getEnterMembershipPanel().getMembershipMessage();
    	assertEquals("Welcome back Mary Jane!             Points total: 100", message);
    }
    
    /**
     * Test to see if it will be able to load database when the initial file was corrupted.
     * The database flag should be true for an issue is present
     */
    @Test
    public void testUnableToLoadDBGUI(){
        SelfCheckoutStationLogic corruptLogic = new SelfCheckoutStationLogic(station, null, null, membershipLen, "corruptwrite.txt", 0, 0);
        CustomerIO cioTest = new CustomerIO(corruptLogic);
        cioTest.getFrame().setVisible(false);
        corruptLogic.registerEnterMemberObserver(cioTest);
        CommunicationsController.getCommunicator().registerOntoCustomers(cioTest);
        corruptLogic.updateMemberships(new HashMap<>());
        corruptLogic.getMemberships().put(9999, new ArrayList<String>(List.of("Jane", "abc")));
        corruptLogic.uploadMemberships();
        JButton enterButton = cioTest.getEnterMembershipPanel().getEnterButton();
        JButton oneButton = cioTest.getEnterMembershipPanel().getOneButton();
        
        oneButton.doClick();
        oneButton.doClick();
        oneButton.doClick();
        oneButton.doClick();
        oneButton.doClick();
        enterButton.doClick();
        String message = cioTest.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership database was unable to be loaded", message);
    }
    
    /**
     * Test to see if it will be able to write a database when a membership is corrupt (missing first name)
     * The database flag should be true for an issue is present
     */
    @Test
    public void testCorruptedDBGUI(){
        enterMembership.updateMemberships(new HashMap<>());
        enterMembership.getMemberships().put(99999, new ArrayList<String>(List.of("Mary", "Jane", "abc")));
        enterMembership.uploadMemberships();
        JButton enterButton = cio.getEnterMembershipPanel().getEnterButton();
        JButton nineButton = cio.getEnterMembershipPanel().getNineButton();
        
        nineButton.doClick();
        nineButton.doClick();
        nineButton.doClick();
        nineButton.doClick();
        nineButton.doClick();
        enterButton.doClick();
        String message = cio.getEnterMembershipPanel().getMembershipMessage();
        assertEquals("Error: membership database corrupted", message);
    }

}
