package com.example.mesoffres


import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mesoffres.notifications.NotificationHelper
import com.example.mesoffres.ui.navigation.AppNavigation
import com.example.mesoffres.ui.theme.mesoffresTheme
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

class MainActivity : ComponentActivity() {

    private val emploiViewModel: EmploiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

        setContent {
            val notificationPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { granted ->
                        // La permission a été accordée ou refusée.
                        // Nous pourrons exploiter ce résultat plus tard.
                    }
                )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permission = Manifest.permission.POST_NOTIFICATIONS

                    if (
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(permission)
                    }
                }
            }

            mesoffresTheme {

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(
                        viewModel = emploiViewModel
                    )
                }

            }
        }
    }
}
