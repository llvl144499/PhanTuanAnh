package com.example.phantuananh14;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phantuananh14.model.AbsModel;
import com.example.phantuananh14.model.Note;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireBaseUtils {

    private static FireBaseUtils instance;

    public static FireBaseUtils getInstance() {
        if (instance == null) {
            instance = new FireBaseUtils();
        }
        return instance;
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public FireBaseUtils() {

    }

    public <T> void listNote(String topic,  ChildListener<T> childListener) {
        database.getReference(topic).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    String key = snapshot.getKey();
                    T value = snapshot.getValue(childListener.modelClass());
                    if (value != null) childListener.addItem(new AbsModel<T>(key, value));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    String key = snapshot.getKey();
                    T value = snapshot.getValue(childListener.modelClass());
                    childListener.addItem(new AbsModel<T>(key, value));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{
                    String key = snapshot.getKey();
                    childListener.deleteItem(key);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void pushItem(String topic, Note note) {
        DatabaseReference myRef = database.getReference(topic);
        myRef.push().setValue(note);
    }

    public void updateItem(String topic, Note note) {
        DatabaseReference myRef = database.getReference(topic);
        myRef.setValue(note);
    }

    public void deleteItem(String topic, String key) {
        database.getReference(topic).child(key).setValue(null);
    }

    public interface ChildListener<T> {
        void addItem(AbsModel<T> note);

        void deleteItem(String key);

        Class<T> modelClass();
    }
}
