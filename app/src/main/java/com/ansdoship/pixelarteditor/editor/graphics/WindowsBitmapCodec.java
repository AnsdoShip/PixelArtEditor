package com.ansdoship.pixelarteditor.editor.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class WindowsBitmapCodec {

    private WindowsBitmapCodec(){}

    private final static byte[] FILE_TYPE = new byte[] {'B', 'M'};

    private final static byte BITMAP_INFO_SIZE = 40;

    private final static class BitCount {
        // The number of bits-per-pixel is specified or is implied by the JPEG or PNG format.
        final static byte BIT_COUNT_NULL = 0;
        // The bitmap is monochrome, and the bmiColors member of BITMAP_INFO contains two entries.
        // Each bit in the bitmap array represents a pixel.
        // If the bit is clear,
        // the pixel is displayed with the color of the first entry in the bmiColors table;
        // if the bit is set, the pixel has the color of the second entry in the table.
        final static byte BIT_COUNT_1 = 1;
        // The bitmap has a maximum of 16 colors,
        // and the bmiColors member of BITMAP_INFO contains up to 16 entries.
        // Each pixel in the bitmap is represented by a 4-bit index into the color table.
        // For example, if the first byte in the bitmap is 0x1F, the byte represents two pixels.
        // The first pixel contains the color in the second table entry,
        // and the second pixel contains the color in the sixteenth table entry.
        final static byte BIT_COUNT_4 = 4;
        // The bitmap has a maximum of 256 colors,
        // and the bmiColors member of BITMAP_INFO contains up to 256 entries.
        // In this case, each byte in the array represents a single pixel.
        final static byte BIT_COUNT_8 = 8;
        // The bitmap has a maximum of 2^16 colors.
        // If the biCompression member of the BITMAP_INFO_HEADER is BI_RGB,
        // the bmiColors member of BITMAP_INFO is NULL.
        // Each WORD in the bitmap array represents a single pixel.
        // The relative intensities of red, green,
        // and blue are represented with five bits for each color component.
        // The value for blue is in the least significant five bits,
        // followed by five bits each for green and red. The most significant bit is not used.
        // The bmiColors color table is used for optimizing colors used on palette-based devices,
        // and must contain the number of entries specified by the biClrUsed member of the BITMAP_INFO_HEADER.
        // If the biCompression member of the BITMAP_INFO_HEADER is BI_BIT_FIELDS,
        // the bmiColors member contains three DWORD color masks that specify the red,
        // green, and blue components, respectively, of each pixel.
        // Each WORD in the bitmap array represents a single pixel.
        // When the biCompression member is BI_BIT_FIELDS,
        // bits set in each DWORD mask must be contiguous and should not overlap the bits of another mask.
        // All the bits in the pixel do not have to be used.
        final static byte BIT_COUNT_16 = 16;
        // The bitmap has a maximum of 2^24 colors,
        // and the bmiColors member of BITMAP_INFO is NULL.
        // Each 3-byte triplet in the bitmap array represents the relative intensities of blue,
        // green, and red, respectively, for a pixel.
        // The bmiColors color table is used for optimizing colors used on palette-based devices,
        // and must contain the number of entries specified by the biClrUsed member of the BITMAP_INFO_HEADER.
        final static byte BIT_COUNT_24 = 24;
        // The bitmap has a maximum of 2^32 colors.
        // If the biCompression member of the BITMAP_INFO_HEADER is BI_RGB,
        // the bmiColors member of BITMAP_INFO is NULL.
        // Each DWORD in the bitmap array represents the relative intensities of blue,
        // green, and red for a pixel. The value for blue is in the least significant 8 bits,
        // followed by 8 bits each for green and red.
        // The high byte in each DWORD is not used.
        // The bmiColors color table is used for optimizing colors used on palette-based devices,
        // and must contain the number of entries specified by the biClrUsed member of the BITMAP_INFO_HEADER.
        // If the biCompression member of the BITMAP_INFO_HEADER is BI_BIT_FIELDS,
        // the bmiColors member contains three DWORD color masks that specify the red,
        // green, and blue components, respectively, of each pixel.
        // Each DWORD in the bitmap array represents a single pixel.
        // When the biCompression member is BI_BIT_FIELDS,
        // bits set in each DWORD mask must be contiguous and should not overlap the bits of another mask.
        // All the bits in the pixel do not need to be used.
        final static byte BIT_COUNT_32 = 32;
    }

    private final static class CompressFormat {
        // An uncompressed format.
        final static byte BI_RGB = 0;
        // A run-length encoded (RLE) format for bitmaps with 8 bpp.
        // The compression format is a 2-byte format
        // consisting of a count byte followed by a byte containing a color index.
        final static byte BI_RLE_8 = 1;
        // An RLE format for bitmaps with 4 bpp.
        // The compression format is a 2-byte format
        // consisting of a count byte followed by two word-length color indexes.
        final static byte BI_RLE_4 = 2;
        // Specifies that the bitmap is not compressed and that the color table
        // consists of three DWORD color masks that specify the red,
        // green, and blue components, respectively, of each pixel.
        // This is valid when used with 16- and 32-bpp bitmaps.
        final static byte BI_BIT_FIELDS = 3;
        // Indicates that the image is a JPEG image.
        final static byte BI_JPEG = 4;
        // Indicates that the image is a PNG image.
        final static byte BI_PNG = 5;
    }

    private static final int BITMAP_HEADER_SIZE = 14;

    private static final byte PALETTE_COLOR_ALL_USED = 0;

    private static final byte PALETTE_COLOR_ALL_IMPORTANT = 0;

    private static @NonNull byte[] BITMAP_HEADER (int imageSize, int paletteSize) {
        byte[] buffer = new byte[BITMAP_HEADER_SIZE];
        // bfType
        // The file type; must be BM.
        buffer[0] = FILE_TYPE[0];
        buffer[1] = FILE_TYPE[1];
        // bfSize
        // The size, in bytes, of the bitmap file.
        buffer[2] = (byte) (0xFF & (imageSize + BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize));
        buffer[3] = (byte) (0xFF & ((imageSize + BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize) >> 8));
        buffer[4] = (byte) (0xFF & ((imageSize + BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize) >> 16));
        buffer[5] = (byte) (0xFF & ((imageSize + BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize) >> 24));
        // bfReserved1
        // Reserved; must be zero.
        buffer[6] = 0;
        buffer[7] = 0;
        // bfReserved2
        // Reserved; must be zero.
        buffer[8] = 0;
        buffer[9] = 0;
        // bfOffBits
        // The offset, in bytes, from the beginning of the BITMAP_FILE_HEADER structure to the bitmap bits.
        buffer[10] = (byte) (0xFF & (BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize));
        buffer[11] = (byte) (0xFF & ((BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize) >> 8));
        buffer[12] = (byte) (0xFF & ((BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize) >> 16));
        buffer[13] = (byte) (0xFF & ((BITMAP_INFO_SIZE + BITMAP_HEADER_SIZE + paletteSize) >> 24));
        return buffer;
    }

    private static int readInt (@NonNull byte[] data, int offset) {
        int result;
        result = data[offset + 3] & 0xFF;
        result = result << 8 | data[offset + 2] & 0xFF;
        result = result << 8 | data[offset + 1] & 0xFF;
        result = result << 8 | data[offset] & 0xFF;
        return result;
    }

    private static int getBfOffBits (@NonNull byte[] data) {
        return readInt(data, 10);
    }

    private static int getBiWidth (@NonNull byte[] data) {
        return readInt(data, 14 + 4);
    }

    private static int getBiHeight (@NonNull byte[] data) {
        return readInt(data, 14 + 8);
    }

    private static byte getBiCount (@NonNull byte[] data) {
        return data[14 + 14];
    }

    private static byte getBiCompression (@NonNull byte[] data) {
        return data[14 + 16];
    }

    private static int getBiSizeImage (@NonNull byte[] data) {
        return readInt(data, 14 + 20);
    }

    private static int getBiXPelsPerMeter (@NonNull byte[] data) {
        return readInt(data, 14 + 24);
    }

    private static int getBiYPelsPerMeter (@NonNull byte[] data) {
        return readInt(data, 14 + 28);
    }

    private static int getBiClrUsed (@NonNull byte[] data) {
        return readInt(data, 14 + 32);
    }

    private static int getBiClrImportant (@NonNull byte[] data) {
        return readInt(data, 14 + 36);
    }

    private static @NonNull byte[] DIB_DATA (@NonNull byte[] data) {
        int bfOffBits = getBfOffBits(data);
        int IMAGE_SIZE = data.length - bfOffBits;
        byte[] buffer = new byte[IMAGE_SIZE];
        System.arraycopy(data, bfOffBits, buffer, 0, IMAGE_SIZE);
        return buffer;
    }

    private static @NonNull byte[] BITMAP_INFO (int width, int height, byte bitCount,
                                         byte compressFormat, int imageSize, int xPixelsPerMeter,
                                         int yPixelsPerMeter, int paletteColorUsed,
                                         int paletteColorImportant) {
        byte[] buffer = new byte[BITMAP_INFO_SIZE];
        // biSize
        // The number of bytes required by the structure.
        buffer[0] = BITMAP_INFO_SIZE;
        buffer[1] = 0;
        buffer[2] = 0;
        buffer[3] = 0;
        // biWidth
        // The width of the bitmap, in pixels.
        buffer[4] = (byte) (0xFF & width);
        buffer[5] = (byte) (0xFF & (width >> 8));
        buffer[6] = (byte) (0xFF & (width >> 16));
        buffer[7] = (byte) (0xFF & (width >> 24));
        // biHeight
        // The height of the bitmap, in pixels. If biHeight is positive,
        // the bitmap is a bottom-up DIB and its origin is the lower-left corner.
        // If biHeight is negative,
        // the bitmap is a top-down DIB and its origin is the upper-left corner.
        // If biHeight is negative, indicating a top-down DIB,
        // biCompression must be either BI_RGB or BI_BIT_FIELDS.
        // Top-down DIBs cannot be compressed.
        buffer[8] = (byte) (0xFF & height);
        buffer[9] = (byte) (0xFF & (height >> 8));
        buffer[10] = (byte) (0xFF & (height >> 16));
        buffer[11] = (byte) (0xFF & (height >> 24));
        // biPlanes
        // The number of planes for the target device. This value must be set to 1.
        buffer[12] = 1;
        buffer[13] = 0;
        // biBitCount
        // The number of bits-per-pixel.
        // The biBitCount member of the BITMAP_INFO_HEADER structure determines
        // the number of bits that define each pixel and the maximum number of colors in the bitmap.
        buffer[14] = bitCount;
        buffer[15] = 0;
        // biCompression
        // The type of compression for a compressed bottom-up bitmap
        // (top-down DIBs cannot be compressed).
        // This member can be one of the following values.
        buffer[16] = compressFormat;
        buffer[17] = 0;
        buffer[18] = 0;
        buffer[19] = 0;
        // biSizeImage
        // The size, in bytes, of the image. This may be set to zero for BI_RGB bitmaps.
        // If biCompression is BI_JPEG or BI_PNG,
        // biSizeImage indicates the size of the JPEG or PNG image buffer, respectively.
        buffer[20] = (byte) (0xFF & imageSize);
        buffer[21] = (byte) (0xFF & (imageSize >> 8));
        buffer[22] = (byte) (0xFF & (imageSize >> 16));
        buffer[23] = (byte) (0xFF & (imageSize >> 24));
        // biXPelsPerMeter
        // The horizontal resolution, in pixels-per-meter, of the target device for the bitmap.
        // An application can use this value to select a bitmap
        // from a resource group that best matches the characteristics of the current device.
        buffer[24] = (byte) (0xFF & xPixelsPerMeter);
        buffer[25] = (byte) (0xFF & (xPixelsPerMeter >> 8));
        buffer[26] = (byte) (0xFF & (xPixelsPerMeter >> 16));
        buffer[27] = (byte) (0xFF & (xPixelsPerMeter >> 24));
        // biYPelsPerMeter
        // The vertical resolution, in pixels-per-meter, of the target device for the bitmap.
        buffer[28] = (byte) (0xFF & yPixelsPerMeter);
        buffer[29] = (byte) (0xFF & (yPixelsPerMeter >> 8));
        buffer[30] = (byte) (0xFF & (yPixelsPerMeter >> 16));
        buffer[31] = (byte) (0xFF & (yPixelsPerMeter >> 24));
        // biClrUsed
        // The number of color indexes in the color table that are actually used by the bitmap.
        // If this value is zero, the bitmap uses the maximum number of colors
        // corresponding to the value of the biBitCount member
        // for the compression mode specified by biCompression.
        // If biClrUsed is nonzero and the biBitCount member is less than 16, the biClrUsed member specifies the actual number of colors the graphics engine or device driver accesses. If biBitCount is 16 or greater, the biClrUsed member specifies the size of the color table used to optimize performance of the system color palettes. If biBitCount equals 16 or 32, the optimal color palette starts immediately following the three DWORD masks.
        // When the bitmap array immediately follows the BITMAP_INFO structure, it is a packed bitmap.
        // Packed bitmaps are referenced by a single pointer.
        // Packed bitmaps require that the biClrUsed member must be either zero or the actual size of the color table.
        buffer[32] = (byte) (0xFF & paletteColorUsed);
        buffer[33] = (byte) (0xFF & (paletteColorUsed >> 8));
        buffer[34] = (byte) (0xFF & (paletteColorUsed >> 16));
        buffer[35] = (byte) (0xFF & (paletteColorUsed >> 24));
        // biClrImportant
        // The number of color indexes that are required for displaying the bitmap.
        // If this value is zero, all colors are required.
        buffer[36] = (byte) (0xFF & paletteColorImportant);
        buffer[37] = (byte) (0xFF & (paletteColorImportant >> 8));
        buffer[38] = (byte) (0xFF & (paletteColorImportant >> 16));
        buffer[39] = (byte) (0xFF & (paletteColorImportant >> 24));
        return buffer;
    }

    private static @NonNull byte[] RGB_QUAD (@NonNull int[] colors) {
        byte[] buffer = new byte[colors.length * 4];
        for (int i = 0; i < colors.length; i ++) {
            // rgbBlue
            // The intensity of blue in the color.
            buffer[i * 4] = (byte) (0xFF & Color.blue(colors[i]));
            // rgbGreen
            // The intensity of green in the color.
            buffer[i * 4 + 1] = (byte) (0xFF & Color.green(colors[i]));
            // rgbRed
            // The intensity of red in the color.
            buffer[i * 4 + 2] = (byte) (0xFF & Color.red(colors[i]));
            // rgbReserved
            // This member is reserved and must be zero.
            buffer[i * 4 + 3] = 0;
        }
        return buffer;
    }

    private static @NonNull byte[] DIB_DATA (@NonNull Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] buffer;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int offset = 0;
        if (bitmap.getConfig() != Bitmap.Config.RGB_565) {
            buffer = new byte[width * height * 4];
            for (int i = pixels.length - 1; i >= 0; i -= width) {
                for (int j = i - width + 1; j <= i; j ++) {
                    buffer[offset] = (byte) (Color.blue(pixels[j]));
                    buffer[offset + 1] = (byte) (Color.green(pixels[j]));
                    buffer[offset + 2] = (byte) (Color.red(pixels[j]));
                    buffer[offset + 3] = (byte) (Color.alpha(pixels[j]));
                    offset += 4;
                }
            }
        }
        else {
            buffer = new byte[width * height * 3];
            for (int i = pixels.length - 1; i >= 0; i -= width) {
                for (int j = i - width + 1; j <= i; j ++) {
                    buffer[offset] = (byte) (Color.blue(pixels[j]));
                    buffer[offset + 1] = (byte) (Color.green(pixels[j]));
                    buffer[offset + 2] = (byte) (Color.red(pixels[j]));
                    offset += 3;
                }
            }
        }
        return buffer;
    }

    public static boolean compress (@NonNull Bitmap bitmap, @NonNull OutputStream stream) {
        byte[] DIB_DATA = DIB_DATA(bitmap);
        int IMAGE_SIZE = DIB_DATA.length;
        byte[] BITMAP_HEADER = BITMAP_HEADER(IMAGE_SIZE, 0);
        byte BIT_COUNT;
        if (bitmap.getConfig() != Bitmap.Config.RGB_565) {
            BIT_COUNT = BitCount.BIT_COUNT_32;
        }
        else {
            BIT_COUNT = BitCount.BIT_COUNT_24;
        }
        byte[] BITMAP_INFO = BITMAP_INFO(
                bitmap.getWidth(),
                bitmap.getHeight(),
                BIT_COUNT,
                CompressFormat.BI_RGB,
                IMAGE_SIZE,
                96,
                96,
                PALETTE_COLOR_ALL_USED,
                PALETTE_COLOR_ALL_IMPORTANT
                );
        byte[] buffer = new byte[BITMAP_HEADER_SIZE + BITMAP_INFO_SIZE + IMAGE_SIZE];
        System.arraycopy(BITMAP_HEADER, 0, buffer, 0, BITMAP_HEADER_SIZE);
        System.arraycopy(BITMAP_INFO, 0, buffer, BITMAP_HEADER_SIZE, BITMAP_INFO_SIZE);
        System.arraycopy(DIB_DATA, 0, buffer, BITMAP_HEADER_SIZE + BITMAP_INFO_SIZE,
                IMAGE_SIZE);
        try {
            stream.write(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static @Nullable int[] get32BitPixels (@NonNull byte[] data) {
        int[] pixels = null;
        if (getBiCount(data) == 32 && getBiCompression(data) == CompressFormat.BI_RGB) {
            byte[] DIB_DATA = DIB_DATA(data);
            int width = getBiWidth(data);
            int height = getBiHeight(data);
            pixels = new int[width * Math.abs(height)];
            int y;
            int index;
            if (height >= 0) {
                for (int i = 0; i < height; i ++) {
                    y = height - i - 1;
                    for (int x = 0; x < width; x ++) {
                        index = i * width * 4 + x * 4;
                        pixels[y * width + x] = Color.argb(
                                DIB_DATA[index + 2] & 0xFF,
                                DIB_DATA[index + 2] & 0xFF,
                                DIB_DATA[index + 1] & 0xFF,
                                DIB_DATA[index] & 0xFF
                        );
                    }
                }
            }
            else {
                height = Math.abs(height);
                for (int i = 0; i < height; i ++) {
                    y = i;
                    for (int x = 0; x < width; x ++) {
                        index = i * width * 4 + x * 4;
                        pixels[y * width + x] = Color.argb(
                                DIB_DATA[index + 3] & 0xFF,
                                DIB_DATA[index + 2] & 0xFF,
                                DIB_DATA[index + 1] & 0xFF,
                                DIB_DATA[index] & 0xFF
                        );
                    }
                }
            }
            DIB_DATA = null;
        }
        return pixels;
    }

    public static @Nullable Bitmap decodeStream (@NonNull InputStream stream) {
        return decodeStream(stream, false);
    }

    public static @Nullable Bitmap decodeStream (@NonNull InputStream stream, boolean tryReadAlpha) {
        Bitmap bitmap = null;
        try {
            if (tryReadAlpha) {
                int length = stream.available();
                if (stream.markSupported()) {
                    stream.mark(length);
                }
                byte[] data = new byte[length];
                int count = stream.read(data, 0, length);
                while (count < length) {
                    count += stream.read(data, count, length - count);
                }
                int[] pixels = get32BitPixels(data);
                if (pixels == null) {
                    if (stream.markSupported()) {
                        stream.reset();
                        bitmap = BitmapFactory.decodeStream(stream);
                    }
                    else {
                        bitmap = BitmapFactory.decodeByteArray(data, 0, length);
                    }
                }
                else {
                    bitmap = Bitmap.createBitmap(pixels, getBiWidth(data), Math.abs(getBiHeight(data)), Bitmap.Config.ARGB_8888);
                }
            }
            else {
                bitmap = BitmapFactory.decodeStream(stream);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static @Nullable Bitmap decodeFile (@NonNull File file) {
        return decodeFile(file, true);
    }

    public static @Nullable Bitmap decodeFile (@NonNull File file, boolean tryReadAlpha) {
        if (!file.exists()) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        Bitmap bitmap = null;
        FileInputStream stream;
        try {
            int[] pixels = null;
            byte[] data = null;
            if (tryReadAlpha) {
                stream = new FileInputStream(file);
                int length = stream.available();
                data = new byte[length];
                int count = stream.read(data, 0, length);
                while (count < length) {
                    count += stream.read(data, count, length - count);
                }
                stream.close();
                pixels = get32BitPixels(data);
            }
            if (pixels == null) {
                stream = new FileInputStream(file);
                bitmap = BitmapFactory.decodeFileDescriptor(stream.getFD());
                stream.close();
            }
            else {
                bitmap = Bitmap.createBitmap(pixels, getBiWidth(data), getBiHeight(data), Bitmap.Config.ARGB_8888);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static @Nullable Bitmap decodeFile (@NonNull String path) {
        return decodeFile(path, true);
    }

    public static @Nullable Bitmap decodeFile (@NonNull String path, boolean tryReadAlpha) {
        return decodeFile(new File(path), tryReadAlpha);
    }

    public static @Nullable Bitmap decodeByteArray (@NonNull byte[] data, int offset, int length) {
        return decodeByteArray(data, offset, length, true);
    }

    public static @Nullable Bitmap decodeByteArray (@NonNull byte[] data, int offset, int length, boolean tryReadAlpha) {
        byte[] buffer = new byte[length];
        System.arraycopy(data, offset, buffer, 0, length);
        int[] pixels = null;
        if (tryReadAlpha) {
            pixels = get32BitPixels(buffer);
        }
        if (pixels == null) {
            return BitmapFactory.decodeByteArray(data, offset, length);
        }
        else {
            return Bitmap.createBitmap(pixels, getBiWidth(buffer), getBiHeight(buffer), Bitmap.Config.ARGB_8888);
        }
    }

}
