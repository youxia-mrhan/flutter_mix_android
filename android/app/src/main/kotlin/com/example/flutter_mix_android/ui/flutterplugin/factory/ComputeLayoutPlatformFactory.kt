package com.example.flutter_mix_android.ui.flutterplugin.factory

import android.content.Context
import com.example.flutter_mix_android.ui.flutterplugin.platform.ComputeLayoutPlatform
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

/**
 * 通过PlatformView工厂，创建PlatformView
 */
class ComputeLayoutPlatformFactory(
    private val rootContext: Context,
    private val messenger: BinaryMessenger, // 二进制信使
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) { // 消息编解码器

    private lateinit var computeLayoutPlatform: ComputeLayoutPlatform

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        computeLayoutPlatform = ComputeLayoutPlatform(context, rootContext, messenger, viewId, args)
        return computeLayoutPlatform
    }

}