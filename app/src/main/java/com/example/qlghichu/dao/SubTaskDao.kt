package com.example.qlghichu.dao

import androidx.room.*
import com.example.qlghichu.Entity.SubTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {
    @Insert
    suspend fun insert(subTask: SubTask): Long

    @Update
    suspend fun update(subTask: SubTask)

    @Query("UPDATE subtask SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteSubTask(id: Int)

    @Query("SELECT * FROM subtask WHERE taskId = :taskId AND isDeleted = 0")
    fun getSubTasksForTask(taskId: Int): Flow<List<SubTask>>

    @Query("SELECT * FROM subtask WHERE isDeleted = 1 ORDER BY id DESC")
    fun getDeletedSubTasks(): Flow<List<SubTask>>

    @Query("UPDATE subtask SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreSubTask(id: Int)

    @Query("DELETE FROM subtask WHERE id = :id")
    suspend fun deleteForever(id: Int)
}
