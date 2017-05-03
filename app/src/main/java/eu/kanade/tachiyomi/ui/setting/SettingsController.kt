package eu.kanade.tachiyomi.ui.setting

import android.support.v7.preference.PreferenceScreen
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import eu.kanade.tachiyomi.R

class SettingsController : BaseSettingsController() {
    override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
        titleRes = R.string.label_settings

        preference {
            iconRes = R.drawable.ic_tune_black_24dp
            iconTintAttr = R.attr.colorAccent
            titleRes = R.string.pref_category_general
            onClick { navigateTo(SettingsGeneralController()) }
        }
        preference {
            iconRes = R.drawable.ic_chrome_reader_mode_black_24dp
            iconTintAttr = R.attr.colorAccent
            titleRes = R.string.pref_category_reader
            onClick { navigateTo(SettingsReaderController()) }
        }
        preference {
            iconRes = R.drawable.ic_language_black_24dp
            iconTintAttr = R.attr.colorAccent
            titleRes = R.string.pref_category_sources
            onClick { navigateTo(SettingsSourcesController()) }
        }
        preference {
            iconRes = R.drawable.ic_sync_black_24dp
            iconTintAttr = R.attr.colorAccent
            titleRes = R.string.pref_category_tracking
            onClick { navigateTo(SettingsTrackingController()) }
        }
        preference {
            iconRes = R.drawable.ic_code_black_24dp
            iconTintAttr = R.attr.colorAccent
            titleRes = R.string.pref_category_advanced
            onClick { navigateTo(SettingsAdvancedController()) }
        }
        preference {
            iconRes = R.drawable.ic_help_black_24dp
            iconTintAttr = R.attr.colorAccent
            titleRes = R.string.pref_category_about
            onClick { navigateTo(SettingsAboutController()) }
        }
    }

    private fun navigateTo(controller: BaseSettingsController) {
        router.pushController(RouterTransaction.with(controller)
                .pushChangeHandler(FadeChangeHandler())
                .popChangeHandler(FadeChangeHandler()))
    }
}