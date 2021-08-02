package com.dengetelekom.telsiz.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dengetelekom.telsiz.ImageCaptureActivity
import com.dengetelekom.telsiz.R
import com.dengetelekom.telsiz.ReadBarcodeActivity
import com.dengetelekom.telsiz.TransceiverViewModel
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.models.*
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM_TASK = "taskDetail"
private const val REQUEST_IMAGE_CAPTURE = 1
private const val THUMBNAIL_SIZE = 50
private const val MY_CAMERA_PERMISSION_CODE = 100

/**
 * A simple [Fragment] subclass.
 * Use the [TaskDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UrgentNotificationFragment : Fragment() {

    private val MY_CAMERA_REQUEST_CODE = 100
    private lateinit var textExplanation: TextView
    private lateinit var transceiverViewModel: TransceiverViewModel
    private lateinit var imgView: ImageView
    private lateinit var btnSave: Button
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_urgent_notification, container, false)

        textExplanation = view.findViewById(R.id.text_explanation)


        transceiverViewModel = ViewModelProvider(
            requireActivity(),
            TransceiverViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )
        view.findViewById<Button>(R.id.btn_save).setOnClickListener {
            if (isImageSelected()) saveImage()
            else saveExplanation()
        }

        imgView = view.findViewById(R.id.img_photo)
        btnSave = view.findViewById(R.id.btn_save)
        view.findViewById<Button>(R.id.btn_add_photo).setOnClickListener {
            dispatchTakePictureIntent()
        }


        return view
    }

    private fun isImageSelected() = this::currentPhotoPath.isInitialized

    private fun saveExplanation() {
        btnSave.text = getString(R.string.register_completing)
        btnSave.isEnabled = false
        if (textExplanation.text == null) {
            enableSaveButton()
            saveImage()
            return
        }
        val requestModel = UrgentNotificationAddRequestModel(
            "",
            textExplanation.text.toString()
        )
        transceiverViewModel.addUrgentNotification(requestModel).observe(requireActivity(), {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Toast.makeText(
                            requireContext(),
                            resource.data.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        goMainActivity()
                    }
                    Resource.Status.API_ERROR -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                        enableSaveButton()
                    }
                    Resource.Status.ERROR -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.unhandled_error),
                            Toast.LENGTH_SHORT
                        ).show()
                        enableSaveButton()
                    }
                    Resource.Status.LOADING -> {
                        // loadingProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun enableSaveButton() {
        btnSave.text = getString(R.string.btn_save)
        btnSave.isEnabled = true
    }

    private fun goMainActivity() {
        activity?.finish()
    }

    private fun saveImage() {
        transceiverViewModel.uploadUrgentNotificationPhoto(
            if (textExplanation.text == null) "" else textExplanation.text.toString(),
            currentPhotoPath).observe(requireActivity(), {
                it?.let { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            Toast.makeText(requireContext(), resource.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            goMainActivity()
                        }
                        Resource.Status.API_ERROR -> {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                                .show()
                            enableSaveButton()
                        }
                        Resource.Status.ERROR -> {
                            Toast.makeText(requireContext(),
                                "Resim yüklenirken hata oluştu.",
                                Toast.LENGTH_SHORT
                            ).show()
                            enableSaveButton()
                        }
                        Resource.Status.LOADING -> {
                            // loadingProgressBar.visibility = View.VISIBLE
                        }
                    }
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentPhotoPath=  data?.getStringExtra("result").toString()
            imgView.setImageURI(Uri.parse(currentPhotoPath))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), getString(R.string.request_camera_permission_result), Toast.LENGTH_LONG).show()
                openCameraForImage()
            } else {
                Toast.makeText(requireContext(), getString(R.string.request_camera_permission_result_error), Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun dispatchTakePictureIntent() {
        if (checkSelfPermission(requireContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE);
        }else{
            openCameraForImage();
        }


    }

    private fun openCameraForImage() {
        startActivityForResult(
            Intent(activity, ImageCaptureActivity::class.java),
            REQUEST_IMAGE_CAPTURE
        )
    }




}