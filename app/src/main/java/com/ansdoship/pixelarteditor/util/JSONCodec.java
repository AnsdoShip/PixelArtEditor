/*
 * Copyright (C) 2021 AnsdoShip Studio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
