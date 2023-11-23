package com.jay_puzon.dashboard

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserEditRecord : AppCompatActivity() {
    private var CurrentUser: User? = null

    private var Fname: EditText? = null
    private var Mname: EditText? = null
    private var Lname: EditText? = null
    private var Address: EditText? = null
    private var Username: EditText? = null
    private var Gender: Spinner? = null
    private var Role: EditText? = null
    private var Approved: Spinner? = null
    private var Password: EditText? = null
    private var Contact: EditText? = null
    private var BtnUpdate: Button? = null
    private var GenderSelection: Int = 0
    private var ApprovedSelection: Int = 0

    private val Conn: SQLiteDB = SQLiteDB(this);

    inner class GenderActivity: Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            GenderSelection = position
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }

    inner class ApprovedActivity: Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            ApprovedSelection = position
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jp_activity_user_edit_record)

        Username = findViewById(R.id.username)
        Password = findViewById(R.id.password)
        Fname = findViewById(R.id.fname)
        Mname = findViewById(R.id.mname)
        Lname = findViewById(R.id.lname)
        Address = findViewById(R.id.address)
        Gender = findViewById(R.id.gender)
        Password = findViewById(R.id.password)
        Role = findViewById(R.id.role)
        Approved = findViewById(R.id.approved)
        Contact = findViewById(R.id.contact)
        BtnUpdate = findViewById(R.id.btnUpdate)

        Gender!!.onItemSelectedListener = GenderActivity();
        ArrayAdapter.createFromResource(
            this,
            R.array.genders,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            Gender!!.adapter = adapter
        }

        Approved!!.onItemSelectedListener = ApprovedActivity();
        ArrayAdapter.createFromResource(
            this,
            R.array.approved,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            Approved!!.adapter = adapter
        }

        // retrieve the extra values
        val extras = intent.extras;
        val id = extras!!.getInt(SQLiteDB.PROFILE_ID);
        val username = extras.getString(SQLiteDB.USERNAME);
        val password = extras.getString(SQLiteDB.PASSWORD);
        val fname = extras.getString(SQLiteDB.FNAME);
        val mname = extras.getString(SQLiteDB.MNAME);
        val lname = extras.getString(SQLiteDB.LNAME);
        val role = extras.getString(SQLiteDB.ROLE);
        val gender = extras.getString(SQLiteDB.GENDER);
        val approved = extras.getBoolean(SQLiteDB.APPROVED);
        val contact = extras.getString(SQLiteDB.CONTACT);
        val address = extras.getString(SQLiteDB.ADDRESS);
        CurrentUser = extras.getSerializable(SQLiteDB.CURRENT_PROFILE) as User;

        // Log the information
        Log.d("UserEditRecord", "ID: $id")
        Log.d("UserEditRecord", "Username: $username")
        Log.d("UserEditRecord", "Password: $password")
        Log.d("UserEditRecord", "First Name: $fname")
        Log.d("UserEditRecord", "Middle Name: $mname")
        Log.d("UserEditRecord", "Last Name: $lname")
        Log.d("UserEditRecord", "Role: $role")
        Log.d("UserEditRecord", "Gender: $gender")
        Log.d("UserEditRecord", "Approved: $approved")
        Log.d("UserEditRecord", "Contact: $contact")
        Log.d("UserEditRecord", "Address: $address")
        Log.d("UserEditRecord", "Profile: ${CurrentUser.toString()}")

        // if the user is not an admin, hide the approved linear layout
        if (!CurrentUser!!.role.equals(SQLiteDB.ADMIN)) {
            findViewById<LinearLayout>(R.id.llApproved).visibility = View.GONE;
        } else {
            // the current user is an admin
            if (CurrentUser!!.username.equals(username)) {
                // admin is viewing their own profile, they can edit
                // but disable the llApproved layout
                findViewById<LinearLayout>(R.id.llApproved).visibility = View.GONE;
            }

            if (!CurrentUser!!.username.equals(username)) {
                // admin is viewing another user's profile, disable all the editable fields
                Username!!.isEnabled = false
                Password!!.isEnabled = false
                Fname!!.isEnabled = false
                Mname!!.isEnabled = false
                Lname!!.isEnabled = false
                Address!!.isEnabled = false
                Gender!!.isEnabled = false
                Contact!!.isEnabled = false
            }
        }

        // set the values
        Fname!!.setText(fname);
        Mname!!.setText(mname);
        Lname!!.setText(lname);
        Contact!!.setText(contact)
        Address!!.setText(address);
        Username!!.setText(username);
        Password!!.setText(password);
        Role!!.setText(role)

        // update the value of approved spinner
        val approvedArray = resources.getStringArray(R.array.approved)
        val approvedIndex = if (approved) 1 else 0
        Approved!!.setSelection(approvedIndex)

        // update the value of the gender spinner
        val gendersArray = resources.getStringArray(R.array.genders)
        val genderIndex = gendersArray.indexOf(gender)
        Gender!!.setSelection(genderIndex)

        BtnUpdate!!.setOnClickListener {
            if (CurrentUser!!.role.equals(SQLiteDB.ADMIN)) {
                // modify the approve user based on the currently selected approved option
                if (!CurrentUser!!.username.equals(username)) {
                    val approved = Approved!!.selectedItem.toString() == "Yes"

                    if (Conn.ToggleApproveUser(id, approved)) {
                        val message = if (approved) "$username has been approved" else "$username has been revoked access"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // create an array full of updated fields
            val fields = listOf(
                Fname, Mname, Lname, Address, Contact, Password
            )

            // create an array full of name value fields
            val names = listOf(
                Fname!!.text.toString() + "",
                Mname!!.text.toString() + "",
                Lname!!.text.toString() + ""
            )

            // check if each fields have values
            fields.forEachIndexed { i, name ->
                if (name!!.text.equals("")) {
                    fields[i]!!.error = "Please fill up this field!"
                    fields[i]!!.requestFocus();
                    return@setOnClickListener
                }
            }

            // check if the new values conflict with existing record (except for the current record)
            if (Conn.RecordExists(names[0], names[1], names[2], id)) {
                Log.i("BtnUpdate", "Conflicts with existing record!");
                Toast.makeText(this, "Conflicts with another record", Toast.LENGTH_LONG).show()
                return@setOnClickListener;
            }

            // create a new object that contains the updated information
            val updatedUser = User(
                id,
                username,
                Password!!.text.toString(),
                role,
                Fname!!.text.toString(),
                Mname!!.text.toString(),
                Lname!!.text.toString(),
                Address!!.text.toString(),
                Gender!!.selectedItem.toString(),
                Contact!!.text.toString(),
                approved,
            )

            // perform the update
            val success = Conn.UpdateRecord(
                id,
                Password!!.text.toString(),
                Fname!!.text.toString(),
                Mname!!.text.toString(),
                Lname!!.text.toString(),
                Address!!.text.toString(),
                Gender!!.selectedItem.toString(),
                Contact!!.text.toString(),
            )
            if (success) {
                Log.i("BtnUpdate", "Record updated!");
                Toast.makeText(this, "Your record has been updated!", Toast.LENGTH_SHORT).show()
            } else {
                Log.i("BtnUpdate", "Error saving changes");
                Toast.makeText(this, "Error saving updated record", Toast.LENGTH_SHORT).show()
            }
        }
    }
}