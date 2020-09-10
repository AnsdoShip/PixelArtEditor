package com.ansdoship.ansdopix.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorPalette {

    private List<Integer> mColors;

    private ColorPalette (int[] colors) {
        mColors = new ArrayList<>(colors.length);
        for (int color : colors) {
            mColors.add(color);
        }
    }

    private ColorPalette (int size, int color) {
        mColors = new ArrayList<>(size);
        for (int i = 0; i < mColors.size(); i++) {
            mColors.set(i, color);
        }
    }

    public ColorPalette createColorPalette (int size) {
        return createColorPalette (size, 0);
    }

    public ColorPalette createColorPalette (int size, int color) {
        if (size < 1) {
            return null;
        }
        return new ColorPalette (size, color);
    }

    public ColorPalette createColorPalette (int[] colors) {
        if (colors.length < 1) {
            return null;
        }
        else {
            return new ColorPalette (colors);
        }
    }

    public int size () {
        return mColors.size();
    }

    public int get (int index) {
        return mColors.get(index);
    }

    public void set (int index, int color) {
        mColors.set(index, color);
    }

    public void setAll (int color) {
        for (int i = 0; i < mColors.size(); i++) {
            mColors.set(i, color);
        }
    }

    public void clear () {
        for (int i = 0; i < mColors.size(); i++) {
            mColors.set(i, 0);
        }
    }

    public static ColorPalette decodeFile (String pathAndName) {
        File file = new File(pathAndName);
        if (file.exists()) {
            try {
                if (file.length() > Integer.MAX_VALUE) {
                    return null;
                }
                int size = (int) file.length();
                FileInputStream fileIS = new FileInputStream(file);
                byte[] data = new byte[size];
                String encoding = "UTF-8";
                String string = new String(data, 3, fileIS.read(data) - 3, encoding);
                JSONArray array = new JSONObject(string).getJSONArray("colors");
                int[] colors = new int[array.length()];
                for (int i = 0; i < array.length(); i ++) {
                    Arrays.fill(colors, i, i + 1, array.getInt(i));
                }
                return new ColorPalette(colors);
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public boolean saveToFile (String pathAndName, boolean override) {
        boolean result = false;
        File file = new File(pathAndName + ".palette");
        if(!file.exists()) {
            try
            {
                result = file.createNewFile();
                FileOutputStream fileOS = new FileOutputStream(file);
                byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
                String encoding = "UTF-8";
                fileOS.write(bom);
                fileOS.write(compressToJSONObject().toString(2).getBytes(encoding));
                fileOS.flush();
                fileOS.close();
            }
            catch (IOException | JSONException e)
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
                    byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
                    String encoding = "UTF-8";
                    fileOS.write(bom);
                    fileOS.write(compressToJSONObject().toString(2).getBytes(encoding));
                    fileOS.flush();
                    fileOS.close();
                }
                catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }

    private JSONObject compressToJSONObject () {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (int color : mColors) {
            array.put(color);
        }
        try {
            object.put("colors", array);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

}
