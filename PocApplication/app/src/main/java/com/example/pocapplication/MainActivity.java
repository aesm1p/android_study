package com.example.pocapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
        Button bypassBtn = findViewById(R.id.bypassPriBtn);
        EditText et = findViewById(R.id.CodeEditText);
        WebView wv = findViewById(R.id.inteneSchemeWebview);

        Intent i = getIntent();
        String username = i.getStringExtra("username");
        String password = i.getStringExtra("token");
        TextView textView = findViewById(R.id.logTextView);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        textView.setText("");
        builder.append("Received Message:");
        builder.setSpan(
                new StyleSpan(Typeface.BOLD),
                builder.length() - "Received Message:".length(),
                builder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        builder.append("\n");
        textView.append(builder);
        textView.append("username: "+username+", token: " + password);

        findViewById(R.id.openUnexpBtn).setOnClickListener(v -> {
            Intent next = new Intent();
            next.setClassName("com.example.myapplication", "com.example.myapplication.DataActivity");
            next.putExtra("balance", 1000);
            Intent menu = new Intent();
            menu.setClassName("com.example.myapplication", "com.example.myapplication.MenuActivity");
            menu.putExtra("targetIntent", next);
            startActivity(menu);
        });

        bypassBtn.setOnClickListener(v->{
            Intent pi = new Intent("com.example.myapplication.CHANGE_PAYCODE");
            String code = et.getText().toString().trim();
            pi.putExtra("payCode", code);
            pi.putExtra("token", "123456abcd");
            pi.setClassName("com.example.myapplication", "com.example.myapplication.PayCodeActivity");
            startActivity(pi);
        });

        WebSettings settings = wv.getSettings();
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        wv.loadUrl("file:///android_asset/html/index.html");




    }
}