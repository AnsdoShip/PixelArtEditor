package com.ansdoship.pixelarteditor;

import android.content.Context;

import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.app.ApplicationUtils;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFlag;
import com.ansdoship.pixelarteditor.editor.ToolFlag;
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

        MMKV.initialize(ApplicationUtils.getApplicationContext(),
                ApplicationUtils.getApplicationContext().getDir("data", Context.MODE_PRIVATE).getAbsolutePath());
        preferences = MMKV.mmkvWithID(PREFERENCES_FILE_NAME, MMKV.MULTI_PROCESS_MODE);

    }
    
    public static Settings getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private final static String PREFERENCES_FILE_NAME = "app_data";
    
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
    public final static String KEY_SELECTION_FLAG = "selection_flag";
    public final static String KEY_PALETTE_FLAG = "palette_flag";

    public final static String KEY_EXTERNAL_PALETTE_NAME = "external_palette_name";
    
    public final static String KEY_PAINT_WIDTH = "paint_width";
    
    public final static String KEY_GRID_VISIBILITY = "grid_visibility";
    public final static String KEY_GRID_WIDTH = "grid_width";
    public final static String KEY_GRID_HEIGHT = "grid_height";
    
    public static String IMAGE_NAME_DEFAULT() {
    	return ApplicationUtils.getApplicationContext().getString(R.string.image_name_default);
    }
    public final static int IMAGE_SCALE_DEFAULT = 20;
    public final static int IMAGE_TRANSLATION_X_DEFAULT = 100;
    public final static int IMAGE_TRANSLATION_Y_DEFAULT = 100;
    public final static int IMAGE_ORIGIN_X_DEFAULT = 0;
    public final static int IMAGE_ORIGIN_Y_DEFAULT = 0;
    public final static int IMAGE_WIDTH_DEFAULT = 32;
    public final static int IMAGE_HEIGHT_DEFAULT = 32;
    
    public final static int TOOL_FLAG_DEFAULT = ToolFlag.PAINT;
    public final static int SHAPE_FLAG_DEFAULT = ToolFlag.ShapeFlag.LINE;
    public final static int PAINT_FLAG_DEFAULT = ToolFlag.PaintFlag.REPLACE;
    public final static int SELECTION_FLAG_DEFAULT = ToolFlag.SelectionFlag.NONE;
    public final static int PALETTE_FLAG_DEFAULT = PaletteFlag.INTERNAL;
    
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
    
    public String getImageDirectoryPath() {
		return getString(KEY_IMAGE_PATH, Utils.getFilesPath("images"));
	}
    
    public void putImageScale(int imageScale) {
		putInt(KEY_IMAGE_SCALE, imageScale);
	}
    
    public int getImageScale() {
		return getInt(KEY_IMAGE_SCALE, IMAGE_SCALE_DEFAULT);
	}
    
    public void putImageTranslationX(int imageTranslationX) {
		putInt(KEY_IMAGE_TRANSLATION_X, imageTranslationX);
	}
    
    public int getImageTranslationX() {
		return getInt(KEY_IMAGE_TRANSLATION_X, IMAGE_TRANSLATION_X_DEFAULT);
	}
    
    public void putImageTranslationY(int imageTranslationY) {
		putInt(KEY_IMAGE_TRANSLATION_Y, imageTranslationY);
	}
    
    public int getImageTranslationY() {
		return getInt(KEY_IMAGE_TRANSLATION_Y, IMAGE_TRANSLATION_Y_DEFAULT);
	}
    
    public void putImageOriginX(int imageOriginX) {
		putInt(KEY_IMAGE_ORIGIN_X, imageOriginX);
	}
    
    public int getImageOriginX() {
		return getInt(KEY_IMAGE_ORIGIN_X, IMAGE_ORIGIN_X_DEFAULT);
	}
    
    public void putImageOriginY(int imageOriginY) {
		putInt(KEY_IMAGE_ORIGIN_Y, imageOriginY);
	}
    
    public int getImageOriginY() {
		return getInt(KEY_IMAGE_ORIGIN_Y, IMAGE_ORIGIN_Y_DEFAULT);
	}
    
    public void putToolFlag(int toolFlag) {
		putInt(KEY_TOOL_FLAG, toolFlag);
	}
    
    public int getToolFlag() {
		return getInt(KEY_TOOL_FLAG, TOOL_FLAG_DEFAULT);
	}
    
    public void putShapeFlag(int shapeFlag) {
		putInt(KEY_SHAPE_FLAG, shapeFlag);
	}
    
    public int getShapeFlag() {
		return getInt(KEY_SHAPE_FLAG, SHAPE_FLAG_DEFAULT);
	}
    
    public void putPaintFlag(int paintFlag) {
		putInt(KEY_PAINT_FLAG, paintFlag);
	}
    
    public int getPaintFlag() {
		return getInt(KEY_PAINT_FLAG, PAINT_FLAG_DEFAULT);
	}

    public void putSelectionFlag(int selectionFlag) {
        putInt(KEY_SELECTION_FLAG, selectionFlag);
    }

    public int getSelectionFlag() {
        return getInt(KEY_SELECTION_FLAG, SELECTION_FLAG_DEFAULT);
    }
    
    public void putPaletteFlag(int paletteFlag) {
		putInt(KEY_PALETTE_FLAG, paletteFlag);
	}
    
    public int getPaletteFlag() {
		return getInt(KEY_PALETTE_FLAG, PALETTE_FLAG_DEFAULT);
	}

	public void putExternalPaletteName(String externalPaletteName) {
        putString(KEY_EXTERNAL_PALETTE_NAME, externalPaletteName);
    }

    public @Nullable String getExternalPaletteName() {
        return getString("palette_name", null);
    }

	public static String getBackgroundPaletteName() {
        return ApplicationUtils.getResources().getString(R.string.background_palette);
    }

    public static String getGridPaletteName() {
        return ApplicationUtils.getApplicationContext().getString(R.string.grid_palette);
    }

    public static String getBuiltinPaletteName() {
        return ApplicationUtils.getApplicationContext().getString(R.string.builtin_palette);
    }
    
    public void putPaintWidth(int paintWidth) {
		putInt(KEY_PAINT_WIDTH, paintWidth);
	}
    
    public int getPaintWidth() {
		return getInt(KEY_PAINT_WIDTH, PAINT_WIDTH_DEFAULT);
	}
    
    public void putGridVisibility(boolean gridVisibility) {
		putBoolean(KEY_GRID_VISIBILITY, gridVisibility);
	}
    
    public boolean getGridVisibility() {
		return getBoolean(KEY_GRID_VISIBILITY, GRID_VISIBILITY_DEFAULT);
	}
    
    public void putGridWidth(int gridWidth) {
		putInt(KEY_GRID_WIDTH, gridWidth);
	}
    
    public int getGridWidth() {
		return getInt(KEY_GRID_WIDTH, GRID_WIDTH_DEFAULT);
	}
    
    public void putGridHeight(int gridHeight) {
		putInt(KEY_GRID_HEIGHT, gridHeight);
	}
    
    public int getGridHeight() {
		return getInt(KEY_GRID_HEIGHT, GRID_HEIGHT_DEFAULT);
	}
    
    public void putInt(String key, int value) {
    	getPreferences().putInt(key, value);
    }
    
    public int getInt(String key, int defValue) {
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
