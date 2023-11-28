package com.jay_puzon.dashboard

import java.io.Serializable

class User(
    @JvmField var id: Int,
    @JvmField var username: String,
    @JvmField var password: String,
    @JvmField var role: String,
    @JvmField var fname: String,
    @JvmField var mname: String,
    @JvmField var lname: String,
    @JvmField var address: String,
    @JvmField var gender: String,
    @JvmField var contact: String,
    @JvmField var approved: Boolean
) : Serializable {
    override fun toString(): String {
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
                '}'
    }
}