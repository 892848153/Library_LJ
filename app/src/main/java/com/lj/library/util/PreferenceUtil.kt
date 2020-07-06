package com.lj.library.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lj.library.bean.UserInfo

class PreferenceUtil @SuppressLint("CommitPrefEdits") constructor(context: Context, filename: String) {

    private val sp: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
//        sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE)

        // 使用加密的SharedPreferences
        sp = getEncryptedSharedPreferences(context, filename)
        editor = sp.edit()
    }

    private fun getEncryptedSharedPreferences(context: Context, filename: String): SharedPreferences {

        val masterKey: MasterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        val sharedPreferences = EncryptedSharedPreferences.create(context,
                filename,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences
    }

    var isFirstRun: Boolean
        get() = sp.getBoolean("isFirstRun", true)
        set(isFirstOpen) {
            editor.putBoolean("isFirstRun", isFirstOpen)
            editor.commit()
        }

    var flashImageUrl: String?
        get() = sp.getString("flashUrl", "")
        set(url) {
            editor.putString("flashUrl", url)
            editor.commit()
        }

    var userInfo: UserInfo
        get() {
            val userId = sp.getString("userId", "")
            val userInfo = UserInfo()
            userInfo.userId = userId
            return userInfo
        }
        set(userInfo) {
            editor.putString("userId", userInfo.userId)
            editor.commit()
        }

    var username: String?
        get() = sp.getString("username", "")
        set(username) {
            editor.putString("username", username)
            editor.commit()
        }

    var password: String?
        get() = sp.getString("password", "")
        set(password) {
            editor.putString("password", password)
            editor.commit()
        }


}