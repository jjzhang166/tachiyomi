package eu.kanade.tachiyomi.data.preference

/**
 * This class stores the keys for the preferences in the application. Most of them are defined
 * in the file "keys.xml". By using this class we can define preferences in one place and get them
 * referenced here.
 */
class PreferenceKeys {

    val theme = "pref_theme_key"

    val rotation = "pref_rotation_type_key"

    val enableTransitions = "pref_enable_transitions_key"

    val showPageNumber = "pref_show_page_number_key"

    val fullscreen = "fullscreen"

    val keepScreenOn = "pref_keep_screen_on_key"

    val customBrightness = "pref_custom_brightness_key"

    val customBrightnessValue = "custom_brightness_value"

    val colorFilter = "pref_color_filter_key"

    val colorFilterValue = "color_filter_value"

    val defaultViewer = "pref_default_viewer_key"

    val imageScaleType = "pref_image_scale_type_key"

    val imageDecoder = "image_decoder"

    val zoomStart = "pref_zoom_start_key"

    val readerTheme = "pref_reader_theme_key"

    val cropBorders = "crop_borders"

    val readWithTapping = "reader_tap"

    val readWithVolumeKeys = "reader_volume_keys"

    val portraitColumns = "pref_library_columns_portrait_key"

    val landscapeColumns = "pref_library_columns_landscape_key"

    val updateOnlyNonCompleted = "pref_update_only_non_completed_key"

    val autoUpdateTrack = "pref_auto_update_manga_sync_key"

    val askUpdateTrack = "pref_ask_update_manga_sync_key"

    val lastUsedCatalogueSource = "last_catalogue_source"

    val lastUsedCategory = "last_used_category"

    val catalogueAsList = "pref_display_catalogue_as_list"

    val enabledLanguages = "source_languages"

    val backupDirectory = "backup_directory"

    val downloadsDirectory = "download_directory"

    val downloadThreads = "pref_download_slots_key"

    val downloadOnlyOverWifi = "pref_download_only_over_wifi_key"

    val numberOfBackups = "backup_slots"

    val backupInterval = "backup_interval"

    val removeAfterReadSlots = "remove_after_read_slots"

    val removeAfterMarkedAsRead = "pref_remove_after_marked_as_read_key"

    val libraryUpdateInterval = "pref_library_update_interval_key"

    val libraryUpdateRestriction = "library_update_restriction"

    val libraryUpdateCategories = "library_update_categories"

    val filterDownloaded = "pref_filter_downloaded_key"

    val filterUnread = "pref_filter_unread_key"

    val librarySortingMode = "library_sorting_mode"

    val automaticUpdates = "automatic_updates"

    val startScreen = "start_screen"

    val downloadNew = "download_new"

    val downloadNewCategories = "download_new_categories"

    val libraryAsList = "pref_display_library_as_list"

    val lang = "app_language"

    val defaultCategory = "default_category"

    fun sourceUsername(sourceId: Long) = "pref_source_username_$sourceId"

    fun sourcePassword(sourceId: Long) = "pref_source_password_$sourceId"

    fun trackUsername(syncId: Int) = "pref_mangasync_username_$syncId"

    fun trackPassword(syncId: Int) = "pref_mangasync_password_$syncId"

    fun trackToken(syncId: Int) = "track_token_$syncId"

}
