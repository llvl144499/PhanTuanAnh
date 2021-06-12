package com.example.phantuananh14;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phantuananh14.adapter.NoteAdapter;
import com.example.phantuananh14.adapter.NoteListener;
import com.example.phantuananh14.folder.FolderActivity;
import com.example.phantuananh14.list.ListActivity;
import com.example.phantuananh14.model.AbsModel;
import com.example.phantuananh14.model.Note;
import com.example.phantuananh14.model.NoteType;
import com.example.phantuananh14.note.NoteActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private View btnCreate;
    private View btnMenu;
    private TextView tvTitle;
    private RecyclerView listItem;
    private NoteAdapter noteAdapter;
    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initActions();

        initAdapter();
        listenNotes();
        // Write a message to the database
    }
    //khoi tao adapter cua note
    private void initAdapter(){
        noteAdapter = new NoteAdapter(new NoteListener() {
            //            là cái 3 cham ở mỗi item
            @Override
            public void showMenu(AbsModel<Note> absModel, View view) {
                showMenuForItem(absModel, view);
            }

            @Override
            public void openItem(AbsModel<Note> absModel) {
                //thiet lap activity cho tung option trong menu
                Intent startIntent = null;
                switch (absModel.getData().getType()){ // lấy kiểu của item trong list adapter , tẹo intent tương ứng với kiểu item , folder => folder ac , note => noteac
                    case NoteType.FOLDER:
                        startIntent = new Intent(MainActivity.this, FolderActivity.class);
                        break;
                    case NoteType.LIST:
                        startIntent = new Intent(MainActivity.this, ListActivity.class);
                        break;
                    case NoteType.NOTE:
                        startIntent = new Intent(MainActivity.this, NoteActivity.class);
                        break;
                }
                //sau khi nhan duoc thiet lap activity thi truyen data theo tuong ung theo tung activity
//                intent cơ chuyền dữ liệu từ ac - > ac khác
                if (startIntent!=null ) {
                    startIntent.putExtra(AbsModel.class.getName(), absModel.getKey()); // gửi theo key trên firebase
                    startIntent.putExtra(Note.class.getName(), absModel.getData()); // data ~ (-key) ~ object Note type : note/foleder
                    startActivity(startIntent);
                }
            }
        });
        //thiet lap adapter cho listview
        listItem.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listItem.setAdapter(noteAdapter);
    }

    //thiet lap chuc nang cho tung note trong firebase
    private void listenNotes() {
        FireBaseUtils.getInstance().listNote( Topic.NOTE,  new FireBaseUtils.ChildListener<Note>() {
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
        btnCreate = findViewById(R.id.btn_create);
        tvTitle = findViewById(R.id.tv_title);
        listItem = findViewById(R.id.list_item);
        container = findViewById(R.id.container);
        btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setVisibility(View.VISIBLE);
    }

    //thiet lap su kien cho nut
    private void initActions(){
        btnCreate.setOnClickListener(view -> {
            onCreateNote();
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainMenu();

            }
        });
    }

    //thiet lap cho menu popup
    private void showMainMenu(){   // nut logout o main_note_menu
        PopupMenu popup = new PopupMenu(this, btnCreate);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {//su kien click vao nut logout
                switch (item.getItemId()){
                    case R.id.action_logout:
                        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                }
                return false;
            }
        });
        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    //Khi tao 1 cai note tien hanh hien 1 cai menu popup
    private void onCreateNote() {
        PopupMenu popup = new PopupMenu(this, btnCreate);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {//thiet lap chuc nang cho tung option trong menu vua tao
                switch (item.getItemId()){
                    case R.id.add_folder:
                        createFolder(null);
                        break;
                    case R.id.add_note:
                        startActivity(new Intent(MainActivity.this, NoteActivity.class));
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

    //khi tao 1 folder
    private void createFolder(AbsModel<Note> folder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        new AlertDialog.Builder(this);

        //thiet lap tung tanh phan trong dialog
        final EditText edittext = new AppCompatEditText(this);
        edittext.setHint("nhap ten thu muc");
        if (folder!=null && folder.getData()!=null){
            //edit
            edittext.setText(folder.getData().getName());
            alert.setTitle("Sua ten thu muc");
        }else{
            //create
            alert.setTitle("Them thu muc moi");
        }

        //set view va kiem tra dieu kien cho tung thanh phan
        alert.setView(edittext);
        alert.setPositiveButton((folder!=null?"Edit":"Create"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edittext!=null && edittext.getText()!=null ){
                    String content = edittext.getText().toString().trim();
                    if (content!=null && !content.isEmpty()){
                        //kiem tra neu folder da ton tai nd thi update(them nd vao folder)
                        //nguoc lai thi tao folder dong thoi them nd vao folder
                        if (folder!=null && folder.getData()!=null){
                            //edit
                            FireBaseUtils.getInstance().updateItem(Topic.NOTE+"/"+folder.getKey(), Note.Create.folder(content));
                        }else{
                            //create
                            FireBaseUtils.getInstance().pushItem(Topic.NOTE, Note.Create.folder(content));
                        }
                    }else {
                        showError("Chua nhap ten thu muc");
                    }
                }else {
                    showError("Chua nhap ten thu muc");
                }
//                if (folder==null){
//                    //neu tao moi , thi ms dismiss
//                    //neu edit thi ko an di
//                    dialog.dismiss();
//                }
            }
        });

        //neu chon cancel thi thoat dialog
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

    //thiet lap menu option cho tung item(folder, note)
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
        //neu la folder thi hien thi sua k thi chi xoa
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
    //xac nhan lai co muon xoa item hay k
    private void requestDeleteItem(AbsModel<Note> absModel) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        new AlertDialog.Builder(this);
        alert.setTitle("Xoa item");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FireBaseUtils.getInstance().deleteItem(Topic.NOTE, absModel.getKey());
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