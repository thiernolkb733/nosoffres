package com.example.mesoffres.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.data.model.LetterModel
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val LettersBackground = Color(0xFF191A18)
private val LettersSurface = Color(0xFF20211F)
private val LettersOrange = Color(0xFFE86A3A)
private val LettersMuted = Color(0xFFAAAAA6)
private val LettersBorder = Color(0xFF333430)

/** Bibliothèque locale des modèles de lettres enregistrés dans Room. */
@Composable
fun LettersScreen(
    viewModel: EmploiViewModel,
    onBack: () -> Unit,
    onOpenLetter: (Long) -> Unit
) {
    val letters by viewModel.letters.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LettersBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LettersOrange)
                .padding(end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Text(
                text = "Modèles de lettres",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        if (letters.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Aucun modèle de lettre disponible.", color = LettersMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(letters, key = { it.id }) { letter ->
                    LetterListCard(letter = letter, onClick = { onOpenLetter(letter.id) })
                }
            }
        }
    }
}

/** Carte sélectionnable avec un aperçu court du contenu du modèle. */
@Composable
private fun LetterListCard(letter: LetterModel, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = LettersSurface,
        border = BorderStroke(1.dp, LettersBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = LettersOrange
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = letter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = letter.content.replace('\n', ' '),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LettersMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ouvrir le modèle",
                tint = LettersMuted
            )
        }
    }
}
