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

package GUIComponents;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;
import com.autovend.software.SelfCheckoutStationLogic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

// For implementation of pictures:
//https://stackoverflow.com/questions/22266506/how-to-add-image-in-jlist
//https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html


public class AddByBrowsingPanel {
    private JFrame frame;
    private JLabel welcomeLabel;
    private JLabel itemLabel;
    private JLabel quantityLabel;
    private JLabel priceLabel;
    private JButton scanButton;
    private JButton payButton;
    private JButton exitButton;
    private JButton plusButton;
    private JButton minusButton;
    private JButton addButton;
	private CustomerIO customerIO;
    private JScrollPane scrollGroceries;

    private String[] demoCatalog;
    private HashMap<String, Product> internalDB;
    private int index = -1;
    private JPanel panel;
    private JList groceries;

    private ListSelectionModel selectModel;

    private int quantity = 0;
    private BigDecimal price = BigDecimal.ZERO;

    private boolean selected = false;
    private boolean isPLU = false;
    private boolean systemBlocked = false;

    // Booleans for testing
    private boolean weightDiscrepancy = false;
    private boolean isHuman = true;
    private boolean isTooHeavy = false;
    public boolean attedantNotified = false;

    public AddByBrowsingPanel(CustomerIO ci, JPanel GUIContainer, SelfCheckoutStationLogic scsl) {
    	
    	customerIO = ci;
        panel = new JPanel();
        panel.setSize(800, 600);
        panel.setLayout(new GridBagLayout());


        GridBagConstraints buttonDesign = new GridBagConstraints();


        demoCatalog = getCatalogueFrontend().toArray(new String[0]);
        internalDB = getCatalogueBackend();
        groceries = new JList(demoCatalog);
        groceries.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        groceries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scrollGroceries = new JScrollPane(groceries);

        welcomeLabel = new JLabel("Add item by browsing");
        welcomeLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        exitButton = new JButton("Cancel");
        exitButton.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        plusButton = new JButton("+");
        plusButton.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        minusButton = new JButton("-");
        minusButton.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        addButton = new JButton("Add to Bill");
        addButton.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
        priceLabel = new JLabel("Price:");
        priceLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));

        buttonDesign.weightx = 0.5;
        buttonDesign.fill = GridBagConstraints.HORIZONTAL;
        buttonDesign.gridx = 0;
        buttonDesign.gridy = 0;
        buttonDesign.weighty = 1;
        panel.add(welcomeLabel, buttonDesign);

        buttonDesign.weightx = 0;
        buttonDesign.fill = GridBagConstraints.HORIZONTAL;
        buttonDesign.gridx = 0;
        buttonDesign.gridy = 1;
        buttonDesign.gridwidth = 3;
        buttonDesign.gridheight = 2;
        panel.add(scrollGroceries, buttonDesign);

        buttonDesign.weightx = 0.5;
        buttonDesign.weighty = 1;
        buttonDesign.fill = GridBagConstraints.HORIZONTAL;
        buttonDesign.gridx = 0;
        buttonDesign.gridy = 3;
        buttonDesign.gridwidth = 1;
        buttonDesign.gridheight = 1;
        panel.add(quantityLabel, buttonDesign);

        buttonDesign.weightx = 0.5;
        buttonDesign.weighty = 1;
        buttonDesign.fill = GridBagConstraints.BOTH;
        buttonDesign.gridwidth = 1;
        buttonDesign.gridheight = 2;
        buttonDesign.gridx = 1;
        buttonDesign.gridy = 3;
        panel.add(plusButton, buttonDesign);

        buttonDesign.weightx = 0.5;
        buttonDesign.weighty = 1;
        buttonDesign.fill = GridBagConstraints.BOTH;
        buttonDesign.gridwidth = 1;
        buttonDesign.gridheight = 2;
        buttonDesign.gridx = 2;
        buttonDesign.gridy = 3;
        panel.add(minusButton, buttonDesign);

        buttonDesign.weightx = 0.5;
        buttonDesign.weighty = 1;
        buttonDesign.fill = GridBagConstraints.BOTH;
        buttonDesign.gridwidth = 1;
        buttonDesign.gridheight = 2;
        buttonDesign.gridx = 2;
        buttonDesign.gridy = 4;
        panel.add(addButton, buttonDesign);

        buttonDesign.weightx = 0.5;
        buttonDesign.weighty = 1;
        buttonDesign.fill = GridBagConstraints.BOTH;
        buttonDesign.gridwidth = 1;
        buttonDesign.gridheight = 2;
        buttonDesign.gridx = 0;
        buttonDesign.gridy = 4;
        panel.add(priceLabel, buttonDesign);

        buttonDesign.weightx = 0.5;
        buttonDesign.weighty = 1;
        buttonDesign.gridx = 0;
        buttonDesign.gridy = 7;
        buttonDesign.fill = GridBagConstraints.BOTH;
        buttonDesign.gridwidth = 1;
        buttonDesign.gridheight = 2;
        panel.add(exitButton, buttonDesign);


        // Not to be tested as this simply switches panels on the GUI
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isHuman)
                    ((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Adding");
            }
        });

        selectModel = groceries.getSelectionModel();
        selectModel.addListSelectionListener(x -> {
            if (systemBlocked)
                return;
            if (!groceries.getValueIsAdjusting()) {
                index = groceries.getSelectedIndex();
                if (index != -1) {
                    System.out.println("You have selected " + demoCatalog[index]);
                    // Barcoded products are priced by unit
                    if (internalDB.get(demoCatalog[index]) instanceof BarcodedProduct){
                        isPLU = false;
                        quantity = 1;
                        price = internalDB.get(demoCatalog[index]).getPrice();
                        updateLabels();
                        selected = true;
                    } else { // PLU products are priced by kg
                        quantity = 1;
                        isPLU = true;
                        price = internalDB.get(demoCatalog[index]).getPrice();
                        updateLabels();
                        selected = true;
                    }
                }
            }
        });

        plusButton.addActionListener(e -> {
            if (!selected || isPLU || systemBlocked)
                return;
            quantity++;
            updateLabels();
        });

        minusButton.addActionListener(e -> {
            if (!selected || isPLU || systemBlocked)
                return;
            if (quantity > 1)
                quantity --;
            updateLabels();
        });

        addButton.addActionListener(e ->{
            if (systemBlocked)
                return;
            if (selected){
                systemBlocked = true;

                // Communicate with the hardware what occurred. Abstracted during testing
				for (CustomerIOListener communicator : customerIO.getListeners()) {
                    if (internalDB.get(demoCatalog[index]) instanceof BarcodedProduct)
					    communicator.selectedBarcodedItem(ci, scsl, internalDB.get(demoCatalog[index]), quantity);
                    else // When PLU Coded product, no need to pass in quantity
                        communicator.selectedItem(ci, scsl, internalDB.get(demoCatalog[index]));
				}
                if (isHuman)
                    ((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Adding");
                
//                startAddingBySearch(internalDB.get(demoCatalog[index])) // Use this so child class given, not Product
                if (!weightDiscrepancy){
                    if (isTooHeavy){
                        attedantNotified = true;
                    }
                    System.out.println(demoCatalog[index] + " x"+ quantity +" added to bill!");
                    groceries.clearSelection();
                    quantity = 0;
                    price = BigDecimal.ZERO;
                    index = -1;
                    quantityLabel.setText("Quantity: ");
                    priceLabel.setText("Price: ");
                    selected = false;
                    systemBlocked = false;
                } else {
                    systemBlocked = true;
                    attedantNotified = true;
                }
            }
        });


    }


    private void updateLabels(){
        quantityLabel.setText("Quantity: " + quantity);
        BigDecimal showPrice = price.multiply(BigDecimal.valueOf(quantity));
        if(isPLU)
            priceLabel.setText("Price/kg: $"+showPrice);
        else
            priceLabel.setText("Price: $"+showPrice);
    }
    /**
     * A method that generates a HashMap of products that are currently available for the system to index.
     * Indexing is done by using the description (which is what the customer selects)
     * @return HashMap<String, Product> catalogue - the current available products
     */
    private HashMap<String, Product> getCatalogueBackend(){
        // Make a HashMap<String, Product> of each product
        // String is the product description and product is its respective product
        HashMap<String, Product> catalogue = new HashMap<>();

        // Go through the values for PLU products in Map<PriceLookUpCode, PLUCodedProduct>
        for (PLUCodedProduct plu : ProductDatabases.PLU_PRODUCT_DATABASE.values()){
            catalogue.put(plu.getDescription(), plu);
        }

        // Go through the values for Barcoded products in Map<Barcode, BarcodedProduct>
        for (BarcodedProduct bar : ProductDatabases.BARCODED_PRODUCT_DATABASE.values()){
            catalogue.put(bar.getDescription(), bar);
        }

        return catalogue;
    }

    /**
     * A method that generates an ArrayList<String> of all current Products in the database
     * The descriptions are sorted alphabetically to help the customer find their product
     * @return A sorted ArrayList<String> of current Product descriptions
     */
    private ArrayList<String> getCatalogueFrontend(){
        // Generate a catalogue of product descriptions
        ArrayList<String> catalogue = new ArrayList<>();

        // Go through the values for PLU products in Map<PriceLookUpCode, PLUCodedProduct>
        for (PLUCodedProduct plu : ProductDatabases.PLU_PRODUCT_DATABASE.values()){
            catalogue.add(plu.getDescription());
        }

        // Go through the values for Barcoded products in Map<Barcode, BarcodedProduct>
        for (BarcodedProduct bar : ProductDatabases.BARCODED_PRODUCT_DATABASE.values()){
            catalogue.add(bar.getDescription());
        }

        // Return the catalogue in alphabetical order
        Collections.sort(catalogue);
        return catalogue;
    }

    /**
     * Clears item selection to prevent persistence of selection between sessions.
     */
    public void clearSelection() {
        groceries.clearSelection();
        updateLabels();
    }

    /**
     * Generates a panel for AddByBrowsing
     * @return the above-described panel
     */
    public JPanel getAddByBrowsingPanel(){
        return this.panel;
    }

    // Getters for testing:
    public JButton getPlusButton(){return  this.plusButton;}
    public JButton getMinusButton(){return  this.minusButton;}
    public JButton getAddButton(){return  this.addButton;}
    public String getQuantityLabelText(){return this.quantityLabel.getText();}
    public String getPriceLabelText(){return this.priceLabel.getText();}
    public JList getSelection(){return this.groceries;}
    public ListSelectionModel getSelectModel(){return this.selectModel;}


    public void isTest(){isHuman = false;}
    public void setWeightDiscrepancy(){weightDiscrepancy = true;}
    public void setTooHeavy(){isTooHeavy = true;}
    public void attendantVerified(){systemBlocked = false; weightDiscrepancy = false;}
}
