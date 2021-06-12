package com.example.phantuananh14.model;

//java gnerric -> định nghĩa 1 lớp dùng cho nhiều kiểu dữ liệu
public class AbsModel <T>  {
    private String key; // key trên firebase
    private T data; // note type: note/folder

    public AbsModel(String key) {
        this.key = key;
    }

    public AbsModel(String key, T data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
