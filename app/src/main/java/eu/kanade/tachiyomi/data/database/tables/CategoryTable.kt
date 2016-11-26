package eu.kanade.tachiyomi.data.database.tables

object CategoryTable {

    const val TABLE = "categories"

    const val COL_ID = "cat_id"

    const val COL_NAME = "cat_name"

    const val COL_ORDER = "cat_order"

    const val COL_FLAGS = "cat_flags"

    val createTableQuery: String
        get() = """CREATE TABLE $TABLE(
            $COL_ID INTEGER NOT NULL PRIMARY KEY,
            $COL_NAME TEXT NOT NULL,
            $COL_ORDER INTEGER NOT NULL,
            $COL_FLAGS INTEGER NOT NULL,
            UNIQUE ($COL_NAME COLLATE NOCASE) ON CONFLICT IGNORE
            )"""

}
