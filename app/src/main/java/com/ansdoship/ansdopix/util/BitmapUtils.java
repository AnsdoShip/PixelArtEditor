package com.ansdoship.ansdopix.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class BitmapUtils {
	 public static boolean saveBitmapToPNG(String pathAndName, Bitmap bitmap, boolean override) {
		boolean result = false;
		File file = new File(pathAndName + ".png");
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
		File file = new File(pathAndName + ".jpeg");
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

	public static boolean saveBitmapToBase64(String pathAndName, Bitmap bitmap, boolean override) {
		boolean result = false;
		File file = new File(pathAndName + ".base64");
		if(!file.exists()) {
			try
			{
				result = file.createNewFile();
				FileOutputStream fileOS = new FileOutputStream(file);
				fileOS.write(bitmapToBase64(bitmap).getBytes());
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
					fileOS.write(bitmapToBase64(bitmap).getBytes());
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

	public static String bitmapToBase64(Bitmap bitmap) {
	 	String result = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			if (bitmap != null) {
				byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
				byteArrayOutputStream.flush();
				byteArrayOutputStream.close();
				byte[] bytes = byteArrayOutputStream.toByteArray();
				result = Base64.encodeToString(bytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.flush();
					byteArrayOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static Bitmap loadBitmapFromFile(String pathAndName){
		File file = new File(pathAndName);
		if (file.exists()) {
			if (pathAndName.endsWith(".png") || pathAndName.endsWith(".jpg") || pathAndName.endsWith(".jpeg")) {
				return BitmapFactory.decodeFile(file.getAbsolutePath());
			}
			if (pathAndName.endsWith(".base64")) {
				try {
					if (file.length() > Integer.MAX_VALUE) {
						return null;
					}
					FileInputStream fileInputStream = new FileInputStream(file);
					byte[] bytes = new byte[(int) file.length()];
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) file.length());
					int length;
					while ((length = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
						byteArrayOutputStream.write(bytes, 0, length);
					}
					return base64ToBitmap(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static Bitmap base64ToBitmap (String base64Data) {
	 	byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
	 	return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
