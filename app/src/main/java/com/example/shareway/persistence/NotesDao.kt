package com.example.shareway.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shareway.models.Article
import com.example.shareway.models.Category
import com.example.shareway.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("SELECT * FROM notes WHERE articleUrl =:articleURL")
    fun getNotes(articleURL: String): Flow<List<Note>>
}