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
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.autovend.devices.ElectronicScale;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.software.AddItemObserver;
import com.autovend.software.AttendantCommunicationsController;
import com.autovend.software.CustomerIO;
import com.autovend.software.PayCashObserver;
import com.autovend.software.PrintReceiptObserver;
import com.autovend.software.SelfCheckoutStationLogic;

public class AttendantIO extends JPanel
		implements ActionListener, PrintReceiptObserver, PayCashObserver, AddItemObserver {
	public static SupervisionStation supervisionStation;
	public static AttendantCommunicationsController attendantCommunicationsController = new AttendantCommunicationsController();
	public static AttendantIO attendantIOGUI;
	public static JFrame jf;
	static JButton button;
	private String logString;
	static SelfCheckoutStation scs;
	static SelfCheckoutStation scs2;
	static SelfCheckoutStation scs3;
	static SelfCheckoutStation scs4;
	static SelfCheckoutStation scs5;
	static SelfCheckoutStation scs6;
	LoginWindow login;

	static SelfCheckoutStation scs7;
	static SelfCheckoutStation scs8;
	static SelfCheckoutStation scs9;
	static SelfCheckoutStation scs10;
	static SelfCheckoutStation scs11;
	static SelfCheckoutStation scs12;

	static SelfCheckoutStation scs13;
	static SelfCheckoutStation scs14;
	static SelfCheckoutStation scs15;

	public AttendantIO() {
		this.supervisionStation = attendantCommunicationsController.getSupervisionStation();
		attendantCommunicationsController.registerAttendantToLogic(this);
		jf = supervisionStation.screen.getFrame();
		jf.setTitle("Tutorial");
		jf.setVisible(true);
		jf.getContentPane().setBackground(Color.white);
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		JLabel label = new JLabel();
		label.setText("Attendant Station");
		label.setForeground(Color.black);
		label.setFont(new Font("MV Boli", Font.BOLD, screenWidth / 30));
		label.setVerticalAlignment(JLabel.CENTER);
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setBackground(Color.white);
		label.setOpaque(true);
		label.setBounds(screenWidth / 4 + screenWidth * 3 / 32, screenHeight / 4 + screenHeight / 16, screenWidth,
				screenHeight / 16);

		button = new JButton("Login");
		button.setFont(new Font("MV Boli", Font.PLAIN, screenWidth / 40));
		button.setBounds(screenWidth / 4 + screenWidth * 6 / 32, screenHeight / 4 + screenHeight / 4, screenWidth / 8,
				screenHeight / 16);
		button.setFocusable(false);
		button.addActionListener(this);

		jf.add(label);
		jf.add(button);
	}

	public AttendantCommunicationsController getAttendantCommunicationsController() {
		return attendantCommunicationsController;
	}

	public static void main(String[] args) {
		supervisionStation = attendantCommunicationsController.getSupervisionStation();
		Currency c1 = Currency.getInstance(Locale.CANADA);
		int[] billdenominations = { 5, 10, 15, 20, 50 };
		BigDecimal[] coindenominations = { new BigDecimal("1") };
		attendantCommunicationsController.initializeSelfCheckoutStationLogics(15, c1, billdenominations,
				coindenominations, 20, 1, null, null, 0, null);
		;
		new AttendantIO();
	}

	// getter for testing
	public JButton getLoginButton() {
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		jf.getContentPane().removeAll();
		jf.revalidate();
		jf.repaint();
		login = new LoginWindow(supervisionStation, attendantCommunicationsController);
	}

	public void stationHelp(SelfCheckoutStationLogic logic) {
		int stat = attendantCommunicationsController.findStation(logic);
		login.sendCall("Station " + stat + " needs help");

	}

	public void noBaggingRequestInProgress(CustomerIO customer, SelfCheckoutStationLogic logic) {
		int stat = attendantCommunicationsController.findStation(logic);
		login.sendCall("Station " + stat + " needs No Bag Verification");

	}

	@Override
	public void updatedAmount(SelfCheckoutStation station, BigDecimal amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insufficientChange(SelfCheckoutStation station, BigDecimal change) {
		int stat = attendantCommunicationsController.findStation(station);
		login.sendCall("Station " + stat + " is out change, needs $" + change.doubleValue());

	}

	@Override
	public void sessionComplete(SelfCheckoutStation station) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requiresMaintenance(SelfCheckoutStation station, String message) {
		int stat = attendantCommunicationsController.findStation(station);
		login.sendCall("Station " + stat + " requires maintaince.");

	}

	@Override
	public void lowPaper(SelfCheckoutStation station, String message) {
		int stat = attendantCommunicationsController.findStation(station);
		login.sendCall("Station " + stat + " is low on paper");

	}

	@Override
	public void lowInk(SelfCheckoutStation station, String message) {
		int stat = attendantCommunicationsController.findStation(station);
		login.sendCall("Station " + stat + " is low on ink");
	}

	@Override
	public void notWaitingForItem(SelfCheckoutStation station) {
	}

	@Override
	public void waitingForItem(SelfCheckoutStation station) {
	}

	@Override
	public void weightDiscrepancyOccured(SelfCheckoutStation station) {
		int stat = attendantCommunicationsController.findStation(station);
		login.sendCall("Station " + stat + " has weight discreprancy");
	}

	@Override
	public void weightDiscrepancyResolved(SelfCheckoutStation station) {
	}

	@Override
	public void scaleOverloaded(ElectronicScale scale) {

	}

	@Override
	public void scaleOverloadedResolved(SelfCheckoutStation station) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noBagsApproved(SelfCheckoutStation station) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addOwnBagsStart(SelfCheckoutStation station) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addOwnBagsComplete(SelfCheckoutStation station) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OwnBagsCancelled(SelfCheckoutStation station) {
	}

	@Override
	public void updateStation(SelfCheckoutStation station) {
	}

	public void ownBagsVerify(SelfCheckoutStationLogic scsLogic) {
		int stat = attendantCommunicationsController.findStation(scsLogic);
		login.sendCall("Station " + stat + " needs Own bags verification");
	}

	public void stationNeedsBags(CustomerIO customerIO, SelfCheckoutStationLogic scsLogic) {
		int stat = attendantCommunicationsController.findStation(scsLogic);
		login.sendCall("Station " + stat + " needs reusable bags ");
		
	}

	@Override
	public void preventStation(SelfCheckoutStation station) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void permitStation(SelfCheckoutStation station) {
		// TODO Auto-generated method stub
		
	}

}
