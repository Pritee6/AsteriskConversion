package com.example.asteriskconversion

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    var tiet_email: TextInputEditText? = null
    var tiet_password:TextInputEditText? = null
    var til_password: TextInputLayout? = null
    var btn_login: Button? = null
    var imageView: ImageView? = null
    var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tiet_password = findViewById(R.id.tiet_password)
        til_password = findViewById(R.id.til_password)
        tiet_password?.transformationMethod = AsteriskPassword()

        with(til_password) { this?.addEndIconClickListener() }

    }
}

private fun TextInputLayout.addEndIconClickListener() {
    var isPasswordVisible = false
    this.setEndIconOnClickListener {
        if (isPasswordVisible) {
            isPasswordVisible = false
            this.editText!!.transformationMethod = AsteriskPasswordTransformationMethod()
        } else {
            isPasswordVisible = true
            this.editText!!.transformationMethod = HideReturnsTransformationMethod()
        }
    }
}
