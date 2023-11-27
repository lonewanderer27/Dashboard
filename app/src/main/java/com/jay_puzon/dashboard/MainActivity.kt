package com.jay_puzon.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var Conn: SQLiteDB = SQLiteDB(this);
    var Login: Button? = null
    var Signup: Button? = null
    var Username: EditText? = null
    var Password: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jp_activity_main)

        Conn.InitializeAdmin();

        Login = findViewById(R.id.btnLogin);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        Signup = findViewById(R.id.btnSignup)

        Signup!!.setOnClickListener {
            val CallSignup = Intent(".Signup");
            startActivity(CallSignup);
        }

        Login!!.setOnClickListener {
            try {
                val username = Username!!.text.toString();
                val password = Password!!.text.toString();
                var user: User? = null;

                // create a user object
                try {
                    user = Conn.GetRecord(username, password)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error getting user record", e)
                }

                if (Conn.RecordExists(username, password)) {
                    if (user!!.role == SQLiteDB.ADMIN) {
                        val intent = Intent(this, Records::class.java);
                        intent.putExtra(SQLiteDB.CURRENT_PROFILE, user);
                        startActivity(intent);
                    } else {
                        // check first if the user has been approved
                        if (!user.approved) {
                            Toast.makeText(
                                this,
                                "Your account has not been approved yet by the admin!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        // Pass the data to the next activity
                        //              0           1           2       3     4     5       6       7
                        // columns = {PROFILE_ID, USERNAME, PASSWORD, ROLE, FNAME, MNAME, LNAME, ADDRESS };

                        // Pass the data to the next activity
                        val CallEdit = Intent(this, UserEditRecord::class.java)
                        CallEdit.putExtra(SQLiteDB.PROFILE_ID, user.id)
                        CallEdit.putExtra(SQLiteDB.USERNAME, user.username)
                        CallEdit.putExtra(SQLiteDB.PASSWORD, user.password)
                        CallEdit.putExtra(SQLiteDB.ROLE, user.role)
                        CallEdit.putExtra(SQLiteDB.FNAME, user.fname)
                        CallEdit.putExtra(SQLiteDB.MNAME, user.mname)
                        CallEdit.putExtra(SQLiteDB.LNAME, user.lname)
                        CallEdit.putExtra(SQLiteDB.GENDER, user.gender)
                        CallEdit.putExtra(SQLiteDB.ADDRESS, user.address)
                        CallEdit.putExtra(SQLiteDB.CONTACT, user.contact)
                        CallEdit.putExtra(SQLiteDB.APPROVED, user.approved)

                        CallEdit.putExtra(SQLiteDB.CURRENT_PROFILE, user)

                        // start the activity
                        startActivity(CallEdit);
                    }
                } else {
                    // display an error toast message
                    Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_LONG).show();
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in Login OnClickListener")
                Log.e("MainActivity", e.message.toString());

                Toast.makeText(this, "An error has occurred, please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }
}