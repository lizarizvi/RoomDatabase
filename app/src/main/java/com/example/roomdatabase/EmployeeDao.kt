package com.example.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert
    suspend fun insert(employeeEntity: EmployeeEntity)

    @Update
    suspend fun update(employeeEntity: EmployeeEntity)

    @Delete
    suspend fun delete(employeeEntity: EmployeeEntity)

    @Query("select*from employee_table")
    fun fetchAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("select*from employee_table where id=:id")
    fun fetchAllEmployees(id: Int): Flow<EmployeeEntity>
}