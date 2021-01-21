package pl.lodz.mobile.covidinfo.modules

import android.app.AlertDialog
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.utility.getColorForAttr
import java.util.*


class FilteredDialog {

    fun interface OnValueSelected {
        fun onValueSelected(position: Int, value: String)
    }

    class Builder(private val context: Context) {


        private lateinit var values: List<String>

        private var listener: OnValueSelected? = null

        fun setValues(values: List<String>): Builder {

            this.values = values
            return this
        }

        fun setOnItemSelect(listener: OnValueSelected): Builder {
            this.listener = listener
            return this
        }

        fun show(): AlertDialog {

            val view = LayoutInflater
                    .from(context)
                    .inflate(R.layout.search_list, null, false)

            val recycler = view.findViewById<RecyclerView>(R.id.recycler)
            val adapter = FilteredStringAdapter(Locale.getDefault())

            adapter.setValues(values)

            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            val searchView = view.findViewById<SearchView>(R.id.searchView)

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false
                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText == null) return true

                    adapter.filter(newText)
                    return true
                }
            })
            val searchIcon: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)

            val searchCloseIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)

            searchIcon.setColorFilter(context.getColorForAttr(R.attr.colorOnPrimary), PorterDuff.Mode.SRC_IN)
            searchCloseIcon.setColorFilter(context.getColorForAttr(R.attr.colorOnPrimary), PorterDuff.Mode.SRC_IN)

            val dialog = AlertDialog.Builder(context, R.style.Theme_CovidInfo_Dialog)
                    .setView(view)
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

            adapter.onItemClickListener = FilteredStringAdapter.OnItemClickListener { _, value, _, absolutePosition ->
                listener?.onValueSelected(absolutePosition, value)
                dialog.dismiss()
            }

            return dialog
        }
    }
}