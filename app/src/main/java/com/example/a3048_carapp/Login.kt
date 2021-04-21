package com.example.a3048_carapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        tv_register.setOnClickListener {

            startActivity(Intent(this@Login, Register::class.java))

                     }
            btnLogin.setOnClickListener{
                when {
                    TextUtils.isEmpty(et_login_email.text.toString().trim { it <= ' '}) -> {
                        Toast.makeText(
                                this@Login,
                                "Please enter email.",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                    TextUtils.isEmpty(et_login_password.text.toString().trim { it <= ' '}) -> {
                        Toast.makeText(
                                this@Login,
                                "Please enter password.",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                    else ->
                    {
                        val email: String = et_login_email.text.toString().trim { it <= ' '}
                        val password: String = et_login_password.text.toString().trim { it <= ' '}

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->

                                            if (task.isSuccessful) {


                                                Toast.makeText(
                                                        this@Login,
                                                        "You are Logged in successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show()

                                                val intent =
                                                        Intent(this@Login, MainActivity::class.java)
                                                intent.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                intent.putExtra(
                                                        "user_id",
                                                        FirebaseAuth.getInstance().currentUser!!.uid
                                                )
                                                intent.putExtra("email_id" , email)
                                                startActivity(intent)
                                                finish()
                                            } else  {

                                                Toast.makeText(
                                                        this@Login,
                                                        task.exception!!.message.toString(),
                                                        Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                    }
                }
            }
        }
    }
