package com.example.flutter_mix_android.ui.flutterplugin.plugin;

import android.content.Context
import com.example.flutter_mix_android.ui.flutterplugin.factory.ComputeLayoutPlatformFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin

/**
 * 将AndroidView 注册为 Flutter插件
 *
 * rootContext：这个context，我是用来作ViewModel观察的，setLifecycleOwner
 */
class ComputeLayoutPlugin(private val rootContext: Context) : FlutterPlugin {

    companion object {
        // Android原生View 在Flutter引擎上注册的唯一标识，在Flutter端使用时必须一样
        private const val viewType: String = "com.example.flutter_mix_android.ui.flutterplugin.platform/ComputeLayoutPlatform"
    }

    /**
     * 连接到flutter引擎时调用
     */
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        // 将Android原生View 在Flutter引擎上注册
        binding.platformViewRegistry.registerViewFactory(
            viewType,
            ComputeLayoutPlatformFactory(rootContext, binding.binaryMessenger)
        )
    }

    /**
     * 与flutter引擎分离时调用
     */
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {}

}
