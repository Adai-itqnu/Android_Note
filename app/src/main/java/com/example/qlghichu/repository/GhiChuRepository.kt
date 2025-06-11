package com.example.qlghichu.repository

import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.Entity.NhiemVu
import com.example.qlghichu.Entity.SubTask
import com.example.qlghichu.dao.GhiChuDao
import com.example.qlghichu.dao.NhiemVuDao
import com.example.qlghichu.dao.SubTaskDao
import kotlinx.coroutines.flow.Flow

class GhiChuRepository(
    private val ghiChuDao: GhiChuDao,
    private val nhiemVuDao: NhiemVuDao,
    private val subTaskDao: SubTaskDao
) {
    // --- GHI CHÚ (NOTE) ---
    suspend fun insertGhiChu(ghiChu: GhiChu) = ghiChuDao.insert(ghiChu)
    suspend fun updateGhiChu(ghiChu: GhiChu) = ghiChuDao.update(ghiChu)
    suspend fun deleteGhiChu(ghiChu: GhiChu) = ghiChuDao.softDeleteGhiChu(ghiChu.id)
    suspend fun restoreGhiChu(id: Int) = ghiChuDao.restoreGhiChu(id)
    suspend fun deleteGhiChuForever(id: Int) = ghiChuDao.deleteGhiChuForever(id)
    suspend fun getGhiChuById(id: Int): GhiChu? = ghiChuDao.getGhiChuById(id)
    fun getAllGhiChu(searchQuery: String): Flow<List<GhiChu>> = ghiChuDao.getAllGhiChu("%$searchQuery%")
    fun getDeletedGhiChu(): Flow<List<GhiChu>> = ghiChuDao.getDeletedGhiChu()
    suspend fun updateGhiChuPinned(id: Int, isPinned: Boolean) = ghiChuDao.updatePinnedStatus(id, isPinned)
    fun getAllPinnedGhiChu(): Flow<List<GhiChu>> = ghiChuDao.getAllPinnedGhiChu()

    // --- NHIỆM VỤ (TASK) ---
    suspend fun insertNhiemVu(nhiemVu: NhiemVu): Long = nhiemVuDao.insert(nhiemVu)
    suspend fun updateNhiemVu(nhiemVu: NhiemVu) = nhiemVuDao.update(nhiemVu)
    suspend fun deleteNhiemVu(nhiemVu: NhiemVu) = nhiemVuDao.softDeleteNhiemVu(nhiemVu.id)
    suspend fun restoreNhiemVu(id: Int) = nhiemVuDao.restoreNhiemVu(id)
    suspend fun deleteNhiemVuForever(id: Int) = nhiemVuDao.deleteNhiemVuForever(id)
    suspend fun getNhiemVuById(id: Int): NhiemVu? = nhiemVuDao.getNhiemVuById(id)
    fun getAllNhiemVu(searchQuery: String): Flow<List<NhiemVu>> = nhiemVuDao.getAllNhiemVu("%$searchQuery%")
    fun getDeletedNhiemVu(): Flow<List<NhiemVu>> = nhiemVuDao.getDeletedNhiemVu()
    suspend fun updateNhiemVuPinned(id: Int, isPinned: Boolean) = nhiemVuDao.updatePinnedStatus(id, isPinned)
    fun getAllPinnedNhiemVu(): Flow<List<NhiemVu>> = nhiemVuDao.getAllPinnedNhiemVu()

    // --- SUBTASK ---
    suspend fun insertSubTask(subTask: SubTask) = subTaskDao.insert(subTask)
    suspend fun updateSubTask(subTask: SubTask) = subTaskDao.update(subTask)
    suspend fun deleteSubTask(subTask: SubTask) = subTaskDao.softDeleteSubTask(subTask.id)
    suspend fun restoreSubTask(id: Int) = subTaskDao.restoreSubTask(id)
    suspend fun deleteSubTaskForever(id: Int) = subTaskDao.deleteForever(id)
    fun getSubTasksForTask(taskId: Int): Flow<List<SubTask>> = subTaskDao.getSubTasksForTask(taskId)
    fun getDeletedSubTasks(): Flow<List<SubTask>> = subTaskDao.getDeletedSubTasks()
}
