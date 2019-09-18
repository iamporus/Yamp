package com.prush.justanotherplayer.ui.trackslibrary

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.base.BaseViewHolder
import com.prush.justanotherplayer.base.RecyclerAdapter
import com.prush.justanotherplayer.model.Track_State
import com.prush.justanotherplayer.utils.getTimeStringFromSeconds

open class TrackViewHolder(itemView: View) : BaseViewHolder(itemView), TrackItemRow {

    override fun setDuration(duration: Long) {
        val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        durationTextView.text = getTimeStringFromSeconds(duration / 1000)
    }

    override fun setTrackState(
        state: Track_State,
        listener: RecyclerAdapter.OnItemInteractedListener
    ) {
        val container: ViewGroup = itemView.findViewById(R.id.rowLayout)
        val nowPlayingImageView: ImageView = itemView.findViewById(R.id.nowPlayingImageView)

        val handleImageView: ImageView? = itemView.findViewById(R.id.dragHandleImageView)

        when (state) {
            Track_State.PLAYED -> {
                container.alpha = 0.5f
                nowPlayingImageView.visibility = View.INVISIBLE
                handleImageView?.visibility = View.INVISIBLE
                setOnTouchListener(null)
            }
            Track_State.PLAYING, Track_State.PAUSED -> {
                container.alpha = 1f
                nowPlayingImageView.visibility = View.VISIBLE
                handleImageView?.visibility = View.INVISIBLE
                setOnTouchListener(null)
            }
            Track_State.IN_QUEUE -> {
                container.alpha = 1f
                nowPlayingImageView.visibility = View.INVISIBLE
                handleImageView?.visibility = View.VISIBLE
                setOnTouchListener(listener)
            }
        }


    }

    override fun setTrackNumber(trackNumber: Int) {
        val trackNumberTextView: TextView = itemView.findViewById(R.id.trackNumberTextView)
        trackNumberTextView.text = if (trackNumber > 0) trackNumber.toString() else "-"
    }
}