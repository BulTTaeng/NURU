package com.example.nuru.view.activity.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.nuru.R
import com.example.nuru.databinding.ActivityAddFarmBinding
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.view.activity.map.SearchAddressActivity2
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_farm.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class AddFarmActivity : AppCompatActivity() {
    var latitude: Double = 0.0
    var longtitude: Double = 0.0

    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityAddFarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add_farm)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_farm)
        binding.activity = this@AddFarmActivity
        singletonC.setcurrentContext(this)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.progressBarAddFarm.visibility = View.GONE

        val Intent = intent

        val b = intent.extras
        if (b == null) {
            binding.address = getString(R.string.press_address_find)
        } else {
            latitude = b.getDouble("latitude")
            longtitude = b.getDouble("longtitude")
            binding.address = Intent.getStringExtra("ADDRESS").toString()
        }

    }

    fun btnFindAddress(view: View) {
        val Intent =
            Intent(this, com.example.nuru.view.activity.map.SearchAddressActivity2::class.java)
        startActivity(Intent)
        finish()
        //MyPageController.navigate(R.id.action_addFarmFragment_to_myPageFragment)
    }

    fun btnAddFarmInfo(view: View) {
        binding.progressBarAddFarm.visibility = View.VISIBLE
        var ok: Boolean = true
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                ok = addFarmInfoFirebase()
            }.join()
            if (ok) {
                finish()
            } else {
                Log.d("error", "FirebaseWriting Failed")
                binding.progressBarAddFarm.visibility = View.GONE
            }
        }
    }

    suspend fun addFarmInfoFirebase(): Boolean {
        val farmname = edt_farmName.text
        val products = edt_products.text
        val UserId = firebaseAuth.currentUser?.uid
        val pro = products.split(",") as List<String>
        var temp = ArrayList<String>()
        temp.add(getString(R.string.empty_image))
        var t = ArrayList<String>()
        t.add(UserId.toString())
        val data = hashMapOf(
            "farmAddress" to binding.address.toString(),
            "farmId" to "farmId".toString(),
            "farmOwner" to UserId.toString(),
            "farmName" to farmname.toString(),
            "latitude" to latitude.toDouble(),
            "longitude" to longtitude.toDouble(),
            "products" to products.toString(),
            "farmId" to "aaa",
            "farmPhoto" to temp,
            "farmAdmin" to t
        )

        val tempdata = hashMapOf(
            "humidity" to 0.0,
            "information" to getString(R.string.no_sensor),
            "temperature" to 0.0,
            "weather" to getString(R.string.no_sensor)
        )
        var docId: String = ""

        return try {
            val docRef = db.collection("farmList")

            docRef.add(data)
                .addOnSuccessListener { it ->
                    docId = it.id
                }
                .addOnFailureListener { e ->
                    Log.w("ErrorInAddingFarm", "Error adding document", e)
                }.await()

            docRef.document(docId).update("farmId", docId).await()
            db.collection("user").document(UserId.toString())
                .update("farmList", FieldValue.arrayUnion(docId)).await()
            db.collection("farmInformation").document(docId).set(tempdata).await()
            true
        } catch (e: FirebaseException) {
            Log.d("error", e.toString())
            false
        }


    }
}