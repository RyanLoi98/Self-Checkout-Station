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

import java.awt.CardLayout;

//import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;
import com.autovend.software.SelfCheckoutStationLogic;


public class CreditDebitPanel {
	
    private JPanel creditDebitPanel;
	private JTextField textField;
	
	public CreditDebitPanel(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic, JPanel GUIContainer) {
    	creditDebitPanel = new JPanel();
    	creditDebitPanel.setLayout(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.insets = new Insets(10, 10, 10, 10);
    	gbc.fill = GridBagConstraints.HORIZONTAL;
        
    	JLabel label = new JLabel("Please Select Payment Amount:");
        label.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        creditDebitPanel.add(label, gbc);
        
        textField = new JTextField();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        creditDebitPanel.add(textField, gbc);
        
        JPanel keyboardPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        creditDebitPanel.add(keyboardPanel, gbc);

        // Add buttons for each number to the keyboard panel
        for (int i = 0; i < 10; i++) {
        	final int finalI = i;
            JButton button = new JButton(String.valueOf(i));
            button.addActionListener(e -> textField.setText(textField.getText() + finalI));

            if ((9-i) % 3 == 0)
            	gbc.gridx = 2;
            else if ((9-i) % 3 == 1)
            	gbc.gridx = 1;
            else
            	gbc.gridx = 0;
            gbc.gridy = (9-i) / 3;
            if (i == 0) {
            	gbc.gridx = ((9-i) % 3) + 1;
            }
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            keyboardPanel.add(button, gbc);
        }

        // Add a key listener to the text field to listen for key presses and add the pressed number
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c)) {
                    textField.setText(textField.getText() + c);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        JButton cancelButton = new JButton("Cancel");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        creditDebitPanel.add(cancelButton, gbc);
        
        cancelButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Payment");
				// clear number entered so far, so it doesn't persist when screen re-entered
				resetAmountToPayField();
    		}
    	});

        // Create a button and add it to the bottom right corner
        JButton payButton = new JButton("Pay");
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        creditDebitPanel.add(payButton, gbc);
        
        payButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			BigDecimal amountToPay = new BigDecimal(textField.getText());
    			BigDecimal amountDue = scsLogic.getAddItemController().getBillRecord().getTotalDue();
    			if (amountToPay.compareTo(amountDue) > 0) {
    				amountToPay = amountDue;
    			}
//    			selectedPayment = customerIO.getPaymentPanel().getMethodOfPayment();
//				customerIO.notifyCardPaymentAttempt(selectedPayment.name().toLowerCase(), new BigDecimal(amountToPay));
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "ProceedPayment");  
				for (CustomerIOListener communicator : customerIO.getListeners()) {
					System.out.println(textField.getText());
					System.out.println("field entered");
					System.out.println(scsLogic.getSelectedPayment());

					communicator.setCardPaymentAmount(customerIO, scsLogic, amountToPay);
				}

				// clear number entered so far, so it doesn't persist when screen re-entered
				resetAmountToPayField();
    		}
    	});
        
        JButton deleteButton = new JButton("Del");
        deleteButton.addActionListener(e -> {
            String currentText = textField.getText();
            if (currentText.length() > 0) {
                textField.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        keyboardPanel.add(deleteButton, gbc);
        
        JButton decimalButton = new JButton(".");
        decimalButton.addActionListener(e -> {
            String currentText = textField.getText();
            if (!currentText.contains(".")) {
                textField.setText(currentText + ".");
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        keyboardPanel.add(decimalButton, gbc);
        
        JButton billTotal = new JButton("Add Bill Total");
        billTotal.addActionListener(e -> {
        	//TODO: implement total amount
        	textField.setText("total amount");
        });
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        creditDebitPanel.add(billTotal, gbc);
        
	}
	
	public JPanel getPanel() {
    	return creditDebitPanel;
    }

	/**
	 * Clears the text field indicating the specified amount the customer wishes to pay,
	 * so the number doesn't persist when screen exited and re-entered.
	 */
	public void resetAmountToPayField() {
		textField.setText(null);
	}
}
