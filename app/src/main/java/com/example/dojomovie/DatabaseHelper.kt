package com.example.dojomovie

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "DoJoMovie.db"
        private const val DB_VERSION = 2

        private const val TABLE_USERS = "users"
        private const val TABLE_FILMS = "films"
        private const val TABLE_TRANSACTIONS = "transactions"

        private const val USER_ID = "user_id"
        private const val USER_PHONE = "phone_number"
        private const val USER_PASSWORD = "password"

        private const val FILM_ID = "film_id"
        private const val FILM_TITLE = "film_title"
        private const val FILM_IMAGE = "film_image"
        private const val FILM_PRICE = "film_price"

        private const val TRANS_ID = "id"
        private const val TRANS_USER_ID = "user_id"
        private const val TRANS_FILM_ID = "film_id"
        private const val TRANS_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = "CREATE TABLE $TABLE_USERS (" +
                "$USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$USER_PHONE VARCHAR UNIQUE," +
                "$USER_PASSWORD VARCHAR" +
                ")"

        val createFilmsTable = "CREATE TABLE $TABLE_FILMS (" +
                "$FILM_ID VARCHAR PRIMARY KEY," +
                "$FILM_TITLE VARCHAR," +
                "$FILM_IMAGE VARCHAR," +
                "$FILM_PRICE INTEGER" +
                ")"

        val createTransTable = "CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$TRANS_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$TRANS_USER_ID INTEGER," +
                "$TRANS_FILM_ID VARCHAR," +
                "$TRANS_QUANTITY INTEGER," +
                "FOREIGN KEY($TRANS_USER_ID) REFERENCES $TABLE_USERS($USER_ID)," +
                "FOREIGN KEY($TRANS_FILM_ID) REFERENCES $TABLE_FILMS($FILM_ID)" +
                ")"

        db?.execSQL(createUsersTable)
        db?.execSQL(createFilmsTable)
        db?.execSQL(createTransTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FILMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertUser(phone: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(USER_PHONE, phone)
            put(USER_PASSWORD, password)
        }

        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    fun checkUserLogin(phone: String, password: String): Int? {
        val db = this.readableDatabase
        val query = "$USER_PHONE = ? AND $USER_PASSWORD = ?"
        val args = arrayOf(phone, password)
        var userId: Int? = null

        val cursor: Cursor? = db.query(
            TABLE_USERS,
            arrayOf(USER_ID),
            query,
            args,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val userIdIndex = it.getColumnIndex(USER_ID)
                if (userIdIndex != -1) {
                    userId = it.getInt(userIdIndex)
                }
            }
        }
        db.close()
        return userId
    }

    fun isPhoneRegistered(phone: String): Boolean {
        val db = this.readableDatabase
        val query = "$USER_PHONE = ?"
        val args = arrayOf(phone)
        var isRegistered = false

        val cursor: Cursor? = db.query(
            TABLE_USERS,
            arrayOf(USER_ID),
            query,
            args,
            null, null, null
        )

        cursor?.use {
            if (it.count > 0) {
                isRegistered = true
            }
        }
        db.close()
        return isRegistered
    }

    fun getUserPhone(userId: Int): String? {
        val db = this.readableDatabase
        val query = "$USER_ID = ?"
        val args = arrayOf(userId.toString())
        var userPhone: String? = null

        val cursor: Cursor? = db.query(
            TABLE_USERS,
            arrayOf(USER_PHONE),
            query,
            args,
            null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val phoneIndex = it.getColumnIndex(USER_PHONE)
                if (phoneIndex != -1) {
                    userPhone = it.getString(phoneIndex)
                }
            }
        }
        db.close()
        return userPhone
    }

    fun insertFilm(film: Film): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(FILM_ID, film.id)
            put(FILM_TITLE, film.title)
            put(FILM_IMAGE, film.image)
            put(FILM_PRICE, film.price)
        }

        val result = db.insertWithOnConflict(TABLE_FILMS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result
    }

    fun getAllFilms(): List<Film> {
        val filmList = mutableListOf<Film>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(TABLE_FILMS, null, null, null, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(FILM_ID))
                val title = it.getString(it.getColumnIndexOrThrow(FILM_TITLE))
                val image = it.getString(it.getColumnIndexOrThrow(FILM_IMAGE))
                val price = it.getInt(it.getColumnIndexOrThrow(FILM_PRICE))

                filmList.add(Film(id, title, image, price))
            }
        }
        db.close()
        return filmList
    }

    fun getFilmById(filmId: String): Film? {
        val db = this.readableDatabase
        val query = "$FILM_ID = ?"
        val args = arrayOf(filmId)
        var film: Film? = null

        val cursor: Cursor? = db.query(
            TABLE_FILMS,
            null,
            query,
            args,
            null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getString(it.getColumnIndexOrThrow(FILM_ID))
                val title = it.getString(it.getColumnIndexOrThrow(FILM_TITLE))
                val image = it.getString(it.getColumnIndexOrThrow(FILM_IMAGE))
                val price = it.getInt(it.getColumnIndexOrThrow(FILM_PRICE))

                film = Film(id, title, image, price)
            }
        }
        db.close()
        return film
    }

    fun insertTransaction(userId: Int, filmId: String, quantity: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(TRANS_USER_ID, userId)
            put(TRANS_FILM_ID, filmId)
            put(TRANS_QUANTITY, quantity)
        }

        val result = db.insert(TABLE_TRANSACTIONS, null, values)
        db.close()
        return result
    }

    fun getUserTransactions(userId: Int): List<Transaction> {
        val transList = mutableListOf<Transaction>()
        val db = this.readableDatabase

        val query = """
            SELECT t.$TRANS_ID, t.$TRANS_USER_ID, 
                   t.$TRANS_FILM_ID, t.$TRANS_QUANTITY,
                   f.$FILM_TITLE, f.$FILM_PRICE
            FROM $TABLE_TRANSACTIONS t
            JOIN $TABLE_FILMS f ON t.$TRANS_FILM_ID = f.$FILM_ID
            WHERE t.$TRANS_USER_ID = ?
        """

        val cursor: Cursor? = db.rawQuery(query, arrayOf(userId.toString()))

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(TRANS_ID))
                val userIdCol = it.getInt(it.getColumnIndexOrThrow(TRANS_USER_ID))
                val filmId = it.getString(it.getColumnIndexOrThrow(TRANS_FILM_ID))
                val quantity = it.getInt(it.getColumnIndexOrThrow(TRANS_QUANTITY))
                val filmTitle = it.getString(it.getColumnIndexOrThrow(FILM_TITLE))
                val filmPrice = it.getInt(it.getColumnIndexOrThrow(FILM_PRICE))

                transList.add(Transaction(id, userIdCol, filmId, quantity, filmTitle, filmPrice))
            }
        }
        db.close()
        return transList
    }
}