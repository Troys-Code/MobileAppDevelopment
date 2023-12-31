/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Ref : CSE118 example code from Dr. Harrison
// Understanding recycler view and Creating dynamic lists with RecyclerView https://developer.android.com/develop/ui/views/layout/recyclerview
// kotlin stdlib https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/sorted-by-descending.html
// How to set Onclick Listeners https://stackoverflow.com/questions/44301301/android-how-to-achieve-setonclicklistener-in-kotlin
// How to add a back button to the action bar at the top : https://devofandroid.blogspot.com/2018/03/add-back-button-to-action-bar-android.html
// How to go to the previous fragment by back button in action bar : https://stackoverflow.com/questions/62622943/how-to-go-to-the-previous-fragment-by-back-button-in-action-bar-kotlin
// Google Search for syntax error messages

package edu.ucsc.cse118.assignment2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // If the activity is created for the first time (not due to configuration changes),
    // show the LoginFragment
    if (savedInstanceState == null) {
      showLoginFragment()
    }
  }

  // Function to show the LoginFragment
  fun showLoginFragment() {
    supportFragmentManager.commit {
      replace(R.id.fragmentContainerView, LoginFragment::class.java, null)
      setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Optional animation
    }
    supportActionBar?.title = "CSE118 Assignment 2"
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
  }

  // Function to navigate to the WorkspacesFragment
  fun navigateToWorkspaces() {
    supportFragmentManager.commit {
      replace(R.id.fragmentContainerView, WorkspacesFragment::class.java, null)
      addToBackStack(null)
      setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Optional animation
    }
    supportActionBar?.title = "Workspaces"
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  // Called when the user presses the "Up" button in the action bar
  override fun onSupportNavigateUp(): Boolean {
    supportFragmentManager.popBackStack()
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
    return true
  }
}
