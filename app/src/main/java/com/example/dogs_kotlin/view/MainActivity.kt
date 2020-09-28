package com.example.dogs_kotlin.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.dogs_kotlin.R
import com.example.dogs_kotlin.databinding.ActivityMainBinding
import com.example.dogs_kotlin.util.PERMISSION_SEND_SMS
import com.example.dogs_kotlin.view.fragments.DetailFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun checkSmsPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)){
                AlertDialog.Builder(this)
                    .setTitle("Send SMS Permission")
                    .setMessage("This app requires access to send an SMS")
                    .setPositiveButton("Ask me"){ _, _ ->
                        requestSmsPermission()
                    }
                    .setNegativeButton("No"){ _, _ ->
                        notifyDetailFragment(false)
                    }
                    .show()
            }else{
                requestSmsPermission()
            }
        }else{
            notifyDetailFragment(true)
        }
    }

    private fun requestSmsPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_SEND_SMS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyDetailFragment(true)
                }else{
                    notifyDetailFragment(false)
                }
            }
        }
    }

    private fun notifyDetailFragment(permissionGranted: Boolean){
        val activeFragment = fragment.childFragmentManager.primaryNavigationFragment
        if(activeFragment is DetailFragment){
            activeFragment.onPermissionResult(permissionGranted)
        }
    }
}