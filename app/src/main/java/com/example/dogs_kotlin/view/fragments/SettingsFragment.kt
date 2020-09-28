package com.example.dogs_kotlin.view.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.dogs_kotlin.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}