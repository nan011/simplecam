package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.model.entities.image.Image


class ImageContainerAdapter(
    activity: AppCompatActivity,
    private var imageContainer: ArrayList<Pair<String, ArrayList<Image>>>
) : RecyclerView.Adapter<ImageContainerAdapter.ViewHolder>() {
    private var mActivity = activity

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            mActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val imageListAdapter = ImageListAdapter(mActivity, imageContainer[position].second)
        holder.nameView.text = imageContainer[position].first
        holder.imageRowRecyclerView.layoutManager = linearLayoutManager
        holder.imageRowRecyclerView.adapter = imageListAdapter

        val isFirstItem = position == 0
        val isLastItem = position == itemCount - 1
        if (isFirstItem || isLastItem) {
            val gap = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                48F,
                mActivity.resources.displayMetrics
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