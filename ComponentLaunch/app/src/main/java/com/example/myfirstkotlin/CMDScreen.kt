package com.example.myfirstkotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CMDSceen(navController: NavHostController) {
    var command by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // 请求必要权限（如需要）
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("执行系统命令", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // 结果展示区
        ScrollableResultText(
            text = resultText.ifEmpty { "命令执行结果将显示在这里..." },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = command,
            onValueChange = { command = it },
            label = { Text("输入命令 (如: getprop ro.build.version.release)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    val process = Runtime.getRuntime().exec(command)
                    val result = process.inputStream.bufferedReader().readText()
                    resultText = "✅ 执行成功:\n$result"
                } catch (e: Exception) {
                    resultText = "❌ 执行失败: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("执行命令")
        }
    }
}