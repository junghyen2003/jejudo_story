package com.namooplus.jejurizmandroid;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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

import static com.namooplus.jejurizmandroid.common.AppSetting.SAVE_EXCEL_PATH;

/**
 * Created by HeungSun-AndBut on 2016. 6. 14..
 */

public class ExcelManager {

    //private final static String EXCEL_FILE_NAME = "CAMERA_INFOMATION_DATA.xls";


    private static ExcelManager instance;
    private File mExcelDir;
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

        //엑셀 파일 생성
        mExcelDir = new File(SAVE_EXCEL_PATH);

        //기존 엑셀 파일이 있는지 있으면 해당 줄만큼 들고오기
        if (!mExcelDir.exists()) {
            mExcelDir.mkdir();
        }

    }

    public File createExcelFile(String fileName) {
        File mExcelFile = new File(mExcelDir, fileName);

        if(mExcelFile.exists()) {
            mExcelFile.delete();
        }

        //최초일경우 첫줄 정의
        initExcelFile(mExcelFile);

        return mExcelFile;
    }


    /*public void readExcelFile() {
        try {
            FileInputStream myInput = new FileInputStream(mExcelFile);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            if (myWorkBook.getNumberOfSheets() != 0) {
                HSSFSheet mySheet = myWorkBook.getSheetAt(0); //첫번째 시트만 활용

                mLastRowNum = mySheet.getLastRowNum();

                Iterator rowIter = mySheet.rowIterator();

                while (rowIter.hasNext()) {
                    HSSFRow myRow = (HSSFRow) rowIter.next(); // 한줄 데이터
                    Iterator cellIter = myRow.cellIterator();
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }*/

    private void initExcelFile(File excelFile) {
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
            c.setCellValue("제목");
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue("저장 주소");
            c.setCellStyle(cs);

            c = row.createCell(2);
            c.setCellValue("조도");
            c.setCellStyle(cs);

            c = row.createCell(3);
            c.setCellValue("방향");
            c.setCellStyle(cs);

            c = row.createCell(4);
            c.setCellValue("위도");
            c.setCellStyle(cs);

            c = row.createCell(5);
            c.setCellValue("경도");
            c.setCellStyle(cs);

            os = new FileOutputStream(excelFile);
            myWorkBook.write(os);
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
    }

    public void saveExcelFile(File excelFile,String imagePath,  String mTitle, float bright, float direction, double lat, double lon) {
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

            c = row.createCell(0);
            c.setCellValue(mTitle);

            c = row.createCell(1);
            c.setCellValue(imagePath);

            c = row.createCell(2);
            c.setCellValue(bright);

            c = row.createCell(3);
            c.setCellValue(direction);

            c = row.createCell(4);
            c.setCellValue(lat);

            c = row.createCell(5);
            c.setCellValue(lon);

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
