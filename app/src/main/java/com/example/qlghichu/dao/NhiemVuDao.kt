package com.example.qlghichu.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.qlghichu.Entity.NhiemVu
import kotlinx.coroutines.flow.Flow

@Dao
interface NhiemVuDao {
    @Insert
    suspend fun insert(nhiemVu: NhiemVu)

    @Update
    suspend fun update(nhiemVu: NhiemVu)

    @Query("SELECT * FROM nhiem_vu WHERE tieuDe LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun getAllNhiemVu(searchQuery: String): Flow<List<NhiemVu>>
}