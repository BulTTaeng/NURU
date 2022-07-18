package com.example.nuru.view.activity.community

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.view.adapter.CommentsAdapter
import com.example.nuru.view.adapter.ImgInCommunityAdapter
import com.example.nuru.model.data.community.CommunityDTO
import com.example.nuru.R
import com.example.nuru.databinding.ActivityCommunityContentsBinding
import com.example.nuru.repository.FcmPush
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.view.activity.map.MapsActivity
import com.example.nuru.viewmodel.community.CommentsViewModel
import com.example.nuru.viewmodel.community.CommunityContentsViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForComments
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForCommunityContents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_community_contents.*
import kotlinx.android.synthetic.main.community_view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CommunityContentsActivity : AppCompatActivity() , CoroutineScope {

    val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    var singletonC = GetCurrentContext.getInstance()
    //public lateinit var commentsRef : CollectionReference
    lateinit var commentsViewModel : CommentsViewModel
    lateinit var communityContentsViewModel : CommunityContentsViewModel
    private lateinit var adapter: CommentsAdapter
    private lateinit var adapterForImage: ImgInCommunityAdapter
    var block = false
    private lateinit var binding: ActivityCommunityContentsBinding

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var docRef : DocumentReference
    val fcmPush = FcmPush()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_community_contents)
        binding.activity = this@CommunityContentsActivity

        val Intent: Intent = getIntent()

        val CommunityInfo = Intent.getParcelableExtra<CommunityDTO>("COMMUNITY")!!

        var id = CommunityInfo.id
        var writer = CommunityInfo.writer
        var like = CommunityInfo.like
        var image = CommunityInfo.image


        val commentsRef = db.collection("comments").document(id!!).collection(id)
        docRef = db.collection("community").document(id!!)
        commentsViewModel = ViewModelProvider(this, ViewModelFactoryForComments(commentsRef))
            .get(CommentsViewModel::class.java)

        communityContentsViewModel = ViewModelProvider(this , ViewModelFactoryForCommunityContents(docRef))
            .get(CommunityContentsViewModel::class.java)
        binding.communityContentsViewModel = communityContentsViewModel
        singletonC.setcurrentContext(this)

        firebaseAuth = FirebaseAuth.getInstance()
        job = Job()

        widget_progressbarInCommunityImage.visibility = View.GONE


        btn_EditContents.isVisible = writer.equals(firebaseAuth.currentUser?.uid)
        btn_DeleteCommunity.isVisible = writer.equals(firebaseAuth.currentUser?.uid)

        adapter = CommentsAdapter(getCommunityContentsActivity() , commentsViewModel)
        // Setting the Adapter with the recyclerview
        comments_recycleView.layoutManager = LinearLayoutManager(getCommunityContentsActivity())
        comments_recycleView.adapter = adapter

        adapterForImage = ImgInCommunityAdapter(GetContext())
        community_recycleViewInCommunityContents.adapter = adapterForImage
        community_recycleViewInCommunityContents.layoutManager = LinearLayoutManager(GetContext(), LinearLayoutManager.HORIZONTAL, false)
        adapterForImage.submitList(image)

        observeData(id)
        observeCommunityContentsData()


        if(like.contains(firebaseAuth.currentUser?.uid)){
            btn_LikeInContents.setColorFilter(Color.BLUE)
        }
        else{
            btn_LikeInContents.setColorFilter(Color.GRAY)
        }


        btn_CommentsInContents.setEnabled(false)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RETURN_FROM_EDIT){
            communityContentsViewModel.updateCommunityContents()
        }
    }

    override fun onBackPressed() {
        if(!block){
            super.onBackPressed()
        }
        else{
            Toast.makeText(this , R.string.problem_try_later, Toast.LENGTH_LONG).show()
        }
    }

    fun btnEditContents(view :View){
        val info = communityContentsViewModel.fetchData().value
        if(info?.writer.equals(firebaseAuth.currentUser?.uid)){
            val intent_to_EditCommunity = Intent(this, AddCommunityActivity::class.java)
            intent_to_EditCommunity.putExtra("COMMUNITYENTITY",info)
            startActivityForResult(intent_to_EditCommunity, RETURN_FROM_EDIT)
        }
    }

    fun btnDeleteCommunity(view : View){
        if(communityContentsViewModel.fetchData().value?.writer.equals(firebaseAuth.currentUser?.uid)){
            var done = true
            widget_progressbarInCommunityImage.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    done = communityContentsViewModel.deleteCommunityAndComments()
                }.join()
                if(done) {
                    val Intent = Intent()
                    Intent.putExtra("DELETE_COMMUNITY_KEY", "DELETION")
                    setResult(Activity.RESULT_OK , Intent)
                    finish()
                }
                else{
                    Toast.makeText(GetContext() , getString(R.string.try_later) , Toast.LENGTH_LONG).show()
                    widget_progressbarInCommunityImage.visibility = View.GONE
                }
            }

        }
    }

    fun btnLikeInContents(view : View){
        val info = communityContentsViewModel.fetchData().value

        if (info!!.like.contains(firebaseAuth.currentUser?.uid)) {
            communityContentsViewModel.deleteLike()
            btn_LikeInContents.setColorFilter(Color.GRAY)
        } else {
            communityContentsViewModel.addLike()
            btn_LikeInContents.setColorFilter(Color.BLUE)
        }
    }

    fun btnAddComments(view : View){
        val info = communityContentsViewModel.fetchData().value

        if(edt_AddComments.text.isNotEmpty() && edt_AddComments.text.isNotBlank()){
            CoroutineScope(Dispatchers.Main).launch {

                widget_progressbarInCommunityContents.visibility = View.VISIBLE
                block = true
                var success = false;
                CoroutineScope(Dispatchers.IO).async {
                    success = communityContentsViewModel.addComments(edt_AddComments.text.toString())
                }.await()

                if(success) {
                    commentsViewModel.updateComments()
                    var message = getString(R.string.new_comments) + "\n" + edt_AddComments.text
                    fcmPush.sendMessage(info!!.writer, getString(R.string.alarm_message), message)
                    edt_AddComments.text.clear()
                    widget_progressbarInCommunityContents.visibility = View.GONE
                    block = false
                }
                else{
                    Toast.makeText(GetContext() , R.string.comments_rewrite, Toast.LENGTH_LONG).show()
                    widget_progressbarInCommunityContents.visibility = View.GONE
                    block = false
                }

            }

        }
    }

    fun GetContext() : CommunityContentsActivity {
        return this
    }

    fun getCommunityContentsActivity() : CommunityContentsActivity {
        return this
    }

    fun observeData(id : String){
        commentsViewModel.fetchData().observe(
            this, androidx.lifecycle.Observer {
                widget_progressbarInCommunityContents.visibility = View.VISIBLE

                adapter.submitList(it.map{
                    it.copy()
                }
                )
                widget_progressbarInCommunityContents.visibility = View.GONE
            }
        )
    }

    fun observeCommunityContentsData(){
        communityContentsViewModel.fetchData().observe(
            this, androidx.lifecycle.Observer {
                widget_progressbarInCommunityImage.visibility = View.VISIBLE
                binding.communityContentsViewModel = communityContentsViewModel
                adapterForImage.submitList(communityContentsViewModel.fetchData().value!!.image)
                widget_progressbarInCommunityImage.visibility = View.GONE
            }
        )
    }

    companion object{
        const val RETURN_FROM_EDIT = 909
    }

}
