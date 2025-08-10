package com .example.victim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_command);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView et = findViewById(R.id.editTextText);
        TextView resTv = findViewById(R.id.textView);
        resTv.setMovementMethod(new ScrollingMovementMethod());
        Button btn1 = findViewById(R.id.button1);

        // 2. 为按钮添加点击事件监听器
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd = et.getText().toString().trim(); // 获取输入框的命令
                if (cmd.isEmpty()) {
                    resTv.setText("请输入命令");
                    return;
                }

                try {
                    // 执行命令
                    Process process = Runtime.getRuntime().exec(cmd);

                    // 读取命令执行结果
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()));

                    StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    while ((line = errorReader.readLine()) != null) {
                        output.append(line).append("\n");
                    }

                    // 等待命令执行完成
                    process.waitFor();

                    // 显示结果
                    resTv.setText(output.toString());

                } catch (Exception e) {
                    resTv.setText("执行出错: " + e.getMessage());
                }
            }
        });

    }
}