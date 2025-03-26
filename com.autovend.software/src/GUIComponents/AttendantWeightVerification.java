package GUIComponents;

import com.autovend.devices.OverloadException;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AttendantWeightVerification implements ActionListener {

	static JButton button, exitButton;
	static JFrame frame;
	SelfCheckoutStation scs;
	static SupervisionStation station;
	static AttendantCommunicationsController attendantCommunicationsController;
	ArrayList<JButton> buttons;
	List<SelfCheckoutStation> stations;
	String Message;
	Border verifiedBorder;
	Border unverifiedBorder;
	HashSet<Integer> station_activated = new HashSet<Integer>();

	public AttendantWeightVerification(SupervisionStation super_stat,
								 AttendantCommunicationsController attendantCommunicationsController) {

		this.station = super_stat;
		this.attendantCommunicationsController = attendantCommunicationsController;
		stations = attendantCommunicationsController.getStationList();
		buttons = new ArrayList<JButton>();
		frame = station.screen.getFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new GridBagLayout());
		frame.setVisible(true);
		frame.getContentPane().setBackground(Color.white);

		verifiedBorder = BorderFactory.createLineBorder(Color.green, 2);
		unverifiedBorder = BorderFactory.createLineBorder(Color.red, 2);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);

		JLabel instructionLbl = new JLabel("Weight Discrepancy Verification");
		instructionLbl.setFont(GUIConstants.LARGE_FONT);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(instructionLbl, gbc);

		JLabel greenLbl = new JLabel("Green = Verified");
		greenLbl.setFont(GUIConstants.SMALL_FONT);
		greenLbl.setForeground(Color.green);
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.WEST;
		frame.getContentPane().add(greenLbl, gbc);

		JLabel redLbl = new JLabel("Red = Unverified");
		redLbl.setFont(GUIConstants.SMALL_FONT);
		redLbl.setForeground(Color.red);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.EAST;
		frame.getContentPane().add(redLbl, gbc);

		// build all the buttons
		stations = attendantCommunicationsController.getStationList();
		// note: max number of stations is 20
		JPanel stationsPanel = new JPanel(new GridLayout(4, 5, 10, 10));
		stationsPanel.setBackground(Color.WHITE);
		for (int stationIndex = 0; stationIndex < station.supervisedStationCount(); stationIndex++) {
			if (stationIndex >= 20) {
				System.out.println("Attendant station cannot handle more than 20 customer stations");
			} else {
				button = new JButton("Station " + (stationIndex + 1));
				button.setFont(GUIConstants.FONT);
				button.addActionListener(this);
				stationsPanel.add(button);
				buttons.add(button);
			}
		}
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.CENTER;
		frame.getContentPane().add(stationsPanel, gbc);

		resetButtons();

		exitButton = new JButton("Go Back");
		exitButton.addActionListener(this);
		exitButton.setForeground(Color.red);
		exitButton.setFont(GUIConstants.FONT);
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		frame.getContentPane().add(exitButton, gbc);

		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetButtons();
				station.screen.getFrame().getContentPane().removeAll();
				station.screen.getFrame().revalidate();
				station.screen.getFrame().repaint();
				@SuppressWarnings("unused")
				MainScreen screen = new MainScreen(super_stat, attendantCommunicationsController);
			}
		});
	}

	/**
	 * Sets the borders of all the buttons to red, unverified.
	 */
	private void resetButtons() {
		for (int buttonIndex = 0; buttonIndex < buttons.size(); buttonIndex++) {
			buttons.get(buttonIndex).setBorder(unverifiedBorder);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exitButton) {
		} else {
			for (int i = 0; i < buttons.size(); i++) {
				if (e.getSource() == buttons.get(i)) {
					try {
						attendantCommunicationsController.solveWeightDiscrep(stations.get(i));
						buttons.get(i).setBorder(verifiedBorder);
					} catch (OverloadException exc) {
						// attendant station should be notified of overloads through other means.
						// nothing to do about it here
					}
				}
			}
		}
	}

	// getters for testing
	public JButton getBackButton() {
		return exitButton;
	}
	public ArrayList<JButton> getButtons() {
		return buttons;
	}
}
