package com.smartdesk.model.SmartDesk;

public class NewDesk {

    public String iD;
    public String name;
    public boolean wirelessCharging;
    public boolean builtinSpeaker;
    public boolean bluetoothConnection;

    public boolean groupUser;

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWirelessCharging() {
        return wirelessCharging;
    }

    public void setWirelessCharging(boolean wirelessCharging) {
        this.wirelessCharging = wirelessCharging;
    }

    public boolean isBuiltinSpeaker() {
        return builtinSpeaker;
    }

    public void setBuiltinSpeaker(boolean builtinSpeaker) {
        this.builtinSpeaker = builtinSpeaker;
    }

    public boolean isBluetoothConnection() {
        return bluetoothConnection;
    }

    public void setBluetoothConnection(boolean bluetoothConnection) {
        this.bluetoothConnection = bluetoothConnection;
    }

    public boolean isGroupUser() {
        return groupUser;
    }

    public void setGroupUser(boolean groupUser) {
        this.groupUser = groupUser;
    }
}
