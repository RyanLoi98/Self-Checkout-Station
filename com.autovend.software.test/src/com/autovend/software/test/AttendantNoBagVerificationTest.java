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
import javax.swing.JToggleButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.*;
import com.autovend.software.AttendantCommunicationsController;

import GUIComponents.AttendantNoBagVerification;



public class AttendantNoBagVerificationTest {
	SupervisionStation super_stn;
	AttendantCommunicationsController attendantComm;
	AttendantNoBagVerification anbv;
	ArrayList<JButton> buttons;
	ArrayList<SelfCheckoutStation> stationList;
	JToggleButton tog_button;
	
	
	@Before
	public void setup() {
		super_stn = new SupervisionStation();
		Currency c1 = Currency.getInstance(Locale.CANADA);
		int[] billdenominations = { 5, 10, 15, 20, 50 };
		BigDecimal[] coindenominations = { new BigDecimal("1") };
		
		attendantComm = new AttendantCommunicationsController();
		
		attendantComm.initializeSelfCheckoutStationLogics(15, c1, billdenominations, coindenominations, 20, 1, null, null, 0,
				null);
		anbv = new AttendantNoBagVerification(attendantComm.getSupervisionStation(), attendantComm);
		buttons = anbv.getButtons();
		stationList=attendantComm.getStationList();
		tog_button=anbv.togbutton();
	
		
	}
	
	@Test
	public void verificationButtonhasbeenPressed_and_tog_button_been_pressed() {


		tog_button.doClick();
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			
			buttons.get(i).doClick();
			assertTrue(attendantComm.isApproved());
		}
						   
	}
	
	@Test
	public void verificationButtonhasnotbeenPressed_and_tog_button_been_selected() {

		tog_button.doClick();
		
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			
			assertFalse(attendantComm.isApproved());
		}
						   
	}
	
	@Test
	public void verificationButtonhasbeenPressed_and_tog_button_not_pressesed() {


		tog_button.doClick();
		tog_button.doClick();

		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			
			assertFalse(attendantComm.isApproved());
		}
						   
	}
	
	@Test
	public void verificationButtonhasnotbeenPressed_and_tog_button_not_pressesed() {

		tog_button.doClick();
		tog_button.doClick();

		
		for(int i = 0; i < attendantComm.getNumberOfSCS(); i++) {
			
			buttons.get(i).doClick();
			assertTrue(attendantComm.isApproved());
		}
						   
	}
	
	
	
	
	
	@Test
	public void exitButton() {
		JButton exitButton = anbv.getBackButton();
		exitButton.doClick();
		assertFalse(stationList.get(0).screen.isDisabled());
	}
	
	
	@After
	
	public void tearDown() {
		super_stn = null;
		attendantComm = null;

	}
	
}
