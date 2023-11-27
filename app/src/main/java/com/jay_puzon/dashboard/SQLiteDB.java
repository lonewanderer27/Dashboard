package com.jay_puzon.dashboard;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SQLiteDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "records.db";
    public static final String CURRENT_PROFILE = "current_profile";
    public static final String PROFILE = "profile", PROFILE_ID = "PROFILE_ID", FNAME = "fname", MNAME = "mname", LNAME = "lname", ADDRESS = "address", ROLE = "role", USER = "user", ADMIN = "admin", APPROVED = "approved", USERNAME = "username", PASSWORD = "password", GENDER = "gender", CONTACT = "contact";
    public static final String AD_FNAME = "Zyrich Kio", AD_MNAME = "", AD_LNAME = "Salonga", AD_ADDRESS = "1234 Elm Street, Suite 567, Cityville, State 98765, Countryland", AD_CONTACT = "5551234567", AD_GENDER = "Male";
    ContentValues VALS;
    Cursor rs;
    ArrayList<String> Items;
    ArrayList<Integer> ItemsId;

    public SQLiteDB(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase conn) {
        // init table
        String CREATE_TABLE_QUERY = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s BOOLEAN, %s TEXT, %s TEXT, %s TEXT)",
                PROFILE, PROFILE_ID, FNAME, MNAME, LNAME, ROLE, USERNAME, PASSWORD, APPROVED, GENDER, ADDRESS, CONTACT
        );

        conn.execSQL(CREATE_TABLE_QUERY);
    }

    public void InitializeAdmin() {
        if (!AdminExists()) {
            Log.i("SQLiteDB", "Inserting admin record...");
            // add admin record
            InsertAdmin();
        } else {
            Log.i("SQLiteDB", "Admin record already exists.");
        }
    }

    public void InsertAdmin() {
        AddRecord(
                ADMIN,
                ADMIN,
                ADMIN,
                AD_FNAME,
                AD_MNAME,
                AD_LNAME,
                AD_ADDRESS,
                AD_GENDER,
                AD_CONTACT,
                true
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase conn, int i, int i1) {
        conn.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(conn);
    }

    public boolean NotEmpty() {
        ArrayList<String> ItemList = this.GetRecords();
        return ItemList.size() > 0;
    }

    public boolean AdminExists() {
        SQLiteDatabase conn = this.getReadableDatabase();
        String[] columns = {ROLE};
        String selection = ROLE + " = ?";
        String[] selectionArgs = {ADMIN};

        rs = conn.query(PROFILE, columns, selection, selectionArgs, null, null, null);
        boolean exists = rs.moveToFirst();
        rs.close();
        return exists;
    }

    public boolean RecordExists(String username, String password) {
        SQLiteDatabase conn = this.getReadableDatabase();
        String[] columns = {USERNAME, PASSWORD};
        String selection = USERNAME + " = ? AND " + PASSWORD + " = ?";
        String[] selectionArgs = {username.trim(), password.trim()};

        rs = conn.query(PROFILE, columns, selection, selectionArgs, null, null, null);
        return rs.moveToFirst();
    }

    public boolean RecordExists(String username, String fName, String mName, String lName) {
        SQLiteDatabase conn = this.getReadableDatabase();
        String[] Columns = {USERNAME, FNAME, MNAME, LNAME};
        String selection = USERNAME + " = ? AND " + FNAME + " = ? AND " + MNAME + " = ? AND " + LNAME + " = ?";
        String[] selectionArgs = {username.trim(), fName.trim(), mName.trim(), lName.trim()};

        rs = conn.query(PROFILE, Columns, selection, selectionArgs, null, null, null);
        return rs.moveToFirst();
    }

    public boolean RecordExists(String fName, String mName, String lName, int ignoreRecordIndex) {
        SQLiteDatabase conn = this.getReadableDatabase();
        String[] columns = {FNAME, MNAME, LNAME};
        String selection = FNAME + " = ? AND " + MNAME + " = ? AND " + LNAME + " = ? AND " + PROFILE_ID + " != ?";

        String[] selectionArgs = {fName.trim(), mName.trim(), lName.trim(), String.valueOf(ignoreRecordIndex)};

        rs = conn.query(PROFILE, columns, selection, selectionArgs, null, null, null);
        return rs.moveToFirst();
    }

    public boolean AddRecord(String username, String password, String role, String fName, String mName, String lName, String address, String gender, String contact, Boolean approved) {
        SQLiteDatabase conn = this.getWritableDatabase();
        VALS = new ContentValues();
        VALS.put(USERNAME, username.trim());
        VALS.put(PASSWORD, password.trim());
        VALS.put(ROLE, role.trim());
        VALS.put(FNAME, fName.trim());
        VALS.put(MNAME, mName.trim());
        VALS.put(LNAME, lName.trim());
        VALS.put(ADDRESS, address.trim());
        VALS.put(GENDER, gender.trim());
        VALS.put(CONTACT, contact.trim());
        VALS.put(APPROVED, approved);      // to be approved
        try {
            conn.insert(PROFILE, null, VALS);
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    public boolean ToggleApproveUser(int index, boolean approved) {
        SQLiteDatabase conn = this.getWritableDatabase();
        VALS = new ContentValues();
        VALS.put(APPROVED, approved);
        String selection = PROFILE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(index)};

        int rowsAffected = conn.update(PROFILE, VALS, selection, selectionArgs);
        conn.close();
        return rowsAffected > 0;
    }

    public boolean UpdateRecord(int index, String password, String fName, String mName, String lName, String address, String gender, String contact) {
        SQLiteDatabase conn = this.getWritableDatabase();
        VALS = new ContentValues();
        VALS.put(FNAME, fName.trim());
        VALS.put(MNAME, mName.trim());
        VALS.put(LNAME, lName.trim());
        VALS.put(PASSWORD, password.trim());
        VALS.put(CONTACT, contact.trim());
        VALS.put(ADDRESS, address.trim());
        VALS.put(GENDER, gender.trim());
        String selection = PROFILE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(index)};

        int rowsAffected = conn.update(PROFILE, VALS, selection, selectionArgs);

        conn.close();
        return rowsAffected > 0;
    }

    public boolean DeleteRecords() {
        SQLiteDatabase conn = this.getWritableDatabase();
        int rowsDeleted = conn.delete(PROFILE, null, null);
        conn.close();
        return rowsDeleted > 0;
    }

    public boolean DeleteRecord(int index) {
        SQLiteDatabase conn = this.getWritableDatabase();
        String selection = PROFILE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(index)};

        int rowsDeleted = conn.delete(PROFILE, selection, selectionArgs);
        conn.close();

        return rowsDeleted > 0;
    }

    @SuppressLint("Range")
    public User GetRecord(int index) {
        android.database.sqlite.SQLiteDatabase conn = this.getReadableDatabase();
        String[] recordData;
        User user = null;

        String[] columns = {PROFILE_ID, USERNAME, PASSWORD, ROLE, FNAME, MNAME, LNAME, ADDRESS, GENDER, CONTACT, APPROVED };
        String selection = PROFILE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(index)};

        rs = conn.query(PROFILE, columns, selection, selectionArgs, null, null, null);
        if (rs.moveToFirst()) {
            recordData = new String[]{
                    rs.getString(rs.getColumnIndex(PROFILE_ID)),
                    rs.getString(rs.getColumnIndex(USERNAME)),
                    rs.getString(rs.getColumnIndex(PASSWORD)),
                    rs.getString(rs.getColumnIndex(ROLE)),
                    rs.getString(rs.getColumnIndex(FNAME)),
                    rs.getString(rs.getColumnIndex(MNAME)),
                    rs.getString(rs.getColumnIndex(LNAME)),
                    rs.getString(rs.getColumnIndex(ADDRESS)),
                    rs.getString(rs.getColumnIndex(GENDER)),
                    rs.getString(rs.getColumnIndex(CONTACT)),
                    rs.getString(rs.getColumnIndex(APPROVED))
            };

            // create new User object
            user = new User(
                    Integer.parseInt(recordData[0]),
                    recordData[1],
                    recordData[2],
                    recordData[3],
                    recordData[4],
                    recordData[5],
                    recordData[6],
                    recordData[7],
                    recordData[8],
                    recordData[9],
                    Boolean.parseBoolean(recordData[10].equals("0") ? "false" : "true")
            );
        }

        Log.d("User in GetRecord", user.toString());

        rs.close();
        conn.close();

        return user;
    }

    @SuppressLint("Range")
    public User GetRecord(String username, String password) {
        android.database.sqlite.SQLiteDatabase conn = this.getReadableDatabase();
        String[] recordData;
        User user = null;

        String[] columns = {PROFILE_ID, USERNAME, PASSWORD, ROLE, FNAME, MNAME, LNAME, ADDRESS, GENDER, CONTACT, APPROVED };
        String selection = USERNAME + " = ? AND " + PASSWORD + " = ? ";
        String[] selectionArgs = {username.trim(), password.trim()};

        rs = conn.query(PROFILE, columns, selection, selectionArgs, null, null, null);
        if (rs.moveToFirst()) {
            recordData = new String[]{
                    rs.getString(rs.getColumnIndex(PROFILE_ID)),
                    rs.getString(rs.getColumnIndex(USERNAME)),
                    rs.getString(rs.getColumnIndex(PASSWORD)),
                    rs.getString(rs.getColumnIndex(ROLE)),
                    rs.getString(rs.getColumnIndex(FNAME)),
                    rs.getString(rs.getColumnIndex(MNAME)),
                    rs.getString(rs.getColumnIndex(LNAME)),
                    rs.getString(rs.getColumnIndex(ADDRESS)),
                    rs.getString(rs.getColumnIndex(GENDER)),
                    rs.getString(rs.getColumnIndex(CONTACT)),
                    rs.getString(rs.getColumnIndex(APPROVED))
            };

            // create new User object
            user = new User(
                    Integer.parseInt(recordData[0]),
                    recordData[1],
                    recordData[2],
                    recordData[3],
                    recordData[4],
                    recordData[5],
                    recordData[6],
                    recordData[7],
                    recordData[8],
                    recordData[9],
                    Boolean.parseBoolean(recordData[10].equals("0") ? "false" : "true")
            );
        }

        rs.close();
        conn.close();

        Log.d("User in GetRecord", user.toString());

        return user;
    }

    @SuppressLint("Range")
    public ArrayList<String> GetRecords() {
        SQLiteDatabase conn = this.getReadableDatabase();

        Items = new ArrayList<>();
        ItemsId = new ArrayList<>();

        rs = conn.rawQuery("SELECT * FROM " + PROFILE, null);
        rs.moveToFirst();

        while (!rs.isAfterLast()) {
            ItemsId.add(rs.getInt(rs.getColumnIndex(PROFILE_ID)));
            String fName = rs.getString(rs.getColumnIndex(FNAME));
            String mName = rs.getString(rs.getColumnIndex(MNAME));
            String lName = rs.getString(rs.getColumnIndex(LNAME));

            fName = (fName == null || fName.isEmpty()) ? "N/A" : fName;
            mName = (mName == null || mName.isEmpty()) ? "N/A" : mName;
            lName = (lName == null || lName.isEmpty()) ? "N/A" : lName;

            Items.add(
                    "\nRole: " + rs.getString(rs.getColumnIndex(ROLE)) + " - ID: " + rs.getString(rs.getColumnIndex(PROFILE_ID)) + "\n\n" +
                            "First Name: " + fName + "\n" +
                            "Middle Name: " + mName + "\n" +
                            "Last Name: " + lName + "\n\n" +
                            "Approved: " + (rs.getString(rs.getColumnIndex(APPROVED)).equals("0") ? "FALSE" : "TRUE")  + "\n");
            rs.moveToNext();
        }

        rs.close();
        return Items;
    }
}
