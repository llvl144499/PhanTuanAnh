package com.example.phantuananh14.model;

// Parcelable truyền dữ liệu

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Note implements Parcelable {
    private  long time;
    private int type;

    private String name;
    private String content;
    private boolean status;
    private List<Note> list ;
    private int color = Color.WHITE;

    public Note() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Note> getList() {
        return list;
    }

    public void setList(List<Note> list) {
        this.list = list;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static final class Create {

        public static Note folder(String name){
            Note note = new Note();
            note.type = NoteType.FOLDER;
            note.time = System.currentTimeMillis();
            note.name = name;
            return note;
        }

        public static Note note(String title, String content, int color){
            Note note = new Note();
            note.type = NoteType.NOTE;
            note.time = System.currentTimeMillis();
            note.name = title;
            note.content = content;
            note.color = color;
            return note;
        }

        public static Note list(String title, List<Note> items){
            Note note = new Note();
            note.type = NoteType.LIST;
            note.time = System.currentTimeMillis();
            note.name = title;
            note.list = items;
            return note;
        }

        public static Note item(String content, boolean status){
            Note note = new Note();
            note.type = NoteType.ITEM;
            note.content = content;
            note.status = status;
            return note;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.time);
        dest.writeInt(this.type);
        dest.writeString(this.name);
        dest.writeString(this.content);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeList(this.list);
        dest.writeInt(this.color);
    }

    public void readFromParcel(Parcel source) {
        this.time = source.readLong();
        this.type = source.readInt();
        this.name = source.readString();
        this.content = source.readString();
        this.status = source.readByte() != 0;
        this.list = new ArrayList<Note>();
        source.readList(this.list, Note.class.getClassLoader());
        this.color = source.readInt();
    }

    protected Note(Parcel in) {
        this.time = in.readLong();
        this.type = in.readInt();
        this.name = in.readString();
        this.content = in.readString();
        this.status = in.readByte() != 0;
        this.list = new ArrayList<Note>();
        in.readList(this.list, Note.class.getClassLoader());
        this.color = in.readInt();
    }


    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
