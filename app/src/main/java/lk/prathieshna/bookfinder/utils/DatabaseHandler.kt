package lk.prathieshna.bookfinder.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import lk.prathieshna.bookfinder.domain.local.ImageLinks
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.domain.local.VolumeInfo
import lk.prathieshna.bookfinder.state.projections.*
import lk.prathieshna.bookfinder.store.bookFinderStore

class DatabaseHandler(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "BookDatabase"
        private const val TABLE_FAVOURITES = "FavouriteBooks"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_AUTHOR = "author"
        private const val KEY_SUBTITLE = "subtitle"
        private const val KEY_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql = ("CREATE TABLE " + TABLE_FAVOURITES + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_AUTHOR + " TEXT,"
                + KEY_SUBTITLE + " TEXT,"
                + KEY_IMAGE_URL + " TEXT"
                + ")")
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_FAVOURITES")
        onCreate(db)
    }

    fun getFavouriteStatus(): Boolean {
        val id = getSelectedItemId(bookFinderStore.state, context)
        val selectQuery = "SELECT  * FROM $TABLE_FAVOURITES WHERE id = '$id'"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery(selectQuery, null)
            cursor?.moveToFirst() ?: false
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            cursor?.close()
            false
        }
    }

    fun getFavouriteBooks(): List<Item> {
        val selectQuery = "SELECT * FROM $TABLE_FAVOURITES"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery(selectQuery, null)
            val list: ArrayList<Item> = ArrayList<Item>()
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex(KEY_ID))
                    val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                    val author = cursor.getString(cursor.getColumnIndex(KEY_AUTHOR))
                    val subtitle = cursor.getString(cursor.getColumnIndex(KEY_SUBTITLE))
                    val imageUrl = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL))
                    val item = Item().copy(
                        id = id,
                        volumeInfo = VolumeInfo(
                            title = name,
                            subtitle = subtitle,
                            authors = listOf(author),
                            imageLinks = ImageLinks(thumbnail = imageUrl)
                        )
                    )
                    list.add(item)
                } while (cursor.moveToNext())
            }
            cursor.close()
            list
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            cursor?.close()
            listOf()
        }
    }

    fun addToFavourites(): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, getSelectedItemId(bookFinderStore.state, context))
        contentValues.put(KEY_NAME, getSelectedItemVolumeName(bookFinderStore.state, context))
        contentValues.put(KEY_AUTHOR, getSelectedItemVolumeAuthors(bookFinderStore.state, context))
        contentValues.put(
            KEY_SUBTITLE,
            getSelectedItemVolumeSubtitle(bookFinderStore.state, context)
        )
        contentValues.put(
            KEY_IMAGE_URL,
            getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state)
        )
        val success = db.insert(TABLE_FAVOURITES, null, contentValues)
        db.close()
        return success
    }

    fun removeFromFavourites(): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, getSelectedItemId(bookFinderStore.state, context))
        val success = db.delete(
            TABLE_FAVOURITES,
            "id= '" + getSelectedItemId(bookFinderStore.state, context) + "'",
            null
        )
        db.close()
        return success
    }
}