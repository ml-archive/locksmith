package dk.nodes.locksmith.example

import android.app.Application
import dk.nodes.locksmith.core.Locksmith
import dk.nodes.locksmith.core.models.LocksmithConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configure our Locksmith instance
        val locksmithConfiguration = LocksmithConfiguration()
        locksmithConfiguration.keyValidityDuration = 120

        // Start our locksmith instance
        Locksmith.init(this, locksmithConfiguration)
    }
}