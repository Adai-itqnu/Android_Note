package com.example.qlghichu.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ghi_chu")
data class GhiChu(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tieuDe: String,
    val noiDung: String,
    val ngay: String
)