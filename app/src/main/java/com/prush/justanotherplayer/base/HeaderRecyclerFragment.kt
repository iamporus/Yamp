package com.prush.justanotherplayer.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.R
import kotlinx.android.synthetic.main.base_recylerview_layout.baseRecyclerView
import kotlinx.android.synthetic.main.header_recylerview_layout.*

abstract class HeaderRecyclerFragment : BaseRecyclerFragment(), BaseView {

    override fun getLayoutResource(): Int {
        return R.layout.header_title_recylerview_layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseRecyclerView.apply {
            layoutManager = getBaseLayoutManager()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).apply {

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(shouldDisplayHomeAsUpEnabled())
        }

    }

    abstract fun shouldDisplayHomeAsUpEnabled(): Boolean

}