package com.example.nuru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.R
import com.example.nuru.databinding.CardviewCityProductBinding
import com.example.nuru.viewmodel.fitcheck.FitCheckViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class CityProductAdapter(private val context: Context, private var check : Int, private val viewModel : FitCheckViewModel) :
    ListAdapter<String, CityProductAdapter.CityProductViewHolder>(CITY_PRODUCT_DIFF_CALLBACK)
{

    inner class CityProductViewHolder(
        private val binding: CardviewCityProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: String) = with(binding) {
            txtCityProduct.text = data

            itemView.setOnClickListener {
                when(check){
                    0 -> {
                        viewModel.selectedCity.set(data)
                        CoroutineScope(Dispatchers.IO).launch {
                            async {  viewModel.getCountry(data)    }.await()
                            submitList(viewModel.fetchCountryData().value)
                            check += 1
                        }
                    }
                    1 -> {
                        viewModel.selectedCountry.set(" $data")
                        Navigation.findNavController(itemView).navigate(R.id.action_fitCheckAddressCityFragment_to_fitCheckSelectFragment)
                    }
                    2 -> {
                        viewModel.selectedProduct.set(data)
                        Navigation.findNavController(itemView).navigate(R.id.action_fitCheckAddressCityFragment_to_fitCheckSelectFragment)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityProductViewHolder {
        val binding = CardviewCityProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CityProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityProductViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        val CITY_PRODUCT_DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }
}