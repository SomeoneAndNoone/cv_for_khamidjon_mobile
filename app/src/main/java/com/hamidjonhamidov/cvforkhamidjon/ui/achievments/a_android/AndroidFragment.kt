package com.hamidjonhamidov.cvforkhamidjon.ui.achievments.a_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.hamidjonhamidov.cvforkhamidjon.R
import com.hamidjonhamidov.cvforkhamidjon.models.offline.achievements.AchievementModel
import com.hamidjonhamidov.cvforkhamidjon.ui.achievments.AchievmentsActivity
import com.hamidjonhamidov.cvforkhamidjon.ui.achievments.viewmodel.AchievementsViewModel
import com.hamidjonhamidov.cvforkhamidjon.util.glide.GlideManager
import com.hamidjonhamidov.cvforkhamidjon.util.recycler.AchievementAdapter
import com.hamidjonhamidov.cvforkhamidjon.util.recycler.AchievementListener
import kotlinx.android.synthetic.main.fragment_android_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class AndroidFragment(
    viewModelFactory: ViewModelProvider.Factory,
    val glideManager: GlideManager
)
    : Fragment(R.layout.fragment_android_fragment), AchievementListener {

    val viewModel: AchievementsViewModel by activityViewModels{
        viewModelFactory
    }

    lateinit var listAdapter: AchievementAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        subscribeObservers()
    }

    fun initData(){
        android_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@AndroidFragment.context)
            listAdapter = AchievementAdapter(this@AndroidFragment, glideManager)
            adapter = listAdapter
        }
    }

    fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState->
            viewState?.achievementsFragmentView?.let {
                it.achievements?.let {
                    updateView(it[0])
                }
            }
        })
    }

    private fun updateView(achievementModel: AchievementModel) {
        listAdapter.submitList(achievementModel.honorsList)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AchievmentsActivity).shouldRefresh()
    }

    override fun onAchievmentClick(position: Int) {

    }

}
