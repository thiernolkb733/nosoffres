package com.example.mesoffres.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mesoffres.data.model.JobApplication
import com.example.mesoffres.data.model.LetterModel
import com.example.mesoffres.data.model.Offer

/**
 * Base de données Room principale.
 *
 * Version 2 : ajout de la date de relance sans effacer les données déjà enregistrées.
 */
@Database(
    entities = [
        Offer::class,
        JobApplication::class,
        LetterModel::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun offerDao(): OfferDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun letterDao(): LetterDao
}

/** Migration additive : les anciennes candidatures reçoivent une date de relance vide. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE applications ADD COLUMN followUpDate TEXT NOT NULL DEFAULT ''"
        )
    }
}
