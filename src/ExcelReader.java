import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
/*
 * Description: This program is designed to read the data from an excel sheet. It has built-in methods to
 * read the content of a specific cell, as well as matching the profiles with the specific names entered.
 * Author: Geoffrey Qin
 * Version: v1.0
 * Date: May 22, 2018
 */
public class ExcelReader {

	private String inputFile;							//create a String to store the path of the file
	int participant_index;								//create an integer to store the index of the participants
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;						//set the inputFile of the reader to the String parameter
	}

	public void read() throws IOException  {
		File inputWorkbook = new File(inputFile);		//enable the file under the path selected
		Workbook w;										//enable workbook
		try {
			w = Workbook.getWorkbook(inputWorkbook);		//get the Workbook using the path
			
			Sheet sheet = w.getSheet(0);					// Get the first sheet

			for (int j = 0; j < sheet.getColumns(); j++) {	//keeps looping until the program reached the last column of the data sheet
				for (int i = 0; i < sheet.getRows(); i++) {	//keeps looping until the program reached the last row of the data sheet
					Cell cell = sheet.getCell(j, i);			//enable the cell at row i and column j
					CellType type = cell.getType();			//enable CellType
					if (type == CellType.LABEL) {			//check if the label is text label
						System.out.println("I got a label "	
								+ cell.getContents());		//display get text message
					}

					if (type == CellType.NUMBER) {			//check if the label is number label
						System.out.println("I got a number "
								+ cell.getContents());		//display get number message
					}//end if (type == CellType.NUMBER)

				}//end for (int i = 0; i < sheet.getRows(); i++)
			}//end for(int j = 0; j < sheet.getColumns(); j++) 
		} catch (BiffException e) {
			e.printStackTrace();
		}//end catch
	}//end read
	
	public int getSheetRow(int sn) throws IOException {
		File inputWorkbook = new File(inputFile);		//enable the file under the path selected
		Workbook w;										//enable workbook
		int rValue = 0;									//create a integer to store the number of rows
		try {
			w = Workbook.getWorkbook(inputWorkbook);		//get the Workbook using the path
			Sheet sheet = w.getSheet(sn);				// Get the specified sheet
			rValue = sheet.getRows();					//store the number of rows in rValue
		}catch (BiffException e) {
			e.printStackTrace();
		}//end catch
		return rValue;
	}//end method

	public int getSheetColumn(int sn) throws IOException {
		File inputWorkbook = new File(inputFile);		//enable the file under the path selected
		Workbook w;										//enable workbook
		int cValue = 0;									//create a integer to store the number of column
		try {
			w = Workbook.getWorkbook(inputWorkbook);		//get the Workbook using the path
			Sheet sheet = w.getSheet(sn);				//Get the specified sheet
			cValue = sheet.getColumns();					//store the number of columns in cValue
		}catch (BiffException e) {
			e.printStackTrace();
		}//end catch
		return cValue;
	}//end method 
	
	public String readCell(int row, int column) throws IOException, BiffException  {
		File inputWorkbook = new File(inputFile);		//enable the file under the path selected
		Workbook w;										//enable workbook
		try {
		w = Workbook.getWorkbook(inputWorkbook);			//get the Workbook using the path
		Sheet sheet = w.getSheet(0);						//Look at the first sheet
		Cell cell = sheet.getCell(column, row);			//get the cell at the specified row and column
		return cell.getContents();						//return the contents of the cell

		  } catch (BiffException e) {
		    e.printStackTrace();
		 }//end catch
		return null;
	}//end method
	
	public void searchProfile(String fn, String ln) throws IOException  {
		//input: the first name and the last name of the participant 
		//output: the index number of the row of participant
		File inputWorkbook = new File(inputFile);		//enable the file under the path selected
		Workbook w;										//enable workbook
		try {
			w = Workbook.getWorkbook(inputWorkbook);		//get the Workbook using the path
			
			Sheet sheet = w.getSheet(0);					// Get the first sheet

			for (int row = 0; row < sheet.getRows(); row++) {		//keep looping until the program reached the last row of the dataSheet
				Cell fncell = sheet.getCell(0, row);		//enable the first name and last name cells
				Cell lncell = sheet.getCell(1, row);
				if(fn.equalsIgnoreCase(fncell.getContents()) == true && ln.equalsIgnoreCase(lncell.getContents()) == true) {		//check if the first name and last name columns matched with the names of the participants wanted 
					this.participant_index = row;		//store the row number of the participant
					break;								//break the loop
				}else {									//if the names does not match with any profile
					this.participant_index = -1;			//return -1 to show that the program cannot match the profile
				}//end else if (fn.equalsIgnoreCase(fncell.getContents()) == true && ln.equalsIgnoreCase(lncell.getContents()) == true)

			}//end for loop
		
		} catch (BiffException e) {
			e.printStackTrace();
		}//end catch
	}//end method

	public static void main(String[] args) throws IOException {
		ExcelReader test = new ExcelReader();					//test
		test.setInputFile("/Desktop/Relay for Life Test Data.xls");
		test.read();
		test.searchProfile("Richard", "Brown");
	}//end main

}//end class