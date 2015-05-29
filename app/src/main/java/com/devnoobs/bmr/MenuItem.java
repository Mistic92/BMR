package com.devnoobs.bmr;

/**
 * Created by Lukasz on 2015-05-30.
 */
public class MenuItem {
    private int icon;
    private String tytul;
    private boolean isGroupHeader = false;

    public boolean isGroupHeader() {
        return isGroupHeader;
    }

    public void setIsGroupHeader(boolean isGroupHeader) {
        this.isGroupHeader = isGroupHeader;
    }

    public MenuItem(String title) {
        this(-1, title);
        isGroupHeader = true;
    }

    public MenuItem(int icon, String tytul) {
        this.icon = icon;
        this.tytul = tytul;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }


}
