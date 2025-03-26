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

import javax.swing.JOptionPane;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;

import GUIComponents.LoginWindow;
import GUIComponents.SuperUserWindow;


public class LoginTest {
	SupervisionStation su;
	AttendantCommunicationsController at;
	LoginWindow login;
	
	@Before
	public void Setup() {
		su = new SupervisionStation();
		at = new AttendantCommunicationsController();
		login = new LoginWindow(su, at);
	}
	
	/**
	 * Ensures that Login Window correctly takes inputs from physical keyboard and that it accepts valid login credentials
	 */
	@Test
	public void testLogin () {
		login.selectUserID();
		su.keyboard.type("test");
		login.deSelectUserID();
		login.selectPassword();
		su.keyboard.type("test");
		login.deSelectPassword();
		login.getVerifyButton().doClick();
		assertEquals(login.getUsertext(), "test");
		assertEquals(login.getPswdtext(), "test");
		assertTrue(login.getUserTyped());
		assertTrue(login.getPswdTyped());
		assertTrue(login.getLoginSuccess());
	}
	
	/**
	 * Ensures that Login Window rejects invalid credentials
	 */
	@Test
	public void testLoginFailed () {
		login.selectUserID();
		su.keyboard.type("test");
		login.deSelectUserID();
		login.selectPassword();
		su.keyboard.type("wrong");
		login.deSelectPassword();
		login.getVerifyButton().doClick();
		assertTrue(login.getLoginFailed());
	}
	
	/**
	 * Ensures super user Login is working as intended
	 */
	@Test
	public void testSuperUser () {
		login.selectUserID();
		su.keyboard.type("admin");
		login.deSelectUserID();
		login.selectPassword();
		su.keyboard.type("admin");
		login.deSelectPassword();
		login.getVerifyButton().doClick();
		assertTrue(SuperUserWindow.isSuperUser);
	}
	
	/**
	 * Ensures addUser works as intended
	 */
	@Test
	public void addUser () {
		LoginWindow.addUser("j", "j");
		login.selectUserID();
		su.keyboard.type("j");
		login.deSelectUserID();
		login.selectPassword();
		su.keyboard.type("j");
		login.deSelectPassword();
		login.getVerifyButton().doClick();
		assertTrue(login.getLoginSuccess());
		assertTrue(LoginWindow.getUsers().containsKey("j"));
	}
	
	@After
	public void Teardown() {
		su = null;
		at = null;
		login = null;
	}
}
