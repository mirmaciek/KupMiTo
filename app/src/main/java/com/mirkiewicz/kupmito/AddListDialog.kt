package com.mirkiewicz.kupmito

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment

class AddListDialog : AppCompatDialogFragment() {

    private var et_listname: EditText? = null
    private var et_listdesc: EditText? = null
    private var listener: AddListDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.addlist_dialog, null)

        builder.setView(view)
                .setTitle(getString(R.string.list_add))
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, i -> }
                .setPositiveButton(getString(R.string.save)) { dialogInterface, i ->
                    val name = et_listname!!.text.toString()
                    val description = et_listdesc!!.text.toString()
                    listener!!.applyTexts(name, description)
                }

        et_listname = view.findViewById(R.id.edit_listname)
        et_listdesc = view.findViewById(R.id.edit_listdesc)

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