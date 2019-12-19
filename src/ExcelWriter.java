import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
/*
 * Description: This program is designed to output the data collected by generating a excel sheet. It will
 * create headers and contents of the list as specified, and it can also customize the content of a specific cell.
 * Author: Geoffrey Qin
 * Version: v1.0
 * Date: May 23, 2018
 */

public class ExcelWriter {

	private WritableCellFormat timesBoldUnderline;					//enable the WritableCellFormat for BoldUnderline and regular times fonts
	private WritableCellFormat times;								
	private String inputFile;										//create a String to store the path of the file

	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;									//set the inputFile of the reader to the String parameter
	}//end setOutputFile

	public void write(String[][] information, String[] headers) throws IOException, WriteException {
		File file = new File(inputFile);								//enable the file under the path selected
		WorkbookSettings wbSettings = new WorkbookSettings();			//enable WorkbookSettings

		wbSettings.setLocale(new Locale("en", "EN"));				//set the locale of the WorkbookSettings

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);	//create a workbook with the Workbook Setting at the path selected
		workbook.createSheet("Report", 0);							//create the first layer of the sheet
		WritableSheet excelSheet = workbook.getSheet(0);				//enable WritableSheet

		createLabel(excelSheet, headers);							//create the headers of the sheet
		createContent(excelSheet, information, headers);				//create the contents of the sheet

		workbook.write();											
		workbook.close();
	}//end write

	private void createLabel(WritableSheet sheet, String[] headers)
			throws WriteException {
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);	// Lets create a times font

		times = new WritableCellFormat(times10pt);					 // Define the cell format

		times.setWrap(true);											 // Lets automatically wrap the cells


		WritableFont times10ptBoldUnderline = new WritableFont(		
				WritableFont.TIMES, 10, WritableFont.BOLD, false,
				UnderlineStyle.SINGLE);								// create create a bold font with unterlines
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);

		timesBoldUnderline.setWrap(true);						    // Lets automatically wrap the cells

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

		// Write a few headers
		for(int column = 0; column < headers.length; column++) {		//keeps looping until the program reached the end of headers array
			addLabel(sheet, column, 0, headers[column]);				//create a label at the specific column with corresponding header
		}//end for loop


	}//end method

	private void createContent(WritableSheet sheet, String[][] information, String[] headers) throws WriteException,
	RowsExceededException {
		for(int row = 0; row < information.length; row++) {			//keeps looping until the program has reached the end of information's row
			for(int column = 0 ; column < information[row].length; column++) {	//keep looping until the program has reached the end of the information's cells on the row
				addLabel(sheet, column, row + 1, information[row][column]);		//create a label at the specific row and column with the corresponding contents
			}//end for (int column = 0 ; column < information[row].length; column++)
		}//end for(int row = 0; row < information.length; row++) 

	}//end method
	
	protected void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;													//enable label
		label = new Label(column, row, s, times);					//create the label at the specific row and column with the specific content
		sheet.addCell(label);										//add the label to the sheet
	}//end method

	protected void addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;													//enable label
		label = new Label(column, row, s, timesBoldUnderline);		//create the label at the specific row and column with the specific content in Bold Underline format
		sheet.addCell(label);										//add the label to the sheet
	}//end method

	protected void addNumber(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException {
		Number number;												//enable number
		number = new Number(column, row, integer, times);				//create a number input at the specific row and column with the specific number
		sheet.addCell(number);										//add the cell to the sheet
	}//end method

	public static void main(String[] args) throws WriteException, IOException {
		ExcelWriter test = new ExcelWriter();						//testing
		test.setOutputFile("Desktop/Registration Data.xls");//"c:/temp/lars.xls");
		//test.write();
		System.out.println("Please check the result file under c:/temp/lars.xls ");
	}//end main
}//end class