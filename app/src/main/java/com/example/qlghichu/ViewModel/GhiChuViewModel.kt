package com.example.qlghichu.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qlghichu.database.DatabaseProvider
import com.example.qlghichu.repository.GhiChuRepository
import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.Entity.NhiemVu
import com.example.qlghichu.Entity.SubTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GhiChuViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GhiChuRepository

    init {
        val db = DatabaseProvider.getDatabase(application)
        repository = GhiChuRepository(db.ghiChuDao(), db.nhiemVuDao(), db.subTaskDao())
    }

    // --- Ghi chú ---
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

    fun restoreGhiChu(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restoreGhiChu(id)
        }
    }

    fun deleteGhiChuForever(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGhiChuForever(id)
        }
    }

    suspend fun getGhiChuById(id: Int): GhiChu? = repository.getGhiChuById(id)
    fun getAllGhiChu(searchQuery: String): Flow<List<GhiChu>> = repository.getAllGhiChu(searchQuery)
    fun getDeletedGhiChu(): Flow<List<GhiChu>> = repository.getDeletedGhiChu()
    fun updateGhiChuPinned(id: Int, isPinned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.getAllPinnedGhiChu().first().size
            if (isPinned || count < 3) {
                repository.updateGhiChuPinned(id, isPinned)
            }
        }
    }

    // --- Nhiệm vụ ---
    fun themNhiemVu(
        tieuDe: String,
        trangThai: String,
        ngay: String,
        onSuccess: (newTaskId: Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repository.insertNhiemVu(
                NhiemVu(tieuDe = tieuDe, trangThai = trangThai, ngay = ngay)
            )
            withContext(Dispatchers.Main) {
                onSuccess(newId.toInt())
            }
        }
    }

    fun updateNhiemVu(nhiemVu: NhiemVu) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNhiemVu(nhiemVu)
        }
    }

    fun deleteNhiemVu(nhiemVu: NhiemVu) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNhiemVu(nhiemVu)
        }
    }

    fun restoreNhiemVu(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restoreNhiemVu(id)
        }
    }

    fun deleteNhiemVuForever(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNhiemVuForever(id)
        }
    }

    suspend fun getNhiemVuById(id: Int): NhiemVu? = repository.getNhiemVuById(id)
    fun getAllNhiemVu(searchQuery: String): Flow<List<NhiemVu>> =
        repository.getAllNhiemVu(searchQuery)

    fun getDeletedNhiemVu(): Flow<List<NhiemVu>> = repository.getDeletedNhiemVu()
    fun updateNhiemVuPinned(id: Int, isPinned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.getAllPinnedNhiemVu().first().size
            if (isPinned || count < 3) {
                repository.updateNhiemVuPinned(id, isPinned)
            }
        }
    }

    // --- SubTask ---
    fun getSubTasksForTask(taskId: Int): Flow<List<SubTask>> = repository.getSubTasksForTask(taskId)

    fun addSubTask(taskId: Int, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSubTask(SubTask(taskId = taskId, title = title))
            updateTaskStatusFromSubtasks(taskId)
        }
    }

    fun themSubTask(subTask: SubTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSubTask(subTask)
            updateTaskStatusFromSubtasks(subTask.taskId)
        }
    }

    fun updateSubTask(subTask: SubTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSubTask(subTask)
            updateTaskStatusFromSubtasks(subTask.taskId)
        }
    }

    fun deleteSubTask(subTask: SubTask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubTask(subTask)
            updateTaskStatusFromSubtasks(subTask.taskId)
        }
    }

    fun restoreSubTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restoreSubTask(id)
        }
    }

    fun deleteSubTaskForever(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubTaskForever(id)
        }
    }

    private fun updateTaskStatusFromSubtasks(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val subTasks = repository.getSubTasksForTask(taskId).first()
            val isAllCompleted = subTasks.isNotEmpty() && subTasks.all { it.isCompleted }
            val task = repository.getNhiemVuById(taskId)
            task?.let {
                if (isAllCompleted && it.trangThai != "Hoàn thành") {
                    repository.updateNhiemVu(it.copy(trangThai = "Hoàn thành"))
                } else if (!isAllCompleted && it.trangThai == "Hoàn thành") {
                    repository.updateNhiemVu(it.copy(trangThai = "Chưa hoàn thành"))
                }
            }
        }
    }
}
