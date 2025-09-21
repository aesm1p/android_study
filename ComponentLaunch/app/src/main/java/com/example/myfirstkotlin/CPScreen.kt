@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myfirstkotlin

import android.content.ContentValues
import android.os.Bundle
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown // ✅ 修复：补充缺失图标
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.text.contains

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CPScreen(navController: NavController) {
    var uri by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("query") }
    var selection by remember { mutableStateOf("") }
    var selectionArgs by remember { mutableStateOf("") }
    var valuesJson by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var argsJson by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("准备操作 ContentProvider...") }

    // ✅ 权限管理
//    var permissions by remember { mutableStateListOf<String>() }
    var permissions by remember { mutableStateOf(mutableListOf<String>()) }
    var showAddPermissionDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    // 权限请求状态
    val permissionsState = rememberMultiplePermissionsState(permissions.toList())

    val handleAddPermission: (String) -> Unit = { perm ->
        val trimmed = perm.trim()
        if (trimmed.isNotBlank() && !permissions.contains(trimmed)) {
            permissions.add(trimmed)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp), // 防止底部按钮被系统栏遮挡
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ===== 标题 =====
        item {
            Text("ContentProvider", style = MaterialTheme.typography.headlineMedium)
        }

        // ===== 可滚动的结果展示区 =====
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // 👈 固定高度（推荐）或使用 weight + 父容器约束
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()) // ✅ 内部滚动
            ) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ===== 复制按钮 =====
        if (resultText.isNotEmpty() && resultText != "准备操作 ContentProvider...") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(resultText))
                            Toast.makeText(context, "✅ 已复制到剪贴板", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("复制结果", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // ===== 输入区域 =====
        item {
            OutlinedTextField(
                value = uri,
                onValueChange = { uri = it },
                label = { Text("URI (如: content://contacts/people)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            DropdownTextField(
                label = "操作类型",
                items = listOf("query", "insert", "update", "delete", "call"),
                selectedItem = operation,
                onItemSelected = { operation = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = selection,
                onValueChange = { selection = it },
                label = { Text("Selection (WHERE子句)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = selectionArgs,
                onValueChange = { selectionArgs = it },
                label = { Text("Selection Args (逗号分隔)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = valuesJson,
                onValueChange = { valuesJson = it },
                label = { Text("Values (JSON格式，用于 insert/update)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                supportingText = {
                    if (operation in listOf("insert", "update") && valuesJson.isNotEmpty()) {

                        Json.parseToJsonElement(valuesJson)
                        Text("✅ JSON 格式有效", style = MaterialTheme.typography.labelSmall)

                    }
                }
            )
        }

        item {
            OutlinedTextField(
                value = method,
                onValueChange = { method = it },
                label = { Text("Method (用于 call)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = operation == "call"
            )
        }

        item {
            OutlinedTextField(
                value = argsJson,
                onValueChange = { argsJson = it },
                label = { Text("Args Bundle (JSON格式，用于 call)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = operation == "call",
                supportingText = {
                    if (operation == "call" && argsJson.isNotEmpty()) {
                        Json.parseToJsonElement(argsJson)
                        Text("✅ JSON 格式有效", style = MaterialTheme.typography.labelSmall)

                    }
                }
            )
        }

        // ===== 权限管理 =====
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "所需权限 (${permissions.size})", // ✅ 修复：size 不是函数
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { showAddPermissionDialog = true },
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("添加")
                }
            }
        }

        // ===== 权限列表 =====
        item {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(8.dp)
                    )
            ) {
                if (permissions.isEmpty()) {
                    item {
                        Text(
                            "暂无权限，点击“添加”输入如 android.permission.READ_CONTACTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                } else {
                    itemsIndexed(permissions) { index, permission ->
                        PermissionItem(
                            permission = permission,
                            onRemove = { idx ->
                                (permissions as MutableList<String>).removeAt(idx)
                            },
                            index = index
                        )
                    }
                }
            }
        }

        // ===== 执行按钮 =====
        item {
            Button(
                onClick = {
                    if (permissions.isNotEmpty() && !permissionsState.allPermissionsGranted) {
                        permissionsState.launchMultiplePermissionRequest()
                        scope.launch {
                            snapshotFlow { permissionsState.allPermissionsGranted }
                                .distinctUntilChanged()
                                .collect { granted ->
                                    if (granted) {
                                        performOperation(
                                            context = context,
                                            uri = uri,
                                            operation = operation,
                                            selection = selection,
                                            selectionArgs = selectionArgs,
                                            valuesJson = valuesJson,
                                            method = method,
                                            argsJson = argsJson,
                                            onResult = { resultText = it }
                                        )
                                    } else {
                                        resultText = "❌ 部分权限被拒绝，操作可能失败"
                                    }
                                }
                        }
                    } else {
                        performOperation(
                            context = context,
                            uri = uri,
                            operation = operation,
                            selection = selection,
                            selectionArgs = selectionArgs,
                            valuesJson = valuesJson,
                            method = method,
                            argsJson = argsJson,
                            onResult = { resultText = it }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uri.isNotEmpty()
            ) {
                Text("执行操作")
            }
        }
    }

    // ✅ 添加权限对话框
    if (showAddPermissionDialog) {
        AddPermissionDialog(
            onAddPermission = handleAddPermission,
            onDismiss = { showAddPermissionDialog = false }
        )
    }
}



@Composable
fun PermissionItem(
    permission: String,
    onRemove: (Int) -> Unit,
    index: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                permission,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onRemove(index) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ✅ 稳定版下拉菜单
@Composable
fun DropdownTextField(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown, // ✅ 修复图标引用
                    contentDescription = "Toggle dropdown",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// ✅ 添加权限对话框
@Composable
fun AddPermissionDialog(
    onAddPermission: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newPermission by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加权限") },
        text = {
            Column {
                OutlinedTextField(
                    value = newPermission,
                    onValueChange = { newPermission = it },
                    label = { Text("权限全名 (如: android.permission.READ_CONTACTS)") },
                    placeholder = { Text("android.permission.XXX") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "请输入完整的权限名称，例如：\nandroid.permission.WRITE_EXTERNAL_STORAGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPermission.isNotBlank()) {
                        onAddPermission(newPermission.trim())
                        newPermission = ""
                    }
                    onDismiss()
                }
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

// ✅ 抽离执行逻辑，避免嵌套 launch 导致作用域混乱
private fun performOperation(
    context: android.content.Context,
    uri: String,
    operation: String,
    selection: String,
    selectionArgs: String,
    valuesJson: String,
    method: String,
    argsJson: String,
    onResult: (String) -> Unit
) {
    try {
        executeCpOperation(
            context = context,
            uri = uri,
            operation = operation,
            selection = selection,
            selectionArgs = selectionArgs,
            valuesJson = valuesJson,
            method = method,
            argsJson = argsJson,
            onResult = onResult
        )
    } catch (e: Exception) {
        onResult("❌ 操作异常: ${e.message}")
    }
}

// ✅ 核心执行函数
private fun executeCpOperation(
    context: android.content.Context,
    uri: String,
    operation: String,
    selection: String,
    selectionArgs: String,
    valuesJson: String,
    method: String,
    argsJson: String,
    onResult: (String) -> Unit
) {
    try {
        val contentResolver = context.contentResolver
        val parsedUri = Uri.parse(uri)

        when (operation) {
            "query" -> {
                val cursor = contentResolver.query(
                    parsedUri,
                    null,
                    selection.ifEmpty { null },
                    selectionArgs.takeIf { it.isNotEmpty() }?.split(",")?.toTypedArray(),
                    null
                )
                cursor?.use {
                    val cols = it.columnCount
                    val sb = StringBuilder("✅ 查询到 ${it.count} 行:\n")
                    val colNames = (0 until cols).map { i -> it.getColumnName(i) }

                    while (it.moveToNext()) {
                        sb.append("{ ")
                        colNames.forEachIndexed { index, colName ->
                            sb.append("$colName: ${it.getString(index)}")
                            if (index < colNames.size - 1) sb.append(", ")
                        }
                        sb.append(" }\n")
                    }
                    onResult(sb.toString())
                } ?: run {
                    onResult("❌ 查询失败或返回空 Cursor")
                }
            }

            "insert" -> {
                val values = ContentValues().apply {
                    if (valuesJson.isNotEmpty()) {
                        try {
                            val element = Json.parseToJsonElement(valuesJson)
                            if (element !is JsonObject) throw IllegalArgumentException("JSON 必须是对象")
                            element.forEach { (key, value) ->
                                put(key, value.asContentValueString())
                            }
                        } catch (e: Exception) {
                            throw IllegalArgumentException("JSON 解析失败: ${e.message}")
                        }
                    }
                }
                val newUri = contentResolver.insert(parsedUri, values)
                onResult("✅ 插入成功，返回 URI: $newUri")
            }

            "update" -> {
                val values = ContentValues().apply {
                    if (valuesJson.isNotEmpty()) {
                        try {
                            val element = Json.parseToJsonElement(valuesJson)
                            if (element !is JsonObject) throw IllegalArgumentException("JSON 必须是对象")
                            element.forEach { (key, value) ->
                                put(key, value.asContentValueString())
                            }
                        } catch (e: Exception) {
                            throw IllegalArgumentException("JSON 解析失败: ${e.message}")
                        }
                    }
                }
                val count = contentResolver.update(
                    parsedUri,
                    values,
                    selection.ifEmpty { null },
                    selectionArgs.takeIf { it.isNotEmpty() }?.split(",")?.toTypedArray()
                )
                onResult("✅ 更新成功，影响 $count 行")
            }

            "delete" -> {
                val count = contentResolver.delete(
                    parsedUri,
                    selection.ifEmpty { null },
                    selectionArgs.takeIf { it.isNotEmpty() }?.split(",")?.toTypedArray()
                )
                onResult("✅ 删除成功，影响 $count 行")
            }

            "call" -> {
                val bundle = Bundle().apply {
                    if (argsJson.isNotEmpty()) {
                        try {
                            val element = Json.parseToJsonElement(argsJson)
                            if (element !is JsonObject) throw IllegalArgumentException("JSON 必须是对象")
                            element.forEach { (key, value) ->
                                putString(key, value.asContentValueString())
                            }
                        } catch (e: Exception) {
                            throw IllegalArgumentException("JSON 解析失败: ${e.message}")
                        }
                    }
                }
                val resultBundle = contentResolver.call(parsedUri, method, null, bundle)
                onResult("✅ Call 成功，返回 Bundle: $resultBundle")
            }

            else -> onResult("❌ 不支持的操作: $operation")
        }
    } catch (e: Exception) {
        onResult("❌ 操作失败: ${e.message ?: "未知错误"}")
    }
}

// ✅ JsonElement 扩展函数
private fun JsonElement.asContentValueString(): String? = when (this) {
    is JsonPrimitive -> {
        if (isString) content else toString()
    }
    is JsonNull -> null
    else -> toString() // JsonArray / JsonObject
}