package com.example.nuru.repository.farm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nuru.R
import com.example.nuru.model.data.farm.Farm
import com.example.nuru.model.data.farm.FarmEntity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.Exception
import kotlin.collections.ArrayList

class MyFarmRepository(val farmRef : DocumentReference) {
    val db = FirebaseFirestore.getInstance()

    val _mutableData = MutableLiveData<MutableList<Farm>>()
    val _mutableImages = MutableLiveData<MutableList<String>>()
    val Farm: LiveData<MutableList<Farm>>
        get() = _mutableData

    val images : LiveData<MutableList<String>>
        get() = _mutableImages

    var isInit : Boolean = false

    init {
        updateFarm()
    }

    suspend fun getUserNameAndEmail(userId : String) : ArrayList<String> {
        val docRef = db.collection("user").document(userId)
        var resultString = ArrayList<String>()
        docRef.get().addOnSuccessListener {
            resultString.add(it["name"].toString())
            resultString.add(it["email"].toString())
        }.await()
        return resultString
    }

    fun updateFarm(){
        farmRef.get().addOnSuccessListener {
            var i =1

            var user_farm_info = ArrayList<Farm>()

            val username = it["name"] as String
            var userEmail = it["email"] as String
            val user_farm_list = it["farmList"] as ArrayList<String>
            //Log.d("userName", "DocumentSnapshot data: ${username} ")
            //txt_UserNameMyPage.text = username + "\n" +userEmail
            //Log.d("userName", "DocumentSnapshot data: ${document.data}")
            if (user_farm_list.isEmpty()) {
                user_farm_list.add("zerozero")
            }
            val Farm_Info = db.collection("farmList")

            Farm_Info.whereIn("farmId", user_farm_list).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("newwww", document.data["farmName"].toString())
                        val lati = document.data["latitude"] as Double
                        val longti = document.data["longitude"] as Double

                        val location = LatLng(lati, longti)

                        user_farm_info.add(
                            Farm(
                                document.id,
                                document.data["farmName"].toString(),
                                document.data["farmPhoto"] as ArrayList<String>,
                                location,
                                document.data["farmAddress"].toString(),
                                document.data["farmOwner"].toString(),
                                i,
                                document.data["products"].toString()
                            )
                        )

                        i++

                    }
                    _mutableData.value = user_farm_info
                }
        }
    }

    suspend fun addFarm(farmEntity : FarmEntity) : Boolean{
        Log.d("hhhhhhhhhhhhh", "hhhhhhhhhhh")
        val data = hashMapOf(
            "farmAddress" to farmEntity.farmAddress,
            "farmId" to farmEntity.farmId,
            "farmOwner" to farmEntity.farmOwner,
            "farmName" to farmEntity.farmName,
            "latitude" to farmEntity.latitude,
            "longitude" to farmEntity.longitude,
            "products" to farmEntity.products,
            "farmPhoto" to farmEntity.farmPhoto,
            "farmAdmin" to farmEntity.farmAdmin
        )

        val tempdata = hashMapOf(
            "humidity" to 0.0,
            "information" to "센서가 없어요",
            "temperature" to 0.0,
            "weather" to "센서가 없어요"
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
            db.collection("user").document(farmEntity.farmOwner)
                .update("farmList", FieldValue.arrayUnion(docId)).await()
            db.collection("farmInformation").document(docId).set(tempdata).await()
            true
        } catch (e: FirebaseException) {
            Log.d("error", e.toString())
            false
        }
    }

    ///////////////addAdmin///////////////
    suspend fun addAdmin(addId : String , farmId : String) : Boolean{
        return try {
            db.collection("farmList").document(farmId).update("farmAdmin" , FieldValue.arrayUnion(addId)).addOnSuccessListener {
                true
            }.addOnFailureListener{
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("Error" , e.toString())
            false
        }
    }

    suspend fun updateImage(id : String) : Boolean{
        return try {
            db.collection("farmList").document(id).get().addOnSuccessListener {
                _mutableImages.value = it["farmPhoto"] as ArrayList<String>
                true
            }.addOnFailureListener {
                false
            }.await()
            true
        }catch (e : Exception){
            Log.d("Error" , e.toString())
            false
        }
    }

}