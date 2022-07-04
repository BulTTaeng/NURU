package com.example.nuru.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.nuru.SearchAddressActivity2
import com.example.nuru.ShowImageActivity
import com.example.nuru.databinding.CommunityViewBinding
import com.example.nuru.model.CommunityEntity
import com.google.firebase.auth.FirebaseAuth
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommunityAdapter(private val context: Context) : RecyclerView.Adapter<CommunityAdapter.ResultViewHolder>() {
    //remember!
    //stroke in seperate_item_in_recycleview is for line's thickness and color
    //solid is for background color.

    var mList = mutableListOf<CommunityEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListData(data:MutableList<CommunityEntity>){
        mList = data
        searchResultList = data
    }
    private lateinit var adapter: ImgInCommunityAdapter

    private var searchResultList: List<CommunityEntity> = mList
    var currentPage = 1
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var searchResultClickListener: (CommunityEntity) -> Unit

    @SuppressLint("NotifyDataSetChanged")
    inner class ResultViewHolder(

        private val binding: CommunityViewBinding,
        private val searchResultClickListener: (CommunityEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: CommunityEntity) = with(binding) {

            //여기 if image empty로 나눠버리면 업데이트하는도중 잘못인식해서 이미지 사라짐...

            txtTitle.text = data.title

            adapter = ImgInCommunityAdapter(context , data.image)
            communityRecycleViewForImage.adapter = adapter
            communityRecycleViewForImage.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter.setSearchResultList(data.image) {
                val intent= Intent(context , ShowImageActivity::class.java)
                intent.putStringArrayListExtra("DATA", data.image as ArrayList<String>)
                context.startActivity(intent)
            }


            //Glide.with(context).load(urt).into(imgThumbnail)
            btnComments.setEnabled(false)

            txtLikeNumber.text = data.like.size.toString()
            txtCommentsNumber.text = data.comments.toString()
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val currentDate : String = sdf.format(Date())
            val writetime : String = sdf.format(data.time)

            txtDate.text = findDifference(writetime , currentDate)

            //txtDate.text = currentDate.toString()

            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/nuru-a3203.appspot.com/o/profile_lightGray.png?alt=media&token=fbd9ab57-dbde-45e2-900d-b7ce20534888")
                .transform(CenterCrop(), RoundedCorners(60)).into(imgProfile)

            firebaseAuth = FirebaseAuth.getInstance()

            if(data.like.contains(firebaseAuth.currentUser?.uid)){
                btnLike.setColorFilter(Color.BLUE)
            }
            else{
                btnLike.setColorFilter(Color.GRAY)
            }

            /*btnLike.setOnClickListener {

                val refdoc = db.collection("community").document(data.id)
                if(data.like.contains(firebaseAuth.currentUser?.uid)){
                    refdoc.update("likeId" , FieldValue.arrayRemove(firebaseAuth.currentUser?.uid))
                    btnLike.setColorFilter(Color.GRAY)
                }
                else{
                    refdoc.update("likeId" , FieldValue.arrayUnion(firebaseAuth.currentUser?.uid))
                    btnLike.setColorFilter(Color.BLUE)
                }
                //btnLike.setBackgroundColor(Color.BLUE)
                //btnLike.setColorFilter(Color.BLUE)
            }*/
        }

        fun bindViews(data: CommunityEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    // create new views
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        //val view = LayoutInflater.from(parent.context)
         //   .inflate(R.layout.cardview_farm, parent, false)
        val binding = CommunityViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResultViewHolder(binding , searchResultClickListener)


    }

    // return the number of the items in the list
    @SuppressLint("NotifyDataSetChanged")
    override fun getItemCount(): Int {
        return mList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSearchResultList(
        searchResultList: List<CommunityEntity>,
        searchResultClickListener: (CommunityEntity) -> Unit
    ) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        this.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun findDifference(start_date: String?, end_date: String?): String {

        // SimpleDateFormat converts the
        // string format to date object
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            val d1 = sdf.parse(start_date)
            val d2 = sdf.parse(end_date)

            // Calucalte time difference
            // in milliseconds
            val difference_In_Time = d2.time - d1.time
            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            val difference_In_Seconds = ((difference_In_Time
                    / 1000)
                    % 60)
            val difference_In_Minutes = ((difference_In_Time
                    / (1000 * 60))
                    % 60)
            val difference_In_Hours = ((difference_In_Time
                    / (1000 * 60 * 60))
                    % 24)
            val difference_In_Years = (difference_In_Time
                    / (1000L * 60 * 60 * 24 * 365))
            val difference_In_Days = ((difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365)

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            if(difference_In_Years.toInt() != 0){
                return difference_In_Years.toString() + "년 전"
            }
            else if(difference_In_Days.toInt() > 30){
                return (difference_In_Days.toInt()/30).toString() + "개월 전"
            }
            else if(difference_In_Days.toInt() <= 30){
                return difference_In_Days.toInt().toString() + "일 전"
            }
            else if(difference_In_Hours.toInt() != 0){
                return difference_In_Hours.toInt().toString() + "시간 전"
            }
            else if(difference_In_Minutes.toInt() != 0){
                return difference_In_Minutes.toString() + "분 전"
            }
            else{
                return "방금 전"
            }

        } // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
            return " "
        }
    }


}