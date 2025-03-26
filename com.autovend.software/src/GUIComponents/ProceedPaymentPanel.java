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
import java.awt.Color;
//import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.autovend.software.CustomerIO;
import com.autovend.software.SelfCheckoutStationLogic;
import com.autovend.software.SelfCheckoutStationLogic.MethodOfPayment;



public class ProceedPaymentPanel {
	
    private JPanel proceedPaymentPanel;

    private JLabel paymentPrompt;

    private MethodOfPayment selectedPayment;
    
    private JButton backButton;
    
	public ProceedPaymentPanel(JPanel GUIContainer, MethodOfPayment methodOfPayment ) {
//    	This panel comes after creditDebitPanel or PaymentPanel and basically prompts the user to put in whatever payment method they chose. 
//    	After running this panel for credit or debit, if the full receipt has not been paid, switch back to payment screen 
		selectedPayment = methodOfPayment;
    	proceedPaymentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        if (selectedPayment == MethodOfPayment.CREDIT) {
        	this.paymentPrompt = new JLabel("Please insert credit card and PIN into card reader");
        }else if (selectedPayment== MethodOfPayment.DEBIT) {
        	this.paymentPrompt = new JLabel("Please insert debit card and PIN into card reader");
        }else if (selectedPayment== MethodOfPayment.GIFT) {
        	this.paymentPrompt = new JLabel("Please insert gift card and PIN into card reader");
        }else {
        	this.paymentPrompt = new JLabel("Please insert Cash to Bill or Coin Slot");
        }
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.CENTER;
        constraints.anchor = GridBagConstraints.CENTER;
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(10, 10, 10, 10);
        Border compound = new CompoundBorder(line, margin);
        this.paymentPrompt.setBorder(compound);
        proceedPaymentPanel.add(paymentPrompt, constraints);
        
        backButton = new JButton("Back");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        proceedPaymentPanel.add(backButton, constraints);
        
        backButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Payment");  
    		}
    	});
	}
	
	public JPanel getPanel() {
    	return proceedPaymentPanel;
    }
	
	public void setMethodOfPayment(MethodOfPayment selectedPayment) {
		this.selectedPayment = selectedPayment;
        if (selectedPayment == MethodOfPayment.CREDIT) {
        	this.paymentPrompt = new JLabel("Please insert credit card and PIN into card reader");
        }else if (selectedPayment== MethodOfPayment.DEBIT) {
        	this.paymentPrompt = new JLabel("Please insert debit card and PIN into card reader");
        }else if (selectedPayment== MethodOfPayment.GIFT) {
        	this.paymentPrompt = new JLabel("Please insert gift card and PIN into card reader");
        }else {
        	this.paymentPrompt = new JLabel("Please insert Cash to Bill or Coin Slot");
        }
    }
	
    // Getters for tests:
    public JButton getBackButton(){return backButton;}
    public JLabel getPaymentPrompt(){return paymentPrompt;}
	
}
