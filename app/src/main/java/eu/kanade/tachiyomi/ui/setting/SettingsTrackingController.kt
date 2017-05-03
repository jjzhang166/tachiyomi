package eu.kanade.tachiyomi.ui.setting

import android.content.Intent
import android.support.customtabs.CustomTabsIntent
import android.support.v7.preference.PreferenceScreen
import android.view.View
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.track.TrackManager
import eu.kanade.tachiyomi.data.track.TrackService
import eu.kanade.tachiyomi.data.track.anilist.AnilistApi
import eu.kanade.tachiyomi.util.getResourceColor
import eu.kanade.tachiyomi.widget.preference.LoginPreference
import uy.kohesive.injekt.injectLazy

class SettingsTrackingController : BaseSettingsController() {

    private val trackManager: TrackManager by injectLazy()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
        titleRes = R.string.pref_category_tracking

        switchPreference {
            key = keys.autoUpdateTrack
            titleRes = R.string.pref_auto_update_manga_sync
            defaultValue = true
        }
        switchPreference {
            key = keys.askUpdateTrack
            titleRes = R.string.pref_ask_update_manga_sync
            defaultValue = false
        }.apply {
            dependency = keys.autoUpdateTrack // the preference needs to be attached.
        }
        preferenceCategory {
            titleRes = R.string.services

            trackPreference(trackManager.myAnimeList) {
                onClick {
                    // TODO
//                    val fragment = TrackLoginDialog.newInstance(it)
//                    fragment.setTargetFragment(this, SettingsTrackingFragment.SYNC_CHANGE_REQUEST)
//                    fragment.show(fragmentManager, null)
                }
            }
            trackPreference(trackManager.aniList) {
                onClick {
                    val tabsIntent = CustomTabsIntent.Builder()
                            .setToolbarColor(context.getResourceColor(R.attr.colorPrimary))
                            .build()
                    tabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    tabsIntent.launchUrl(activity, AnilistApi.authUrl())
                }
            }
            trackPreference(trackManager.kitsu) {
                onClick {
                    // TODO
                }
            }
        }
    }

    inline fun PreferenceScreen.trackPreference(
            service: TrackService,
            block: (@DSL LoginPreference).() -> Unit
    ): LoginPreference {
        return initThenAdd(LoginPreference(context).apply {
            key = keys.trackUsername(service.id)
            title = service.name
        }, block)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        // Manually refresh anilist holder
        updatePreference(trackManager.aniList.id)
    }

    private fun updatePreference(id: Int) {
        val pref = findPreference(keys.trackUsername(id)) as? LoginPreference
        pref?.notifyChanged()
    }

}