package ru.tradernet.presentation.vm

import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import kotlinx.coroutines.delay
import ru.tradernet.R
import ru.tradernet.core.AlertParams
import ru.tradernet.core.AuthException
import ru.tradernet.core.BaseViewModel
import ru.tradernet.data.AUTH_STATUS
import ru.tradernet.data.repository.AppRepository
import ru.tradernet.data.repository.StockMarketRepository

class AuthScreenViewModel(
    private val appRepository: AppRepository,
    private val stockMarketRepository: StockMarketRepository
) : BaseViewModel() {
    private val _authState = MutableLiveData<AUTH_STATUS>(AUTH_STATUS.UNKNOWN)
    private var login: String = ""
    private var password: String = ""

    val hasValues = MutableLiveData<Boolean>(false)
    val hasFocus = MutableLiveData<Boolean>(false)

    val debouncedOnFocusChange = debounce<Boolean> {
        hasFocus.value = it
    }

    fun onFocusChange(value: Boolean) {
        debouncedOnFocusChange(value)
    }

    fun getAuthState(): MutableLiveData<AUTH_STATUS> {
        tryIt(hasLoadingIndicator = false) {
            delay(1000)
            _authState.value = appRepository.resumeSession()
        }
        return _authState
    }


    fun handleOnLogin() = tryIt {
        try {
            _authState.value = appRepository.login(login, password)
            navigateTo(R.id.action_home)
        } catch (e: AuthException) {
            Logger.w("AuthException ${e.message}")
            showAlert(e.message)
        }
    }

    fun handleOnSignOut() {
        appRepository.signOut()
        _authState.value = AUTH_STATUS.NOT_AUTHENTICATED
        navigateTo(R.id.action_sing_in)
    }


    fun setPassword(value: String) {
        password = value
        hasValues.value = login.isNotEmpty() && password.isNotEmpty()
    }

    fun setLogin(value: String) {
        login = value
        hasValues.value = login.isNotEmpty() && password.isNotEmpty()
    }
}