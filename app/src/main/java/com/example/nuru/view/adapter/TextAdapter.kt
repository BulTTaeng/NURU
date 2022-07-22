package com.example.nuru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.databinding.CardviewSettingBinding
import com.example.nuru.databinding.TextViewAlarmBinding
import com.example.nuru.model.data.setting.SettingItem

class TextAdapter (private val context : Context) : RecyclerView.Adapter<TextAdapter.TextViewHolder>() {
    var data = mutableListOf<String>()

    inner class TextViewHolder(val binding : TextViewAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: String) {
            binding.txtAlarmText.text = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val binding = TextViewAlarmBinding.inflate(
            LayoutInflater.from(context), parent, false)

        return TextViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.onBind(data[position])
    }
}