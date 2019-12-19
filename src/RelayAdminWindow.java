import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.SystemColor;
import javax.swing.JTextField;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.awt.event.ActionEvent;
import java.awt.Font;
/*
 * Description: This program is designed to allow the staff members to organize the list that the RegistrationWindow has generated.
 * It will first ask the users to locate the file that they wish to edit, and users can sort the list by specific columns once the 
 * list has been loaded to the program. The program can also display the total amount of donation a specific team has raised.
 * Author: Geoffrey Qin
 * Version: v1.0
 * Date: May 24, 2018
 */

public class RelayAdminWindow {

	private JFrame frame;
	static ExcelReader masterList = new ExcelReader();		//enable the excel file writer
	static ExcelWriter listGenerator = new ExcelWriter();		//enable the excel file reader
	static String participantProfile[][];					//create a 2D array String to hold participants' profiles
	static String teamList[];								//create a 1D array String to hold team names
	static String sheetHeader[];								//create a 1D array String to hold the headers of the excel data file
	static String dataFilePath;								//create a String to store the path of the excel data file
	static String fileNameModifer;							//create a String to hold the fileNameModifer(Sorted by which column)

	///////////////////////////////////// constants /////////////////////////////////////

	static final int FIRST_NAME_COLUMN = 0;					//create the constants for each column in the data sheet
	static final int LAST_NAME_COLUMN = 1;
	static final int TEAM_CAPTAIN_COLUMN = 24;
	static final int TEAM_NAME_COLUMN = 25;
	static final int DONATION_COLUMN = 28;
	static final int WRIST_BAND_COLUMN = 29;
	static final int ENTRY_TIME_COLUMN = 30;
	static final int NOTES_COLUMN = 31;
	static final int TEAM_SIZE_LIMIT = 10;					//create a constant to store the size limit of each team

	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RelayAdminWindow window = new RelayAdminWindow();			//create the GUI window
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
	public RelayAdminWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();												//create the frame of the window
		frame.getContentPane().setBackground(SystemColor.controlHighlight);	//set the background color of the frame
		frame.getContentPane().setLayout(null);								//set the layout of the frame to null

		JComboBox cboSortBy = new JComboBox();								//create a combo box called "cboSortBy"
		cboSortBy.setBounds(36, 107, 196, 27);								//set the size and location of the combo box
		frame.getContentPane().add(cboSortBy);								//add the combo box to the panel

		JLabel lblSortBy = new JLabel("Sort By");							//create a label called "lblSortBy"
		lblSortBy.setForeground(Color.WHITE);								//set the font color of the label
		lblSortBy.setBounds(36, 70, 85, 16);									//set the size and location of the label
		frame.getContentPane().add(lblSortBy);								//add the label to the panel

		JComboBox cboTeamName = new JComboBox();								//create a combo box called "cboTeamName"
		cboTeamName.setBounds(36, 174, 196, 27);								//set the size and location of the combo box
		frame.getContentPane().add(cboTeamName);								//add the combo box to the panel

		JLabel lblLookUpTeam = new JLabel("Look Up Team");					//create a label called "lblLookUpTeam"
		lblLookUpTeam.setForeground(Color.WHITE);							//set the font color of the label
		lblLookUpTeam.setBounds(36, 146, 117, 16);							//set the size and location of the label
		frame.getContentPane().add(lblLookUpTeam);							//add the label to the panel

		JLabel lblStatus = new JLabel("Status:");							//create a label called "lblStatus"
		lblStatus.setForeground(Color.WHITE);								//set the font color of the label
		lblStatus.setBounds(36, 254, 61, 16);								//set the size and location of the label
		frame.getContentPane().add(lblStatus);								//add the label to the panel

		JLabel lblLoadingStatus = new JLabel("");							//create a label called "lblLoadingStatus"
		lblLoadingStatus.setForeground(Color.WHITE);							//set the font color of the label
		lblLoadingStatus.setBounds(103, 234, 341, 64);						//set the size and location of the label
		frame.getContentPane().add(lblLoadingStatus);						//add the label to the panel

		JLabel lblAdminTools = new JLabel("Admin Tools");						//create a label called "lblAdminTools"
		lblAdminTools.setFont(new Font("Lucida Grande", Font.PLAIN, 18));		//set the font of the label
		lblAdminTools.setForeground(Color.WHITE);							//set the font color of the label
		lblAdminTools.setBounds(36, 16, 147, 24);							//set the size and location of the label
		frame.getContentPane().add(lblAdminTools);							//add the label to the panel

		JButton btnLoadFile = new JButton("Locate File");						//create a button called "btnLoadFile"
		btnLoadFile.addActionListener(new ActionListener() {					//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();					//enable the file chooser
				int status = chooser.showOpenDialog(null);					//create an integer to store the status of the chooser
				if (status == JFileChooser.APPROVE_OPTION) {					//if the choose is at approve option 
					File file = chooser.getSelectedFile();					//get the filed selected
					if (file == null) {										//if the file is null
						return;												//return null
					}//end if(file == null)

					dataFilePath = chooser.getSelectedFile().getAbsolutePath();	//store the path of the file selected

					masterList.setInputFile(dataFilePath);					//load the inputFile of the excel reader with the file selected
					try {									
						sheetHeader = new String[masterList.getSheetColumn(0)];	//set the length of the sheetHeader String array to the number of columns of the spreadsheet
						participantProfile = new String [masterList.getSheetRow(0) - 1][masterList.getSheetColumn(0) ]; // set the lengths of the participantProfile String 2D array to the number of columns and rows of the spreadsheet(one less row to exclude headers row)
						teamList = new String[masterList.getSheetRow(0)]; 		//set the length of the teamList String array to the number of rows of the spreadSheet
						int teamIndex = 0;									//create an integer called teamIndex to store the index of the team
						for (int readProfiles = 0; readProfiles < participantProfile.length; readProfiles++) {		//load the profiles into the participantProfile array
							participantProfile[readProfiles][FIRST_NAME_COLUMN] = masterList.readCell(readProfiles + 1 , FIRST_NAME_COLUMN);		//read the corresponding cells after the header column
							participantProfile[readProfiles][LAST_NAME_COLUMN] = masterList.readCell(readProfiles + 1, LAST_NAME_COLUMN);	
							participantProfile[readProfiles][TEAM_NAME_COLUMN] = masterList.readCell(readProfiles + 1, TEAM_NAME_COLUMN);
							participantProfile[readProfiles][TEAM_CAPTAIN_COLUMN] = masterList.readCell(readProfiles + 1, TEAM_CAPTAIN_COLUMN);
							participantProfile[readProfiles][DONATION_COLUMN] = masterList.readCell(readProfiles + 1, DONATION_COLUMN);
							participantProfile[readProfiles][WRIST_BAND_COLUMN] = masterList.readCell(readProfiles + 1, WRIST_BAND_COLUMN);
							participantProfile[readProfiles][ENTRY_TIME_COLUMN] = masterList.readCell(readProfiles + 1, ENTRY_TIME_COLUMN);
							participantProfile[readProfiles][NOTES_COLUMN] = masterList.readCell(readProfiles + 1, NOTES_COLUMN);
							for(int checkRepeat = teamList.length - 1; checkRepeat >= 0; checkRepeat--) {							//keep looping until the program checked all the cells of checkRepeat
								if(participantProfile[readProfiles][TEAM_NAME_COLUMN].equals(teamList[checkRepeat])) {			//check if the team name is already existing in the options of the combo box
									break;									//break the loop if the team has been entered into the list
								}else {										//if the team name has not been entered in the list
									if(checkRepeat == 0) {																		//if every cell in the teamList has been checked and no repetition has been found
										teamList[teamIndex] = participantProfile[readProfiles][TEAM_NAME_COLUMN];						//add the team name to teamList
										teamIndex ++;																				//move on to the next cell
									}//end if (checkRepeat == 0)
								}//end else if (participantProfile[readProfiles][TEAM_NAME_COLUMN].equals(teamList[checkRepeat]))
							}//end for(checkRepeat) loop

							lblLoadingStatus.setText("Database Loading ... (" + (readProfiles + 1) + "/" + (masterList.getSheetRow(0) - 1) + ")");		//display the current status of the program(which profiles it is at and how many more to go)
							lblLoadingStatus.paintImmediately(lblLoadingStatus.getVisibleRect());														//update the content of the label immediately
						}//end for(readProfiles) loop

					} catch (IOException | BiffException e1) {
						// TODO Auto-generated catch block
						System.err.println("Ther has been a problem with reading the file(declaring array)");		//display the error message
						e1.printStackTrace();
					}//end catch

					for(int column = 0; column < sheetHeader.length; column++) {			//keep looping until the program has reached the end of sheetHeader array
						try {
							sheetHeader[column] = masterList.readCell(0, column);			//load the contents of the first row into the sheetHeader array
							lblLoadingStatus.setText("Loading Headers...");				//display the status of the program
							lblLoadingStatus.paintImmediately(lblLoadingStatus.getVisibleRect());		//update the content of the label immediately
						} catch (BiffException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}//end catch

					}//end for loop
					cboTeamName.setModel(new DefaultComboBoxModel(teamList));				//load the options of the combo boxes
					cboSortBy.setModel(new DefaultComboBoxModel(sheetHeader));				
					lblLoadingStatus.setText("Loading Complete!");						//display "Loading Complete"

				}//if(status == JFileChooser.APPROVE_OPTION)

			}//end action performer
		});
		btnLoadFile.setBounds(254, 31, 150, 45);											//set the location and size of the button
		frame.getContentPane().add(btnLoadFile);											//add the button to the panel

		JButton btnSortList = new JButton("Sort List");									//create a button called "btnSortList"
		btnSortList.addActionListener(new ActionListener() {								//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				lblLoadingStatus.setText("Sorting...");									//display the status of the program
				lblLoadingStatus.paintImmediately(lblLoadingStatus.getVisibleRect());		//update the content of the status label immediately
				selection_Sort_Ascending(cboSortBy.getSelectedIndex());					//sort the participant profiles by the category selected by the user in the combo box "cboSortBy"
				fileNameModifer = (String) cboSortBy.getSelectedItem();					//store what category did the user asked to sort by

				listGenerator.setOutputFile(Paths.get(dataFilePath).getParent()+ "/Relay Data " + fileNameModifer + " Sorted.xls");		//set the destination of the output file
				lblLoadingStatus.setText("<html>" + " List Generated! Please check the original folder: "+"<br />"  +
						Paths.get(dataFilePath).getParent() +"<br />" +
						" for " + fileNameModifer+ " sorted list" + "</html>");			//display the instruction

				try {
					listGenerator.write(participantProfile, sheetHeader);					//create the sorted excel file
				} catch (WriteException | IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}//end catch

			}//end action performer
		});
		btnSortList.setBounds(254, 98, 150, 45);											//set the location and size of the button
		frame.getContentPane().add(btnSortList);											//add the button to the panel

		JButton btnTeamReport = new JButton("Team Report");								//create a button called "btnTeamReport"
		btnTeamReport.addActionListener(new ActionListener() {							//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				String targetTeam = (String) cboTeamName.getSelectedItem();				//store the name of the team that requested the report for
				int teamSize = 0;														//create an integer to store the size of the team
				int teamMemberIndex = 0;													//create an integer to store the index of the team member
				double teamMembersDonation[] = new double[TEAM_SIZE_LIMIT];				//create an array of double to store the team members' donations
				double teamTotalDonation = 0;											//create a double to store the net donation of the team
				for(int row = 0; row < participantProfile.length; row++) {				//keep looping until the program has reached the last row of the participantProfile
					if(participantProfile[row][TEAM_NAME_COLUMN].equalsIgnoreCase(targetTeam) == true) {	//check if the participant is from this team
						teamMembersDonation[teamMemberIndex] = Double.valueOf(participantProfile[row][DONATION_COLUMN]);	//if the participant is from the team, store the particpant's donation in the array "teamMembersDonation"
						teamMemberIndex++;												//move on to the next cell
						teamSize++;														//add one to the teamSize
					}//end if (participantProfile[row][TEAM_NAME_COLUMN].equalsIgnoreCase(targetTeam) == true)
				}//end for loop
				RelayTeam target = new RelayTeam(targetTeam);							//create a team under this name
				teamTotalDonation = target.getTeamDonation(teamSize, teamMembersDonation);//calculate the sum donation of the team using recursion
				JOptionPane.showMessageDialog(null, "Team " + targetTeam + " has " + teamSize + " members.\n The total donation is $ " + teamTotalDonation + "." , "Team Report", JOptionPane.INFORMATION_MESSAGE);	//display the report of the team
			}//end action performer
		});
		btnTeamReport.setBounds(254, 165, 147, 45);										//set the location and size of the button
		frame.getContentPane().add(btnTeamReport);										//add the button to the panel


		frame.setBounds(100, 100, 450, 350);												//set the size and location of the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);								//enable the exit function of the window

	}

	public static void selection_Sort_Ascending(int column) {								//sort the array by the column selected in an ascending order
		String lowestCell = null;														//reset lowestCell to null
		String swapInformation[] = new String [participantProfile[0].length] ;			//create a string called "swapInformation" that has the same number of columns with the array to store the row needed to be swapped temporarily
		double lowestValue = 0;															//declare an integer to store the value that got swapped
		int lowestValueIndex = 0;														//declare an integer to store the position of where the lowest value occur
		int 	currentSortingPosition = 0;													//declare an integer to store which cell of the array is the program checking

		if(participantProfile[0][column] == null) {										//check if the first row has any data(if not, it means that user has not put any data in the table yet)
			System.err.println("\nNo data has been stored yet");							//print out the error message
		}//end if(teachAssist[0][category] == null)
		else {																			//run only if there is data stored in the array already
			for(currentSortingPosition = 0; currentSortingPosition < participantProfile.length ; currentSortingPosition ++ ) {		//keep looping until all the values are in the right place
				lowestValueIndex = currentSortingPosition;								//store the index number of the cell that the program is currently at
				lowestCell = participantProfile[currentSortingPosition][column];			//store the content of the current cell that the program is at as the new lowest cell 
				if(column == DONATION_COLUMN) {											//run if the user wants to sort the donation column
					lowestValue = Double.valueOf(participantProfile[currentSortingPosition][column]);		//store the value of the current cell that the program is at as the new lowest value 
				}//end if (column == DONATION_COLUMN)
				for(int row = currentSortingPosition; row < (participantProfile.length); row++) {		//starting from the place that are still unsorted, keep looping until the last cell of the array is reached
					if(column == DONATION_COLUMN) {													//run if the user wants to sort the donation column
						if(Double.valueOf(participantProfile[row][column]) < lowestValue) {			//check if this cell is smaller than the lowest value we find so far
							lowestValue = Double.valueOf(participantProfile[row][column]);			//store this cell's value as the new lowest value  
							lowestValueIndex = row;										//store the index number of this cell
						}//end if (Double.valueOf(participantProfile[row][column]) < lowestValue)
					}else {																//run if the user is not sorting donations
						if(participantProfile[row][column].compareTo(lowestCell) < 0) {	//check if this cell is smaller than the lowest cell we find so far
							lowestCell = participantProfile[row][column];				//store this cell's content as the new lowest cell 
							lowestValueIndex = row;										//store the index number of this cell
						}//end if (participantProfile[row][column].compareTo(lowestCell) < 0)
					}//end else if  (column == DONATION_COLUMN)
				}//end for (int row = currentSortingPosition; row < (participantProfile.length); row++)
				swapInformation = participantProfile[lowestValueIndex] ;					//copy the contents of the row with the lowest cell into "swapInformation"
				participantProfile[lowestValueIndex] = participantProfile[currentSortingPosition];		//store the value of the place where the program is currently sorting/determining into the cell that has the lowest cell
				participantProfile[currentSortingPosition] = swapInformation;				//store the lowest cell the program find into the cell that the program is sorting/determining 
				swapInformation = null;													//reset "swapInformation" to null
			}//end for (currentSortingPosition = 0; currentSortingPosition < participantProfile.length ; currentSortingPosition ++ )
		}//end else if(participantProfile[0][column] == null)
	}//end method selection_Sort_Ascending()
	
}//end class
