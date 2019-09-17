package com.prush.justanotherplayer.base

import android.graphics.Canvas
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


class RecyclerAdapter<T>(
    private val listPresenter: ListPresenter<T>,
    private val itemInteractedListener: OnItemInteractedListener
) :
    RecyclerView.Adapter<BaseViewHolder>(), OnItemMovedListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return listPresenter.getViewHolder(parent.context, parent, viewType)
    }

    override fun getItemCount(): Int {
        return listPresenter.getItemsCount()
    }

    override fun getItemViewType(position: Int): Int {
        return listPresenter.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val newPosition = listPresenter.getHeaderInclusivePosition(position)

        listPresenter.onBindTrackRowViewAtPosition(
            holder.itemView.context,
            holder,
            holder.itemViewType,
            newPosition,
            itemInteractedListener
        )
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        listPresenter.onItemMoved(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
        listPresenter.onItemSwiped(position)
    }

    enum class ViewTypeEnum {

        CAROUSAL_LIST_ITEM_VIEW,
        FLAT_LIST_ITEM_VIEW,
        HEADER_LIST_ITEM_VIEW,
        HEADER_LIST_ITEM_ACTION_VIEW
    }

    interface OnItemInteractedListener {

        fun onItemClick(selectedTrackPosition: Int)

        fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {}

    }

    interface OnCarousalItemClickListener {

        fun onCarousalItemClick(selectedItemPosition: Int)
    }
}

interface OnItemMovedListener {

    fun onItemMoved(fromPosition: Int, toPosition: Int)

    fun onItemSwiped(position: Int)
}

class ItemTouchHelperCallback<T>(val adapter: RecyclerAdapter<T>) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
        )
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (viewHolder.itemViewType != target.itemViewType) {
            return false
        }

        adapter.onItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemSwiped(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val alpha = 1.0f - abs(dX) / viewHolder.itemView.width

            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        viewHolder.itemView.alpha = 1.0f
    }

}