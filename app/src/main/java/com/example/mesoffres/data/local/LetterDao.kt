package com.example.mesoffres.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mesoffres.data.model.LetterModel
import kotlinx.coroutines.flow.Flow


/**
 * DAO des modèles de lettres.
 */
@Dao
interface LetterDao {

    @Query("SELECT * FROM letter_models ORDER BY id DESC")
    fun observeLetters(): Flow<List<LetterModel>>

    @Insert
    suspend fun insertAll(letters: List<LetterModel>)

    // Met à jour le contenu d'un modèle sans créer un nouvel enregistrement.
    @Update
    suspend fun update(letter: LetterModel)
}
