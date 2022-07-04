package com.example.nuru.Adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.databinding.CardviewFarmBinding
import com.example.nuru.model.Farm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FarmAdapter(private val context: Context) : RecyclerView.Adapter<FarmAdapter.ResultViewHolder>() {
    var mList = mutableListOf<Farm>()
    private var searchResultList: List<Farm> = mList

    fun setListData(data:MutableList<Farm>){
        searchResultList = data
        println(searchResultList)
    }
    var currentPage = 1

    private lateinit var searchResultClickListener: (Farm) -> Unit

    val db = FirebaseFirestore.getInstance()
    lateinit var UserId : String
    private lateinit var firebaseAuth: FirebaseAuth


    inner class ResultViewHolder(

        private val binding: CardviewFarmBinding,
        private val searchResultClickListener: (Farm) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Farm) = with(binding) {

            var products : String
            products = ""
            for(pname in data.products){
                products += pname
                products += " "
            }
            firebaseAuth = FirebaseAuth.getInstance()
            UserId = firebaseAuth.currentUser!!.uid


            txtFarmProducts.text = data.products.toString()
            txtFarmLocation.text = data.farm_address
            btnDeleteFarmm.setOnClickListener {
                showAlert(data.farm_id)
            }
        }

        fun bindViews(data: Farm) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }

    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        //val view = LayoutInflater.from(parent.context)
        //   .inflate(R.layout.cardview_farm, parent, false)
        val binding = CardviewFarmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResultViewHolder(binding , searchResultClickListener)


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return searchResultList.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    fun setSearchResultList(
        searchResultList: List<Farm>,
        searchResultClickListener: (Farm) -> Unit
    ) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
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
            }
        }
    }

}