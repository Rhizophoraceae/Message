package com.rjgc.zhs.data;

public class Setting {
    private int icon;
    private int describe;

    public Setting(int icon, int describe) {
        this.icon = icon;
        this.describe = describe;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getDescribe() {
        return describe;
    }

    public void setDescribe(int describe) {
        this.describe = describe;
    }
}
