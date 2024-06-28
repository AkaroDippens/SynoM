package com.example.synomprojectkotlin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import com.example.synomprojectkotlin.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.security.MessageDigest

class DataBaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTableQuery = ("CREATE TABLE " + TABLE_NAME + "(" + COL_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, "
                + COL_EMAIL + " TEXT, " + COL_PASSWORD + " TEXT, " + COL_USER_ROLE_ID + " INTEGER, "
                + COL_MAX_SYNONYM_SCORE + " INTEGER DEFAULT 0, "
                + COL_MAX_DEFINITION_SCORE + " INTEGER DEFAULT 0);")

        val createRoleTableQuery = ("CREATE TABLE " + TABLE_ROLES + "(" + COL_ROLE_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ROLE_NAME + " TEXT);")

        db.execSQL(createUserTableQuery)
        db.execSQL(createRoleTableQuery)

        val insertAdminRoleQuery = "INSERT INTO $TABLE_ROLES ($COL_ROLE_NAME) VALUES ('Администратор')"
        val insertUserRoleQuery = "INSERT INTO $TABLE_ROLES ($COL_ROLE_NAME) VALUES ('Пользователь')"

        db.execSQL(insertAdminRoleQuery)
        db.execSQL(insertUserRoleQuery)

        val adminPasswordHashed = hashPassword("admin123")
        val insertAdminUserQuery = "INSERT INTO $TABLE_NAME ($COL_USERNAME, $COL_EMAIL, $COL_PASSWORD, $COL_USER_ROLE_ID) " +
                "VALUES ('admin', 'mirzoev.nidzhat@mail.ru', '$adminPasswordHashed', 1)"

        db.execSQL(insertAdminUserQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROLES")
        onCreate(db)
    }

    fun resetDatabase() {
        val db = this.writableDatabase
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROLES")

        onCreate(db)
    }

    fun getUsers(): ArrayList<Users> {
        val list: ArrayList<Users> = ArrayList()
        val db: SQLiteDatabase = this.readableDatabase

        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val username: String = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME))
                val email: String = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
                val password: String = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
                val roleId: Int = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ROLE_ID))

                val user = Users(id, username, email, password, roleId)
                list.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getUserById(id: Int): Users {
        val db: SQLiteDatabase = this.readableDatabase
        val columns = arrayOf(COL_ID, COL_USERNAME, COL_EMAIL, COL_PASSWORD, COL_USER_ROLE_ID, COL_MAX_SYNONYM_SCORE, COL_MAX_DEFINITION_SCORE)
        val selection = "$COL_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor: Cursor? = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)

        cursor?.moveToFirst()
        val userId: Int = cursor!!.getInt(cursor.getColumnIndexOrThrow(COL_ID))
        val username: String = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME))
        val email: String = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
        val password: String = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD))
        val roleId: Int = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ROLE_ID))
        val maxSynonymScore: Int = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MAX_SYNONYM_SCORE))
        val maxDefinitionScore: Int = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MAX_DEFINITION_SCORE))
        cursor.close()

        return Users(userId, username, email, password, roleId, maxSynonymScore, maxDefinitionScore)
    }

    fun addUser(users: Users, context: Context): Long {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(users.email, users.password)
            .addOnSuccessListener {
                val fireBaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                Toast.makeText(
                    context,
                    "Пользователь с почтой ${fireBaseUser?.email} успешно зарегестрирован!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(COL_USERNAME, users.username)
            put(COL_EMAIL, users.email)
            put(COL_PASSWORD, hashPassword(users.password))
            put(COL_USER_ROLE_ID, users.roleId)
        }
        return db.insert(TABLE_NAME, null, cv)
    }

    fun readUser(email: String, password: String): Users? {
        val db = readableDatabase
        val selection = "$COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val selectionArgs = arrayOf(email, hashPassword(password))
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        return if (cursor.moveToFirst()) {
            val userIdIndex = cursor.getColumnIndexOrThrow(COL_ID)
            val usernameIndex = cursor.getColumnIndexOrThrow(COL_USERNAME)
            val emailIndex = cursor.getColumnIndexOrThrow(COL_EMAIL)
            val passwordIndex = cursor.getColumnIndexOrThrow(COL_PASSWORD)
            val roleIdIndex = cursor.getColumnIndexOrThrow(COL_USER_ROLE_ID)
            val maxSynonymScoreIndex = cursor.getColumnIndexOrThrow(COL_MAX_SYNONYM_SCORE)
            val maxDefinitionScoreIndex = cursor.getColumnIndexOrThrow(COL_MAX_DEFINITION_SCORE)

            val userId: Int = cursor.getInt(userIdIndex)
            val username: String = cursor.getString(usernameIndex)
            val email: String = cursor.getString(emailIndex)
            val password: String = cursor.getString(passwordIndex)
            val roleId: Int = cursor.getInt(roleIdIndex)
            val maxSynonymScore: Int = cursor.getInt(maxSynonymScoreIndex)
            val maxProposalScore: Int = cursor.getInt(maxDefinitionScoreIndex)
            cursor.close()

            Users(userId, username, email, password, roleId, maxSynonymScore, maxProposalScore)
        } else {
            cursor.close()
            null
        }
    }

    fun readUserForScore(email: String, password: String): Users? {
        val db = readableDatabase
        val selection = "$COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        return if (cursor.moveToFirst()) {
            val userIdIndex = cursor.getColumnIndexOrThrow(COL_ID)
            val usernameIndex = cursor.getColumnIndexOrThrow(COL_USERNAME)
            val emailIndex = cursor.getColumnIndexOrThrow(COL_EMAIL)
            val passwordIndex = cursor.getColumnIndexOrThrow(COL_PASSWORD)
            val roleIdIndex = cursor.getColumnIndexOrThrow(COL_USER_ROLE_ID)
            val maxSynonymScoreIndex = cursor.getColumnIndexOrThrow(COL_MAX_SYNONYM_SCORE)
            val maxDefinitionScoreIndex = cursor.getColumnIndexOrThrow(COL_MAX_DEFINITION_SCORE)

            val userId: Int = cursor.getInt(userIdIndex)
            val username: String = cursor.getString(usernameIndex)
            val email: String = cursor.getString(emailIndex)
            val password: String = cursor.getString(passwordIndex)
            val roleId: Int = cursor.getInt(roleIdIndex)
            val maxSynonymScore: Int = cursor.getInt(maxSynonymScoreIndex)
            val maxProposalScore: Int = cursor.getInt(maxDefinitionScoreIndex)
            cursor.close()

            Users(userId, username, email, password, roleId, maxSynonymScore, maxProposalScore)
        } else {
            cursor.close()
            null
        }
    }

    fun readUserFirebase(email: String): Users? {
        val db = readableDatabase
        val selection = "$COL_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        return if (cursor.moveToFirst()) {
            val userIdIndex = cursor.getColumnIndexOrThrow(COL_ID)
            val usernameIndex = cursor.getColumnIndexOrThrow(COL_USERNAME)
            val emailIndex = cursor.getColumnIndexOrThrow(COL_EMAIL)
            val passwordIndex = cursor.getColumnIndexOrThrow(COL_PASSWORD)
            val roleIdIndex = cursor.getColumnIndexOrThrow(COL_USER_ROLE_ID)
            val maxSynonymScoreIndex = cursor.getColumnIndexOrThrow(COL_MAX_SYNONYM_SCORE)
            val maxDefinitionScoreIndex = cursor.getColumnIndexOrThrow(COL_MAX_DEFINITION_SCORE)

            val userId: Int = cursor.getInt(userIdIndex)
            val username: String = cursor.getString(usernameIndex)
            val email: String = cursor.getString(emailIndex)
            val password: String = cursor.getString(passwordIndex)
            val roleId: Int = cursor.getInt(roleIdIndex)
            val maxSynonymScore: Int = cursor.getInt(maxSynonymScoreIndex)
            val maxProposalScore: Int = cursor.getInt(maxDefinitionScoreIndex)
            cursor.close()

            Users(userId, username, email, password, roleId, maxSynonymScore, maxProposalScore)
        } else {
            cursor.close()
            null
        }
    }

    fun updateUserScores(email: String, maxSynonymScore: Int, maxDefinitionScore: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_MAX_SYNONYM_SCORE, maxSynonymScore)
        values.put(COL_MAX_DEFINITION_SCORE, maxDefinitionScore)
        return db.update(TABLE_NAME, values, "$COL_EMAIL = ?", arrayOf(email))
    }

    fun updateUser(email: String, password: String): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_PASSWORD, hashPassword(password))
        return db.update(TABLE_NAME, values, "$COL_EMAIL = ?", arrayOf(email))
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun deleteUser(id: Int) {
        val db: SQLiteDatabase = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val DB_NAME = "users.db"
        var TABLE_NAME = "Users"
        var COL_ID = "id"
        var COL_USERNAME = "username"
        var COL_EMAIL = "email"
        var COL_PASSWORD = "password"
        var COL_USER_ROLE_ID = "user_role_id"
        var COL_MAX_SYNONYM_SCORE = "max_synonym_score"
        var COL_MAX_DEFINITION_SCORE = "max_definition_score"

        var TABLE_ROLES = "Roles"
        var COL_ROLE_ID = "role_id"
        var COL_ROLE_NAME = "role_name"
    }
}