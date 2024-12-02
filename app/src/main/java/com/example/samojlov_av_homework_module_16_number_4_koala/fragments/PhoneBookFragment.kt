package com.example.samojlov_av_homework_module_16_number_4_koala.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.util.Log
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
import com.example.samojlov_av_homework_module_16_number_4_koala.databinding.FragmentPhoneBookBinding
import com.example.samojlov_av_homework_module_16_number_4_koala.models.PhoneBook
import com.example.samojlov_av_homework_module_16_number_4_koala.utils.PhoneBookAdapter

class PhoneBookFragment : Fragment() {

    private lateinit var binding: FragmentPhoneBookBinding
    private lateinit var contactListRV: RecyclerView
    private lateinit var phoneContactsEditET: EditText
    private lateinit var nameContactsEditET: EditText
    private lateinit var addContactBT: Button

    private var adapter: PhoneBookAdapter? = null
    private var contactsModelList: MutableList<PhoneBook>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPhoneBookBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        nameContactsEditET = binding.nameContactsEditET
        phoneContactsEditET = binding.phoneContactsEditET
        addContactBT = binding.addContactBT
        contactListRV = binding.contactListRV
        contactListRV.layoutManager = LinearLayoutManager(context)

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
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        addContactBT.setOnClickListener {
            if (nameContactsEditET.text.isEmpty() || phoneContactsEditET.text.isEmpty()) return@setOnClickListener

            permissionWriteContact()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun permissionWriteContact() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_CONTACTS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionEditContact.launch(Manifest.permission.WRITE_CONTACTS)
        } else {
            addContacts()
            adapter?.notifyDataSetChanged()

        }
    }

    private fun addContacts() {
        val name = nameContactsEditET.text.toString().trim()
        val phone = phoneContactsEditET.text.toString().trim()

        val listCPO = ArrayList<ContentProviderOperation>()

        listCPO.add(
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        listCPO.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, name)
                .build()
        )

        listCPO.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, phone)
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                .build()
        )

        Toast.makeText(context, getString(R.string.addContacts_Toast, name), Toast.LENGTH_LONG)
            .show()
        try {
            requireActivity().contentResolver.applyBatch(ContactsContract.AUTHORITY, listCPO)
        } catch (e: Exception) {
            Log.e(getString(R.string.addContacts_Exeption), e.message!!)
        }

        nameContactsEditET.text.clear()
        phoneContactsEditET.text.clear()
    }

    @SuppressLint("Range")
    private fun getContact() {
        contactsModelList = ArrayList()
        val phones = requireActivity().contentResolver?.query(
            Phone.CONTENT_URI,
            null,
            null,
            null,
            Phone.DISPLAY_NAME + " ASC"
        )
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(Phone.NUMBER))
            val contactModel = PhoneBook(name, phoneNumber)
            contactsModelList?.add(contactModel)
        }
        phones.close()
        if (contactsModelList != null) {
            adapter = PhoneBookAdapter(activity, contactsModelList!!)
        }
        contactListRV.adapter = adapter
        contactListRV.setHasFixedSize(true)
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
                    ?.remove(this@PhoneBookFragment)
                    ?.commit()
                (activity as MainActivity).backMenuFragment = SMSFragment()
                (activity as MainActivity).menuSearch = false
                (activity as MainActivity).invalidateOptionsMenu()
            }

        })
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

    private val permissionEditContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(
                context,
                getString(R.string.permissionEditContact_Toast_true), Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(context, R.string.permission_call_phone_else_toast, Toast.LENGTH_LONG)
                .show()
        }
    }

}