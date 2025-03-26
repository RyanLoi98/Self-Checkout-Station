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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.autovend.devices.AbstractDevice;
import com.autovend.devices.Keyboard;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SupervisionStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.KeyboardObserver;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;
import com.autovend.software.AttendantCommunicationsController;

public class AddItemByTextWindow extends JPanel implements KeyboardObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JTable resultsTable;
	public DefaultTableModel resultsTableModel;
	GridBagConstraints gbcBagLayout = new GridBagConstraints();
	public JTextField searchField;
	private ArrayList<Product> searchProducts = new ArrayList<>();
	private ArrayList<Product> addedProducts = new ArrayList<>();
	private Set<Product> searchResults;
	private String messageString;
	public static boolean selectKeyword;
	public static boolean userTyped;
	public JComboBox<String> comboBox = new JComboBox<>();
	public JPanel keyboardPanel = new JPanel(new GridLayout(4, 10, 1, 1));
	public JPanel inputPanel = new JPanel();
	public JPanel buttonPanel = new JPanel();
	public JPanel messagePanel = new JPanel();
	public JButton searchButton = new JButton();
	public JButton addButton = new JButton();
	public JButton resetButton = new JButton();
	public JButton gobackButton = new JButton();
	public JLabel messageField = new JLabel();

	public AddItemByTextWindow(SupervisionStation station,
			AttendantCommunicationsController attendantCommunicationsController) {
		JFrame jf = station.screen.getFrame();
		jf.setLayout(new GridBagLayout());
		station.keyboard.register(this);
		setLayout(new GridBagLayout());
		resultsTable = new JTable();
		resultsTableModel = new DefaultTableModel(new Object[] { "", "Barcode", "PLUCode", "Description", "Price" },
				0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
		};

		addScrollPane();
		addInputPanel(attendantCommunicationsController);
		addButtonPanel(station, attendantCommunicationsController);
		addKeyboardPanel(attendantCommunicationsController);
		addMessagePanel(attendantCommunicationsController);

		jf.revalidate();
		jf.repaint();
		jf.setTitle("Add Item By Text");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.add(this);
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
		gbcBagLayout.gridx = 0;
		gbcBagLayout.gridy = 0;
		gbcBagLayout.gridwidth = 3;
		gbcBagLayout.fill = GridBagConstraints.HORIZONTAL;
		gbcBagLayout.weighty = 0.0;
		add(scrollPane, gbcBagLayout);
	}

	public void addInputPanel(AttendantCommunicationsController attendantCommunicationsController) {
		searchField = new JTextField();
		searchField.setFont(new Font("MV Boli", Font.PLAIN, 20));
		searchField.setPreferredSize(new Dimension(300, 30));
		JLabel textLabel = new JLabel();
		textLabel.setVerticalAlignment(SwingConstants.CENTER);
		textLabel.setText("  Keyword  ");
		textLabel.setOpaque(true);
		textLabel.setHorizontalAlignment(SwingConstants.CENTER);
		textLabel.setForeground(new Color(0x00ff00));
		textLabel.setFont(new Font("MV Boli", Font.PLAIN, 20));
		JLabel stationLabel = new JLabel();

		stationLabel.setVerticalAlignment(SwingConstants.CENTER);
		stationLabel.setText("  Select SelfCheckOutStation");
		stationLabel.setOpaque(true);
		stationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		stationLabel.setForeground(new Color(0x00ff00));
		stationLabel.setFont(new Font("MV Boli", Font.PLAIN, 20));

		Integer numberOfSCS = attendantCommunicationsController.getNumberOfSCS();
		ArrayList<String> SCSName = new ArrayList<>();
		for (Integer i = 0; i < numberOfSCS; i++) {
			SCSName.add(String.valueOf(i + 1));
		}
		for (String option : SCSName) {
			comboBox.addItem(option);
		}
		comboBox.setPreferredSize(new Dimension(200, comboBox.getPreferredSize().height));

		inputPanel.add(textLabel);
		inputPanel.add(searchField);
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
		searchButton.setBounds(0, 328, 164, 33);
		searchButton.setText("Search");
		searchButton.setForeground(Color.RED);
		searchButton.setFont(new Font("MV Boli", Font.PLAIN, 20));
		searchButton.setFocusable(false);
		searchButton.addActionListener(e -> {
			String keyword = searchField.getText();
			searchProducts = new ArrayList<>();
			searchResults = attendantCommunicationsController.searchItemByText(keyword,
					ProductDatabases.BARCODED_PRODUCT_DATABASE, ProductDatabases.PLU_PRODUCT_DATABASE);
			resultsTableModel.setRowCount(0);
			for (Product searchProduct : searchResults) {
				if (searchProduct instanceof BarcodedProduct) {
					Object[] rowData = new Object[] { false, ((BarcodedProduct) searchProduct).getBarcode().toString(),
							"", ((BarcodedProduct) searchProduct).getDescription(), searchProduct.getPrice() };
					resultsTableModel.addRow(rowData);
					searchProducts.add(searchProduct);
				}
				if (searchProduct instanceof PLUCodedProduct) {
					Object[] rowData = new Object[] { false, "",
							((PLUCodedProduct) searchProduct).getPLUCode().toString(),
							((PLUCodedProduct) searchProduct).getDescription(), searchProduct.getPrice() };
					resultsTableModel.addRow(rowData);
					searchProducts.add(searchProduct);
				}
			}
			messageString = searchResults.size() + " product(s) have been found!";
			messageField.setText(messageString);
		});

		addButton.setBounds(213, 328, 164, 33);
		addButton.setText("Add");
		addButton.setForeground(Color.RED);
		addButton.setFont(new Font("MV Boli", Font.PLAIN, 20));
		addButton.setFocusable(false);
		addButton.addActionListener(e -> {
			addedProducts = new ArrayList<>();
			for (int i = 0; i < resultsTable.getRowCount(); i++) {
				if ((boolean) resultsTable.getValueAt(i, 0)) {
					addedProducts.add(searchProducts.get(i));
				}
			}
			if (addedProducts.size() > 0) {
				messageString = addedProducts.size() + " product(s) have been added.";
			} else {
				messageString = "No products are selected yet!";
			}
			int stationIndex = Integer.valueOf(comboBox.getSelectedItem().toString()) - 1;

			try {
				attendantCommunicationsController.addItemByText(addedProducts,
						attendantCommunicationsController.getStationLogicList().get(stationIndex));
			} catch (OverloadException e1) {
			}

			messageField.setText(messageString);
		});

		resetButton.setText("Reset");
		resetButton.setForeground(Color.RED);
		resetButton.setFont(new Font("MV Boli", Font.PLAIN, 20));
		resetButton.setFocusable(false);
		resetButton.addActionListener(e -> {
			searchProducts = new ArrayList<>();
			addedProducts = new ArrayList<>();
			resultsTableModel.setRowCount(0);
			searchField.setText("");
			messageString = "Panel has been reset!";
			messageField.setText(messageString);
		});

		gobackButton.setBounds(640, 328, 154, 33);
		gobackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				station.screen.getFrame().getContentPane().removeAll();
				station.screen.getFrame().revalidate();
				station.screen.getFrame().repaint();
				@SuppressWarnings("unused")
				MainScreen screen = new MainScreen(station, attendantCommunicationsController);
			}
		});
		gobackButton.setText("Back");
		gobackButton.setForeground(Color.RED);
		gobackButton.setFont(new Font("MV Boli", Font.PLAIN, 20));
		gobackButton.setFocusable(false);
		buttonPanel.add(searchButton);
		buttonPanel.add(addButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(gobackButton);
		GridBagConstraints gbcButtonPanel = new GridBagConstraints();
		gbcButtonPanel.gridx = 0;
		gbcButtonPanel.gridy = 3;
		gbcButtonPanel.gridwidth = 3;
		gbcButtonPanel.fill = GridBagConstraints.HORIZONTAL;
		gbcButtonPanel.weighty = 0.0;
		add(buttonPanel, gbcButtonPanel);
	}

	public void addKeyboardPanel(AttendantCommunicationsController attendantCommunicationsController) {
		keyboardPanel.setPreferredSize(new Dimension(500, 200));
		String[] keyLabels = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "Q", "W", "E", "R", "T", "Y", "U", "I",
				"O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "DEL", "Z", "X", "C", "V", "B", "N", "M", ".",
				"SPACE", "ENTER" };
		for (String label : keyLabels) {
			JButton keyButton = new JButton(label);
			keyButton.setPreferredSize(new Dimension(20, 0));
			keyButton.setFont(new Font("MV Boli", Font.PLAIN, 15));
			if (keyButton.getText().equals("SPACE") || keyButton.getText().equals("ENTER")) {
				keyButton.setFont(new Font("MV Boli", Font.PLAIN, 12));
			}
			keyButton.setFocusable(false);
			keyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (keyButton.getText().equals("DEL")) {
						if (!searchField.getText().isEmpty()) {
							searchField.setText(searchField.getText().substring(0, searchField.getText().length() - 1));
						}
					} else if (keyButton.getText().equals("SPACE")) {
						searchField.setText(searchField.getText() + " ");
					} else if (keyButton.getText().equals("ENTER")) {
						String keyword = searchField.getText();
						searchProducts = new ArrayList<>();
						searchResults = attendantCommunicationsController.searchItemByText(keyword,
								ProductDatabases.BARCODED_PRODUCT_DATABASE, ProductDatabases.PLU_PRODUCT_DATABASE);
						resultsTableModel.setRowCount(0);
						for (Product searchProduct : searchResults) {
							if (searchProduct instanceof BarcodedProduct) {
								Object[] rowData = new Object[] { false,
										((BarcodedProduct) searchProduct).getBarcode().toString(), "",
										((BarcodedProduct) searchProduct).getDescription(), searchProduct.getPrice() };
								resultsTableModel.addRow(rowData);
								searchProducts.add(searchProduct);
							}
							if (searchProduct instanceof PLUCodedProduct) {
								Object[] rowData = new Object[] { false, "",
										((PLUCodedProduct) searchProduct).getPLUCode().toString(),
										((PLUCodedProduct) searchProduct).getDescription(), searchProduct.getPrice() };
								resultsTableModel.addRow(rowData);
								searchProducts.add(searchProduct);
							}
						}

						messageString = searchProducts.size() + " product(s) have been found!";
						messageField.setText(messageString);
					} else {
						searchField.setText(searchField.getText() + keyButton.getText());
					}
				}
			});
			keyboardPanel.add(keyButton);
		}
		GridBagConstraints gbcKeyboardPanel = new GridBagConstraints();
		gbcKeyboardPanel.gridx = 0;
		gbcKeyboardPanel.gridy = 4;
		gbcKeyboardPanel.gridwidth = 3;
		gbcKeyboardPanel.fill = GridBagConstraints.HORIZONTAL;
		gbcKeyboardPanel.weighty = 0.0;
		add(keyboardPanel, gbcKeyboardPanel);
	}

	public void addMessagePanel(AttendantCommunicationsController attendantCommunicationsController) {
		messageField.setFont(new Font("MV Boli", Font.PLAIN, 20));
		messageField.setPreferredSize(new Dimension(300, 30));
		messagePanel.add(messageField);
		GridBagConstraints gbcMessagePanel = new GridBagConstraints();
		gbcMessagePanel.gridx = 0;
		gbcMessagePanel.gridy = 7;
		gbcMessagePanel.gridwidth = 3;
		gbcMessagePanel.fill = GridBagConstraints.HORIZONTAL;
		gbcMessagePanel.weighty = 0.0;
		add(messagePanel, gbcMessagePanel);
	}

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToKeyPressedEvent(Keyboard k, char c) {
		// TODO Auto-generated method stub
		if (selectKeyword) {
			userTyped = true;
			searchField.setText(searchField.getText() + c);
		}
	}

	public String getKeyword() {
		return searchField.getText();
	}

	public void deSelectKeyword() {
		selectKeyword = false;
	}

	public void selectKeyword() {
		selectKeyword = true;
	}
}