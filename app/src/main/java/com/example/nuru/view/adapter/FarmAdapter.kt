package com.example.nuru.view.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.R
import com.example.nuru.databinding.CardviewFarmBinding
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.view.activity.mypage.NewMyFarmActivity
import com.example.nuru.view.fragment.adminfarm.AdminFarmPageFragmentDirections
import com.example.nuru.viewmodel.farm.MyFarmViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FarmAdapter(private val context: Context , private val myFarMViewModel : MyFarmViewModel) :
    ListAdapter<Farm, FarmAdapter.FarmViewHolder>(FARM_DIFF_CALLBACK) {

    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    var UserId = firebaseAuth.currentUser!!.uid
    lateinit var binding : CardviewFarmBinding


    inner class FarmViewHolder(
        private val binding: CardviewFarmBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Farm) = with(binding) {
            binding.farm = data

            btnDeleteFarmm.setOnClickListener {
                showAlert(data.farm_id)
            }

            itemView.setOnClickListener {
                val navController = Navigation.findNavController(itemView)
                navController.navigate(AdminFarmPageFragmentDirections.actionAdminFarmPageFragmentToAdminFarm2Fragment(absoluteAdapterPosition))
                //TODO : 여기서 임시로 만들 농장이면 그냥 터치 이벤트 없애야함.
//                val Intent = Intent(context, NewMyFarmActivity::class.java)
//                Intent.putExtra("FARM",data)
//                context.startActivity(Intent)
            }
        }

    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        //val view = LayoutInflater.from(parent.context)
        //   .inflate(R.layout.cardview_farm, parent, false)
        binding = CardviewFarmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FarmViewHolder(binding)


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: FarmViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

    companion object {
        val FARM_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Farm>() {
            override fun areItemsTheSame(oldItem: Farm, newItem: Farm): Boolean =
                oldItem.farm_id == newItem.farm_id

            override fun areContentsTheSame(oldItem: Farm, newItem: Farm): Boolean =
                oldItem == newItem
        }
    }

    fun showAlert(farmId : String) {
        AlertDialog.Builder(context)
            .setTitle("정말 이 농장을 삭제 하실 건가요?")
            .setPositiveButton("아니요") {
                    dialogInterface: DialogInterface, i: Int ->
            } .setNegativeButton("네") {
                    dialogInterface: DialogInterface, i: Int -> deleteFarm(farmId)
            } .show()
    }

    fun deleteFarm(farmId : String){
        db.collection("user").document(UserId).get().addOnSuccessListener {
            var tt = it["farmList"] as ArrayList<String>

            if(tt.size == 1 && tt[0].equals("zerozero")){
                Toast.makeText(context , "농장 하나 이상 추가하기 전에 삭제가 불가능 합니다." , Toast.LENGTH_LONG).show()
            }
            else if(tt.isEmpty()){
                Toast.makeText(context , "농장 하나 이상 추가하기 전에 삭제가 불가능 합니다." , Toast.LENGTH_LONG).show()
            }
            else{
                db.collection("user").document(UserId).update("farmList" , FieldValue.arrayRemove(farmId))
                myFarMViewModel.updateFarm()
            }
        }
    }

}