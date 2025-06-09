package com.example.qlghichu.dao

import androidx.room.*
import com.example.qlghichu.Entity.NhiemVu
import kotlinx.coroutines.flow.Flow

@Dao
interface NhiemVuDao {
    @Insert
    suspend fun insert(nhiemVu: NhiemVu)

    @Update
    suspend fun update(nhiemVu: NhiemVu)

    @Delete
    suspend fun delete(nhiemVu: NhiemVu)

    @Query("SELECT * FROM nhiem_vu WHERE tieuDe LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getAllNhiemVu(searchQuery: String): Flow<List<NhiemVu>>

    @Query("SELECT * FROM nhiem_vu WHERE id = :id")
    suspend fun getNhiemVuById(id: Int): NhiemVu?
}
