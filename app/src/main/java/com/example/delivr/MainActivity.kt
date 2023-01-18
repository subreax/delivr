package com.example.delivr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.categorySelectorFragment, R.id.cartFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavBar = findViewById(R.id.bottom_nav_view)
        bottomNavBar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, args ->
            if (destination.id == R.id.categorySelectorFragment || destination.id == R.id.cartFragment) {
                bottomNavBar.visibility = View.VISIBLE
            } else {
                bottomNavBar.visibility = View.GONE
            }
        }

        Repository.openUserSharedPreferences(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setCartSizeBadge(number: Int) {
        if (number > 0)
            bottomNavBar.getOrCreateBadge(R.id.cartFragment).number = number
        else
            bottomNavBar.removeBadge(R.id.cartFragment)
    }
}