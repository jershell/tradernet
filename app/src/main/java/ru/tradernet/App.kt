package ru.tradernet

import androidx.multidex.MultiDexApplication
import ru.tradernet.data.repository.AppRepository
import ru.tradernet.data.repository.PersistentRepository
import ru.tradernet.data.repository.ResourceRepository
import ru.tradernet.presentation.vm.*
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.*
import ru.tradernet.data.repository.StockMarketRepository


class App : MultiDexApplication(), KodeinAware {
    override val kodein = Kodein {
        bind() from singleton { ResourceRepository(applicationContext) }
        bind() from singleton { PersistentRepository(applicationContext) }
        bind() from singleton { AppRepository(instance()) }
        bind() from singleton { StockMarketRepository(instance()) }

        bind<AuthScreenViewModel>() with provider {
            AuthScreenViewModel(instance(), instance())
        }

        bind<QuotesTabViewModel>() with provider {
            QuotesTabViewModel(instance(), instance())
        }
    }

    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.d("run App")
    }
}