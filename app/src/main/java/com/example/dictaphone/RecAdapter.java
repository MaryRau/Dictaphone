package com.example.dictaphone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<RecViewHolder> {
    private Context context;
    private List<File> fileList;
    private OnSelectListener listener;

    public RecAdapter(Context context, List<File> fileList, OnSelectListener listener) {
        this.context = context;
        this.fileList = fileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txtName.setText(fileList.get(position).getName());
        holder.txtName.setSelected(true);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnSelected(fileList.get(position));
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Удаление записи")
                        .setMessage("Вы уверены, что хотите удалить эту запись?")
                        .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File fileToDelete = fileList.get(position);
                                if (fileToDelete.delete()) {
                                    Toast.makeText(context, "Запись удалена", Toast.LENGTH_SHORT).show();
                                    fileList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, fileList.size());
                                } else {
                                    Toast.makeText(context, "Не удалось удалить запись", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
