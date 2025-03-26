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

public class PreventPermitWindow implements ActionListener {

	static JButton button, goBackButton;
	static JFrame frame;
	SelfCheckoutStation scs;
	static SupervisionStation station;
	static AttendantCommunicationsController attendantCommunicationsController;
	ArrayList<JButton> buttons;
	List<SelfCheckoutStation> stations;
	JLabel disabledStationsListLbl;
	JLabel lblNewLabel_2_1;
	String Message = "Disabled stations: ";
	Border enabledBorder;
	Border disabledBorder;
	HashSet<Integer> station_disabled = new HashSet<Integer>();

	public PreventPermitWindow(SupervisionStation super_stat,
			AttendantCommunicationsController attendantCommunicationsController) {

		this.station = super_stat;
		this.attendantCommunicationsController = attendantCommunicationsController;
		buttons = new ArrayList<JButton>();
		frame = station.screen.getFrame();

		enabledBorder = BorderFactory.createLineBorder(Color.green, 2);
		disabledBorder = BorderFactory.createLineBorder(Color.red, 2);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // setting up frame
		frame.setResizable(false);
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new GridBagLayout());
		frame.setVisible(true);
		frame.getContentPane().setBackground(Color.white);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);

		JLabel instructionLbl = new JLabel("Select a station to permit/prevent its use:");
		instructionLbl.setFont(GUIConstants.LARGE_FONT);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(instructionLbl, gbc);

		JLabel greenLbl = new JLabel("Green = Use permitted");
		greenLbl.setFont(GUIConstants.SMALL_FONT);
		greenLbl.setForeground(Color.green);
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.WEST;
		frame.getContentPane().add(greenLbl, gbc);

		JLabel redLbl = new JLabel("Red = Use prevented");
		redLbl.setFont(GUIConstants.SMALL_FONT);
		redLbl.setForeground(Color.red);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.EAST;
		frame.getContentPane().add(redLbl, gbc);

		disabledStationsListLbl = new JLabel();
		disabledStationsListLbl.setFont(GUIConstants.SMALL_FONT);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(disabledStationsListLbl, gbc);

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
				button.setBorder(enabledBorder);
				button.addActionListener(this);
				stationsPanel.add(button);
				buttons.add(button);
			}
		}
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		frame.getContentPane().add(stationsPanel, gbc);

		for (int i = 0; i < attendantCommunicationsController.getNumberOfSCS(); i++) {
			if (!stations.get(i).baggingArea.isDisabled()) {
				buttons.get(i).setBorder(enabledBorder);
				int station_number = i + 1;
				station_disabled.remove(station_number);
			} else {
				buttons.get(i).setBorder(disabledBorder);
				int station_number = i + 1;
				station_disabled.add(station_number);
			}
			ArrayList<Integer> array = new ArrayList<>(station_disabled);
			Message= Message+ "Start up Station ";

			for (int j = 0; j < station_disabled.size(); j++) {
				Message = Message + array.get(j).toString() + " ";
			}
			disabledStationsListLbl.setText(Message);
			Message = "Disabled stations: ";
		}

		goBackButton = new JButton("Go Back");
		goBackButton.addActionListener(this);
		goBackButton.setForeground(Color.red);
		goBackButton.setFont(GUIConstants.FONT);
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		frame.add(goBackButton, gbc);
		goBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				station.screen.getFrame().getContentPane().removeAll();
				station.screen.getFrame().revalidate();
				station.screen.getFrame().repaint();
				@SuppressWarnings("unused")
				MainScreen screen = new MainScreen(super_stat, attendantCommunicationsController);
			}
		});
		

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Border border = BorderFactory.createLineBorder(Color.green, 2);
		//lblNewLabel_2.setText("");

		if (e.getSource() == goBackButton) {
//			frame.dispose();
//			frame.getContentPane().removeAll();
//			frame.revalidate();
//			frame.repaint();
//			MainScreen screen = new MainScreen(station, attendantCommunicationsController);

		} else {
			for (int i = 0; i < buttons.size(); i++) {

				if (e.getSource() == buttons.get(i) && station_disabled.contains(i + 1) == false) {

					attendantCommunicationsController.preventStationUse(stations.get(i));
					buttons.get(i).setBorder(disabledBorder);

					int station_number = i + 1;
					station_disabled.add(station_number);
					ArrayList<Integer> array = new ArrayList<>(station_disabled);

					for (int j = 0; j < station_disabled.size(); j++) {
						Message = Message + array.get(j).toString() + " ";
					}

					disabledStationsListLbl.setText(Message);

					Message = "Disabled stations: ";
				}

				else if (e.getSource() == buttons.get(i) && station_disabled.contains(i + 1)) {
					attendantCommunicationsController.permitStationUse(stations.get(i));
					buttons.get(i).setBorder(enabledBorder);

					int station_number = i + 1;
					station_disabled.remove(station_number);
					ArrayList<Integer> array = new ArrayList<>(station_disabled);

					for (int j = 0; j < station_disabled.size(); j++) {
						Message = Message + array.get(j).toString() + " ";
					}

					disabledStationsListLbl.setText(Message);

					Message = "Disabled stations: ";
				}
			}
		}
	}

	public JButton getgoBackButton() {
		return goBackButton;
	}

	public ArrayList<JButton> getButtons() {
		return buttons;
	}

}
