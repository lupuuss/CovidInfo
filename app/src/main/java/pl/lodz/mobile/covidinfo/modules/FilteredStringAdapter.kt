package pl.lodz.mobile.covidinfo.modules

import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.toSpanned
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.utility.boldQuery
import pl.lodz.mobile.covidinfo.utility.htmlToSpanned
import java.text.Collator
import java.util.*

private class DisplayText(
        val value: String,
        val displayText: Spanned =  value.toSpanned()
)

class FilteredStringAdapter(
        private val locale: Locale
) : RecyclerView.Adapter<FilteredStringAdapter.ViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(view: View, value: String, relPosition: Int, absolutePosition: Int)
    }

    var onItemClickListener: OnItemClickListener? = null

    class ViewHolder(val displayText: TextView) : RecyclerView.ViewHolder(displayText)

    internal val collator = Collator.getInstance()

    private var valuesList: List<String>? = null

    private val dataSet: SortedList<DisplayText> = SortedList(
            DisplayText::class.java,
            object : SortedList.Callback<DisplayText>() {
                override fun areItemsTheSame(item1: DisplayText?, item2: DisplayText?): Boolean {

                    return item1 == item2
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    this@FilteredStringAdapter.notifyItemMoved(fromPosition, toPosition)
                }

                override fun onChanged(position: Int, count: Int) {
                    this@FilteredStringAdapter.notifyItemRangeChanged(position, count)
                }

                override fun onInserted(position: Int, count: Int) {
                    this@FilteredStringAdapter.notifyItemRangeInserted(position, count)
                }

                override fun onRemoved(position: Int, count: Int) {
                    this@FilteredStringAdapter.notifyItemRangeRemoved(position, count)
                }

                override fun compare(o1: DisplayText, o2: DisplayText): Int {
                    return collator.compare(o1.value, o2.value)
                }

                override fun areContentsTheSame(oldItem: DisplayText, newItem: DisplayText): Boolean {
                    return oldItem.displayText == newItem.displayText
                }

            })

    fun setValues(values: List<String>) {

        dataSet.clear()
        dataSet.addAll(values.map { DisplayText(it) })
        valuesList = values
    }

    fun filter(query: String) {

        if (query == "") {
            dataSet.clear()
            dataSet.addAll(valuesList?.map { DisplayText(it) } ?: emptyList())
        } else {

            valuesList?.let { notNullList ->

                val filtered = notNullList.filter {
                    it.toLowerCase(locale).contains(query.toLowerCase(locale))
                }.toList()

                dataSet.replaceAll(filtered.map { DisplayText(it, it.boldQuery(query).htmlToSpanned()) })
            }
        }
    }

    fun clearValues() {
        valuesList = null
        dataSet.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.querable_item, parent, false)

        return ViewHolder(view as TextView)
    }

    override fun getItemCount() = dataSet.size()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.displayText.text = dataSet[position].displayText
        holder.displayText.setOnClickListener {

            val value = dataSet[position].value
            val absolute = valuesList!!.indexOf(value)

            onItemClickListener?.onItemClick(it, value, position, absolute)
        }
    }
}