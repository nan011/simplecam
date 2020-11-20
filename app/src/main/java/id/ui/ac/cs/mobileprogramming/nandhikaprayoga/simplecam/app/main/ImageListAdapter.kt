package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.main

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.R
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.preview.ImagePreviewActivity


class ImageListAdapter(
    context: Context,
    imagePaths: ArrayList<String>,
): RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    private var imagePaths = ArrayList<String>()
    private var mContext: Context

    init {
        this.imagePaths = imagePaths
        this.mContext = context
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById(R.id.mainImageView) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.main_image,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide
            .with(mContext)
            .asBitmap()
            .load(imagePaths[position])
            .into(holder.imageView)

        val isFirstItem = position == 0
        val isLastItem = position == itemCount - 1
        if (isFirstItem || isLastItem) {
            val gap = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                16F,
                mContext.resources.displayMetrics
            ).toInt()

            val marginParams = MarginLayoutParams(holder.imageView.layoutParams)

            if (isFirstItem) {
                marginParams.setMargins(gap, 0, gap / 2, 0)
            } else if (isLastItem) {
                marginParams.setMargins(gap / 2, 0, gap, 0)
            }

            val layoutParams = RelativeLayout.LayoutParams(marginParams)

            holder.imageView.layoutParams = layoutParams
        }

        holder.imageView.setOnClickListener {
            val imagePreviewIntent = Intent(mContext, ImagePreviewActivity::class.java)
            imagePreviewIntent.putExtra("IMAGE_PATH", imagePaths[position])
            mContext.startActivity(imagePreviewIntent)
        }
    }

    override fun getItemCount(): Int {
        return imagePaths.size
    }
}