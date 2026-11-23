package com.example.weather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM city ORDER BY name")
    fun observeAll(): Flow<List<CityEntity>>

    @Query("SELECT * FROM city WHERE id = :id")
    suspend fun getById(id: Long): CityEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: CityEntity): Long

    @Query("DELETE FROM city WHERE id = :id")
    suspend fun delete(id: Long)
}
