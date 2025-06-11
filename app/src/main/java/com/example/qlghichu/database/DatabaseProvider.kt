package com.example.qlghichu.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qlghichu.Entity.GhiChu
import com.example.qlghichu.Entity.NhiemVu
import com.example.qlghichu.Entity.SubTask
import com.example.qlghichu.dao.GhiChuDao
import com.example.qlghichu.dao.NhiemVuDao
import com.example.qlghichu.dao.SubTaskDao

@Database(entities = [GhiChu::class, NhiemVu::class,SubTask::class], version = 2, exportSchema = false)
abstract class GhiChuDatabase : RoomDatabase() {
    abstract fun ghiChuDao(): GhiChuDao
    abstract fun nhiemVuDao(): NhiemVuDao
    abstract fun subTaskDao(): SubTaskDao
}

object DatabaseProvider {
    @Volatile
    private var INSTANCE: GhiChuDatabase? = null

    fun getDatabase(context: Context): GhiChuDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                GhiChuDatabase::class.java,
                "ghi_chu_database"
            )
                .fallbackToDestructiveMigration() // Cho phép xóa database cũ khi thay đổi cấu trúc
                .build()
            INSTANCE = instance
            instance
        }
    }
}
