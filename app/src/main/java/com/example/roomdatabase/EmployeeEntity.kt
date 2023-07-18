package com.example.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "employee_table")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "Name")
    val name: String = "",
    @ColumnInfo(name = "Email_Id")
    val email: String = "",
)
