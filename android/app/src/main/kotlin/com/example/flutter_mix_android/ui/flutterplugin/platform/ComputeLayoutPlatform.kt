package com.example.flutter_mix_android.ui.flutterplugin.platform;

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.example.flutter_mix_android.R
import com.example.flutter_mix_android.bean.CountBean
import com.example.flutter_mix_android.databinding.LayoutComputeBinding
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

/**
 * 封装成PlatformView
 */
class ComputeLayoutPlatform(
    context: Context,
    rootContext: Context,
    messenger: BinaryMessenger,
    viewId: Int,
    args: Any?,
) : FrameLayout(context), PlatformView, MethodChannel.MethodCallHandler {

    private lateinit var mChannel: MethodChannel
    private lateinit var bind: LayoutComputeBinding
    private lateinit var viewModel: CountBean

    companion object {
        // Android原生View 在Flutter引擎上注册的唯一标识，在Flutter端使用时必须一样
        private const val ANDROID_SEND_FLUTTER_DATA_NOTICE: String = "androidSendFlutterDataNotice" // Android端 向 Flutter端 发送数据
        private const val ANDROID_GET_FLUTTER_DATA_NOTICE: String = "androidGetFlutterDataNotice" // Android端 获取 Flutter端 数据
        private const val FLUTTER_SEND_ANDROID_DATA_NOTICE: String = "flutterSendAndroidDataNotice" // Flutter端 向 Android端 发送数据
        private const val FLUTTER_GET_ANDROID_DATA_NOTICE: String = "flutterGetAndroidDataNotice" // Flutter端 获取 Android端 数据
    }

    init {
        initChannel(messenger, viewId)
        initView()
        initData(rootContext, args)
    }

    /**
     * 初始化消息通道
     */
    private fun initChannel(messenger: BinaryMessenger, viewId: Int) {
        // 创建 Android端和Flutter端的，相互通信的通道
        // 通道名称，两端必须一致
        mChannel = MethodChannel(messenger, "flutter.mix.android/compute/$viewId")

        // 监听来自 Flutter端 的消息通道
        // Flutter端调用了函数，这个handler函数就会被触发
        mChannel.setMethodCallHandler(this)
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_compute, this, true)
        bind = LayoutComputeBinding.bind(getChildAt(0))
        bind.add.setOnClickListener {
            val count: Int = viewModel.curNum.value ?: 0
            viewModel.curNum.value = count + 1
        }

        bind.androidSendFlutterData.setOnClickListener {
            androidSendFlutterData()
        }

        bind.androidGetFlutterData.setOnClickListener {
            androidGetFlutterData()
        }
    }

    /**
     * Android端 向 Flutter端 发送数据，PUT 操作
     */
    private fun androidSendFlutterData() {
        val map: MutableMap<String, Int> = mutableMapOf<String, Int>()
        map["androidNum"] = viewModel.curNum.value ?: 0

        mChannel.invokeMethod(
            ANDROID_SEND_FLUTTER_DATA_NOTICE,
            map,
            object : MethodChannel.Result {
                override fun success(result: Any?) {
                    Log.d("TAG", "success：$result")
                    updateFlutterNum((result as? Int) ?: 0)
                }

                override fun error(
                    errorCode: String,
                    errorMessage: String?,
                    errorDetails: Any?
                ) {
                    Log.d(
                        "TAG",
                        "errorCode：$errorCode --- errorMessage：$errorMessage --- errorDetails：$errorDetails"
                    )
                }

                /**
                 * Flutter端 未实现 Android端 定义的接口方法
                 */
                override fun notImplemented() {
                    Log.d("TAG", "notImplemented")
                }
            })
    }

    /**
     * Android端 获取 Flutter端 数据，GET 操作
     */
    private fun androidGetFlutterData() {
        // 说一个坑，不传参数可以写null，
        // 但不能这样写，目前它没有这个重载方法，invokeMethod第二个参数是Object类型，所以编译器不会提示错误
        // mChannel.invokeMethod(ANDROID_GET_FLUTTER_DATA_NOTICE, object : MethodChannel.Result {

        // public void invokeMethod(@NonNull String method, @Nullable Object arguments)

        mChannel.invokeMethod(
            ANDROID_GET_FLUTTER_DATA_NOTICE,
            null,
            object : MethodChannel.Result {
                override fun success(result: Any?) {
                    Log.d("TAG", "success：$result")
                    updateGetFlutterNum((result as? Int) ?: 0)
                }

                override fun error(
                    errorCode: String,
                    errorMessage: String?,
                    errorDetails: Any?
                ) {
                    Log.d(
                        "TAG",
                        "errorCode：$errorCode --- errorMessage：$errorMessage --- errorDetails：$errorDetails"
                    )
                }

                /**
                 * Flutter端 未实现 Android端 定义的接口方法
                 */
                override fun notImplemented() {
                    Log.d("TAG", "notImplemented")
                }
            })
    }

    /**
     * 初始化数据
     */
    private fun initData(rootContext: Context, args: Any?) {
        val owner = rootContext as FlutterFragmentActivity
        viewModel = ViewModelProvider(owner)[CountBean::class.java]
        bind.countBean = viewModel
        bind.lifecycleOwner = owner

        // 获取初始化时 Flutter端 向 Android 传递的参数
        val map: Map<String, Int> = args as Map<String, Int>
        viewModel.getFlutterNum.value = map["flutterNum"]
    }

    /**
     * 监听来自 Flutter端 的消息通道
     *
     * call： Android端 接收到 Flutter端 发来的 数据对象
     * result：Android端 给 Flutter端 执行回调的接口对象
     */
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        // 获取调用函数的名称
        val methodName: String = call.method
        when (methodName) {
            FLUTTER_SEND_ANDROID_DATA_NOTICE -> {
                // 回调结果对象
                // 获取Flutter端传过来的数据
                val flutterCount: Int? = call.argument<Int>("flutterNum")
                updateFlutterNum(flutterCount ?: 0)
                result.success("success")

                // 回调状态接口对象，里面有三个回调方法
                // result.success(result: Any?)
                // result.error(errorCode: String, errorMessage: String?, errorDetails: Any?)
                // result.notImplemented()
            }

            FLUTTER_GET_ANDROID_DATA_NOTICE -> {
                result.success(viewModel.curNum.value)
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    fun updateFlutterNum(flutterCount: Int) {
        viewModel.flutterNum.value = flutterCount
    }

    fun updateGetFlutterNum(flutterCount: Int) {
        viewModel.getFlutterNum.value = flutterCount
    }

    override fun getView(): View? {
        return this
    }

    override fun dispose() {
        // 解除绑定
        mChannel.setMethodCallHandler(null)
    }

}
