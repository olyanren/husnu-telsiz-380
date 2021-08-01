package com.dengetelekom.telsiz.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dengetelekom.telsiz.Constants
import com.dengetelekom.telsiz.R
import com.dengetelekom.telsiz.TransceiverViewModel
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.helpers.AlertHelper
import com.dengetelekom.telsiz.helpers.SharedPreferencesUtil
import com.dengetelekom.telsiz.models.ApiResponseModel
import com.dengetelekom.telsiz.models.Resource
import com.dengetelekom.telsiz.models.TaskModel
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import com.google.gson.Gson

/**
 * A fragment representing a list of Items.
 */
class TaskFragment : Fragment(), TaskRecyclerViewAdapter.OnItemClickListener {
    private var LAUNCH_WRITE_NFC_ACTIVITY = 2
    private var columnCount = 1
    private lateinit var transceiverViewModel: TransceiverViewModel
    private lateinit var taskListRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        taskListRecyclerView =
            inflater.inflate(R.layout.fragment_task_list, container, false) as RecyclerView
        transceiverViewModel = ViewModelProvider(
            requireActivity(),
            TransceiverViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )
        transceiverViewModel.showRefreshButton()
        loadTasks()
        return taskListRecyclerView
    }

    private fun TaskFragment.loadTasks() {
        with(taskListRecyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            transceiverViewModel.tasks().observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            //loadingProgressBar.visibility = View.GONE
                            @Suppress("UNCHECKED_CAST")
                            setCompanyName((resource.data as List<TaskModel>?))
                            setNFCBarcodeStatus(resource.data)
                            adapter = resource.data?.let { it1 ->
                                TaskRecyclerViewAdapter(
                                    it1,
                                    this@TaskFragment
                                )
                            }
                        }
                        Resource.Status.ERROR -> {
                            it.message?.let { it1 ->
                                AlertHelper.showInfoDialog(requireContext(),resources.getString(R.string.notify),
                                    it1)
                            }
                        }
                        Resource.Status.LOADING -> {
                            // loadingProgressBar.visibility = View.VISIBLE
                        }
                    }
                }
            })

        }
    }

    private fun setCompanyName(data: List<TaskModel>?) {
        if (data?.size != null && data.isNotEmpty()) {
            transceiverViewModel.setCompanyName(data[0].companyName)

        }
    }
    private fun setNFCBarcodeStatus(data: List<TaskModel>?) {
        if (data?.size != null && data.isNotEmpty()) {
            Constants.NFC_READER_ACTIVE = data[0].nfcReaderActive == "1"||data[0].nfcReaderActive == "true"
            Constants.BARCODE_READER_ACTIVE = data[0].barcodeReaderActive == "1"||data[0].barcodeReaderActive == "true"
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transceiverViewModel.refreshState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                if (loginFormState == "REFRESHED") {
                    refreshTasks()
                }
            })

        transceiverViewModel.previousState.observe(viewLifecycleOwner,
            Observer { previousState ->
                if (previousState == null) {
                    return@Observer
                }
                if (previousState == "PREVIOUS") {
                    previousTask()
                }
            })
    }

    private fun refreshTasks() {
        loadTasks()
    }

    private fun previousTask() {
        loadPreviousTask()
    }

    private fun TaskFragment.loadPreviousTask() {
        with(taskListRecyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            transceiverViewModel.previousTask().observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            //loadingProgressBar.visibility = View.GONE
                            setCompanyName(resource.data?.data)
                            setNFCBarcodeStatus(resource.data?.data)
                            adapter = resource.data?.data?.let { it1 ->
                                TaskRecyclerViewAdapter(
                                    it1,
                                    this@TaskFragment
                                )
                            }
                        }
                        Resource.Status.ERROR -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                        }
                        Resource.Status.LOADING -> {
                            // loadingProgressBar.visibility = View.VISIBLE
                        }
                    }
                }
            })

        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onImageStartClicked(item: TaskModel?) {
        if (item == null) {
            Toast.makeText(requireContext(), R.string.task_model_null_error, Toast.LENGTH_LONG)
                .show()
            return
        }
         SharedPreferencesUtil.writeTask(requireContext(),item)
        val action =
            TaskFragmentDirections.actionTaskFragmentToTaskDetailFragment()
        view?.findNavController()?.navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LAUNCH_WRITE_NFC_ACTIVITY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                if (result != null) {
                    Log.i("DENGETELEKOM_NFC_WRITE", result)
                }
                if (result == null) {
                    Toast.makeText(activity, R.string.barcode_write_cancelled, Toast.LENGTH_LONG)
                        .show()
                    return
                }
                Toast.makeText(activity, "Başarılı bir şekilde yazıldı", Toast.LENGTH_LONG).show()
            }
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Toast.makeText(activity, R.string.barcode_write_cancelled, Toast.LENGTH_LONG).show()
            }
        }
    }
}