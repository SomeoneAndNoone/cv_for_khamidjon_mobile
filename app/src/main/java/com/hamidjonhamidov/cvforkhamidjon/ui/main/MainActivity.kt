package com.hamidjonhamidov.cvforkhamidjon.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.hamidjonhamidov.cvforkhamidjon.MyApplication
import com.hamidjonhamidov.cvforkhamidjon.R
import com.hamidjonhamidov.cvforkhamidjon.di.main_subcomponent.MainComponent
import com.hamidjonhamidov.cvforkhamidjon.ui.achievments.AchievmentsActivity
import com.hamidjonhamidov.cvforkhamidjon.ui.main.viewmodel.MainViewModel
import com.hamidjonhamidov.cvforkhamidjon.ui.source_code.SourceCodeActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var mainComponent: MainComponent

    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment)
    }


    val viewModel: MainViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent = (application as MyApplication).appComponent
            .mainComponent()
            .create()
        mainComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set toolbar
        setSupportActionBar(main_toolbar)

        val appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.homeFragment,
                    R.id.aboutMeFragment,
                    R.id.mySkillsFragment,
                    R.id.aboutAppFragment,
                    R.id.notificationsFragment,
                    R.id.projectsFragment,
                    R.id.postsFragment
                ), drawerLayout = drawer_layout
            )

        findViewById<Toolbar>(R.id.main_toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        nav_view
            .setupWithNavController(navController)

        setListenerForNavView()
    }

    private fun setListenerForNavView() {
        nav_view.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.mi_achievments -> {
                    val mIntent = Intent(this, AchievmentsActivity::class.java)
                    startActivity(mIntent)
                    true
                }

                R.id.mi_source_code -> {
                    val mIntent = Intent(this, SourceCodeActivity::class.java)
                    startActivity(mIntent)
                    true
                }

                else -> {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    it.onNavDestinationSelected(navController)
                }
            }
        }
    }

    private val TAG = "AppDebug"

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}
// empty commit

















