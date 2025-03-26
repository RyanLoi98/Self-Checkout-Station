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

import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.products.PLUCodedProduct;
import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.*;

import static com.autovend.external.ProductDatabases.PLU_PRODUCT_DATABASE;

public class AddItemByPLUPanel {
    private JFrame frame;
    private JPanel panel;
    private JLabel welcomeLabel;
    private JLabel itemLabel;
    private JLabel quantityLabel;
    private JLabel priceLabel;
    private JButton scanButton;
    private JButton payButton;
    private JButton exitButton;
    private JButton zeroButton;
    private JButton oneButton;
    private JButton twoButton;
    private JButton threeButton;
    private JButton fourButton;
    private JButton fiveButton;
    private JButton sixButton;
    private JButton sevenButton;
    private JButton eightButton;
    private JButton nineButton;
    private JButton addButton;

    private JButton delButton;
    private JButton enterButton;
    PLUCodedProduct item;

    private JButton plusButton;
    private JButton minusButton;

    private String pluCode;
    CustomerIO customerIO;

    private Integer quantity = 0;
    private BigDecimal price = BigDecimal.ZERO;

    private boolean itemSelected = false;
    private boolean systemBlocked = false;

    // For testing:
    private boolean weightDiscrepancy = false;  // Set a weight discrepancy
    public boolean itemTooHeavy = false;
    public boolean attendantAlerted = false;    // Simulate attendant notification
    private boolean isHuman = true;

    public AddItemByPLUPanel(JPanel GUIContainer, CustomerIO cIO) {
    	customerIO = cIO;

        panel = new JPanel();
        welcomeLabel = new JLabel("Add item by PLU");
        panel.setSize(800, 600);
        GridLayout panelLayout = new GridLayout(0,6);
        panel.setLayout(panelLayout);
        itemLabel = new JLabel("Item: ");
        pluCode = "";
        quantityLabel = new JLabel("Quantity:");
        priceLabel = new JLabel("Price/kg:");

        addButton = new JButton("Add Item to Bill");



         zeroButton = new JButton("0");
         oneButton = new JButton("1");
         twoButton = new JButton("2");
         threeButton = new JButton("3");
         fourButton = new JButton("4");
         fiveButton = new JButton("5");
         sixButton = new JButton("6");
         sevenButton = new JButton("7");
         eightButton = new JButton("8");
         nineButton = new JButton("9");

        delButton = new JButton("Delete");
        enterButton = new JButton("Enter");



        exitButton = new JButton("Cancel");

        panel.add(itemLabel);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(sevenButton);
        panel.add(eightButton);
        panel.add(nineButton);

        panel.add(priceLabel);
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(fourButton);
        panel.add(fiveButton);
        panel.add(sixButton);

        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        panel.add(oneButton);
        panel.add(twoButton);
        panel.add(threeButton);

        panel.add(exitButton);
        panel.add(new JLabel(""));
        panel.add(addButton);
        panel.add(zeroButton);
        panel.add(delButton);
        panel.add(enterButton);


        enterButton.addActionListener(e -> {
            if (systemBlocked)
                return;
            if (itemSelected)
                return;
            // Check if PLU code is a valid length
            if (pluCode.length() >= 4 && pluCode.length() < 6){
                item = PLU_PRODUCT_DATABASE.get(inputToPLU(pluCode));
                if (item != null) {
                    String description = item.getDescription();
                    itemLabel.setText("Item: "+description);

                    price = item.getPrice(); // The price
                    BigDecimal displayPrice = price;
                    displayPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    priceLabel.setText("Price/kg: $" + displayPrice);

                    quantity = 1;
//                    quantityLabel.setText("Quantity: "+quantity);

                    itemSelected = true;

                } else {
                    resetEncounter();
                    itemLabel.setText("Item unavailable!");
                }
            } else {
                itemLabel.setText("Invalid PLU Code: " + pluCode);
            }
        });

        addButton.addActionListener( e -> {
            if (systemBlocked)
                return;
            if (itemSelected){
                systemBlocked = true;
                if (!weightDiscrepancy) {
                    if (itemTooHeavy)
                        attendantAlerted = true;
                    // Communicate with the hardware what occurred
                    for (CustomerIOListener communicator : customerIO.getListeners()) {
                        communicator.enteredPLU(customerIO, customerIO.getScsLogic(), item.getPLUCode());
                    }


                    resetEncounter();
                    itemLabel.setText("Item(s) added to bill!");
                    if (isHuman) // Changes panel. When testing it should not occur.
                        ((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Adding");
                } else {
                    // Simulate attendant alert when there is a weight discrepancy
                    attendantAlerted = true;
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Going back to previous panel!");
                resetEncounter();
                if (isHuman)
                    ((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Adding");
            }
        });

        // Let the number buttons alter stored PLU. Changes shown on itemLabel
        JButton numbers[] = {oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton,
                eightButton, nineButton, zeroButton};
        for (JButton num : numbers) {
            num.addActionListener(e -> {
                if (systemBlocked)
                    return;
                if (itemSelected)
                    return;
                if (pluCode.length() < 5) {
                    pluCode += num.getText();
                    String displayCode = "Item: " + pluCode;
                    itemLabel.setText(displayCode);
                }
            });
        }

        // Delete numbers off the PLU. Changes shown on itemLabel
        delButton.addActionListener(e ->{
            if (systemBlocked)
                return;
            if (itemSelected){
                // Delete currently selected item
                resetEncounter();
            }
            if (pluCode.length() > 1){
                pluCode = pluCode.substring(0, pluCode.length()-1);
            } else  {
                pluCode = "";
            }
            String displayCode = "Item: " + pluCode;
            itemLabel.setText(displayCode);
        });
    }


    /**
     * Reset the state of the panel to first entry
     */
    public void resetEncounter(){
        pluCode = "";
        String displayCode = "Item: " + pluCode;
        itemLabel.setText(displayCode);
        price = BigDecimal.ZERO;
        priceLabel.setText("Price/kg: ");
        itemSelected = false;
        systemBlocked = false;
    }

    /**
     * Take in a valid PLU code (validated by checkPLUInput) and convert it into a PLU to look up
     * To be used to index PLU_PRODUCT_DATABASE and get the desired PLUProduct
     * @param code - A valid PLU code given as a string
     * @return Code converted into a PLU
     */
    private PriceLookUpCode inputToPLU(String code){
        // Initialize an array of Numeral to make out PLU
        Numeral[] numerals = new Numeral[code.length()];
        // For each char in code, convert it to a Numeral and putu it into numerals[]
        for (int i = 0; i < code.length(); i++) {
            numerals[i] = Numeral.valueOf((byte) Integer.parseInt(String.valueOf(code.charAt(i))));
        }
        PriceLookUpCode pluCode = new PriceLookUpCode(numerals);
        return pluCode;
    }

    public JPanel getAddingByPLUPanel(){  // Getter for panel. Should not be tested.
        return this.panel;
    }

//    private void addtoBillPopUp(JFrame frame){
//        // Generate a GUI telling customer to add the item. If too heavy, then call attendant.
//        int selection = JOptionPane.showConfirmDialog(frame,
//                "Please add item to bagging area.\n" +
//                        "Is attendant assistance required?",
//                "Add item to bagging area",
//                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//        if (selection == JOptionPane.NO_OPTION) {
//            System.out.println("No button clicked");
//        } else if (selection == JOptionPane.YES_OPTION) {
//            System.out.println("Yes button clicked");
//            JOptionPane.showMessageDialog(frame, "Calling attendant...");
//        }
//        systemBlocked = false;
//    }

    // Getters for tests:
    public JButton getDelete(){return delButton;}
    public JButton getEnter(){return enterButton;}
    public JButton getAddToBill(){return addButton;}
    public JButton getFiveButton(){return fiveButton;}
    public JButton getExitButton(){return exitButton;}

    public BigDecimal getPrice(){return price;}
    public String getEnteredPLU(){return pluCode;};
    public String getItemLabelOutput(){return itemLabel.getText();}
    public String getPriceLabelOutput(){return priceLabel.getText();}

    public void setValidWeightAdded(){weightDiscrepancy = false; systemBlocked = false;}
    public void setInvalidWeightAdded(){weightDiscrepancy = true;}
    public void isTest(){isHuman = false;}
    public boolean isBlocked(){return systemBlocked;}
}
