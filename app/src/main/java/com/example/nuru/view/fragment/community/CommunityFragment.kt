package com.example.nuru.view.fragment.community

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.view.adapter.CommunityAdapter
import com.example.nuru.view.activity.community.AddCommunityActivity
import com.example.nuru.R
import com.example.nuru.databinding.FragmentCommunityBinding
import com.example.nuru.model.data.tmap.SearchResultEntity
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.view.activity.mypage.MyPageActivity
import com.example.nuru.viewmodel.community.CommunityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class CommunityFragment : Fragment() {

    private lateinit var adapter: CommunityAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()

    lateinit var UserId : String

    private lateinit var job: Job
    private lateinit var binding: FragmentCommunityBinding

    lateinit var myPageActivity : MyPageActivity

    private val viewModel by lazy { ViewModelProvider(this).get(CommunityViewModel::class.java) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPageActivity = context as MyPageActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_community, container, false)
        binding.fragment = this@CommunityFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        job = Job()

        UserId = firebaseAuth.currentUser!!.uid
        widget_ProgressBarInCommunity.visibility = View.GONE


        adapter = CommunityAdapter(myPageActivity, this)
        // Setting the Adapter with the recyclerview
        community_recycleView.layoutManager = LinearLayoutManager(myPageActivity)
        community_recycleView.adapter = adapter
        //observeData()
        getProducts()
        setProgressBarAccordingToLoadState()

        swipe_in_community.setOnRefreshListener {

            CoroutineScope(Dispatchers.IO).launch {
                val update = async {  adapter.refresh() }
                update.await()
                swipe_in_community.isRefreshing = false
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RETURN_FROM_ADD_COMMUNITY){
            val done = data?.getStringExtra("UPLOAD_DONE")

            if(done == "OK") {
                CoroutineScope(Dispatchers.IO).launch {
                    val update = async {  adapter.refresh() }
                    update.await()
                }
            }
        }
        else if(requestCode == DELETE_COMMUNITY){
            val done = data?.getStringExtra("DELETE_COMMUNITY_KEY")
            if(done == "DELETION") {
                Log.d("TTTTTTT",resultCode.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    widget_ProgressBarInCommunity.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        adapter.refresh()
                    }.join()

                    widget_ProgressBarInCommunity.visibility = View.GONE
                }
            }
        }
    }

    fun btnAddCommunity(view:View){
        val intent = Intent(requireContext(), AddCommunityActivity::class.java)
        startActivityForResult(intent , RETURN_FROM_ADD_COMMUNITY)
    }

    private fun getProducts() {
        lifecycleScope.launch {
            viewModel.communityData.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setProgressBarAccordingToLoadState() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                widget_ProgressBarInCommunity.isVisible = it.append is LoadState.Loading
            }
        }
    }


    /*fun observeData(){

        viewModel.fetchData().observe(
            viewLifecycleOwner, androidx.lifecycle.Observer {
                lifecycleScope.launch {
                    adapter.submitData(
                        it
                    ).let {
                        swipe_in_community.isRefreshing = false
                        widget_ProgressBarInCommunity.visibility = View.GONE
                        community_recycleView.smoothScrollToPosition(0)
                    }
                }
            }
        )
    }*/

    companion object{
        const val RETURN_FROM_ADD_COMMUNITY = 144
        const val DELETE_COMMUNITY = 902
    }

}