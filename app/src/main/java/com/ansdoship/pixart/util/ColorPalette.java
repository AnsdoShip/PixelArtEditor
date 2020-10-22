package com.ansdoship.pixart.util;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColorPalette implements Serializable {

    private List<Integer> mColors;

    private ColorPalette (int[] colors) {
        mColors = new ArrayList<>(colors.length);
        for (int color : colors) {
            mColors.add(color);
        }
    }

    private ColorPalette (int size, int color) {
        mColors = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mColors.add(i, color);
        }
    }

    public static ColorPalette createColorPalette (int size) {
        return createColorPalette (size, 0);
    }

    public static ColorPalette createColorPalette (int size, int color) {
        if (size < 1) {
            return null;
        }
        return new ColorPalette (size, color);
    }

    public static ColorPalette createColorPalette (@NonNull ColorPalette src) {
        int[] colors = new int[src.size()];
        for (int i = 0; i < src.size(); i ++) {
            colors[i] = src.get(i);
        }
        return createColorPalette(colors);
    }

    public static ColorPalette createColorPalette (@NonNull ColorPalette src, int size) {
        int[] colors = new int[size];
        for (int i = 0; i < Math.min(size, src.size()); i ++) {
            colors[i] = src.get(i);
        }
        return createColorPalette(colors);
    }

    public static ColorPalette createColorPalette (int[] colors) {
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
        if (file.exists() && pathAndName.toLowerCase().endsWith(".palette")) {
            try {
                ObjectInputStream ObjectIS = new ObjectInputStream(new FileInputStream(file));
                if (!(ObjectIS.readObject() instanceof ColorPalette)) {
                    return null;
                }
                return (ColorPalette) ObjectIS.readObject();
            }
            catch (IOException | ClassNotFoundException e) {
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
                ObjectOutputStream ObjectOS = new ObjectOutputStream(new FileOutputStream(file));
                ObjectOS.writeObject(this);
                ObjectOS.close();
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
                    ObjectOutputStream ObjectOS = new ObjectOutputStream(new FileOutputStream(file));
                    ObjectOS.writeObject(this);
                    ObjectOS.close();
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

}
