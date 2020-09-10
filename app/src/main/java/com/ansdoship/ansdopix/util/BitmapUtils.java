package com.ansdoship.ansdopix.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public final class BitmapUtils {
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
	
	public static boolean saveBitmapToJPEG(String pathAndName, Bitmap bitmap, boolean override, int quality) {
		boolean result = false;
		File file = new File(pathAndName + ".jpeg");
		if(!file.exists()) {
			try
			{
				result = file.createNewFile();
				FileOutputStream fileOS = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOS);
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
					bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOS);
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

	public static boolean saveBitmapToBMP(String pathAndName, Bitmap bitmap, boolean override) {
		boolean result = false;
		File file = new File(pathAndName + ".bmp");
		if(!file.exists()) {
			try
			{
				result = file.createNewFile();
				FileOutputStream fileOS = new FileOutputStream(file);
				ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getRowBytes() * bitmap.getHeight());
				bitmap.copyPixelsToBuffer(byteBuffer);
				byte[] DIBPixels = new byte[byteBuffer.remaining()];
				byteBuffer.get(DIBPixels);
				byteBuffer.clear();
				byte[] buffer = new byte[54 + DIBPixels.length];
				byte[] header = addBMPHeader(DIBPixels.length);
				byte[] info = addBMPInfo(bitmap.getWidth(), bitmap.getHeight());
				System.arraycopy(header, 0, buffer, 0, header.length);
				System.arraycopy(info, 0, buffer, 14, info.length);
				System.arraycopy(DIBPixels, 0, buffer, 54, DIBPixels.length);
				fileOS.write(buffer);
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
					ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getRowBytes() * bitmap.getHeight());
					bitmap.copyPixelsToBuffer(byteBuffer);
					byte[] DIBPixels = new byte[byteBuffer.remaining()];
					byteBuffer.get(DIBPixels);
					byteBuffer.clear();
					byte[] buffer = new byte[54 + DIBPixels.length];
					byte[] header = addBMPHeader(DIBPixels.length);
					byte[] info = addBMPInfo(bitmap.getWidth(), bitmap.getHeight());
					System.arraycopy(header, 0, buffer, 0, header.length);
					System.arraycopy(info, 0, buffer, 14, info.length);
					System.arraycopy(DIBPixels, 0, buffer, 54, DIBPixels.length);
					fileOS.write(buffer);
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
	
	public static Bitmap loadBitmapFromFile(String pathAndName){
		File file = new File(pathAndName);
		if (file.exists()) {
			if (pathAndName.endsWith(".bmp") ||
					pathAndName.endsWith(".png") ||
					pathAndName.endsWith(".jpg") ||
					pathAndName.endsWith(".jpeg")) {
				return BitmapFactory.decodeFile(file.getAbsolutePath());
			}
		}
		return null;
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

	private static byte[] addBMPHeader(int size) {
		byte[] buffer = new byte[14];
		buffer[0] = 0x42;
		buffer[1] = 0x4D;
		buffer[2] = (byte) (size);
		buffer[3] = (byte) (size >> 8);
		buffer[4] = (byte) (size >> 16);
		buffer[5] = (byte) (size >> 24);
		buffer[6] = 0x00;
		buffer[7] = 0x00;
		buffer[8] = 0x00;
		buffer[9] = 0x00;
		buffer[10] = 0x36;
		buffer[11] = 0x00;
		buffer[12] = 0x00;
		buffer[13] = 0x00;
		return buffer;
	}

	private static byte[] addBMPInfo(int width, int height) {
		byte[] buffer = new byte[40];
		buffer[0] = 0x28;
		buffer[1] = 0x00;
		buffer[2] = 0x00;
		buffer[3] = 0x00;
		buffer[4] = (byte) (width);
		buffer[5] = (byte) (width >> 8);
		buffer[6] = (byte) (width >> 16);
		buffer[7] = (byte) (width >> 24);
		buffer[8] = (byte) (height);
		buffer[9] = (byte) (height >> 8);
		buffer[10] = (byte) (height >> 16);
		buffer[11] = (byte) (height >> 24);
		buffer[12] = 0x01;
		buffer[13] = 0x00;
		buffer[14] = 0x20;
		buffer[15] = 0x00;
		buffer[16] = 0x00;
		buffer[17] = 0x00;
		buffer[18] = 0x00;
		buffer[19] = 0x00;
		buffer[20] = 0x00;
		buffer[21] = 0x00;
		buffer[22] = 0x00;
		buffer[23] = 0x00;
		buffer[24] = (byte) 0xE0;
		buffer[25] = 0x01;
		buffer[26] = 0x00;
		buffer[27] = 0x00;
		buffer[28] = 0x02;
		buffer[29] = 0x03;
		buffer[30] = 0x00;
		buffer[31] = 0x00;
		buffer[32] = 0x00;
		buffer[33] = 0x00;
		buffer[34] = 0x00;
		buffer[35] = 0x00;
		buffer[36] = 0x00;
		buffer[37] = 0x00;
		buffer[38] = 0x00;
		buffer[39] = 0x00;
		return buffer;
	}

}
