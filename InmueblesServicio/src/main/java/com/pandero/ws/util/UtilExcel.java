package com.pandero.ws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author alcabrera
 */
public class UtilExcel {
	private static Logger logger = Logger.getLogger(UtilExcel.class);

	public enum CELL_TYPE {
		CELL_TYPE_NUMERIC, CELL_TYPE_STRING, CELL_TYPE_FORMULA, CELL_TYPE_BLANK, CELL_TYPE_BOOLEAN, CELL_TYPE_ERROR;
		public static CELL_TYPE valueOf(int index) {
			return values()[index];
		}
	}

	/**
	 * @param path
	 *            path of excel file location
	 * @param sheetNumber
	 *            number of sheet to work
	 * @return
	 */
	public static XSSFSheet readExcelToXSSFSheet(String path, int sheetNumber) {
		FileInputStream file;
		XSSFWorkbook workbook = null;
		try {
			file = new FileInputStream(new File(path));
			// Create Workbook instance holding reference to .xlsx file
			workbook = new XSSFWorkbook(file);
		} catch (FileNotFoundException e) {
			logger.error(e, e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e, e);
			e.printStackTrace();
		}
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber); // first sheet = 0
		return sheet;
	}

	/**
	 * @param path
	 *            path of excel file location
	 * @param sheetNumber
	 *            number of sheet to work
	 * @return
	 */
	public static List<List<Object>> readExcelToListObject(String path,
			int sheetNumber) {
		FileInputStream file;
		XSSFWorkbook workbook = null;
		try {
			file = new FileInputStream(new File(path));
			// Create Workbook instance holding reference to .xlsx file
			workbook = new XSSFWorkbook(file);
		} catch (Exception e) {
			logger.error(e, e);
			e.printStackTrace();
		}
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber); // first sheet = 0
		Iterator<Row> rowIterator = sheet.iterator();
		List<List<Object>> rowValues = new ArrayList<List<Object>>();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// For each row, iterate through all the columns
			Iterator<Cell> cellIterator = row.cellIterator();
			List<Object> columnValues = new ArrayList<Object>();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (CELL_TYPE.valueOf(cell.getCellType())) {
				case CELL_TYPE_NUMERIC:
					cell.setCellType(Cell.CELL_TYPE_STRING);
					columnValues.add(cell.getStringCellValue());
					break;
				case CELL_TYPE_BOOLEAN:
					columnValues.add(cell.getBooleanCellValue());
					break;
				default:
					cell.setCellType(Cell.CELL_TYPE_STRING);
					columnValues.add(cell.getStringCellValue());
					break;
				}
			}
			rowValues.add(columnValues);
		}
		return rowValues;
	}

	/**
	 * @param sheet
	 *            XSSFSheet object to work
	 * @return
	 */
	public static List<Map<String, Object>> transformXSSFSheetToListofMap(
			XSSFSheet sheet) {
		List<Map<String, Object>> listOfMaps = new ArrayList<Map<String, Object>>();
		int rowCount = 0;
		// list where variables found in the first row are stored
		List<String> variableList = new ArrayList<String>();
		// list where values found in the other rows are stored
		List<List<String>> valuesList = new ArrayList<List<String>>();
		try {
			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				List<String> columnValues = new ArrayList<String>();
				int countCellValue = 0;

				for (int i = 0; i < row.getLastCellNum(); i++) {
					Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					if (rowCount == 0) {
						// if current row is the first row variables name of
						// current column are obtained and stored in the variable list
						variableList.add(cell.getStringCellValue());
					} else {
						if (countCellValue >= variableList.size()) {
							break;
						}
						countCellValue++;
						// otherwise add value of current column to the value list
						columnValues.add(cell.getStringCellValue());
					}
				}
				if (rowCount != 0) {
					// if not is the first row add list of value of the current row to value list
					valuesList.add(columnValues);
				}
				rowCount++;
			}
			for (List<String> row : valuesList) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				for (int i = 0; i < row.size(); i++) {
					String columnValue = row.get(i);
					map.put(variableList.get(i).toString().trim(),
							columnValue.trim());
				}
				listOfMaps.add(map);
			}
		} catch (Exception ex) {
			logger.error(ex, ex);
		}
		return listOfMaps;
	}

	public static List<Map<String, String>> readCsvFile(InputStream inputStream) {
		// TODO Auto-generated method stub
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		List<String> keys = new ArrayList<String>();
		Scanner scanner = new Scanner(inputStream);
		int count = 0;
		while (scanner.hasNextLine()) {
			count++;
			String arr[] = scanner.nextLine().split(",", -1);
			Map<String, String> map = new HashMap<String, String>();
			int colCont = 0;
			for (String current : arr) {
				String value = current;
				if (1 == count) {
					keys.add(value);
				}
				if (1 < count) {
					map.put(keys.get(colCont), value);
				}
				colCont++;
			}
			if (!map.isEmpty()) {
				data.add(map);
			}
		}
		return data;
	}

	public static List<List<String>> readCsvFilePlane(InputStream inputStream) {
		// TODO Auto-generated method stub
		List<List<String>> data = new ArrayList<List<String>>();
		Scanner scanner = new Scanner(inputStream);
		while (scanner.hasNextLine()) {
			String arr[] = scanner.nextLine().split(",", -1);
			List<String> values = new ArrayList<String>();
			for (String current : arr) {
				String value = current;
				values.add(value);
			}
			if (!values.isEmpty()) {
				data.add(values);
			}
		}
		return data;
	}

	/**
	 * @param values
	 *            for fill excel file
	 * @param sheetNumber
	 * @param sheetName
	 * @param folder
	 * @param fileName
	 */
	public static File createExcelFile(List<Map<String, Object>> values,
			String sheetName, String fileName) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		FileOutputStream out = null;
		XSSFSheet sheet = workbook.createSheet(sheetName);
		File file = null;
		for (int i = -1; i < values.size(); i++) {
			Map<String, Object> currentRow = values.get(i == -1 ? i + 1 : i);
			Row row = sheet.createRow(i + 1);
			int j = 0;
			for (Map.Entry<String, Object> entry : currentRow.entrySet()) {
				Cell cell = row.createCell(j);
				sheet.setColumnWidth(j, 6000);
				if (i == -1) {
					cell.setCellValue(entry.getKey());
				} else {
					cell.setCellValue(entry.getValue().toString());
				}
				j++;
			}
		}
		try {
			// out = new FileOutputStream(new File(String.format("%s/%s.xlsx",
			// folder, fileName)));
			file = new File(System.getProperty("java.io.tmpdir"), fileName);
			out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
		} catch (Exception ex) {
			logger.error(ex, ex);
		}
		return file;
	}
}