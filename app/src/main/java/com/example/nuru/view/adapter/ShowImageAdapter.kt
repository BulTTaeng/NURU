package com.example.nuru.view.adapter

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.nuru.R
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.image_view.view.*


class ShowImageAdapter (private var searchResultList : ArrayList<String> , private val context: Context): RecyclerView.Adapter<ShowImageAdapter.MyPagerViewHolder>() {

    var img = ArrayList<TouchImageView>()

    inner class MyPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ImageFarm: TouchImageView =
            itemView.findViewById<TouchImageView>(R.id.img_imageCommunity)
        private val txtImageNumber = itemView.findViewById<TextView>(R.id.txt_imageNumber)

        fun bind(farm_PhotoUrl: String, position: Int) {

            val urt = Uri.parse(farm_PhotoUrl)
            Glide.with(context).load(urt).into(ImageFarm)
            val p = position + 1
            img.add(ImageFarm)

            txtImageNumber.text = p.toString() + "/" + searchResultList.size.toString()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_view, parent, false)
        return MyPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPagerViewHolder, position: Int) {
        holder.bind(searchResultList[position], position)
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }


}
