package ch.bailu.aat_gtk.app

import ch.bailu.aat_lib.app.AppConfig
import ch.bailu.aat_lib.map.AppDensity
import ch.bailu.aat_lib.resources.Res
import ch.bailu.foc.FocFactory
import ch.bailu.foc.FocFile

object GtkAppConfig : AppConfig() {

    object Foc : FocFactory {
        override fun toFoc(string: String?): ch.bailu.foc.Foc {
            return FocFile(string)
        }
    }

    val density = AppDensity()

    val icon = "/images/icon.svg"
    val title = Res.str().app_name()


    override fun getApplicationId(): String {
        return "ch.bailu.aat_gtk"
    }

    override fun getVersionName(): String {
        return "v0.1 alpha"
    }

    override fun getVersionCode(): Int {
        return 1
    }

    override fun isRelease(): Boolean {
        return false
    }
}