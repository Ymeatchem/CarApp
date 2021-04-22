package com.example.a3048_carapp.service

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.room.Room
import com.example.a3048_carapp.RetrofitClientInstance
import com.example.a3048_carapp.dao.CarDatabase
import com.example.a3048_carapp.dao.ICarDAO
import com.example.a3048_carapp.dao.ILocalCarDAO
import com.example.a3048_carapp.dto.Car
import kotlinx.coroutines.*
import retrofit2.Call

class CarService(application: Application) {
    private val application = application

    internal suspend fun fetchCars(carName: String){
        withContext(Dispatchers.IO){
            val service = RetrofitClientInstance.retrofitInstance?.create(ICarDAO::class.java)
            val cars = async {service?.getAllCars()}

            updateLocalCars(cars.await())
        }
    }

    private suspend fun updateLocalCars(cars: Call<ArrayList<Car>>?) {
        // ArrayList<Car>() equals cars
        var sizeOfPlants = ArrayList<Car>().size
        try {
            var localCarDAO = getLocalCarDAO()
            //kotlin.collections.ArrayList<Car>() equals cars
            localCarDAO.insertAll(kotlin.collections.ArrayList<Car>()!!)
        }catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }

    }

    internal fun getLocalCarDAO() : ILocalCarDAO {
        val db = Room.databaseBuilder(application, CarDatabase::class.java, "carlist").build()
        val localCarDAO = db.localCarDAO()
        return localCarDAO
    }

    internal fun save(car: Car) {
        getLocalCarDAO().save(car)
    }
}