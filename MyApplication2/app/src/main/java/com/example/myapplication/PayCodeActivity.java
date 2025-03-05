package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class PayCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay_code);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPref =getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String code = sharedPref.getString("payCode", null);
        if(code == null){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("payCode", "666888");
            editor.apply();
        }

        TextView codeTv = findViewById(R.id.CodeTv);
        codeTv.append(code);

        Intent i = getIntent();
        if(Objects.equals("com.example.myapplication.CHANGE_PAYCODE", i.getAction())){
            if(Objects.equals(i.getStringExtra("token"), "123456abcd")){
                if(i.getStringExtra("payCode").toString().trim().isEmpty()){
                    Toast.makeText(this, "Pay code cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("payCode", i.getStringExtra("payCode"));
                    editor.apply();
                    codeTv.setText(i.getStringExtra("payCode"));
                }



            }
        }

    }
}