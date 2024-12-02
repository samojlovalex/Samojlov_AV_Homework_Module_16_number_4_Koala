package com.example.samojlov_av_homework_module_16_number_4_koala.fragments

import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.samojlov_av_homework_module_16_number_4_koala.MainActivity
import com.example.samojlov_av_homework_module_16_number_4_koala.R
import com.example.samojlov_av_homework_module_16_number_4_koala.databinding.FragmentSMSBinding
import com.example.samojlov_av_homework_module_16_number_4_koala.models.PhoneBook
import com.google.gson.Gson
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class)
class SMSFragment : Fragment() {

    private lateinit var binding: FragmentSMSBinding
    private lateinit var nameSMSTV: TextView
    private lateinit var phoneSMSTV: TextView
    private lateinit var smsTextET: EditText
    private lateinit var smsSendBT: Button

    private var phoneBook: PhoneBook? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSMSBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initData() {
        initDataPhoneBook()

        if (phoneBook != null) {
            nameSMSTV.text = phoneBook!!.name
            phoneSMSTV.text = phoneBook!!.phone
        }
    }

    private fun initDataPhoneBook() {
        val gson = (activity as MainActivity).getDataToSMS()
        val type = typeOf<PhoneBook?>().javaType
//        val gson = arguments?.getString("smsOut")
        phoneBook = Gson().fromJson(gson, type)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initData()
    }

    private fun init() {

        nameSMSTV = binding.nameSMSTV
        phoneSMSTV = binding.phoneSMSTV
        smsTextET = binding.smsTextET
        smsSendBT = binding.smsSendBT

        nameSMSTV.setSelected(true)

        smsSendBT.setOnClickListener {
            if (smsTextET.text.isEmpty()) return@setOnClickListener
            sendSMS()
        }


    }

    private fun sendSMS() {
        val message = smsTextET.text.toString()
        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= 23) {
                activity?.getSystemService(SmsManager::class.java)!!
            } else {
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneBook?.phone, null, message, null, null)
            Toast.makeText(
                activity?.applicationContext,
                getString(R.string.sendSMS_Toast_true), Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                activity?.applicationContext,
                getString(R.string.sendSMS_Toast_false) + e.message.toString(), Toast.LENGTH_LONG
            ).show()
        }
        smsTextET.text.clear()
    }

}