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

import com.autovend.MembershipCard;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.products.PLUCodedProduct;
import org.junit.*;
import com.autovend.devices.*;
import com.autovend.external.CardIssuer;
import com.autovend.software.CommunicationsController;
import com.autovend.software.SelfCheckoutStationLogic;

import GUIComponents.ThankYouPanel;
import GUIComponents.WelcomePanel;

import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.CardLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;

public class ThankYouPanelTest {
    JPanel dummyPanel;
    ThankYouPanel thankYouPanel;
    WelcomePanel welcomePanel;
    JButton welcomeButton;
    JLabel thankYouLabel;
	JLabel changeLabel;
    BigDecimal changeDue;
    CustomerIO customerIO;
	SelfCheckoutStation station;
	int[] billDenominations;
	BigDecimal[] coinDenominations;
	int maxWeight;
	int sensitivity;
	Currency CAD;
	int membershipLen;
	int frameWidth;
	int frameHeight;
	SelfCheckoutStationLogic scsl;

    @Before public void setup(){
        membershipLen = 4;
        CAD = Currency.getInstance("CAD");
        billDenominations = new int[]{5, 10, 20, 50, 100};
        coinDenominations = new BigDecimal[]{BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.25),
                BigDecimal.valueOf(1), BigDecimal.valueOf(2)};
        maxWeight = 50;
        sensitivity = 1;
        station = new SelfCheckoutStation(CAD, billDenominations, coinDenominations, maxWeight, sensitivity);
        
        scsl = new SelfCheckoutStationLogic(station, null, null, membershipLen, "membershipdatabase.txt", 10, 10);

        dummyPanel = new JPanel(new CardLayout());
        customerIO = new CustomerIO(scsl);
        thankYouPanel = new ThankYouPanel(dummyPanel, customerIO);
        
        frameWidth = 800;
        frameHeight = 600;
        welcomePanel = new WelcomePanel(customerIO, frameWidth, frameHeight);
        thankYouLabel = thankYouPanel.getThankYouLabel();
        changeLabel = thankYouPanel.getChangeLabel();
        welcomeButton = thankYouPanel.getWelcomeButton();
        changeDue = new BigDecimal(0.00);
    }
    @After public void teardown(){
        dummyPanel = null;
        thankYouPanel = null;
        thankYouLabel = null;
        changeLabel = null;
        welcomeButton = null;
    }

    @Test public void defaultLabelTest(){
        Assert.assertEquals("Thank you for shopping with us!", thankYouLabel.getText());
        Assert.assertEquals("Please collect your $" + changeDue, changeLabel.getText());
    }
    
    @Test public void setChangeTest(){
    	thankYouPanel.setChangeDue(new BigDecimal("1.23"));
        Assert.assertEquals("Thank you for shopping with us!", thankYouLabel.getText());
        Assert.assertEquals("Please collect your $1.23", thankYouPanel.getChangeLabel().getText());
    }
    
    @Test public void clickWelcomeButtonTest(){
    	welcomeButton.doClick();
    	Assert.assertEquals(true, welcomePanel.isVisible());
    }

}
