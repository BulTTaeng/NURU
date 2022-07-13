package com.example.nuru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.model.data.setting.SettingItem
import com.example.nuru.databinding.CardviewSettingBinding

class SettingAdapter (private val context : Context) : RecyclerView.Adapter<SettingAdapter.SettingViewHolder>() {
    var data = listOf<SettingItem>()
    var currentPage = 1
    private lateinit var onClickListener: (SettingItem) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val binding = CardviewSettingBinding.inflate(
            LayoutInflater.from(context), parent, false)

        return SettingViewHolder(binding, onClickListener)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.onBind(data[position])
        holder.bindViews(data[position])
    }

    inner class SettingViewHolder(val binding : CardviewSettingBinding, private val onClickListener: (SettingItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: SettingItem) {
            binding.settingTitle = data
        }

        fun bindViews(data: SettingItem) {
            binding.root.setOnClickListener {
                onClickListener(data)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setOnClickListener(data: List<SettingItem>, onClickListener: (SettingItem) -> Unit) {
        this.data = data
        this.onClickListener = onClickListener
        notifyDataSetChanged()
    }
}