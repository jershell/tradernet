package ru.tradernet.presentation.vm

import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import ru.tradernet.core.BaseViewModel
import ru.tradernet.data.model.Quotation
import ru.tradernet.data.repository.AppRepository
import ru.tradernet.data.repository.StockMarketRepository


class QuotesTabViewModel(
    private val appRepository: AppRepository,
    private val stockMarketRepository: StockMarketRepository
) : BaseViewModel() {

    init {
        Logger.i("INIT VIEW_MODEL")
    }

    var quotes = MutableLiveData<List<Quotation>>(emptyList())

    fun unsubscribe() {
        stockMarketRepository.unsubscribeOnUpdate()
        quotes = MutableLiveData(emptyList())
    }

    fun subscribe(): MutableLiveData<List<Quotation>> {
        Logger.d("subscribe() ")
        tryIt {
            stockMarketRepository.getTopSecurities()

            stockMarketRepository.subscribeOnUpdate {
                quotes.postValue(it.values.toList())
            }
        }

        return quotes
    }
}