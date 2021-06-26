package com.dengetelekom.telsiz.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dengetelekom.telsiz.R
import com.dengetelekom.telsiz.TransceiverViewModel
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.helpers.SharedPreferencesUtil
import com.dengetelekom.telsiz.models.*
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
class TaskTodoFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private lateinit var taskModel: TaskModel
    private lateinit var transceiverViewModel: TransceiverViewModel
    private lateinit var btnAddExplanation: Button
    private lateinit var btnSave: Button
    private var todoList = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            taskModel = SharedPreferencesUtil.readTask(requireContext())
            if (taskModel.todos == null){
                taskModel.todos = mutableListOf()
                SharedPreferencesUtil.writeTask(requireContext(),taskModel )
            }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_todo, container, false)
        btnSave = view.findViewById<Button>(R.id.btn_save)
        btnAddExplanation = view.findViewById<Button>(R.id.btn_add_explanation)

        btnSave.visibility = View.GONE
        btnAddExplanation.visibility = View.GONE

        btnSave.setOnClickListener { saveToDoList() }
        btnAddExplanation.setOnClickListener { saveToDoList();openExplanation() }

        transceiverViewModel = ViewModelProvider(
            requireActivity(),
            TransceiverViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )
        initViews(view)
        initTodoList(view)
        return view
    }

    private fun openExplanation() {
        val action =
            TaskTodoFragmentDirections.actionTaskTodoFragmentToTaskExplanationFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun saveToDoList() {
        val request = ToDoAddRequestModel(taskModel.id, taskModel.locationId, todoList.distinct())
        transceiverViewModel.addTodos(request).observe(requireActivity(), {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        Toast.makeText(requireContext(), resource.data?.message, Toast.LENGTH_SHORT)
                            .show()
                        taskModel.isTodoAdded = true
                        SharedPreferencesUtil.writeTask(requireContext(),taskModel )

                        goDetail()

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

    private fun goDetail() {
        val action =
            TaskTodoFragmentDirections.actionTaskTodoFragmentToTaskDetailFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun initViews(view: View) {
        view.findViewById<TextView>(R.id.text_title).text = "TUR - ${taskModel.startDate}"
    }

    private fun initTodoList(view: View) {
        val mainCardView = view.findViewById<CardView>(R.id.cardMain)
        val sv = ScrollView(requireContext())
        val ll = LinearLayout(requireContext())
        ll.orientation = LinearLayout.VERTICAL
        sv.addView(ll)
        mainCardView.addView(sv)
        transceiverViewModel.todos().observe(requireActivity(), Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data?.data != null) {
                            for (item in resource.data.data) {
                                val cb = CheckBox(requireContext())
                                cb.text = item.title
                                cb.tag = item.id
                                cb.isChecked = taskModel.todos?.contains(item.id)!!
                                if (cb.isChecked) todoList.add(cb.tag.toString().toInt())
                                else todoList.remove(cb.tag.toString().toInt())
                                cb.setOnCheckedChangeListener { buttonView, isChecked ->
                                    if (isChecked) todoList.add(buttonView.tag.toString().toInt())
                                    else todoList.remove(buttonView.tag.toString().toInt())

                                    if (todoList.size == resource.data.data.size) {
                                        btnSave.visibility = View.VISIBLE
                                        btnAddExplanation.visibility = View.VISIBLE
                                    } else {
                                        btnSave.visibility = View.GONE
                                        btnAddExplanation.visibility = View.GONE
                                    }
                                }

                                ll.addView(cb)
                            }
                        }


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