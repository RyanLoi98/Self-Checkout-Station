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

import com.autovend.devices.ReusableBagDispenser;
import com.autovend.software.CustomerIO;
import com.autovend.software.SelfCheckoutStationLogic;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import GUIComponents.WelcomePanel;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.CardIssuer;
import com.autovend.software.CustomerIO;
import com.autovend.software.SelfCheckoutStationLogic;

import java.math.BigDecimal;
import java.util.Currency;


public class SelectLanguageTest {
    WelcomePanel selectLanguageController;

    @Before
    public void setUp() {
        int[] billDenominations = {20,50};
        BigDecimal[] coinDenominations = {BigDecimal.valueOf(1), BigDecimal.valueOf(2)};
        int scaleMax = 10000;
        int scaleSens = 1;
        SelfCheckoutStation station = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenominations, coinDenominations, scaleMax, scaleSens);
        CardIssuer creditBank = new CardIssuer("Credit Bank");
        CardIssuer debitBank = new CardIssuer("Debit Bank");
        SelfCheckoutStationLogic scsL = new SelfCheckoutStationLogic(station, creditBank, debitBank, 5, "membershipdatabase.txt", 10, 10);
        CustomerIO myCust = new CustomerIO(scsL);
        selectLanguageController = new WelcomePanel(myCust, 50, 50);
    }

    @Test
    public void langChangedToEnglishTest() {
        selectLanguageController.getLanguagesList().clearSelection();
        selectLanguageController.getLanguagesList().setSelectedIndex(0);
        Assert.assertEquals("English", selectLanguageController.getDesiredLanguage());
    }

    



}
