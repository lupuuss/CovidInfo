package pl.lodz.mobile.covidinfo.modules.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_ranking.*
import org.koin.android.scope.currentScope
import org.koin.core.parameter.parametersOf
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.base.BaseFragment

class RankingFragment : BaseFragment(), RankingContract.View {

    private var allowSwitchProperty: Boolean = true
    private var limit: Int = 0
    override var isLoading: Boolean = false
    override var isContentVisible: Boolean = false
    override var isContentLoadingError: Boolean = false

    private lateinit var adapter: RankingArrayAdapter
    private val items = mutableListOf<RankItem>()

    private lateinit var properties: List<CovidPropertyDto>

    private lateinit var presenter: RankingContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            limit = it.getInt(itemsLimitBundle)
            allowSwitchProperty = it.getBoolean(allowSwitchPropertyBundle)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = currentScope.get(parameters = { parametersOf(limit) })

        adapter = RankingArrayAdapter(requireContext(), items)

        rankingList.adapter = adapter

        rankingList.isNestedScrollingEnabled = true
        rankingList.isSmoothScrollbarEnabled = true

        propertyChoose.isEnabled = allowSwitchProperty
        propertyChoose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.setProperty(properties[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        presenter.init(this)
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

        fun newInstance(limit: Int = 0, allowSwitchProperty: Boolean = true): RankingFragment {

            val args = Bundle()

            args.putInt(itemsLimitBundle, limit)
            args.putBoolean(allowSwitchPropertyBundle, allowSwitchProperty)

            return RankingFragment().apply {
                arguments = args
            }
        }
    }
}