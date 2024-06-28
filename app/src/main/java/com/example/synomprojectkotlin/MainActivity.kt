package com.example.synomprojectkotlin

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.synomprojectkotlin.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var buttonDefinition: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            bottomNavigation = bottomNav
        }

        buttonDefinition = findViewById(R.id.btnToDefinition)

        bottomNavigation.selectedItemId = R.id.play
        setFragment(SynomFragment(), "SYNOM_FRAGMENT")

        buttonDefinition.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_layout)

            if (currentFragment is DefinitionFragment) {
                bottomNavigation.selectedItemId = R.id.play
                setFragment(SynomFragment(), "SYNOM_FRAGMENT")
                buttonDefinition.setBackgroundResource(R.drawable.button_definition)
            } else {
                bottomNavigation.selectedItemId = R.id.play
                setFragment(DefinitionFragment(), "DEFINITION_FRAGMENT")
                buttonDefinition.setBackgroundResource(R.drawable.button_to_synom)
            }
        }

        bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            if (item.itemId == bottomNavigation.selectedItemId) {
                return@OnNavigationItemSelectedListener true
            }
            when (item.itemId) {
                R.id.search -> {
                    setFragment(SearchFragment(), "SEARCH_FRAGMENT")
                    return@OnNavigationItemSelectedListener true
                }

                R.id.play -> {
                    setFragment(SynomFragment(), "SYNOM_FRAGMENT")
                    return@OnNavigationItemSelectedListener true
                }

                R.id.profile -> {
                    setFragment(ProfileFragment(), "PROFILE_FRAGMENT")
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
    fun setFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, fragment, tag)
            .commit()
    }
}