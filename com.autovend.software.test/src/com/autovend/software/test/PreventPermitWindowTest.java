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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.swing.JButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.*;
import com.autovend.software.AttendantCommunicationsController;
import GUIComponents.PreventPermitWindow;

public class PreventPermitWindowTest {
    SupervisionStation super_stn;
    AttendantCommunicationsController attendantComm;
    SelfCheckoutStation scs1, scs2, scs3, scs4;
    PreventPermitWindow ppw;
    Currency c1 = Currency.getInstance(Locale.CANADA);
    int[] billdenominations = { 5, 10, 15, 20, 50 };
    BigDecimal[] coindenominations = { new BigDecimal("1") };
    ArrayList<JButton> buttons;
    int stations;

    @Before
    public void setup() {
        super_stn = new SupervisionStation();
        attendantComm = new AttendantCommunicationsController();
        scs1 = new SelfCheckoutStation(c1, billdenominations, coindenominations, 20, 1);
        scs2 = new SelfCheckoutStation(c1, billdenominations, coindenominations, 20, 1);
        scs3 = new SelfCheckoutStation(c1, billdenominations, coindenominations, 20, 1);
        scs4 = new SelfCheckoutStation(c1, billdenominations, coindenominations, 20, 1);
        super_stn.add(scs1);
        super_stn.add(scs2);
        super_stn.add(scs3);
        super_stn.add(scs4);
        ppw = new PreventPermitWindow(super_stn, attendantComm);
        stations = super_stn.supervisedStationCount();
    }
    @Test
    public void testGoBackButtonActionPerformed() {
        JButton goBackButton = ppw.getgoBackButton();
        ActionListener[] listeners = goBackButton.getActionListeners();
        boolean actionPerformedTriggered = false;

        // Create an ActionEvent with goBackButton as the source
        ActionEvent event = new ActionEvent(goBackButton, ActionEvent.ACTION_PERFORMED, "");

        for (ActionListener listener : listeners) {
            try {
                listener.actionPerformed(event);
                actionPerformedTriggered = true;
            } catch (Exception e) {
                fail("error " + e.getMessage());
            }
        }

        assertTrue(actionPerformedTriggered);
    }

	@Test
	public void testPreventPermitWindowInitialization() {
	    assertNotNull(ppw);
	    assertNotNull(ppw.getgoBackButton());
	    assertEquals(4, ppw.getButtons().size()); //Assuming there are 4 SelfCheckoutStations 
	}
	
	@Test
	public void testNumberOfButtons() {
	    assertEquals(stations, ppw.getButtons().size());
	}


	@Test
	public void testGetButtons() {
	    PreventPermitWindow ppw = new PreventPermitWindow(super_stn, attendantComm);
	    assertEquals(super_stn.supervisedStationCount(), ppw.getButtons().size());
	}
	
	@Test
	public void testPreventStationUse_prevent() {
	    List<SelfCheckoutStation> stations = attendantComm.getStationList();
	    Random rand = new Random();
	    for (int i = 0; i < stations.size(); i++) {
	        int index = rand.nextInt(stations.size());
	        JButton button = ppw.getButtons().get(index);
	        button.doClick();
			assertTrue(stations.get(index).baggingArea.isDisabled());
			assertTrue(stations.get(index).billInput.isDisabled());
			assertTrue(stations.get(index).billOutput.isDisabled());
			assertTrue(stations.get(index).billStorage.isDisabled());
			assertTrue(stations.get(index).billValidator.isDisabled());
			assertTrue(stations.get(index).cardReader.isDisabled());	
			assertTrue(stations.get(index).coinSlot.isDisabled());
			assertTrue(stations.get(index).coinStorage.isDisabled());
			assertTrue(stations.get(index).coinTray.isDisabled());
			assertTrue(stations.get(index).coinValidator.isDisabled());
			assertTrue(stations.get(index).handheldScanner.isDisabled());
			assertTrue(stations.get(index).mainScanner.isDisabled());	
			assertTrue(stations.get(index).printer.isDisabled());	
			assertTrue(stations.get(index).scale.isDisabled());
			assertTrue(stations.get(index).screen.isDisabled());
			buttons.get(0).doClick();
			
	    }
	}
	
	
	@Test
	public void testPreventStationUse_permit() {
	    List<SelfCheckoutStation> stations = attendantComm.getStationList();
	    Random rand = new Random();
	    for (int i = 0; i < stations.size(); i++) {
	        int index = rand.nextInt(stations.size());
	        JButton button = ppw.getButtons().get(index);
	        buttons.get(0).doClick();
			assertFalse(stations.get(index).baggingArea.isDisabled());
			assertFalse(stations.get(index).billInput.isDisabled());
			assertFalse(stations.get(index).billOutput.isDisabled());
			assertFalse(stations.get(index).billStorage.isDisabled());
			assertFalse(stations.get(index).billValidator.isDisabled());
			assertFalse(stations.get(index).cardReader.isDisabled());	
			assertFalse(stations.get(index).coinSlot.isDisabled());
			assertFalse(stations.get(index).coinStorage.isDisabled());
			assertFalse(stations.get(index).coinTray.isDisabled());
			assertFalse(stations.get(index).coinValidator.isDisabled());
			assertFalse(stations.get(index).handheldScanner.isDisabled());
			assertFalse(stations.get(index).mainScanner.isDisabled());	
			assertFalse(stations.get(index).printer.isDisabled());	
			assertFalse(stations.get(index).scale.isDisabled());
			assertFalse(stations.get(index).screen.isDisabled());
	    }
	}

	
    @After
    public void tearDown() {
        super_stn = null;
        attendantComm = null;
        scs1 = null;
        scs2 = null;
        scs3 = null;
        scs4 = null;
        ppw = null;
        buttons = null;
    }
}
