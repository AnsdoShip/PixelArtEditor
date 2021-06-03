package com.ansdoship.pixelarteditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.util.ApplicationUtils;
import com.ansdoship.pixelarteditor.editor.palette.Palette;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFactory;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFlag;
import com.ansdoship.pixelarteditor.editor.ToolFlag;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapDecoder;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapEncoder;
import com.ansdoship.pixelarteditor.util.BitmapPool;
import com.ansdoship.pixelarteditor.util.ToolBufferPool;
import com.ansdoship.pixelarteditor.util.Utils;

import java.io.IOException;

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
        preferences = ApplicationUtils.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    
    public static Settings getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    public final static String PREFERENCES_NAME = "settings";
    
    public final static String KEY_IMAGE_NAME = "image_name";
    private String imageName;
    public final static String KEY_IMAGE_PATH = "image_path";
    private String imagePath;
    public final static String KEY_IMAGE_SCALE = "image_scale";
    private int imageScale;
    public final static String KEY_IMAGE_TRANSLATION_X = "image_translation_x";
    private int imageTranslationX;
    public final static String KEY_IMAGE_TRANSLATION_Y = "image_translation_y";
    private int imageTranslationY;
    public final static String KEY_IMAGE_ORIGIN_X = "image_origin_x";
    private int imageOriginX;
    public final static String KEY_IMAGE_ORIGIN_Y = "image_origin_y";
    private int imageOriginY;
    
    public final static String KEY_TOOL_FLAG = "tool_flag";
    private int toolFlag;
    public final static String KEY_SHAPE_FLAG = "shape_flag";
    private int shapeFlag;
    public final static String KEY_PAINT_FLAG = "paint_flag";
    private int paintFlag;
    public final static String KEY_SELECTION_FLAG = "selection_flag";
    private int selectionFlag;
    public final static String KEY_PALETTE_FLAG = "palette_flag";
    private int paletteFlag;

    public final static String KEY_EXTERNAL_PALETTE_NAME = "external_palette_name";
    private String externalPaletteName;
    
    public final static String KEY_PAINT_WIDTH = "paint_width";
    private int paintWidth;
    
    public final static String KEY_GRID_VISIBLE = "grid_visible";
    private boolean gridVisible;
    public final static String KEY_GRID_WIDTH = "grid_width";
    private int gridWidth;
    public final static String KEY_GRID_HEIGHT = "grid_height";
    private int gridHeight;
    public final static String KEY_BACKGROUND_PALETTE = "background_palette";
    private Palette backgroundPalette;
    public final static String KEY_GRID_PALETTE = "grid_palette";
    private Palette gridPalette;
    public final static String KEY_BUILTIN_PALETTE = "builtin_palette";
    private Palette builtinPalette;

    private Palette externalPalette;

    private ToolBufferPool toolBufferPool;
    private BitmapPool bitmapPool;

    private static final String KEY_CACHE_BITMAP = "cache_bitmap";
    private static final String KEY_CURRENT_BITMAP = "current_bitmap";
    private static final String KEY_CANVAS_BACKGROUND_BITMAP = "canvas_background_bitmap";
    private static final String KEY_SELECTED_BITMAP = "selected_bitmap";

    private static final String KEY_SCALE_MODE = "scale_mode";
    private boolean scaleMode;

    public void loadData() {
        setImageName(preferences.getString(KEY_IMAGE_NAME, IMAGE_NAME_DEFAULT()));
        setImagePath(preferences.getString(KEY_IMAGE_PATH, IMAGE_PATH_DEFAULT()));
        setImageScale(preferences.getInt(KEY_IMAGE_SCALE, IMAGE_SCALE_DEFAULT));
        setImageTranslationX(preferences.getInt(KEY_IMAGE_TRANSLATION_X, IMAGE_TRANSLATION_X_DEFAULT));
        setImageTranslationY(preferences.getInt(KEY_IMAGE_TRANSLATION_Y, IMAGE_TRANSLATION_Y_DEFAULT));
        setImageOriginX(preferences.getInt(KEY_IMAGE_ORIGIN_X, IMAGE_ORIGIN_X_DEFAULT));
        setImageOriginY(preferences.getInt(KEY_IMAGE_ORIGIN_Y, IMAGE_ORIGIN_Y_DEFAULT));
        setToolFlag(preferences.getInt(KEY_TOOL_FLAG, TOOL_FLAG_DEFAULT));
        setShapeFlag(preferences.getInt(KEY_SHAPE_FLAG, SHAPE_FLAG_DEFAULT));
        setPaintFlag(preferences.getInt(KEY_PAINT_FLAG, PAINT_FLAG_DEFAULT));
        setSelectionFlag(preferences.getInt(KEY_SELECTION_FLAG, SELECTION_FLAG_DEFAULT));
        setPaletteFlag(preferences.getInt(KEY_PALETTE_FLAG, PALETTE_FLAG_DEFAULT));
        setExternalPaletteName(preferences.getString(KEY_EXTERNAL_PALETTE_NAME, KEY_EXTERNAL_PALETTE_NAME));
        setPaintWidth(preferences.getInt(KEY_PAINT_WIDTH, PAINT_WIDTH_DEFAULT));
        setGridVisible(preferences.getBoolean(KEY_GRID_VISIBLE, GRID_VISIBLE_DEFAULT));
        setGridWidth(preferences.getInt(KEY_GRID_WIDTH, GRID_WIDTH_DEFAULT));
        setGridHeight(preferences.getInt(KEY_GRID_HEIGHT, GRID_HEIGHT_DEFAULT));
        setScaleMode(preferences.getBoolean(KEY_SCALE_MODE, SCALE_MODE_DEFAULT));
        String backgroundPaletteString = preferences.getString(KEY_BACKGROUND_PALETTE, null);
        if (backgroundPaletteString == null) {
            backgroundPalette = Palette.createPalette(BACKGROUND_PALETTE_COLORS_DEFAULT);
        }
        else {
            backgroundPalette = PaletteFactory.decodeString(backgroundPaletteString);
            if (backgroundPalette == null) {
                backgroundPalette = Palette.createPalette(BACKGROUND_PALETTE_COLORS_DEFAULT);
            }
        }
        String gridPaletteString = preferences.getString(KEY_GRID_PALETTE, null);
        if (gridPaletteString == null) {
            gridPalette = Palette.createPalette(GRID_PALETTE_COLORS_DEFAULT);
        }
        else {
            gridPalette = PaletteFactory.decodeString(gridPaletteString);
            if (gridPalette == null) {
                gridPalette = Palette.createPalette(GRID_PALETTE_COLORS_DEFAULT);
            }
        }
        String builtinPaletteString = preferences.getString(KEY_BUILTIN_PALETTE, null);
        if (builtinPaletteString == null) {
            builtinPalette = Palette.createPalette(BUILTIN_PALETTE_COLORS_DEFAULT);
        }
        else {
            builtinPalette = PaletteFactory.decodeString(builtinPaletteString);
            if (builtinPalette == null) {
                builtinPalette = Palette.createPalette(BUILTIN_PALETTE_COLORS_DEFAULT);
            }
        }

        Bitmap cacheBitmap = null;
        String cacheBitmapPathName = getCacheBitmapPathname();
        if (cacheBitmapPathName != null) {
            cacheBitmap = BitmapDecoder.decodeFile(cacheBitmapPathName);
        }
        if(cacheBitmap == null) {
            cacheBitmap = Bitmap.createBitmap(IMAGE_WIDTH_DEFAULT,
                    IMAGE_HEIGHT_DEFAULT, Bitmap.Config.ARGB_8888);
        }

        if (toolBufferPool == null) {
            toolBufferPool = ToolBufferPool.createToolBufferPool(cacheBitmap,
                    MAX_BUFFER_SIZE_DEFAULT, false);
        }

        bitmapPool = new BitmapPool();
        bitmapPool.addBitmap(KEY_CACHE_BITMAP, toolBufferPool.getCacheBitmap());
        bitmapPool.addBitmap(KEY_CURRENT_BITMAP, toolBufferPool.getCurrentBitmap());
    }

    public void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString(KEY_IMAGE_NAME, imageName);
        editor.putString(KEY_IMAGE_PATH, imagePath);
        editor.putInt(KEY_IMAGE_SCALE, imageScale);
        editor.putInt(KEY_IMAGE_TRANSLATION_X, imageTranslationX);
        editor.putInt(KEY_IMAGE_TRANSLATION_Y, imageTranslationY);
        editor.putInt(KEY_IMAGE_ORIGIN_X, imageOriginX);
        editor.putInt(KEY_IMAGE_ORIGIN_Y, imageOriginY);
        editor.putInt(KEY_TOOL_FLAG, toolFlag);
        editor.putInt(KEY_SHAPE_FLAG, shapeFlag);
        editor.putInt(KEY_PAINT_FLAG, paintFlag);
        editor.putInt(KEY_SELECTION_FLAG, selectionFlag);
        editor.putInt(KEY_PALETTE_FLAG, paletteFlag);
        editor.putString(KEY_EXTERNAL_PALETTE_NAME, externalPaletteName);
        editor.putInt(KEY_PAINT_WIDTH, paintWidth);
        editor.putBoolean(KEY_GRID_VISIBLE, gridVisible);
        editor.putInt(KEY_GRID_WIDTH, gridWidth);
        editor.putInt(KEY_GRID_HEIGHT, gridHeight);
        editor.putString(KEY_BACKGROUND_PALETTE, PaletteFactory.encodeString(backgroundPalette));
        editor.putString(KEY_GRID_PALETTE, PaletteFactory.encodeString(gridPalette));
        editor.putString(KEY_BUILTIN_PALETTE, PaletteFactory.encodeString(builtinPalette));
        editor.putBoolean(KEY_SCALE_MODE, scaleMode);
        editor.apply();

        String cacheBitmapPathname = getCacheBitmapPathname();
        if (cacheBitmapPathname != null) {
            BitmapEncoder.encodeFile(cacheBitmapPathname,
                    toolBufferPool.getCurrentBitmap(), true, BitmapEncoder.CompressFormat.PNG, 100,
                    new BitmapEncoder.Callback() {
                        @Override
                        public void onCreateFailure() {}
                        @Override
                        public void onCompressFailure() {}
                        @Override
                        public void onFileExists(boolean isDirectory) {}
                        @Override
                        public void onIOException(IOException e) {}
                    });
        }

    }
    
    public static String IMAGE_NAME_DEFAULT() {
    	return ApplicationUtils.getApplicationContext().getString(R.string.image_name_default);
    }
    public static String IMAGE_PATH_DEFAULT() {
        return Utils.getFilesPath("images");
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

    public final static String EXTERNAL_PALETTE_DEFAULT = null;
    
    public final static int PAINT_WIDTH_DEFAULT = 1;
    
    public final static boolean GRID_VISIBLE_DEFAULT = false;
    public final static int GRID_WIDTH_DEFAULT = 1;
    public final static int GRID_HEIGHT_DEFAULT = 1;

    public final static int[] BACKGROUND_PALETTE_COLORS_DEFAULT = new int[] {
            Color.DKGRAY, Color.LTGRAY, Color.GRAY
    };
    public final static int[] GRID_PALETTE_COLORS_DEFAULT = new int[] {Color.BLACK};
    public final static int[] BUILTIN_PALETTE_COLORS_DEFAULT = new int[] {
            Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
            Color.WHITE, Color.LTGRAY, Color.GRAY, Color.DKGRAY, Color.BLACK, Color.TRANSPARENT
    };

    public final static int MAX_BUFFER_SIZE_DEFAULT = 20;

    public final static boolean SCALE_MODE_DEFAULT = false;
    
    private final SharedPreferences preferences;

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImageScale(int imageScale) {
        this.imageScale = imageScale;
    }

    public int getImageScale() {
        return imageScale;
    }

    public void setImageTranslationX(int imageTranslationX) {
        this.imageTranslationX = imageTranslationX;
    }

    public int getImageTranslationX() {
        return imageTranslationX;
    }

    public void setImageTranslationY(int imageTranslationY) {
        this.imageTranslationY = imageTranslationY;
    }

    public int getImageTranslationY() {
        return imageTranslationY;
    }

    public void setImageOriginX(int imageOriginX) {
        this.imageOriginX = imageOriginX;
    }

    public int getImageOriginX() {
        return imageOriginX;
    }

    public void setImageOriginY(int imageOriginY) {
        this.imageOriginY = imageOriginY;
    }

    public int getImageOriginY() {
        return imageOriginY;
    }

    public void setToolFlag(int toolFlag) {
        this.toolFlag = toolFlag;
    }

    public int getToolFlag() {
        return toolFlag;
    }

    public void setShapeFlag(int shapeFlag) {
        this.shapeFlag = shapeFlag;
    }

    public int getShapeFlag() {
        return shapeFlag;
    }

    public void setPaintFlag(int paintFlag) {
        this.paintFlag = paintFlag;
    }

    public int getPaintFlag() {
        return paintFlag;
    }

    public void setSelectionFlag(int selectionFlag) {
        this.selectionFlag = selectionFlag;
    }

    public int getSelectionFlag() {
        return selectionFlag;
    }

    public void setPaletteFlag(int paletteFlag) {
        this.paletteFlag = paletteFlag;
    }

    public int getPaletteFlag() {
        return paletteFlag;
    }

    public void setExternalPaletteName(String externalPaletteName) {
        this.externalPaletteName = externalPaletteName;
    }

    @Nullable
    public String getExternalPaletteName() {
        return externalPaletteName;
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

    public void setPaintWidth(int paintWidth) {
        this.paintWidth = paintWidth;
    }

    public int getPaintWidth() {
        return paintWidth;
    }

    public void setGridVisible(boolean gridVisible) {
        this.gridVisible = gridVisible;
    }

    public boolean isGridVisible() {
        return gridVisible;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public Palette getBackgroundPalette() {
        return backgroundPalette;
    }

    public Palette getGridPalette() {
        return gridPalette;
    }

    public Palette getBuiltinPalette() {
        return builtinPalette;
    }

    public void loadExternalPalette(String name) {
        externalPalette = PaletteFactory.decodeFile(getPalettesPath() + "/" + name + ".palette");
    }

    public @Nullable
    Palette getExternalPalette() {
        return externalPalette;
    }

    public static String getPalettesPath() {
        return Utils.getFilesPath("palettes");
    }

    @NonNull
    public static String getCacheBitmapName() {
        return "CACHE.png";
    }

    @Nullable
    public static String getCacheBitmapPathname() {
        String cachePath = Utils.getCachePath();
        if (cachePath == null) {
            return null;
        }
        return cachePath + "/" + getCacheBitmapName();
    }

    public ToolBufferPool getToolBufferPool() {
        return toolBufferPool;
    }

    public BitmapPool getBitmapPool() {
        return bitmapPool;
    }

    public void setScaleMode(boolean scaleMode) {
        this.scaleMode = scaleMode;
    }

    public boolean isScaleMode() {
        return scaleMode;
    }

}
