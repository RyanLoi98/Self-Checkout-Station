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

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.AttendantCommunicationsController;

import GUIComponents.AddItemByTextWindow;

public class AddItemByTextTest {
	SupervisionStation su;
	AttendantCommunicationsController at;
	AddItemByTextWindow addItemWindow;
	JOptionPane mockOptionPane;
	String randomItem = "ItemTest";
	Barcode barcode = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
	BarcodedProduct product = new BarcodedProduct(barcode, "BARTest", new BigDecimal("20"), 2.5);
	String code = "55555";
	PriceLookUpCode pluCode = new PriceLookUpCode(Numeral.nine, Numeral.one, Numeral.two, Numeral.three);
	PLUCodedProduct test = new PLUCodedProduct(pluCode, "PLUTest", BigDecimal.valueOf(13.37));
	PriceLookUpCodedUnit pLUCodeUnit = new PriceLookUpCodedUnit(pluCode, 10);

	@Before
	public void Setup() {
		su = new SupervisionStation();
		at = new AttendantCommunicationsController();
		Currency c1 = Currency.getInstance(Locale.CANADA);
		int[] billdenominations = { 5, 10, 15, 20, 50 };
		BigDecimal[] coindenominations = { new BigDecimal("1") };
		at.initializeSelfCheckoutStationLogics(15, c1, billdenominations, coindenominations, 20, 1, null, null, 0, null);
		addItemWindow = new AddItemByTextWindow(su, at);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode, test);
	}

	/**
	 * Ensures that Login Window correctly takes inputs from physical keyboard and
	 * that it accepts valid login credentials
	 */
	@Test
	public void testKeywordTyping() {
		addItemWindow.selectKeyword();
		su.keyboard.disable();
		su.keyboard.enable();
		su.keyboard.type("test");
		assertEquals("test", addItemWindow.getKeyword());
		addItemWindow.deSelectKeyword();
	}

	@Test
	public void testAddKeyboardPanel() {
		JButton button = (JButton) addItemWindow.keyboardPanel.getComponent(0);
		button.doClick();
		assertEquals("1", addItemWindow.getKeyword());
		button = (JButton) addItemWindow.keyboardPanel.getComponent(1);
		button.doClick();
		assertEquals("12", addItemWindow.getKeyword());
		button = (JButton) addItemWindow.keyboardPanel.getComponent(29);
		button.doClick();
		assertEquals("1", addItemWindow.getKeyword());
		addItemWindow.resetButton.doClick();
		button = (JButton) addItemWindow.keyboardPanel.getComponent(39);
		button.doClick();
		assertEquals("", addItemWindow.searchField.getText());
		addItemWindow.resetButton.doClick();
		button = (JButton) addItemWindow.keyboardPanel.getComponent(38);
		button.doClick();
		assertEquals(" ", addItemWindow.searchField.getText());
		addItemWindow.resetButton.doClick();
		button = (JButton) addItemWindow.keyboardPanel.getComponent(37);
		button.doClick();
		assertEquals(".", addItemWindow.searchField.getText());
	}

	@Test
	public void testReset() {
		System.setProperty("java.awt.headless", "true");
		JButton button = (JButton) addItemWindow.resetButton;
		button.doClick();
		assertEquals("Panel has been reset!", addItemWindow.messageField.getText());
	}

	@Test
	public void testSearchAndAddBarcodedProduct() {
		addItemWindow.selectKeyword();
		addItemWindow.resetButton.doClick();
		su.keyboard.type("BARTest");
		addItemWindow.searchButton.doClick();
		assertEquals("1 product(s) have been found!", addItemWindow.messageField.getText());

		addItemWindow.addButton.doClick();
		assertEquals("No products are selected yet!", addItemWindow.messageField.getText());
		addItemWindow.resultsTableModel.setValueAt(true, 0, 0);
		addItemWindow.addButton.doClick();
		assertEquals("1 product(s) have been added.", addItemWindow.messageField.getText());
		assertEquals(1, at.getStationLogicList().get(0).getAddItemController().getBillRecord()
				.getItemQuantity("BARTest").intValue());
		assertEquals(null,
				at.getStationLogicList().get(1).getAddItemController().getBillRecord().getItemQuantity("BARTest"));
		addItemWindow.resetButton.doClick();
		addItemWindow.gobackButton.doClick();
	}

	@Test
	public void testSearchAndAddPLUProduct() throws OverloadException {
		at.getStationList().get(0).scale.add(pLUCodeUnit);
		addItemWindow.selectKeyword();
		addItemWindow.resetButton.doClick();
		su.keyboard.type("PLUTest");
		addItemWindow.searchButton.doClick();
		assertEquals("1 product(s) have been found!", addItemWindow.messageField.getText());
		addItemWindow.addButton.doClick();
		assertEquals("No products are selected yet!", addItemWindow.messageField.getText());
		addItemWindow.resultsTableModel.setValueAt(true, 0, 0);
		addItemWindow.addButton.doClick();
		assertEquals("1 product(s) have been added.", addItemWindow.messageField.getText());

		assertEquals(1, at.getStationLogicList().get(0).getAddItemController().getBillRecord()
				.getItemQuantity("PLUTest").intValue());
		assertEquals(null,
				at.getStationLogicList().get(1).getAddItemController().getBillRecord().getItemQuantity("PLUTest"));
		addItemWindow.resetButton.doClick();
		addItemWindow.gobackButton.doClick();
	}

}
