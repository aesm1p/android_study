package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        Log.i("victim", "onCreate: ");
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action != null && action.equals("ACTION_SHARE_TO_ME")){
            Log.i("victim", "onCreate: ACTION_SHARE_TO_ME");
            setResult(-1, getIntent());
            finish();
        }
        

//        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent("aesm1p.intent.action.LOGIN");
//                i.putExtra("username", "admin");
//                i.putExtra("token", "123456abcd");
//
//                // safa code
////                ComponentName cn = new ComponentName(
////                        "com.example.myapplication",
////                        "com.example.myapplication.MenuActivity"
////                );
////                i.setComponent(cn);
//
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                // 打开activity
//                startActivity(i);
//
//            }
//        });



    }




}
