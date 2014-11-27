/*
 * Copyright (C) 2014 k3b
 * 
 * This file is part of de.k3b.android.toGoZip (https://github.com/k3b/ToGoZip/) .
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package de.k3b.android;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

import de.k3b.android.toGoZip.Global;
import de.k3b.android.toGoZip.R;
import de.k3b.android.toGoZip.SettingsImpl;
import de.k3b.android.widgets.Clipboard;
import de.k3b.zip.CompressJob;
import de.k3b.zip.ZipLog;
import de.k3b.zip.ZipLogImpl;

/**
 * Android specific version of zip-CompressJob.<br/>
 * <p/>
 * Created by k3b on 17.11.2014.
 */
public class AndroidCompressJob extends CompressJob {
    private final Context context;

    /**
     * Creates a job.
     *  @param destZip     full path to the zipfile where the new files should be added to
     * @param zipLog if true collect diagnostics/debug messages to debugLogMessages.
     */
    public AndroidCompressJob(Context context, File destZip, ZipLog zipLog) {
        super(destZip, zipLog);
        this.context = context;
    }
    //############ processing ########

    public void addToZip(String textToBeAdded, File[] fileToBeAdded) {
        if ((textToBeAdded != null) || (fileToBeAdded != null)) {
            destZip.getParentFile().mkdirs();
            addToCompressQue("", fileToBeAdded);
            if (textToBeAdded != null) {
                addTextToCompressQue(SettingsImpl.getTextfile(), textToBeAdded);
            }
            int result = compress();

            String currentZipFileAbsolutePath = destZip.getAbsolutePath();
            final String text = getResultMessage(result);
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            if (Global.debugEnabled) {
                Clipboard.addToClipboard(context, text + "\n\n" + getLastError(true));
            }
        }
    }

    private String getResultMessage(int convertResult) {
        String currentZipFileAbsolutePath = this.destZip.getAbsolutePath();
        if (convertResult == CompressJob.RESULT_ERROR_ABOART) {
            return String.format(context.getString(R.string.ERR_ADD),
                    currentZipFileAbsolutePath, getLastError(false));
        } else if (convertResult == CompressJob.RESULT_NO_CHANGES) {
            return String.format(context.getString(R.string.WARN_ADD_NO_CHANGES), currentZipFileAbsolutePath);
        } else {
            return String.format(context.getString(R.string.SUCCESS_ADD), currentZipFileAbsolutePath, getAddCount());
        }
    }
}