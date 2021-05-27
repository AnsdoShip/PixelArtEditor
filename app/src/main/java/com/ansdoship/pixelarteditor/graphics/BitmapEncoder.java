package com.ansdoship.pixelarteditor.graphics;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.math.MathUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public final class BitmapEncoder {

	private BitmapEncoder(){}

	public enum CompressFormat {
		PNG,
		JPEG,
		WEBP,
		BMP
	}

	public interface Callback {
		void onCreateFailure();
		void onCompressFailure();
		void onFileExists(boolean isDirectory);
		void onIOException(IOException e);
	}

	public static @Nullable byte[] encodeByteArray (@NonNull Bitmap bitmap,
													@NonNull CompressFormat format, int quality) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		quality = MathUtils.clamp (quality, 0, 100);
		boolean result = false;
		switch (format) {
			case PNG:
				result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
				break;
			case JPEG:
				result = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
				break;
			case WEBP:
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					if (quality == 100) {
						result = bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS,
								quality, byteArrayOutputStream);
					}
					else {
						result = bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY,
								quality, byteArrayOutputStream);
					}
				}
				else {
					result = bitmap.compress(Bitmap.CompressFormat.WEBP, quality, byteArrayOutputStream);
				}
				break;
			case BMP:
				result = WindowsBitmapCodec.compress(bitmap, byteArrayOutputStream);
				break;
		}
		if (result) {
			return byteArrayOutputStream.toByteArray();
		}
		return null;
	}

	public static void encodeFile (@NonNull String pathname, @NonNull Bitmap bitmap,
								   boolean override, @NonNull CompressFormat format,
								   int quality, @NonNull Callback callback) {
		encodeFile(new File(pathname), bitmap, override, format, quality, callback);
	}

	public static void encodeFile (@NonNull File file, @NonNull Bitmap bitmap,
								   boolean override, @NonNull CompressFormat format,
								   int quality, @NonNull Callback callback) {
		if (file.isDirectory()) {
			callback.onFileExists(true);
			return;
		}
		try
		{
			if (file.exists()) {
				if (!override) {
					callback.onFileExists(false);
					return;
				}
			}
			else {
				if (!file.createNewFile()) {
					callback.onCreateFailure();
					return;
				}
			}
			if (file.canWrite()) {
				FileOutputStream fileOS = new FileOutputStream(file);
				boolean result = false;
				quality = MathUtils.clamp (quality, 0, 100);
				switch (format) {
					case PNG:
						result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
						break;
					case JPEG:
						result = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOS);
						break;
					case WEBP:
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
							if (quality == 100) {
								result = bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS,
										quality, fileOS);
							}
							else {
								result = bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY,
										quality, fileOS);
							}
						}
						else {
							result = bitmap.compress(Bitmap.CompressFormat.WEBP, quality, fileOS);
						}
						break;
					case BMP:
						result = WindowsBitmapCodec.compress(bitmap, fileOS);
						break;
				}
				fileOS.flush();
				fileOS.close();
				if(!result) {
					callback.onCompressFailure();
				}
			}
			else {
				callback.onCreateFailure();
			}
		}
		catch (IOException e)
		{
			callback.onIOException(e);
		}
	}

}
