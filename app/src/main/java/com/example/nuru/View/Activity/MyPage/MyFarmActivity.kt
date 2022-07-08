package com.example.nuru.View.Activity.MyPage

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.nuru.R
import com.example.nuru.databinding.ActivityMyFarmBinding
import com.example.nuru.Utility.GetCurrentContext
import com.example.nuru.View.Activity.Community.AddImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_my_farm.*


class MyFarmActivity : AppCompatActivity() ,  PopupMenu.OnMenuItemClickListener {
    lateinit var binding: ActivityMyFarmBinding

    lateinit var farm_id : String
    var singletonC = GetCurrentContext.getInstance()

    var weather = ""
    var humidity : Double = 0.0
    var information = ""
    var temperature :Double = 0.0


    val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var UserId : String

    lateinit var info_farmList : DocumentReference
    lateinit var farminfo2 : DocumentReference

    lateinit var farm_Photo : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_farm)
        singletonC.setcurrentContext(this)

        val Intent = intent
        farm_id = Intent.getStringExtra("FARMID").toString()

        info_farmList = db.collection("farmList").document(farm_id)
        firebaseAuth = FirebaseAuth.getInstance()

        UserId = firebaseAuth.currentUser!!.uid

        info_farmList.addSnapshotListener{ snapshot , e ->

            widget_progressBarInMyFarm.visibility = View.GONE

            if (e != null) {
                Log.w("error", "Listen failed.", e)
                return@addSnapshotListener
            }

            info_farmList.get().addOnSuccessListener { document ->
                if (document != null) {
                    var temp = document["farmPhoto"]
                    if(temp != null){
                        farm_Photo = document["farmPhoto"] as ArrayList<String>
                        call()
                    }
                    else{
                        farm_Photo.add(getString(R.string.empty_image))
                        call()
                    }


                } else {

                }
            }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }

        var viewPager = findViewById(R.id.viewPager) as ViewPager2

        // 뷰페이저 사용을 위한 Adapter 생성 후 뷰페이저에 Adapter를 연결



        btn_Add.setOnClickListener {
            if(farm_id.equals("zerozero")){
                Toast.makeText(this , getString(R.string.default_farm) , Toast.LENGTH_LONG).show();
            }
            else{
                showPopup(btn_Add)
            }

        }

    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v) // PopupMenu 객체 선언
        popup.menuInflater.inflate(R.menu.menu_button, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
            R.id.btn_AddAdmin -> AddAdmin()
            R.id.btn_addImage -> AddImage()
        }

        return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
    }

    fun AddAdmin(){
        val intent_address = Intent(this, AddAdminActivity()::class.java)
        intent_address.putExtra("FARMID",farm_id )
        startActivity(intent_address)
    }

    fun AddImage(){
        val intent_addimage = Intent(this, AddImageActivity::class.java)
        intent_addimage.putExtra("FARMID",farm_id )
        startActivity(intent_addimage)
    }

    fun call(){
        widget_progressBarInMyFarm.visibility = View.VISIBLE
        farminfo2 = db.collection("farmInformation").document(farm_id)

        farminfo2.get().addOnSuccessListener {
            if(it != null){

                weather = it["weather"].toString()
                humidity = it["humidity"] as Double
                information = it["information"].toString()
                temperature = it["temperature"] as Double

                txt_Information.text = information
                txt_Weather.text = weather
                txt_humidity.text = "{$humidity} %"
                txt_temperature.text = "{$temperature} C"

                viewPager.adapter = CustomPagerAdapter_Myfarm()
            }
            else{
                Log.d("Document not found" , "document2 no found")
            }

        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
        widget_progressBarInMyFarm.visibility = View.GONE
    }

    fun getContext(): MyFarmActivity {
        return this
    }

    fun showAlert() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.sure_to_delete_farm))
            .setPositiveButton(getString(R.string.no)) {
                    dialogInterface: DialogInterface, i: Int ->
            } .setNegativeButton(getString(R.string.yes)) {
                    dialogInterface: DialogInterface, i: Int -> deleteFarm()
            } .show()
    }

    fun deleteFarm(){
        db.collection("user").document(UserId).get().addOnSuccessListener {
            var tt = it["farmList"] as ArrayList<String>

            if(tt.size == 1 && tt[0].equals("zerozero")){
                Toast.makeText(this , getString(R.string.cannot_delete_farm) , Toast.LENGTH_LONG).show()
            }
            else{
                db.collection("user").document(UserId).update("farmList" , FieldValue.arrayRemove(farm_id))
            }
        }
        finish()
    }


    inner class CustomPagerAdapter_Myfarm : RecyclerView.Adapter<CustomPagerAdapter_Myfarm.MyPagerViewHolder>() {

        // onCreateViewHolder() 메소드는 좀 전에 살펴본 ViewHolder 클래스 객체를 생성하는 역할을 하며, *.xml을 코틀린에서 사용할 수 있게 해줍니다.
        // 매개변수로 넘어오는 parent는 뷰를 보여줄 부모 뷰를 의미, viewType은 아이템에 따라 서로 다른 뷰홀더를 생성하고 싶을 때 사용할 수 있는 값
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagerViewHolder {

            // LayoutInflater 클래스는 레이아웃 XML 파일을 코틀린에서 사용할 수 있는 객체로 변환하는 역할
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_other, parent, false)
            return MyPagerViewHolder(view)
        }

        // onBindViewHolder() 메소드는 뷰홀더에 아이템을 설정하는 메소드입니다.
        // 매개변수로는 뷰홀더와 아이템을 보여줄 위치 값(position)이 있습니다.
        // onBindViewHolder() 메소드는 어댑터 생성자에 넘어온 데이터를 뷰홀더에 매칭해주는 역할을 합니다.
        override fun onBindViewHolder(holder: MyPagerViewHolder, position: Int) {
            holder.bind(farm_Photo[position], position)
        }

        // 어댑터에 설정된 아이템 리스트의 크기를 반환하는 메소드
        // 어댑터 생성자로 넘어온 items의 크기를 반환하는 코드를 작성해야 합니다.
        override fun getItemCount(): Int {
            return farm_Photo.size
        }

        // 뷰홀더 클래스를 정의: onCreateViewHolder() 메소드에서 뷰 객체로 생성한 *.xml 레이아웃이 ViewHolder 생성자에 지정된 것
        // 이를 통해 itemView.findViewById<TextView>(R.id.textView)처럼 코드를 작성해서 텍스트뷰를 찾을 수 있습니다.
        inner class MyPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val ImageFarm : ImageView= itemView.findViewById<ImageView>(R.id.img_FarmImage)
            private val DeleteFarmButton : ImageView= itemView.findViewById<ImageView>(R.id.btn_DeleteFarmImage)

            fun bind(farm_PhotoUrl: String, position: Int) {

                val urt = Uri.parse(farm_PhotoUrl)
                Glide.with(getContext()).load(urt).into(ImageFarm)

                DeleteFarmButton.setOnClickListener {
                    _showAlert()
                }
                //ImageFarm.setImageURI(urt)
                //itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, bgColor))
            }

            fun deleteFarmPage(){
                val farminfo3 = db.collection("farmList").document(farm_id)

                farminfo3.get().addOnSuccessListener {
                    var farm_photo_id = it["farmPhoto"] as ArrayList<String>
                    if(farm_photo_id[position].equals(getString(R.string.empty_image))){
                        Toast.makeText(getContext() , getString(R.string.cannot_delete_farm_image), Toast.LENGTH_SHORT).show()
                    }
                    else{
                        farminfo3.update("farmPhoto" , FieldValue.arrayRemove(farm_photo_id[position]))
                    }

                }
            }

            fun _showAlert() {
                AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.sure_to_delete_farm_page))
                    .setPositiveButton(getString(R.string.no)) {
                            dialogInterface: DialogInterface, i: Int ->
                    } .setNegativeButton(getString(R.string.yes)) {
                            dialogInterface: DialogInterface, i: Int -> deleteFarmPage()
                    } .show()
            }
        }
    }


}