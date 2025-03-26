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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.autovend.software.CustomerIO;



public class InsufficientChangePanel {
	
    private JPanel insufficientChangePanel;
    private JLabel changeDue;
    private JLabel popupBody;
	JLabel popupTitle;

    private CustomerIO customerIO;
    
	public InsufficientChangePanel(JPanel GUIContainer, CustomerIO CIO) {
		customerIO = CIO;
		insufficientChangePanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(10, 10, 10, 10);

		popupTitle = new JLabel("Please wait, not enough change.");
		changeDue = new JLabel("$" + CIO.getAmountDue() + " owed.");
		popupBody = new JLabel("An attendant will help shortly.");

		popupTitle.setFont(GUIConstants.FONT);
		changeDue.setFont(GUIConstants.FONT);
		popupBody.setFont(GUIConstants.SMALL_FONT);

		insufficientChangePanel.add(popupTitle, constraints);
		insufficientChangePanel.add(changeDue, constraints);
		insufficientChangePanel.add(popupBody, constraints);
	}
	
	public JPanel getPanel() {
    	return insufficientChangePanel;
    }
		
    // Getters for tests:
    public JLabel getPopupTitle(){return popupTitle;}
    public JLabel getChangeDue(){return changeDue;}
    public JLabel getPopupBody(){return popupBody;}
}
