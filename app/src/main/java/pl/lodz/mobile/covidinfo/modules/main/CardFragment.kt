package pl.lodz.mobile.covidinfo.modules.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import pl.lodz.mobile.covidinfo.utility.dpToPixels

class CardFragment : BaseFragment() {

    private lateinit var pagerEnum: Pager

    fun interface OnClick {
        fun onClickCardFragment(pager: Pager)
    }

    enum class Pager {
        World, Poland
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_card, container, false)

        pagerEnum = Pager.valueOf(arguments?.getString(pagerEnumBundle)!!)

        view.findViewById<TextView>(R.id.title).setText(when(pagerEnum) {
            Pager.World -> R.string.world
            Pager.Poland -> R.string.poland
        })

        val pager = view.findViewById<ViewPager2>(R.id.pager)

        pager.setPageTransformer(MarginPageTransformer(dpToPixels(requireContext(), 20f)))

        pager.offscreenPageLimit = 2


        pager.adapter = when (pagerEnum) {
            Pager.World -> WorldFragmentsAdapter(childFragmentManager, lifecycle)
            Pager.Poland -> PolandFragmentsAdapter(childFragmentManager, lifecycle)
        }

        val tabsIndicator = view.findViewById<TabLayout>(R.id.tabsIndicator)

        TabLayoutMediator(tabsIndicator, pager) { _, _ -> }.attach()

        view.setOnClickListener {

            (activity as? OnClick)?.onClickCardFragment(pagerEnum)
        }

        return view
    }


    companion object {

        private const val pagerEnumBundle = "PagerEnumBundle"

        fun newInstance(pager: Pager): CardFragment {

            return CardFragment().apply {
                val bundle = Bundle()

                bundle.putString(pagerEnumBundle, pager.name)

                arguments = bundle
            }
        }
    }
}