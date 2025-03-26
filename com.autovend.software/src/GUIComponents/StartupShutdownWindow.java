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
import java.util.HashSet;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;

public class StartupShutdownWindow implements ActionListener {

	static JButton button, exitButton;
	static JFrame frame;
	SelfCheckoutStation scs;
	static SupervisionStation station;
	static AttendantCommunicationsController attendantCommunicationsController;
	ArrayList<JButton> buttons;
	List<SelfCheckoutStation> stations;
	JLabel startedStationsList;
	String Message;
	Border startedBorder;
	Border shutDownBorder;
	HashSet<Integer> station_activated = new HashSet<Integer>();

	public StartupShutdownWindow(SupervisionStation super_stat,
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

		startedBorder = BorderFactory.createLineBorder(Color.green, 2);
		shutDownBorder = BorderFactory.createLineBorder(Color.red, 2);

		Font smallFont = new Font("MV Boli", Font.PLAIN, 20);
		Font font = new Font("MV Boli", Font.PLAIN, 30);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);

		JLabel instructionLbl = new JLabel("Select a station to shut it down/start it up:");
		instructionLbl.setFont(GUIConstants.LARGE_FONT);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(instructionLbl, gbc);

		JLabel greenLbl = new JLabel("Green = Started up");
		greenLbl.setFont(GUIConstants.SMALL_FONT);
		greenLbl.setForeground(Color.green);
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.WEST;
		frame.getContentPane().add(greenLbl, gbc);

		JLabel redLbl = new JLabel("Red = Is shut down");
		redLbl.setFont(GUIConstants.SMALL_FONT);
		redLbl.setForeground(Color.red);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.EAST;
		frame.getContentPane().add(redLbl, gbc);

		startedStationsList = new JLabel();
		startedStationsList.setFont(GUIConstants.SMALL_FONT);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(startedStationsList, gbc);

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
				button.setBorder(startedBorder);
				button.addActionListener(this);
				stationsPanel.add(button);
				buttons.add(button);
			}
		}
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(stationsPanel, gbc);

		updateButtons();
		updateStartedStationsList();

		exitButton = new JButton("Go Back");
		exitButton.addActionListener(this);
		exitButton.setForeground(Color.red);
		exitButton.setFont(GUIConstants.FONT);
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
	}

	/**
	 * Sets the borders of all the buttons depending on whether they're shutdown or not.
	 * Also updates the station_activated list for use by updateStartedStationsList
	 */
	private void updateButtons() {
		for (int buttonIndex = 0; buttonIndex < buttons.size(); buttonIndex++) {
			// billOutput was chosen because it's one of the components disabled when station is shut down
			// but not when station use is prevented
			if (!stations.get(buttonIndex).billOutput.isDisabled()) {
				buttons.get(buttonIndex).setBorder(startedBorder);
				int station_number = buttonIndex + 1;
				station_activated.add(station_number);
			} else {
				buttons.get(buttonIndex).setBorder(shutDownBorder);
				int station_number = buttonIndex + 1;
				station_activated.remove(station_number);
			}
		}
	}

	private void updateStartedStationsList() {
		Message = "Started up stations: ";
		if (station_activated.size() > 0) {
			ArrayList<Integer> array = new ArrayList<>(station_activated);
			for (int j = 0; j < station_activated.size(); j++) {
				Message = Message + array.get(j).toString() + " ";
			}
			startedStationsList.setText(Message);
		} else {
			startedStationsList.setText(Message + "None");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		startedStationsList.setText("");

		if (e.getSource() == exitButton) {
		} else {
			for (int i = 0; i < buttons.size(); i++) {
				if (e.getSource() == buttons.get(i) && stations.get(i).baggingArea.isDisabled()) {
					attendantCommunicationsController.enableStation(stations.get(i));
				} else if (e.getSource() == buttons.get(i) && stations.get(i).baggingArea.isDisabled() == false) {
					attendantCommunicationsController.disableStation(stations.get(i));
				}
			}
			updateButtons();
			updateStartedStationsList();
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
