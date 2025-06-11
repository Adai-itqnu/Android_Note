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

    @Query("UPDATE ghi_chu SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteGhiChu(id: Int)

    @Query("SELECT * FROM ghi_chu WHERE isDeleted = 0 AND (tieuDe LIKE :searchQuery OR noiDung LIKE :searchQuery) ORDER BY isPinned DESC, id DESC")
    fun getAllGhiChu(searchQuery: String): Flow<List<GhiChu>>

    @Query("SELECT * FROM ghi_chu WHERE id = :id")
    suspend fun getGhiChuById(id: Int): GhiChu?

    @Query("SELECT * FROM ghi_chu WHERE isDeleted = 1 ORDER BY id DESC")
    fun getDeletedGhiChu(): Flow<List<GhiChu>>

    @Query("UPDATE ghi_chu SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreGhiChu(id: Int)

    @Query("DELETE FROM ghi_chu WHERE id = :id")
    suspend fun deleteGhiChuForever(id: Int)

    @Query("UPDATE ghi_chu SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinnedStatus(id: Int, isPinned: Boolean)

    @Query("SELECT * FROM ghi_chu WHERE isPinned = 1 AND isDeleted = 0 ORDER BY id DESC")
    fun getAllPinnedGhiChu(): Flow<List<GhiChu>>
}
