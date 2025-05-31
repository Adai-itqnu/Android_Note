package com.example.qlghichu.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.qlghichu.Entity.GhiChu
import kotlinx.coroutines.flow.Flow

@Dao
interface GhiChuDao {
    @Insert
    suspend fun insert(ghiChu: GhiChu)

    @Update
    suspend fun update(ghiChu: GhiChu)

    @Delete
    suspend fun delete(ghiChu: GhiChu)

    @Query("SELECT * FROM ghi_chu WHERE tieuDe LIKE :searchQuery OR noiDung LIKE :searchQuery")
    fun getAllGhiChu(searchQuery: String): Flow<List<GhiChu>>

    @Query("SELECT * FROM ghi_chu WHERE id = :id")
    suspend fun getGhiChuById(id: Int): GhiChu?
}