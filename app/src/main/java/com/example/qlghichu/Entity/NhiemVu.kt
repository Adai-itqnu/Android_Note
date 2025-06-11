package com.example.qlghichu.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nhiem_vu")
data class NhiemVu(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tieuDe: String,
    val trangThai: String,
    val ngay: String,
    val isDeleted: Boolean = false,
    val isPinned: Boolean = false
)