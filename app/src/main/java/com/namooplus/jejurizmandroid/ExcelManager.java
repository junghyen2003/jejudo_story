package com.namooplus.jejurizmandroid;

import android.location.Location;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by HeungSun-AndBut on 2016. 6. 14..
 */

public class ExcelManager {

    //private final static String EXCEL_FILE_NAME = "CAMERA_INFOMATION_DATA.xls";

    private final static String EXCEL_SHEET_NAME = "사진정보";
    private final static String EXCEL_CELL_VALUE_STRING_TITLE = "title";
    private final static int EXCEL_CELL_VALUE_TITLE_NUM = 0;
    private final static String EXCEL_CELL_VALUE_STRING_SAVE_PATH = "save_path";
    private final static int EXCEL_CELL_VALUE_SAVE_PATH_NUM = 1;
    private final static String EXCEL_CELL_VALUE_STRING_LIGHT = "light";
    private final static int EXCEL_CELL_VALUE_LIGHT_NUM = 2;
    private final static String EXCEL_CELL_VALUE_STRING_DIRECTION = "direction";
    private final static int EXCEL_CELL_VALUE_DIRECTION_NUM = 3;
    private final static String EXCEL_CELL_VALUE_STRING_LATITUDE = "latitude";
    private final static int EXCEL_CELL_VALUE_LATITUDE_NUM = 4;
    private final static String EXCEL_CELL_VALUE_STRING_LONGITUDE = "longitude";
    private final static int EXCEL_CELL_VALUE_LONGITUDE_NUM = 5;
    private final static String EXCEL_CELL_VALUE_STRING_ORIENTATION = "orientation";
    private final static int EXCEL_CELL_VALUE_ORIENTATION_NUM = 6;



    private static ExcelManager instance;
    private int mLastRowNum = 0;

    public static ExcelManager getInstance() {
        if (instance == null) {
            instance = new ExcelManager();
        }

        return instance;
    }

    public ExcelManager() {
        //저장공간을 사용할수 있는지 확인
        String extStorageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(extStorageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            Log.e("HS", "초기화가 되지 않았습니다.");
        }

    }

    public Location readLatLanForExcel(String path) {
        try {
            File excelFile = new File(path);
            if (excelFile.exists()) {
                FileInputStream myInput = new FileInputStream(excelFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                if (myWorkBook.getNumberOfSheets() != 0) {
                    HSSFSheet mySheet = myWorkBook.getSheetAt(0); //첫번째 시트만 활용

                    HSSFRow firstRow = mySheet.getRow(1);
                    double lat = firstRow.getCell(EXCEL_CELL_VALUE_LATITUDE_NUM).getNumericCellValue();
                    double lon = firstRow.getCell(EXCEL_CELL_VALUE_LONGITUDE_NUM).getNumericCellValue();

                    Location result = new Location("end");
                    result.setLatitude(lat);
                    result.setLongitude(lon);

                    return result;
                }
            }

        } catch (Exception e) {
            //Log.i("HS", "기존 엑셀 값 가져오기 실패 : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public File getExcelFile(File excelFile) {
        FileOutputStream os = null;
        try {
            if (!excelFile.exists()) {
                // New Workbook
                HSSFWorkbook myWorkBook = new HSSFWorkbook();

                Cell c = null;

                // Cell style for header row
                CellStyle cs = myWorkBook.createCellStyle();
                cs.setFillForegroundColor(HSSFColor.LIME.index);
                cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                Sheet sheet1 = myWorkBook.createSheet(EXCEL_SHEET_NAME);

                //만약에 처음 파일을 생성하게 된다면
                Row row = sheet1.createRow(0);

                c = row.createCell(EXCEL_CELL_VALUE_TITLE_NUM);
                c.setCellValue(EXCEL_CELL_VALUE_STRING_TITLE);
                c.setCellStyle(cs);

                c = row.createCell(EXCEL_CELL_VALUE_SAVE_PATH_NUM);
                c.setCellValue(EXCEL_CELL_VALUE_STRING_SAVE_PATH);
                c.setCellStyle(cs);

                c = row.createCell(EXCEL_CELL_VALUE_LIGHT_NUM);
                c.setCellValue(EXCEL_CELL_VALUE_STRING_LIGHT);
                c.setCellStyle(cs);

                c = row.createCell(EXCEL_CELL_VALUE_DIRECTION_NUM);
                c.setCellValue(EXCEL_CELL_VALUE_STRING_DIRECTION);
                c.setCellStyle(cs);

                c = row.createCell(EXCEL_CELL_VALUE_LATITUDE_NUM);
                c.setCellValue(EXCEL_CELL_VALUE_STRING_LATITUDE);
                c.setCellStyle(cs);

                c = row.createCell(EXCEL_CELL_VALUE_LONGITUDE_NUM);
                c.setCellValue(EXCEL_CELL_VALUE_STRING_LONGITUDE);
                c.setCellStyle(cs);


                c = row.createCell(EXCEL_CELL_VALUE_ORIENTATION_NUM);
                c.setCellValue(EXCEL_CELL_VALUE_STRING_ORIENTATION);
                c.setCellStyle(cs);


                os = new FileOutputStream(excelFile);
                myWorkBook.write(os);

            } else {
                return excelFile;
            }
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + excelFile, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Exception ex) {
            }
        }
        return excelFile;
    }

    public void saveExcelFile(File excelFile, String imagePath, String mTitle, float bright,
                              float direction, double lat, double lon, int orientation) {
        FileOutputStream os = null;
        Cell c = null;
        try {
            //기존 엑셀 파일 writer 셋팅
            // Creating Input Stream
            FileInputStream myInput = new FileInputStream(excelFile);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            HSSFSheet sheet1 = myWorkBook.getSheetAt(0); //첫번째 시트만 활용

            mLastRowNum = sheet1.getLastRowNum();

            // Generate column headings
            Row row = sheet1.createRow(++mLastRowNum);
            c = row.createCell(EXCEL_CELL_VALUE_TITLE_NUM);
            c.setCellValue(mTitle);

            c = row.createCell(EXCEL_CELL_VALUE_SAVE_PATH_NUM);
            c.setCellValue(imagePath);

            c = row.createCell(EXCEL_CELL_VALUE_LIGHT_NUM);
            c.setCellValue(bright);

            c = row.createCell(EXCEL_CELL_VALUE_DIRECTION_NUM);
            c.setCellValue(direction);

            c = row.createCell(EXCEL_CELL_VALUE_LATITUDE_NUM);
            c.setCellValue(lat);

            c = row.createCell(EXCEL_CELL_VALUE_LONGITUDE_NUM);
            c.setCellValue(lon);

            c = row.createCell(EXCEL_CELL_VALUE_ORIENTATION_NUM);
            c.setCellValue(orientation);

            os = new FileOutputStream(excelFile);
            myWorkBook.write(os);

        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + excelFile, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
    }

}
