package eu.kanade.tachiyomi.ui.setting

import android.support.v7.preference.PreferenceScreen
import com.afollestad.materialdialogs.MaterialDialog
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.library.LibraryUpdateService
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.util.plusAssign
import eu.kanade.tachiyomi.util.toast
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import uy.kohesive.injekt.injectLazy
import java.util.concurrent.atomic.AtomicInteger

class SettingsAdvancedController : BaseSettingsController() {

    private val network: NetworkHelper by injectLazy()

    private val chapterCache: ChapterCache by injectLazy()

    private val db: DatabaseHelper by injectLazy()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
        titleRes = R.string.pref_category_advanced

        preference {
            titleRes = R.string.pref_clear_chapter_cache
            summary = context.getString(R.string.used_cache, chapterCache.readableSize)

            onClick { clearChapterCache() }
        }
        preference {
            titleRes = R.string.pref_clear_cookies

            onClick {
                network.cookies.removeAll()
                activity?.toast(R.string.cookies_cleared)
            }
        }
        preference {
            titleRes = R.string.pref_clear_database
            summaryRes = R.string.pref_clear_database_summary

            onClick { clearDatabase() }
        }
        preference {
            titleRes = R.string.pref_refresh_library_metadata
            summaryRes = R.string.pref_refresh_library_metadata_summary

            onClick { LibraryUpdateService.start(context, details = true) }
        }
    }

    private fun clearChapterCache() {
        val activity = activity ?: return

        val deletedFiles = AtomicInteger()

        val files = chapterCache.cacheDir.listFiles() ?: return

        val dialog = MaterialDialog.Builder(activity)
                .title(R.string.deleting)
                .progress(false, files.size, true)
                .cancelable(false)
                .show()

        untilDestroySubscriptions += Observable.defer { Observable.from(files) }
                .concatMap { file ->
                    if (chapterCache.removeFileFromCache(file.name)) {
                        deletedFiles.incrementAndGet()
                    }
                    Observable.just(file)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dialog.incrementProgress(1)
                }, {
                    dialog.dismiss()
                    activity.toast(R.string.cache_delete_error)
                }, {
                    dialog.dismiss()
                    activity.toast(resources?.getString(R.string.cache_deleted, deletedFiles.get()))
//                    clearCache.summary = getString(R.string.used_cache, chapterCache.readableSize)
                })
    }

    private fun clearDatabase() {
        val activity = activity ?: return

        MaterialDialog.Builder(activity)
                .content(R.string.clear_database_confirmation)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive { _, _ ->
                    (activity as SettingsActivity).parentFlags = SettingsActivity.FLAG_DATABASE_CLEARED
                    db.deleteMangasNotInLibrary().executeAsBlocking()
                    db.deleteHistoryNoLastRead().executeAsBlocking()
                    activity.toast(R.string.clear_database_completed)
                }
                .show()
    }
}
