package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R


class ImageContainerAdapter(
    context: Context,
    private var names: ArrayList<String>,
    imageContainer: ArrayList<ArrayList<String>>
): RecyclerView.Adapter<ImageContainerAdapter.ViewHolder>() {
    private var imageContainer = ArrayList<ArrayList<String>>()
    private var mContext: Context

    init {
        this.imageContainer = imageContainer
        this.mContext = context
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageRowParent = itemView.findViewById(R.id.imageRowParent) as FlexboxLayout
        val nameView = itemView.findViewById(R.id.nameView) as TextView
        val imageRowRecyclerView = itemView.findViewById(R.id.imageRow) as RecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.main_image_row,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val linearLayoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val imageListAdapter = ImageListAdapter(mContext, imageContainer[position])
        holder.nameView.text = names[position]
        holder.imageRowRecyclerView.layoutManager = linearLayoutManager
        holder.imageRowRecyclerView.adapter = imageListAdapter

        val isFirstItem = position == 0
        val isLastItem = position == itemCount - 1
        if (isFirstItem || isLastItem) {
            val gap = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                48F,
                mContext.resources.displayMetrics
            ).toInt()

            val marginParams = ViewGroup.MarginLayoutParams(holder.imageRowParent.layoutParams)

            if (isFirstItem) {
                marginParams.setMargins(0, gap, 0, gap / 2)
            } else if (isLastItem) {
                marginParams.setMargins(0, gap / 2, 0, gap)
            }

            val layoutParams = RelativeLayout.LayoutParams(marginParams)
            holder.imageRowParent.layoutParams = layoutParams
        }
    }

    override fun getItemCount(): Int {
        return imageContainer.size
    }
}