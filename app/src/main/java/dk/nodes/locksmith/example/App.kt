package dk.nodes.locksmith.example

import android.app.Application
import android.os.Build
import dk.nodes.locksmith.core.Locksmith

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Locksmith.Builder(this)
                    .setKeyValidityDuration(120)
                    .setUseFingerprint(true)
                    .build()
        } else {
            Locksmith.Builder(this)
                    .build()
                    .init()
        }
    }
}