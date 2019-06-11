package ru.tradernet.core

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.tradernet.MainActivity
import ru.tradernet.R
import ru.tradernet.data.BACK_STACK
import ru.tradernet.presentation.dialogs.*
import ru.tradernet.utils.KodeinViewModelFactory
import ru.tradernet.utils.isNetworkConnected
import com.orhanobut.logger.Logger
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.navigation.NavOptions
import ru.tradernet.utils.hideKeyboard
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.widget.Toolbar


abstract class BaseFragment : Fragment(), KodeinAware {
    override val kodein: Kodein by lazy { (context?.applicationContext as KodeinAware).kodein }

    private val defaultNavOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_left).build()

    fun showOverlay() {
        (activity as MainActivity).showLoadingOverlay()
    }

    fun hideOverlay() {
        (activity as MainActivity).hideLoadingOverlay()
    }

    inline fun <reified T : BaseViewModel> provideViewModel(): T {

        val vm = ViewModelProviders
            .of(this, KodeinViewModelFactory(kodein))
            .get(T::class.java)

        vm.dialogProgress.observe(this, Observer { status ->
            if (status == BaseViewModel.VISIBLE) {
                showOverlay()
                vm.dialogProgress.value = BaseViewModel.DONE
            } else if (status == BaseViewModel.INVISIBLE) {
                hideOverlay()
                vm.dialogProgress.value = BaseViewModel.DONE
            }
        })

        vm.alert.observe(this, Observer {
            if (it != null) {
                showAlert(it.description, it.title, it.onOk)
                vm.alert.value = null
            }
        })

        vm.confirm.observe(this, Observer {
            if (it != null) {
                showAlert(it.description, it.title, it.onOk)
                vm.alert.value = null
            }
        })

        vm.dialogWrong.observe(this, Observer {
            if (it != null) {
                showWrongDialog(it.reason, it.onOk)
                vm.dialogWrong.value = null
            }
        })

        vm.getNavigator().observe(this, Observer {
            if (it != BaseViewModel.NAVIGATION_TASK_DONE) {
                navigate(it)
                vm.getNavigator().value = BaseViewModel.NAVIGATION_TASK_DONE
            }
        })

        vm.dialogConnectTroubleIsVisible.observe(this, Observer {
            if (it) {
                vm.dialogConnectTroubleIsVisible.value = false
                showConnectionTroubleDialog {
                    navigate(BACK_STACK)
                }
            }
        })

        vm.onProvide()

        return vm
    }

    fun showConnectionTroubleDialog(retryCallback: () -> Unit) {
        val ft = fragmentManager!!.beginTransaction()
        ConnectionTroubleDialog()
            .setCallBack(retryCallback)
            .show(ft, ConnectionTroubleDialog::class.simpleName)
    }

    fun showWrongDialog(reason: String = "", onOk: () -> Unit = {}) {
        val ft = fragmentManager!!.beginTransaction()
        WrongDialog().apply {
            details = if (reason.isNotEmpty()) {
                reason
            } else {
                getString(R.string.page_cant_display)
            }
            handler = onOk
            show(ft, WrongDialog::class.simpleName)
        }
    }

    fun showAlert(message: String, title: String = "", onOk: () -> Unit = {}) {
        val ft = fragmentManager!!.beginTransaction()
        val defaultTitle = if (title.isNotEmpty()) {
            title
        } else {
            getString(R.string.attention)
        }

        AlertDialog().apply {
            setDescription(message)
            setCallBack(onOk)
            setTitle(defaultTitle)
        }.show(ft, AlertDialog::class.simpleName)
    }

    private fun showConfirm(message: String, title: String = "", onOk: () -> Unit) {
        val ft = fragmentManager!!.beginTransaction()

        val d = ConfirmDialog()

        d.setDescription(message)
        if (title.isNotEmpty()) {
            d.setTitle(title)
        } else {
            d.setTitle(getString(R.string.attention))
        }
        d.setCallBack(onOk)
        d.show(ft, AlertDialog::class.simpleName)
    }

    fun navigate(fragmentId: Int, navOptions: NavOptions? = null) {

        val mActivity = activity as MainActivity
        hideKeyboard(activity!!)

        if (fragmentId == BACK_STACK) {
            Logger.i("Navigate to BACK_STACK")
            mActivity.navController.popBackStack()
        } else {
            val fragmentName = resources.getResourceEntryName(fragmentId)
            Logger.i("Navigate to $fragmentName with options $navOptions")
            if (navOptions != null) {
                mActivity.navController.navigate(fragmentId, null, navOptions)
            } else {
                mActivity.navController.navigate(fragmentId)
            }
        }

        hideOverlay()
    }

    fun confirm(title: String, message: String, block: () -> Unit) {
        showConfirm(message, title, onOk = block)
    }

    fun r(res: Int): String {
        return context!!.getString(res)
    }

    fun checkNetworkConnect(block: () -> Unit) {
        if (isNetworkConnected(context!!)) {
            block()
        } else {
            showConnectionTroubleDialog(block)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Logger.d("Nothing")
        return super.onOptionsItemSelected(item)
    }

    fun download(url: String, fileName: String) {
        val uri = Uri.parse(url)
        val downloadManager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverRoaming(false)
        request.setTitle(fileName)
        request.setDescription("${r(R.string.downloading)} $fileName")
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)

        downloadManager.enqueue(request)
    }

    fun showToast(message: String) {
        val toast = Toast.makeText(
            activity!!.applicationContext,
            message,
            Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun getToolbar(): Toolbar {
        return (activity as MainActivity).actionToolBar
    }
}