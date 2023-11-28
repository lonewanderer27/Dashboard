package com.jay_puzon.dashboard;

import java.io.Serializable;

public class User implements Serializable {
    public Integer id;
    public String username, password, role, fname, mname, lname, address, gender, contact;
    public Boolean approved;
    public User(Integer id, String username, String password, String role, String fname, String mname, String lname, String address, String gender, String contact, Boolean approved) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.address = address;
        this.gender = gender;
        this.contact = contact;
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", fname='" + fname + '\'' +
                ", mname='" + mname + '\'' +
                ", lname='" + lname + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", contact='" + contact + '\'' +
                ", approved=" + approved +
                '}';
    }
}
