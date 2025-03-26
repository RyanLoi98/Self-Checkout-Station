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

package com.autovend.demo;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import com.autovend.ReusableBag;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.CardIssuer;
import com.autovend.software.CommunicationsController;
import com.autovend.software.SelfCheckoutStationLogic;

import GUIComponents.AttendantIO;

public class demoEnv {
	public static void main(String[] args) throws OverloadException {
		int[] billDenominations = {20,50};
		BigDecimal[] coinDenominations = {new BigDecimal(1)};
		int scaleMax = 10000;
		int scaleSens = 1;
		AttendantIO.supervisionStation = AttendantIO.attendantCommunicationsController.getSupervisionStation();
		Currency c1 = Currency.getInstance(Locale.CANADA);
		int[] billdenominations = { 5, 10, 15, 20, 50 };
		BigDecimal[] coindenominations = { new BigDecimal("1") };
		CardIssuer creditBank = new CardIssuer("Credit Bank");
		CardIssuer debitBank = new CardIssuer("Debit Bank");

		AttendantIO.attendantCommunicationsController.initializeSelfCheckoutStationLogics(1, c1, billdenominations,
				coindenominations, 50, 1, creditBank, debitBank, 5, "membershipdatabase.txt");
		SelfCheckoutStation station = AttendantIO.attendantCommunicationsController.getStationList().get(0);
		SelfCheckoutStationLogic scsl = AttendantIO.attendantCommunicationsController.getStationLogicList().get(0);
		
		//SelfCheckoutStation station = new SelfCheckoutStation(Currency.getInstance("CAD"), billDenominations, coinDenominations, scaleMax, scaleSens);
		
		// load up the dispenser to half
		ReusableBag[] bags = new ReusableBag[station.BAG_DISPENSER_CAPACITY / 2];
		for (int i = 0; i < station.BAG_DISPENSER_CAPACITY / 2; i++) {
			bags[i] = new ReusableBag();
		}

		try {
			station.bagDispenser.load(bags);
		// should never happen, as we only load up dispenser till the halfway point
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		
		// This is to cause low ink and paper
		station.printer.addInk(1);
		station.printer.addPaper(1);
		
		hardwareSim hwSim = new hardwareSim(station, creditBank, debitBank);
		buildSoftware builder = new buildSoftware(station, creditBank, debitBank, scsl);
		AttendantIO attendantIO = new AttendantIO();
		CommunicationsController comms = CommunicationsController.getCommunicator();
		comms.registerOntoAttendant(attendantIO);
	}
}
