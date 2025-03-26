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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;

import GUIComponents.LogoutWindow;
import GUIComponents.SuperUserWindow;


public class LogoutTest {
	SupervisionStation su;
	AttendantCommunicationsController at;
	LogoutWindow logout;
	
	@Before
	public void Setup() {
		su = new SupervisionStation();
		at = new AttendantCommunicationsController();
		logout = new LogoutWindow(su, at);
	}
	
	@After
	public void Teardown() {
		su = null;
		at = null;
		logout = null;
	}
	
	
	/**
	 * test that logout works as intended
	 */
	@Test
	public void testLogout () {
		logout.getYesButton().doClick();
		assertTrue(LogoutWindow.logoutSuccess);
	}
	
	
	
	/**
	 * test that go back works as intended
	 */
	@Test
	public void testLogoutGoBack () {
		logout.getNoButton().doClick();
		assertTrue(LogoutWindow.logoutgoBack);
	}
	
	
	/**
	 * Ensures super user Logout is working as intended
	 */
	@Test
	public void testSuperUser () {
		SuperUserWindow.isSuperUser = true;
		logout.getNoButton().doClick();
		assertTrue(SuperUserWindow.isSuperUser);
	}

}
