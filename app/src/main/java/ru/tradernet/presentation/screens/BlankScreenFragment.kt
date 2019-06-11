package ru.tradernet.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tradernet.R
import ru.tradernet.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_blank_screen.view.*



class BlankScreenFragment : BaseFragment() {
    companion object {
        fun newInstance(): BlankScreenFragment {
            return BlankScreenFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank_screen, container, false)
    }
}