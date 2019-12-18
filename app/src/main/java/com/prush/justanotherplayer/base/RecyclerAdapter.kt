package com.prush.justanotherplayer.base

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


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

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        listPresenter.onViewRecycled(holder)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDropped(fromPosition: Int, toPosition: Int) {
        listPresenter.onItemMoved(fromPosition, toPosition, this)
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

        fun onContextMenuClicked(position: Int) {}
    }

    interface OnCarousalItemClickListener {

        fun onCarousalItemClick(selectedItemPosition: Int)
    }
}

interface OnItemMovedListener {

    fun onItemMoved(fromPosition: Int, toPosition: Int)

    fun onItemDropped(fromPosition: Int, toPosition: Int)
}

class ItemTouchHelperCallback<T>(val adapter: RecyclerAdapter<T>) : ItemTouchHelper.Callback() {

    private var fromPosition = -1
    private var toPosition = -1

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
        )
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        if (fromPosition == -1) {
            fromPosition = viewHolder.adapterPosition
        }

        toPosition = target.adapterPosition

        adapter.onItemMoved(viewHolder.adapterPosition, toPosition)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        adapter.onItemDropped(fromPosition, toPosition)

        fromPosition = -1
        toPosition = -1
    }

}