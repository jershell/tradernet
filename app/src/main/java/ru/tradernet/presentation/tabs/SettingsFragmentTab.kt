package ru.tradernet.presentation.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tab_settings_screen.view.*
import ru.tradernet.R
import ru.tradernet.core.BaseFragment
import ru.tradernet.presentation.vm.AuthScreenViewModel

/**
 * A placeholder fragment containing a simple view.
 */
class SettingsFragmentTab : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_settings_screen, container, false)
        val viewModel = provideViewModel<AuthScreenViewModel>()

        view.button_sign_out.setOnClickListener {
            viewModel.handleOnSignOut()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(): SettingsFragmentTab {
            return SettingsFragmentTab()
        }
    }
}