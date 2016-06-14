package com.namooplus.jejurizmandroid;

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

    private final static String EXCEL_FILE_NAME = "CAMERA_INFOMATION_DATA.xls";


    private static ExcelManager instance;
    private File mExcelFile;
    private int mLastRowNum = 0;

    public static ExcelManager getInstance() {
        if (instance == null) {
            instance = new ExcelManager();
        }

        return instance;
    }

    public ExcelManager() {

        Log.i("HS", "엑셀 초기화");
        //저장공간을 사용할수 있는지 확인
        String extStorageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(extStorageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            Log.e("HS", "초기화가 되지 않았습니다.");
        }

        //엑셀 파일 생성
        mExcelFile = new File(Environment.getExternalStorageDirectory(), EXCEL_FILE_NAME);

        //기존 엑셀 파일이 있는지 있으면 해당 줄만큼 들고오기
        if (mExcelFile.exists()) {
            readExcelFile();
        }

        //최초일경우 첫줄 정의
        if (mLastRowNum == 0) {
            initExcelFile();
        }

    }


    public void readExcelFile() {
        try {
            FileInputStream myInput = new FileInputStream(mExcelFile);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            if (myWorkBook.getNumberOfSheets() != 0) {
                HSSFSheet mySheet = myWorkBook.getSheetAt(0); //첫번째 시트만 활용

                mLastRowNum = mySheet.getLastRowNum();
                Log.i("HS", "마지막 번호 :  " + mLastRowNum);

                /** We now need something to iterate through the cells. **/
                Iterator rowIter = mySheet.rowIterator();

                while (rowIter.hasNext()) {
                    HSSFRow myRow = (HSSFRow) rowIter.next(); // 한줄 데이터
                    Iterator cellIter = myRow.cellIterator();
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        Log.d("HS", "Cell Value: " + myCell.toString());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    private void initExcelFile() {
        FileOutputStream os = null;
        try {
            // New Workbook
            HSSFWorkbook myWorkBook = new HSSFWorkbook();

            Cell c = null;

            // Cell style for header row
            CellStyle cs = myWorkBook.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.LIME.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            Sheet sheet1 = myWorkBook.createSheet("사진정보");

            //만약에 처음 파일을 생성하게 된다면
            Log.i("HS", "엑셀 파일 최초 생성으로 최상단 태그명 붙이기");
            // Generate column headings
            Row row = sheet1.createRow(0);

            c = row.createCell(0);
            c.setCellValue("저장 주소");
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue("조도");
            c.setCellStyle(cs);

            c = row.createCell(2);
            c.setCellValue("방향");
            c.setCellStyle(cs);

            c = row.createCell(3);
            c.setCellValue("위도");
            c.setCellStyle(cs);

            c = row.createCell(4);
            c.setCellValue("경도");
            c.setCellStyle(cs);

            os = new FileOutputStream(mExcelFile);
            myWorkBook.write(os);
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + mExcelFile, e);
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
    }

    public void saveExcelFile(String path, float bright, float direction, double lat, double lon) {
        FileOutputStream os = null;
        Cell c = null;
        try {
            //기존 엑셀 파일 writer 셋팅
            // Creating Input Stream
            FileInputStream myInput = new FileInputStream(mExcelFile);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            HSSFSheet sheet1 = myWorkBook.getSheetAt(0); //첫번째 시트만 활용

            mLastRowNum = sheet1.getLastRowNum();

            Log.i("HS", mLastRowNum + "번째 까지 기록됨 ");

            Log.i("HS", "엑셀 파일 최초 생성으로 최상단 태그명 붙이기");
            // Generate column headings
            Row row = sheet1.createRow(++mLastRowNum);

            c = row.createCell(0);
            c.setCellValue(path);

            c = row.createCell(1);
            c.setCellValue(bright);

            c = row.createCell(2);
            c.setCellValue(direction);

            c = row.createCell(3);
            c.setCellValue(lat);

            c = row.createCell(4);
            c.setCellValue(lon);

            os = new FileOutputStream(mExcelFile);
            myWorkBook.write(os);

            Log.i("HS", "path " + mExcelFile.getAbsolutePath());
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + mExcelFile, e);
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
