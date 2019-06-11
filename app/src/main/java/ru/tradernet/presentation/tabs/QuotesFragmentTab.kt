package ru.tradernet.presentation.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_tab_quotes.view.*
import ru.tradernet.R
import ru.tradernet.core.BaseFragment
import ru.tradernet.presentation.adapters.QuotesAdapter
import ru.tradernet.presentation.vm.QuotesTabViewModel


class QuotesFragmentTab : BaseFragment() {
    lateinit var viewModel: QuotesTabViewModel
    lateinit var quotesAdapter: QuotesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_quotes, container, false)
        val locale = ConfigurationCompat.getLocales(context!!.resources.configuration)[0]

        viewModel = provideViewModel()

        view.recycler_view_quotes.layoutManager = LinearLayoutManager(context)
        quotesAdapter = QuotesAdapter(
            mutableListOf(), locale, context!!, onClick = { _, _ ->
                showToast("Click Click")
            }
        )
        Logger.d("QuotesFragmentTab ON onCreateView${viewModel.quotes.hasActiveObservers()}")

        view.recycler_view_quotes.adapter = quotesAdapter

        viewModel
            .subscribe()
            .observe(this, Observer {
                quotesAdapter.updateItems(it)
                quotesAdapter.notifyDataSetChanged()
            })

        return view
    }

    override fun onStop() {
        super.onStop()
        Logger.d("QuotesFragmentTab ON STOP")
        viewModel.unsubscribe()
    }

    companion object {
        @JvmStatic
        fun newInstance(): QuotesFragmentTab {
            return QuotesFragmentTab()
        }
    }
}