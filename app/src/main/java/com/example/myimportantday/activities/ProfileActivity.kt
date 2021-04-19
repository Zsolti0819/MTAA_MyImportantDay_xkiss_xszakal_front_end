package com.example.myimportantday.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myimportantday.R
import com.example.myimportantday.api.APIclient
import com.example.myimportantday.models.EventsResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_calendar,
            R.id.navigation_home,
            R.id.navigation_new_event,
            R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.upper_menu_settings, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, SettingsActivity::class.java)
        when (item.itemId) {
            R.id.navigation_settings -> startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun fetchPosts() {
//
//        variable = APIclient
//
//        apiClient.getApiService(this).fetchPosts()
//            .enqueue(object : Callback<EventsResponse> {
//                override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
//                    // Error fetching posts
//                }
//
//                override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
//                    // Handle function to display posts
//                }
//            })
//    }

}

