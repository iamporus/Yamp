package com.prush.justanotherplayer.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.model.Track
import com.prush.justanotherplayer.utils.getAlbumArtUri
import kotlinx.android.synthetic.main.base_recylerview_layout.*

open class BaseRecyclerFragment : Fragment(), BaseView {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, state: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResource(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseRecyclerView.apply {
            layoutManager = getBaseLayoutManager()
        }
    }

    open fun getLayoutResource(): Int {
        return R.layout.base_recylerview_layout
    }

    open fun getBaseLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun showProgress() {
        progressBar.show()
    }

    override fun hideProgress() {
        progressBar.hide()
    }

    override fun showEmptyLibrary() {
        emptyLayout.visibility = View.VISIBLE
        baseRecyclerView.visibility = View.INVISIBLE
    }

    override fun displayError() {
        if (activity != null)
            Snackbar.make(
                (activity as AppCompatActivity).findViewById(android.R.id.content),
                "Something went wrong",
                Snackbar.LENGTH_SHORT
            ).show()
    }

    override fun showContextMenu(any: Any) {

        val track = any as Track

        val tempContext: Context = this.context!!

        val view = layoutInflater.inflate(R.layout.context_menu_modal_sheet, null, false)
        val dialog = BottomSheetDialog(tempContext, R.style.Theme_Design_BottomSheetDialog)

        val imageView = view.findViewById<ImageView>(R.id.rowArtImageView)
        val titleTextView = view.findViewById<TextView>(R.id.rowTitleTextView)
        val subTitleTextView = view.findViewById<TextView>(R.id.rowSubtitleTextView)

        titleTextView.text = track.title
        subTitleTextView.text = track.artistName

        Glide.with(tempContext)
            .asBitmap()
            .load(getAlbumArtUri(tempContext, track.albumId))
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageView.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    imageView.setImageBitmap(
                        BitmapFactory.decodeResource(
                            tempContext.resources,
                            R.drawable.playback_track_icon
                        )
                    )
                }

            })

        dialog.setContentView(view)
        dialog.show()
    }
}