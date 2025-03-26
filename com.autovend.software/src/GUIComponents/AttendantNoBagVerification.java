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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;

public class AttendantNoBagVerification implements ActionListener {

	static JButton button, exitButton;
	/**
	 * Selected = in No Bags Verification mode. <br>
	 * Not selected = in Own Bags Verification mode
	 */
	static JToggleButton tog_button;
	static JFrame frame;
	static JLabel titleLbl;
	SelfCheckoutStation scs;
	static SupervisionStation station;
	static AttendantCommunicationsController attendantCommunicationsController;
	ArrayList<JButton> buttons;
	List<SelfCheckoutStation> stations;

	Border verifiedBorder;
	Border unverifiedBorder;

	public AttendantNoBagVerification(SupervisionStation super_stat,
			AttendantCommunicationsController attendantCommunicationsController) {

		this.station = super_stat;
		this.attendantCommunicationsController = attendantCommunicationsController;
		stations = attendantCommunicationsController.getStationList();
		Border border = BorderFactory.createLineBorder(Color.red, 2);
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

		titleLbl = new JLabel("");
		titleLbl.setFont(GUIConstants.LARGE_FONT);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(titleLbl, gbc);

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
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(stationsPanel, gbc);

		exitButton = new JButton("Go Back");
		exitButton.addActionListener(this);
		exitButton.setForeground(Color.red);
		exitButton.setFont(GUIConstants.FONT);
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		frame.getContentPane().add(exitButton, gbc);

		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				station.screen.getFrame().getContentPane().removeAll();
				station.screen.getFrame().revalidate();
				station.screen.getFrame().repaint();
				@SuppressWarnings("unused")
				MainScreen screen = new MainScreen(super_stat, attendantCommunicationsController);
			}
		});

		tog_button= new JToggleButton();
		tog_button.setText("Switch Mode");
		tog_button.addActionListener(this);
		tog_button.setForeground(Color.red);
		tog_button.setFont(GUIConstants.FONT);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		frame.getContentPane().add(tog_button, gbc);

		resetButtons();
	}

	/**
	 * Resets the border to all station buttons to default (red, unverified).
	 * Sets the title label depending on state of toggle button
	 */
	private void resetButtons() {
		if(tog_button.isSelected()) {
			titleLbl.setText("No Bag Verification");
		}
		else {
			titleLbl.setText("Add Own Bag Verification");
		}

		for (int buttonIndex = 0; buttonIndex < buttons.size(); buttonIndex++) {
			buttons.get(buttonIndex).setBorder(unverifiedBorder);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Border border = BorderFactory.createLineBorder(Color.green, 2);

		if (e.getSource() == exitButton) {
			frame.getContentPane().removeAll();
			frame.revalidate();
			frame.repaint();
			MainScreen screen = new MainScreen(station, attendantCommunicationsController);

		} else if(e.getSource()==tog_button){
			resetButtons();
		}
		else {
			if(tog_button.isSelected()) { // in No Bag Verification mode
				for (int i = 0; i < buttons.size(); i++) {
					if (e.getSource() == buttons.get(i)) {
						attendantCommunicationsController.approveNoBags(stations.get(i));
						buttons.get(i).setBorder(verifiedBorder);
					}
				}
			}
			else { // in Add Own Bag Verification mode
				for (int i = 0; i < buttons.size(); i++) {
					if (e.getSource() == buttons.get(i)) {
						attendantCommunicationsController.approveOwnBags(stations.get(i));
						buttons.get(i).setBorder(verifiedBorder);
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
	public JToggleButton togbutton() {
		return tog_button;		
	}
	
}
