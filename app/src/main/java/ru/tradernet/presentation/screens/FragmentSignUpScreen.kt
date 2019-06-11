package ru.tradernet.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tradernet.R
import ru.tradernet.core.BaseFragment


class FragmentSignUpScreen : BaseFragment() {
    companion object {
        fun newInstance(): FragmentSignUpScreen {
            return FragmentSignUpScreen()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank_screen, container, false)
    }
}