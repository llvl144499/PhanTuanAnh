package com.example.phantuananh14.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phantuananh14.R;
import com.example.phantuananh14.model.AbsModel;
import com.example.phantuananh14.model.Note;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private NoteListener noteListener;
    private List<AbsModel<Note>> listData = new ArrayList<>();

    public NoteAdapter(NoteListener noteListener) {
        this.noteListener = noteListener;
    }

    public void addItem(AbsModel<Note> absModel){
        if (absModel==null && absModel.getData()==null) return;
        int index = Iterators.indexOf(listData.iterator(), new Predicate<AbsModel<Note>>() {
            @Override
            public boolean apply(@NullableDecl AbsModel<Note> input) {
                return input!=null && TextUtils.equals(input.getKey(), absModel.getKey());
            }
        });
        if (index<0){
            //add
            listData.add(absModel);
            notifyItemInserted(listData.size()-1);
        }else {
            listData.set(index, absModel);
            notifyItemChanged(index);
        }
    }

    public void deleteItem(String key){
        int index = Iterators.indexOf(listData.iterator(), new Predicate<AbsModel<Note>>() {
            @Override
            public boolean apply(@NullableDecl AbsModel<Note> input) {
                return input!=null && TextUtils.equals(input.getKey(), key);
            }
        });
        if (index>=0){
            listData.remove(index);
            notifyItemRemoved(index);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NoteViewHolder(view, noteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
