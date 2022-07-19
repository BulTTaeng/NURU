package com.example.nuru.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuru.databinding.FarmImageViewBinding
import com.example.nuru.databinding.ImgviewForCommunityBinding
import com.example.nuru.view.activity.community.ShowImageActivity


class SensorImageAdapter(private val context: Context) :
    ListAdapter<String, SensorImageAdapter.SensorImageViewHolder>(IMAGE_DIFF_CALLBACK)
{

    inner class SensorImageViewHolder(
        private val binding: FarmImageViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: String) = with(binding) {
            val urt = Uri.parse(data)
            Glide.with(context).load(urt).into(imgSensorImage)

            val idx = absoluteAdapterPosition + 1
            txtSensorNum.text = idx.toString() + "번 센서"

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorImageViewHolder {
        val binding = FarmImageViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SensorImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SensorImageViewHolder, position: Int) {
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