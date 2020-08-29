package com.ansdoship.ansdopix.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class FileIO {
	 public static boolean saveBitmapToPNG(String pathAndName, Bitmap bitmap, boolean override) {
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
	
	public static boolean saveBitmapToJPEG(String pathAndName, Bitmap bitmap, boolean override, int value) {
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
	
}
