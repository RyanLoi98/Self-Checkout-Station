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

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.border.Border;

import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Bill;
import com.autovend.Card;
import com.autovend.Coin;
import com.autovend.CreditCard;
import com.autovend.DebitCard;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

public class hardwareSim {
	customerActions customerDo = new customerActions();
	SelfCheckoutStation station;
	Calendar calendar = Calendar.getInstance();
	// Building item1 to add
	Barcode test_barcode1 = new Barcode(Numeral.three, Numeral.zero, Numeral.one, Numeral.five, Numeral.nine, Numeral.nine, Numeral.two, Numeral.seven);
	BarcodedUnit test_scannedItem1 = new BarcodedUnit(test_barcode1, 12);
	BarcodedProduct test_productItem1 = new BarcodedProduct(test_barcode1, "Item 1", new BigDecimal(10.0), 12);
	// Building item2 to add
	Barcode test_barcode2 = new Barcode(Numeral.three, Numeral.zero, Numeral.one, Numeral.five, Numeral.nine, Numeral.nine, Numeral.two, Numeral.eight);
	BarcodedUnit test_scannedItem2 = new BarcodedUnit(test_barcode2, 15);
	BarcodedProduct test_productItem2 = new BarcodedProduct(test_barcode2, "Item 2", new BigDecimal(12.0), 15);
	// Building item3 to add
	Barcode test_barcode3 = new Barcode(Numeral.three, Numeral.zero, Numeral.one, Numeral.five, Numeral.nine, Numeral.nine, Numeral.two, Numeral.nine);
	BarcodedUnit test_scannedItem3 = new BarcodedUnit(test_barcode3, 16);
	BarcodedProduct test_productItem3 = new BarcodedProduct(test_barcode3, "Item 3", new BigDecimal(17.0), 16);
	// Building item array
	BarcodedUnit[] itemArray = {test_scannedItem1, test_scannedItem2, test_scannedItem3};
	int currentItemScan = -1;
	int currentItemBag = -1;
	// Build PLU Item
	PriceLookUpCode PLU = new PriceLookUpCode(Numeral.two, Numeral.zero, Numeral.eight, Numeral.zero);
	PriceLookUpCodedUnit PLU_item1 = new PriceLookUpCodedUnit(PLU, 20);
	PLUCodedProduct PLU_product1 = new PLUCodedProduct(PLU, "PLU ITEM", new BigDecimal(25.0));
	// Building cards to be used
	Card CCard = new CreditCard("credit", "123456", "Jeff", "456", "4321", true, true);
	Card DCard = new DebitCard("debit", "123456", "Jeff", "456", "4321", true, true);
	// Building bills to be used
	Bill billTwenty = new Bill(20, Currency.getInstance("CAD"));
	Bill billFifty = new Bill(50, Currency.getInstance("CAD"));
	// Building coins to be used
	Coin coinLoonie = new Coin(new BigDecimal(1), Currency.getInstance("CAD"));
	Coin coinToonie = new Coin(new BigDecimal(2), Currency.getInstance("CAD"));


	// Buttons
	private JButton addButton_item1Scan;
	private JButton addButton_item1Bag;
	private JButton addButton_item1Rem;
	private JButton addButton_item2Scan;
	private JButton addButton_item2Bag;
	private JButton addButton_item2Rem;
	private JButton addButton_pluItem;
	private JButton addButton_pluItem1;
	private JButton addButton_pluItemRem;
	private JButton addButton3;
	private JButton addButton4;
	private JButton addButton5;
	private JButton addButton6;
	private JButton addButton7;
	
	public hardwareSim(SelfCheckoutStation scs, CardIssuer creditBank, CardIssuer debitBank) {
		JFrame mainFrame = new JFrame("Hardware Simulation");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.add(makePanel());
		mainFrame.setSize(800, 600);
		mainFrame.setVisible(true);
		this.station = scs;
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(test_barcode1, test_productItem1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(test_barcode2, test_productItem2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(test_barcode3, test_productItem3);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLU, PLU_product1);
		calendar.add(Calendar.YEAR, 3);
		creditBank.addCardData("123456",  "Jeff", calendar, "456", BigDecimal.valueOf(1000));
		debitBank.addCardData("123456",  "Jeff", calendar, "456", BigDecimal.valueOf(1000));
	}
	
	public JPanel makePanel() {
		JPanel hardwarePanel = new JPanel();
		hardwarePanel.setLayout(new GridLayout(4,5));
		
		// Building buttons
		addButton_item1Scan = new JButton("Add Item1 by Scanning");
		addButton_item1Bag = new JButton("Place Item1 in Bagging Area");
		addButton_item1Rem = new JButton("Remove Item1 from Bagging Area");
		addButton_item2Scan = new JButton("Add Item2 by Scanning");
		addButton_item2Bag = new JButton("Place Item2 in Bagging Area");
		addButton_item2Rem = new JButton("Remove Item2 from Bagging Area");
		addButton_pluItem = new JButton("Place PLU Item in Bagging Area");
		addButton_pluItem1 = new JButton("Place PLU Item on Scale");
		addButton_pluItemRem = new JButton("Remove PLU Item from Bagging Area");
		addButton3 = new JButton("Insert Credit Card");
		addButton4 = new JButton("Insert Debit Card");
		addButton5 = new JButton("Insert $20 Bill");
		addButton6 = new JButton("Insert $50 Bill");
		addButton7 = new JButton("Insert $1 Coin");
		
		// Adding button events
		addButton_pluItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.placePLUItem(station, PLU_item1);
			}
		});
		addButton_item1Scan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.scanItem(station, itemArray[0]);
			}
		});
		addButton_item1Bag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.bagItem(station, itemArray[0]);
			}	
		});
		addButton_item1Rem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.removeItem(station, itemArray[0]);
			}	
		});
		addButton_item2Scan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.scanItem(station, itemArray[1]);
			}
		});
		addButton_item2Bag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.bagItem(station, itemArray[1]);
			}	
		});
		addButton_item2Rem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.removeItem(station, itemArray[1]);
			}	
		});
		addButton_pluItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.bagPLUItem(station, PLU_item1);
			}	
		});
		addButton_pluItemRem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.removePLUItem(station, PLU_item1);
			}	
		});
		addButton3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.insertCard(station, CCard);
			}	
		});
		addButton4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.insertCard(station, DCard);
			}	
		});
		addButton5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.insertBill(station, billTwenty);
			}	
		});
		addButton6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.insertBill(station, billFifty);
			}	
		});
		addButton7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerDo.insertCoin(station, coinLoonie);
			}
		});
		// Building hardware panel
		hardwarePanel.add(addButton_item1Scan);
		hardwarePanel.add(addButton_item1Bag);
		hardwarePanel.add(addButton_item1Rem);
		hardwarePanel.add(addButton_item2Scan);
		hardwarePanel.add(addButton_item2Bag);
		hardwarePanel.add(addButton_item2Rem);
		hardwarePanel.add(addButton_pluItem);
		hardwarePanel.add(addButton_pluItem1);
		hardwarePanel.add(addButton_pluItemRem);
		hardwarePanel.add(addButton3);
		hardwarePanel.add(addButton4);
		hardwarePanel.add(addButton5);
		hardwarePanel.add(addButton6);
		hardwarePanel.add(addButton7);
		
		return hardwarePanel;
	}
	
	public static void main(String[] args) {
	}

	// Button getters for testing

	public JButton getAddButton_item1Scan() {
		return addButton_item1Scan;
	}

	public JButton getAddButton_item1Bag() {
		return addButton_item1Bag;
	}

	public JButton getAddButton_item1Rem() {
		return addButton_item1Rem;
	}

	public JButton getAddButton_item2Scan() {
		return addButton_item2Scan;
	}

	public JButton getAddButton_item2Bag() {
		return addButton_item2Bag;
	}

	public JButton getAddButton_item2Rem() {
		return addButton_item2Rem;
	}

	public JButton getAddButton_pluItem() {
		return addButton_pluItem;
	}

	public JButton getAddButton_pluItem1() {
		return addButton_pluItem1;
	}

	public JButton getAddButton_pluItemRem() {
		return addButton_pluItemRem;
	}

	public JButton getAddButton4() {
		return addButton4;
	}

	public JButton getAddButton5() {
		return addButton5;
	}

	public JButton getAddButton6() {
		return addButton6;
	}

	public JButton getAddButton7() {
		return addButton7;
	}

	public JButton getAddButton3() {
		return addButton3;
	}
}
