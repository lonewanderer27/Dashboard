package com.jay_puzon.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get

class Signup : AppCompatActivity() {
    private var Fname: EditText? = null
    private var Mname: EditText? = null
    private var Lname: EditText? = null
    private var Address: EditText? = null
    private var Username: EditText? = null
    private var Password: EditText? = null
    private var Gender: Spinner? = null
    private var Contact: EditText? = null
    private var BtnSignup: Button? = null
    private var BtnLogin: Button? = null
    private var GenderSelection: Int = 0
    private var Conn = SQLiteDB(this)

    inner class GenderActivity: Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            GenderSelection = position
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jp_activity_signup)

        Conn.InitializeAdmin();

        Username = findViewById(R.id.username)
        Password = findViewById(R.id.password)
        Fname = findViewById(R.id.fname)
        Mname = findViewById(R.id.mname)
        Lname = findViewById(R.id.lname)
        Address = findViewById(R.id.address)
        Gender = findViewById(R.id.gender)
        Contact = findViewById(R.id.contact)
        BtnSignup = findViewById(R.id.btnSignup)
        BtnLogin = findViewById(R.id.btnLogin)

        Gender!!.onItemSelectedListener = GenderActivity();
        ArrayAdapter.createFromResource(
            this,
            R.array.genders,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            Gender!!.adapter = adapter
        }

        BtnLogin!!.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        BtnSignup!!.setOnClickListener {
            Log.i("JobAlleyMainActivity", "BtnSignup clicked!")
            // get the values from the edit text fields
            val vals = listOf(
                Username!!.text.toString() + "",
                Fname!!.text.toString() + "",
                Mname!!.text.toString() + "",
                Lname!!.text.toString() + ""
            )

            // create an array full of name value fields
            val fields = listOf(
                Username,
                Password,
                Fname,
                Mname,
                Lname,
                Address,
                Contact
            )

            // check if each field is filled up
            fields.forEachIndexed { i, name ->
                if (name!!.text.toString().equals("")) {
                    fields[i]!!.error = "Please fill up this field!"
                    fields[i]!!.requestFocus();
                    return@setOnClickListener
                }
            }

            // check if there's existing record, if there is, don't add
            if (Conn.RecordExists(vals[0], vals[1], vals[2], vals[3])) {
                Log.i("AddRecord", "Record already exists")
                Toast.makeText(this, "Record already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Conn.AddRecord(
                    Username!!.text.toString(),
                    Password!!.text.toString(),
                    SQLiteDB.USER,
                    Fname!!.text.toString(),
                    Mname!!.text.toString(),
                    Lname!!.text.toString(),
                    Address!!.text.toString(),
                    Gender!!.selectedItem.toString(),
                    Contact!!.text.toString(),
                    false
                )
            ) {
                Log.i("AddRecord", "Record saved!");
                Toast.makeText(this, "Your account is awaiting for approval", Toast.LENGTH_SHORT).show()

                fields.forEach { editText ->
                    editText!!.setText("");
                }
            } else {
                Log.e("AddRecord", "Error saving record!");
                Toast.makeText(this, "Error saving record!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}