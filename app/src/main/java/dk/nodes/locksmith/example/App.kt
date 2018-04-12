package dk.nodes.locksmith.example

import android.app.Application
import dk.nodes.locksmith.core.Locksmith

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Locksmith.init(this)
    }
}