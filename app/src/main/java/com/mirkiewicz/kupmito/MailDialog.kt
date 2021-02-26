package com.mirkiewicz.kupmito

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment


class MailDialog(val itemID: String) : AppCompatDialogFragment() {

    private var editText: EditText? = null
    private var listener: ShareDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = this.requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.oneline_dialog, null)

        editText = view.findViewById(R.id.edittext)
        editText?.hint = getString(R.string.email_address)

        builder.setTitle(R.string.share_inputmail)
        builder.setView(view)
                .setNegativeButton(getString(R.string.cancel)) { _, i -> }
                .setPositiveButton(getString(R.string.ok)) { _, i ->


                    var text = editText!!.text.toString().trim()

                    val mailregex = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"


                    if (text.matches(mailregex.toRegex()))
                        listener!!.sendMail(itemID, text)
                    else
                        Toast.makeText(context, getString(R.string.incorrect_mail), Toast.LENGTH_LONG).show()

                }

        return builder.create()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ShareDialogListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    interface ShareDialogListener {
        fun sendMail(itemID: String, mail: String)

    }

}
