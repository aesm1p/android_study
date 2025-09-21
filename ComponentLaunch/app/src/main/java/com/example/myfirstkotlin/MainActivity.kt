package com.example.myfirstkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                startDestination = "Intent",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("Intent") { IntentScreen(navController) }
                composable("CMD") { CMDSceen(navController) }
                composable("CP") { CPScreen(navController) }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val items = listOf("Intent" to "Intent", "CMD" to "CMD", "CP" to "CP")
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



