package com.example.nuru.view.activity.community

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
import com.example.nuru.model.data.community.Comments
import com.example.nuru.model.data.community.CommunityEntity
import com.example.nuru.model.data.tmap.PushDTO
import com.example.nuru.R
import com.example.nuru.databinding.ActivityCommunityContentsBinding
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.view.activity.mypage.NewMyFarmActivity
import com.example.nuru.viewmodel.community.CommentsViewModel
import com.example.nuru.viewmodel.community.CommunityContentsViewModel
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForComments
import com.example.nuru.viewmodel.viewmodelfactory.ViewModelFactoryForCommunityContents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_community_contents.*
import kotlinx.coroutines.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class CommunityContentsActivity : AppCompatActivity() , CoroutineScope {

    val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    var singletonC = GetCurrentContext.getInstance()
    lateinit var comments_info : ArrayList<Comments>
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_community_contents)
        binding.activity = this@CommunityContentsActivity

        val Intent: Intent = getIntent()

        val CommunityInfo = Intent.getParcelableExtra<CommunityEntity>("COMMUNITY")!!

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
        //val urt = Uri.parse(image)
        //Glide.with(this).load(urt).into(img_ContentImage)

        btn_EditContents.isVisible = writer.equals(firebaseAuth.currentUser?.uid)
        btn_DeleteCommunity.isVisible = writer.equals(firebaseAuth.currentUser?.uid)
        //viewModel = ViewModelProvider(this).get(commentsViewModel(commentsRef)::class.java)

        //updateCommunityContents(docRef)

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

    override fun onResume() {
        super.onResume()
        communityContentsViewModel.updateCommunityContents()
    }

    fun btnEditContents(view :View){
        val info = communityContentsViewModel.fetchData().value
        if(info?.writer.equals(firebaseAuth.currentUser?.uid)){
            val intent_to_EditCommunity = Intent(this, EditCommunityActivity::class.java)
            intent_to_EditCommunity.putExtra("COMMUNITYENTITY",info)
            startActivity(intent_to_EditCommunity)
        }
    }

    fun btnDeleteCommunity(view : View){
        if(communityContentsViewModel.fetchData().value?.writer.equals(firebaseAuth.currentUser?.uid)){
            db.collection("community").document(communityContentsViewModel.fetchData().value?.id.toString()).delete()
            finish()
        }
    }

    fun btnLikeInContents(view : View){
        val info = communityContentsViewModel.fetchData().value
        if (info!!.like.contains(firebaseAuth.currentUser?.uid)) {
            docRef.update("likeId", FieldValue.arrayRemove(firebaseAuth.currentUser?.uid))
            //info.like.remove(firebaseAuth.currentUser?.uid)
            //recallThenumber(info.like)
            communityContentsViewModel.updateCommunityContents()
            btn_LikeInContents.setColorFilter(Color.GRAY)
        } else {
            docRef.update("likeId", FieldValue.arrayUnion(firebaseAuth.currentUser?.uid))
            //info.like.add(firebaseAuth.currentUser?.uid.toString())
            //recallThenumber(info.like)
            communityContentsViewModel.updateCommunityContents()
            btn_LikeInContents.setColorFilter(Color.BLUE)
        }
    }

    fun btnAddComments(view : View){
        val info = communityContentsViewModel.fetchData().value
        val id = info!!.id
        if(edt_AddComments.text.isNotEmpty() && edt_AddComments.text.isNotBlank()){
            CoroutineScope(Dispatchers.Main).launch {

                val commentsRef = db.collection("comments").document(info!!.id).collection(id)
                val createTime = FieldValue.serverTimestamp()
                var nameofwriter :String
                nameofwriter =""

                widget_progressbarInCommunityContents.visibility = View.VISIBLE
                block = true
                CoroutineScope(Dispatchers.IO).async {
                    db.collection("user").document(firebaseAuth.currentUser?.uid.toString()).get().addOnCompleteListener{
                        if(it.isSuccessful) {
                            nameofwriter = it.result["name"].toString()
                            val data = hashMapOf(
                                "contents" to edt_AddComments.text.toString(),
                                "id" to "",
                                "communityId" to id,
                                "time" to createTime,
                                "writer" to firebaseAuth.currentUser?.uid,
                                "name" to nameofwriter
                            )

                            commentsRef.add(data).addOnSuccessListener {
                                val data = hashMapOf(
                                    "id" to it.id
                                )
                                commentsRef.document(it.id).update("id" , it.id)
                                edt_AddComments.text.clear()
                            }
                            commentsRef.get().addOnSuccessListener {
                                //TODO :: 동시접근 이슈 있음. DeleteComment도 마찬가지
                                var size : Long = 0
                                db.collection("community").document(id).get().addOnSuccessListener {
                                    size = it["commentsNum"] as Long
                                    size++
                                    txt_CommentsNumberInContents.text = size.toString()
                                    db.collection("community").document(id).update("commentsNum" , size)
                                    communityContentsViewModel.updateCommunityContents()
                                }

                            }

                            var message = getString(R.string.new_comments) + "\n" + edt_AddComments.text
                            var fcmPush = FcmPush()
                            fcmPush?.sendMessage(info!!.writer, getString(R.string.alarm_message), message)
                            commentsViewModel.updateComments()
                        }
                        else{
                            Toast.makeText(GetContext() , R.string.comments_rewrite, Toast.LENGTH_LONG).show()
                        }
                    }
                }.await()

                widget_progressbarInCommunityContents.visibility = View.GONE
                block = false

            }

            //푸시를 받을 유저의 UID가 담긴 destinationUid 값을 넣어준후 fcmPush클래스의 sendMessage 메소드 호출

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

    fun GetContext() : CommunityContentsActivity {
        return this
    }

    fun recallThenumber(like :ArrayList<String>){
        txt_LikeNumberInContents.text = like.size.toString()
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



}

class FcmPush() {
    val JSON = MediaType.parse("application/json; charset=utf-8")//Post전송 JSON Type
    val url = "https://fcm.googleapis.com/fcm/send" //FCM HTTP를 호출하는 URL
    val serverKey=
        "@string/firebaseMessaging_server_key"
    //Firebase에서 복사한 서버키
    var okHttpClient: OkHttpClient
    var gson: Gson

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get()//destinationUid의 값으로 푸시를 보낼 토큰값을 가져오는 코드
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var token = task.result!!["pushtoken"].toString()
                    Log.i("토큰정보", token)
                    var pushDTO = PushDTO()
                    pushDTO.to = token                   //푸시토큰 세팅
                    pushDTO.notification?.title = title  //푸시 타이틀 세팅
                    pushDTO.notification?.body = message //푸시 메시지 세팅

                    var body = RequestBody.create(JSON, gson?.toJson(pushDTO)!!)
                    var request = Request
                        .Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=" + serverKey)
                        .url(url)       //푸시 URL 세팅
                        .post(body)     //pushDTO가 담긴 body 세팅
                        .build()
                    okHttpClient?.newCall(request)?.enqueue(object : Callback {//푸시 전송
                    /*override fun onFailure(call: Call?, e: IOException?) {
                    }

                        override fun onResponse(call: Call?, response: Response?) {
                            println(response?.body()?.string())  //요청이 성공했을 경우 결과값 출력
                        }*/

                        override fun onFailure(request: Request?, e: java.io.IOException?) {
                            TODO("Not yet implemented")
                        }

                        override fun onResponse(response: Response?) {
                            Log.d("Success!" , (response?.body()?.string()).toString())
                        }
                    })
                }
            }
    }
}
