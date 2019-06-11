package ru.tradernet.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import ru.tradernet.R
import ru.tradernet.data.model.Quotation
import kotlinx.android.synthetic.main.item_stock_quote.view.*
import ru.tradernet.data.TIME_FORMAT
import ru.tradernet.utils.fromISO8601UTC
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.text.DecimalFormat
import android.R.attr.start
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import android.view.View.TRANSLATION_X
import android.animation.PropertyValuesHolder
import com.google.android.material.animation.AnimatorSetCompat.playTogether


typealias OfferOnClick = (Quotation, Int) -> Unit

class QuotesAdapter(
    private val items: MutableList<Quotation>,
    currentLocale: Locale,
    private val context: Context,
    private val onClick: OfferOnClick
) : RecyclerView.Adapter<QuotesAdapter.ViewHolder>() {
    lateinit var parent: ViewGroup
    private val timeFormatter = SimpleDateFormat(TIME_FORMAT, currentLocale)
    private var df = DecimalFormat("0.00")

    private fun formatTime(value: String): String {
        return try {
            timeFormatter.format(fromISO8601UTC(value + "Z"))
        } catch (e: ParseException) {
            Logger.d("Parse date error $value")
            ""
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, idx: Int) {
        viewHolder.title.text = items[idx].code
        viewHolder.lastUpdate.text = formatTime(items[idx].timeOfLastTrade)
        viewHolder.source.text = items[idx].exchangeOfTheLatestTrade ?: ""
        viewHolder.value.text = df.format(items[idx].lastTradePrice) ?: ""
        viewHolder.absoluteValueOfChanges.text = df.format(items[idx].changeInThePriceOfTheLastTradeInPoints) ?: ""

        if (items[idx].percentageChangeRelative != null) {
            val percentage = items[idx].percentageChangeRelative

            when {
                percentage!! < 0f -> {
                    viewHolder.changePercents.setTextColor(ContextCompat.getColor(context, R.color.text_fail))
                    viewHolder.changePercents.text = "${df.format(percentage)}%"
                }
                percentage > 0f -> {
                    viewHolder.changePercents.setTextColor(ContextCompat.getColor(context, R.color.text_success))
                    viewHolder.changePercents.text = "+${df.format(percentage)}%"
                }
                percentage == 0f -> {
                    viewHolder.changePercents.setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                }
            }

            val prevLastTradePrice = items[idx].prevLastTradePrice
            val lastTradePrice = items[idx].lastTradePrice
            if (prevLastTradePrice != null) {
                if (prevLastTradePrice > lastTradePrice!!) {
                    animateDown(viewHolder.changePercents, items[idx])
                } else if (prevLastTradePrice < lastTradePrice!!) {
                    animateUp(viewHolder.changePercents, items[idx])
                }
                items[idx].prevLastTradePrice = items[idx].lastTradePrice
            }
        }

        viewHolder.view.setOnClickListener {
            onClick(items[idx], idx)
        }
    }

    private fun colorByValue(value: Float): Int {
        return if (value > 0) {
            ContextCompat.getColor(context, R.color.text_success)
        } else {
            ContextCompat.getColor(context, R.color.text_fail)
        }
    }

    private fun animateUp(textView: TextView, quotation: Quotation) {
        val colorUp = ContextCompat.getColor(context, R.color.text_success)
        val colorWhite = ContextCompat.getColor(context, R.color.white)
        val colorTransparent = ContextCompat.getColor(context, R.color.transparent)

        val colorByValue = colorByValue(quotation.percentageChangeRelative!!)

        val bgAnim = ObjectAnimator
            .ofObject(textView, "backgroundColor", ArgbEvaluator(), colorUp, colorTransparent)

        val textAnim = ObjectAnimator
            .ofObject(textView, "textColor", ArgbEvaluator(), colorWhite, colorByValue)

        AnimatorSet().apply {
            playTogether(bgAnim, textAnim)
            duration = 500
            start()
        }
    }

    private fun animateDown(textView: TextView, quotation: Quotation) {
        val colorDown = ContextCompat.getColor(context, R.color.text_fail)
        val colorWhite = ContextCompat.getColor(context, R.color.white)
        val colorTransparent = ContextCompat.getColor(context, R.color.transparent)

        val colorByValue = colorByValue(quotation.percentageChangeRelative!!)

        val bgAnim = ObjectAnimator
            .ofObject(textView, "backgroundColor", ArgbEvaluator(), colorDown, colorTransparent)

        val textAnim = ObjectAnimator
            .ofObject(textView, "textColor", ArgbEvaluator(), colorWhite, colorByValue)

        AnimatorSet().apply {
            playTogether(bgAnim, textAnim)
            duration = 500
            start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_stock_quote,
                parent,
                false
            )
        )
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.label_title
        val lastUpdate: TextView = view.text_last_update
        val source: TextView = view.text_label_source
        val changePercents: TextView = view.label_change_percents
        val value: TextView = view.text_value
        val absoluteValueOfChanges: TextView = view.text_change_absolute
    }

    override fun getItemCount() = items.size

    fun updateItems(items: List<Quotation>) {
        this.items.clear()
        this.items.addAll(items)
    }
}