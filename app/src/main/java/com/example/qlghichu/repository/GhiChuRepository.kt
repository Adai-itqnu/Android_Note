package com.example.qlghichu.repository

import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.Entity.NhiemVu
import com.example.qlghichu.dao.GhiChuDao
import com.example.qlghichu.dao.NhiemVuDao
import kotlinx.coroutines.flow.Flow

class GhiChuRepository(
    private val ghiChuDao: GhiChuDao,
    private val nhiemVuDao: NhiemVuDao
) {
    suspend fun insertGhiChu(ghiChu: GhiChu) {
        ghiChuDao.insert(ghiChu)
    }

    suspend fun updateGhiChu(ghiChu: GhiChu){
        ghiChuDao.update(ghiChu)
    }

    suspend fun deleteGhiChu(ghiChu: GhiChu) {
        ghiChuDao.delete(ghiChu)
    }

    suspend fun getGhiChuById(id: Int): GhiChu? {
        return ghiChuDao.getGhiChuById(id)
    }

    suspend fun insertNhiemVu(nhiemVu: NhiemVu) {
        nhiemVuDao.insert(nhiemVu)
    }

    suspend fun updateNhiemVu(nhiemVu: NhiemVu) {
        nhiemVuDao.update(nhiemVu)
    }

    fun getAllGhiChu(searchQuery: String): Flow<List<GhiChu>> {
        return ghiChuDao.getAllGhiChu("%$searchQuery%")
    }

    fun getAllNhiemVu(searchQuery : String): Flow<List<NhiemVu>> {
        return nhiemVuDao.getAllNhiemVu(searchQuery)
    }
}