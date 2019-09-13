package com.prush.justanotherplayer.ui.trackslibrary

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.utils.getTimeStringFromSeconds

open class TrackViewHolder(itemView: View) : BaseViewHolder(itemView), TrackItemRow {

    override fun setDuration(duration: Long) {
        val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        durationTextView.text = getTimeStringFromSeconds(duration / 1000)
    }

    override fun markAsNowPlaying(isNowPlaying: Boolean) {
        val nowPlayingImageView: ImageView = itemView.findViewById(R.id.nowPlayingImageView)
        nowPlayingImageView.visibility = if (isNowPlaying) View.VISIBLE else View.GONE
    }

    override fun setTrackNumber(trackNumber: Int) {
        val trackNumberTextView: TextView = itemView.findViewById(R.id.trackNumberTextView)
        trackNumberTextView.text = if (trackNumber > 0) trackNumber.toString() else "-"
    }
}