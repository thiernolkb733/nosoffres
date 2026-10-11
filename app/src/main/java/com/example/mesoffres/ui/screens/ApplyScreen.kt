package com.example.mesoffres.ui.screens


import androidx.compose.foundation.layout.statusBarsPadding

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.mesoffres.data.model.Offer
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

private val ApplyBackground = Color(0xFF191A18)
private val ApplySurface = Color(0xFF20211F)
private val ApplyOrange = Color(0xFFE86A3A)
private val ApplyMuted = Color(0xFFAAAAA6)
private val ApplyBorder = Color(0xFF393A36)

/** Écran de préparation de candidature : lettre préremplie, partage et enregistrement local. */
@Composable
fun ApplyScreen(
    offerId: Long,
    viewModel: EmploiViewModel,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val letterModels by viewModel.letters.collectAsStateWithLifecycle()
    val applications by viewModel.applications.collectAsStateWithLifecycle()
    val offer = offers.firstOrNull { it.id == offerId }
    val context = LocalContext.current

    if (offer == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ApplyBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Cette offre n'est plus disponible.", color = Color.White)
            OutlinedButton(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                Text("Retour aux offres")
            }
        }
        return
    }

    // Le brouillon est associé à l'identifiant de l'offre et survit à une rotation de l'écran.
    var letter by rememberSaveable(offer.id) {
        mutableStateOf(createLetterDraft(letterModels, offer))
    }
    var hasEditedLetter by rememberSaveable(offer.id) { mutableStateOf(false) }
    var isSaving by rememberSaveable(offer.id) { mutableStateOf(false) }
    var duplicateMessage by rememberSaveable(offer.id) { mutableStateOf(false) }
    val alreadyApplied = applications.any { it.offerId == offer.id }

    // Les modèles sont chargés depuis Room. Si l'écran s'ouvre avant leur arrivée, on met à jour
    // le brouillon une seule fois, tant que l'utilisateur n'a pas commencé à le modifier.
    LaunchedEffect(offer.id, letterModels) {
        if (!hasEditedLetter && letterModels.isNotEmpty()) {
            letter = createLetterDraft(letterModels, offer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApplyBackground)
    ) {
        // Barre orange et flèche de retour, cohérentes avec les autres écrans.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ApplyOrange)
                .statusBarsPadding()
                .padding(end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Text(
                text = "Préparer ma candidature",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        // Le contenu défile, tandis que les actions de partage et d'enregistrement restent en bas.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = offer.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = offer.company,
                style = MaterialTheme.typography.titleMedium,
                color = ApplyMuted
            )

            if (alreadyApplied || duplicateMessage) {
                Text(
                    text = "Une candidature est déjà enregistrée pour cette offre.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE7B644)
                )
            }

            Text(
                text = "Lettre de candidature",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            OutlinedTextField(
                value = letter,
                onValueChange = {
                    letter = it
                    hasEditedLetter = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp),
                label = { Text("Modifiez votre lettre") },
                minLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = ApplyOrange,
                    unfocusedBorderColor = ApplyBorder,
                    focusedLabelColor = ApplyOrange,
                    unfocusedLabelColor = ApplyMuted,
                    cursorColor = ApplyOrange,
                    focusedContainerColor = ApplySurface,
                    unfocusedContainerColor = ApplySurface
                )
            )

            // Le MVP conserve la candidature hors ligne ; il ne joint pas encore un fichier CV.
            Text(
                text = "La candidature sera enregistrée avec le statut « À envoyer ». L'application ne l'envoie pas à l'entreprise : partagez votre lettre depuis votre téléphone, puis mettez à jour le statut. Le CV n'est pas joint dans cette version.",
                style = MaterialTheme.typography.bodyMedium,
                color = ApplyMuted
            )
            Spacer(Modifier.height(4.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ApplyBackground)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ACTION_SEND ouvre la feuille de partage Android avec le texte de la lettre.
            OutlinedButton(
                onClick = { shareLetter(context, offer, letter) },
                modifier = Modifier.height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Text(" Partager")
            }

            Button(
                onClick = {
                    isSaving = true
                    // Le rappel de navigation ne part qu'après l'écriture réussie dans Room.
                    viewModel.apply(offer) { wasCreated ->
                        isSaving = false
                        if (wasCreated) onSubmitted() else duplicateMessage = true
                    }
                },
                enabled = letter.isNotBlank() && !isSaving && !alreadyApplied,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ApplyOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = when {
                        isSaving -> "Enregistrement…"
                        alreadyApplied -> "Déjà enregistrée"
                        else -> "Enregistrer"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/** Personnalise le modèle stocké dans Room avec les informations de l'offre sélectionnée. */
private fun createLetterDraft(models: List<LetterModel>, offer: Offer): String {
    val oldPlaceholder = "je souhaite mettre mes compétences au service de votre équipe..."
    val preferredModel = models.firstOrNull { it.content.contains(oldPlaceholder) }
        ?: if (offer.type.equals("Stage", ignoreCase = true)) {
            models.firstOrNull { it.title.contains("stage", ignoreCase = true) }
        } else {
            models.firstOrNull { !it.title.contains("stage", ignoreCase = true) }
        }
    val template = (preferredModel ?: models.firstOrNull())?.content
        ?: "Madame, Monsieur,\n\nActuellement étudiant en informatique, je souhaite vous soumettre ma candidature.\n\nJe reste disponible pour un entretien.\n\nCordialement."

    val targetedSentence = "Je vous adresse ma candidature pour le poste de ${offer.title} au sein de ${offer.company}."

    // Si le modèle a déjà une phrase à personnaliser, on la remplace directement.
    if (template.contains(oldPlaceholder)) {
        return template.replace(oldPlaceholder, targetedSentence)
    }

    // Sinon, on insère une phrase liée à l'offre juste après la formule d'appel.
    val greetingEnd = template.indexOf("\n\n")
    return if (greetingEnd >= 0) {
        template.substring(0, greetingEnd + 2) + targetedSentence + "\n\n" +
                template.substring(greetingEnd + 2)
    } else {
        "$template\n\n$targetedSentence"
    }
}

/** Prépare le partage Android sans envoyer la lettre à un destinataire automatiquement. */
private fun shareLetter(context: android.content.Context, offer: Offer, letter: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Candidature : ${offer.title} - ${offer.company}")
        putExtra(Intent.EXTRA_TEXT, letter)
    }
    context.startActivity(Intent.createChooser(sendIntent, "Partager ma lettre"))
}
