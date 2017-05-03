package eu.kanade.tachiyomi.ui.setting

import android.os.Handler
import android.support.v7.preference.PreferenceScreen
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.library.LibraryUpdateJob
import eu.kanade.tachiyomi.util.LocaleHelper
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class SettingsGeneralController : BaseSettingsController() {

    private val db: DatabaseHelper = Injekt.get()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
        titleRes = R.string.pref_category_general

        listPreference {
            key = keys.lang
            titleRes = R.string.pref_language
            entryValues = arrayOf("", "bg", "en", "es", "fr", "it", "pt", "ru", "vi")
            entries = entryValues.map { value ->
                val locale = LocaleHelper.getLocaleFromString(value.toString())
                locale?.getDisplayName(locale)?.capitalize() ?:
                        context.getString(R.string.system_default)
            }.toTypedArray()
            defaultValue = ""
            summary = "%s"

            onChange { newValue ->
                val activity = activity ?: return@onChange false
                val app = activity.application
                LocaleHelper.changeLocale(newValue.toString())
                LocaleHelper.updateConfiguration(app, app.resources.configuration)
                activity.recreate()
                true
            }
        }
        intListPreference {
            key = keys.theme
            titleRes = R.string.pref_theme
            entriesRes = arrayOf(R.string.light_theme, R.string.dark_theme)
            entryValues = arrayOf("1", "2")
            defaultValue = "1"
            summary = "%s"

            onChange {
                activity?.recreate()
                true
            }
        }
        intListPreference {
            key = keys.startScreen
            titleRes = R.string.pref_start_screen
            entriesRes = arrayOf(R.string.label_library, R.string.label_recent_manga,
                    R.string.label_recent_updates)
            entryValues = arrayOf("1", "2", "3")
            defaultValue = "1"
            summary = "%s"
        }
        intListPreference {
            key = keys.libraryUpdateInterval
            titleRes = R.string.pref_library_update_interval
            entriesRes = arrayOf(R.string.update_never, R.string.update_1hour,
                    R.string.update_2hour, R.string.update_3hour, R.string.update_6hour,
                    R.string.update_12hour, R.string.update_24hour, R.string.update_48hour)
            entryValues = arrayOf("0", "1", "2", "3", "6", "12", "24", "48")
            defaultValue = "0"
            summary = "%s"

            onChange { newValue ->
                // Always cancel the previous task, it seems that sometimes they are not updated.
                LibraryUpdateJob.cancelTask()

                val interval = (newValue as String).toInt()
                if (interval > 0) {
                    LibraryUpdateJob.setupTask(interval)
                }
                true
            }
        }
        multiSelectListPreference {
            key = keys.libraryUpdateRestriction
            titleRes = R.string.pref_library_update_restriction
            entriesRes = arrayOf(R.string.wifi, R.string.charging)
            entryValues = arrayOf("wifi", "ac")
            summaryRes = R.string.pref_library_update_restriction_summary

            preferences.libraryUpdateInterval().asObservable()
                    .subscribeUntilDestroy { isVisible = it > 0 }

            onChange {
                // Post to event looper to allow the preference to be updated.
                Handler().post { LibraryUpdateJob.setupTask() }
                true
            }
        }
        switchPreference {
            key = keys.updateOnlyNonCompleted
            titleRes = R.string.pref_update_only_non_completed
            defaultValue = false
        }

        val dbCategories = db.getCategories().executeAsBlocking()

        multiSelectListPreference {
            key = keys.libraryUpdateCategories
            titleRes = R.string.pref_library_update_categories
            entries = dbCategories.map { it.name }.toTypedArray()
            entryValues = dbCategories.map { it.id.toString() }.toTypedArray()

            preferences.libraryUpdateCategories().asObservable()
                    .subscribeUntilDestroy {
                        val selectedCategories = it
                                .mapNotNull { id -> dbCategories.find { it.id == id.toInt() } }
                                .sortedBy { it.order }

                        summary = if (selectedCategories.isEmpty())
                            context.getString(R.string.all)
                        else
                            selectedCategories.joinToString { it.name }
                    }
        }
        intListPreference {
            key = keys.defaultCategory
            titleRes = R.string.default_category

            val selectedCategory = dbCategories.find { it.id == preferences.defaultCategory() }
            entries = arrayOf(context.getString(R.string.default_category_summary)) +
                    dbCategories.map { it.name }.toTypedArray()
            entryValues = arrayOf("-1") + dbCategories.map { it.id.toString() }.toTypedArray()
            defaultValue = "-1"
            summary = selectedCategory?.name ?: context.getString(R.string.default_category_summary)

            onChange { newValue ->
                summary = dbCategories.find {
                    it.id == (newValue as String).toInt()
                }?.name ?: context.getString(R.string.default_category_summary)
                true
            }
        }
    }

}