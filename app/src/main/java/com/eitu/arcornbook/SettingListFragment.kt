package com.eitu.arcornbook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eitu.arcornbook.databinding.FragmentSettingListBinding
import com.google.firebase.messaging.FirebaseMessaging

class SettingListFragment : Fragment() {

    lateinit var binding:FragmentSettingListBinding
    lateinit var mainActivity: MainActivity
    lateinit var firebaseMessaging:FirebaseMessaging

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingListBinding.inflate(inflater, container, false)
        firebaseMessaging = FirebaseMessaging.getInstance()

        val sp = mainActivity.getSharedPreferences("token", Context.MODE_PRIVATE)
        val allow = sp.getBoolean("notificationAllowed", true)
        binding.set3sw.isChecked = allow

        setOnClick()

        return binding.root
    }

    private fun setOnClick(){
        binding.set1.root.setOnClickListener {
            mainActivity.openSetting()
        }
        binding.set2.root.setOnClickListener {
            mainActivity.showDialogDelete()
        }
        binding.set3sw.setOnCheckedChangeListener { buttonView, isChecked ->
            val sp = mainActivity.getSharedPreferences("token", Context.MODE_PRIVATE)
            val editor = sp.edit()

            if(isChecked){
                editor.putBoolean("notificationAllowed", true)
                editor.apply()
                firebaseMessaging.subscribeToTopic("notificationAllowed")
            }
            else{
                editor.putBoolean("notificationAllowed", false)
                editor.apply()
                firebaseMessaging.unsubscribeFromTopic("notificationAllowed")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }
}