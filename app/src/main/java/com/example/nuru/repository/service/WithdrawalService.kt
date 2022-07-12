package com.example.nuru.repository.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.nuru.R
import com.example.nuru.view.activity.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_setting.*

class WithdrawalService() : Service() {

    private var startMode: Int = 0             // 서비스가 종료되면 어떻게 할지 명시
    private var binder: IBinder? = null        // bind한 클라이언트를 위한 인터페이스
    private var allowRebind: Boolean = false   // onRebind가 allow 되는지 명시
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var broadcaster :  LocalBroadcastManager

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        // 이 서비스가 맨 처음으로 call 됐을 때만 수행
        Log.d("ONCREATE" , "This Service is new one!!")

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("OnStartCommand" , "Ready to start")
        revokeAccess()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("onDestroy" , "End service")
        super.onDestroy()
    }

    fun sendMessage(message: String) {
        val intent = Intent("DONE")
        intent.putExtra("KEYVALUE", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun revokeAccess() {
        val fAuth: FirebaseUser = firebaseAuth.getCurrentUser()!!
        val uuid = fAuth.uid

        fAuth!!.delete().addOnCompleteListener{
            if(it.isSuccessful){
                Log.d("[revokeAccess]","firebaseAuth 회원정보 delete 성공")

                FirebaseAuth.getInstance().signOut()
                db.collection("user").document(uuid).delete().addOnCompleteListener{
                    if(it.isSuccessful){
                        Log.d("[revokeAccess]","firebaseAuth 회원정보 delete 성공")
                        FirebaseAuth.getInstance().signOut()
                        sendMessage("good")
                    }
                    else{
                        sendMessage("error")
                    }
                }

            }
            else{
                sendMessage("error")
            }
        }
    }


}