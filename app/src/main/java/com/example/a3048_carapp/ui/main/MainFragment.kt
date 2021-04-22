 package com.example.a3048_carapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.a3048_carapp.MapsActivity
import com.example.a3048_carapp.R
import com.example.a3048_carapp.dto.Car
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private lateinit var applicationViewModel: ApplicationViewModel
    internal lateinit var viewModel: MainViewModel
    private val CAMERA_REQUEST_CODE: Int = 1998
    private val SAVE_IMAGE_REQUEST_CODE: Int = 1999
    private val AUTH_REQUEST_CODE = 2002
    private val CAMERA_PERMISSION_REQUEST_CODE: Int = 1997
    private val IMAGE_GALLERY_REQUEST_CODE: Int = 2001
    private lateinit var currentPhotoPath: String
    private var photoURI : Uri? = null
    private var user : FirebaseUser? = null
    var selectedCar: Car = Car("", "")

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        applicationViewModel = ViewModelProvider(this).get(ApplicationViewModel::class.java)

        applicationViewModel.carService.getLocalCarDAO().getAllCars().observe(this, Observer {
            cars -> actCarBrand.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, cars))
        })

        actCarBrand.setOnItemClickListener { parent, view, position, id ->
            selectedCar = parent.getItemAtPosition(position) as Car
        }
        actCarBrand.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCar = Car("", "")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCar = parent!!.getItemAtPosition(position) as Car
            }
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        btnMap.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            startActivity(intent)
        }
        btn_camera.setOnClickListener{
                prepTakePhoto()
        }
        /*
        viewModel.cars.observe(this, Observer {
            cars -> actCarBrand.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, cars))
        })*/
    }
    protected fun prepTakePhoto() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        } else {
            val permissionRequest = arrayOf(Manifest.permission.CAMERA);
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE)

        }
    }

    protected fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
                takePictureIntent -> takePictureIntent.resolveActivity(context!!.packageManager)
            if (takePictureIntent == null) {
                Toast.makeText(context, "Unable to save photo", Toast.LENGTH_LONG).show()
            } else {
                // if we are here, we have a valid intent.
                val photoFile: File = createImageFile()
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(activity!!.applicationContext, "com.a3048_carapp.android.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, SAVE_IMAGE_REQUEST_CODE)
                }
            }
        }
    }

    private fun createImageFile() : File {
        // genererate a unique filename with date.
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // get access to the directory where we can write pictures.
        val storageDir: File? = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PlantDiary${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, let's do stuff.
                    takePhoto()
                } else {
                    Toast.makeText(context, "Unable to take photo without permission", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }

        }
    }
}