package com.hamidjonhamidov.cvforkhamidjon.ui.main.viewmodel

import com.hamidjonhamidov.cvforkhamidjon.models.offline.main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

private val TAG = "AppDebug"

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun MainViewModel.setAboutMe(aboutMe: AboutMeModel){
    val update = getCurrentViewStateOrNew()
    update.homeFragmentView.aboutMe = aboutMe
    update.aboutMeFragmentView.aboutMe = aboutMe
    setViewState(update)
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun MainViewModel.setMySkills(skills: List<SkillModel>){
    val update = getCurrentViewStateOrNew()
    update.mySkillsFragmentView.mySkills = skills
    setViewState(update)
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun MainViewModel.setAchievments(achievments: List<AchievementModel>){
    val update = getCurrentViewStateOrNew()
    update.achievementsFragmentView.achievements = achievments
    setViewState(update)
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun MainViewModel.setProjects(projects: List<ProjectModel>){
    val update = getCurrentViewStateOrNew()
    update.projectsFragmentView.projects = projects
    setViewState(update)
}

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
fun MainViewModel.setPosts(posts: List<PostModel>){
    val update = getCurrentViewStateOrNew()
    update.postsFragmentView.posts = posts
    setViewState(update)
}


















