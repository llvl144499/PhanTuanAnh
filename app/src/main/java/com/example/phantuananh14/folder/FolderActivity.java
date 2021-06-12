package com.example.phantuananh14.folder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phantuananh14.FireBaseUtils;
import com.example.phantuananh14.R;
import com.example.phantuananh14.Topic;
import com.example.phantuananh14.adapter.NoteAdapter;
import com.example.phantuananh14.adapter.NoteListener;
import com.example.phantuananh14.list.ListActivity;
import com.example.phantuananh14.model.AbsModel;
import com.example.phantuananh14.model.Note;
import com.example.phantuananh14.model.NoteType;
import com.example.phantuananh14.note.NoteActivity;
import com.google.android.material.snackbar.Snackbar;

public class FolderActivity extends AppCompatActivity {

    private View btnBack;
    private View btnCreate;
    private TextView tvTitle;
    private RecyclerView listItem;
    private NoteAdapter noteAdapter;
    private View container;

    private AbsModel<Note> folder;
    private AbsModel<Note> folderParent;

    private void initIntent() {
        String key = getIntent().getStringExtra(AbsModel.class.getName());
        Note value = getIntent().getParcelableExtra(Note.class.getName());
        folder = new AbsModel<Note>(key, value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initIntent();

        findViews();
        initActions();
        initViews();

        initAdapter();
        listenNotes();
    }


    private void initAdapter(){
        noteAdapter = new NoteAdapter(new NoteListener() {

            @Override
            public void showMenu(AbsModel<Note> absModel, View view) {
                showMenuForItem(absModel, view);
            }

            @Override
            public void openItem(AbsModel<Note> absModel) {
                Intent startIntent = null;  // gán startIntent = rỗng
                switch (absModel.getData().getType()){
                    case NoteType.FOLDER:
                        startIntent = new Intent(FolderActivity.this, FolderActivity.class);
                        break;
                    case NoteType.LIST:
                        startIntent = new Intent(FolderActivity.this, ListActivity.class);
                        break;
                    case NoteType.NOTE:
                        startIntent = new Intent(FolderActivity.this, NoteActivity.class);
                        break;
                }
                if (startIntent!=null ) {
                    startIntent.putExtra(AbsModel.class.getName(), absModel.getKey());
                    startIntent.putExtra(Note.class.getName(), absModel.getData());
                    if (folder!=null){
                        startIntent.putExtra(Topic.FOLDER+AbsModel.class.getName(), folder.getKey());
                        startIntent.putExtra(Topic.FOLDER+Note.class.getName(), folder.getData());
                    }
                    startActivity(startIntent);
                }
            }
        });
        listItem.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listItem.setAdapter(noteAdapter);
    }

    private void listenNotes() {
        FireBaseUtils.getInstance().listNote(Topic.FOLDER+"/"+folder.getKey(), new FireBaseUtils.ChildListener<Note>() {
            @Override
            public void addItem(AbsModel<Note> note) {
                noteAdapter.addItem(note);
            }

            @Override
            public void deleteItem(String key) {
                noteAdapter.deleteItem(key);
            }

            @Override
            public Class<Note> modelClass() {
                return Note.class;
            }
        });
    }


    private void findViews(){
        btnBack = findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnCreate = findViewById(R.id.btn_create);
        tvTitle = findViewById(R.id.tv_title);
        listItem = findViewById(R.id.list_item);
        container = findViewById(R.id.container);
        View btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setVisibility(View.GONE);
    }

    private void initActions(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCreate.setOnClickListener(view -> {
            onCreateNote();
        });
    }

    private void initViews(){
        if (folder!=null && folder.getData()!=null ) tvTitle.setText(folder.getData().getName());
    }

    private void onCreateNote() {
        PopupMenu popup = new PopupMenu(this, btnCreate);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_folder:
                        createFolder(null);
                        break;
                    case R.id.add_note:
                        Intent intent= new Intent(FolderActivity.this, NoteActivity.class);
                        intent.putExtra(Topic.FOLDER+AbsModel.class.getName(), folder.getKey());
                        intent.putExtra(Topic.FOLDER+Note.class.getName(), folder.getData());
                        startActivity(intent);
                        break;
                    case R.id.add_list:

                        break;
                }
                return false;
            }
        });
        popup.inflate(R.menu.main_note_menu);
        popup.show();
    }


    private void createFolder(AbsModel<Note> item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        new AlertDialog.Builder(this);

        final EditText edittext = new AppCompatEditText(this);
        edittext.setHint("nhap ten thu muc");
        if (item!=null && item.getData()!=null){
            //edit
            edittext.setText(item.getData().getName());
            alert.setTitle("Sua ten thu muc");
        }else{
            //create
            alert.setTitle("Them thu muc moi");
        }
        alert.setView(edittext);
        alert.setPositiveButton(folder!=null?"Edit":"Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edittext!=null && edittext.getText()!=null ){
                    String content = edittext.getText().toString();
                    if (content!=null && !content.isEmpty()){
                        if (item!=null && item.getData()!=null){
                            FireBaseUtils.getInstance().updateItem(Topic.FOLDER+"/"+folder.getKey()+"/"+item.getKey(), Note.Create.folder(content));
                            dialog.dismiss();
                        }else{
                            //create
                            FireBaseUtils.getInstance().pushItem(Topic.FOLDER+"/"+ folder.getKey(), Note.Create.folder(content));
                            dialog.dismiss();
                        }
                    }else {
                        showError("Chua nhap ten thu muc");
                    }
                }else {
                    showError("Chua nhap ten thu muc");
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void showError(String content) {
        Snackbar.make(container, content, Snackbar.LENGTH_LONG).show();
    }


    private void showMenuForItem(AbsModel<Note> absModel, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_edit:
                        switch (absModel.getData().getType()){
                            case NoteType.FOLDER:
                                createFolder(absModel);
                                break;
                        }
                        break;
                    case R.id.add_delete:
                        requestDeleteItem(absModel);
                        break;
                }
                return false;
            }
        });
        popup.inflate(R.menu.item_menu);
        switch (absModel.getData().getType()){
            case NoteType.FOLDER:
                popup.getMenu().findItem(R.id.add_edit).setVisible(true);
                break;
            case NoteType.NOTE:
                popup.getMenu().findItem(R.id.add_edit).setVisible(false);
                break;
            case NoteType.LIST:
                popup.getMenu().findItem(R.id.add_edit).setVisible(false);
                break;
        }
        popup.show();
    }

    private void requestDeleteItem(AbsModel<Note> absModel) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        new AlertDialog.Builder(this);
        alert.setTitle("Xoa item");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FireBaseUtils.getInstance().deleteItem(Topic.FOLDER+"/"+FolderActivity.this.folder.getKey(), absModel.getKey());
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });
        alert.show();
    }
}