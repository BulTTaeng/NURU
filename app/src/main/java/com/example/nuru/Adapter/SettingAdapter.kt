package com.example.nuru.Adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.SettingItem
import com.example.nuru.databinding.CardviewSettingBinding


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

