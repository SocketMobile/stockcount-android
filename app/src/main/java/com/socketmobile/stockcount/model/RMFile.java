/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RMFile extends RealmObject {
    @PrimaryKey
    String fileName = "";
    String fileTitle = "";
    String firstScan = "";
    String fileContent = "";
    Date updatedTime = new Date();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getFirstScan() {
        return firstScan;
    }

    public void setFirstScan(String firstScan) {
        this.firstScan = firstScan;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

}
