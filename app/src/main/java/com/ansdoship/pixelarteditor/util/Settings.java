package com.ansdoship.pixelarteditor.util;

import android.content.Context;

import com.ansdoship.pixelarteditor.R;
import com.tencent.mmkv.MMKV;

public final class Settings {
	
    private enum Singleton {
        INSTANCE;
        private final Settings instance;
        Singleton() {
            instance = new Settings();
        }
        public Settings getInstance() {
            return instance;
        }
    }
    
    private Settings() {

        String rootDir = MMKV.initialize(ApplicationUtils.getApplicationContext(),
                ApplicationUtils.getApplicationContext().getDir("data",
                        Context.MODE_PRIVATE).getAbsolutePath());
        preferences = MMKV.mmkvWithID(PREFERENCES_FILE_NAME, MMKV.MULTI_PROCESS_MODE);

    }
    
    public static Settings getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    public final static String PREFERENCES_FILE_NAME = "app_data";
    
    public final static String KEY_IMAGE_NAME = "image_name";
    public final static String KEY_IMAGE_PATH = "image_path";
    public final static String KEY_IMAGE_SCALE = "image_scale";
    public final static String KEY_IMAGE_TRANSLATION_X = "image_translation_x";
    public final static String KEY_IMAGE_TRANSLATION_Y = "image_translation_y";
    public final static String KEY_IMAGE_ORIGIN_X = "image_origin_x";
    public final static String KEY_IMAGE_ORIGIN_Y = "image_origin_y";
    
    public final static String KEY_TOOL_FLAG = "tool_flag";
    public final static String KEY_SHAPE_FLAG = "shape_flag";
    public final static String KEY_PAINT_FLAG = "paint_flag";
    public final static String KEY_PALETTE_FLAG = "palette_flag";
    
    public final static String KEY_PAINT_WIDTH = "paint_width";
    
    public final static String KEY_GRID_VISIBILITY = "grid_visibility";
    public final static String KEY_GRID_WIDTH = "grid_width";
    public final static String KEY_GRID_HEIGHT = "grid_height";
    
    public static String IMAGE_NAME_DEFAULT() {
    	return ApplicationUtils.getResources().getString(R.string.image_name_default);
    }
    public final static float IMAGE_SCALE_DEFAULT = 1.0f;
    public final static int IMAGE_TRANSLATION_X_DEFAULT = 0;
    public final static int IMAGE_TRANSLATION_Y_DEFAULT = 0;
    public final static int IMAGE_ORIGIN_X_DEFAULT = 0;
    public final static int IMAGE_ORIGIN_Y_DEFAULT = 0;
    
    public final static int TOOL_FLAG_DEFAULT = Flags.ToolFlag.PAINT;
    public final static int SHAPE_FLAG_DEFAULT = Flags.ShapeFlag.LINE;
    public final static int PAINT_FLAG_DEFAULT = Flags.PaintFlag.REPLACE;
    public final static int PALETTE_FLAG_DEFAULT = Flags.PaletteFlag.INTERNAL;
    
    public final static int PAINT_WIDTH_DEFAULT = 1;
    
    public final static boolean GRID_VISIBILITY_DEFAULT = false;
    public final static int GRID_WIDTH_DEFAULT = 1;
    public final static int GRID_HEIGHT_DEFAULT = 1;
    
    private final MMKV preferences;
    
    public MMKV getPreferences() {
    	return preferences;
    }
    
    public void putImageName(String imageName) {
		putString(KEY_IMAGE_NAME, imageName);
	}
    
    public String getImageName() {
		return getString(KEY_IMAGE_NAME, IMAGE_NAME_DEFAULT());
	}
    
    public void putImagePath(String imagePath) {
    	putString(KEY_IMAGE_PATH, imagePath);
	}
    
    public String getImagePath() {
		return getString(KEY_IMAGE_PATH, null);
	}
    
    public void putImageScale(float imageScale) {
		putFloat(KEY_IMAGE_SCALE, imageScale);
	}
    
    public float getImageScale() {
		return getFloat(KEY_IMAGE_SCALE, IMAGE_SCALE_DEFAULT);
	}
    
    public void putImageTranslationX(int imageTranslationX) {
		putInteger(KEY_IMAGE_TRANSLATION_X, imageTranslationX);
	}
    
    public int getImageTranslationX() {
		return getInteger(KEY_IMAGE_TRANSLATION_X, IMAGE_TRANSLATION_X_DEFAULT);
	}
    
    public void putImageTranslationY(int imageTranslationY) {
		putInteger(KEY_IMAGE_TRANSLATION_Y, imageTranslationY);
	}
    
    public int getImageTranslationY() {
		return getInteger(KEY_IMAGE_TRANSLATION_Y, IMAGE_TRANSLATION_Y_DEFAULT);
	}
    
    public void putImageOriginX(int imageOriginX) {
		putInteger(KEY_IMAGE_ORIGIN_X, imageOriginX);
	}
    
    public int getImageOriginX() {
		return getInteger(KEY_IMAGE_ORIGIN_X, IMAGE_ORIGIN_X_DEFAULT);
	}
    
    public void putImageOriginY(int imageOriginY) {
		putInteger(KEY_IMAGE_ORIGIN_Y, imageOriginY);
	}
    
    public int getImageOriginY() {
		return getInteger(KEY_IMAGE_ORIGIN_Y, IMAGE_ORIGIN_Y_DEFAULT);
	}
    
    public void putToolFlag(int toolFlag) {
		putInteger(KEY_TOOL_FLAG, toolFlag);
	}
    
    public int getToolFlag() {
		return getInteger(KEY_TOOL_FLAG, TOOL_FLAG_DEFAULT);
	}
    
    public void putShapeFlag(int shapeFlag) {
		putInteger(KEY_SHAPE_FLAG, shapeFlag);
	}
    
    public int getShapeFlag() {
		return getInteger(KEY_SHAPE_FLAG, SHAPE_FLAG_DEFAULT);
	}
    
    public void putPaintFlag(int paintFlag) {
		putInteger(KEY_PAINT_FLAG, paintFlag);
	}
    
    public int getPaintFlag() {
		return getInteger(KEY_PAINT_FLAG, PAINT_FLAG_DEFAULT);
	}
    
    public void putPaletteFlag(int paletteFlag) {
		putInteger(KEY_PALETTE_FLAG, paletteFlag);
	}
    
    public int getPaletteFlag() {
		return getInteger(KEY_PALETTE_FLAG, PALETTE_FLAG_DEFAULT);
	}
    
    public void putPaintWidth(int paintWidth) {
		putInteger(KEY_PAINT_WIDTH, paintWidth);
	}
    
    public int getPaintWidth() {
		return getInteger(KEY_PAINT_WIDTH, PAINT_WIDTH_DEFAULT);
	}
    
    public void putGridVisibility(boolean gridVisibility) {
		putBoolean(KEY_GRID_VISIBILITY, gridVisibility);
	}
    
    public boolean getGridVisibility() {
		return getBoolean(KEY_GRID_VISIBILITY, GRID_VISIBILITY_DEFAULT);
	}
    
    public void putGridWidth(int gridWidth) {
		putInteger(KEY_GRID_WIDTH, gridWidth);
	}
    
    public int getGridWidth() {
		return getInteger(KEY_GRID_WIDTH, GRID_WIDTH_DEFAULT);
	}
    
    public void putGridHeight(int gridHeight) {
		putInteger(KEY_GRID_HEIGHT, gridHeight);
	}
    
    public int getGridHeight() {
		return getInteger(KEY_GRID_HEIGHT, GRID_HEIGHT_DEFAULT);
	}
    
    public void putInteger(String key, int value) {
    	getPreferences().putInt(key, value);
    }
    
    public int getInteger(String key, int defValue) {
    	return getPreferences().getInt(key, defValue);
    }
    
    public void putBoolean(String key, boolean value) {
    	getPreferences().putBoolean(key, value);
    }
    
    public boolean getBoolean(String key, boolean defValue) {
    	return getPreferences().getBoolean(key, defValue);
    }
    
    public void putString(String key, String value) {
    	getPreferences().putString(key, value);
    }
    
    public String getString(String key, String defValue) {
    	return getPreferences().getString(key, defValue);
    }
    
    public void putFloat(String key, float value) {
    	getPreferences().putFloat(key, value);
    }
    
    public float getFloat(String key,float defValue) {
    	return getPreferences().getFloat(key, defValue);
    }
    
}
