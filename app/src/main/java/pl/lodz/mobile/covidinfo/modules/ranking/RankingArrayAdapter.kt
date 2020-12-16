package pl.lodz.mobile.covidinfo.modules.ranking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import pl.lodz.mobile.covidinfo.R

data class RankItem(
        val title: String,
        val value: String
)

class RankingArrayAdapter(
        context: Context, list: List<RankItem>
) : ArrayAdapter<RankItem>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View =
                convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.ranking_item, parent, false)

        val rankItem = getItem(position)!!

        view.findViewById<TextView>(R.id.rankTitle).text = rankItem.title
        view.findViewById<TextView>(R.id.rankValue).text = rankItem.value

        return view
    }

}