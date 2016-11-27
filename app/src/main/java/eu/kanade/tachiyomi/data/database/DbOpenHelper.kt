package eu.kanade.tachiyomi.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import eu.kanade.tachiyomi.data.database.tables.*

class DbOpenHelper(context: Context)
: SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        /**
         * Name of the database file.
         */
        const val DATABASE_NAME = "tachiyomi.db"

        /**
         * Version of the database.
         */
        const val DATABASE_VERSION = 5
    }

    override fun onCreate(db: SQLiteDatabase) = with(db) {
        execSQL(MangaTable.createTableQuery)
        execSQL(ChapterTable.createTableQuery)
        execSQL(MangaSyncTable.createTableQuery)
        execSQL(CategoryTable.createTableQuery)
        execSQL(MangaCategoryTable.createTableQuery)
        execSQL(HistoryTable.createTableQuery)

        // DB indexes
        execSQL(MangaTable.createUrlIndexQuery)
        execSQL(MangaTable.createFavoriteIndexQuery)
        execSQL(ChapterTable.createMangaIdIndexQuery)
        execSQL(HistoryTable.createChapterIdIndexQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(ChapterTable.sourceOrderUpdateQuery)

            // Fix kissmanga covers after supporting cloudflare
            db.execSQL("""UPDATE mangas SET thumbnail_url =
                    REPLACE(thumbnail_url, '93.174.95.110', 'kissmanga.com') WHERE source = 4""")
        }
        if (oldVersion < 3) {
            // Initialize history tables
            db.execSQL(HistoryTable.createTableQuery)
            db.execSQL(HistoryTable.createChapterIdIndexQuery)
        }
        if (oldVersion < 4) {
            db.execSQL(ChapterTable.bookmarkUpdateQuery)
        }
        if (oldVersion < 5) {
            // Full db migration

            with(MangaTable) {
                val old = listOf(
                        "_id", "source", "url", "artist", "author", "description",
                        "genre", "title", "status", "thumbnail_url", "favorite",
                        "last_update", "initialized", "viewer", "chapter_flags")
                val new = listOf(
                        COL_ID, COL_SOURCE, COL_URL, COL_ARTIST, COL_AUTHOR, COL_DESCRIPTION,
                        COL_GENRE, COL_TITLE, COL_STATUS, COL_THUMBNAIL_URL, COL_FAVORITE,
                        COL_LAST_UPDATE, COL_INITIALIZED, COL_VIEWER, COL_CHAPTER_FLAGS)

                prefixMigration(db, TABLE, createTableQuery, old, new)
            }

            with(ChapterTable) {
                val old = listOf(
                        "_id", "manga_id", "url", "name", "read", "bookmark",
                        "last_page_read", "chapter_number", "source_order", "date_fetch",
                        "date_upload")
                val new = listOf(
                        COL_ID, COL_MANGA_ID, COL_URL, COL_NAME, COL_READ, COL_BOOKMARK,
                        COL_LAST_PAGE_READ, COL_NUMBER, COL_SOURCE_ORDER, COL_DATE_FETCH,
                        COL_DATE_UPLOAD)

                prefixMigration(db, TABLE, createTableQuery, old, new)
            }

            with(MangaSyncTable) {
                val old = listOf(
                        "_id", "manga_id", "sync_id", "remote_id", "title",
                        "last_chapter_read", "status", "score", "total_chapters")
                val new = listOf(
                        COL_ID, COL_MANGA_ID, COL_SERVICE_ID, COL_REMOTE_ID, COL_TITLE,
                        COL_LAST_CHAPTER_READ, COL_STATUS, COL_SCORE, COL_TOTAL_CHAPTERS)

                prefixMigration(db, TABLE, createTableQuery, old, new)
            }

            with(CategoryTable) {
                val old = listOf("_id", "name", "sort", "flags")
                val new = listOf(COL_ID, COL_NAME, COL_ORDER, COL_FLAGS)

                prefixMigration(db, TABLE, createTableQuery, old, new)
            }

            with(MangaCategoryTable) {
                val old = listOf("_id", "manga_id", "category_id")
                val new = listOf(COL_ID, COL_MANGA_ID, COL_CATEGORY_ID)

                prefixMigration(db, TABLE, createTableQuery, old, new)
            }

            with(HistoryTable) {
                val old = listOf(
                        "history_id", "history_chapter_id", "history_last_read", "history_time_read")
                val new = listOf(COL_ID, COL_CHAPTER_ID, COL_LAST_READ, COL_TIME_READ)

                prefixMigration(db, TABLE, createTableQuery, old, new)
            }
        }
    }

    override fun onOpen(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    private fun prefixMigration(db: SQLiteDatabase, table: String, createTable: String,
                                oldCols: List<String>, newCols: List<String>) {

        db.execSQL("ALTER TABLE $table RENAME TO ${table}_tmp")
        db.execSQL(createTable)
        db.execSQL("INSERT INTO $table (${newCols.joinToString()})"
                + " SELECT ${oldCols.joinToString()} FROM ${table}_tmp")
        db.execSQL("DROP TABLE ${table}_tmp")
    }

}
