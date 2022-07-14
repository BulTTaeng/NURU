package com.example.nuru.view.fragment.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.view.adapter.CommunityAdapter
import com.example.nuru.view.activity.community.AddCommunityActivity
import com.example.nuru.R
import com.example.nuru.databinding.FragmentCommunityBinding
import com.example.nuru.model.data.tmap.SearchResultEntity
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.viewmodel.community.CommunityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.coroutines.*

class CommunityFragment : Fragment() {

    private lateinit var adapter: CommunityAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()

    lateinit var UserId : String

    private lateinit var job: Job
    private lateinit var binding: FragmentCommunityBinding

    private val viewModel by lazy { ViewModelProvider(this).get(CommunityViewModel::class.java) }

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


        adapter = CommunityAdapter(requireContext())
        // Setting the Adapter with the recyclerview
        community_recycleView.layoutManager = LinearLayoutManager(requireContext())
        community_recycleView.adapter = adapter
        observeData()

        swipe_in_community.setOnRefreshListener {

            CoroutineScope(Dispatchers.IO).launch {
                val update = async {  viewModel.updateView() }
                update.await()
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TTTTTTT",resultCode.toString())
        if(requestCode == RETURN_FROM_ADD_COMMUNITY){
            val done = data?.getStringExtra("UPLOAD_DONE")

            if(done == "OK") {
                CoroutineScope(Dispatchers.IO).launch {
                    val update = async { viewModel.updateView() }
                    update.await()
                }
            }
        }
        else if(resultCode == DELETE_COMMUNITY){
            CoroutineScope(Dispatchers.IO).launch {
                val update = async {  viewModel.updateView() }
                update.await()
            }
        }
    }

    fun btnAddCommunity(view:View){
        val intent = Intent(requireContext(), AddCommunityActivity::class.java)
        startActivityForResult(intent , RETURN_FROM_ADD_COMMUNITY)
    }


    fun observeData(){
        viewModel.fetchData().observe(
            viewLifecycleOwner, androidx.lifecycle.Observer {
                adapter.submitList(it.map{
                    it.copy()
                }
                ).let{
                    swipe_in_community.isRefreshing = false
                    widget_ProgressBarInCommunity.visibility = View.GONE
                    community_recycleView.smoothScrollToPosition(0)
                }
            }
        )
    }

    companion object{
        const val RETURN_FROM_ADD_COMMUNITY = 144
        const val DELETE_COMMUNITY = 902
    }

}