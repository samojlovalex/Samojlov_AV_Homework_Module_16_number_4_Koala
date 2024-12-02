package com.example.samojlov_av_homework_module_16_number_4_koala.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.samojlov_av_homework_module_16_number_4_koala.MainActivity
import com.example.samojlov_av_homework_module_16_number_4_koala.R
import com.example.samojlov_av_homework_module_16_number_4_koala.models.PhoneBook
import com.google.gson.Gson
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
class PhoneBookAdapter(
    private val activity: FragmentActivity?,
    private val list: MutableList<PhoneBook>,
) :
    RecyclerView.Adapter<PhoneBookAdapter.PhoneBookViewHolder>() {


    private var phoneBook: PhoneBook? = null

    private var onPhoneBookClickListener: OnPhoneBookClickListener? = null

    interface OnPhoneBookClickListener {
        fun onPhoneClick(phoneBook: PhoneBook, position: Int)
    }

    interface SmsDataListener {
        fun onData(data:String)
    }

    private var smsDataListener: SmsDataListener? = null

    class PhoneBookViewHolder(itemVieW: View) : RecyclerView.ViewHolder(itemVieW) {
        val name = itemVieW.findViewById<TextView>(R.id.nameTV)!!
        val phone = itemVieW.findViewById<TextView>(R.id.phoneTV)!!
        val call = itemVieW.findViewById<ImageView>(R.id.callPhoneIV)!!
        val sms = itemVieW.findViewById<ImageView>(R.id.smsPhoneIV)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneBookViewHolder {
        val itemVieW = LayoutInflater.from(parent.context)
            .inflate(R.layout.phone_book_item, parent, false)
        return PhoneBookViewHolder(itemVieW)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: PhoneBookViewHolder, position: Int) {

        phoneBook = list[position]
        holder.name.text = phoneBook?.name
        holder.phone.text = phoneBook?.phone

        holder.sms.setOnClickListener {
            val permission = arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE
            )
            if (ActivityCompat.checkSelfPermission(
                    activity?.applicationContext!!,
                    Manifest.permission.SEND_SMS
                ) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    activity.applicationContext!!,
                    Manifest.permission.READ_PHONE_STATE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                (activity as MainActivity).permissionOfSMS.launch(permission)
            } else {
                if (smsDataListener != null){
                    val typePhone = typeOf<PhoneBook>().javaType
                    val gsonPhone = Gson().toJson(phoneBook, typePhone)
                    smsDataListener!!.onData(gsonPhone)
                }
            }

        }

        holder.call.setOnClickListener {
            val permission = Manifest.permission.CALL_PHONE
            if (ActivityCompat.checkSelfPermission(
                    activity?.applicationContext!!,
                    Manifest.permission.CALL_PHONE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                (activity as MainActivity).permissionOfCall.launch(permission)
            } else {
                callTheNumber(phoneBook?.phone)
            }

        }


        holder.itemView.setOnClickListener {
            if (onPhoneBookClickListener != null) {
                onPhoneBookClickListener!!.onPhoneClick(phoneBook!!, position)
            }
        }
    }

    private fun callTheNumber(number: String?) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        activity?.startActivity(intent)
    }


    fun setOnPhoneBookClickListener(onPhoneBookClickListener: OnPhoneBookClickListener) {
        this.onPhoneBookClickListener = onPhoneBookClickListener
    }

    fun setSmsDataListener (smsDataListener: SmsDataListener){
        this.smsDataListener = smsDataListener
    }

}