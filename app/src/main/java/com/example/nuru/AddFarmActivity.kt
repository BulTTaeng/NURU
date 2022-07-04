package com.example.nuru

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nuru.utility.GetCurrentContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_farm.*

class AddFarmActivity : AppCompatActivity() {
    var latitude : Double = 0.0
    var longtitude : Double = 0.0

    var address : String = "주소"

    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        singletonC.setcurrentContext(this)
        firebaseAuth = FirebaseAuth.getInstance()

        txt_farmAddress.text = getString(R.string.press_address_find)

        val Intent = intent

        val b = intent.extras
        if(b == null){
            txt_farmAddress.text = getString(R.string.press_address_find)
        }
        else{
            address = Intent.getStringExtra("ADDRESS").toString()
            latitude = b.getDouble("latitude")
            longtitude = b.getDouble("longtitude")
            txt_farmAddress.text = address
        }


        btn_FindAddress.setOnClickListener {
            val Intent = Intent(this, SearchAddressActivity2::class.java)
            startActivity(Intent)
            finish()
            //MyPageController.navigate(R.id.action_addFarmFragment_to_myPageFragment)
        }

        btn_AddFarmInfo.setOnClickListener {
            val farmname = edt_farmName.text
            val products = edt_products.text
            val UserId = firebaseAuth.currentUser?.uid
            val pro = products.split(",") as List<String>
            var temp = ArrayList<String>()
            temp.add(getString(R.string.empty_image))
            var t = ArrayList<String>()
            t.add(UserId.toString())
            val data = hashMapOf(
                "farmAddress" to address.toString(),
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

            val docRef = db.collection("farmList")
                docRef.add(data)
                .addOnSuccessListener { documentReference ->

                    docRef.document(documentReference.id).update("farmId" , documentReference.id)
                    db.collection("user").document(UserId.toString()).update("farmList", FieldValue.arrayUnion(documentReference.id))
                    db.collection("farmInformation").document(documentReference.id).set(tempdata)
                }
                .addOnFailureListener { e ->
                    Log.w("ErrorInAddingFarm", "Error adding document", e)
                }

            finish()


        }

    }
}