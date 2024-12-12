package com.example.dictaphone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Recordings extends Fragment implements OnSelectListener{
    private RecyclerView recyclerView;
    private List<File> fileList;
    private RecAdapter recAdapter;
    static File path;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recordings, container, false);

        Recorder.setRecordingsFragment(this);
        
        path = new File(requireContext().getExternalFilesDir(null) + "/rec");
        if (!path.exists()) {
            if (!path.mkdirs()) {
                Toast.makeText(requireContext(), "Не удалось создать директорию: " + path.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }
        else
            showFiles();

        return view;
    }

    private void showFiles() {
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        fileList = new ArrayList<>();
        fileList.addAll(findFile(path));
        recAdapter = new RecAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(recAdapter);
    }

    public static ArrayList<File> findFile(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files){
            if (singleFile.getName().toLowerCase().endsWith(".aac"))
                arrayList.add(singleFile);
        }

        return arrayList;
    }

    @Override
    public void OnSelected(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "audio/x-wav");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getContext().startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            showFiles();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateFileList() {
        fileList.clear();
        fileList.addAll(findFile(path));
        recAdapter.notifyDataSetChanged();
    }
}
