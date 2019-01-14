package com.interswitch.posinterface.posshim;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class PrintConfiguration {

    private List<Object> printObjects = new ArrayList<>();

    class Line {}

    public PrintConfiguration addString(String data) {
        printObjects.add(data);
        return this;
    }

    public PrintConfiguration addBitmap(Bitmap bitmap) {
        printObjects.add(bitmap);
        return this;
    }

    public PrintConfiguration addLine() {
        printObjects.add(new Line());
        return this;
    }

    public List<Object> getPrintObjects() {
        return printObjects;
    }
}
