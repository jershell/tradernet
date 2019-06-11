package ru.tradernet.presentation.screens


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import ru.tradernet.R
import ru.tradernet.core.BaseFragment
import ru.tradernet.data.AUTH_STATUS
import ru.tradernet.presentation.vm.AuthScreenViewModel


class FragmentSplashScreen : BaseFragment() {

    companion object {
        fun newInstance(): FragmentSplashScreen {
            return FragmentSplashScreen()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        provideViewModel<AuthScreenViewModel>()
            .getAuthState()
            .observe(this, Observer {
                when (it) {
                    AUTH_STATUS.AUTHENTICATED -> navigate(R.id.action_home)
                    AUTH_STATUS.NOT_AUTHENTICATED -> navigate(R.id.action_sing_in)
                    else -> {}
                }
            })

        getToolbar().visibility = View.GONE

        return inflater.inflate(
            R.layout.fragment_splash_screen,
            container,
            false
        )
    }
}