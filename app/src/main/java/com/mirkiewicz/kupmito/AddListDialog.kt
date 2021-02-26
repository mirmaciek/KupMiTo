package com.mirkiewicz.kupmito

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment

class AddListDialog : AppCompatDialogFragment() {

    private var et_listname: EditText? = null
    private var et_listdesc: EditText? = null
    private var listener: AddListDialogListener? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.addlist_dialog, null)

        et_listname = view.findViewById(R.id.edit_listname)
        et_listdesc = view.findViewById(R.id.edit_listdesc)

        builder.setView(view)
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, i -> }
                .setPositiveButton(getString(R.string.save)) { dialogInterface, i ->

//                    val filtered = ".[]#$"
                    var name = et_listname!!.text.toString().trim()
//                    name = name.filterNot { filtered.indexOf(it) > -1 }

                    var description = et_listdesc!!.text.toString().trim()

                    if (description.length >= 100)
                        description = description.substring(0,100)
                    if (name.length >= 100)
                        name = name.substring(0,100)

                    if (name != "") {
                        listener!!.applyTexts(name, description)
                    } else {
                        val text = getString(R.string.empty_field, "name")
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }
                }

        return builder.create()
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
        try {
            listener = context as AddListDialogListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    interface AddListDialogListener {
        fun applyTexts(name: String, description: String)
    }
}

