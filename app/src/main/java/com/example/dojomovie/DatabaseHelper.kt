package com.example.dojomovie

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, "DoJoMovie", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        val queryCreateUsersTable = "CREATE TABLE IF NOT EXISTS users(" +
                "user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "phone_number VARCHAR(255) NOT NULL," +
                "password VARCHAR(255) NOT NULL" +
                ")"

        val queryCreateFilmsTable = "CREATE TABLE IF NOT EXISTS films(" +
                "film_id VARCHAR(255) NOT NULL PRIMARY KEY," +
                "film_title VARCHAR(255) NOT NULL," +
                "film_image VARCHAR(255) NOT NULL," +
                "film_price INTEGER NOT NULL," +
                "film_synopsis TEXT DEFAULT 'An exciting movie experience that will keep you entertained from start to finish.'" +
                ")"

        val queryCreateTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "film_id VARCHAR(255) NOT NULL," +
                "quantity INTEGER NOT NULL" +
                ")"

        db?.execSQL(queryCreateUsersTable)
        db?.execSQL(queryCreateFilmsTable)
        db?.execSQL(queryCreateTransactionsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE films ADD COLUMN film_synopsis TEXT DEFAULT 'An exciting movie experience that will keep you entertained from start to finish.'")
        }
    }

    fun createUser(phone: String, password: String) {
        var db = writableDatabase
        var values = ContentValues().apply {
            put("phone_number", phone)
            put("password", password)
        }
        db.insert("users", null, values)
        db.close()
    }

    fun getUser(): ArrayList<User> {
        val result = ArrayList<User>()
        val db = writableDatabase
        val query = "SELECT * FROM users"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        if(cursor.count > 0) {
            do {
                var id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
                var phone = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"))
                var password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                result.add(User(id.toString(), phone, password))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return result
    }

    fun updateUser(user: User) {
        var db = writableDatabase
        var values = ContentValues().apply {
            put("phone_number", user.phone)
            put("password", user.password)
        }
        db.update("users", values, "user_id = ?", arrayOf(user.id))
        db.close()
    }

    fun deleteUser(id: String) {
        var db = writableDatabase
        db.delete("users", "user_id = ?", arrayOf(id))
        db.close()
    }

    fun createFilm(film: Film) {
        var db = writableDatabase
        var values = ContentValues().apply {
            put("film_id", film.id)
            put("film_title", film.title)
            put("film_image", film.image)
            put("film_price", film.price)
            put("film_synopsis", film.synopsis)
        }
        db.insertWithOnConflict("films", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun getFilm(): ArrayList<Film> {
        val result = ArrayList<Film>()
        val db = writableDatabase
        val query = "SELECT * FROM films"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        if(cursor.count > 0) {
            do {
                var id = cursor.getString(cursor.getColumnIndexOrThrow("film_id"))
                var title = cursor.getString(cursor.getColumnIndexOrThrow("film_title"))
                var image = cursor.getString(cursor.getColumnIndexOrThrow("film_image"))
                var price = cursor.getInt(cursor.getColumnIndexOrThrow("film_price"))

                var synopsis = try {
                    cursor.getString(cursor.getColumnIndexOrThrow("film_synopsis"))
                } catch (e: Exception) {
                    "An exciting movie experience that will keep you entertained from start to finish."
                }

                result.add(Film(id, title, image, price, synopsis))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return result
    }

    fun updateFilm(film: Film) {
        var db = writableDatabase
        var values = ContentValues().apply {
            put("film_title", film.title)
            put("film_image", film.image)
            put("film_price", film.price)
            put("film_synopsis", film.synopsis)
        }
        db.update("films", values, "film_id = ?", arrayOf(film.id))
        db.close()
    }

    fun deleteFilm(id: String) {
        var db = writableDatabase
        db.delete("films", "film_id = ?", arrayOf(id))
        db.close()
    }

    fun createTransaction(userId: Int, filmId: String, quantity: Int) {
        var db = writableDatabase
        var values = ContentValues().apply {
            put("user_id", userId)
            put("film_id", filmId)
            put("quantity", quantity)
        }
        db.insert("transactions", null, values)
        db.close()
    }

    fun getTransaction(): ArrayList<Transaction> {
        val result = ArrayList<Transaction>()
        val db = writableDatabase
        val query = "SELECT * FROM transactions"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        if(cursor.count > 0) {
            do {
                var id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                var userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
                var filmId = cursor.getString(cursor.getColumnIndexOrThrow("film_id"))
                var quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                result.add(Transaction(id, userId, filmId, quantity))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return result
    }

    fun updateTransaction(transaction: Transaction) {
        var db = writableDatabase
        var values = ContentValues().apply {
            put("user_id", transaction.userId)
            put("film_id", transaction.filmId)
            put("quantity", transaction.quantity)
        }
        db.update("transactions", values, "id = ?", arrayOf(transaction.id.toString()))
        db.close()
    }

    fun deleteTransaction(id: String) {
        var db = writableDatabase
        db.delete("transactions", "id = ?", arrayOf(id))
        db.close()
    }

    fun insertUser(phone: String, password: String): Long {
        createUser(phone, password)
        return 1
    }

    fun checkUserLogin(phone: String, password: String): Int? {
        val db = readableDatabase
        val query = "SELECT user_id FROM users WHERE phone_number = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(phone, password))

        var userId: Int? = null
        cursor.moveToFirst()
        if (cursor.count > 0) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
        }

        cursor.close()
        db.close()
        return userId
    }

    fun isPhoneRegistered(phone: String): Boolean {
        val db = readableDatabase
        val query = "SELECT user_id FROM users WHERE phone_number = ?"
        val cursor = db.rawQuery(query, arrayOf(phone))

        val isRegistered = cursor.count > 0
        cursor.close()
        db.close()
        return isRegistered
    }

    fun getUserPhone(userId: Int): String? {
        val db = readableDatabase
        val query = "SELECT phone_number FROM users WHERE user_id = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        var userPhone: String? = null
        cursor.moveToFirst()
        if (cursor.count > 0) {
            userPhone = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"))
        }

        cursor.close()
        db.close()
        return userPhone
    }

    fun insertFilm(film: Film): Long {
        createFilm(film)
        return 1
    }

    fun getAllFilms(): List<Film> {
        return getFilm()
    }

    fun getFilmById(filmId: String): Film? {
        val db = readableDatabase
        val query = "SELECT * FROM films WHERE film_id = ?"
        val cursor = db.rawQuery(query, arrayOf(filmId))

        var film: Film? = null
        cursor.moveToFirst()
        if (cursor.count > 0) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("film_id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("film_title"))
            val image = cursor.getString(cursor.getColumnIndexOrThrow("film_image"))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow("film_price"))

            val synopsis = try {
                cursor.getString(cursor.getColumnIndexOrThrow("film_synopsis"))
            } catch (e: Exception) {
                "An exciting movie experience that will keep you entertained from start to finish."
            }

            film = Film(id, title, image, price, synopsis)
        }

        cursor.close()
        db.close()
        return film
    }

    fun insertTransaction(userId: Int, filmId: String, quantity: Int): Long {
        createTransaction(userId, filmId, quantity)
        return 1
    }

    fun getUserTransactions(userId: Int): List<Transaction> {
        val result = ArrayList<Transaction>()
        val db = readableDatabase

        val query = """
            SELECT t.id, t.user_id, t.film_id, t.quantity,
                   f.film_title, f.film_price
            FROM transactions t
            JOIN films f ON t.film_id = f.film_id
            WHERE t.user_id = ?
        """

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        cursor.moveToFirst()

        if (cursor.count > 0) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val userIdCol = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
                val filmId = cursor.getString(cursor.getColumnIndexOrThrow("film_id"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                val filmTitle = cursor.getString(cursor.getColumnIndexOrThrow("film_title"))
                val filmPrice = cursor.getInt(cursor.getColumnIndexOrThrow("film_price"))

                result.add(Transaction(id, userIdCol, filmId, quantity, filmTitle, filmPrice))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return result
    }
}

data class User(val id: String, val phone: String, val password: String)