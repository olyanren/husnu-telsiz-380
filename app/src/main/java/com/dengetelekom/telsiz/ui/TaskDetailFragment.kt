package com.dengetelekom.telsiz.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dengetelekom.telsiz.*
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.helpers.SharedPreferencesUtil
import com.dengetelekom.telsiz.models.Resource
import com.dengetelekom.telsiz.models.TaskCompleteRequestModel
import com.dengetelekom.telsiz.models.TaskModel
import com.dengetelekom.telsiz.repositories.TransceiverRepository
import com.google.gson.Gson


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM_TASK = "taskDetail"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskDetailFragment : Fragment() {
    private var LAUNCH_QR_CODE_ACTIVITY = 1
    private var LAUNCH_NFC_ACTIVITY = 2


    // TODO: Rename and change types of parameters
    private lateinit var taskModel: TaskModel
    private lateinit var transceiverViewModel: TransceiverViewModel
    private lateinit var btnTodo: Button
    private lateinit var btnReadBarcode: Button
    private lateinit var btnReadNFC: Button
    private lateinit var btnAddExplanation: Button
    private lateinit var btnCompleteTour: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskModel = SharedPreferencesUtil.readTask(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)
        transceiverViewModel = ViewModelProvider(
            requireActivity(),
            TransceiverViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )
        transceiverViewModel.hideRefreshButton()

        btnReadBarcode = view.findViewById(R.id.btn_read_barcode)
        btnTodo = view.findViewById(R.id.btn_todo)
        btnReadNFC = view.findViewById(R.id.btn_read_nfc)
        btnAddExplanation = view.findViewById(R.id.btn_add_explanation)
        btnCompleteTour = view.findViewById(R.id.btn_complete_tour)
        if (!taskModel.isTodoAdded) {
            btnTodo.visibility = View.GONE
            btnCompleteTour.visibility = View.GONE
        }
        if (Constants.NFC_READER_ACTIVE) {
            btnReadNFC.visibility = View.VISIBLE
            btnReadBarcode.visibility = View.GONE
        } else {
            btnReadNFC.visibility = View.GONE
            if (Constants.BARCODE_READER_ACTIVE) {
                btnReadBarcode.visibility = View.VISIBLE
            } else {
                btnReadBarcode.visibility = View.GONE
                btnTodo.visibility = View.VISIBLE
            }
        }


        btnAddExplanation.visibility = View.GONE



        btnReadBarcode.setOnClickListener {
            if (!Constants.NFC_READER_ACTIVE) {
                taskModel.isNcfValid = true
                SharedPreferencesUtil.writeTask(requireContext(), taskModel)
            }
            openReadBarcodeActivity()
        }
        btnReadNFC.setOnClickListener {
            startActivityForResult(
                Intent(activity, ReadNcfActivity::class.java),
                LAUNCH_NFC_ACTIVITY
            )
        }

        btnAddExplanation.setOnClickListener {
            val action =
                TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskExplanationFragment()
            view?.findNavController()?.navigate(action)
        }
        btnTodo.setOnClickListener {
            if (!Constants.NFC_READER_ACTIVE) {
                taskModel.isNcfValid = true
            }
            if (!Constants.BARCODE_READER_ACTIVE) {
                taskModel.isQrCodeValid = true
            }
            SharedPreferencesUtil.write(requireContext(), "taskModel", taskModel)
            val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskTodoFragment()
            view?.findNavController()?.navigate(action)
        }
        btnCompleteTour.text =
            if (taskModel.completedDate != null && taskModel.completedDate != "")
                getText(R.string.mission_is_completed) else
                getText(R.string.complete_mission)


        btnCompleteTour.setOnClickListener {
            when {

                !taskModel.isQrCodeValid -> Toast.makeText(
                    requireContext(),
                    "Karakod bilgisi okunmadı ya da yanlış",
                    Toast.LENGTH_LONG
                ).show()
                !taskModel.isNcfValid -> Toast.makeText(
                    requireContext(),
                    "NFC bilgisi okunmadı ya da yanlış",
                    Toast.LENGTH_LONG
                ).show()
                else -> complete()
            }
        }
        initViews(view)
        return view
    }

    private fun openReadBarcodeActivity() {
        startActivityForResult(
            Intent(activity, ReadBarcodeActivity::class.java),
            LAUNCH_QR_CODE_ACTIVITY
        )
    }

    private fun complete() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.complete_mission)
            .setMessage(R.string.complete_mission_question)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.yes) { _, _ ->
                run {
                    val request = TaskCompleteRequestModel(taskModel.id, taskModel.locationId)

                    transceiverViewModel.complete(request).observe(requireActivity(), {
                        it?.let { resource ->
                            when (resource.status) {
                                Resource.Status.SUCCESS -> {
                                    Toast.makeText(
                                        requireContext(),
                                        resource.data?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    goHome()
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
            }
            .setNegativeButton(R.string.no, null).show()

    }

    private fun goHome() {
        val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun initViews(view: View) {
        view.findViewById<TextView>(R.id.text_title).text = "TUR - ${taskModel.startDate}"
        view.findViewById<TextView>(R.id.text_time).text = "${taskModel.startDate.split(" ")[1]}"
        view.findViewById<TextView>(R.id.text_finish_date).text = taskModel.finishDate.split(' ')[1]
        view.findViewById<TextView>(R.id.text_minute).text = taskModel.minute.toString()
        view.findViewById<TextView>(R.id.text_location_active).text =
            taskModel.completedLocationCount.toString() + "/" + taskModel.totalLocationCount.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_QR_CODE_ACTIVITY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                if (result != null) {
                    Log.i("DENGETELEKOM_NFC", result)
                    Toast.makeText(activity, result, Toast.LENGTH_LONG).show()

                }
                if (result == null) {
                    Toast.makeText(activity, R.string.barcode_read_cancelled, Toast.LENGTH_LONG)
                        .show()
                    return
                }
                setQrCodeResult(result)
            }
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Toast.makeText(activity, R.string.barcode_read_cancelled, Toast.LENGTH_LONG).show()

            }
        } else if (requestCode == LAUNCH_NFC_ACTIVITY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                if (result != null) {
                    Log.i("DENGETELEKOM_NFC", result)
                    Toast.makeText(activity, result, Toast.LENGTH_LONG).show()
                    setNFCResult(result)
                }
                if (result == null) {
                    Toast.makeText(activity, R.string.barcode_read_cancelled, Toast.LENGTH_LONG)
                        .show()

                    return
                }

            }
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Toast.makeText(activity, R.string.barcode_read_cancelled, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun setQrCodeResult(barcodeNo: String) {

        if (barcodeNo != taskModel.name) {
            Toast.makeText(activity, resources.getText(R.string.number_invalid), Toast.LENGTH_LONG).show()
            taskModel.isQrCodeValid = false
            SharedPreferencesUtil.writeTask(requireContext(), taskModel)
        } else {
            Toast.makeText(activity, resources.getText(R.string.number_valid), Toast.LENGTH_LONG).show()
            taskModel.isQrCodeValid = true
            SharedPreferencesUtil.writeTask(requireContext(), taskModel)
            btnTodo.visibility = View.VISIBLE
            btnReadNFC.visibility = View.GONE
        }

    }

    private fun setNFCResult(barcodeNo: String) {

        if (barcodeNo != taskModel.name) {
            Toast.makeText(activity, resources.getText(R.string.number_invalid), Toast.LENGTH_LONG).show()
            taskModel.isNcfValid = false
            SharedPreferencesUtil.writeTask(requireContext(), taskModel)
        } else {
            Toast.makeText(activity, resources.getText(R.string.number_valid), Toast.LENGTH_LONG).show()
            taskModel.isNcfValid = true
            SharedPreferencesUtil.writeTask(requireContext(), taskModel)
            if (Constants.BARCODE_READER_ACTIVE){
                openReadBarcodeActivity()
            }
            else {
                taskModel.isQrCodeValid = true
                SharedPreferencesUtil.writeTask(requireContext(), taskModel)
                btnTodo.visibility = View.VISIBLE
                btnReadNFC.visibility = View.GONE
            }
        }

    }

    override fun onResume() {
        super.onResume()

    }


}