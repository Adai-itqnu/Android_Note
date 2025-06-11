package com.example.qlghichu.Entity

import androidx.room.*

@Entity(
    tableName = "subtask",
    foreignKeys = [ForeignKey(
        entity = NhiemVu::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("taskId")]
)
data class SubTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false
)
