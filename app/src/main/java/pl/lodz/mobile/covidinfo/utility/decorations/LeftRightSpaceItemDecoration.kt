package pl.lodz.mobile.covidinfo.utility.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LeftRightSpaceItemDecoration(private val mSpaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = mSpaceHeight
        outRect.right = mSpaceHeight
    }
}