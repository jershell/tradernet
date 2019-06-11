package ru.tradernet.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import ru.tradernet.R
import ru.tradernet.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_blank_screen.view.*
import kotlinx.android.synthetic.main.fragment_sign_in_screen.view.*
import ru.tradernet.presentation.vm.AuthScreenViewModel
import ru.tradernet.utils.hideKeyboardFrom


class FragmentSignInScreen : BaseFragment() {
    companion object {
        fun newInstance(): FragmentSignInScreen {
            return FragmentSignInScreen()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = provideViewModel<AuthScreenViewModel>()
        val view = inflater
            .inflate(R.layout.fragment_sign_in_screen, container, false)

        viewModel.hasValues.observe(this, Observer {
            view.button_login.isEnabled = it
        })

        view.input_login.addTextChangedListener { viewModel.setLogin(it.toString()) }
        view.input_password.addTextChangedListener { viewModel.setPassword(it.toString()) }

        view.input_login.setOnFocusChangeListener { _, hasFocus -> viewModel.onFocusChange(hasFocus) }
        view.input_password.setOnFocusChangeListener { _, hasFocus -> viewModel.onFocusChange(hasFocus) }

        view.button_login.setOnClickListener {
            hideKeyboardFrom(context!!, view)
            viewModel.handleOnLogin()
        }

        viewModel.hasFocus.observe(this, Observer {
            view.app_bar.setExpanded(!it, true)
            if (!it) {
                hideKeyboardFrom(context!!, view)
            }
        })

        return view
    }
}