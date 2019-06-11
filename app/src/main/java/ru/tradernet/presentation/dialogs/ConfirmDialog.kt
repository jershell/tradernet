package ru.tradernet.presentation.dialogs

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import ru.tradernet.R
import kotlinx.android.synthetic.main.dialog_confirm.view.*

class ConfirmDialog : DialogFragment() {
    private var onOk: () -> Unit = {}
    private var title: String = ""
    private var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AlertDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.dialog_confirm, container, false)!!

        view.button_ok.setOnClickListener {
            dismiss()
            onOk()
        }

        view.button_cancel.setOnClickListener {
            dismiss()
        }

        view.label_title.text = title
        view.label_description.text = description
        return view
    }

    fun setCallBack(block: () -> Unit): ConfirmDialog {
        onOk = block
        return this
    }

    fun setTitle(value: String): ConfirmDialog {
        title = value
        return this
    }

    fun setDescription(value: String): ConfirmDialog {
        description = value
        return this
    }
}