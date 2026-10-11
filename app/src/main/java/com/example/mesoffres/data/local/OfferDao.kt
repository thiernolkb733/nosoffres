package com.example.mesoffres.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mesoffres.data.model.Offer
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour toutes les opérations concernant les offres.
 */
@Dao
interface OfferDao {

    @Query("SELECT * FROM offers ORDER BY id DESC")
    fun observeOffers(): Flow<List<Offer>>

    @Query("SELECT * FROM offers WHERE id = :id LIMIT 1")
    suspend fun getOfferById(id: Long): Offer?

    // Insérer une nouvelle offre manuellement.
    @Insert
    suspend fun insertOffer(offer: Offer): Long

    @Insert
    suspend fun insertAll(offers: List<Offer>)

    @Update
    suspend fun update(offer: Offer)


    @Query("DELETE FROM offers WHERE id = :offerId")
    suspend fun deleteOfferById(offerId: Long)


    @Query("DELETE FROM offers")
    suspend fun deleteAll()
}