package com.example.a3048_carapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.a3048_carapp.dto.Car

@Dao
interface ILocalCarDAO {
    @Query("SELECT * FROM car")
    fun getAllCars() : LiveData<List<Car>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cars: ArrayList<Car>)

    @Delete
    fun delete(car: Car)

    @Insert
    fun save(car: Car)
}