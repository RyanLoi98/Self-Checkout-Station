/** SENG300 Iteration 3 Group 1 Members
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

import com.autovend.software.BillRecord;
import com.autovend.software.CustomerIO;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class ItemsListPanel extends JPanel{
	private CustomerIO customerIO;

	// text area reference for updating item list display
	private JTextArea itemListText;
	private String defaultItemListText;

	// label references for updating total costs
	private JLabel totalCostLabel;
	private JLabel totalPaidLabel;
	private JLabel totalDueLabel;

	// label references for updating language
	private JLabel totalLabel;
	private JLabel paidLabel;
	private JLabel dueLabel;

	// button references for getters to set button behaviour outside this class
	private JButton helpButton;
	private JButton membershipButton;

	public ItemsListPanel(CustomerIO customerIO) {
		this.customerIO = customerIO;

		setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		// set spacing of elements
		constraints.insets = new Insets(10,10,10,10);

		JPanel itemListPanel = makeListPanel(constraints);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weighty = 10;
		add(itemListPanel, constraints);

		// buttons underneath the items list
		helpButton = new JButton("Attendant Help");
		membershipButton = new JButton("Enter Membership");

		helpButton.setFont(GUIConstants.FONT);
		membershipButton.setFont(GUIConstants.FONT);

		constraints.gridwidth = 1;
		constraints.weighty = 1;
		constraints.weightx = 1;
		add(helpButton, constraints);
		constraints.weightx = 2;
		add(membershipButton, constraints);
	}


	/**
	 * makes the items list display and display of totals
	 * @param constraints
	 */
	private JPanel makeListPanel(GridBagConstraints constraints){
		JPanel listPanel = new JPanel(new GridBagLayout());

		JScrollPane itemListPane = new JScrollPane();
		itemListPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		itemListPane.setViewportBorder(new LineBorder(Color.WHITE, 10));

		defaultItemListText = "Items will appear here as you add them to your cart.";
		itemListText = new JTextArea(defaultItemListText);
		itemListText.setFont(GUIConstants.SMALL_FONT);

		JPanel totalsPanel = new JPanel(new GridLayout(3, 2));

		totalLabel = new JLabel("Total:  $");
		totalLabel.setFont(GUIConstants.FONT);
		totalLabel.setHorizontalAlignment(JLabel.RIGHT);
		paidLabel = new JLabel("Paid:  $");
		paidLabel.setFont(GUIConstants.FONT);
		paidLabel.setHorizontalAlignment(JLabel.RIGHT);
		dueLabel = new JLabel("DUE:  $");
		dueLabel.setFont(GUIConstants.FONT);
		dueLabel.setHorizontalAlignment(JLabel.RIGHT);

		totalCostLabel = new JLabel("0.00");
		totalCostLabel.setFont(GUIConstants.FONT);
		totalPaidLabel = new JLabel("0.00");
		totalPaidLabel.setFont(GUIConstants.FONT);
		totalDueLabel = new JLabel("0.00");
		totalDueLabel.setFont(GUIConstants.FONT);

		totalsPanel.add(totalLabel);
		totalsPanel.add (totalCostLabel);
		totalsPanel.add(paidLabel);
		totalsPanel.add (totalPaidLabel);
		totalsPanel.add(dueLabel);
		totalsPanel.add (totalDueLabel);

		JViewport itemListView = new JViewport();
		itemListView.add(itemListText);
		itemListPane.setViewport(itemListView);

		constraints.weighty = 6;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		listPanel.add(itemListPane, constraints);
		constraints.weighty = 1;
		listPanel.add(totalsPanel, constraints);
		return listPanel;
	}


	/**
	 * Updates shown cart using the current state of the given BillRecord
	 * @param record
	 */
	public void refreshCart(BillRecord record) {
		String list = "";

		Set<String> items = record.getItems();
		if (items.size() != 0) {
			for (String name : items) {
				int quantity = record.getItemQuantity(name);
				BigDecimal price = record.getItemCost(name);
				list += String.format("x%d  %s     $%.2f\n", quantity, name, price.doubleValue());
			}
			itemListText.setText(list);

			BigDecimal totalCost = record.getTotal();
			System.out.println(totalCost);
			BigDecimal totalDue = record.getTotalDue();
			BigDecimal totalPaid = totalCost.subtract(totalDue);

			totalCostLabel.setText(totalCost.toString());
			totalPaidLabel.setText(totalPaid.toString());
			totalDueLabel.setText(totalDue.toString());
		} else {
			BigDecimal totalCost = record.getTotal();
			BigDecimal totalDue = record.getTotalDue();
			BigDecimal totalPaid = totalCost.subtract(totalDue);

			totalCostLabel.setText(totalCost.setScale(2, RoundingMode.HALF_UP).toString());
			totalPaidLabel.setText(totalPaid.setScale(2, RoundingMode.HALF_UP).toString());
			totalDueLabel.setText(totalDue.setScale(2, RoundingMode.HALF_UP).toString());
			emptyCart();
		}
	}


	/**
	 * Resets the cart to the default empty cart message
	 */
	public void emptyCart() {
		itemListText.setText(defaultItemListText);
	}


	// getters for changing language

	public String getDefaultItemListText() {
		return defaultItemListText;
	}

	public JLabel getTotalLabel() {
		return totalLabel;
	}

	public JLabel getPaidLabel() {
		return paidLabel;
	}

	public JLabel getDueLabel() {
		return dueLabel;
	}


	// getters for setting button behaviour

	public JButton getHelpButton() {
		return helpButton;
	}

	public JButton getMembershipButton() {
		return membershipButton;
	}
}
