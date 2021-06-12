package com.example.phantuananh14.adapter;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phantuananh14.R;
import com.example.phantuananh14.model.AbsModel;
import com.example.phantuananh14.model.Note;
import com.example.phantuananh14.model.NoteType;

import java.util.Date;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private ImageView icon;
    private TextView name;
    private TextView timeV;
    private View btnMenu;
    private NoteListener noteListener;

    public NoteViewHolder(@NonNull View itemView, NoteListener noteListener) {
        super(itemView);
        this.noteListener = noteListener;
        icon=  itemView.findViewById(R.id.icon);
        name = itemView.findViewById(R.id.name);
        timeV = itemView.findViewById(R.id.time);
        btnMenu = itemView.findViewById(R.id.btn_menu);
    }

    public void bind(AbsModel<Note> absModel){
        switch (absModel.getData().getType()){
            case NoteType.FOLDER:
                icon.setImageResource(R.drawable.ic_folder);
                break;
            case NoteType.NOTE:
                icon.setImageResource(R.drawable.ic_note);
                icon.setBackgroundColor(absModel.getData().getColor());
                break;
            case NoteType.LIST:
                icon.setImageResource(R.drawable.ic_check_list);
                break;
        }
        long time = absModel.getData().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy HH:mm", new Date(time)).toString();

        name.setText(absModel.getData().getName());
        timeV.setText(dateString);
        btnMenu.setOnClickListener(view -> noteListener.showMenu(absModel, view));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteListener.openItem(absModel);
            }
        });
    }
}