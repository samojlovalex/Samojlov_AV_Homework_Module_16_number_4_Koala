package com.example.samojlov_av_homework_module_16_number_4_koala.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samojlov_av_homework_module_16_number_4_koala.MainActivity
import com.example.samojlov_av_homework_module_16_number_4_koala.R
import com.example.samojlov_av_homework_module_16_number_4_koala.databinding.FragmentSearchBinding
import com.example.samojlov_av_homework_module_16_number_4_koala.models.PhoneBook
import com.example.samojlov_av_homework_module_16_number_4_koala.utils.PhoneBookAdapter


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchContactsEditET: EditText
    private lateinit var searchContactBT: Button
    private lateinit var contactSearchListRV: RecyclerView

    private var adapter: PhoneBookAdapter? = null
    private var contactsModelList: MutableList<PhoneBook>? = null
    private var filterContactsMobileList = mutableListOf<PhoneBook>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        searchContactsEditET = binding.searchContactsEditET
        searchContactBT = binding.searchContactBT
        contactSearchListRV = binding.contactSearchListRV
        contactSearchListRV.layoutManager = LinearLayoutManager(context)

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionContact.launch(Manifest.permission.READ_CONTACTS)
        } else {
            getContact()
        }

        searchContactBT.setOnClickListener {
            if (searchContactsEditET.text.isEmpty()) return@setOnClickListener
            filterContactS()
        }
    }

    private fun filterContactS() {
        filterContactsMobileList.clear()
        val searchResult = searchContactsEditET.text.toString().trim().lowercase()
        if (contactsModelList != null) {
            for (result in contactsModelList!!) {
                if (searchResult in result.name.lowercase()) {
                    filterContactsMobileList.add(result)
                }
            }
        }
        searchContactsEditET.text.clear()
        initAdapter()
    }

    @SuppressLint("Range")
    private fun getContact() {
        contactsModelList = ArrayList()
        val phones = requireActivity().contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val contactModel = PhoneBook(name, phoneNumber)
            contactsModelList?.add(contactModel)
        }
        phones.close()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapter() {
        adapter = PhoneBookAdapter(activity, filterContactsMobileList)
        contactSearchListRV.adapter = adapter
        contactSearchListRV.setHasFixedSize(true)
        adapter!!.setOnPhoneBookClickListener(object : PhoneBookAdapter.OnPhoneBookClickListener {
            override fun onPhoneClick(phoneBook: PhoneBook, position: Int) {
                Toast.makeText(context, phoneBook.name, Toast.LENGTH_LONG).show()
            }

        })
        adapter!!.setSmsDataListener(object : PhoneBookAdapter.SmsDataListener {
            override fun onData(data: String) {
                (activity as MainActivity).sendDataToSMS(data)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.containerFragmentMainActivityFCV, SMSFragment())
                    ?.remove(this@SearchFragment)
                    ?.commit()
                (activity as MainActivity).backMenuFragment = SMSFragment()
            }

        })
        adapter?.notifyDataSetChanged()
    }

    private val permissionContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(
                context,
                getString(R.string.permissionContact_true_Toast), Toast.LENGTH_LONG
            ).show()
            getContact()
        } else {
            Toast.makeText(context, R.string.permission_call_phone_else_toast, Toast.LENGTH_LONG)
                .show()
        }
    }

}