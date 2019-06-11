package ru.tradernet.presentation.dialogs

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import ru.tradernet.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.dialog_alert.view.*


class AlertDialog : DialogFragment() {
    private var onOk: () -> Unit = {}
    private var title: String = ""
    private var description: String = "~"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AlertDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.dialog_alert, container, false)!!

        view.button_ok.setOnClickListener {
            dismiss()
            onOk()
        }
        view.label_title.text = title
        Logger.d("onCreateView $description")
        view.label_description.text = description
        return view
    }

    fun setCallBack(block: () -> Unit): AlertDialog {
        onOk = block
        return this
    }

    fun setTitle(value: String): AlertDialog {
        title = value
        return this
    }

    fun setDescription(value: String): AlertDialog {
        Logger.d("setDescription $value")
        description = value
        return this
    }
}