package com.example.mesoffres.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.ui.util.UpcomingDeadline
import com.example.mesoffres.ui.util.buildUpcomingDeadlines
import com.example.mesoffres.ui.viewmodel.EmploiViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val RemindersBackground = Color(0xFF191A18)
private val RemindersSurface = Color(0xFF20211F)
private val RemindersOrange = Color(0xFFE86A3A)
private val RemindersMuted = Color(0xFFAAAAA6)

/** Écran des dates limites à venir pour les offres et les candidatures actives. */
@Composable
fun RemindersScreen(
    viewModel: EmploiViewModel,
    onBack: () -> Unit
) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val applications by viewModel.applications.collectAsStateWithLifecycle()
    val today = LocalDate.now()

    // On réutilise le même calcul que le tableau de bord afin que les deux écrans restent cohérents.
    val deadlines = remember(offers, applications, today) {
        buildUpcomingDeadlines(
            offers = offers,
            applications = applications,
            limit = Int.MAX_VALUE,
            today = today
        )
    }
    val thisWeek = deadlines.filter { ChronoUnit.DAYS.between(today, it.date) <= 7 }
    val later = deadlines.filter { ChronoUnit.DAYS.between(today, it.date) > 7 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RemindersBackground)
    ) {
        // Bandeau de navigation de l'écran secondaire.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RemindersOrange)
                .padding(end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Text(
                text = "Rappels",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (deadlines.isEmpty()) {
                item {
                    Text(
                        text = "Aucune date limite à venir.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = RemindersMuted
                    )
                }
            } else {
                if (thisWeek.isNotEmpty()) {
                    item { ReminderSectionTitle("Cette semaine") }
                    items(thisWeek, key = { "week-${it.company}-${it.title}-${it.date}" }) { deadline ->
                        ReminderCard(deadline = deadline, today = today)
                    }
                }

                if (later.isNotEmpty()) {
                    item { ReminderSectionTitle("À venir") }
                    items(later, key = { "later-${it.company}-${it.title}-${it.date}" }) { deadline ->
                        ReminderCard(deadline = deadline, today = today)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = Modifier.padding(top = 6.dp)
    )
}

/** Présente l'entreprise, la date limite et le temps restant. */
@Composable
private fun ReminderCard(deadline: UpcomingDeadline, today: LocalDate) {
    val daysRemaining = ChronoUnit.DAYS.between(today, deadline.date)
    val countdown = when (daysRemaining) {
        0L -> "Aujourd'hui"
        1L -> "Demain"
        else -> "Dans $daysRemaining jours"
    }
    val sourceLabel = if (deadline.kind == "Relance") "Relance de candidature"
    else deadline.applicationStatus?.let { "Candidature · $it" } ?: "Offre à surveiller"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = RemindersSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Date limite",
                tint = if (daysRemaining <= 2) Color(0xFFE79A9D) else RemindersOrange
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = deadline.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "${deadline.kind} · ${deadline.company} · ${deadline.dateLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RemindersMuted
                )
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF131412),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = sourceLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFE0E0DC),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                    Text(
                        text = countdown,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (daysRemaining <= 2) Color(0xFFE79A9D) else RemindersOrange,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
    }
}
