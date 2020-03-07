package com.anytrash.manager.model;

import java.io.Serializable;

public class PackageItem implements Serializable {
    private String packageName;
    private String packageTitle;
    private String packageSourcePath;
    private String packageDataDir;

    public PackageItem(String packageName, String packageTitle, String packageSourcePath, String packageDataDir) {
        this.packageName = packageName;
        this.packageTitle = packageTitle;
        this.packageSourcePath = packageSourcePath;
        this.packageDataDir = packageDataDir;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageTitle() {
        return packageTitle;
    }

    public String getPackageSourcePath() {
        return packageSourcePath;
    }

    public String getPackageDataDir() {
        return packageDataDir;
    }
}
