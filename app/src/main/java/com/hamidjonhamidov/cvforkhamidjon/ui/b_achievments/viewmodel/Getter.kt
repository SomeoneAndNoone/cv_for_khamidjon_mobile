package com.hamidjonhamidov.cvforkhamidjon.ui.b_achievments.viewmodel

import com.hamidjonhamidov.cvforkhamidjon.models.offline.achievements.AchievementModel
import com.hamidjonhamidov.cvforkhamidjon.models.offline.achievements.Honor
import com.hamidjonhamidov.cvforkhamidjon.ui.b_achievments.viewmodel.state.AchievementsViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi


@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun AchievementsViewModel.getCurrentViewStateOrNew()
        : AchievementsViewState  = viewState.value ?: AchievementsViewState()

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun AchievementsViewModel.getAndroid(): List<Honor>{
    return getCurrentViewStateOrNew().achievementsFragmentView.achievements?.get(0)?.honorsList ?: listOf()
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun AchievementsViewModel.getAlgorithms(): List<Honor>{
    return getCurrentViewStateOrNew().achievementsFragmentView.achievements?.get(1)?.honorsList ?: listOf()
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun AchievementsViewModel.getOthers(): List<Honor>{
    return getCurrentViewStateOrNew().achievementsFragmentView.achievements?.get(2)?.honorsList ?: listOf()
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun AchievementsViewModel.getAndroidPos(pos: Int): Int{
    return viewState.value?.achievementsFragmentView?.androidPos ?: -1
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun AchievementsViewModel.getAlgoPos(pos: Int): Int{
    return viewState.value?.achievementsFragmentView?.algoPos ?: -1
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun AchievementsViewModel.getOthersPos(pos: Int): Int{
    return viewState.value?.achievementsFragmentView?.othersPos ?: -1
}
