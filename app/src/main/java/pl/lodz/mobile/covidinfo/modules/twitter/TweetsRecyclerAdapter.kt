package pl.lodz.mobile.covidinfo.modules.twitter

import android.app.ActionBar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto

class TweetsRecyclerAdapter(
    private val tweets: List<TweetDto>,
    private val orientation: TwitterFragment.Orientation
) : RecyclerView.Adapter<TweetsRecyclerAdapter.TweetViewHolder>() {

    class TweetViewHolder(
        val view: View,
        orientation: TwitterFragment.Orientation
    ) : RecyclerView.ViewHolder(view) {

        init {

            when (orientation) {
                TwitterFragment.Orientation.Vertical -> {
                    view.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                    view.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                }
                TwitterFragment.Orientation.Horizontal -> {
                    view.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    view.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                }
            }

        }

        private val name: TextView = view.findViewById(R.id.name)
        private val whenText: TextView = view.findViewById(R.id.whenTextView)
        private val content: TextView = view.findViewById(R.id.content)
        private val image: ImageView = view.findViewById(R.id.profileImage)

        fun fill(dto: TweetDto) {
            name.text = dto.userName
            whenText.text = dto.createdAt
            content.text = dto.text

            Glide.with(view)
                .load(dto.userImageLink)
                .circleCrop()
                .placeholder(CircularProgressDrawable(this.view.context))
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tweet_item, parent, false)

        return TweetViewHolder(view, orientation)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        holder.fill(tweets[position])
    }

    override fun getItemCount(): Int = tweets.size
}