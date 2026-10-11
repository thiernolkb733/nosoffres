package com.example.mesoffres.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mesoffres.data.model.JobApplication
import kotlinx.coroutines.flow.Flow


/**
 * DAO des candidatures.
 */
@Dao
interface ApplicationDao {

    @Query("SELECT * FROM applications ORDER BY id DESC")
    fun observeApplications(): Flow<List<JobApplication>>

    @Insert
    suspend fun insert(application: JobApplication)

    // Permet de savoir si l'offre possède déjà une candidature avant d'en créer une autre.
    @Query("SELECT * FROM applications WHERE offerId = :offerId LIMIT 1")
    suspend fun findForOffer(offerId: Long): JobApplication?

    @Query("UPDATE applications SET status = :status, dateApplied = CASE WHEN :status = 'Envoyée' THEN :sentDate ELSE dateApplied END, followUpDate = CASE WHEN :status = 'Envoyée' THEN :followUpDate ELSE '' END WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, sentDate: String, followUpDate: String)


    @Query("DELETE FROM applications WHERE offerId = :offerId")
    suspend fun deleteApplicationsForOffer(offerId: Long)

    @Query("DELETE FROM applications")
    suspend fun deleteAllApplications()
}
