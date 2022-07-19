package com.example.nuru.view.adapter

import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.nuru.databinding.CardviewCommentsBinding
import com.example.nuru.model.data.community.Comments
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.nuru.R
import com.example.nuru.utility.FindDifference
import com.example.nuru.viewmodel.community.CommentsViewModel

//Your Fragment's views should have a shorter lifecycle than your associated ViewModel,
// so it should be OK to pass it in as a constructor parameter.

class CommentsAdapter( private val context: Context , private val viewModel : CommentsViewModel) :
    ListAdapter<Comments, CommentsAdapter.CommentsViewHolder>(COMMENTS_DIFF_CALLBACK)
{
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()

    val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    val currentDate : String = sdf.format(Date())
    private lateinit var binding: CardviewCommentsBinding

    inner class CommentsViewHolder(
        private val binding: CardviewCommentsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Comments) = with(binding) {
            binding.comments= data
            Log.d("aaaa",data.commentsContents)

            txtCommentsContents.isEnabled = false
            btnEditCommentsDone.visibility = View.GONE
            btnEditCommentsDone.isEnabled = false
            val writetime : String = sdf.format(data.time)
            txtTimeDiffInComments.text = FindDifference.findDifference(writetime , currentDate)

            Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/nuru-a3203.appspot.com/o/profile_lightGray.png?alt=media&token=fbd9ab57-dbde-45e2-900d-b7ce20534888")
                .transform(CenterCrop(), RoundedCorners(100)).into(imgProfileInComments)
            if(data.writer.equals(auth.currentUser?.uid)){
                txtViewOptions.visibility = View.VISIBLE
            }
            else{
                txtViewOptions.visibility = View.GONE
            }

            // 댓글 옵션 메뉴
            txtViewOptions.setOnClickListener{
                val popupMenu = PopupMenu(context, txtViewOptions)
                popupMenu.inflate(R.menu.comments_menu)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        when(item?.itemId){
                            R.id.option_edit -> {
                                Log.d("수정 버튼!", "수정 버튼을 눌르셨습니다. $data")
                                txtCommentsContents.isEnabled = true
                                txtCommentsContents.paintFlags = Paint.UNDERLINE_TEXT_FLAG

                                btnEditCommentsDone.visibility = View.VISIBLE
                                btnEditCommentsDone.isEnabled = true
                                return true
                            }
                            R.id.option_delete -> {
                                Log.d("삭제 버튼!", "삭제 버튼을 눌르셨습니다. $data")
                                showAlert(data.id , data.communityId)
                                return true
                            }
                        }
                        return false
                    }
                })
                popupMenu.show()
            }

            btnEditCommentsDone.setOnClickListener {
                val editedText = txtCommentsContents.text.toString()
                db.collection("comments").document(data.communityId.toString())
                    .collection(data.communityId)
                    .document(data.id.toString())
                    .update("contents",editedText).addOnCompleteListener{
                        if(it.isSuccessful){
                            btnEditCommentsDone.visibility = View.GONE
                            btnEditCommentsDone.isEnabled = false
                            txtCommentsContents.setPaintFlags(txtCommentsContents.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
                            txtCommentsContents.isEnabled = false
                        }
                        else{
                            Toast.makeText(context ,context.getString(R.string.problem_try_later) , Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        //val view = LayoutInflater.from(parent.context)
        //   .inflate(R.layout.cardview_farm, parent, false)
        binding = CardviewCommentsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentsViewHolder(binding)


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

    companion object {
        val COMMENTS_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comments>() {
            override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean =
                oldItem == newItem
        }
    }

    fun showAlert(commentsId : String , communityId : String) {
        AlertDialog.Builder(context)
            .setTitle(R.string.delete_comments)
            .setPositiveButton("아니요") {
                    dialogInterface: DialogInterface, i: Int ->
            } .setNegativeButton("네") {
                    dialogInterface: DialogInterface, i: Int -> deleteComments(commentsId , communityId)
            } .show()
    }

    fun deleteComments(commentsId: String , communityId: String){
        db.collection("comments").document(communityId).collection(communityId).document(commentsId).delete()
        var size : Long =0
        db.collection("community").document(communityId).get().addOnCompleteListener {
            if(it.isSuccessful) {
                size = it.result["commentsNum"] as Long
                size--
                db.collection("community").document(communityId).update("commentsNum", size)
                viewModel.updateComments()
            }
            else{
                Toast.makeText(context , R.string.problem_try_later , Toast.LENGTH_LONG).show()
            }
        }

    }
}