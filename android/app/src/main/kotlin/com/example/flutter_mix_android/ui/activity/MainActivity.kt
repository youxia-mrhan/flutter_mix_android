package com.example.flutter_mix_android.ui.activity

import com.example.flutter_mix_android.ui.flutterplugin.plugin.ComputeLayoutPlugin
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity: FlutterFragmentActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        // 注册为Flutter插件
        flutterEngine.plugins.add(ComputeLayoutPlugin(this))
    }

}
