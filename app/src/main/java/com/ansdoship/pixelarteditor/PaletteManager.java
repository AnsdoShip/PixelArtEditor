package com.ansdoship.pixelarteditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.app.ApplicationUtils;
import com.ansdoship.pixelarteditor.editor.palette.Palette;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFactory;

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
        preferences = ApplicationUtils.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private void loadBackgroundPalette() {
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
    }

    private void loadGridPalette() {
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
    }

    private void loadBuiltinPalette() {
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
    }

    public static PaletteManager getInstance() {
        return PaletteManager.Singleton.INSTANCE.getInstance();
    }

    public final static String PREFERENCES_NAME = "palettes";

    public final static String KEY_BACKGROUND_PALETTE = "background_palette";
    public final static String KEY_GRID_PALETTE = "grid_palette";
    public final static String KEY_BUILTIN_PALETTE = "builtin_palette";

    public final static int[] BACKGROUND_PALETTE_COLORS_DEFAULT = new int[] {
            Color.DKGRAY, Color.LTGRAY, Color.GRAY
    };
    public final static int[] GRID_PALETTE_COLORS_DEFAULT = new int[] {Color.BLACK};
    public final static int[] BUILTIN_PALETTE_COLORS_DEFAULT = new int[] {
            Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
            Color.WHITE, Color.LTGRAY, Color.GRAY, Color.DKGRAY, Color.BLACK, Color.TRANSPARENT
    };

    private final SharedPreferences preferences;

    private Palette backgroundPalette;
    private Palette gridPalette;
    private Palette builtinPalette;
    private Palette externalPalette;

    public void loadInternalPalettes() {
        loadBackgroundPalette();
        loadGridPalette();
        loadBuiltinPalette();
    }

    public void saveInternalPalettes() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString(KEY_BACKGROUND_PALETTE, PaletteFactory.encodeString(backgroundPalette));
        editor.putString(KEY_GRID_PALETTE, PaletteFactory.encodeString(gridPalette));
        editor.putString(KEY_BUILTIN_PALETTE, PaletteFactory.encodeString(builtinPalette));
        editor.apply();
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
        externalPalette = PaletteFactory.decodeFile(getPalettesPath() + name + ".palette");
    }

    public @Nullable
    Palette getExternalPalette() {
        return externalPalette;
    }

    public static String getPalettesPath() {
        return Utils.getFilesPath("palettes");
    }

}
