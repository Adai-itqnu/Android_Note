package com.example.qlghichu.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qlghichu.database.DatabaseProvider
import com.example.qlghichu.repository.GhiChuRepository
import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.Entity.NhiemVu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GhiChuViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GhiChuRepository

    init {
        val database = DatabaseProvider.getDatabase(application)
        repository = GhiChuRepository(database.ghiChuDao(), database.nhiemVuDao())
    }

    fun themGhiChu(tieuDe: String, noiDung: String, ngay: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGhiChu(GhiChu(tieuDe = tieuDe, noiDung = noiDung, ngay = ngay))
        }
    }

    fun updateGhiChu(ghiChu: GhiChu) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGhiChu(ghiChu)
        }
    }

    fun deleteGhiChu(ghiChu: GhiChu) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGhiChu(ghiChu)
        }
    }

    suspend fun getGhiChuById(id: Int): GhiChu? {
        return repository.getGhiChuById(id)
    }

    fun themNhiemVu(tieuDe: String, trangThai: String, ngay: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNhiemVu(NhiemVu(tieuDe = tieuDe, trangThai = trangThai, ngay = ngay))
        }
    }

    fun updateNhiemVu(nhiemVu: NhiemVu) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNhiemVu(nhiemVu)
        }
    }

    fun getAllGhiChu(searchQuery: String): Flow<List<GhiChu>> {
        return repository.getAllGhiChu(searchQuery)
    }

    fun getAllNhiemVu(searchQuery: String): Flow<List<NhiemVu>> {
        return repository.getAllNhiemVu(searchQuery)
    }
    fun deleteNhiemVu(nhiemVu: NhiemVu) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNhiemVu(nhiemVu)
        }
    }

    suspend fun getNhiemVuById(id: Int): NhiemVu? {
        return repository.getNhiemVuById(id)
    }
}
