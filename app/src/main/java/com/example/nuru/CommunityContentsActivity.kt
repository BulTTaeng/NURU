package com.example.nuru

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.nuru.Adapter.CommentsAdapter
import com.example.nuru.Adapter.ImgInCommunityAdapter
import com.example.nuru.model.Comments
import com.example.nuru.model.PushDTO
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.viewmodel.CommentsViewModel
import com.example.nuru.viewModelFactory.viewModelFactoryForComments
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_community_contents.*
import kotlinx.android.synthetic.main.cardview_comments.*
import kotlinx.android.synthetic.main.fragment_my_page.*
import kotlinx.coroutines.*
import java.util.*
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


    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_contents)
        singletonC.setcurrentContext(this)

        firebaseAuth = FirebaseAuth.getInstance()
        job = Job()

        val Intent = intent
        var title = Intent.getStringExtra("TITLE").toString()
        var contents = Intent.getStringExtra("CONTENTS").toString()
        //var image = Intent.getStringExtra("IMAGE").toString()
        var image = Intent.getStringArrayListExtra("IMAGE").toString()
        var writer = Intent.getStringExtra("WRITER").toString()
        var id = Intent.getStringExtra("IDD")
        val args = intent.getBundleExtra("BUNDLE")
        var like = args?.getSerializable("ARRAYLIST") as ArrayList<String>
        var comments = intent.getSerializableExtra("COMMENTS") as Long

        txt_TitleInContent.text = title
        txt_ContentInContent.text = contents

        //val urt = Uri.parse(image)
        //Glide.with(this).load(urt).into(img_ContentImage)

        btn_EditContents.isVisible = writer.equals(firebaseAuth.currentUser?.uid)
        btn_DeleteCommunity.isVisible = writer.equals(firebaseAuth.currentUser?.uid)

        val commentsRef = db.collection("comments").document(id!!).collection(id)
        viewModel = ViewModelProvider(this, viewModelFactoryForComments(commentsRef))
            .get(CommentsViewModel::class.java)
        //viewModel = ViewModelProvider(this).get(commentsViewModel(commentsRef)::class.java)


        val docRef = db.collection("community").document(id!!)

        adapter = CommentsAdapter(getCommunityContentsActivity())
        // Setting the Adapter with the recyclerview
        comments_recycleView.layoutManager = LinearLayoutManager(getCommunityContentsActivity())
        comments_recycleView.adapter = adapter
        observerData(id)

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

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("error", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                txt_TitleInContent.text = snapshot["title"].toString()
                txt_ContentInContent.text = snapshot["contents"].toString()
                var updated_image = snapshot["image"] as ArrayList<String>
                if(updated_image.isEmpty()){
                    community_recycleViewInCommunityContents.visibility = View.GONE
                }
                else{
                    if(this.isFinishing()){
                        //Toast.makeText(this,"잠시뒤 다시 시도해 주세요", Toast.LENGTH_LONG).show()
                    }
                    else{
                        //Glide.with(this).load(urt).into(img_ContentImage)
                        adapterForImage = ImgInCommunityAdapter(this , updated_image)
                        community_recycleViewInCommunityContents.adapter = adapterForImage
                        community_recycleViewInCommunityContents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                        adapterForImage.setSearchResultList(updated_image) {
                            val intent= Intent(this , ShowImageActivity::class.java)
                            intent.putStringArrayListExtra("DATA", updated_image)
                            startActivity(intent)
                        }
                    }
                }

            } else {
                Log.d("current null", "Current data: null")
            }
        }

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

                val commentsRef = db.collection("comments").document(id).collection(id)
                val createTime = FieldValue.serverTimestamp()
                var nameofwriter :String
                nameofwriter =""
                db.collection("user").document(firebaseAuth.currentUser?.uid.toString()).get().addOnSuccessListener {
                    if(it.data!=null) {
                        nameofwriter = it["name"].toString()
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

                        var message = getString(R.string.new_comments) + "\n" + edt_AddComments.text
                        var fcmPush = FcmPush()
                        fcmPush?.sendMessage(writer, getString(R.string.alarm_message), message)
                    }
                }
                //푸시를 받을 유저의 UID가 담긴 destinationUid 값을 넣어준후 fcmPush클래스의 sendMessage 메소드 호출

            }
        }


    }

    fun recallThenumber(like :ArrayList<String>){
        txt_LikeNumberInContents.text = like.size.toString()
    }

    fun getCommunityContentsActivity() : CommunityContentsActivity{
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

    fun observerData(id : String){
        viewModel.fetchData().observe(
            this, androidx.lifecycle.Observer {
                widget_progressbarInCommunityContents.visibility = View.VISIBLE
                adapter.setListData(it)
                adapter.notifyDataSetChanged()
                txt_CommentsNumberInContents.text = adapter.getItemCount().toString()

                db.collection("community").document(id).update("commentsNum" ,adapter.getItemCount() as Int )
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
