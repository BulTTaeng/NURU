package com.example.nuru.utility.paging3.community

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nuru.model.data.community.CommunityDTO
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

//PagingSource에서 그냥 query를 통해 데이터를 가져오고
//RemoteMediator에서 load()를 구현하는 경우도 있음 (return MediatorResult)

private const val STARTING_KEY = 0
private const val LOAD_DELAY_MILLIS = 3_000L


class CommunityPagingSource : PagingSource<QuerySnapshot, CommunityDTO>(){

    val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun getRefreshKey(state: PagingState<QuerySnapshot, CommunityDTO>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, CommunityDTO> {
        return try {
            val currentPage = params.key ?: db.collection("community").orderBy("time" , Query.Direction.DESCENDING).limit(30).get().addOnSuccessListener {

            }.addOnFailureListener{

            }.await()

            val dataDto = ArrayList<CommunityDTO>()

            for(it in currentPage.documents){

                val time = it["time"] as Timestamp
                val timeDate = Date(time.seconds * 1000)


                dataDto.add(CommunityDTO(
                    it["image"] as ArrayList<String> ,
                    it["contents"].toString() ,
                    it["title"].toString() ,
                    it["writer"].toString() ,
                    it.id,
                    it["likeId"] as ArrayList<String>,
                    it["commentsNum"] as Long,
                    timeDate
                )
                )
            }


            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage = db.collection("community").orderBy("time" , Query.Direction.DESCENDING).startAfter(lastVisibleProduct).limit(30).get().await()
            LoadResult.Page(
                data = dataDto,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    suspend fun reLoadAll(){

    }


}