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
import com.autovend.software.SelfCheckoutStationLogic.MethodOfPayment;

import GUIComponents.PaymentPanel;
import GUIComponents.ProceedPaymentPanel;
import GUIComponents.ThankYouPanel;
import GUIComponents.WelcomePanel;

import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.CardLayout;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;

public class ProceedPaymentPanelTest {
    JPanel dummyPanel;
    ProceedPaymentPanel proceedPaymentPanel;
    PaymentPanel paymentPanel;
    JButton backButton;
    JLabel paymentPrompt;
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
        proceedPaymentPanel = new ProceedPaymentPanel(dummyPanel, null);
        customerIO = new CustomerIO(scsl);
        
        frameWidth = 800;
        frameHeight = 600;
        paymentPanel = new PaymentPanel(customerIO, scsl, dummyPanel);
        paymentPrompt = proceedPaymentPanel.getPaymentPrompt();
        backButton = proceedPaymentPanel.getBackButton();
        dummyPanel.add(paymentPanel.getPanel(), "Payment");
        dummyPanel.add(proceedPaymentPanel.getPanel(), "ProceedPayment");

    }
    @After public void teardown(){
        dummyPanel = null;
        proceedPaymentPanel = null;
        paymentPrompt = null;
        backButton = null;
    }

    @Test public void giftLabelTest(){
    	proceedPaymentPanel.setMethodOfPayment(MethodOfPayment.GIFT);
        Assert.assertEquals("Please insert gift card and PIN into card reader", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void creditLabelTest(){
    	proceedPaymentPanel.setMethodOfPayment(MethodOfPayment.CREDIT);
        Assert.assertEquals("Please insert credit card and PIN into card reader", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void debitLabelTest(){
    	proceedPaymentPanel.setMethodOfPayment(MethodOfPayment.DEBIT);
        Assert.assertEquals("Please insert debit card and PIN into card reader", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void cashLabelTest(){
    	proceedPaymentPanel.setMethodOfPayment(MethodOfPayment.CASH);
        Assert.assertEquals("Please insert Cash to Bill or Coin Slot", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void giftConstructorTest(){
        proceedPaymentPanel = new ProceedPaymentPanel(dummyPanel, MethodOfPayment.GIFT);
        Assert.assertEquals("Please insert gift card and PIN into card reader", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void creditConstructorTest(){
        proceedPaymentPanel = new ProceedPaymentPanel(dummyPanel, MethodOfPayment.CREDIT);
        Assert.assertEquals("Please insert credit card and PIN into card reader", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void debitConstructorTest(){
        proceedPaymentPanel = new ProceedPaymentPanel(dummyPanel, MethodOfPayment.DEBIT);
        Assert.assertEquals("Please insert debit card and PIN into card reader", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void cashConstructorTest(){
        proceedPaymentPanel = new ProceedPaymentPanel(dummyPanel, MethodOfPayment.CASH);
        Assert.assertEquals("Please insert Cash to Bill or Coin Slot", proceedPaymentPanel.getPaymentPrompt().getText());
    }
    
    @Test public void clickBackButtonTest(){
    	backButton.doClick();
    	int i = 0;
//    	System.out.println(dummyPanel.getLayout().);
    	for (Component card: dummyPanel.getComponents()) {
    		if (card.isVisible()) {
    			// make sure we go back to payment panel which is the first panel added
    			// to dummyPanel
    			Assert.assertEquals(0, i); 
    		}
    		i += 1;
    		
    	}
    }

}
