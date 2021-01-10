package pl.lodz.mobile.covidinfo.modules.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.koin.android.scope.currentScope
import org.koin.core.parameter.parametersOf
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment
import pl.lodz.mobile.covidinfo.utility.dpToPixels

class RankingFragment : BaseFragment(), RankingContract.View {

    private var customHeightDp: Int? = null
    private var allowSwitchProperty: Boolean = true
    private var limit: Int = 0
    override var isLoading: Boolean = true
        set (value) {
            field = value
            progressBar.isVisible = value
        }

    override var isContentVisible: Boolean = false
        set (value) {
            field = value
            rankingList.isInvisible = !value
        }

    override var isContentLoadingError: Boolean = false
        set (value) {
            field = value
            errorMessage.isVisible = value
        }

    private lateinit var adapter: RankingArrayAdapter
    private val items = mutableListOf<RankItem>()

    private lateinit var properties: List<CovidPropertyDto>

    private lateinit var presenter: RankingContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            limit = it.getInt(itemsLimitBundle)
            allowSwitchProperty = it.getBoolean(allowSwitchPropertyBundle)

            if (it.containsKey(customHeightDpBundle)) {
                customHeightDp = it.getInt(customHeightDpBundle)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ranking, container, false)


        customHeightDp?.let {

            view.findViewById<ListView>(R.id.rankingList)
                    .layoutParams
                    .height = dpToPixels(requireContext(), it)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = currentScope.get(parameters = { parametersOf(limit) })

        adapter = RankingArrayAdapter(requireContext(), items)

        rankingList.adapter = adapter

        rankingList.isNestedScrollingEnabled = true
        rankingList.isSmoothScrollbarEnabled = true

        refreshButton.setOnClickListener { presenter.refresh() }

        presenter.init(this)

        propertyChoose.isEnabled = allowSwitchProperty
        propertyChoose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.setProperty(properties[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.close()
    }

    override fun setPossibleProperties(properties: List<CovidPropertyDto>) {
        this.properties = properties

        propertyChoose.adapter = ArrayAdapter(
                requireContext(),
                R.layout.ranking_property_spinner_item,
                properties.map { it.localizedName }
        )
    }

    override fun setCurrentProperty(currentPropertyDto: CovidPropertyDto) {
        propertyChoose.setSelection(properties.indexOf(currentPropertyDto))
    }

    override fun addNextPositionToRanking(name: String, value: String) {
        items.add(RankItem(name, value))
        rankingList.adapter = adapter
    }

    override fun clearRanking() {
        items.clear()
        rankingList.adapter = adapter
    }

    companion object {

        private const val itemsLimitBundle = "ItemsLimitBundle"
        private const val allowSwitchPropertyBundle = "AllowSwitchPropertyBundle"
        private const val customHeightDpBundle = "CustomHeightDpBundle"

        fun newInstance(
            limit: Int = 0,
            allowSwitchProperty: Boolean = true,
            customHeightDp: Int? = null
        ): RankingFragment {

            val args = Bundle()

            args.putInt(itemsLimitBundle, limit)
            args.putBoolean(allowSwitchPropertyBundle, allowSwitchProperty)

            customHeightDp?.let {
                args.putInt(customHeightDpBundle, it)
            }

            return RankingFragment().apply {
                arguments = args
            }
        }
    }
}