package com.example.phantuananh14.adapter;

import android.view.View;

import com.example.phantuananh14.model.AbsModel;
import com.example.phantuananh14.model.Note;

public interface NoteListener {
    void showMenu(AbsModel<Note> absModel, View view);

    void openItem(AbsModel<Note> absModel);
}
