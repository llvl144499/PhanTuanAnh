package com.example.phantuananh14;

import android.text.TextUtils;

import com.google.gson.Gson;

public final class Strings {

    public static void logPer(Object object) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String method = "";
        method = elements[3].getMethodName();
        String content = object.getClass().getSimpleName() + "::" + method + "():";
        log(content, "");
    }

    public static void logPer(Object object, Object o) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String method = "";
        method = elements[3].getMethodName();
        String content = object.getClass().getSimpleName() + "::" + method + "():";
        log(content, o);
    }

    public static void log(Object o) {
        if (BuildConfig.DEBUG) log("", o);
    }

    public static void log(String message, Object o) {

        String json = "";
        if (o instanceof String) {
            json = (String) o;
        } else {
            json = (o == null ? "NULL" : new Gson().toJson(o));
        }
        int maxLogSize = 2600;
        int partSize = json.length() / maxLogSize;
        if (partSize > 0 && BuildConfig.DEBUG)
            android.util.Log.d("TESTAPP", (TextUtils.isEmpty(message) ? "" : (message + ": ")) + "Size: " + json.length());

        for (int i = 0; i <= partSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > json.length() ? json.length() : end;
            if (BuildConfig.DEBUG) android.util.Log.d("TESTAPP",
                    (TextUtils.isEmpty(message)
                            ? "" : (message + ": "))
                            + (partSize > 0 ? "PART " + (i + 1) + ": " : "")
                            + json.substring(start, end));
        }
    }
}
