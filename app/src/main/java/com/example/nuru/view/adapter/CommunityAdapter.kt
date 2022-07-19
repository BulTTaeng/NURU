package com.example.nuru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.nuru.R
import com.example.nuru.databinding.CommunityViewBinding
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.view.activity.community.CommunityContentsActivity
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.nuru.utility.FindDifference
import com.example.nuru.view.fragment.community.CommunityFragment


class CommunityAdapter(private val context: Context , private val communityFragment: CommunityFragment) :
    PagingDataAdapter<CommunityDTO, CommunityAdapter.CommunityViewHolder>(COMMUNITYENTITY_DIFF_CALLBACK)
{
    //remember!
    //stroke in seperate_item_in_recycleview is for line's thickness and color
    //solid is for background color.

    private lateinit var adapter: ImgInCommunityAdapter

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: CommunityViewBinding

    inner class CommunityViewHolder(
        private val binding: CommunityViewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: CommunityDTO) = with(binding) {

            //여기 if image empty로 나눠버리면 업데이트하는도중 잘못인식해서 이미지 사라짐...
            binding.community = data
            txtEachCommunityContents.text = data.contents

            adapter = ImgInCommunityAdapter(context)
            communityRecycleViewForImage.adapter = adapter
            communityRecycleViewForImage.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter.submitList(data.image)


            //Glide.with(context).load(urt).into(imgThumbnail)
            btnComments.setEnabled(false)

            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val currentDate : String = sdf.format(Date())
            val writetime : String = sdf.format(data.time)

            txtDate.text = FindDifference.findDifference(writetime , currentDate)

            val into = Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/nuru-a3203.appspot.com/o/profile_lightGray.png?alt=media&token=fbd9ab57-dbde-45e2-900d-b7ce20534888")
                .transform(CenterCrop(), RoundedCorners(60)).into(imgProfile)

            firebaseAuth = FirebaseAuth.getInstance()

            if(data.like.contains(firebaseAuth.currentUser?.uid)){
                btnLike.setColorFilter(Color.BLUE)
            }
            else{
                btnLike.setColorFilter(Color.GRAY)
            }

            itemView.setOnClickListener{
                try{
                    val db = FirebaseFirestore.getInstance()
                    db.collection("community").document(data.id).get().addOnCompleteListener {
                        if(it.isSuccessful){
                            if(it.result["title"] == null){
                                Toast.makeText(context , "해당 커뮤니티가 삭제되었어요 , 새로고침 해주세요" , Toast.LENGTH_LONG).show()
                            }
                            else{
                                val intent_to_CommunityCotents = Intent(context, CommunityContentsActivity::class.java)
                                intent_to_CommunityCotents.putExtra("COMMUNITY",data)
                                //context.startActivity(intent_to_CommunityCotents)

                                communityFragment.startActivityForResult(intent_to_CommunityCotents, CommunityFragment.DELETE_COMMUNITY)
                            }
                        }
                        else{
                            Toast.makeText(context, R.string.try_later, Toast.LENGTH_LONG).show()
                        }
                    }
                }catch (e : FirebaseException){
                    Toast.makeText(context, R.string.try_later, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        //val view = LayoutInflater.from(parent.context)
        //   .inflate(R.layout.cardview_farm, parent, false)
        binding = CommunityViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CommunityViewHolder(binding)


    }

    // return the number of the items in the list



    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        val community = getItem(position) ?: return
        holder.bindData(community)
    }

    companion object {
        val COMMUNITYENTITY_DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommunityDTO>() {
            override fun areItemsTheSame(oldItem: CommunityDTO, newItem: CommunityDTO): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CommunityDTO, newItem: CommunityDTO): Boolean =
                oldItem == newItem
        }
    }
}

