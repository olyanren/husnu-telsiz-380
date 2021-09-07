package com.dengetelekom.telsiz.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dengetelekom.telsiz.R
import com.dengetelekom.telsiz.TransceiverViewModel
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.helpers.AlertHelper
import com.dengetelekom.telsiz.helpers.SharedPreferencesUtil
import com.dengetelekom.telsiz.models.NotificationModel
import com.dengetelekom.telsiz.models.Resource
import com.dengetelekom.telsiz.models.TaskModel
import com.dengetelekom.telsiz.repositories.TransceiverRepository

/**
 * A fragment representing a list of Items.
 */
class NotificationFragment : Fragment(), NotificationViewAdapter.OnItemClickListener {

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
        loadNotifications()
        return taskListRecyclerView
    }

    private fun NotificationFragment.loadNotifications() {
        with(taskListRecyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            transceiverViewModel.notifications().observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            //loadingProgressBar.visibility = View.GONE
                            @Suppress("UNCHECKED_CAST")

                            adapter = resource.data?.let { it1 ->
                                NotificationViewAdapter(
                                        it1 as List<NotificationModel>,
                                    this@NotificationFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transceiverViewModel.refreshState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                if (loginFormState == "REFRESHED") {
                    refreshNotifications()
                }
            })

        transceiverViewModel.previousState.observe(viewLifecycleOwner,
            Observer { previousState ->
                if (previousState == null) {
                    return@Observer
                }
                if (previousState == "PREVIOUS") {
                    previousNotification()
                }
            })
    }

    private fun refreshNotifications() {
        loadNotifications()
    }

    private fun previousNotification() {
        loadPreviousNotification()
    }

    private fun NotificationFragment.loadPreviousNotification() {
        with(taskListRecyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            transceiverViewModel.previousNotification().observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {

                            adapter = resource.data?.data?.let { it1 ->
                                NotificationViewAdapter(
                                    it1,
                                    this@NotificationFragment
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
            NotificationFragment().apply {
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


}