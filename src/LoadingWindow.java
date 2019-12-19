import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.SystemColor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.JTextField;

import jxl.read.biff.BiffException;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
/*
 * Description: This program is designed to allow the user to select the data file for the dataBase, customize the name of 
 * the output excel file, and load the data files to the RegistrationWindow program.
 * Author: Geoffrey Qin
 * Version: v1.0
 * Date: May 27, 2018
 */
public class LoadingWindow {

	private JFrame frame;
	private JTextField txtFileName;
	static ExcelReader dataSheet = new ExcelReader();						//enable the excel reader
	public static String dataFilePath;									//create a String to store the path of the data file
	static String dataFileName;											//create a String to store the name of the output file name
	static String[] FirstNameList;										//create a String array to store the first names of the participants
	static String[]	LastNameList;										//create a String array to store the last names of the participants
	static String[] teamNames;											//create a String array to store the team names

	///////////////////////////////////// constants /////////////////////////////////////

	static final int FIRST_NAME_COLUMN = 0;								//create the constants for each column in the data sheet
	static final int LAST_NAME_COLUMN = 1;
	static final int TEAM_NAME_COLUMN = 25;

	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoadingWindow window = new LoadingWindow();			//create the GUI winodw
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
	public LoadingWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();												//create the frame of the window
		frame.getContentPane().setBackground(SystemColor.controlHighlight);	//set the background color of the frame
		frame.getContentPane().setLayout(null);								//set the layout of the frame to null


		JLabel lblFileSelected = new JLabel("File Selected:");				//create a label called "lblFileSelected"
		lblFileSelected.setForeground(Color.WHITE);							//set the font color of the label
		lblFileSelected.setBounds(174, 45, 244, 16);							//set the size and location of the label
		frame.getContentPane().add(lblFileSelected);							//add the label to the panel

		JLabel lblStatus = new JLabel("Status:");							//create a label called "lblStatus"
		lblStatus.setForeground(Color.WHITE);								//set the font color of the label
		lblStatus.setBounds(31, 143, 51, 16);								//set the size and location of the label
		frame.getContentPane().add(lblStatus);								//add the label to the panel

		JLabel lblLoadingStatus = new JLabel("");							//create a label called "lblLoadingStatus"
		lblLoadingStatus.setForeground(Color.WHITE);							//set the font color of the label
		lblLoadingStatus.setBounds(35, 181, 380, 72);						//set the size and location of the label
		frame.getContentPane().add(lblLoadingStatus);						//add the label to the panel

		JLabel lblOutputFileName = new JLabel("Output File Name:");			//create a label called "lblOutputFileName"
		lblOutputFileName.setForeground(Color.WHITE);						//set the font color of the label
		lblOutputFileName.setBounds(28, 92, 128, 16);						//set the size and location of the label
		frame.getContentPane().add(lblOutputFileName);						//add the label to the panel

		txtFileName = new JTextField();										//create a text field called "txtFileName"
		txtFileName.setText("2018 May 31st Relay Participants List");			//set the content of the text field
		txtFileName.setBounds(174, 87, 246, 26);								//set the size and location of the text field
		frame.getContentPane().add(txtFileName);								//add the text field to the panel
		txtFileName.setColumns(10);											//set the columns of the text field to 10


		JButton btnImportFile = new JButton("Import File");					//create a button called "btnImportFile"
		btnImportFile.addActionListener(new ActionListener() {				//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();					//enable the file chooser
				int status = chooser.showOpenDialog(null);					//create an integer to store the status of the chooser
				if (status == JFileChooser.APPROVE_OPTION){					//if the choose is at approve option 
					File file = chooser.getSelectedFile();					//get the filed selected
					if (file == null) {										//if the file is null
						return;												//return null
					}//end if(file == null)

					dataFilePath = chooser.getSelectedFile().getAbsolutePath();	//store the path of the file selected
				}//end if (status == JFileChooser.APPROVE_OPTION)
				lblFileSelected.setText("File Selected:" + dataFilePath);		//display the file path in the label
			}//end action performer
		});
		btnImportFile.setBounds(22, 40, 128, 29);							//set the location and size of the button
		frame.getContentPane().add(btnImportFile);							//add the button to the panel



		JButton btnLaunch = new JButton("Launch");							//create a button called "btnLaunch"
		btnLaunch.addActionListener(new ActionListener() {					//create the actionlistener of the button
			public void actionPerformed(ActionEvent e) {
				dataFileName = txtFileName.getText();						//store the output file name that the user wanted and entered in the text field
				dataSheet.setInputFile(dataFilePath);						//set the inputFile of the excel reader to the file selected
				try {
					FirstNameList = new String[dataSheet.getSheetRow(0)];		//assign the size of the number of rows in the dataSheet to arrays
					LastNameList = new String[dataSheet.getSheetRow(0)];
					teamNames = new String[dataSheet.getSheetRow(0)];
					for (int readProfiles = 1; readProfiles < dataSheet.getSheetRow(0); readProfiles++) {	//keep looping until the program reached the last row of the dataSheet(started from 1 to exclude the header column)

						FirstNameList[readProfiles] = dataSheet.readCell(readProfiles, FIRST_NAME_COLUMN);	//load the information from the dataSheet to the corresponding array
						LastNameList[readProfiles] = dataSheet.readCell(readProfiles, LAST_NAME_COLUMN);
						teamNames[readProfiles] = dataSheet.readCell(readProfiles, TEAM_NAME_COLUMN);
						lblLoadingStatus.setText("Database Loading ... (" + readProfiles + "/" + (dataSheet.getSheetRow(0) - 1) + ")");	//display the current status of the program(which profiles it is at and how many more to go)
						lblLoadingStatus.paintImmediately(lblLoadingStatus.getVisibleRect());					//update the content of the label immediately
					}//end for loop
					RegistrationWindow.main(null);							//run the registrationWindow
				} catch (BiffException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//end catch
			}//end action performer
		});
		btnLaunch.setBounds(300, 138, 117, 29);								//set the location and size of the button
		frame.getContentPane().add(btnLaunch);								//add the button to the panel

		frame.setBounds(100, 100, 450, 300);									//set the size and location of the window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);					//enable the exit function
	}//end method
}//end class
