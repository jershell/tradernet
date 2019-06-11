package ru.tradernet.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_main_screen.view.*
import ru.tradernet.R
import ru.tradernet.core.BaseFragment
import ru.tradernet.presentation.adapters.SectionsPagerAdapter


class FragmentMainScreen : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance(): FragmentMainScreen {
            return FragmentMainScreen()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        view.view_pager.adapter = SectionsPagerAdapter(context!!, childFragmentManager)
        view.tabs.setupWithViewPager(view.view_pager)

        Logger.d("onCreateView FragmentMainScreen")
        return view
    }
}