package com.example.nuru.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.databinding.CounselingCardViewBinding
import com.example.nuru.model.data.counsel.NameCard
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.viewmodel.counsel.CounselViewModel

class CounselAdapter (private val context: Context, private val viewModel : CounselViewModel) :
    ListAdapter<NameCard, CounselAdapter.CounselViewHolder>(CounselAdapter.DIFF_CALLBACK) {

    lateinit var binding : CounselingCardViewBinding

    inner class CounselViewHolder(private val binding: CounselingCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: NameCard) = with(binding) {
            binding.data = data

            btnTell.setOnClickListener{
                Log.d("dd","${data.company}/tel:${data.tel} 전화번호 누르셨습니다.")
                try {
                    var intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${data.tel}"))
                    context.startActivity(intent)
                } catch (exception: Exception) {
                    Toast.makeText(context , "전화 연결에 실패하셨습니다." , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CounselViewHolder {
        binding = CounselingCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CounselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CounselViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NameCard>() {
            override fun areItemsTheSame(oldItem: NameCard, newItem: NameCard): Boolean =
                oldItem.company == newItem.company

            override fun areContentsTheSame(oldItem: NameCard, newItem: NameCard): Boolean =
                oldItem == newItem
        }
    }
}
