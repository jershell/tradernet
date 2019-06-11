package ru.tradernet.presentation.dialogs

import androidx.fragment.app.DialogFragment
import android.os.Bundle
import ru.tradernet.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.dialog_troublesome.view.*

class WrongDialog : DialogFragment() {
    var details: String = ""
    var handler = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_troublesome, container, false).apply {
            label_description.text = details
            button_ok.setOnClickListener { handler() }
        }
    }
}