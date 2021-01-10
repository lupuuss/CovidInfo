package pl.lodz.mobile.covidinfo.modules.twitter

import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import pl.lodz.mobile.covidinfo.R
import pl.lodz.mobile.covidinfo.modules.twitter.dto.TweetDto

class TweetsRecyclerAdapter(
        private val tweets: List<TweetDto>,
        private val childrenWrapsContent: Boolean = false
) : RecyclerView.Adapter<TweetsRecyclerAdapter.TweetViewHolder>() {

    class TweetViewHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.name)
        private val whenText: TextView = view.findViewById(R.id.whenTextView)
        private val content: TextView = view.findViewById(R.id.content)
        private val image: ImageView = view.findViewById(R.id.profileImage)

        fun fill(dto: TweetDto) {
            name.text = dto.userName
            whenText.text = dto.createdAt
            content.text = dto.text

            content.movementMethod = LinkMovementMethod.getInstance()

            Glide.with(view)
                .load(dto.userImageLink)
                .circleCrop()
                .placeholder(CircularProgressDrawable(this.view.context))
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {

        val layoutId = if (childrenWrapsContent)
            R.layout.tweet_item_wrap_content
        else
            R.layout.tweet_item_match_parent

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return TweetViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        holder.fill(tweets[position])
    }

    override fun getItemCount(): Int = tweets.size
}