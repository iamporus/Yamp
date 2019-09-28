package com.prush.justanotherplayer.base

interface BaseView {

    fun showEmptyLibrary()

    fun showProgress()

    fun hideProgress()

    fun displayError()

    fun showContextMenu(any: Any)

}