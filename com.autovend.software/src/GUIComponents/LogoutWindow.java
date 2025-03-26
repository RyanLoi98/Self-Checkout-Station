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
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.autovend.devices.SupervisionStation;
import com.autovend.software.AttendantCommunicationsController;

public class LogoutWindow implements ActionListener {

	JFrame frame;
	JButton yes_button = new JButton("Yes, Logout");
	JButton no_button = new JButton("No, Go back");
	static SupervisionStation station;
	static AttendantCommunicationsController attendantCommunicationsController;
	public static boolean logoutSuccess = false;
	public static boolean logoutgoBack = false;
	public static boolean logoutSuper = false;

	public LogoutWindow(SupervisionStation super_stat,
			AttendantCommunicationsController attendantCommunicationsController) {
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		logoutSuccess = false;
		logoutgoBack = false;
		logoutSuper = false;
		this.station = super_stat;
		this.attendantCommunicationsController = attendantCommunicationsController;
		frame = super_stat.screen.getFrame();
		frame.setTitle("Logout");

		// make yes button
		yes_button.setBounds(screenWidth * 3 / 16 + screenWidth / 8, screenHeight / 2 + screenHeight / 16,
				screenWidth / 8, screenHeight / 16);
		// yes_button.setBounds(-165, 0, 100 ,40);
		yes_button.setFocusable(false);
		yes_button.addActionListener(this);

		// make no button
		no_button.setBounds(screenWidth / 2, screenHeight / 2 + screenHeight / 16, screenWidth / 8, screenHeight / 16);
		// no_button.setBounds(65, 0, 110 , 40);
		no_button.setFocusable(false);
		no_button.addActionListener(this);

		// create the frame
		JLabel label = new JLabel();
		yes_button.setFont(new Font("MV Boli", Font.PLAIN, screenWidth / 60));
		no_button.setFont(new Font("MV Boli", Font.PLAIN, screenWidth / 60));

		label.setText("Are you sure you want to Logout?");
		label.setForeground(Color.black);
		label.setFont(new Font("MV Boli", Font.PLAIN, screenWidth / 40));
		label.setVerticalAlignment(JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setBackground(Color.white);
		label.setOpaque(true);
		label.setBounds(screenWidth / 8 + screenWidth / 8, screenHeight / 4 + screenHeight / 16, screenWidth,
				screenHeight / 16);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setExtendedState(frame.MAXIMIZED_BOTH); // set to full screen
		frame.setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(Color.white);
		frame.add(label);
		frame.add(yes_button);
		frame.add(no_button);
		frame.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == yes_button) {
			frame.getContentPane().removeAll();
			frame.revalidate();
			frame.repaint();
			logoutSuccess = true;
			LoginWindow loginWindow = new LoginWindow(station, attendantCommunicationsController);
		}

		else if (e.getSource() == no_button) {
			if (SuperUserWindow.isSuperUser == true) {
				frame.getContentPane().removeAll();
				frame.revalidate();
				frame.repaint();
				logoutSuper = true;
				SuperUserWindow s = new SuperUserWindow(station, attendantCommunicationsController);
			} else {
				frame.getContentPane().removeAll();
				frame.revalidate();
				frame.repaint();
				logoutgoBack = true;
				MainScreen screen = new MainScreen(station, attendantCommunicationsController);
			}

		}

	}

	// getters for testing
	public JButton getNoButton() {
		return no_button;
	}

	public JButton getYesButton() {
		return yes_button;
	}

}
