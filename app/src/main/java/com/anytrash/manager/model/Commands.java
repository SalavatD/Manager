package com.anytrash.manager.model;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Commands {

    public static String[] executeCommand(String string) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "system/bin/sh"});
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        char[] chars = new char[64];
        int len;
        bw.write(string);
        bw.write("\n");
        bw.flush();
        bw.close();
        while ((len = br.read(chars)) > 0) {
            sb.append(chars, 0, len);
        }
        br.close();
        p.waitFor();
        return sb.toString().split("\n");
    }

    public static void clearExternalStorage() throws IOException, InterruptedException {
        String externalStorage = Environment.getExternalStorageDirectory().getPath();
        String[] fileList = executeCommand("ls -1 " + externalStorage);
        for (String file : fileList) {
            executeCommand("rm -rf " + externalStorage + "/" + file);
        }
    }

    public static void clearPackageData(String packageName) throws IOException, InterruptedException {
        executeCommand("pm clear " + packageName);
    }

    public static void deletePackage(String packageSourcePath) throws IOException, InterruptedException {
        executeCommand("rm -rf " + packageSourcePath);
    }
}
