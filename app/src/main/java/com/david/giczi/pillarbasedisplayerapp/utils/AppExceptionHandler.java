package com.david.giczi.pillarbasedisplayerapp.utils;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AppExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;

    public AppExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        saveExceptionToFile(e);
        System.exit(1);
    }

    private void saveExceptionToFile(Throwable e){
        try {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            File file = new File(Environment.getExternalStorageDirectory(),
                    "/Documents/pillar_base_app_crash_log.txt");
            FileWriter writer = new FileWriter(file, true);
            writer.append("---- Crash ----\n");
            writer.append(exceptionAsString);
            writer.append("\n\n");
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
