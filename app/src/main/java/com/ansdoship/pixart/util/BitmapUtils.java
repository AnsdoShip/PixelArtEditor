package com.ansdoship.pixart.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


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
				compressBitmapToBMP(bitmap, fileOS);
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
					compressBitmapToBMP(bitmap, fileOS);
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
		Bitmap bitmap = null;
		if (file.exists()) {
			if (pathAndName.toLowerCase().endsWith(".png") ||
					pathAndName.toLowerCase().endsWith(".jpg") ||
					pathAndName.toLowerCase().endsWith(".jpeg")) {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					bitmap = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD());
					fileInputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				if (bitmap != null) {
					return bitmap.copy(Bitmap.Config.ARGB_8888, true);
				}
			}
			else if (pathAndName.toLowerCase().endsWith(".bmp")) {
				bitmap = decodeBMPFromFile(file).copy(Bitmap.Config.ARGB_8888, true);
				if (!bitmap.hasAlpha()) {
					bitmap.setHasAlpha(true);
				}
				return bitmap;
			}
		}
		return null;
	}

	public static Bitmap rotateBitmap (Bitmap src, int degrees) {
		if (src != null) {
			Matrix matrix = new Matrix();
			matrix.setRotate(degrees, src.getWidth() * 0.5f, src.getHeight() * 0.5f);
			src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
			return src;
		}
		return null;
	}

	public static Bitmap flipBitmapHorizontally (Bitmap src) {
		if (src != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(-1, 1);
			src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
			return src;
		}
		return null;
	}

	public static Bitmap flipBitmapVertically (Bitmap src) {
		if (src != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(1, -1);
			src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
			return src;
		}
		return null;
	}

	private static byte[] addBMPFileHeader(int size) {
		byte[] buffer = new byte[14];
		buffer[0] = 0x42;
		buffer[1] = 0x4D;
		buffer[2] = (byte) (size + 54);
		buffer[3] = (byte) ((size + 54) >> 8);
		buffer[4] = (byte) ((size + 54) >> 16);
		buffer[5] = (byte) ((size + 54) >> 24);
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

	private static byte[] addBMPInfomation(int size, int width, int height) {
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
		buffer[20] = (byte) (size);
		buffer[21] = (byte) (size >> 8);
		buffer[22] = (byte) (size >> 16);
		buffer[23] = (byte) (size >> 24);
		buffer[24] = (byte) 0xC4;
		buffer[25] = 0x0E;
		buffer[26] = 0x00;
		buffer[27] = 0x00;
		buffer[28] = (byte) 0xC4;
		buffer[29] = 0x0E;
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

	private static byte[] addDIBPixels (Bitmap bitmap) {
	 	int width = bitmap.getWidth();
	 	int height = bitmap.getHeight();
	 	byte[] buffer = new byte[width * height * 4];
	 	int[] pixels = new int[width * height];
	 	bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	 	int offset = 0;
	 	for (int i = pixels.length - 1; i >= 0; i -= width) {
	 		for (int j = i - width + 1; j <= i; j ++) {
				buffer[offset] = (byte) (Color.blue(pixels[j]));
				buffer[offset + 1] = (byte) (Color.green(pixels[j]));
				buffer[offset + 2] = (byte) (Color.red(pixels[j]));
				buffer[offset + 3] = (byte) (Color.alpha(pixels[j]));
				offset += 4;
			}
		}
		return buffer;
	}

	private static void compressBitmapToBMP (final Bitmap bitmap, final FileOutputStream fileOS) {
		byte[] DIBPixels = addDIBPixels(bitmap);
		byte[] header = addBMPFileHeader(DIBPixels.length);
		byte[] info = addBMPInfomation(DIBPixels.length, bitmap.getWidth(), bitmap.getHeight());
		byte[] buffer = new byte[DIBPixels.length + header.length + info.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(info, 0, buffer, header.length, info.length);
		System.arraycopy(DIBPixels, 0, buffer, header.length + info.length, DIBPixels.length);
		try {
			fileOS.write(buffer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Bitmap decodeBMPFromFile (String pathAndName) {
	 	File file = new File(pathAndName);
	 	return decodeBMPFromFile(file);
	}

	private static Bitmap decodeBMPFromFile (File file) {
	 	Bitmap bitmap = null;
	 	if (file.exists()) {
			try {
				FileInputStream fileIS = new FileInputStream(file);
				ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length;
				while ((length = fileIS.read(buffer)) != -1) {
					byteArrayOS.write(buffer, 0, length);
				}
				byte[] data = byteArrayOS.toByteArray();
				if (data[28] == 0x20 && data[29] == 0x00) {
					byte[] DIBPixels = new byte[data.length - 54];
					System.arraycopy(data, 54, DIBPixels, 0, data.length - 54);
					int width;
					width = data[21] & 0xff;
					width = width << 8 | data[20] & 0xff;
					width = width << 8 | data[19] & 0xff;
					width = width << 8 | data[18] & 0xff;
					int height;
					height = data[25] & 0xff;
					height = height << 8 | data[24] & 0xff;
					height = height << 8 | data[23] & 0xff;
					height = height << 8 | data[22] & 0xff;
					int[] pixels = new int[width * height];
					int y;
					int index;
					for (int i = 0; i < height; i ++) {
						y = height - i - 1;
						for (int x = 0; x < width; x ++) {
							index = i * width * 4 + x * 4;
							pixels[y * width + x] = Color.argb(
									DIBPixels[index + 3] & 0xff,
									DIBPixels[index + 2] & 0xff,
									DIBPixels[index + 1] & 0xff,
									DIBPixels[index] & 0xff
							);
						}
					}
					bitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
				}
				else {
					bitmap = BitmapFactory.decodeFileDescriptor(fileIS.getFD());
				}
				fileIS.close();
				byteArrayOS.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	 	return bitmap;
	}

}
