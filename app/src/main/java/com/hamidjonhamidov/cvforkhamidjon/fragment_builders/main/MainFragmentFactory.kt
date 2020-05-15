package com.hamidjonhamidov.cvforkhamidjon.fragment_builders.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.hamidjonhamidov.cvforkhamidjon.di.main_subcomponent.MainActivityScope
import com.hamidjonhamidov.cvforkhamidjon.ui.main.a_home.HomeFragment
import com.hamidjonhamidov.cvforkhamidjon.util.glide.GlideManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@MainActivityScope
class MainFragmentFactory
@Inject
constructor(
    private val viewModelProviderFactory: ViewModelProvider.Factory,
    private val glideManager: GlideManager
) : FragmentFactory(){

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when(className){

            HomeFragment::class.java.name -> {
                HomeFragment(viewModelProviderFactory, glideManager)
            }

            else -> {
                super.instantiate(classLoader, className)
            }
        }
}