package com.fanhong.cn.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public final class CommonUtility {
	public final static String TAG = "friends";
	public final static String dirName = "YCSZ";
	public final static String appFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + dirName +File.separator;

	public final static String recordFilePath = appFilePath +"打钻"+File.separator;
	public final static String finalFilePath = appFilePath +"实钻"+File.separator;
	public final static String processFilePath = appFilePath +"轨迹"+File.separator;
	public final static String backupFilePath = appFilePath +"备份"+File.separator;

	public final static String timefileName = "YCSZ-TB.CSV";
	public final static String timefileName_backup = "YCSZ-TB_BACKUP.CSV";
	public final static String finalfilePre= "Final";
	public final static String trackfilePre = "End";
	public final static String tgfileName = "YCSZ-TG";
	public final static String tgfileName2 = "YCSZ-TG.CSV";
	public final static String tgfileName3 = "YCSZ-TG1.CSV";

	public final static String tb_tgData = "tgDataTb";
	public final static String tb_Time = "TimeTb";
	public final static String tb_transTime = "transTimeTb";
	public final static String tb_finalName = "finalDataTb";
	public final static String tb_trajectoryName = "trajectoryDataTb";
	public final static String SERVICE_ACTION = "com.sky.ccriclient.TransferService";
	public static enum DATA_TYPE {
		TG,
		TIME,
		TRANS_TIME,
		FINAL_DATA,
		TRACK_DATA
	};

	public static enum ActionType {
		ImportTG,
		ImportTime,
		ImportTransTime,
		ExportFinalData,
		ExportTrackData,
		ExportTransTime
	};

	public static final int openfileDialogTgId = 1;
	public static final int openfileDialogTimeId = 2;
	public static final int openfileDialogTrackId = 3;
	public static final int openfileDialogCommonCsvId = 4;

	public static double doubleDataToOneDecimal(double data) {
//        BigDecimal bg = new BigDecimal(data);
//        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        return f1;

//        double b=Math.round(data*10)/10.0;//保留1位小数
//        return b;
		double x = 0;
		DecimalFormat decimalFormat =new DecimalFormat("0.00");
		//decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
		x = Double.valueOf(decimalFormat.format(data));
		Log.i(TAG, ""+x);
		return x;
	}




	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete();

				Log.i(TAG, "文件删除成功 ");
				//Toast.makeText(context, "文件删除成功", Toast.LENGTH_SHORT).show();
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				Log.i(TAG, "当前选择的是目录 ");
				//Toast.makeText(context, "当前选择的是目录", Toast.LENGTH_SHORT).show();
			}
			file.delete();
		} else {
			Log.i(TAG, "文件不存在 ");
			//Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
		}
	}

	// 复制文件
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

}
