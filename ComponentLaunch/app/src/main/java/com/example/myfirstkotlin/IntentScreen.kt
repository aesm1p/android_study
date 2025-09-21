@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.myfirstkotlin

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


data class IntentExtra(
    val key: String,
    val type: ExtraType,
    val rawValue: String
)

enum class ExtraType {
    STRING, BOOLEAN, INT, LONG, FLOAT, DOUBLE, URI;

    companion object {
        fun fromString(type: String) = values().firstOrNull { it.name == type } ?: STRING
    }

    fun formatValue(rawValue: String): Any? = when (this) {
        STRING -> rawValue
        BOOLEAN -> rawValue.equals("true", ignoreCase = true)
        INT -> rawValue.toIntOrNull() ?: 0
        LONG -> rawValue.toLongOrNull() ?: 0L
        FLOAT -> rawValue.toFloatOrNull() ?: 0f
        DOUBLE -> rawValue.toDoubleOrNull() ?: 0.0
        URI -> runCatching { Uri.parse(rawValue) }.getOrNull() ?: Uri.EMPTY
    }

    override fun toString() = name.lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
fun IntentScreen(navController: NavHostController) {
    var action by remember { mutableStateOf("") }
    var component by remember { mutableStateOf("") }
    var dataUri by remember { mutableStateOf("") }
    val isUriValid = remember(dataUri) {
        dataUri.isNotEmpty() && runCatching {
            Uri.parse(dataUri).let { it.isHierarchical || it.scheme != null }
        }.getOrDefault(false)
    }
    var flags by remember { mutableStateOf("") }
    var isBroadcast by remember { mutableStateOf(false) }
    var extras by remember { mutableStateOf<List<IntentExtra>>(emptyList()) } // ✅ 新增：Extra 列表
    var resultText by remember { mutableStateOf("准备发送自定义 Intent...") }

    var showAddExtraDialog by remember { mutableStateOf(false) }
    var newExtraKey by remember { mutableStateOf("") }
    var newExtraValue by remember { mutableStateOf("") }
    var newExtraType by remember { mutableStateOf(ExtraType.STRING) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("发送自定义 Intent", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // 👇 可滚动的结果展示区
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== 输入区域 =====

        OutlinedTextField(
            value = action,
            onValueChange = { action = it },
            label = { Text("Action (如: android.intent.action.VIEW)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = component,
            onValueChange = { component = it },
            label = { Text("Component (如: com.example/.MainActivity)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dataUri,
            onValueChange = { dataUri = it },
            label = { Text("Data URI (如: https://www.google.com)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = {
                if (dataUri.isNotEmpty()) {
                    if (dataUri.isNotEmpty()) {
                        if (isUriValid) {
                            Text("✅ URI 格式有效", style = MaterialTheme.typography.labelSmall)
                        } else {
                            Text("⚠️ URI 格式可能无效", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = flags,
            onValueChange = { flags = it },
            label = { Text("Flags (逗号分隔，如: FLAG_ACTIVITY_NEW_TASK)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isBroadcast,
                onCheckedChange = { isBroadcast = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("发送为广播 (否则启动 Activity)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Extra 管理区域
        Text("Extras (${extras.size})", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showAddExtraDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("添加 Extra")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 显示已添加的 Extras
        Column {
            extras.forEachIndexed { index, extra ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(extra.key, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${extra.type}: ${extra.rawValue}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        IconButton(onClick = {
                            extras = extras.toMutableList().apply { removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 发送按钮
        Button(
            onClick = {
                try {
                    val intent = Intent().apply {
                        if (action.isNotEmpty()) setAction(action)

                        if (component.isNotEmpty()) {
                            val comp = ComponentName.unflattenFromString(component)
                            if (comp != null) {
                                setComponent(comp)
                            } else {
                                throw IllegalArgumentException("无效的 Component 格式")
                            }
                        }

                        if (dataUri.isNotEmpty()) {
                            val uri = Uri.parse(dataUri)
                            if (uri.isHierarchical || uri.scheme != null) {
                                setData(uri)
                            } else {
                                throw IllegalArgumentException("无效的 URI: $dataUri")
                            }
                        }

                        // ✅ 添加所有 Extra
                        extras.forEach { extra ->
                            val value = extra.type.formatValue(extra.rawValue)
                            when (value) {
                                is String -> putExtra(extra.key, value)
                                is Boolean -> putExtra(extra.key, value)
                                is Int -> putExtra(extra.key, value)
                                is Long -> putExtra(extra.key, value)
                                is Float -> putExtra(extra.key, value)
                                is Double -> putExtra(extra.key, value)
                                is Uri -> putExtra(extra.key, value)
                            }
                        }

                        flags.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .mapNotNull { flagStr ->
                                try {
                                    Intent::class.java.getField(flagStr).getInt(null)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            .forEach { addFlags(it) }
                    }

                    val ctx = context
                    if (isBroadcast) {
                        ctx.sendBroadcast(intent)
                        resultText = buildString {
                            append("✅ 广播已发送\n")
                            append("Action: $action\n")
                            if (component.isNotEmpty()) append("Component: $component\n")
                            if (dataUri.isNotEmpty()) append("Data: $dataUri\n")
                            if (extras.isNotEmpty()) {
                                append("Extras:\n")
                                extras.forEach {
                                    append("  ${it.key} (${it.type}) = ${it.rawValue}\n")
                                }
                            }
                            if (flags.isNotEmpty()) append("Flags: $flags")
                        }
                    } else {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        ctx.startActivity(intent)
                        resultText = buildString {
                            append("✅ Activity 已启动\n")
                            append("Action: $action\n")
                            if (component.isNotEmpty()) append("Component: $component\n")
                            if (dataUri.isNotEmpty()) append("Data: $dataUri\n")
                            if (extras.isNotEmpty()) {
                                append("Extras:\n")
                                extras.forEach {
                                    append("  ${it.key} (${it.type}) = ${it.rawValue}\n")
                                }
                            }
                            if (flags.isNotEmpty()) append("Flags: $flags")
                        }
                    }
                } catch (e: Exception) {
                    resultText = "❌ 发送失败: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = action.isNotEmpty() || component.isNotEmpty() || dataUri.isNotEmpty() || extras.isNotEmpty()
        ) {
            Text("发送 Intent")
        }
    }

    // ✅ 添加 Extra 对话框
    if (showAddExtraDialog) {
        AlertDialog(
            onDismissRequest = { showAddExtraDialog = false },
            title = { Text("添加 Extra") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newExtraKey,
                        onValueChange = { newExtraKey = it },
                        label = { Text("Key") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val types = ExtraType.values().map { it.toString() }
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = newExtraType.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("类型") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            types.forEach { typeName ->
                                DropdownMenuItem(
                                    text = { Text(typeName) },
                                    onClick = {
                                        newExtraType = ExtraType.valueOf(typeName.uppercase())
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newExtraValue,
                        onValueChange = { newExtraValue = it },
                        label = { Text("值") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newExtraKey.isNotEmpty() && newExtraValue.isNotEmpty()) {
                            extras = extras + IntentExtra(newExtraKey, newExtraType, newExtraValue)
                            newExtraKey = ""
                            newExtraValue = ""
                            newExtraType = ExtraType.STRING
                        }
                        showAddExtraDialog = false
                    }
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExtraDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}