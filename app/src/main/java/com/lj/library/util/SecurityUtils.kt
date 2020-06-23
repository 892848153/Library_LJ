package com.lj.library.util

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * 如果您存储的数据特别敏感或私密，请考虑从 Security 库使用 EncryptedFile 对象，而非 File 对象。
 * Created by liujie on 2020-06-15.
 */
object SecurityUtils {

    fun writeToEncryptedFile(context: Context, directory: String, filename: String, content: String) {
        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
//        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val masterKey: MasterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        // Creates a file with this name, or replaces an existing file
        // that has the same name. Note that the file name cannot contain
        // path separators.
        val encryptedFile = EncryptedFile.Builder(
                context,
                File(directory, filename),
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().bufferedWriter().use {
            it.write(content)
        }
    }

    fun readFromEncryptedFile(context: Context, directory: String, filename: String): String {
        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
//        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val masterKey: MasterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        val encryptedFile = EncryptedFile.Builder(
                context,
                File(directory, filename),
                masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val contents = BufferedReader(InputStreamReader(encryptedFile.openFileInput())).useLines { lines ->
            lines.fold("") { working, line ->
                "$working\n$line"
            }
        }

        return contents
    }

}