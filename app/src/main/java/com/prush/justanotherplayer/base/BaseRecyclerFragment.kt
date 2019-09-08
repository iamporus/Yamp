package com.prush.justanotherplayer.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.prush.justanotherplayer.R
import kotlinx.android.synthetic.main.base_recylerview_layout.*

open class BaseRecyclerFragment : Fragment(), BaseView {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.base_recylerview_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseRecyclerView.apply {
            layoutManager = getBaseLayoutManager()
        }
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
}