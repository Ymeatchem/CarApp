package com.example.a3048_carapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        tv_login.setOnClickListener {

            startActivity(Intent(this, Login::class.java))
        }

        btn_register.setOnClickListener{
            when {
                TextUtils.isEmpty(et_register_email.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@Register,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                    TextUtils.isEmpty(et_register_password.text.toString().trim { it <= ' '}) -> {
                        Toast.makeText(
                           this@Register,
                            "Please enter password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        val email: String = et_register_email.text.toString().trim { it <= ' '}
                        val password: String = et_register_password.text.toString().trim { it <= ' '}

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                OnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val firebaseUser: FirebaseUser = task.result!!.user!!

                                        Toast.makeText(
                                            this@Register,
                                            "You are registered successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val intent =
                                            Intent(this@Register, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra( "user_id", firebaseUser.uid)
                                        intent.putExtra("email_id" , email)
                                        startActivity(intent)
                                        finish()
                                    } else  {
                                        Toast.makeText(
                                            this@Register,
                                            task.exception!!.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )



                    }


            }            }








    }
}