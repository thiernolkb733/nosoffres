package com.example.mesoffres.ui.screens


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mesoffres.ui.components.OfferListCard
import com.example.mesoffres.ui.viewmodel.EmploiViewModel

import androidx.compose.material.icons.filled.Delete

import androidx.compose.foundation.layout.width

// Palette propre à cette première maquette : fond charbon et accent orange.
private val OffersBackground = Color(0xFF191A18)
private val OffersFieldSurface = Color(0xFF20211F)
private val OffersOrange = Color(0xFFE86A3A)
private val OffersMuted = Color(0xFFAAAAA6)
private val OffersBorder = Color(0xFF333430)

/**
 * Écran 1 — catalogue d'offres.
 * Les données viennent du ViewModel (donc de Room) et les filtres restent dans l'écran.
 */
@Composable
fun OffersScreen(
    viewModel: EmploiViewModel,
    onOpenOffer: (Long) -> Unit,
    onOpenApplications: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenAddOffer: () -> Unit
){
    // collectAsStateWithLifecycle observe Room tout en respectant le cycle de vie Android.
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.offerSearchQuery.collectAsStateWithLifecycle()

    // rememberSaveable garde les choix si Android recrée l'écran, par exemple après rotation.
    var selectedType by rememberSaveable { mutableStateOf("Tous") }
    var selectedSector by rememberSaveable { mutableStateOf("Tous") }
    var selectedCity by rememberSaveable { mutableStateOf("Toutes") }

    var offerToDelete by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var showDeleteAllDialog by rememberSaveable {
        mutableStateOf(false)
    }


    // Les choix proviennent des offres enregistrées : aucun filtre n'est codé en dur.
    val types = listOf("Tous") + offers.map { it.type }.distinct().sorted()
    val sectors = listOf("Tous") + offers.map { it.sector }.distinct().sorted()
    val cities = listOf("Toutes") + offers.map { it.city }.distinct().sorted()

    // On applique les trois critères avant de dessiner la liste.
    val filteredOffers = offers.filter { offer ->
        (searchQuery.isBlank() || offer.title.contains(searchQuery, ignoreCase = true) ||
                offer.company.contains(searchQuery, ignoreCase = true)) &&
                (selectedType == "Tous" || offer.type.equals(selectedType, ignoreCase = true)) &&
                (selectedSector == "Tous" || offer.sector.equals(selectedSector, ignoreCase = true)) &&
                (selectedCity == "Toutes" || offer.city.equals(selectedCity, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffersBackground)
    ) {
        // Le bandeau orange reprend le titre et le nombre de résultats de la maquette.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(OffersOrange)
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 20.dp)
        ) {


            Text(
                text = "Offres",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "${filteredOffers.size} offre${if (filteredOffers.size > 1) "s" else ""} correspondent à vos filtres",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.88f)
            )
        }


        Button(
            onClick = onOpenAddOffer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OffersOrange
            )
        ) {
            Text(
                text = "+ Ajouter une offre",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }


        // Cette valeur partagée permet aussi au tableau de bord d'ouvrir une recherche préremplie.
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateOfferSearchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            placeholder = { Text("Rechercher un poste ou une entreprise") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    androidx.compose.material3.IconButton(
                        onClick = { viewModel.updateOfferSearchQuery("") }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Effacer la recherche")
                    }
                }
            } else {
                null
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = OffersOrange,
                unfocusedBorderColor = OffersBorder,
                focusedLabelColor = OffersOrange,
                unfocusedLabelColor = OffersMuted,
                cursorColor = OffersOrange,
                focusedContainerColor = OffersFieldSurface,
                unfocusedContainerColor = OffersFieldSurface
            )
        )

        OutlinedButton(
            onClick = {
                showDeleteAllDialog = true
            },
            enabled = offers.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFE86A3A)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tout supprimer")
        }


        // Chaque menu modifie un seul critère ; les trois filtres peuvent se cumuler.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterDropdown(
                label = if (selectedType == "Tous") "Type" else selectedType,
                options = types,
                modifier = Modifier.weight(1f),
                onSelect = { selectedType = it }
            )
            FilterDropdown(
                label = if (selectedSector == "Tous") "Secteur" else selectedSector,
                options = sectors,
                modifier = Modifier.weight(1.2f),
                onSelect = { selectedSector = it }
            )
            FilterDropdown(
                label = if (selectedCity == "Toutes") "Ville" else selectedCity,
                options = cities,
                modifier = Modifier.weight(1f),
                onSelect = { selectedCity = it }
            )
        }

        // La liste prend tout l'espace disponible au-dessus de la navigation basse.
        if (filteredOffers.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune offre ne correspond à ces filtres.", color = OffersMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredOffers, key = { it.id }) { offer ->

                    OfferListCard(
                        offer = offer,
                        onClick = { onOpenOffer(offer.id) },
                        onFavorite = { viewModel.toggleFavorite(offer) },
                        onDelete = { offerToDelete = offer.id }
                    )

                }
            }
        }


        // Confirmation avant la suppression d'une offre.
        if (offerToDelete != null) {
            val selectedOffer = offers.find { it.id == offerToDelete }

            if (selectedOffer != null) {
                AlertDialog(
                    onDismissRequest = { offerToDelete = null },
                    title = {
                        Text("Supprimer cette offre ?")
                    },
                    text = {
                        Text(
                            "Voulez-vous vraiment supprimer " +
                                    "« ${selectedOffer.title} » chez " +
                                    "${selectedOffer.company} ?"
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteOffer(selectedOffer) {
                                    offerToDelete = null
                                }
                            }
                        ) {
                            Text("Supprimer")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { offerToDelete = null }
                        ) {
                            Text("Annuler")
                        }
                    }
                )
            }
        }


        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteAllDialog = false
                },
                title = {
                    Text("Supprimer toutes les offres ?")
                },
                text = {
                    Text(
                        "Cette action supprimera définitivement " +
                                "${offers.size} offre(s). " +
                                "Cette opération est irréversible."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAllOffers {
                                showDeleteAllDialog = false
                            }
                        }
                    ) {
                        Text(
                            "Tout supprimer",
                            color = Color(0xFFE86A3A)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteAllDialog = false
                        }
                    ) {
                        Text("Annuler")
                    }
                }
            )
        }



        // Les trois destinations suivent la maquette : Offres, Candidatures, Tableau de bord.
        OffersBottomBar(
            onOpenOffers = { /* Cet onglet est déjà sélectionné. */ },
            onOpenApplications = onOpenApplications,
            onOpenDashboard = onOpenDashboard
        )
    }
}

/** Un menu déroulant réutilisé pour Type, Secteur et Ville. */
@Composable
private fun FilterDropdown(
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            border = BorderStroke(1.dp, OffersBorder),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(label, maxLines = 1, color = Color.White)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Afficher les choix",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }


    }
}



/** Barre de navigation du MVP, cohérente avec les trois onglets demandés. */
@Composable
private fun OffersBottomBar(
    onOpenOffers: () -> Unit,
    onOpenApplications: () -> Unit,
    onOpenDashboard: () -> Unit
) {
    NavigationBar(containerColor = OffersBackground) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = OffersOrange,
            selectedTextColor = OffersOrange,
            indicatorColor = OffersBackground,
            unselectedIconColor = OffersMuted,
            unselectedTextColor = OffersMuted
        )

        NavigationBarItem(
            selected = true,
            onClick = onOpenOffers,
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Offres", style = MaterialTheme.typography.bodyMedium) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = false,
            onClick = onOpenApplications,
            icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null) },
            label = { Text("Candidatures", style = MaterialTheme.typography.bodyMedium) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = false,
            onClick = onOpenDashboard,
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Tableau de bord", style = MaterialTheme.typography.bodyMedium) },
            colors = itemColors
        )
    }
}
