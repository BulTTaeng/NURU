package com.example.nuru.repository.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.nuru.viewmodel.login.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WithdrawalService() : Service() {
    private var startMode: Int = 0             // 서비스가 종료되면 어떻게 할지 명시
    private var binder: IBinder? = null        // bind한 클라이언트를 위한 인터페이스
    private var allowRebind: Boolean = false   // onRebind가 allow 되는지 명시
    lateinit var broadcaster :  LocalBroadcastManager
    private val userViewModel : UserViewModel = UserViewModel()

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        // 이 서비스가 맨 처음으로 call 됐을 때만 수행

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        revokeAccess()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun sendMessage(message: String) {
        val intent = Intent("DONE")
        intent.putExtra("KEYVALUE", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun revokeAccess() {
        var check : Boolean = false
        CoroutineScope(Dispatchers.IO).launch {
            async {
                check = userViewModel.deleteAccount()
            }.await()
            when (check) {
                true -> sendMessage("good")
                false -> sendMessage("error")
            }
        }
    }
}