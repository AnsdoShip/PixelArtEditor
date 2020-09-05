package com.ansdoship.ansdopix.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class BitmapUtils {
	 public static boolean saveBMP2PNG(String pathAndName, Bitmap bitmap, boolean override) {
		boolean result = false;
		File file = new File(pathAndName);
		if(!file.exists()) {
			try
			{
				result = file.createNewFile();
				FileOutputStream fileOS = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
				fileOS.flush();
				fileOS.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				result = false;
			}
		}
		else if(file.exists()) {
			if(override) {
				 try
				 {
					 result = file.createNewFile();
					 FileOutputStream fileOS = new FileOutputStream(file);
					 bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
					 fileOS.flush();
					 fileOS.close();
				 }
				 catch (IOException e)
				 {
				 	e.printStackTrace();
					 result = false;
				 }
			}
		}
		return result;
	}
	
	public static boolean saveBMP2JPEG(String pathAndName, Bitmap bitmap, boolean override, int value) {
		boolean result = false;
		File file = new File(pathAndName);
		if(!file.exists()) {
			try
			{
				result = file.createNewFile();
				FileOutputStream fileOS = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, value, fileOS);
				fileOS.flush();
				fileOS.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				result = false;
			}
		}
		else if(file.exists()) {
			if(override) {
				try
				{
					result = file.createNewFile();
					FileOutputStream fileOS = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, value, fileOS);
					fileOS.flush();
					fileOS.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					result = false;
				}
			}
		}
		return result;
	}
	
	public static Bitmap loadBitmapFromFile(String pathAndName) {
		File file = new File(pathAndName);
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}

	public static Bitmap rotateBitmap (Bitmap src, int degrees) {
		if (src != null) {
			Matrix matrix = new Matrix();
			matrix.setRotate(degrees, src.getWidth() * 0.5f, src.getHeight() * 0.5f);
			return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
		}
		return null;
	}

	public static Bitmap flipBitmapHorizontally (Bitmap src) {
		if (src != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(-1, 1);
			return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
		}
		return null;
	}

	public static Bitmap flipBitmapVertically (Bitmap src) {
		if (src != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(1, -1);
			return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
		}
		return null;
	}
	
}
