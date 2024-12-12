package com.example.dictaphone;

import android.animation.ObjectAnimator;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.Manifest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Recorder extends Fragment {
    View view;
    ImageButton btnRec;
    ImageButton btnPause;
    Chronometer timeRec;

    private static String fileName;
    private MediaRecorder recorder;
    boolean isRecording;
    boolean isPausing;
    private long pauseTime;
    int count = 1;

    private ObjectAnimator alphaAnimator;

    private static Recordings recordingsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recorder, container, false);

        File path = new File(requireContext().getExternalFilesDir(null) + "/rec");
        if (!path.exists()) {
            if (!path.mkdirs()) {
                Toast.makeText(requireContext(), "Не удалось создать директорию: " + path.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }

        btnRec = view.findViewById(R.id.btnRec);
        btnPause = view.findViewById(R.id.btnPause);
        timeRec = view.findViewById(R.id.timeRec);

        isRecording = false;
        isPausing = false;

        askRuntimePermission();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    try {
                        fileName = getUniqueFileName(path, date + ".aac");
                        if (!path.exists()){
                            path.mkdirs();
                        }

                        startRecording();
                        timeRec.setBase(SystemClock.elapsedRealtime());
                        timeRec.start();
                        btnRec.setImageResource(R.drawable.stop);
                        btnPause.setVisibility(View.VISIBLE);
                        isRecording = true;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Не удалось начать запись", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    stopRecording();
                    timeRec.setBase(SystemClock.elapsedRealtime());
                    timeRec.stop();
                    btnRec.setImageResource(R.drawable.rec);
                    btnPause.setVisibility(View.INVISIBLE);
                    isRecording = false;
                    if (recordingsFragment != null) {
                        recordingsFragment.updateFileList();
                    }
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPausing){
                    pauseRecording();
                    pauseTime = SystemClock.elapsedRealtime() - timeRec.getBase();
                    timeRec.stop();
                    startBlinking();
                    isPausing = true;
                }
                else {
                    continueRecording();
                    timeRec.setBase(SystemClock.elapsedRealtime() - pauseTime);
                    timeRec.start();
                    stopBlinking();
                    isPausing = false;
                }
            }
        });

        return view;
    }

    private void askRuntimePermission() {
        Dexter.withContext(getContext()).withPermissions(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void startRecording(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncodingBitRate(16_000);
        recorder.setAudioSamplingRate(44_100);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        recorder.start();
    }

    private void stopRecording(){
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void pauseRecording(){
        recorder.pause();
    }

    private void continueRecording(){
        recorder.resume();
    }

    private String getUniqueFileName(File path, String baseFileName) {
        File file = new File(path, baseFileName);
        if (!file.exists()) {
            return file.getAbsolutePath();
        } else {
            String newFileName;
            while (true) {
                newFileName = baseFileName.replace(".aac", "_" + count + ".aac");
                file = new File(path, newFileName);
                if (!file.exists()) {
                    break;
                }
                count++;
            }
            return new File(path, newFileName).getAbsolutePath();
        }
    }

    private void startBlinking() {
        alphaAnimator = ObjectAnimator.ofFloat(timeRec, "alpha", 1f, 0.2f);
        alphaAnimator.setDuration(800);
        alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        alphaAnimator.start();
        alphaAnimator.start();
    }

    private void stopBlinking() {
        if (alphaAnimator != null && alphaAnimator.isRunning()) {
            alphaAnimator.cancel();
        }
        timeRec.setAlpha(1f);
    }

    public static void setRecordingsFragment(Recordings recordings) {
        recordingsFragment = recordings;
    }
}