package com.example.qlghichu.dao

import androidx.room.*
import com.example.qlghichu.Entity.NhiemVu
import kotlinx.coroutines.flow.Flow

@Dao
interface NhiemVuDao {
    @Insert
    suspend fun insert(nhiemVu: NhiemVu): Long

    @Update
    suspend fun update(nhiemVu: NhiemVu)

    @Query("UPDATE nhiem_vu SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteNhiemVu(id: Int)

    @Query("SELECT * FROM nhiem_vu WHERE isDeleted = 0 AND tieuDe LIKE '%' || :searchQuery || '%' ORDER BY isPinned DESC, id DESC")
    fun getAllNhiemVu(searchQuery: String): Flow<List<NhiemVu>>

    @Query("SELECT * FROM nhiem_vu WHERE id = :id")
    suspend fun getNhiemVuById(id: Int): NhiemVu?

    @Query("SELECT * FROM nhiem_vu WHERE isDeleted = 1 ORDER BY id DESC")
    fun getDeletedNhiemVu(): Flow<List<NhiemVu>>

    @Query("UPDATE nhiem_vu SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreNhiemVu(id: Int)

    @Query("DELETE FROM nhiem_vu WHERE id = :id")
    suspend fun deleteNhiemVuForever(id: Int)

    @Query("UPDATE nhiem_vu SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinnedStatus(id: Int, isPinned: Boolean)

    @Query("SELECT * FROM nhiem_vu WHERE isPinned = 1 AND isDeleted = 0 ORDER BY id DESC")
    fun getAllPinnedNhiemVu(): Flow<List<NhiemVu>>
}

