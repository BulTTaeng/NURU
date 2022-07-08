package com.example.nuru.view.adapter

import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            txtCommentsContents.isEnabled = false
            btnEditCommentsDone.visibility = View.GONE
            btnEditCommentsDone.isEnabled = false
            val writetime : String = sdf.format(data.time)
            txtTimeDiffInComments.text = findDifference(writetime , currentDate)

            Glide.with(context)
                .load("https://firebasestorage.googleapis.com/v0/b/nuru-a3203.appspot.com/o/profile_lightGray.png?alt=media&token=fbd9ab57-dbde-45e2-900d-b7ce20534888")
                .transform(CenterCrop(), RoundedCorners(100)).into(imgProfileInComments)
            if(data.writer.equals(auth.currentUser?.uid)){
                btnDeleteComments.visibility = View.VISIBLE
                btnDeleteComments.isClickable = true
                btnEditComments.visibility = View.VISIBLE
                btnEditComments.isClickable = true
            }
            else{
                btnDeleteComments.isClickable = false
                btnDeleteComments.visibility = View.GONE
                btnEditComments.isClickable = false
                btnEditComments.visibility = View.GONE

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
        binding.adapter = this
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

    fun btnEditComments(view : View){
        binding.txtCommentsContents.isEnabled = true
        binding.txtCommentsContents.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.btnEditCommentsDone.visibility = View.VISIBLE
        binding.btnEditCommentsDone.isEnabled = true
    }

    fun btnDeleteComments(view : View){
        showAlert(binding.comments!!.id , binding.comments!!.communityId)
    }

    fun btnEditCommentsDone(view : View){
        val editedText = binding.txtCommentsContents.text.toString()
        db.collection("comments").document(binding.comments?.communityId.toString())
            .collection(binding.comments!!.communityId)
            .document(binding.comments!!.id.toString())
            .update("contents",editedText).addOnCompleteListener{
                if(it.isSuccessful){
                    binding.btnEditCommentsDone.visibility = View.GONE
                    binding.btnEditCommentsDone.isEnabled = false
                    binding.txtCommentsContents.setPaintFlags(binding.txtCommentsContents.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
                }
                else{
                    Toast.makeText(context ,context.getString(R.string.problem_try_later) , Toast.LENGTH_LONG).show()
                }
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
            else if(difference_In_Days.toInt() in 1..30){
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