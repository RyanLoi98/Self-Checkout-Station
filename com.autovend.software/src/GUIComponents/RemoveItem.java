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

/**
 * Should the request be made verbally to the attendant? 
 * What if the customer and attendant do not
 * speak the same language? 
 * Should the customer be able to indicate the item on their bill that they want to remove, 
 * with the attendant merely approving it and checking that the item be removed from the bagging area?
 */

package GUIComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.autovend.devices.OverloadException;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;
import com.autovend.software.BillRecord;

public class RemoveItem extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTable resultsTable;
	public final DefaultTableModel resultsTableModel;
	GridBagConstraints gbcBagLayout = new GridBagConstraints();
	private ArrayList<String> removedProducts = new ArrayList<>();
	private ArrayList<String> billResults;
	private String messageString;
	public JComboBox<String> comboBox = new JComboBox<>();
	public JButton removeButton = new JButton();
	public JButton gobackButton = new JButton();
	public JLabel messageField = new JLabel();
	public JPanel messagePanel = new JPanel();

	public RemoveItem(SupervisionStation station, AttendantCommunicationsController attendantCommunicationsController) {
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		resultsTable = new JTable();
		resultsTableModel = new DefaultTableModel(new Object[] { "", "Description", "Price", "Quantity", "" }, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}

			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
		};

		addScrollPane();
		addInputPanel(attendantCommunicationsController);
		addButtonPanel(station, attendantCommunicationsController);
		addMessagePanel(attendantCommunicationsController);
	}

	public void addScrollPane() {
		resultsTable.setFont(new Font("MV Boli", Font.PLAIN, 20));
		resultsTable.setBackground(new Color(255, 255, 255));
		resultsTable.getTableHeader().setFont(new Font("MV Boli", Font.PLAIN, 20));
		resultsTable.setRowHeight(30);
		resultsTable.setModel(resultsTableModel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (int i = 1; i < resultsTable.getColumnCount(); i++) {
			resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		JPanel resultsPanel = new JPanel();
		resultsPanel.setBackground(new Color(0, 0, 0));
		resultsPanel.setLayout(null);
		JScrollPane scrollPane = new JScrollPane(resultsTable);
		scrollPane.setPreferredSize(new Dimension(1000, 300));
		scrollPane.setBackground(new Color(0, 0, 0));
		GridBagConstraints gbcBagLayout = new GridBagConstraints();
		gbcBagLayout.gridx = 0;
		gbcBagLayout.gridy = 0;
		gbcBagLayout.gridwidth = 3;
		gbcBagLayout.fill = GridBagConstraints.HORIZONTAL;
		gbcBagLayout.weighty = 0.0;
		add(scrollPane, gbcBagLayout);
	}

	public void addInputPanel(AttendantCommunicationsController attendantCommunicationsController) {
		JPanel inputPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER, 75, 30));
		inputPanel.setBackground(Color.white);
		JLabel stationLabel = new JLabel();
		stationLabel.setVerticalAlignment(SwingConstants.CENTER);
		stationLabel.setText("  Select SelfCheckOutStation");
		stationLabel.setOpaque(true);
		stationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		stationLabel.setForeground(Color.black);
		stationLabel.setBackground(Color.white);
		stationLabel.setFont(new Font("MV Boli", Font.PLAIN, 20));

		Integer numberOfSCS = attendantCommunicationsController.getNumberOfSCS();
		ArrayList<String> SCSName = new ArrayList<>();
		comboBox.addItem("None");
		for (Integer i = 0; i < numberOfSCS; i++) {
			SCSName.add(String.valueOf(i + 1));
		}
		for (String option : SCSName) {
			comboBox.addItem(option);
		}
		comboBox.setPreferredSize(new Dimension(200, comboBox.getPreferredSize().height));
		comboBox.setSelectedIndex(0);
		comboBox.addActionListener(e -> {
			resultsTableModel.setRowCount(0);

			String selectedItem = comboBox.getSelectedItem().toString();
			if (selectedItem.matches("None")) {
				return;
			}

			int stationIndex = Integer.valueOf(comboBox.getSelectedItem().toString()) - 1;

			BillRecord billy = attendantCommunicationsController.getStationLogicList().get(stationIndex)
					.getAddItemController().getBillRecord();
			billResults = new ArrayList<String>();

			for (String ItemName : billy.getItems()) {
				billResults.add(ItemName);
				Object[] rowData = new Object[] { false, ItemName, billy.getItemCost(ItemName),
						billy.getItemQuantity(ItemName) };
				resultsTableModel.addRow(rowData);
			}

		});

		inputPanel.add(stationLabel);
		inputPanel.add(comboBox);
		GridBagConstraints gbcInputPanel = new GridBagConstraints();
		gbcInputPanel.gridx = 0;
		gbcInputPanel.gridy = 1;
		gbcInputPanel.gridwidth = 3;
		gbcInputPanel.fill = GridBagConstraints.HORIZONTAL;
		gbcInputPanel.weighty = 0.0;
		add(inputPanel, gbcInputPanel);
	}

	public void addButtonPanel(SupervisionStation station,
			AttendantCommunicationsController attendantCommunicationsController) {
		JPanel buttonPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER, 100, 30));
		buttonPanel.setBackground(Color.white);

//		JButton removeButton = new JButton();
		removeButton.setBounds(213, 328, 200, 40);
		removeButton.setText("Remove");
		removeButton.setForeground(Color.black);
		removeButton.setFont(new Font("MV Boli", Font.PLAIN, 20));
		removeButton.setFocusable(false);
		removeButton.addActionListener(e -> {
			removedProducts = new ArrayList<>();

			for (int i = 0; i < resultsTable.getRowCount(); i++) {
				if ((boolean) resultsTable.getValueAt(i, 0)) {
					removedProducts.add(billResults.get(i));
				}
			}

			String selectedItem = comboBox.getSelectedItem().toString();
			if (selectedItem.matches("None")) {
				messageField.setText("Please select a valid station number!");
//				JOptionPane.showMessageDialog(new JFrame(), "Please select a valid station number!", "Error",
//						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (removedProducts.size() > 0) {
				messageString = removedProducts.size() + " product(s) have been removed:\n";
//				messageString = "The following " + removedProducts.size() + " product(s) have been removed:\n";
			} else {
				messageString = "No products are selected yet!";
			}

//			for (String rmProduct : removedProducts) {
//				messageString = messageString + rmProduct + "\n";
//			}

			int stationIndex = Integer.valueOf(comboBox.getSelectedItem().toString()) - 1;
			System.out.println(stationIndex);

			try {
				attendantCommunicationsController.removeItems(removedProducts, attendantCommunicationsController
						.getStationLogicList().get(stationIndex).getAddItemController());
			} catch (OverloadException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			comboBox.setSelectedIndex(stationIndex + 1);

			messageField.setText(messageString);
//			JOptionPane.showMessageDialog(new JFrame(), messageString, "Dialog", JOptionPane.INFORMATION_MESSAGE);
		});

//		JButton gobackButton = new JButton();
		gobackButton.setBounds(640, 328, 200, 40);
		gobackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				station.screen.getFrame().getContentPane().removeAll();
				station.screen.getFrame().revalidate();
				station.screen.getFrame().repaint();
				@SuppressWarnings("unused")
				MainScreen screen = new MainScreen(station, attendantCommunicationsController);
			}
		});

		gobackButton.setText("Go Back");
		gobackButton.setForeground(Color.black);
		gobackButton.setFont(new Font("MV Boli", Font.PLAIN, 20));
		gobackButton.setFocusable(false);
		buttonPanel.add(removeButton);
		buttonPanel.add(gobackButton);
		GridBagConstraints gbcButtonPanel = new GridBagConstraints();
		gbcButtonPanel.gridx = 0;
		gbcButtonPanel.gridy = 3;
		gbcButtonPanel.gridwidth = 6;
		gbcButtonPanel.fill = GridBagConstraints.HORIZONTAL;
		gbcButtonPanel.weighty = 0.0;
		add(buttonPanel, gbcButtonPanel);
	}

	public void addMessagePanel(AttendantCommunicationsController attendantCommunicationsController) {
		messageField.setFont(new Font("MV Boli", Font.PLAIN, 20));
		messageField.setPreferredSize(new Dimension(500, 30));
		messagePanel.add(messageField);
		GridBagConstraints gbcMessagePanel = new GridBagConstraints();
		gbcMessagePanel.gridx = 0;
		gbcMessagePanel.gridy = 7;
		gbcMessagePanel.gridwidth = 3;
		gbcMessagePanel.fill = GridBagConstraints.HORIZONTAL;
		gbcMessagePanel.weighty = 0.0;
		add(messagePanel, gbcMessagePanel);
	}

	public static void run(SupervisionStation station,
			AttendantCommunicationsController attendantCommunicationsController) {
		RemoveItem addItemByTextGUI = new RemoveItem(station, attendantCommunicationsController);
		JFrame jf = station.screen.getFrame();
		jf.setLayout(new GridBagLayout());
		jf.getContentPane().setBackground(Color.white);

		jf.revalidate();
		jf.repaint();
		jf.setTitle("Tutorial");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.add(addItemByTextGUI);
	}
}