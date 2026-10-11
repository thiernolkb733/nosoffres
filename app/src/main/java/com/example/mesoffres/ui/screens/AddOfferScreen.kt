package com.example.mesoffres.ui.screens


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mesoffres.data.model.Offer
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOfferScreen(
    viewModel: EmploiViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    offerId: Long? = null
) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val existingOffer = offers.firstOrNull { it.id == offerId }
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Conakry") }
    var type by remember { mutableStateOf("Stage") }
    var sector by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    LaunchedEffect(existingOffer) {
        existingOffer?.let { offer ->
            title = offer.title
            company = offer.company
            city = offer.city
            type = offer.type
            sector = offer.sector
            deadline = offer.deadline
            description = offer.description
            contact = offer.contact
        }
    }

    var errorMessage by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (offerId == null) "Ajouter une offre"
                        else "Modifier une offre"
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (offerId == null) {
                    "Enregistre une offre trouvée ailleurs."
                } else {
                    "Modifie les informations de cette offre."
                },
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Intitulé du poste *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Entreprise *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Ville") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type : Stage ou Emploi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = sector,
                onValueChange = { sector = it },
                label = { Text("Secteur") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = { Text("Date limite (ex. 25/12/2026)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact ou lien de l'offre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    if (title.isBlank() || company.isBlank()) {
                        errorMessage =
                            "L'intitulé et l'entreprise sont obligatoires."
                        return@Button
                    }

                    isSaving = true
                    errorMessage = ""

                    val offer = Offer(
                        id = existingOffer?.id ?: 0L,
                        title = title.trim(),
                        company = company.trim(),
                        city = city.trim(),
                        type = type.trim().ifBlank { "Emploi" },
                        sector = sector.trim(),
                        deadline = deadline.trim(),
                        description = description.trim(),
                        contact = contact.trim(),
                        isFavorite = existingOffer?.isFavorite ?: false
                    )

                    if (offerId == null) {
                        // Mode ajout : créer une nouvelle offre.
                        viewModel.addOffer(
                            offer = offer,
                            onSaved = {
                                isSaving = false
                                onSaved()
                            },
                            onError = {
                                isSaving = false
                                errorMessage = "Impossible d'enregistrer l'offre."
                            }
                        )
                    } else {
                        // Mode modification : mettre à jour l'offre existante.
                        viewModel.updateOffer(
                            offer = offer,
                            onSaved = {
                                isSaving = false
                                onSaved()
                            },
                            onError = {
                                isSaving = false
                                errorMessage = "Impossible de modifier l'offre."
                            }
                        )
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Enregistrement..." else "Enregistrer l'offre")
            }
        }
    }
}
