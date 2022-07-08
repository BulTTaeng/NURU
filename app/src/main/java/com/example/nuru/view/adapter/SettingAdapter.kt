package com.example.nuru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.R
import com.example.nuru.model.data.setting.SettingItem
import com.example.nuru.databinding.CardviewSettingBinding
import com.example.nuru.view.activity.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_setting.*

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


/*
class SettingAdapter(private val items: MutableList<SettingItem>): RecyclerView.Adapter<SettingAdapter.ResultViewHolder>()  {

    private var searchResultList: List<SettingItem> = items
    var currentPage = 1

    private lateinit var searchResultClickListener: (SettingItem) -> Unit


    inner class ResultViewHolder(
        private val binding: CardviewSettingBinding,
        private val searchResultClickListener: (SettingItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindData(data: SettingItem) = with(binding) {
            txtTitleSetting.text = data.title

        }

        fun bindViews(data: SettingItem) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        // inflates the constraint_view_design view
        // that is used to hold list item
        //val view = LayoutInflater.from(parent.context)
        //   .inflate(R.layout.cardview_farm, parent, false)
        val binding = CardviewSettingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResultViewHolder(binding , searchResultClickListener)


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSearchResultList(
        searchResultList: List<SettingItem>,
        searchResultClickListener: (SettingItem) -> Unit
    ) {
        this.searchResultList = this.searchResultList + searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }
}

*/