package com.example.phantuananh14.note;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phantuananh14.FireBaseUtils;
import com.example.phantuananh14.R;
import com.example.phantuananh14.Topic;
import com.example.phantuananh14.model.AbsModel;
import com.example.phantuananh14.model.Note;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

//thiet lap cho activity note
public class NoteActivity extends AppCompatActivity {

    private View btnSave;
    private View btnColor;
    private EditText edTitle;
    private EditText edContent;


    private AbsModel<Note> note;
    private AbsModel<Note> folder;
    private int currentColor = Color.WHITE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        initIntent();
        findViews();
        initViews();
        bindActions();
    }

    //khoi tao cac thanh phan
    private void initIntent() {
        String key = getIntent().getStringExtra(AbsModel.class.getName());
        Note value = getIntent().getParcelableExtra(Note.class.getName());
        if (key!=null && value!=null) note = new AbsModel<Note>(key, value);

        String keyFolder = getIntent().getStringExtra(Topic.FOLDER+AbsModel.class.getName());
        Note valueFolder = getIntent().getParcelableExtra(Topic.FOLDER+Note.class.getName());
        if (keyFolder!=null && valueFolder!=null) folder = new AbsModel<Note>(keyFolder, valueFolder);
    }

    private void findViews() {
        edTitle = findViewById(R.id.ed_title);
        edContent = findViewById(R.id.ed_content);
        btnSave = findViewById(R.id.btn_save);
        btnColor= findViewById(R.id.btn_color);
    }

    private void initViews() {
        if (note!=null && note.getData()!=null){
            //Edit
            edTitle.setText(note.getData().getName());
            edContent.setText(note.getData().getContent());
            currentColor = note.getData().getColor();
            updateColor();
        }else{
            //create
        }
    }

    private void updateColor(){
        edTitle.setBackgroundColor(currentColor);
        edContent.setBackgroundColor(currentColor);
    }

    //thiétlap su kien cho nut đổi mầu
    private void bindActions() {
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(NoteActivity.this)
                        .setTitle("Choose color")
                        .initialColor(currentColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                currentColor = selectedColor;
                                updateColor();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edTitle.getText()!=null){
                    String title = edTitle.getText().toString().trim();  // trim() bỏ dấu cách
                    if (title!=null && !title.isEmpty()){
                        String content = "";
                        if (edContent.getText()!=null) content = edContent.getText().toString();
                        //neu thoa man cac dieu kien
                        if (note!=null && note.getData()!=null){
                            //update
                            if (folder!=null && folder.getData()!=null){
                                FireBaseUtils.getInstance().updateItem(Topic.FOLDER+"/"+folder.getKey()+"/"+note.getKey(), Note.Create.note(title, content, currentColor));
                            }else {
                                FireBaseUtils.getInstance().updateItem(Topic.NOTE+"/"+note.getKey(), Note.Create.note(title, content, currentColor));
                            }
                        }else {
                            //create
                            if (folder!=null && folder.getData()!=null){
                                //create in foder
                                FireBaseUtils.getInstance().pushItem(Topic.FOLDER+"/"+folder.getKey(), Note.Create.note(title, content, currentColor));
                            }else {
                                FireBaseUtils.getInstance().pushItem(Topic.NOTE, Note.Create.note(title, content, currentColor));
                            }
                        }
                        finish();
                    }else {
                        showError("Chua nhap tieu de");
                    }
                }else {
                    showError("Chua nhap tieu de");
                }
            }
        });
    }

    private void showError(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
