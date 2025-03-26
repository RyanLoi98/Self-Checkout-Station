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
import com.autovend.software.CustomerIO;
import static com.autovend.software.PurchaseBagsController.BAG_COST;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PurchaseBagsPanel extends JPanel {
	/*
	 * This is what CONSTRAINS the layout; it gets the information of the 
	 * coordinates of each element.
	 */
	private int frameWidth;
	private int frameHeight;
	private CustomerIO customerIO;
	private GridBagConstraints gbc = new GridBagConstraints();
	
	private JTextField currentTotal;
	private JLabel bagsAvailable;
	private JButton needMoreBagsButton;
	private JButton removeBagButton;
	private JButton addBagButton;
	private JButton purchaseButton;
	private JButton backButton;
	
	private int numBags = 0;
	
	public PurchaseBagsPanel(CustomerIO customerIO, int frameWidth, int frameHeight) {
		setLayout(new GridBagLayout());
		// top left bottom right
		gbc.insets = new Insets(0, 10, 25, 10);
		this.customerIO = customerIO;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		addScreenLabel();
		addRemoveBagButton();
		addTotalField();
		addAddBagButton();
		addPurchaseButton();
		addBackButton();
		addBagsAvailableLabel();
		addNeedMoreBagsButton();
	}
	
	private void addScreenLabel() {
		JLabel screenLabel = new JLabel("Purchase Reusable Bags");
		screenLabel.setFont(GUIConstants.LARGE_FONT);
		screenLabel.setForeground(Color.LIGHT_GRAY);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		add(screenLabel, gbc);
	}
	
	private void addRemoveBagButton() {
		removeBagButton = new JButton("-");
		removeBagButton.setFont(GUIConstants.FONT);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		add(removeBagButton, gbc);	
		
		removeBagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numBags--;
				numBags = Math.max(0, numBags);
				computeNewTotal();
			}
		});
	}
	
	private void addTotalField() {
		currentTotal = new JTextField();
		currentTotal.setEditable(false);
		currentTotal.setFont(GUIConstants.FONT);
		currentTotal.setHorizontalAlignment(JTextField.CENTER);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		add(currentTotal, gbc);
		computeNewTotal();
	}
	
	private void addAddBagButton() {
		addBagButton = new JButton("+");
		addBagButton.setFont(GUIConstants.FONT);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		add(addBagButton, gbc);
		
		addBagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numBags++;
				numBags = Math.min(customerIO.getNumBagsInDispenser(), numBags);
				computeNewTotal();
			}
		});
	}
	
	private void addPurchaseButton() {
		purchaseButton = new JButton("Purchase");
		purchaseButton.setFont(GUIConstants.FONT);
		purchaseButton.setForeground(Color.GREEN.darker());
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		add(purchaseButton, gbc);	
		
		purchaseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirmation = getUserConfirmation();
				if (confirmation == JOptionPane.YES_OPTION) {
					customerIO.bagsPurchased(numBags);
					numBags = 0;
					computeNewTotal();
					computeNewBagsAvailable();
				}
			}
		});
	}
	
	private void addBackButton() {
		backButton = new JButton("Go Back");
		backButton.setFont(GUIConstants.SMALL_FONT);
		backButton.setForeground(Color.RED.darker());
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.ipady = (frameHeight / 50);
		gbc.ipadx = (frameWidth / 50);
		add(backButton, gbc);	
		
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerIO.showAddPanel();
			}
		});
	}
	
	private void addBagsAvailableLabel() {
		bagsAvailable = new JLabel("Bags Available: " + customerIO.getNumBagsInDispenser());
		bagsAvailable.setFont(GUIConstants.SMALL_FONT);
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		gbc.insets = new Insets(0, 10, 0, 10);
		add(bagsAvailable, gbc);
	}
	
	private void addNeedMoreBagsButton() {
		needMoreBagsButton = new JButton();
		needMoreBagsButton.setText("<html>Click if you need<p>more than " + customerIO.getNumBagsInDispenser() + " bags.</html>");
		needMoreBagsButton.setFont(GUIConstants.SMALL_FONT);
		needMoreBagsButton.setBackground(Color.YELLOW);
		gbc.gridx = 2;
		gbc.gridy = 5;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		gbc.insets = new Insets(-10, 10, 0, 10);
		add(needMoreBagsButton, gbc);
		
		needMoreBagsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerIO.notifyAttendantNeedBags();
				attendantNotifiedPopup();
			}
		});
	}
	
	/*
	 * Kept in a separate method for unit testing purposes; the unit test will simply override this with a method
	 * that returns JOptionPane.YES_OPTION. In the unit test, because this will be overriden, the confirmation
	 * panel will not open up (and thus the test suite stays fully programmatic).
	 * 
	 * This is also why the visibility is protected; when the child is overriding, it should have a protected
	 * access visiblity so that this parent "PurchaseBagsPanel" can refer to the overriden method in the child.
	 * If the child was private, the code inside this class would not be able to refer to that method, and would
	 * instead proceed with ALWAYS showing the UserConfirmation. 
	 * 
	 * To the TA: THIS CODE CANNOT BE COVERED IN A UNIT TEST! THAT IS INTENTIONAL!
	 */
	protected int getUserConfirmation() {
		int confirmation = JOptionPane.showConfirmDialog(
				customerIO.getFrame(), "Are you sure you would like to purchase " + numBags + " bag(s)?", "Confirm Purchase", 
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		return confirmation;
	}
	
	protected void attendantNotifiedPopup() {
		JOptionPane.showMessageDialog(
				customerIO.getFrame(), "The attendant has been notified and will come add bags!", "Attendant Notified!",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	private void computeNewTotal() {
		currentTotal.setText(numBags + " Bags x $" + BAG_COST.setScale(2, RoundingMode.HALF_EVEN) + " = $" + BAG_COST.multiply(BigDecimal.valueOf(numBags)).setScale(2, RoundingMode.HALF_EVEN) + " Total");
	}
	
	private void computeNewBagsAvailable() {
		bagsAvailable.setText("Bags Available: " + customerIO.getNumBagsInDispenser());
		needMoreBagsButton.setText("<html>Click if you need<p>more than " + customerIO.getNumBagsInDispenser() + " bags.</html>");
	}
	
	// getters for unit tests
	public JButton getNeedMoreBagsButton() {
		return needMoreBagsButton;
	}
	
	public JButton getRemoveBagButton() {
		return removeBagButton;
	}
	
	public JButton getAddBagButton() {
		return addBagButton;
	}
	
	public JButton getPurchaseButton() {
		return purchaseButton;
	}
	
	public JButton getBackButton() {
		return backButton;
	}
	
	public int getNumBags() {
		return numBags;
	}
}
