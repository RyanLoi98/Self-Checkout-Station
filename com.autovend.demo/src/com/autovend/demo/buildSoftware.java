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

import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.external.CardIssuer;
import com.autovend.software.CustomerIO;
import com.autovend.software.SelfCheckoutStationLogic;

import GUIComponents.AttendantIO;

public class buildSoftware {

	public buildSoftware(SelfCheckoutStation scs, CardIssuer creditBank, CardIssuer debitBank, SelfCheckoutStationLogic scsL) {
		// Setting membership length to 6 and bag capacity to 10
		ReusableBagDispenser bagDisp = new ReusableBagDispenser(10);
		//SelfCheckoutStationLogic scsL = new SelfCheckoutStationLogic(scs, creditBank, debitBank, 5, "membershipdatabase.txt", bagDisp);
		CustomerIO myCust = new CustomerIO(scsL);
		myCust.registerSelf();
	}
}
