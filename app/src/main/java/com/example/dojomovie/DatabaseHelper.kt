package com.example.dojomovie

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DoJoMovie.db"
        private const val DATABASE_VERSION = 2 // 🔧 UPDATED: Increment version for schema change

        private const val TABLE_USERS = "users"
        private const val TABLE_FILMS = "films"
        private const val TABLE_TRANSACTIONS = "transactions"

        // Users
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_PHONE_NUMBER = "phone_number"
        private const val COLUMN_PASSWORD = "password"

        // Films - 🔧 UPDATED: film_id is now VARCHAR
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

        // 🔧 UPDATED: film_id is now VARCHAR PRIMARY KEY
        val CREATE_FILMS_TABLE = "CREATE TABLE $TABLE_FILMS (" +
                "$COLUMN_FILM_ID VARCHAR PRIMARY KEY," +
                "$COLUMN_FILM_TITLE VARCHAR," +
                "$COLUMN_FILM_IMAGE VARCHAR," +
                "$COLUMN_FILM_PRICE INTEGER" +
                ")"

        // 🔧 UPDATED: transaction film_id is now VARCHAR
        val CREATE_TRANSACTIONS_TABLE = "CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$COLUMN_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TRANSACTION_USER_ID INTEGER," +
                "$COLUMN_TRANSACTION_FILM_ID VARCHAR," +
                "$COLUMN_TRANSACTION_QUANTITY INTEGER," +
                "FOREIGN KEY($COLUMN_TRANSACTION_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
                "FOREIGN KEY($COLUMN_TRANSACTION_FILM_ID) REFERENCES $TABLE_FILMS($COLUMN_FILM_ID)" +
                ")"

        db?.execSQL(CREATE_USERS_TABLE)
        db?.execSQL(CREATE_FILMS_TABLE)
        db?.execSQL(CREATE_TRANSACTIONS_TABLE)

        Log.d("DatabaseHelper", "Database tables created with String film_id")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FILMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
        Log.d("DatabaseHelper", "Database tables upgraded")
    }

    // User operations (unchanged)
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

    fun getUserPhoneNumber(userId: Int): String? {
        val db = this.readableDatabase
        val selection = "$COLUMN_USER_ID = ?"
        val selectionArgs = arrayOf(userId.toString())
        var phoneNumber: String? = null

        val cursor: Cursor? = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_PHONE_NUMBER),
            selection,
            selectionArgs,
            null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val phoneIndex = it.getColumnIndex(COLUMN_PHONE_NUMBER)
                if (phoneIndex != -1) {
                    phoneNumber = it.getString(phoneIndex)
                }
            }
        }
        db.close()
        return phoneNumber
    }

    // 🔧 UPDATED: Film operations with String ID
    fun insertFilm(film: Film): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FILM_ID, film.id)          // String ID
            put(COLUMN_FILM_TITLE, film.title)
            put(COLUMN_FILM_IMAGE, film.image)
            put(COLUMN_FILM_PRICE, film.price)
        }

        val newRowId = db.insertWithOnConflict(TABLE_FILMS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        Log.d("DatabaseHelper", "Inserted film: ${film.title} with ID: ${film.id}, Row ID: $newRowId")
        return newRowId
    }

    fun getAllFilms(): List<Film> {
        val films = mutableListOf<Film>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(TABLE_FILMS, null, null, null, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(COLUMN_FILM_ID))      // String ID
                val title = it.getString(it.getColumnIndexOrThrow(COLUMN_FILM_TITLE))
                val image = it.getString(it.getColumnIndexOrThrow(COLUMN_FILM_IMAGE))
                val price = it.getInt(it.getColumnIndexOrThrow(COLUMN_FILM_PRICE))

                films.add(Film(id, title, image, price))
            }
        }
        db.close()
        Log.d("DatabaseHelper", "Retrieved ${films.size} films from database")
        return films
    }

    // 🔧 UPDATED: getFilmById with String parameter
    fun getFilmById(filmId: String): Film? {
        val db = this.readableDatabase
        val selection = "$COLUMN_FILM_ID = ?"
        val selectionArgs = arrayOf(filmId)
        var film: Film? = null

        val cursor: Cursor? = db.query(
            TABLE_FILMS,
            null,
            selection,
            selectionArgs,
            null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getString(it.getColumnIndexOrThrow(COLUMN_FILM_ID))      // String ID
                val title = it.getString(it.getColumnIndexOrThrow(COLUMN_FILM_TITLE))
                val image = it.getString(it.getColumnIndexOrThrow(COLUMN_FILM_IMAGE))
                val price = it.getInt(it.getColumnIndexOrThrow(COLUMN_FILM_PRICE))

                film = Film(id, title, image, price)
            }
        }
        db.close()
        Log.d("DatabaseHelper", "Retrieved film: $film")
        return film
    }

    // 🔧 UPDATED: Transaction operations with String film_id
    fun insertTransaction(userId: Int, filmId: String, quantity: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TRANSACTION_USER_ID, userId)
            put(COLUMN_TRANSACTION_FILM_ID, filmId)     // String filmId
            put(COLUMN_TRANSACTION_QUANTITY, quantity)
        }

        val newRowId = db.insert(TABLE_TRANSACTIONS, null, values)
        db.close()
        Log.d("DatabaseHelper", "Inserted transaction: User $userId, Film $filmId, Quantity $quantity")
        return newRowId
    }

    fun getUserTransactions(userId: Int): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase

        val query = """
            SELECT t.$COLUMN_TRANSACTION_ID, t.$COLUMN_TRANSACTION_USER_ID, 
                   t.$COLUMN_TRANSACTION_FILM_ID, t.$COLUMN_TRANSACTION_QUANTITY,
                   f.$COLUMN_FILM_TITLE, f.$COLUMN_FILM_PRICE
            FROM $TABLE_TRANSACTIONS t
            JOIN $TABLE_FILMS f ON t.$COLUMN_TRANSACTION_FILM_ID = f.$COLUMN_FILM_ID
            WHERE t.$COLUMN_TRANSACTION_USER_ID = ?
        """

        val cursor: Cursor? = db.rawQuery(query, arrayOf(userId.toString()))

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_TRANSACTION_ID))
                val userIdCol = it.getInt(it.getColumnIndexOrThrow(COLUMN_TRANSACTION_USER_ID))
                val filmId = it.getString(it.getColumnIndexOrThrow(COLUMN_TRANSACTION_FILM_ID))  // String filmId
                val quantity = it.getInt(it.getColumnIndexOrThrow(COLUMN_TRANSACTION_QUANTITY))
                val filmTitle = it.getString(it.getColumnIndexOrThrow(COLUMN_FILM_TITLE))
                val filmPrice = it.getInt(it.getColumnIndexOrThrow(COLUMN_FILM_PRICE))

                transactions.add(Transaction(id, userIdCol, filmId, quantity, filmTitle, filmPrice))
            }
        }
        db.close()
        Log.d("DatabaseHelper", "Retrieved ${transactions.size} transactions for user $userId")
        return transactions
    }
}