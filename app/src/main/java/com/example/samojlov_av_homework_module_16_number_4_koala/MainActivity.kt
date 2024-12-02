package com.example.samojlov_av_homework_module_16_number_4_koala

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.samojlov_av_homework_module_16_number_4_koala.databinding.ActivityMainBinding
import com.example.samojlov_av_homework_module_16_number_4_koala.fragments.PhoneBookFragment
import com.example.samojlov_av_homework_module_16_number_4_koala.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var dataSMS: String? = null
    var backMenuFragment: Fragment = SearchFragment()
    var menuSearch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init() {

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        title = getString(R.string.toolbar_title)
        toolbar.subtitle = getString(R.string.toolbar_subtitle)

        enterFragment()
    }

    private fun enterFragment() {

        supportFragmentManager.beginTransaction()
            .add(R.id.containerFragmentMainActivityFCV, PhoneBookFragment())
            .commit()

    }

    val permissionOfSMS = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        var allAreGranted = true
        for (isGranted in result.values) {
            allAreGranted = allAreGranted && isGranted
        }
        if (allAreGranted) {

        } else {
            Toast.makeText(
                this,
                this.getString(R.string.permission_call_phone_else_toast),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val permissionOfCall = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        } else {
            Toast.makeText(
                this,
                this.getString(R.string.permission_call_phone_else_toast),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun sendDataToSMS(data: String) {
        dataSMS = data
    }

    fun getDataToSMS(): String? {
        return dataSMS
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        menu?.findItem(R.id.backMenu)?.setVisible(!menuSearch)
        menu?.findItem(R.id.searchMenu)?.setVisible(menuSearch)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("SetTextI18n", "ShowToast")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exitMenu -> {
                Toast.makeText(
                    this,
                    getString(R.string.toast_exit),
                    Toast.LENGTH_LONG
                ).show()
                finishAffinity()
            }

            R.id.backMenu -> {
                menuSearch = true
                invalidateOptionsMenu()
                supportFragmentManager.beginTransaction()
                    .add(R.id.containerFragmentMainActivityFCV, PhoneBookFragment())
                    .remove(backMenuFragment)
                    .commit()
                backMenuFragment = SearchFragment()
            }

            R.id.searchMenu -> {
                menuSearch = false
                invalidateOptionsMenu()
                supportFragmentManager.beginTransaction()
                    .add(R.id.containerFragmentMainActivityFCV, SearchFragment())
                    .remove(PhoneBookFragment())
                    .commit()
                backMenuFragment = SearchFragment()

            }
        }
        return super.onOptionsItemSelected(item)
    }

}