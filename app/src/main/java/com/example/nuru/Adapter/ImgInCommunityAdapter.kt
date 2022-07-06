package com.example.nuru.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuru.ShowImageActivity
import com.example.nuru.databinding.CardviewFarmBinding
import com.example.nuru.databinding.CommunityViewBinding
import com.example.nuru.databinding.ImgviewForCommunityBinding
import com.example.nuru.model.CommunityEntity
import com.example.nuru.model.Farm
import com.google.firebase.auth.FirebaseAuth

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
                val userCollection: Collection<String> = HashSet<String>(currentList)

                val list: ArrayList<String> = ArrayList<String>(userCollection)
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