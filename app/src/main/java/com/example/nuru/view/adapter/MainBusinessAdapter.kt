package com.example.nuru.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuru.databinding.ImgviewForCommunityBinding
import com.example.nuru.databinding.MainBusinessViewBinding
import com.example.nuru.model.data.community.Comments
import com.example.nuru.model.data.mainbusiness.MainBusinessEntity
import com.example.nuru.view.activity.community.ShowImageActivity
import com.example.nuru.viewmodel.community.CommentsViewModel

class MainBusinessAdapter (private val context: Context) :
    ListAdapter<MainBusinessEntity, MainBusinessAdapter.MainBusinessViewHolder>(MAIN_BUSINESS_DIFF_CALLBACK) {

    inner class MainBusinessViewHolder(
        private val binding: MainBusinessViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: MainBusinessEntity) = with(binding) {

            txtMainBusinessTitle.text = data.title

            itemView.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.link));
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainBusinessAdapter.MainBusinessViewHolder {
        val binding = MainBusinessViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MainBusinessViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainBusinessAdapter.MainBusinessViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        val MAIN_BUSINESS_DIFF_CALLBACK= object : DiffUtil.ItemCallback<MainBusinessEntity>() {
            override fun areItemsTheSame(oldItem: MainBusinessEntity, newItem: MainBusinessEntity): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: MainBusinessEntity, newItem: MainBusinessEntity): Boolean =
                oldItem == newItem
        }
    }
}