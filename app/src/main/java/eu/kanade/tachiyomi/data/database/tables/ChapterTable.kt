package eu.kanade.tachiyomi.data.database.tables

object ChapterTable {

    const val TABLE = "chapters"

    const val COL_ID = "ch_id"

    const val COL_MANGA_ID = "ch_manga_id"

    const val COL_URL = "ch_url"

    const val COL_NAME = "ch_name"

    const val COL_READ = "ch_read"

    const val COL_BOOKMARK = "ch_bookmark"

    const val COL_LAST_PAGE_READ = "ch_last_page_read"

    const val COL_NUMBER = "ch_number"

    const val COL_SOURCE_ORDER = "ch_source_order"

    const val COL_DATE_FETCH = "ch_date_fetch"

    const val COL_DATE_UPLOAD = "ch_date_upload"

    val createTableQuery: String
        get() = """CREATE TABLE $TABLE(
            $COL_ID INTEGER NOT NULL PRIMARY KEY,
            $COL_MANGA_ID INTEGER NOT NULL,
            $COL_URL TEXT NOT NULL,
            $COL_NAME TEXT NOT NULL,
            $COL_READ BOOLEAN NOT NULL,
            $COL_BOOKMARK BOOLEAN NOT NULL,
            $COL_LAST_PAGE_READ INT NOT NULL,
            $COL_NUMBER FLOAT NOT NULL,
            $COL_SOURCE_ORDER INTEGER NOT NULL,
            $COL_DATE_FETCH LONG NOT NULL,
            $COL_DATE_UPLOAD LONG NOT NULL,
            FOREIGN KEY($COL_MANGA_ID) REFERENCES ${MangaTable.TABLE} (${MangaTable.COL_ID})
            ON DELETE CASCADE
            )"""

    val createMangaIdIndexQuery: String
        get() = "CREATE INDEX ${TABLE}_${COL_MANGA_ID}_index ON $TABLE($COL_MANGA_ID)"

    val sourceOrderUpdateQuery: String
        get() = "ALTER TABLE $TABLE ADD COLUMN source_order INTEGER DEFAULT 0"

    val bookmarkUpdateQuery: String
        get() = "ALTER TABLE $TABLE ADD COLUMN bookmark BOOLEAN DEFAULT FALSE"

}
