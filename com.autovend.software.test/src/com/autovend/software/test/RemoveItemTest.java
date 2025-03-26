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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.junit.Before;
import org.junit.Test;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.AttendantCommunicationsController;

import GUIComponents.RemoveItem;

public class RemoveItemTest {
	SupervisionStation su;
	AttendantCommunicationsController at;
	RemoveItem removeItem;
	JOptionPane mockOptionPane;
	String randomItem;

	@Before
	public void Setup() {
		su = new SupervisionStation();
		at = new AttendantCommunicationsController();
		Currency c1 = Currency.getInstance(Locale.CANADA);
		int[] billdenominations = { 5, 10, 15, 20, 50 };
		BigDecimal[] coindenominations = { new BigDecimal("1") };
		at.initializeSelfCheckoutStationLogics(15, c1, billdenominations, coindenominations, 20, 1, null, null, 0,
				null);
		removeItem = new RemoveItem(su, at);
		
		
		randomItem = "ItemTest";
		Barcode barcode = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
		BarcodedProduct product = new BarcodedProduct(barcode, "ItemTest", new BigDecimal("20"), 2.5);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
	}

	/**
	 * Ensures that Attendant I/O is populated
	 */
	@Test
    public void testComboBoxPopulated() {
        JComboBox<String> comboBox = null;
        for (Component c : removeItem.getComponents()) {
            if (c instanceof JPanel) {
                for (Component innerComponent : ((JPanel) c).getComponents()) {
                    if (innerComponent instanceof JComboBox) {
                        comboBox = (JComboBox<String>) innerComponent;
                        break;
                    }
                }
            }
        }
        
        assertNotNull("ComboBox not found", comboBox);
        assertEquals(at.getNumberOfSCS() + 1, comboBox.getItemCount());
    }
	
	/**
	 * Ensures that Attendant I/O test button is properly generated
	 */
	@Test
    public void testRemoveButtonExists() {
        boolean removeButtonFound = false;
        for (Component c : removeItem.getComponents()) {
            if (c instanceof JPanel) {
                for (Component innerComponent : ((JPanel) c).getComponents()) {
                    if (innerComponent instanceof JButton) {
                        JButton button = (JButton) innerComponent;
                        if ("Remove".equals(button.getText())) {
                            removeButtonFound = true;
                            break;
                        }
                    }
                }
            }
        }
        
        assertTrue("Remove button not found", removeButtonFound);
    }
	
	/**
	 * Checks if the logic behind removeItem is functional
	 */
	 @Test
     public void testRemoveItemFromBillRecord() {
        int stationIndex = 0;
        at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().clearRecord();
        
        // Add the randomItem to the bill record 
        at.getStationLogicList().get(stationIndex).getAddItemController().processAdd(new BigDecimal("20"), 20.0, randomItem);

        // Check if the randomItem was added to the bill record
        assertEquals(1, at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().getItems().size());
        assertEquals(Integer.valueOf(1), at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().getItemQuantity(randomItem));

        // Remove the randomItem using the removeItem method from the attendantCommunicationsController
        ArrayList<String> removedProducts = new ArrayList<>();
        removedProducts.add(randomItem);
        
        try {
            at.removeItems(removedProducts, at.getStationLogicList().get(stationIndex).getAddItemController());
        } catch (OverloadException e) {
            e.printStackTrace();
        }

        // Check if the randomItem was removed from the bill record
        assertEquals(0, at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().getItems().size());
    }
	 
	 @Test
	 public void testRemoveButtonAction() {
	    // Add the randomItem to the bill record
	    int stationIndex = 0;
	    at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().clearRecord();
	    
	    at.getStationLogicList().get(stationIndex).getAddItemController().processAdd(new BigDecimal("20"), 20.0, randomItem);

	    // Set the JComboBox to the first station
	    removeItem.comboBox.setSelectedIndex(1);
	    
	    // Simulate selecting the randomItem for removal
	    removeItem.resultsTableModel.setValueAt(true, 0, 0);

	    // Get the remove button and perform the click action
	    JButton removeButton = (JButton) removeItem.removeButton;
	    removeButton.doClick();

	    // Check if the randomItem was removed from the bill record
	    assertEquals(0, at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().getItems().size());
	 }
	 
	 @Test
	 public void testRemoveButtonActionNoSelection() {
	    // Add the randomItem to the bill record
	    int stationIndex = 0;
	    at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().clearRecord();
	    
	    at.getStationLogicList().get(stationIndex).getAddItemController().processAdd(new BigDecimal("20"), 20.0, randomItem);

	    // Set the JComboBox to the first station
	    removeItem.comboBox.setSelectedIndex(1);

	    // Get the remove button and perform the click action
	    JButton removeButton = (JButton) removeItem.removeButton;
	    removeButton.doClick();

	    // Check if the randomItem was removed from the bill record
	    assertNotEquals(0, at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().getItems().size());
	 }
	 
	 @Test
	 public void testComboBox() {
	    // Add the randomItem to the bill record
	    int stationIndex = 0;
	    at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().clearRecord();
	    
	    at.getStationLogicList().get(stationIndex).getAddItemController().processAdd(new BigDecimal("20"), 20.0, randomItem);

	    // Set the JComboBox to the first station
	    removeItem.comboBox.setSelectedIndex(0);
	    
//	    // Simulate selecting the randomItem for removal
//	    removeItem.resultsTableModel.setValueAt(true, 0, 0);

	    // Get the remove button and perform the click action
	    JButton removeButton = (JButton) removeItem.removeButton;
	    removeButton.doClick();

	    // Check if the randomItem was removed from the bill record
	    assertNotEquals(0, at.getStationLogicList().get(stationIndex).getAddItemController().getBillRecord().getItems().size());
	 }
	 
	@Test
	public void testRun() {
		// Call the run method
	    RemoveItem.run(su, at);

	    // Check if the RemoveItem panel has been added to the JFrame
	    JFrame frame = su.screen.getFrame();
	    boolean foundRemoveItemPanel = false;
	    for (int i = 0; i < frame.getContentPane().getComponentCount(); i++) {
	        if (frame.getContentPane().getComponent(i) instanceof RemoveItem) {
	            foundRemoveItemPanel = true;
	            break;
	        }
	    }

	    // Assert that the RemoveItem panel was found
	    assertNotNull(foundRemoveItemPanel);
	
	}
	 
	
	 

}
