package com.example.a3048_carapp.ui.main

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.a3048_carapp.MapsActivity
import kotlinx.android.synthetic.main.main_fragment.*
import com.example.a3048_carapp.R
import com.example.a3048_carapp.dto.Photo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private var photos : ArrayList<Photo> = ArrayList<Photo>()
    private var photoURI : Uri? = null
    private var user : FirebaseUser? = null

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

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        btnMap.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            startActivity(intent)
        }
        /*
        viewModel.cars.observe(this, Observer {
            cars -> actCarBrand.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, cars))
        })*/
    }
    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
            takePictureIntent -> takePictureIntent.resolveActivity(context!!.packageManager)
            if (takePictureIntent == null) {
                Toast.makeText(context, "Unable to save photo", Toast.LENGTH_LONG).show()
            } else {
                // if we are here, we have a valid intent.
                val photoFile: File = createImageFile()
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(activity!!.applicationContext, "com.myplantdiary.android.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, SAVE_IMAGE_REQUEST_CODE)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE)  {
                // now we can get the thumbnail
                val imageBitmap = data!!.extras!!.get("data") as Bitmap
            } else if (requestCode == SAVE_IMAGE_REQUEST_CODE) {
                Toast.makeText(context, "Image Saved", Toast.LENGTH_LONG).show()
                var photo = Photo(localUri = photoURI.toString())
                photos.add(photo)
            } else if (requestCode == IMAGE_GALLERY_REQUEST_CODE) {
                if (data != null && data.data != null) {
                    val image = data.data
                    val source = ImageDecoder.createSource(activity!!.contentResolver, image!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                }
            } else if (requestCode == AUTH_REQUEST_CODE) {
                user = FirebaseAuth.getInstance().currentUser
            }
        }
    }
    private fun createImageFile() : File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir:File? = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("CarApp${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
}