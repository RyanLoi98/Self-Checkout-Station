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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.swing.*;

import com.autovend.software.BillRecord;
import com.autovend.software.CustomerIO;
import com.autovend.software.CustomerIOListener;
import com.autovend.software.SelfCheckoutStationLogic;
import com.autovend.software.SelfCheckoutStationLogic.MethodOfPayment;


public class PaymentPanel {
	
    private JPanel paymentPanel;
	private CustomerIO customerIO;
	private SelfCheckoutStationLogic scsLogic;
    private JTextArea itemListText;
    private JLabel total;
    private JLabel paid;
    private JLabel due;
   

    // buttons
    private JButton payCreditButton;
    private JButton payDebitButton;
    private JButton payCashButton;
    private JButton payGiftButton;
    private JButton keepScanningButton;


    // button getters
    public JButton getKeepScanningButton() {
        return keepScanningButton;
    }

    public JButton getPayCreditButton() {
        return payCreditButton;
    }

    public JButton getPayDebitButton() {
        return payDebitButton;
    }

    public JButton getPayCashButton() {
        return payCashButton;
    }

    public JButton getPayGiftButton() {
        return payGiftButton;
    }

    public PaymentPanel(CustomerIO customerIOIn, SelfCheckoutStationLogic scsLogicIn, JPanel GUIContainer) {
		this.customerIO = customerIOIn;
		this.scsLogic = scsLogicIn;

		paymentPanel = new JPanel();
    	paymentPanel.setLayout(new GridLayout(1,2));
    	paymentPanel.setBounds(0, 0, 450, 300);

        int inset_amount = 10;  // for easier modification
        Insets insets = new Insets(inset_amount, inset_amount, inset_amount, inset_amount);

        // components for the left half of the screen

        paymentPanel.add(makeItemListPanel(insets,GUIContainer));

        // ===============================================================
        // right half of the screen
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(400, 300));
        /* components added from right to left. needed for Add Own Bags and Purchase Bags
         * buttons to be laid out together vertically beside Finish and Pay */
//        right.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        right.setLayout(new GridBagLayout());
        // default constraints used to specify characteristics like fill-to-width, relative size for GridBagLayout
        GridBagConstraints constraints = new GridBagConstraints();

        payCreditButton = new JButton("Pay with Credit");
        payDebitButton = new JButton("Pay with Debit");
        payCashButton = new JButton("Pay with Cash");
        payGiftButton = new JButton("Pay with Gift Card");
        keepScanningButton = new JButton("Keep Scanning");

        // set spacing of elements
        constraints.insets = insets;

        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
//        constraints.gridx=0;
        constraints.gridy=0;
        right.add(payCreditButton, constraints);
        right.add(payDebitButton, constraints);

        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.gridheight =1;
        constraints.weighty = 1;
        constraints.gridy=1;
        constraints.weightx = 1;
        right.add(payCashButton, constraints);
        right.add(payGiftButton, constraints);

        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.gridheight = 1;     // return number of grid rows it takes up to default
        constraints.weightx = 1;        // make it less wide than payment button
        constraints.gridx=1;
        constraints.gridy=2;
        right.add(keepScanningButton, constraints);

        payCashButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			customerIO.makeProceedPaymentPanel(MethodOfPayment.CASH);
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "ProceedPayment");
				for (CustomerIOListener communicator : customerIO.getListeners()) {
					communicator.startCashPayment(customerIO, scsLogic);
				}
                customerIO.updatePaymentPanelBill();
    		}
    	});

    	payCreditButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			customerIO.makeProceedPaymentPanel(MethodOfPayment.CREDIT);
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "CreditDebit");
				for (CustomerIOListener communicator : customerIO.getListeners()) {
					communicator.setCardPaymentType(customerIO, scsLogic, MethodOfPayment.CREDIT);
				}
                customerIO.updatePaymentPanelBill();
    		}
    	});

    	payDebitButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			customerIO.makeProceedPaymentPanel(MethodOfPayment.DEBIT);
				((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "CreditDebit");
				for (CustomerIOListener communicator : customerIO.getListeners()) {
					communicator.setCardPaymentType(customerIO, scsLogic, MethodOfPayment.DEBIT);

					System.out.println("on return");
					System.out.println(scsLogic.getSelectedPayment());
				}
                customerIO.updatePaymentPanelBill();
    		}
    	});

    	payGiftButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			customerIO.makeProceedPaymentPanel(MethodOfPayment.GIFT);
    			((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "CreditDebit");
    			for (CustomerIOListener communicator : customerIO.getListeners()) {
    				communicator.setCardPaymentType(customerIO, scsLogic, MethodOfPayment.GIFT);
    			}
                customerIO.updatePaymentPanelBill();
    		}
    	});


        keepScanningButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("Back to addItem");
        		((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Adding");
				for (CustomerIOListener communicator : customerIO.getListeners()) {
					communicator.returnToAddFromPayment(customerIOIn, scsLogicIn);
				}
        	}
        });


        paymentPanel.add(right);

	}
	
	public JPanel getPanel() {
    	return paymentPanel;
    }

    public void updateBill(){
        itemListText.setText("");
        String billString = "";
        BillRecord bill = scsLogic.getBillRecord();
        HashMap<String, BigDecimal> items = bill.getRecord();

        for (String item : items.keySet()){
            Integer quantity = bill.getItemQuantity(item);
            billString += item+"\t x"+quantity+"\t $"+items.get(item)+" each\n";
        }

        total.setText("Total: $"+bill.getTotal());
        paid.setText("Paid: $"+(bill.getTotal().subtract(bill.getTotalDue())));
        due.setText("Due: $"+bill.getTotalDue());

        itemListText.append(billString);
    }
	
	public JPanel makeItemListPanel(Insets insets, JPanel GUIContainer){
    	// components for the left half of the screen
        JPanel left = new JPanel();
        //left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setLayout(new GridBagLayout());

        GridBagConstraints leftConstraints = new GridBagConstraints();
        leftConstraints.fill = GridBagConstraints.BOTH;
        // set spacing of elements
        leftConstraints.insets = insets;

        JScrollPane itemListPane = new JScrollPane();
        //itemListPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemListPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        itemListText = new JTextArea("");
        itemListText.setFont(GUIConstants.FONT);

        total = new JLabel();
        paid = new JLabel();
        due = new JLabel();

        total.setText("Total: ");
        total.setFont(GUIConstants.FONT);

        paid.setText("Paid: ");
        paid.setFont(GUIConstants.FONT);

        due.setText("Due: ");
        due.setFont(GUIConstants.FONT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(0,3));
        textPanel.add(new JLabel(""));
        textPanel.add(total);
        textPanel.add(new JLabel(""));
        textPanel.add(new JLabel(""));
        textPanel.add(paid);
        textPanel.add(new JLabel(""));
        textPanel.add(new JLabel(""));
        textPanel.add(due);
        textPanel.add(new JLabel(""));



        JViewport itemListView = new JViewport();
        itemListView.add(itemListText);
        itemListPane.setViewport(itemListView);

        leftConstraints.weighty = 6;
        leftConstraints.gridwidth = GridBagConstraints.REMAINDER;
        left.add(itemListPane, leftConstraints);
        leftConstraints.weighty = 1;
        left.add(textPanel, leftConstraints);

        leftConstraints.fill = GridBagConstraints.BOTH;

        // buttons underneath the items list
        //JPanel leftButtons = new JPanel();
        JButton attendantHelpButton = new JButton("Attendant Help");
        JButton enterMemberShipButton = new JButton("Enter Membership");

        attendantHelpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "Adding");
            }
        });
        enterMemberShipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) GUIContainer.getLayout()).show(GUIContainer, "EnterMembership");
            }
        });

        leftConstraints.gridwidth = 1;
        leftConstraints.weighty = 1;
        leftConstraints.weightx = 1;
        left.add(attendantHelpButton, leftConstraints);
        leftConstraints.weightx = 2;
        left.add(enterMemberShipButton, leftConstraints);
        
        return left;
    }
	
//	public MethodOfPayment getMethodOfPayment() {
//		return selectedPayment;
//	}
	
	
	
}
