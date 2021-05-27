package com.ansdoship.pixelarteditor.editor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.json.JSONCodec;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class ColorPaletteCodec {

    public interface Callback {
        void onCreateFile(boolean isSuccess);
        void onException(Exception e);
        void onFileExists(boolean isDirectory);
    }

    public @Nullable static ColorPalette decodeFile(@NonNull String pathname) {
        return decodeFile(new File(pathname));
    }

    public @Nullable static ColorPalette decodeFile (@NonNull File file) {
        if (file.exists() && file.canRead()) {
            try {
                JSONObject jsonObject = JSONCodec.decodeFile(file);
                return ColorPalette.createColorPalette(JSONColor2IntArray(jsonObject.getString("colors")),
                        jsonObject.getInt("index"));
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void encodeFile (@NonNull ColorPalette palette, @NonNull File file, boolean override,
                                   @NonNull Callback callback) {
        if((!file.exists()) || (override && file.isFile())) {
            try {
                callback.onCreateFile(file.createNewFile());
                JSONCodec.encodeFile(colorPalette2JSON(palette), file);
            }
            catch (IOException | JSONException e) {
                callback.onException(e);
            }
        }
        else {
            callback.onFileExists(file.isDirectory());
        }
    }

    public static void encodeFile (@NonNull ColorPalette palette, @NonNull String pathname, boolean override,
                                   @NonNull Callback callback) {
        encodeFile(palette, new File(pathname), override, callback);
    }

    public static boolean encodeFile (@NonNull ColorPalette palette, @NonNull File file, boolean override) {
        final boolean[] result = new boolean[1];
        encodeFile(palette, file, override, new Callback() {
            @Override
            public void onCreateFile(boolean isSuccess) {
                result[0] = isSuccess;
            }
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                result[0] = false;
            }
            @Override
            public void onFileExists(boolean isDirectory) {
                result[0] = false;
            }
        });
        return result[0];
    }

    public static boolean encodeFile (@NonNull ColorPalette palette, @NonNull String pathname, boolean override) {
        return encodeFile(palette, new File(pathname), override);
    }

    private static @NonNull JSONObject colorPalette2JSON (@NonNull ColorPalette palette) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("index", palette.getIndex());
        jsonObject.put("colors", Arrays.toString(palette.getColors()));
        return jsonObject;
    }

    private static @NonNull int[] JSONColor2IntArray(@NonNull String colorStringArray) {
        String[] stringArray = colorStringArray.substring(1, colorStringArray.length() - 1).split(",");
        int[] colorArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i ++) {
            colorArray[i] = Integer.parseInt(stringArray[i]);
        }
        return colorArray;
    }

}
