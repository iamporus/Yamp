package com.prush.justanotherplayer.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prush.justanotherplayer.model.Track

class TracksRecyclerAdapter(private val tracksListPresenter: TracksListPresenter, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<TracksRecyclerAdapter.TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(
            LayoutInflater.from(parent.context).inflate(
                android.R.layout.simple_list_item_1,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return tracksListPresenter.getTrackListCount()
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        tracksListPresenter.onBindTrackRowViewAtPosition(holder, position, itemClickListener)
    }


    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), TracksRowView {

        private var titleTextView: TextView = itemView.findViewById(android.R.id.text1)

        override fun setTrackTitle(title: String) {
            titleTextView.text = title
        }

    }

    interface OnItemClickListener {
        fun onItemClick(track: Track)
    }
}