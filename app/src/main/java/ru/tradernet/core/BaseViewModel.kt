package ru.tradernet.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.tradernet.R
import ru.tradernet.data.BACK_STACK
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import javax.net.ssl.SSLHandshakeException

abstract class BaseViewModel : ViewModel() {
    companion object {
        const val NAVIGATION_TASK_DONE: Int = -12041961
        const val DONE: Int = -100
        const val VISIBLE: Int = -101
        const val INVISIBLE: Int = -102
    }

    private val navigation = MutableLiveData<Int>(NAVIGATION_TASK_DONE)
    val dialogWrong = MutableLiveData<DialogWrongParams?>()
    val dialogConnectTroubleIsVisible = MutableLiveData<Boolean>(false)
    val dialogProgress = MutableLiveData<Int>(DONE)
    val alert = MutableLiveData<AlertParams?>()
    val confirm = MutableLiveData<AlertParams?>()

    fun tryIt(hasLoadingIndicator: Boolean = true, block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch {

        if (hasLoadingIndicator) {
            showProgressDialog()
        }

        try {
            block()
        } catch (e: ConnectException) {
            Logger.w("ConnectException ${e.message}")
            dialogConnectTroubleIsVisible.value = true
        } catch (e: AuthException) {
            Logger.w("AuthException ${e.message}")
            alert.value = AlertParams("", e.message, onOk = {
                navigateTo(R.id.nav_sing_in)
            })
        } catch (e: Exception) {
            when (e) {
                is SerializationException,
                is NoSuchElementException,
                is SSLHandshakeException,
                is ExpectedException -> {
                    Logger.w("${e::class.simpleName} ${e.message}")
                    dialogWrong.value = DialogWrongParams(e.message.toString()) {
                        navigateTo(BACK_STACK)
                    }
                }
                else -> throw e
            }
        } finally {
            if (hasLoadingIndicator) {
                hideProgressDialog()
            }
        }
    }

    fun <T> debounce(wait: Long = 100, firstIsAwait: Boolean = false, block: (T) -> Unit): (T) -> Unit {
        var job: Job = viewModelScope.launch {}
        return {
            when {
                job.isCompleted -> job = viewModelScope.launch {
                    Logger.d("debounce was isCompleted")

                    if (firstIsAwait) {
                        delay(wait)
                        block(it)
                    } else {
                        block(it)
                        delay(wait)
                    }
                }
                job.isActive -> {
                    Logger.d("debounce is activating state")
                    job.cancel()
                    job = viewModelScope.launch {
                        delay(wait)
                        block(it)
                    }
                }
                job.isCancelled -> {
                    Logger.d("debounce was cancelled")
                }
            }
        }
    }

    private val debouncedSetStateProgressDialog = debounce<Int>(
        wait = 100,
        firstIsAwait = false,
        block = { state ->
            dialogProgress.value = state
        })

    fun sleep(timeMillis: Long, block: () -> Unit = {}) = tryIt {
        delay(timeMillis)
        block()
    }

    fun navigateTo(destination: Int) {
        navigation.value = destination
    }

    fun showProgressDialog() {
        debouncedSetStateProgressDialog(VISIBLE)
    }

    fun hideProgressDialog() {
        debouncedSetStateProgressDialog(INVISIBLE)
    }

    fun showAlert(message: String, title: String = "", onOk: () -> Unit = {}) {
        alert.value = AlertParams(title, message, onOk)
    }

    fun getNavigator(): MutableLiveData<Int> {
        return navigation
    }

    // event hook that call then ViewModel will be provide for Fragment - Screen
    open fun onProvide() {}
}