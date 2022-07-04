package com.example.nuru.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.databinding.AddCommunityImageViewBinding


class AddCommunityAdapter(private var searchResultList : ArrayList<Uri> , private val context: Context): RecyclerView.Adapter<AddCommunityAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(
        private val binding: AddCommunityImageViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Uri) = with(binding) {
            imgImageInCommunity.setImageURI(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = AddCommunityImageViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    fun setSearchResultList(
        newsearchResultList: ArrayList<Uri>
    ) {
        searchResultList = newsearchResultList
        notifyDataSetChanged()
    }
}