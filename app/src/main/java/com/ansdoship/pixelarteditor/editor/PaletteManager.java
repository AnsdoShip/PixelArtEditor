package com.ansdoship.pixelarteditor.editor;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.Utils;
import com.ansdoship.pixelarteditor.app.ApplicationUtils;
import com.tencent.mmkv.MMKV;

public final class PaletteManager {

    private enum Singleton {
        INSTANCE;
        private final PaletteManager instance;
        Singleton() {
            instance = new PaletteManager();
        }
        public PaletteManager getInstance() {
            return instance;
        }
    }

    private PaletteManager() {

        MMKV.initialize(ApplicationUtils.getApplicationContext(),
                ApplicationUtils.getApplicationContext().getDir("data", Context.MODE_PRIVATE).getAbsolutePath());
        preferences = MMKV.mmkvWithID(PREFERENCES_FILE_NAME, MMKV.MULTI_PROCESS_MODE);

        if (preferences == null) {
            backgroundPalette = ColorPalette.createColorPalette(new int[] {
                    Color.DKGRAY, Color.LTGRAY, Color.GRAY
            });
            gridPalette = ColorPalette.createColorPalette(new int[] {Color.BLACK});
            builtinPalette = ColorPalette.createColorPalette(new int[] {
                    Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
                    Color.WHITE, Color.LTGRAY, Color.GRAY, Color.DKGRAY, Color.BLACK, Color.TRANSPARENT
            });
        }
        else {
            backgroundPalette = ColorPalette.createColorPalette(new int[] {
                    preferences.getInt("canvas_view_background_color", Color.DKGRAY),
                    preferences.getInt("canvas_background_color_1", Color.LTGRAY),
                    preferences.getInt("canvas_background_color_2", Color.GRAY)
            });
            gridPalette = ColorPalette.createColorPalette(1, preferences.getInt("grid_color", Color.BLACK));
            builtinPalette = ColorPalette.createColorPalette(new int[] {
                    preferences.getInt("builtin_palette_color_1", Color.RED),
                    preferences.getInt("builtin_palette_color_2", Color.YELLOW),
                    preferences.getInt("builtin_palette_color_3", Color.GREEN),
                    preferences.getInt("builtin_palette_color_4", Color.CYAN),
                    preferences.getInt("builtin_palette_color_5", Color.BLUE),
                    preferences.getInt("builtin_palette_color_6", Color.MAGENTA),
                    preferences.getInt("builtin_palette_color_7", Color.WHITE),
                    preferences.getInt("builtin_palette_color_8", Color.LTGRAY),
                    preferences.getInt("builtin_palette_color_9", Color.GRAY),
                    preferences.getInt("builtin_palette_color_10", Color.DKGRAY),
                    preferences.getInt("builtin_palette_color_11", Color.BLACK),
                    preferences.getInt("builtin_palette_color_12", Color.TRANSPARENT)
            });
        }

    }

    public static PaletteManager getInstance() {
        return PaletteManager.Singleton.INSTANCE.getInstance();
    }

    public final static String PREFERENCES_FILE_NAME = "color_palette";

    private final MMKV preferences;

    private final ColorPalette backgroundPalette;
    private final ColorPalette gridPalette;
    private final ColorPalette builtinPalette;
    private ColorPalette externalPalette;

    public MMKV getPreferences() {
        return preferences;
    }

    public ColorPalette getBackgroundPalette() {
        return backgroundPalette;
    }

    public ColorPalette getGridPalette() {
        return gridPalette;
    }

    public ColorPalette getBuiltinPalette() {
        return builtinPalette;
    }

    public void loadExternalPalette(String name) {
        externalPalette = ColorPaletteCodec.decodeFile(getPaletteDirectoryPath() + name + ".palette");
    }

    public @Nullable ColorPalette getExternalPalette() {
        return externalPalette;
    }

    public static String getPaletteDirectoryPath() {
        return Utils.getFilesPath("palettes");
    }

}
