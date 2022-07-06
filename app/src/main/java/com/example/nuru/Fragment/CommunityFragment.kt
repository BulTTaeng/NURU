package com.example.nuru.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuru.Adapter.CommunityAdapter
import com.example.nuru.AddCommunityActivity
import com.example.nuru.CommunityContentsActivity
import com.example.nuru.MyPageActivity
import com.example.nuru.R
import com.example.nuru.model.CommunityEntity
import com.example.nuru.utility.GetCurrentContext
import com.example.nuru.viewmodel.CommunityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CommunityFragment : Fragment() {

    private lateinit var adapter: CommunityAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    var singletonC = GetCurrentContext.getInstance()

    lateinit var UserId : String

    private lateinit var job: Job

    private val viewModel by lazy { ViewModelProvider(this).get(CommunityViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        job = Job()

        UserId = firebaseAuth.currentUser!!.uid



        adapter = CommunityAdapter(requireContext())
        // Setting the Adapter with the recyclerview
        community_recycleView1.layoutManager = LinearLayoutManager(requireContext())
        community_recycleView1.adapter = adapter
        observerData()

        btn_AddCommunity1.setOnClickListener {
            val intent = Intent(requireContext(), AddCommunityActivity::class.java)
            startActivity(intent)
        }

        btn_MyPageInCommunity1.setOnClickListener {
            /*val intent_mypage = Intent(requireContext(), MyPageActivity::class.java)
            startActivity(intent_mypage)
            finish()*/

            findNavController().navigate(R.id.myPageFragment)
        }

        swipe_in_community.setOnRefreshListener {
            CoroutineScope(Dispatchers.IO).launch {
                val update = async {  viewModel.updateView() }
                update.await()
            }
        }

    }

    fun observerData(){
        viewModel.fetchData().observe(
            this, androidx.lifecycle.Observer {
                adapter.submitList(it.map{
                    it.copy()
                }
                ).let{
                    swipe_in_community.isRefreshing = false
                    widget_ProgressBarInCommunity1.visibility = View.GONE
                }
            }
        )
    }

}