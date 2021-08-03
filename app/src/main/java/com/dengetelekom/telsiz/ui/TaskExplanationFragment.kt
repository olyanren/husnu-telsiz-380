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
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dengetelekom.telsiz.ImageCaptureActivity
import com.dengetelekom.telsiz.R
import com.dengetelekom.telsiz.TransceiverViewModel
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.helpers.SharedPreferencesUtil
import com.dengetelekom.telsiz.models.ExplanationAddRequestModel
import com.dengetelekom.telsiz.models.ExplanationTitleModel
import com.dengetelekom.telsiz.models.Resource
import com.dengetelekom.telsiz.models.TaskModel
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import com.google.gson.Gson
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM_TASK = "taskDetail"
private const val REQUEST_IMAGE_CAPTURE = 1
private const val THUMBNAIL_SIZE = 50

private val MY_CAMERA_REQUEST_CODE = 100

/**
 * A simple [Fragment] subclass.
 * Use the [TaskDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskExplanationFragment : Fragment() {

    private lateinit var taskModel: TaskModel
    private lateinit var spinnerTitles: Spinner
    private lateinit var textExplanation: TextView
    private lateinit var transceiverViewModel: TransceiverViewModel
    private lateinit var imgView: ImageView
    private lateinit var btnSave: Button
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskModel = SharedPreferencesUtil.readTask(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_explanation, container, false)
        spinnerTitles = view.findViewById(R.id.titles)
        textExplanation = view.findViewById(R.id.text_explanation)
        textExplanation.text = taskModel.explanation

        transceiverViewModel = ViewModelProvider(
            requireActivity(),
            TransceiverViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )
        view.findViewById<Button>(R.id.btn_save).setOnClickListener {
            saveExplanation()
        }

        imgView = view.findViewById(R.id.img_photo)
        btnSave = view.findViewById(R.id.btn_save)
        view.findViewById<Button>(R.id.btn_add_photo).setOnClickListener {
            dispatchTakePictureIntent()
        }

        initViews(view)
        initExplanationTitles(view)
        return view
    }

    private fun enableSaveButton() {
        btnSave.text = getString(R.string.btn_save)
        btnSave.isEnabled = true
    }

    private fun disableSaveButton() {
        btnSave.text = getString(R.string.register_completing)
        btnSave.isEnabled = false
    }

    private fun saveExplanation() {
        disableSaveButton()
        if (textExplanation.text == null || spinnerTitles.selectedItem == null) {
            Toast.makeText(requireContext(), R.string.message_saved_explanation, Toast.LENGTH_SHORT)
                .show()
            enableSaveButton()
            saveImage()
            goDetail()
            return
        }
        val explanationAddRequestModel = ExplanationAddRequestModel(
            taskModel.id,
            taskModel.locationId,
            (spinnerTitles.selectedItem as ExplanationTitleModel).id,
            textExplanation.text.toString()
        )
        transceiverViewModel.addExplanation(explanationAddRequestModel).observe(requireActivity(), {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {

                        saveImage()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.request_camera_permission_result),
                    Toast.LENGTH_LONG
                ).show()
                openCameraForImage()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.request_camera_permission_result_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE);
        } else {
            openCameraForImage();
        }
    }

    private fun openCameraForImage() {
        startActivityForResult(
            Intent(activity, ImageCaptureActivity::class.java),
            REQUEST_IMAGE_CAPTURE
        )
    }

    private fun saveImage() {
        if (!this::currentPhotoPath.isInitialized) {
            btnSave.text = getString(R.string.btn_save)
            goDetail()
            return
        }
        transceiverViewModel.uploadPhoto(taskModel.id, taskModel.locationId, currentPhotoPath)
            .observe(requireActivity(), {
                it?.let { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            Toast.makeText(
                                requireContext(),
                                resource.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            goDetail()
                        }
                        Resource.Status.ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                resource.message,
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        Resource.Status.LOADING -> {
                            // loadingProgressBar.visibility = View.VISIBLE
                        }
                    }
                }
            })
    }


    private fun goDetail() {
        taskModel.isTodoAdded = true
        SharedPreferencesUtil.writeTask(requireContext(), taskModel)
        val action =
            TaskExplanationFragmentDirections.actionTaskExplanationFragmentToTaskDetailFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun initExplanationTitles(view: View) {

        transceiverViewModel.explanationTitles().observe(requireActivity(), Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {

                        val adp: ArrayAdapter<ExplanationTitleModel> =
                            ArrayAdapter<ExplanationTitleModel>(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item, resource.data?.data!!
                            )
                        spinnerTitles.adapter = adp
                        val existingItem =
                            resource.data.data.first { q -> q.id.toString() == taskModel.explanationId }
                        spinnerTitles.setSelection(resource.data.data.indexOf(existingItem))

                    }
                    Resource.Status.ERROR -> {
                        // loadingProgressBar.visibility = View.GONE

                    }
                    Resource.Status.LOADING -> {
                        // loadingProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun initViews(view: View) {
        view.findViewById<TextView>(R.id.text_title).text =
            "TUR - ${taskModel.startDate.split(" ")}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentPhotoPath = data?.getStringExtra("result").toString()
            imgView.setImageURI(Uri.parse(currentPhotoPath))
        }
    }

}