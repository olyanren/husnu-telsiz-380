package com.dengetelekom.telsiz.ui.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dengetelekom.telsiz.Constants
import com.dengetelekom.telsiz.R
import com.dengetelekom.telsiz.TransceiverViewModel
import com.dengetelekom.telsiz.databinding.FragmentLoginBinding
import com.dengetelekom.telsiz.factories.TransceiverViewModelFactory
import com.dengetelekom.telsiz.helpers.SharedPreferencesUtil
import com.dengetelekom.telsiz.models.LoginForm
import com.dengetelekom.telsiz.models.Resource
import com.dengetelekom.telsiz.models.TokenResponseModel
import com.dengetelekom.telsiz.repositories.TransceiverRepository


class LoginFragment : Fragment() {

    private lateinit var loginViewModel: TransceiverViewModel
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(
            requireActivity(),
            TransceiverViewModelFactory(repository = TransceiverRepository())
        ).get(
            TransceiverViewModel::class.java
        )
        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loginCheckRememberMe = binding.chkRememberMe
        val loadingProgressBar = binding.loading
        initLoginValues(usernameEditText, passwordEditText, loginCheckRememberMe)
        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })


        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginStart(usernameEditText, passwordEditText, loadingProgressBar)
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE

            loginStart(usernameEditText, passwordEditText, loadingProgressBar)
        }
    }

    private fun initLoginValues(
        usernameEditText: EditText,
        passwordEditText: EditText,
        loginCheckRememberMe: CheckBox
    ) {
        val loginForm = SharedPreferencesUtil.readLoginForm(requireContext()) ?: return
        usernameEditText.setText(loginForm.username)
        passwordEditText.setText(loginForm.password)
        loginCheckRememberMe.isChecked = loginForm.rememberMe

    }

    private fun loginStart(
        usernameEditText: EditText,
        passwordEditText: EditText,
        loadingProgressBar: ProgressBar
    ) {
        loginViewModel.token(
            usernameEditText.text.toString(),
            passwordEditText.text.toString()
        ).observe(requireActivity(), Observer {
            it?.let { resource ->
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        saveRememberMe(usernameEditText, passwordEditText)
                        updateUiWithUser(it)
                        loadingProgressBar.visibility = View.GONE
                    }
                    Resource.Status.ERROR -> {
                        loadingProgressBar.visibility = View.GONE
                        showLoginFailed()
                    }
                    Resource.Status.LOADING -> {
                        loadingProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun saveRememberMe(usernameEditText: EditText, passwordEditText: EditText) {
        if (binding.chkRememberMe.isChecked) {
            SharedPreferencesUtil.writeLoginForm(
                requireContext(),
                LoginForm(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                    binding.chkRememberMe.isChecked
                )
            )
        } else {
            SharedPreferencesUtil.writeLoginForm(requireContext(), null)
        }
    }

    private fun updateUiWithUser(value: Resource<TokenResponseModel>?) {
        val welcome = getString(R.string.welcome)
        Constants.ACCESS_TOKEN = value?.data?.access_token ?: ""
        Constants.COMPANY_NAME = value?.data?.companyName ?: ""
        Constants.NFC_READER_ACTIVE = value?.data?.nfcReaderActive == true
        Constants.BARCODE_READER_ACTIVE = value?.data?.barcodeReaderActive == true
        Constants.IS_CHECKIN_AVAILABLE = value?.data?.isCheckInAvailable==true

        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()

        val action = LoginFragmentDirections.actionLoginFragmentToTaskFragment()
        view?.findNavController()?.navigate(action)

    }

    private fun showLoginFailed() {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, resources.getText(R.string.login_failed), Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}