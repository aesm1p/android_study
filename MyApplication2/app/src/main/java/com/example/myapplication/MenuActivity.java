package com.example.myapplication;



import static android.os.FileUtils.copy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView userInfoTv = findViewById(R.id.userInfoTv);
        Button balanceBtn = findViewById(R.id.balanceBtn);
        Button payCodeBtn = findViewById(R.id.payCodeBtn);
        Button showCodeBtn = findViewById(R.id.showCodeBtn);
        Button resultBtn = findViewById(R.id.resultBtn);

        Intent i = getIntent();
        String username = i.getStringExtra("username");
        userInfoTv.append("Hello " + username + " \uD83D\uDE00");
        EditText inputEt = findViewById(R.id.CodeEditTextTextPassword);

        Intent targetIntent = getIntent().getParcelableExtra("targetIntent");
        if (targetIntent != null) {
            Log.d("myappunexp", "onCreate: targetIntent is not null");
            startActivity(targetIntent);
        }

        balanceBtn.setOnClickListener(v->{
            Intent di = new Intent(MenuActivity.this, DataActivity.class);
            di.putExtra("balance", 999999);
            startActivity(di);
        });

        showCodeBtn.setOnClickListener(v->{
            Intent pi = new Intent(MenuActivity.this, PayCodeActivity.class);
            startActivity(pi);
        });

        payCodeBtn.setOnClickListener(v->{
            Intent pi = new Intent(MenuActivity.this, PayCodeActivity.class);
            pi.putExtra("payCode", inputEt.getText().toString().trim());
            pi.putExtra("token", "123456abcd");
            pi.setAction("com.example.myapplication.CHANGE_PAYCODE");
            startActivity(pi);
        });

        resultBtn.setOnClickListener(v->{
            Intent pi = new Intent("android.intent.action.PICK");
            pi.putExtra("id", 222);
            startActivityForResult(pi, 1);
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            Uri pickedUri = data.getData();
            Toast.makeText(this, "Activity Result Picked file: " + pickedUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}