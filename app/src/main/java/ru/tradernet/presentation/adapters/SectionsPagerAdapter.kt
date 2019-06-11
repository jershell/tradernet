package ru.tradernet.presentation.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.orhanobut.logger.Logger
import ru.tradernet.R
import ru.tradernet.presentation.screens.BlankScreenFragment
import ru.tradernet.presentation.tabs.QuotesFragmentTab
import ru.tradernet.presentation.tabs.SettingsFragmentTab

private val TAB_TITLES = arrayOf(
    R.string.quotes,
    R.string.settings,
    R.string.blankScreen
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> QuotesFragmentTab.newInstance()
            1 -> SettingsFragmentTab.newInstance()
            else -> BlankScreenFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}