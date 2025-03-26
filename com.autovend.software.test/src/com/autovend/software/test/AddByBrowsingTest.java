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

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import org.junit.*;
import com.autovend.devices.*;
import com.autovend.software.CommunicationsController;
import com.autovend.software.SelfCheckoutStationLogic;

import GUIComponents.AddByBrowsingPanel;

import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import javax.swing.*;

import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;

public class AddByBrowsingTest {
    JPanel dummyPanel;
    AddByBrowsingPanel browsingPanel;
    JButton plusButton;
    JButton minusButton;
    JButton addProduct;
    JButton exitButton;
    JList selection;
    ListSelectionModel selectionPress;
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

    @Before public void setUp(){
        // Set up dummy products in database
        String code = "55555";
        Numeral[] numerals = new Numeral[5];
        for (int i = 0; i < 5; i++) {
            numerals[i] = Numeral.valueOf((byte) Integer.parseInt(String.valueOf(code.charAt(i))));
        }
        PriceLookUpCode pluCode = new PriceLookUpCode(numerals);
        PLUCodedProduct test = new PLUCodedProduct(pluCode, "Salmon", BigDecimal.valueOf(13.37));
        PLU_PRODUCT_DATABASE.put(pluCode, test);

        code = "55555";
        Numeral[] numerals2 = new Numeral[5];
        for (int i = 0; i < 5; i++) {
            numerals2[i] = Numeral.valueOf((byte) Integer.parseInt(String.valueOf(code.charAt(i))));
        }
        Barcode barcode = new Barcode(numerals2);
        BarcodedProduct test4 = new BarcodedProduct(barcode, "Cheetos", BigDecimal.valueOf(77.77), 10);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, test4);
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
        browsingPanel = new AddByBrowsingPanel(customer,dummyPanel,scsl);
        plusButton = browsingPanel.getPlusButton();
        minusButton = browsingPanel.getMinusButton();
        addProduct = browsingPanel.getAddButton();
        selection = browsingPanel.getSelection();
        selectionPress = browsingPanel.getSelectModel();
    }

    @After public void tearDown(){
        ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
        ProductDatabases.PLU_PRODUCT_DATABASE.clear();
        dummyPanel = new JPanel();
        browsingPanel = new AddByBrowsingPanel(null,dummyPanel,null);
        plusButton = null;
        minusButton = null;
        addProduct = null;
        exitButton = null;
        selection = null;
        selectionPress = null;
    }

    @Test public void selectPLU(){
        selection.setSelectedIndex(1); // Select the second item
        // We expect that 1 salmon was selected
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price/kg: $13.37", browsingPanel.getPriceLabelText());
    }
    @Test public void selectBarcode(){
        selection.setSelectedIndex(0); // Select the first item
        // We expect that Cheetos was selected
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: $77.77", browsingPanel.getPriceLabelText());
    }
    @Test public void increaseQuantity(){
        selection.setSelectedIndex(0); // Select BarcodedProduct cheetos
        plusButton.doClick();
        Assert.assertEquals("Quantity: 2", browsingPanel.getQuantityLabelText());
    }
    @Test public void tryIncreaseWithInvalidConditions(){
        plusButton.doClick(); // Trying to increase with no selection
        Assert.assertEquals("Quantity:", browsingPanel.getQuantityLabelText());
    }
    @Test public void decreaseQuantity(){
        selection.setSelectedIndex(0); // Select BarcodedProduct cheetos
        plusButton.doClick();
        Assert.assertEquals("Quantity: 2", browsingPanel.getQuantityLabelText());
        minusButton.doClick();
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());

    }
    @Test public void decreaseQuantityAtOne(){
        selection.setSelectedIndex(0); // Select BarcodedProduct cheetos
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        minusButton.doClick();
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
    }
    @Test public void decreaseQuantityWithInvalidConditions(){
        minusButton.doClick();
        Assert.assertEquals("Quantity:", browsingPanel.getQuantityLabelText());
    }
    @Test public void addToBillValidWeight(){
        browsingPanel.isTest(); // Don't show pop up for humans
        selection.setSelectedIndex(0); // Select BarcodedProduct cheetos
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: $77.77", browsingPanel.getPriceLabelText());
        addProduct.doClick();

        // Item was added to bill
        Assert.assertEquals("Quantity: ", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: ", browsingPanel.getPriceLabelText());
    }
    @Test public void addToBillTooHeavyForCustomer(){
        browsingPanel.isTest(); // Don't show pop up for humans
        browsingPanel.setTooHeavy();
        selection.setSelectedIndex(0); // Select BarcodedProduct cheetos
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: $77.77", browsingPanel.getPriceLabelText());
        addProduct.doClick();

        // Attendant notified to help customer and item added to bill
        Assert.assertEquals("Quantity: ", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: ", browsingPanel.getPriceLabelText());
        Assert.assertTrue(browsingPanel.attedantNotified);
    }
    @Test public void addToBillWeightDiscrepancyAlert(){
        browsingPanel.isTest(); // Don't show pop up for humans
        browsingPanel.setWeightDiscrepancy();
        selection.setSelectedIndex(0); // Select BarcodedProduct cheetos
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: $77.77", browsingPanel.getPriceLabelText());

        addProduct.doClick();

        // After click, we expect the attendant to be notified and the adding to not have gone through
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: $77.77", browsingPanel.getPriceLabelText());
        Assert.assertTrue(browsingPanel.attedantNotified);
    }
    @Test public void addToBillWeightDiscrepancyOkay(){
        browsingPanel.isTest(); // Don't show pop up for humans
        browsingPanel.setWeightDiscrepancy();
        selection.setSelectedIndex(0); // Select BarcodedProduct cheetos
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: $77.77", browsingPanel.getPriceLabelText());

        addProduct.doClick();

        // After click, we expect the attendant to be notified and the adding to not have gone through
        Assert.assertEquals("Quantity: 1", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: $77.77", browsingPanel.getPriceLabelText());
        Assert.assertTrue(browsingPanel.attedantNotified);
        browsingPanel.attendantVerified(); // Attendant thinks discrepancy okay

        addProduct.doClick(); // Now adding goes through
        Assert.assertEquals("Quantity: ", browsingPanel.getQuantityLabelText());
        Assert.assertEquals("Price: ", browsingPanel.getPriceLabelText());
    }
}
