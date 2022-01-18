package com.smartdesk.firebase;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MobileDetails {
    public String serial;
    public String model;
    public String id;
    public String manufacturer;
    public String brand;
    public String type;
    public String user;
    public int base;
    public String incremental;
    public String version_sdk;
    public String board;
    public String host;
    public String version_release;

    public long ram;
    public long freeRam;
    public long storage;
    public String appVersion;

    public MobileDetails() {
        try {

            this.serial = Build.SERIAL;
        } catch (Exception e) {

        }
        try {
            this.model = Build.MODEL;
        } catch (Exception e) {

        }
        try {
            this.id = Build.ID;
        } catch (Exception e) {

        }
        try {
            this.manufacturer = Build.MANUFACTURER;
        } catch (Exception e) {

        }
        try {
            this.brand = Build.BRAND;
        } catch (Exception e) {

        }
        try {
            this.type = Build.TYPE;
        } catch (Exception e) {

        }
        try {
            this.user = Build.USER;
        } catch (Exception e) {

        }
        try {
            this.base = Build.VERSION_CODES.BASE;
        } catch (Exception e) {

        }
        try {
            this.incremental = Build.VERSION.INCREMENTAL;
        } catch (Exception e) {

        }
        try {
            this.version_sdk = Build.VERSION.SDK;
        } catch (Exception ex) {

        }
        try {
            this.board = Build.BOARD;
        } catch (Exception ex) {

        }
        try {
            this.host = Build.HOST;
        } catch (Exception ex) {

        }
        try {
            this.version_release = Build.VERSION.RELEASE;
        } catch (Exception ex) {
        }
        try {
            this.ram = Runtime.getRuntime().totalMemory();
            this.freeRam = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        } catch (Exception ex) {

        }
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable;
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            long megAvailable = bytesAvailable / (1024 * 1024);
            this.storage = megAvailable;
        } catch (Exception ex) {

        }
        this.appVersion = "5.10";
    }

    public long getRam() {
        return ram;
    }

    public void setRam(long ram) {
        this.ram = ram;
    }

    public long getFreeRam() {
        return freeRam;
    }

    public void setFreeRam(long freeRam) {
        this.freeRam = freeRam;
    }

    public long getStorage() {
        return storage;
    }

    public void setStorage(long storage) {
        this.storage = storage;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public String getIncremental() {
        return incremental;
    }

    public void setIncremental(String incremental) {
        this.incremental = incremental;
    }

    public String getVersion_sdk() {
        return version_sdk;
    }

    public void setVersion_sdk(String version_sdk) {
        this.version_sdk = version_sdk;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVersion_release() {
        return version_release;
    }

    public void setVersion_release(String version_release) {
        this.version_release = version_release;
    }
}
