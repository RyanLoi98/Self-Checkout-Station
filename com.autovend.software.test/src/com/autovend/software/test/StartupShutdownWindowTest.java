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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import javax.swing.JButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.*;
import com.autovend.software.AttendantCommunicationsController;

import GUIComponents.StartupShutdownWindow;


public class StartupShutdownWindowTest {
	SupervisionStation super_stn;
	AttendantCommunicationsController attendantComm;
	StartupShutdownWindow ssw;
	ArrayList<JButton> buttons;
	ArrayList<SelfCheckoutStation> stationList;
	
	@Before
	public void setup() {
		super_stn = new SupervisionStation();
		Currency c1 = Currency.getInstance(Locale.CANADA);
		int[] billdenominations = { 5, 10, 15, 20, 50 };
		BigDecimal[] coindenominations = { new BigDecimal("1") };
		
		attendantComm = new AttendantCommunicationsController();
		
		attendantComm.initializeSelfCheckoutStationLogics(15, c1, billdenominations, coindenominations, 20, 1, null, null, 0,
				null);
		ssw = new StartupShutdownWindow(attendantComm.getSupervisionStation(), attendantComm);
		buttons = ssw.getButtons();
		stationList=attendantComm.getStationList();
	
		
	}
	
	@Test
	public void allStationsStarted() {

		
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			stationList.get(i).baggingArea.enable();

		}
		
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			
			buttons.get(i).doClick();
			assertTrue(stationList.get(i).baggingArea.isDisabled());
			assertTrue(stationList.get(i).billInput.isDisabled());
			assertTrue(stationList.get(i).billOutput.isDisabled());
			assertTrue(stationList.get(i).billStorage.isDisabled());	
			assertTrue(stationList.get(i).billValidator.isDisabled());
			assertTrue(stationList.get(i).cardReader.isDisabled());
			assertTrue(stationList.get(i).coinSlot.isDisabled());
			assertTrue(stationList.get(i).coinStorage.isDisabled());			
			assertTrue(stationList.get(i).coinTray.isDisabled());
			assertTrue(stationList.get(i).coinValidator.isDisabled());
			assertTrue(stationList.get(i).handheldScanner.isDisabled());
			assertTrue(stationList.get(i).mainScanner.isDisabled());
			assertTrue(stationList.get(i).printer.isDisabled());
			assertTrue(stationList.get(i).scale.isDisabled());
			assertTrue(stationList.get(i).screen.isDisabled());
		}
						   
	}
	
	@Test
	public void stationsDisabled_if_clicked_on_gets_enabled() {
	
		
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			stationList.get(i).baggingArea.disable();

		}
		
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			
			buttons.get(i).doClick();
			assertFalse(stationList.get(i).baggingArea.isDisabled());
			assertFalse(stationList.get(i).billInput.isDisabled());
			assertFalse(stationList.get(i).billOutput.isDisabled());
			assertFalse(stationList.get(i).billStorage.isDisabled());	
			assertFalse(stationList.get(i).billValidator.isDisabled());
			assertFalse(stationList.get(i).cardReader.isDisabled());
			assertFalse(stationList.get(i).coinSlot.isDisabled());
			assertFalse(stationList.get(i).coinStorage.isDisabled());			
			assertFalse(stationList.get(i).coinTray.isDisabled());
			assertFalse(stationList.get(i).coinValidator.isDisabled());
			assertFalse(stationList.get(i).handheldScanner.isDisabled());
			assertFalse(stationList.get(i).mainScanner.isDisabled());
			assertFalse(stationList.get(i).printer.isDisabled());
			assertFalse(stationList.get(i).scale.isDisabled());
			assertFalse(stationList.get(i).screen.isDisabled());
		}
	}


	
	@Test
	public void stationsDisabledAgain(){
		
		
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			stationList.get(i).baggingArea.enable();

		}
		
		// Disabling Stations
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			buttons.get(i).doClick();
		}
		
		// Enabling Stations Again
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			buttons.get(i).doClick();
		}
		
		for(int i =0 ; i<attendantComm.getNumberOfSCS();i++) {
			assertFalse(stationList.get(i).baggingArea.isDisabled());
			assertFalse(stationList.get(i).billInput.isDisabled());
			assertFalse(stationList.get(i).billOutput.isDisabled());
			assertFalse(stationList.get(i).billStorage.isDisabled());	
			assertFalse(stationList.get(i).billValidator.isDisabled());
			assertFalse(stationList.get(i).cardReader.isDisabled());
			assertFalse(stationList.get(i).coinSlot.isDisabled());
			assertFalse(stationList.get(i).coinStorage.isDisabled());			
			assertFalse(stationList.get(i).coinTray.isDisabled());
			assertFalse(stationList.get(i).coinValidator.isDisabled());
			assertFalse(stationList.get(i).handheldScanner.isDisabled());
			assertFalse(stationList.get(i).mainScanner.isDisabled());
			assertFalse(stationList.get(i).printer.isDisabled());
			assertFalse(stationList.get(i).scale.isDisabled());
			assertFalse(stationList.get(i).screen.isDisabled());
		}

	}
	
	@Test
	public void exitButton() {
		JButton exitButton = ssw.getBackButton();
		exitButton.doClick();
		assertFalse(stationList.get(0).screen.isDisabled());
	}
	
	/*
	@After
	/*
	public void tearDown() {
		super_stn = null;
		attendantComm = null;
		scs1 = null;
		scs2 = null;
		scs3 = null;
		scs4 = null;
		ssw = null;
		buttons = null;
	}
	*/
}

