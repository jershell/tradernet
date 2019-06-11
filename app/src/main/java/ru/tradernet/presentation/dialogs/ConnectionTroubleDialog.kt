package ru.tradernet.presentation.dialogs

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import ru.tradernet.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import ru.tradernet.utils.isNetworkConnected
import kotlinx.android.synthetic.main.dialog_trouble_connect.view.*


class ConnectionTroubleDialog : DialogFragment() {
    private var callbackOnRetry: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.dialog_trouble_connect, container, false)!!
        view.connection_trouble_retry_button.setOnClickListener {
            if (isNetworkConnected(context!!)) {
                dismiss()
                callbackOnRetry()
            }
        }
        return view
    }

    fun setCallBack(retryCallback: () -> Unit): ConnectionTroubleDialog {
        callbackOnRetry = retryCallback
        return this
    }
}