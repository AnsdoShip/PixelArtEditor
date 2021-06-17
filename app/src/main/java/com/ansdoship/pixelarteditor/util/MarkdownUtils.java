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

import android.content.Context;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.commonmark.node.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.file.FileSchemeHandler;

public class MarkdownUtils {
    public static void loadIntoWithAssets(TextView view,String assetsFilePath,boolean translation){
        Context mContext = view.getContext();
        final Markwon markwon = Markwon.builder(mContext)
                .usePlugin(ImagesPlugin.create(new ImagesPlugin.ImagesConfigure(){
                    @Override
                    public void configureImages(@NonNull ImagesPlugin plugin) {
                        plugin.addSchemeHandler(FileSchemeHandler.createWithAssets(mContext));
                    }
                }))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.blockMargin(22)
                                .build();
                    }
                })
                .build();
        if (translation){
            String folderName = LanguageUtils.getResourceFolderName(mContext);
            if(folderName.equals("zh-CN")){
                assetsFilePath = "zh-CN/"+assetsFilePath;
            }
        }
        try {
            final Node node = markwon.parse(inputStream2String(mContext.getAssets().open("markdown/"+assetsFilePath)));
            final Spanned markdown = markwon.render(node);
            markwon.setParsedMarkdown(view, markdown);
        }catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static String inputStream2String(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb.toString();
    }

}
