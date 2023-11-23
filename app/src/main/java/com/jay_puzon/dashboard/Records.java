package com.jay_puzon.dashboard;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Records extends ListActivity {
    User CurrentUser;
    SQLiteDB Conn;
    ArrayList<String> ItemList;

    void refreshData() {
        Conn = new SQLiteDB(this);
        ItemList = Conn.GetRecords();

        if (ItemList.size() > 0) {
            setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ItemList));

            // fetch the current profile from the intent
            Intent intent = getIntent();
            CurrentUser = (User) intent.getSerializableExtra(SQLiteDB.CURRENT_PROFILE);
        } else {
            Toast.makeText(this, "No Records Found!!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Get the data of the selected record
        User user = Conn.GetRecord(Conn.ItemsId.get(position));

        // Create an intent to call the next activity
//        Intent CallEdit = new Intent(".AdminEditRecord");
        // which depends on the role of the user
        Intent CallEdit;
        CallEdit = new Intent(".UserEditRecord");

        // Pass the data to the next activity
        CallEdit.putExtra(SQLiteDB.PROFILE_ID, user.id);
        CallEdit.putExtra(SQLiteDB.USERNAME, user.username);
        CallEdit.putExtra(SQLiteDB.PASSWORD, user.password);
        CallEdit.putExtra(SQLiteDB.ROLE, user.role);
        CallEdit.putExtra(SQLiteDB.FNAME, user.fname);
        CallEdit.putExtra(SQLiteDB.MNAME, user.mname);
        CallEdit.putExtra(SQLiteDB.LNAME, user.lname);
        CallEdit.putExtra(SQLiteDB.GENDER, user.gender);
        CallEdit.putExtra(SQLiteDB.ADDRESS, user.address);
        CallEdit.putExtra(SQLiteDB.CONTACT, user.contact);
        CallEdit.putExtra(SQLiteDB.APPROVED, user.approved);
        CallEdit.putExtra(SQLiteDB.PROFILE, user);

        CallEdit.putExtra(SQLiteDB.CURRENT_PROFILE, this.CurrentUser);

        // Call the next activity
        startActivity(CallEdit);
    }
}