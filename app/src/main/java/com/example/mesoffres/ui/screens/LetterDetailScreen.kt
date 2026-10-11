package com.example.mesoffres.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.data.model.LetterModel
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val LetterDetailBackground = Color(0xFF191A18)
private val LetterDetailSurface = Color(0xFF20211F)
private val LetterDetailOrange = Color(0xFFE86A3A)
private val LetterDetailMuted = Color(0xFFAAAAA6)
private val LetterDetailBorder = Color(0xFF393A36)

/** Éditeur d'un modèle : le texte modifié est enregistré localement et peut être partagé. */
@Composable
fun LetterDetailScreen(
    letterId: Long,
    viewModel: EmploiViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val letters by viewModel.letters.collectAsStateWithLifecycle()
    val letter = letters.firstOrNull { it.id == letterId }
    val context = LocalContext.current

    if (letter == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LetterDetailBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ce modèle n'est plus disponible.", color = Color.White)
            OutlinedButton(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                Text("Retour aux modèles")
            }
        }
        return
    }

    // rememberSaveable garde le texte édité si Android recrée l'écran, par exemple après rotation.
    var content by rememberSaveable(letter.id) { mutableStateOf(letter.content) }
    var isSaving by rememberSaveable(letter.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LetterDetailBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LetterDetailOrange)
                .statusBarsPadding()
                .padding(end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Text(
                text = "Modifier la lettre",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = letter.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "Personnalisez le texte avant de le partager.",
                style = MaterialTheme.typography.bodyMedium,
                color = LetterDetailMuted
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                label = { Text("Contenu de la lettre") },
                minLines = 12,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = LetterDetailOrange,
                    unfocusedBorderColor = LetterDetailBorder,
                    focusedLabelColor = LetterDetailOrange,
                    unfocusedLabelColor = LetterDetailMuted,
                    cursorColor = LetterDetailOrange,
                    focusedContainerColor = LetterDetailSurface,
                    unfocusedContainerColor = LetterDetailSurface
                )
            )
            Text(
                text = "Les modifications sont conservées sur cet appareil.",
                style = MaterialTheme.typography.bodyMedium,
                color = LetterDetailMuted
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LetterDetailBackground)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Le partage utilise le contenu courant, même si l'utilisateur n'a pas encore enregistré.
            OutlinedButton(
                onClick = { shareLetterModel(context, letter, content) },
                modifier = Modifier.height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Text(" Partager")
            }

            Button(
                onClick = {
                    isSaving = true
                    // On conserve l'identifiant et le titre ; seul le contenu modifié est envoyé à Room.
                    viewModel.updateLetter(letter.copy(content = content)) {
                        isSaving = false
                        onSaved()
                    }
                },
                enabled = content.isNotBlank() && !isSaving,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LetterDetailOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isSaving) "Enregistrement…" else "Enregistrer",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/** Ouvre le menu de partage Android et laisse l'utilisateur choisir l'application destinataire. */
private fun shareLetterModel(context: Context, letter: LetterModel, content: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, letter.title)
        putExtra(Intent.EXTRA_TEXT, content)
    }
    context.startActivity(Intent.createChooser(sendIntent, "Partager le modèle"))
}
