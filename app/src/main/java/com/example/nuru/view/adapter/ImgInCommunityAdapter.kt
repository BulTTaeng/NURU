package com.example.nuru.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuru.databinding.ImgviewForCommunityBinding
import com.example.nuru.view.activity.community.ShowImageActivity

class ImgInCommunityAdapter(private val context: Context) :
    ListAdapter<String, ImgInCommunityAdapter.ImageViewHolder>(IMAGE_DIFF_CALLBACK)
{

    inner class ImageViewHolder(
        private val binding: ImgviewForCommunityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: String) = with(binding) {
            val urt = Uri.parse(data)
            Glide.with(context).load(urt).into(imgThumbnail)
            if(currentList.size == 1){
                txtNumber.visibility = View.GONE
            }
            else {
                val idx = absoluteAdapterPosition + 1
                txtNumber.text = idx.toString() + "/" + currentList.size.toString()
            }

            itemView.setOnClickListener{
                val intent= Intent(context , ShowImageActivity::class.java)
                val list: ArrayList<String> = ArrayList<String>(currentList)
                intent.putStringArrayListExtra("DATA", list)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImgviewForCommunityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        val IMAGE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }
}