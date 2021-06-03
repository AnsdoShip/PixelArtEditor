package com.ansdoship.pixelarteditor.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public final class JSONCodec {

    public static @NonNull JSONObject decodeFile (@NonNull String pathname) throws IOException, JSONException {
        return decodeFile(new File(pathname));
    }

    public static @NonNull JSONObject decodeFile (@NonNull File file) throws IOException, JSONException {
        return new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
    }

    public static void encodeFile (@NonNull JSONObject jsonObject, @NonNull String pathname) throws IOException {
        encodeFile(jsonObject, new File(pathname));
    }

    public static void encodeFile (@NonNull JSONObject jsonObject, @NonNull File file) throws IOException {
        FileUtils.writeStringToFile(file, jsonObject.toString(), "UTF-8", false);
    }

}
