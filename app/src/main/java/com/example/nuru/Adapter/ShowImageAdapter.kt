package com.example.nuru.Adapter

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuru.R
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.activity_show_image.*
import android.view.MotionEvent

import android.widget.LinearLayout

import android.view.ViewGroup




class ShowImageAdapter (private var searchResultList : ArrayList<String> , private val context: Context): RecyclerView.Adapter<ShowImageAdapter.MyPagerViewHolder>() {
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var scaleFactor = 1.0f
    var img = ArrayList<ImageView>()
    var loc = 0
    // onCreateViewHolder() 메소드는 좀 전에 살펴본 ViewHolder 클래스 객체를 생성하는 역할을 하며, *.xml을 코틀린에서 사용할 수 있게 해줍니다.
    // 매개변수로 넘어오는 parent는 뷰를 보여줄 부모 뷰를 의미, viewType은 아이템에 따라 서로 다른 뷰홀더를 생성하고 싶을 때 사용할 수 있는 값
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagerViewHolder {

        // LayoutInflater 클래스는 레이아웃 XML 파일을 코틀린에서 사용할 수 있는 객체로 변환하는 역할
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_view, parent, false)
        return MyPagerViewHolder(view)
    }

    // onBindViewHolder() 메소드는 뷰홀더에 아이템을 설정하는 메소드입니다.
    // 매개변수로는 뷰홀더와 아이템을 보여줄 위치 값(position)이 있습니다.
    // onBindViewHolder() 메소드는 어댑터 생성자에 넘어온 데이터를 뷰홀더에 매칭해주는 역할을 합니다.
    override fun onBindViewHolder(holder: MyPagerViewHolder, position: Int) {
        holder.bind(searchResultList[position], position)
    }

    // 어댑터에 설정된 아이템 리스트의 크기를 반환하는 메소드
    // 어댑터 생성자로 넘어온 items의 크기를 반환하는 코드를 작성해야 합니다.
    override fun getItemCount(): Int {
        return searchResultList.size
    }

    // 뷰홀더 클래스를 정의: onCreateViewHolder() 메소드에서 뷰 객체로 생성한 *.xml 레이아웃이 ViewHolder 생성자에 지정된 것
    // 이를 통해 itemView.findViewById<TextView>(R.id.textView)처럼 코드를 작성해서 텍스트뷰를 찾을 수 있습니다.
    inner class MyPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ImageFarm : ImageView = itemView.findViewById<ImageView>(R.id.img_imageCommunity)
        private val txtImageNumber = itemView.findViewById<TextView>(R.id.txt_imageNumber)

        fun bind(farm_PhotoUrl: String, position: Int) {

            val urt = Uri.parse(farm_PhotoUrl)
            Glide.with(context).load(urt).into(ImageFarm)
            val p = position+1
            img.add(ImageFarm)

            txtImageNumber.text = p.toString() + "/" + searchResultList.size.toString()

            mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())

            ImageFarm.setOnTouchListener { view, motionEvent ->
                loc = position
                onTouchEvent1(motionEvent)
            }
            //ImageFarm.setImageURI(urt)
            //itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, bgColor))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    // 제스처 이벤트가 발생하면 실행되는 메소드
    fun onTouchEvent1(motionEvent: MotionEvent?): Boolean {
        // 제스처 이벤트를 처리하는 메소드를 호출
        mScaleGestureDetector!!.onTouchEvent(motionEvent)
        return true
    }

    // 제스처 이벤트를 처리하는 클래스
    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {

            scaleFactor *= scaleGestureDetector.scaleFactor

            // 최소 0.5, 최대 2배
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 3.0f))

            // 이미지에 적용
            img[loc].scaleX = scaleFactor
            img[loc].scaleY = scaleFactor
            return true
        }
    }
}