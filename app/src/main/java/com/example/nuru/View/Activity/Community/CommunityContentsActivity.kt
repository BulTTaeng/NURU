package com.example.nuru.View.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.View.Adapter.CommentsAdapter
import com.example.nuru.View.Adapter.ImgInCommunityAdapter
import com.example.nuru.Model.Data.Community.Comments
import com.example.nuru.Model.Data.Community.CommunityEntity
import com.example.nuru.Model.Data.TMap.PushDTO
import com.example.nuru.R
import com.example.nuru.Utility.GetCurrentContext
import com.example.nuru.View.Activity.Community.EditCommunityActivity
import com.example.nuru.ViewModel.Community.CommentsViewModel
import com.example.nuru.ViewModel.ViewModelFactory.ViewModelFactoryForComments
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
    lateinit var viewModel : CommentsViewModel
    private lateinit var adapter: CommentsAdapter
    private lateinit var adapterForImage: ImgInCommunityAdapter
    var block = false

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_contents)
        singletonC.setcurrentContext(this)

        firebaseAuth = FirebaseAuth.getInstance()
        job = Job()

        val Intent: Intent = getIntent()

        val CommunityInfo = Intent.getParcelableExtra<CommunityEntity>("COMMUNITY")!!

        var title = CommunityInfo.title
        var contents = CommunityInfo.contents
        var image = CommunityInfo.image
        var writer = CommunityInfo.writer
        var id = CommunityInfo.id
        var like = CommunityInfo.like
        var comments = CommunityInfo.comments



        txt_TitleInContent.text = title
        txt_ContentInContent.text = contents
        txt_CommentsNumberInContents.text = comments.toString()
        widget_progressbarInCommunityImage.visibility = View.GONE
        //val urt = Uri.parse(image)
        //Glide.with(this).load(urt).into(img_ContentImage)

        btn_EditContents.isVisible = writer.equals(firebaseAuth.currentUser?.uid)
        btn_DeleteCommunity.isVisible = writer.equals(firebaseAuth.currentUser?.uid)

        val commentsRef = db.collection("comments").document(id!!).collection(id)
        viewModel = ViewModelProvider(this, ViewModelFactoryForComments(commentsRef))
            .get(CommentsViewModel::class.java)
        //viewModel = ViewModelProvider(this).get(commentsViewModel(commentsRef)::class.java)


        val docRef = db.collection("community").document(id!!)
        updateCommunityContents(docRef)

        adapter = CommentsAdapter(getCommunityContentsActivity() , viewModel)
        // Setting the Adapter with the recyclerview
        comments_recycleView.layoutManager = LinearLayoutManager(getCommunityContentsActivity())
        comments_recycleView.adapter = adapter
        observeData(id)

       /* commentsRef.addSnapshotListener{ value, e ->
            comments_info = ArrayList<Comments>()
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if(value != null){
                Thread.sleep(200L)
                //delay(1000L)
                val commentsNumber = value.size()
                docRef.update("commentsNum" , commentsNumber)

                txt_CommentsNumberInContents.text = commentsNumber.toString()
                edt_AddComments.text.clear()
                comments_info = ArrayList<Comments>()
                showComments(id.toString())
            }

        }*/



        if(like.contains(firebaseAuth.currentUser?.uid)){
            btn_LikeInContents.setColorFilter(Color.BLUE)
        }
        else{
            btn_LikeInContents.setColorFilter(Color.GRAY)
        }

        txt_LikeNumberInContents.text = like.size.toString()

        btn_CommentsInContents.setEnabled(false)

        val refdoc = db.collection("community").document(id)
        btn_LikeInContents.setOnClickListener {

            if (like.contains(firebaseAuth.currentUser?.uid)) {
                refdoc.update("likeId", FieldValue.arrayRemove(firebaseAuth.currentUser?.uid))
                like.remove(firebaseAuth.currentUser?.uid)
                recallThenumber(like)
                btn_LikeInContents.setColorFilter(Color.GRAY)
            } else {
                refdoc.update("likeId", FieldValue.arrayUnion(firebaseAuth.currentUser?.uid))
                like.add(firebaseAuth.currentUser?.uid.toString())
                recallThenumber(like)
                btn_LikeInContents.setColorFilter(Color.BLUE)
            }
        }


        btn_EditContents.setOnClickListener {
            if(writer.equals(firebaseAuth.currentUser?.uid)){
                val intent_to_EditCommunity = Intent(this, EditCommunityActivity::class.java)
                intent_to_EditCommunity.putExtra("CONTENTS",contents)
                intent_to_EditCommunity.putExtra("IMAGE",image)
                intent_to_EditCommunity.putExtra("TITLE",title)
                intent_to_EditCommunity.putExtra("IDD",id)

                startActivity(intent_to_EditCommunity)
            }
        }

        btn_DeleteCommunity.setOnClickListener {
            if(writer.equals(firebaseAuth.currentUser?.uid)){
                db.collection("community").document(id).delete()
                finish()
            }
        }

        //댓글 작성

        btn_AddComments.setOnClickListener {
            if(edt_AddComments.text.isNotEmpty() && edt_AddComments.text.isNotBlank()){
                CoroutineScope(Dispatchers.Main).launch {

                    val commentsRef = db.collection("comments").document(id).collection(id)
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
                                    }

                                }

                                var message = getString(R.string.new_comments) + "\n" + edt_AddComments.text
                                var fcmPush = FcmPush()
                                fcmPush?.sendMessage(writer, getString(R.string.alarm_message), message)
                                viewModel.updateComments()
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

    fun updateCommunityContents(docRef : DocumentReference){

        var updated_image = ArrayList<String>()

            CoroutineScope(Dispatchers.Main).async {
                widget_progressbarInCommunityImage.visibility = View.VISIBLE
                docRef.get().addOnSuccessListener {
                    txt_TitleInContent.text = it["title"].toString()
                    txt_ContentInContent.text = it["contents"].toString()
                    updated_image = it["image"] as ArrayList<String>

                    if(updated_image.isEmpty()){
                    }
                    else{
                        if(GetContext().isFinishing()){
                            //Toast.makeText(this,"잠시뒤 다시 시도해 주세요", Toast.LENGTH_LONG).show()
                        }
                        else{
                            //Glide.with(this).load(urt).into(img_ContentImage)
                            adapterForImage = ImgInCommunityAdapter(GetContext())
                            community_recycleViewInCommunityContents.adapter = adapterForImage
                            community_recycleViewInCommunityContents.layoutManager = LinearLayoutManager(GetContext(), LinearLayoutManager.HORIZONTAL, false)
                            adapterForImage.submitList(updated_image)
                        }
                    }
                    widget_progressbarInCommunityImage.visibility = View.GONE
                }
            }



    }

    fun recallThenumber(like :ArrayList<String>){
        txt_LikeNumberInContents.text = like.size.toString()
    }

    fun getCommunityContentsActivity() : CommunityContentsActivity {
        return this
    }

    /*private fun showComments(id : String){
        launch(coroutineContext) {
            try {
                //binding.widgetProgressBarInMyPage.isVisible = true // 로딩 표시
                //widget_ProgressBarInCommunity.visibility = View.VISIBLE
                var i = 1
                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    var commentsContents : String
                    var writer :String
                    var createtime : Timestamp
                    var name : String
                    if(commentsRef == null){
                        return@withContext
                    }
                    else{
                        commentsRef.orderBy("time", Query.Direction.DESCENDING).get()
                            .addOnSuccessListener {
                                //여기서 지금 get을 해버리니까 get하면서 comments collection을 건드려서 다시 addsnapshotlisner가 발동
                                //지금 강제로 ArrayList를 계속 리셋시켜서 막는중, 하지만 매우 비효율적
                                //그런데 time으로 sorting을 해야하는데 어떻게 하지?... 그냥 addsanpshotlistner에서 sort하면 될까?
                                //adapter는 또 어떻하냐아아?
                                if(it != null){
                                    comments_info = ArrayList<Comments>()
                                    for(doc in it){

                                        commentsContents = doc["contents"].toString()
                                        writer = doc["writer"].toString()
                                        createtime = doc["time"] as Timestamp
                                        name = doc["name"].toString()

                                        val timeToDate : Date = Date(createtime.seconds * 1000)

                                        comments_info.add(
                                            Comments(commentsContents , writer, timeToDate , name)
                                        )
                                        i+=1
                                    }

                                    val adapter = CommentsAdapter(getCommunityContentsActivity())
                                    comments_recycleView.layoutManager = LinearLayoutManager(getCommunityContentsActivity())

                                    adapter.currentPage = i
                                    // Setting the Adapter with the recyclerview

                                    comments_recycleView.adapter = adapter
                                }

                            }.addOnFailureListener{

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // error 해결 방법
                // Permission denied (missing INTERNET permission?) 인터넷 권한 필요
                // 또는 앱 삭제 후 재설치
            } finally {
                //binding.widgetProgressBarInMyPage.isVisible = false // 로딩 표시 완료 그런데 안됨 그래서 그냥 visibility로 먹임
                //widget_ProgressBarInCommunity.visibility = View.GONE
            }
            //binding.widgetProgressBarInMyPage.isVisible = false
        }
    }*/

    fun observeData(id : String){
        viewModel.fetchData().observe(
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
