package com.hamidjonhamidov.cvforkhamidjon.ui.main.c_myskills

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.hamidjonhamidov.cvforkhamidjon.R
import com.hamidjonhamidov.cvforkhamidjon.models.offline.main.SkillModel
import com.hamidjonhamidov.cvforkhamidjon.ui.main.BaseMainFragment
import com.hamidjonhamidov.cvforkhamidjon.ui.main.viewmodel.getCurrentViewStateOrNew
import com.hamidjonhamidov.cvforkhamidjon.ui.main.viewmodel.state.MainStateEvent
import com.hamidjonhamidov.cvforkhamidjon.util.recycler.SkillsAdapter
import kotlinx.android.synthetic.main.fragment_my_skills.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi


@ExperimentalCoroutinesApi
@FlowPreview
@InternalCoroutinesApi
class MySkillsFragment(
    viewModelFactory: ViewModelProvider.Factory,
    val skillStateEvent: MainStateEvent
)
    : BaseMainFragment<SkillModel>(R.layout.fragment_my_skills, R.menu.only_refresh_menu, viewModelFactory, skillStateEvent) {

    lateinit var listAdapter: SkillsAdapter

    override fun subscribeDataObservers() {
        // observe data in about me
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.mySkillsFragmentView?.let {
                it.mySkills?.let { mySkills ->
                    updateView(mySkills)
                }
            }
        })
    }

    override fun initData() {
        skills_recycler_view.apply{
            layoutManager = LinearLayoutManager(this@MySkillsFragment.context)
            listAdapter = SkillsAdapter()
            adapter = listAdapter
        }

        if (viewModel.getCurrentViewStateOrNew().mySkillsFragmentView.mySkills == null &&
            !viewModel.jobManger.isJobActive(skillStateEvent.responsibleJob)
        ) {
            viewModel.setStateEvent(skillStateEvent)
        } else {
            updateView(viewModel.getCurrentViewStateOrNew().mySkillsFragmentView.mySkills!!)
        }

    }

    override fun updateView(myModel: SkillModel?) {}
    override fun updateView(modelList: List<SkillModel>) {
        listAdapter.submitList(modelList)
    }


}
