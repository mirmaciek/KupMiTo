package com.mirkiewicz.kupmito

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : AppCompatActivity() {

    private var newproduct: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)


        addproduct_button.setOnClickListener {
            newproduct = addprod_edittext.text.toString()
            Toast.makeText(this, "TBD: $newproduct", Toast.LENGTH_SHORT).show()
        }

    }
}