package com.example.dojomovie
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log // Untuk debugging, opsional

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DoJoMovie.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val TABLE_FILMS = "films"
        private const val TABLE_TRANSACTIONS = "transactions"

        // Users
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_PHONE_NUMBER = "phone_number"
        private const val COLUMN_PASSWORD = "password"

        // Films
        private const val COLUMN_FILM_ID = "film_id"
        private const val COLUMN_FILM_TITLE = "film_title"
        private const val COLUMN_FILM_IMAGE = "film_image"
        private const val COLUMN_FILM_PRICE = "film_price"

        //Transactions
        private const val COLUMN_TRANSACTION_ID = "id"
        private const val COLUMN_TRANSACTION_USER_ID = "user_id"
        private const val COLUMN_TRANSACTION_FILM_ID = "film_id"
        private const val COLUMN_TRANSACTION_QUANTITY = "quantity"
    }


    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_USERS_TABLE = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PHONE_NUMBER VARCHAR UNIQUE," +
                "$COLUMN_PASSWORD VARCHAR" +
                ")"

        val CREATE_FILMS_TABLE = "CREATE TABLE $TABLE_FILMS (" +
                "$COLUMN_FILM_ID INTEGER PRIMARY KEY," +
                "$COLUMN_FILM_TITLE VARCHAR," +
                "$COLUMN_FILM_IMAGE VARCHAR," +
                "$COLUMN_FILM_PRICE INTEGER" +
                ")"

        val CREATE_TRANSACTIONS_TABLE = "CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$COLUMN_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TRANSACTION_USER_ID INTEGER," +
                "$COLUMN_TRANSACTION_FILM_ID INTEGER," +
                "$COLUMN_TRANSACTION_QUANTITY INTEGER," +
                "FOREIGN KEY($COLUMN_TRANSACTION_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
                "FOREIGN KEY($COLUMN_TRANSACTION_FILM_ID) REFERENCES $TABLE_FILMS($COLUMN_FILM_ID)" +
                ")"


        db?.execSQL(CREATE_USERS_TABLE)
        db?.execSQL(CREATE_FILMS_TABLE)
        db?.execSQL(CREATE_TRANSACTIONS_TABLE)

        Log.d("DatabaseHelper", "Database tables created")
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FILMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
        Log.d("DatabaseHelper", "Database tables upgraded")
    }

    fun insertUser(phoneNumber: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PHONE_NUMBER, phoneNumber)
            put(COLUMN_PASSWORD, password)
        }

        val newRowId = db.insert(TABLE_USERS, null, values)
        db.close()
        Log.d("DatabaseHelper", "Inserted user: $phoneNumber, Row ID: $newRowId")
        return newRowId
    }


    fun checkUserCredentials(phoneNumber: String, password: String): Int? {
        val db = this.readableDatabase
        val selection = "$COLUMN_PHONE_NUMBER = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(phoneNumber, password)
        var userId: Int? = null

        val cursor: Cursor? = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {

                val userIdIndex = it.getColumnIndex(COLUMN_USER_ID)
                if (userIdIndex != -1) {
                    userId = it.getInt(userIdIndex)
                    Log.d("DatabaseHelper", "User found: ID $userId for phone $phoneNumber")
                } else {
                    Log.e("DatabaseHelper", "Column user_id not found in cursor")
                }

            } else {
                Log.d("DatabaseHelper", "User not found for phone $phoneNumber")
            }
        }
        db.close()
        return userId
    }

    fun isPhoneNumberRegistered(phoneNumber: String): Boolean {
        val db = this.readableDatabase
        val selection = "$COLUMN_PHONE_NUMBER = ?"
        val selectionArgs = arrayOf(phoneNumber)
        var isRegistered = false

        val cursor: Cursor? = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            selection,
            selectionArgs,
            null, null, null
        )

        cursor?.use {
            if (it.count > 0) {
                isRegistered = true
                Log.d("DatabaseHelper", "Phone number $phoneNumber is already registered")
            } else {
                Log.d("DatabaseHelper", "Phone number $phoneNumber is not registered")
            }
        }
        db.close()
        return isRegistered
    }
}