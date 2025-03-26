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

import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.*;
import org.junit.*;
import com.autovend.devices.*;

import GUIComponents.AddItemByPLUPanel;

import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import javax.swing.*;

public class AddByPLUTest {
    JPanel dummyPanel;
    AddItemByPLUPanel pluPanel;
    JButton numFive;
    JButton deletePLU;
    JButton enterPLU;
    JButton addProduct;
    JButton exitButton;
    CustomerIO customer;

    // Variables for SelfCheckoutStation
    Currency currency;
    int[] denominations;
    BigDecimal[] coinDenominations;
    int scaleMaxWeight;
    int scaleSensitivity;
    SelfCheckoutStation scs;
    SelfCheckoutStationLogic scsLog;

    SelfCheckoutStationLogic scsl;

    @Before public void setup(){
        // Initialize the SelfCheckoutStation
        currency = Currency.getInstance(Locale.CANADA);
        denominations = new int[]{5, 10, 15, 20, 50};
        coinDenominations = new BigDecimal[]{BigDecimal.ONE};
        scaleMaxWeight = 20;
        scaleSensitivity = 1;
        scs = new SelfCheckoutStation(currency, denominations, coinDenominations, scaleMaxWeight, scaleSensitivity);
        scsl = new SelfCheckoutStationLogic(scs, null, null, 0, null, 10, 10);
        // Initialize panel testing
        dummyPanel = new JPanel();
        customer = new CustomerIO(scsl);
        pluPanel = new AddItemByPLUPanel(dummyPanel, customer);
        numFive = pluPanel.getFiveButton();
        deletePLU = pluPanel.getDelete();
        enterPLU = pluPanel.getEnter();
        addProduct = pluPanel.getAddToBill();
        exitButton = pluPanel.getExitButton();

        // Set up dummy products in database
        String code = "55555";
        Numeral[] numerals = new Numeral[5];
        for (int i = 0; i < 5; i++) {
            numerals[i] = Numeral.valueOf((byte) Integer.parseInt(String.valueOf(code.charAt(i))));
        }
        PriceLookUpCode pluCode = new PriceLookUpCode(numerals);
        PLUCodedProduct test = new PLUCodedProduct(pluCode, "Salmon", BigDecimal.valueOf(13.37));
        PLU_PRODUCT_DATABASE.put(pluCode, test);
    }
    @After public void teardown(){
        dummyPanel = null;
        pluPanel = null;
        numFive = null;
        deletePLU = null;
        enterPLU = null;
        addProduct = null;
        exitButton = null;

        PLU_PRODUCT_DATABASE.clear();
    }

    @Test public void numpadTest(){
        numFive.doClick();
        Assert.assertEquals("5", pluPanel.getEnteredPLU());
        Assert.assertEquals("Item: 5", pluPanel.getItemLabelOutput());
    }
    @Test public void deleteTestPluLen2(){
        numFive.doClick();
        numFive.doClick();
        deletePLU.doClick();
        Assert.assertEquals("5", pluPanel.getEnteredPLU());
        Assert.assertEquals("Item: 5", pluPanel.getItemLabelOutput());
    }
    @Test public void deleteTestPluLen0(){
        deletePLU.doClick();
        Assert.assertEquals("", pluPanel.getEnteredPLU());
        Assert.assertEquals("Item: ", pluPanel.getItemLabelOutput());
    }
    @Test public void encounterReset(){
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick();
        pluPanel.resetEncounter();
        Assert.assertEquals("", pluPanel.getEnteredPLU());
        Assert.assertEquals("Item: ", pluPanel.getItemLabelOutput());
        Assert.assertEquals("Price/kg: ", pluPanel.getPriceLabelOutput());
        Assert.assertEquals(BigDecimal.ZERO, pluPanel.getPrice());
    }
    @Test public void invalidPLUEntered(){

        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick();
        Assert.assertEquals("Invalid PLU Code: 555", pluPanel.getItemLabelOutput());
    }
    @Test public void validPLUEnteredAndProductAbsent(){
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick();
        Assert.assertEquals("Item unavailable!", pluPanel.getItemLabelOutput());
    }

    @Test public void validPLUEnteredAndProductPresent(){
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick();
        Assert.assertEquals("Item: Salmon", pluPanel.getItemLabelOutput());
        Assert.assertEquals("Price/kg: $13.37", pluPanel.getPriceLabelOutput());
    }

    @Test public void productWithValidWeightAdded(){
        pluPanel.isTest(); // Set to avoid pop up

        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick(); // Get item and select it

        addProduct.doClick();
        Assert.assertEquals("Item(s) added to bill!", pluPanel.getItemLabelOutput());
    }

    @Test public void productWithInvalidWeightAdded(){
        pluPanel.isTest(); // Set to avoid pop up

        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick(); // Get item and select it

        pluPanel.setInvalidWeightAdded(); // After confirmation, wrong weight added

        addProduct.doClick();

        Assert.assertTrue(pluPanel.attendantAlerted);
        Assert.assertTrue(pluPanel.isBlocked());
    }

    @Test public void productWithValidWeightFalseAlarm(){
        pluPanel.isTest(); // Set to avoid pop up

        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick(); // Get item and select it

        pluPanel.setInvalidWeightAdded(); // After confirmation, wrong weight added

        addProduct.doClick();

        Assert.assertTrue(pluPanel.attendantAlerted);
        Assert.assertTrue(pluPanel.isBlocked());
        pluPanel.setValidWeightAdded(); // Simulate attendant saying product weight is okay
        Assert.assertFalse(pluPanel.isBlocked()); // Show that the system was unblocked
        addProduct.doClick();
        Assert.assertEquals("Item(s) added to bill!", pluPanel.getItemLabelOutput());
    }

    @Test public void deleteItemSelected(){
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick(); // Get item and select it
        Assert.assertEquals("Item: Salmon", pluPanel.getItemLabelOutput());
        Assert.assertEquals("Price/kg: $13.37", pluPanel.getPriceLabelOutput());


        deletePLU.doClick();
        Assert.assertEquals("", pluPanel.getEnteredPLU());
        Assert.assertEquals("Item: ", pluPanel.getItemLabelOutput());
        Assert.assertEquals("Price/kg: ", pluPanel.getPriceLabelOutput());
        Assert.assertEquals(BigDecimal.ZERO, pluPanel.getPrice());
    }

    @Test public void doNotPlaceItemInBaggingArea(){
        pluPanel.isTest(); // Set to avoid pop up

        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick(); // Get item and select it

        pluPanel.itemTooHeavy = true; // Simulate signal to attendant for help
        addProduct.doClick();
        Assert.assertEquals("Item(s) added to bill!", pluPanel.getItemLabelOutput());
        Assert.assertTrue(pluPanel.attendantAlerted); // Attendant was alerted for helping customer
    }

    @Test public void exitInteraction(){
        pluPanel.isTest(); // Set to avoid pop up

        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        numFive.doClick();
        enterPLU.doClick(); // Get item and select it
        // Check if added item is here
        Assert.assertEquals("Item: Salmon", pluPanel.getItemLabelOutput());
        Assert.assertEquals("Price/kg: $13.37", pluPanel.getPriceLabelOutput());

        exitButton.doClick();
        // We expect that variables manipulated in the interaction are reset
        Assert.assertEquals("", pluPanel.getEnteredPLU());
        Assert.assertEquals("Item: ", pluPanel.getItemLabelOutput());
        Assert.assertEquals("Price/kg: ", pluPanel.getPriceLabelOutput());
        Assert.assertEquals(BigDecimal.ZERO, pluPanel.getPrice());
    }
}
