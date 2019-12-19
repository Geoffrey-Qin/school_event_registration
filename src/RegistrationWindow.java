import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.SystemColor;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
/*
 * Description: This program is designed to construct a GUI window that allows Relay for Life staff members to sign-in Relay participants
 * for the event. The program will match the names the user have entered with the participants' profiles from the excel data sheet, and makes 
 * the decision whether the participant is allowed to enter the event or not(check if the participant have raised the minimum amount of donation
 * to be able to participate the event). All the user need to do is enter the first name and last name of the participant in the corresponding 
 * text fields, and clicked the "Search Profile" button to match the profiles, the program will automatically input the team names, raised over $50, 
 * individual participant, and entry time information. After the user have clicked "Register" button, the program will generate a new excel sheet with 
 * the file name entered in the loading window at the original folder.
 * 
 * Highlights:  
 * 1). Reading and writing data in the excel sheets(.xls format)
 * 2). Automatic time stamp for the entry time of participant
 * 3). Status bar showing the progress of reading data(using label.paintImmediately)
 * 4). Using JFileChooser to locate the data file in a more intuitive way instead of typing paths
 * 
 * Crertia: 
 * 1). 2D Arrays     		------------------ 		registeredParticipant in RegistrationWindow, participantProfile in RelayAdminWindow
 * 2). Sorting Algorithm 	------------------		selection_Sort_Ascending in RelayAdminWindow
 * 3). Search Algorithm		------------------ 		linear search in RegistrationWindow and RelayAdminWindow when matching profiles names	and getting team reports
 * 4). Object Oriented 		------------------		Customized classes for excel Reader and Writer, own-designed objects for RelayParticipant and RelayTeam
 * 5). Recursion 			------------------		Calculate the total donations that team has raised using recursion in RelayTeam
 * 6). File I/O				------------------   	Read data from excel sheets and output data in excel sheets
 * 
 * Author: Geoffrey Qin
 * Version: v1.0
 * Date: May 14, 2018
 */


public class RegistrationWindow {

	private JFrame frame;
	static ExcelReader dataBase = new ExcelReader();						//enable the excel reader
	static ExcelWriter registrationData = new ExcelWriter();				//enable the excel writer
	private JTextField txtNotes;

	static String[] participantFirstNameList;							//create a String array to store participants' first names
	static String[]	participantLastNameList;								//create a String array to store participants' last names
	static String[] teamNameList;										//create a String array to store teams' name
	static String[][] registeredParticipant;								//create a 2D String array to store the information of the registered participants
	static String[] sheetHeader;											//create a String array to store the headers of the data sheet

	static int registeredIndex = 0;										//create an integer to store the index of the registered participants

	///////////////////////////////////// constants /////////////////////////////////////

	static final int FIRST_NAME_COLUMN = 0;								//create the constants for each column in the data sheet
	static final int LAST_NAME_COLUMN = 1;
	static final int TEAM_CAPTAIN_COLUMN = 24;
	static final int TEAM_NAME_COLUMN = 25;
	static final int DONATION_COLUMN = 28;
	static final int WRIST_BAND_COLUMN = 29;
	static final int ENTRY_TIME_COLUMN = 30;
	static final int NOTES_COLUMN = 31;
	static final int MINIMUM_RAISED_AMOUNT = 50;							//create the constant for the minimum raised amount

	////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Launch the application.
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static void main(String[] args) throws IOException, BiffException {
		dataBase.setInputFile(LoadingWindow.dataFilePath);				//set the InputFile to the file selected in the loading window
		participantFirstNameList = new String [LoadingWindow.FirstNameList.length];		//create the participantFirstNameList and assign the size of the FirstNameList in Loading window to it
		participantLastNameList = new String[LoadingWindow.LastNameList.length];			//create the participantLastNameList and assign the size of the LastNameList in Loading window to it
		sheetHeader = new String[dataBase.getSheetColumn(0) + 3];							//create the sheetHeader and assign the size of (the number of columns of the file + 3[adding wrist band, time, and notes column] ) to it
		registeredParticipant = new String[dataBase.getSheetRow(0)][dataBase.getSheetColumn(0) + 3];	//create the registeredParticipant and assign the size of rows and (the number of columns of the file + 3[adding wrist band, time, and notes column] ) to it
		teamNameList = new String[LoadingWindow.teamNames.length];						//create the teamNameList and assign the size of the teamNames in Loading window to it
		for(int column = 0; column < dataBase.getSheetColumn(0); column ++) {				//keep looping until the program reaches the last columns of the data sheet
			sheetHeader[column] = dataBase.readCell(0, column);							//store the contents of the first row into the header array
		}//end for loop
		sheetHeader[WRIST_BAND_COLUMN] = "Wrist Band Seen?";								//manually add the contents for the three additional column
		sheetHeader[ENTRY_TIME_COLUMN] = "Entry Time";
		sheetHeader[NOTES_COLUMN] = "Notes";
		participantFirstNameList = LoadingWindow.FirstNameList;							//sync the information loaded in the loading window to the corresponding arrays
		participantLastNameList = LoadingWindow.LastNameList;
		teamNameList = LoadingWindow.teamNames;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegistrationWindow window = new RegistrationWindow();					//create the GUI window
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RegistrationWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();													//create the frame of the window
		frame.getContentPane().setBackground(SystemColor.controlHighlight);		//set the background color of the frame
		frame.getContentPane().setLayout(null);									//set the layout of the frame to null

		JLabel lblRelayForLife = new JLabel("Relay For Life Registration");		//create a label called "lblRelayForLife"
		lblRelayForLife.setForeground(new Color(255, 255, 0));					//set the font color of the label
		lblRelayForLife.setFont(new Font("Lucida Grande", Font.BOLD, 18));		//set the font of the label
		lblRelayForLife.setBounds(185, 33, 262, 33);								//set the size and location of the label
		frame.getContentPane().add(lblRelayForLife);								//add the label to the panel

		JLabel lblFirstName = new JLabel("First Name");							//create a label called "lblFirstName"
		lblFirstName.setForeground(new Color(255, 255, 255));						//set the font color of the label
		lblFirstName.setBounds(35, 107, 73, 16);									//set the size and location of the label
		frame.getContentPane().add(lblFirstName);								//add the label to the panel

		JComboBox cboFirstName = new JComboBox();								//create a combo box called "cboFirstName"
		cboFirstName.setEditable(true);											//set the combo box editable
		cboFirstName.setModel(new DefaultComboBoxModel(participantFirstNameList));//load the options of the combo box
		cboFirstName.setBounds(35, 135, 151, 27);								//set the size and location of the combo box
		frame.getContentPane().add(cboFirstName);								//add the combo box to the panel

		JLabel lblLastName = new JLabel("Last Name");							//create a label called "lblLastName"
		lblLastName.setForeground(Color.WHITE);									//set the font color of the label
		lblLastName.setBounds(206, 107, 73, 16);									//set the size and location of the label
		frame.getContentPane().add(lblLastName);									//add the label to the panel

		JLabel lblTeamName = new JLabel("Team Name");							//create a label called "lblTeamName"
		lblTeamName.setForeground(Color.WHITE);									//set the font color of the label
		lblTeamName.setBounds(35, 174, 82, 16);									//set the size and location of the label
		frame.getContentPane().add(lblTeamName);									//add the label to the panel

		JComboBox cboLastName = new JComboBox();									//create a combo box called "cboLastName"
		cboLastName.setModel(new DefaultComboBoxModel(participantLastNameList));	//load the options of the combo box
		cboLastName.setEditable(true);											//set the combo box editable
		cboLastName.setBounds(206, 135, 151, 27);								//set the size and location of the combo box
		frame.getContentPane().add(cboLastName);									//add the combo box to the panel

		JComboBox cboTeamName = new JComboBox();									//create a combo box called "cboTeamName"
		cboTeamName.setModel(new DefaultComboBoxModel(teamNameList));				//load the options of the combo box
		cboTeamName.setEditable(true);											//set the combo box editable
		cboTeamName.setBounds(35, 202, 151, 27);									//set the size and location of the combo box
		frame.getContentPane().add(cboTeamName);									//add the combo box to the panel

		JRadioButton rdbtnWristbandReceived = new JRadioButton("Wrist Band Seen ");//create a radio button called "rdbtnWristBandReceived"
		rdbtnWristbandReceived.setForeground(Color.WHITE);						//set the color of the radio button
		rdbtnWristbandReceived.setBounds(30, 314, 179, 23);						//set the location and size of the radio button
		frame.getContentPane().add(rdbtnWristbandReceived);						//add the radio button to the panel

		JCheckBox chckbxAdmitted = new JCheckBox("Raised Over $50");				//create a check box called "chckbxAdmitted"
		chckbxAdmitted.setForeground(Color.WHITE);								//set the font color of the check box
		chckbxAdmitted.setBounds(30, 279, 156, 23);								//set the location of the check box
		frame.getContentPane().add(chckbxAdmitted);								//add the check box to the panel

		JCheckBox chckbxIndividual = new JCheckBox("Individual Participant");		//create a check box called "chckbxIndividual"
		chckbxIndividual.setForeground(Color.WHITE);								//set the font color of the check box
		chckbxIndividual.setBounds(30, 241, 165, 23);							//set the location of the check box
		frame.getContentPane().add(chckbxIndividual);							//add the check box to the panel

		txtNotes = new JTextField();												//create a text field called "txtNotes"
		txtNotes.setBounds(205, 313, 130, 26);									//set the size and location of the text field
		frame.getContentPane().add(txtNotes);									//add the text field to the panel
		txtNotes.setColumns(10);													//set the columns of the text field to 10

		JLabel lblNotes = new JLabel("Notes");									//create a label called "lblNotes"
		lblNotes.setForeground(Color.WHITE);										//set the font color of the label
		lblNotes.setBounds(207, 283, 61, 16);									//set the size and location of the label
		frame.getContentPane().add(lblNotes);									//add the label to the panel

		JLabel lblTimeEntered = new JLabel("Time Entered");						//create a label called "lblTimeEntered"
		lblTimeEntered.setForeground(Color.WHITE);								//set the font color of the label
		lblTimeEntered.setBounds(207, 174, 105, 16);								//set the size and location of the label
		frame.getContentPane().add(lblTimeEntered);								//add the label to the panel

		JComboBox cboHour = new JComboBox();										//create a combo box called "cboHour"
		cboHour.setEditable(true);												//set the editable of the combo box to true
		cboHour.setModel(new DefaultComboBoxModel(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"}));		//load the option of the combo box
		cboHour.setSelectedIndex(11);											//set the selected index to 11
		cboHour.setBounds(205, 203, 73, 27);										//set the size and location of the combo box
		frame.getContentPane().add(cboHour);										//add the combo box to the panel

		JComboBox cboMinute = new JComboBox();									//create a combo box called "cboMinute"
		cboMinute.setEditable(true);												//set the editable of the combo box to true
		cboMinute.setModel(new DefaultComboBoxModel(new String[] {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"}));			//load the option of the combo box
		cboMinute.setBounds(205, 241, 73, 27);									//set the size and location of the combo box
		frame.getContentPane().add(cboMinute);									//add the combo box to the panel

		JLabel lblHr = new JLabel("Hr");											//create a label called "lblHr"
		lblHr.setForeground(Color.WHITE);										//set the font color of the label
		lblHr.setBounds(290, 207, 20, 16);										//set the size and location of the label
		frame.getContentPane().add(lblHr);										//add the label to the panel

		JLabel lblMin = new JLabel("Min");										//create a label called "lblMin"
		lblMin.setForeground(Color.WHITE);										//set the font color of the label
		lblMin.setBounds(286, 245, 26, 16);										//set the size and location of the label
		frame.getContentPane().add(lblMin);										//add the label to the panel

		JLabel lblRegistered = new JLabel("Registered:");							//create a label called "lblRegistered"
		lblRegistered.setForeground(Color.WHITE);								//set the font color of the label
		lblRegistered.setBounds(460, 59, 122, 16);								//set the size and location of the label
		frame.getContentPane().add(lblRegistered);

		JButton btnClear = new JButton("Clear");									//create a button called "btnClear"
		btnClear.addActionListener(new ActionListener() {							//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				chckbxIndividual.setSelected(false);								//reset the selection of the check boxes
				chckbxAdmitted.setSelected(false);

				rdbtnWristbandReceived.setSelected(false);						//reset the selection of the radio button
				cboFirstName.setSelectedIndex(0);								//reset the selections of the combo box
				cboLastName.setSelectedIndex(0);
				cboTeamName.setSelectedIndex(0);
				cboHour.setSelectedIndex(0);
				cboMinute.setSelectedIndex(0);
				txtNotes.setText(null);											//reset the content of the text field
				rdbtnWristbandReceived.setEnabled(true);							//enable the radio button
				chckbxIndividual.setEnabled(true);								//enable the check boxes
				chckbxAdmitted.setEnabled(true);
			}
		});
		btnClear.setBounds(380, 164, 156, 56);									//set the location and size of the button
		frame.getContentPane().add(btnClear);									//add the button to the panel


		JButton btnSearchprofile = new JButton("Search Profile");					//create a button called "btnSearchProfile"
		btnSearchprofile.addActionListener(new ActionListener() {					//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				cboHour.setSelectedItem(LocalDateTime.now().getHour());			//automatic record the time of the entry with the system time
				cboMinute.setSelectedItem(LocalDateTime.now().getMinute());		

				try {
					RelayParticipant individual = new RelayParticipant(String.valueOf(cboFirstName.getSelectedItem()), String.valueOf(cboLastName.getSelectedItem()));		//create the participant profile with the first name and last name inputted in the combo box
					dataBase.searchProfile(String.valueOf(cboFirstName.getSelectedItem()), String.valueOf(cboLastName.getSelectedItem()));		//try to match a existing profile in the dataBase
					//System.out.println("Found: " + participant_found);
					//System.out.println("Index: " + dataBase.participant_index);
					if(dataBase.participant_index != -1) {						//if the profile is found in the dataBase
						if  (JOptionPane.showConfirmDialog(null, "The profile you entered matched with 1 profile in database.\nPlease review the details", "Matched Profile",  JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){		//display the "Found" message
							chckbxIndividual.setEnabled(false);					//disable the check boxes
							chckbxAdmitted.setEnabled(false);
						}//end if (JOptionPane.showConfirmDialog(null, "The profile you entered matched with 1 profile in database.\nPlease review the details", "Matched Profile",  JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
						if(Double.valueOf(dataBase.readCell(dataBase.participant_index,DONATION_COLUMN)) >= MINIMUM_RAISED_AMOUNT) {		//check if this participant has raised enough money to enter the event
							individual.admited = true;							//set the admited of the participant to true 
							chckbxAdmitted.setSelected(true);					//set the "admitted" check box to true
						}else {
							individual.admited = false;							//if the participant has not raised enough money
							chckbxAdmitted.setSelected(false);					//set the "admitted" check box to false
							JOptionPane.showMessageDialog(null, "This student is not permited to participate the even because the student has not raised the minimum donation requirement of $" + MINIMUM_RAISED_AMOUNT +"\n Please do the following: \n 1). Double check with the student to see if he/she has raised the $50\n 2). If the student hasn't raised the money, CUT THE STUDENT'S WRIST BAND and take it from the student \n 3). Explain to the student about the reasons why he/she does not pass the registration \n 4). Send the student back to classes if they have not raised $50 to attend the event.\n\nLet staff members know if the student believes there is a mistake or there is any complication, Thank you!", "Participant Not Admitted", JOptionPane.ERROR_MESSAGE); // display the error message
						}//end else if(Double.valueOf(dataBase.readCell(dataBase.participant_index,DONATION_COLUMN)) >= MINIMUM_RAISED_AMOUNT)
						individual.donationRaised = Double.valueOf(dataBase.readCell(dataBase.participant_index,DONATION_COLUMN));		//get the participant's donation raised from the dataBase

						if(dataBase.readCell(dataBase.participant_index, TEAM_CAPTAIN_COLUMN).equalsIgnoreCase("TRUE")){				//check if the participant is the captain of the team
							individual.isCaptain = true;							//set the participant's isCaptain to true
							JOptionPane.showMessageDialog(null, "This participant is the capitain of the team.\n Please remind the participant to pick up the team bag for the team.\n Thanks!", "Reminder", JOptionPane.INFORMATION_MESSAGE);		//remind the captain to pick up the team bag
						}else {													//if the participant is not the captain of the team
							individual.isCaptain = false;						//set the participant's isCaptain to false
						}//end else if (dataBase.readCell(dataBase.participant_index, TEAM_CAPTAIN_COLUMN).equalsIgnoreCase("TRUE"))

						if(dataBase.readCell(dataBase.participant_index, TEAM_NAME_COLUMN) != null) {					//check if the participant is in a team or not
							individual.teamName = dataBase.readCell(dataBase.participant_index, TEAM_NAME_COLUMN) ;	//record the team name of the participant
							cboTeamName.setSelectedItem(individual.teamName);											//select the corresponding team in the Team name check box
							if(individual.teamName != null) {														//if the participant belongs to a team 
								chckbxIndividual.setEnabled(false);													//disable the check box individual if the participant is in a team
							}else {																					//if the participant is an individual participant
								chckbxIndividual.setEnabled(true);													//enable the check box individual
							}//end else if(individual.teamName != null)

						}//end if (dataBase.readCell(dataBase.participant_index, TEAM_NAME_COLUMN) != null)

						if(individual.admited == true) {																//check if the participant is admitted to enter the event
							registeredParticipant[registeredIndex][FIRST_NAME_COLUMN] = dataBase.readCell(dataBase.participant_index, FIRST_NAME_COLUMN);	//record the information of the admitted participant in the 2D array 
							registeredParticipant[registeredIndex][LAST_NAME_COLUMN] = dataBase.readCell(dataBase.participant_index, LAST_NAME_COLUMN);
							registeredParticipant[registeredIndex][TEAM_CAPTAIN_COLUMN] = individual.isCaptain + "";
							registeredParticipant[registeredIndex][TEAM_NAME_COLUMN] = individual.teamName;
							registeredParticipant[registeredIndex][DONATION_COLUMN] = individual.donationRaised + "";
						}//end if (individual.admited == true)

					}else {													//if the names entered does not match up with any profile in the dataBase
						JOptionPane.showMessageDialog(null, "The profile you entered does not match with any profile in database. \nPlease double check with the spellings of names. \nIf the student believes that this is a mistake, please send them to our staff members", "Unmatched Profile", JOptionPane.WARNING_MESSAGE);		//display the "Not Found" message	
					}//end else if (dataBase.participant_index != -1)

				}catch (IOException | NumberFormatException | HeadlessException | BiffException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//end catch

			}//end action performer
		});
		btnSearchprofile.setBounds(380, 98, 156, 56);								//set the location and size of the button
		frame.getContentPane().add(btnSearchprofile);								//add the button to the panel

		JButton btnManuallyOverride = new JButton("Manually Override");				//create a button called "btnManuallyOverride"
		btnManuallyOverride.addActionListener(new ActionListener() {					//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				chckbxIndividual.setEnabled(true);									//enable the check boxes
				chckbxAdmitted.setEnabled(true);
			}
		});
		btnManuallyOverride.setBounds(380, 230, 156, 56);								//set the location and size of the button
		frame.getContentPane().add(btnManuallyOverride);								//add the button to the panel


		JButton btnRegister = new JButton("Register");								//create a button called "btnRegister"
		btnRegister.addActionListener(new ActionListener() {							//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {

				try {
					if(Double.valueOf(registeredParticipant[registeredIndex][DONATION_COLUMN]) >= MINIMUM_RAISED_AMOUNT || chckbxAdmitted.isSelected() == true) {		//check if the participant is admitted
						registeredParticipant[registeredIndex][WRIST_BAND_COLUMN] = rdbtnWristbandReceived.isSelected() + "";												//get the wrist band, entry time, and notes information for the admitted participant 
						registeredParticipant[registeredIndex][ENTRY_TIME_COLUMN] = cboHour.getSelectedItem() + ":" + cboMinute.getSelectedItem();
						registeredParticipant[registeredIndex][NOTES_COLUMN] = txtNotes.getText(); 
						JOptionPane.showConfirmDialog(null, "The registration process is completed! \nthis profile has been stored in the database :)",  "Registration Completed", JOptionPane.OK_CANCEL_OPTION );		//display the complete message
						if(registeredIndex <= registeredParticipant.length) {					//check if the program has reached the end of the array for the registered participants
							registeredIndex ++;												//move on to the next row if there is space available
						}else {																//if there is no more space in the array
							JOptionPane.showMessageDialog(null, "The program has runned out of memory! \n Please check if all the data has been saved and reopen the program \n Thank you!", "Unable to Save More", JOptionPane.WARNING_MESSAGE);		//display "Full" message
						}//end else if(registeredIndex <= registeredParticipant.length)
						btnClear.doClick();													//clear the information entered
					}//end if (Double.valueOf(registeredParticipant[registeredIndex][DONATION_COLUMN]) >= MINIMUM_RAISED_AMOUNT || chckbxAdmitted.isSelected() == true)
				}catch (NullPointerException err) {									//if there is no inforamtion of the student in the registered participant array
					JOptionPane.showMessageDialog(null, "The student cannot be regiserted for the event as he/she has not meet the minimum donation goal of $50.\n If the student thinks there is a mistake, please direct them to staff members.\n ", "Failure", JOptionPane.ERROR_MESSAGE);		//display the error message and direct the student to the staff members
				}//end catch

				registrationData.setOutputFile(Paths.get(LoadingWindow.dataFilePath).getParent() + "/" +LoadingWindow.dataFileName);	//set the destination to the same folder where the dataBase file is located
				try {
					registrationData.write(registeredParticipant, sheetHeader);	//create the excel file with registered participants in the folder
				} catch (WriteException | IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}//end catch 
				lblRegistered.setText("Registered:" + registeredIndex);			//display the number of registered participants
			}
		});
		btnRegister.setBounds(380, 294, 156, 56);									//set the location and size of the button
		frame.getContentPane().add(btnRegister);										//add the button to the panel

		frame.setBounds(100, 100, 600, 400);											//set the size and location of the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);							//enable the exit function of the window
	}//end method
}//end class
