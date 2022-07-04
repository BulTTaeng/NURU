package com.example.nuru.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuru.databinding.CardviewFarmBinding
import com.example.nuru.databinding.CommunityViewBinding
import com.example.nuru.databinding.ImgviewForCommunityBinding
import com.example.nuru.model.CommunityEntity
import com.example.nuru.model.Farm
import com.google.firebase.auth.FirebaseAuth

class ImgInCommunityAdapter(private val context: Context , private val itemList : ArrayList<String>) : RecyclerView.Adapter<ImgInCommunityAdapter.ResultViewHolder>() {
    private lateinit var searchResultClickListener: (String) -> Unit
    var searchResultList = itemList

    inner class ResultViewHolder(

        private val binding: ImgviewForCommunityBinding,
        private val searchResultClickListener: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: String) = with(binding) {
            val urt = Uri.parse(data)
            Glide.with(context).load(urt).into(imgThumbnail)
            if(searchResultList.size == 1){
                txtNumber.visibility = View.GONE
            }
            else {
                val idx = absoluteAdapterPosition + 1
                txtNumber.text = idx.toString() + "/" + searchResultList.size.toString()
            }
        }

        fun bindViews(data: String) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ImgviewForCommunityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResultViewHolder(binding , searchResultClickListener)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bindData(itemList[position])
        holder.bindViews(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setSearchResultList(
        searchResultList1: ArrayList<String>,
        searchResultClickListener: (String) -> Unit
    ) {
        this.searchResultList = searchResultList1
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }
}