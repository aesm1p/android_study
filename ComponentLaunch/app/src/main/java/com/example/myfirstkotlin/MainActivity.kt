package com.example.myfirstkotlin

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComponentLaunchTheme ()
        }
    }
}

@Composable
fun ComponentLaunchTheme() {
    MaterialTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "act",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("act") { ActScreen(navController) }
                composable("br") { BRSceen() }
                composable("cp") { CPScreen() }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val items = listOf("act" to "Act", "br" to "BR", "cp" to "CP")
        items.forEach { (route, label) ->
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home,
                    modifier = Modifier.size(28.dp),
                    contentDescription = label) }, // 可替换为不同图标
                label = { Text(label,
                    style = MaterialTheme.typography.labelLarge.copy( // ✅ 使用更大的 labelLarge
                        fontSize = 14.sp // ✅ 可进一步自定义大小
                    ),
                    ) },
                selected = currentRoute(navController) == route,
                onClick = {
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    return navController.currentBackStackEntry?.destination?.route
}

@Composable
fun ActScreen(navController: NavHostController) {
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var componentName by remember { mutableStateOf("") }
    var flags by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = "启动Activity",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 执行结果展示区
        Text(
            text = "执行结果：\n$resultText",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 参数输入区
        OutlinedTextField(
            value = action,
            onValueChange = { action = it },
            label = { Text("Action (如: android.intent.action.VIEW)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = componentName,
            onValueChange = { componentName = it },
            label = { Text("ComponentName (如: com.example/.TargetActivity)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = flags,
            onValueChange = { flags = it },
            label = { Text("Flags (逗号分隔，如: 0x10000000,0x04000000)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 启动按钮
        Button(
            onClick = {
                try {
                    val intent = Intent()

                    // 设置 Action
                    if (action.isNotEmpty()) {
                        intent.action = action
                    }

                    // 设置 ComponentName
                    if (componentName.isNotEmpty()) {
                        val comp = ComponentName.unflattenFromString(componentName)
                        if (comp != null) {
                            intent.component = comp
                        } else {
                            resultText = "❌ ComponentName 格式错误"
                            return@Button
                        }
                    }

                    // 设置 Flags
                    if (flags.isNotEmpty()) {
                        flags.split(",").map { it.trim() }.forEach { flagStr ->
                            val flagValue = flagStr.toIntOrNull(16) ?: flagStr.toIntOrNull()
                            if (flagValue != null) {
                                intent.addFlags(flagValue)
                            }
                        }
                    }

                    // 启动 Activity
                    context.startActivity(intent)
                    resultText = "✅ Intent 已发送"
                } catch (e: Exception) {
                    resultText = "❌ 错误：${e.message}"
                    Log.e("ActScreen", "启动失败", e)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("启动 Activity")
        }
    }
}

@Composable
fun BRSceen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "BR 页面 - 广播接收器相关功能（待实现）",
            style = MaterialTheme.typography.headlineSmall
        )
        // 后续可添加内容
    }
}

@Composable
fun CPScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CP 页面 - 内容提供者相关功能（待实现）",
            style = MaterialTheme.typography.headlineSmall
        )
        // 后续可添加内容
    }
}