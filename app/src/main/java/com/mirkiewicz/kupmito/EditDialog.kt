package com.mirkiewicz.kupmito

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.mainlist_item.*

class EditDialog(val title: String?, val isTitle: Boolean?) : AppCompatDialogFragment() {

    private var listener: EditDialogListener? = null
    private var editText: EditText? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.oneline_dialog, null)


        editText = view.findViewById(R.id.edittext)
        editText?.setText(title,TextView.BufferType.EDITABLE)

        builder.setTitle(R.string.edit)
        builder.setView(view)
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(getString(R.string.save)) { _, _ ->

                    var text = editText!!.text.toString().trim()

                    if (text.length >= 100)
                        text = text.substring(0,100)

                    if (text == ""){
                        if (isTitle!!){
                            val toastText = getString(R.string.empty_field, "name")
                            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                        }else
                            listener!!.editText(text, isTitle)
                    }else
                        listener!!.editText(text, isTitle!!)
                }

        return builder.create()

    }
    override fun onAttach(context: Context) {

        super.onAttach(context)
        try {
            listener = context as EditDialogListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
    interface EditDialogListener {
        fun editText(text: String, isTitle: Boolean)

    }

}