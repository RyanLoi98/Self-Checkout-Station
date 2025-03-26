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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.autovend.software.CustomerIO;

public class WelcomePanel extends JPanel {
	private int frameWidth;
	private int frameHeight;
	
	private JButton startButton;
	private DefaultListModel languages;
	private JList languagesList;
	private String desiredLanguage;
	
	private CustomerIO customerIO;
	private GridBagConstraints gbc = new GridBagConstraints();
	
	public WelcomePanel(CustomerIO customerIO, int frameWidth, int frameHeight) {
		setLayout(new GridBagLayout());
		this.customerIO = customerIO;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		

		addWelcomeMessage();
		addChooseLanguageLabel();
		addChooseLanguageList();
		addStartButton();
	}
	
	private void addWelcomeMessage() {
		JLabel message = new JLabel("Welcome to TheLocalMarketplace Self Checkout!");
		message.setFont(GUIConstants.FONT);
		message.setForeground(Color.BLACK);
		// padding around element; top, left, bottom, right
		gbc.insets = new Insets(0, 0, 50, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		add(message, gbc);
	}
	
	private void addChooseLanguageList() {
		languages = new DefaultListModel<>();
		languages.addElement("English");
		
		languagesList = new JList<String>(languages);
		// the user can only select one language at a time; they can't have the station be in both
		// English AND FRENCH for example
		languagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// at the start, we'll have the selected language just be the first one (which is English)
		languagesList.setSelectedIndex(0);
		
		languagesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				/*
				 * If the customer is "trigger-happy" and constantly switching
				 * languages, don't set the desired language yet. Wait for them to
				 * "calm down" and stop.
				 */
				if (e.getValueIsAdjusting() == true) return;
				
				// if they somehow managed to select nothing, just default to English
				if (languagesList.getSelectedIndex() == -1) {
					desiredLanguage = "English";
				} else {
					desiredLanguage = (String) languagesList.getSelectedValue();
				}
			}
			
		});
		
		languagesList.setFont(GUIConstants.FONT);
		JScrollPane listScrollPane = new JScrollPane(languagesList);
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		add(listScrollPane, gbc);
	}
	
	private void addChooseLanguageLabel() {
		JLabel message = new JLabel("Choose Language");
		message.setFont(GUIConstants.FONT);
		message.setForeground(Color.LIGHT_GRAY.darker());
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipady = 0;
		gbc.ipadx = 0;
		add(message, gbc);
	}
	
	/*
	 * In case developer would like to test that another language works.
	 */
	public void addLanguage(String language) {
		languages.addElement(language);
	}
	
	private void addStartButton() {
		startButton = new JButton("Start");
		startButton.setFont(GUIConstants.FONT);
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipady = (frameHeight / 10);
		gbc.ipadx = (frameWidth / 10);
		add(startButton, gbc);	
		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				customerIO.showAddPanel();
			}
		});
	}

    public JList getLanguagesList(){
        return languagesList;
    }

    public String getDesiredLanguage(){
        return desiredLanguage;
    }
}
