import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_mix_android/bean/count_bean.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  final CountBean countBean = CountBean();

  late MethodChannel channel;

  // Android原生View 在Flutter引擎上注册的唯一标识，在Flutter端使用时必须一样
  final String viewType = 'com.example.flutter_mix_android.ui.flutterplugin.platform/ComputeLayoutPlatform';
  static const String FLUTTER_SEND_ANDROID_DATA_NOTICE = 'flutterSendAndroidDataNotice'; // Flutter端 向 Android端 发送数据
  static const String FLUTTER_GET_ANDROID_DATA_NOTICE = 'flutterGetAndroidDataNotice'; // Flutter端 获取 Android端 数据
  static const String ANDROID_SEND_FLUTTER_DATA_NOTICE = 'androidSendFlutterDataNotice'; // Android端 向 Flutter端 发送数据
  static const String ANDROID_GET_FLUTTER_DATA_NOTICE = 'androidGetFlutterDataNotice'; // Android端 获取 Flutter端 数据

  /// 初始化消息通道
  initChannel(int viewId) {
    channel = MethodChannel('flutter.mix.android/compute/$viewId'); // 创建 Flutter端和Android端的，相互通信的通道

    // 监听来自 Android端 的消息通道
    // Android端调用了函数，这个handler函数就会被触发
    channel.setMethodCallHandler(handler);
  }

  /// 监听来自 Android端 的消息通道
  /// Android端调用了函数，这个handler函数就会被触发
  Future<dynamic> handler(MethodCall call) async {
    // 获取调用函数的名称
    final String methodName = call.method;
    switch (methodName) {
      case ANDROID_SEND_FLUTTER_DATA_NOTICE:
        {
          int androidCount = call.arguments['androidNum'];
          countBean.androidNum.value = androidCount;
          return '$ANDROID_SEND_FLUTTER_DATA_NOTICE ---> success';
        }
      case ANDROID_GET_FLUTTER_DATA_NOTICE:
        {
          return countBean.curNum.value ?? 0;
        }
      default:
        {
          return PlatformException(
              code: '-1', message: '未找到Flutter端具体实现函数', details: '具体描述');
        }
    }
  }

  /// Flutter端 向 Android端 发送数据，PUT 操作
  flutterSendAndroidData() {
    Map<String, int> map = {'flutterNum': countBean.curNum.value};
    channel.invokeMethod(FLUTTER_SEND_ANDROID_DATA_NOTICE, map).then((value) {
      debugPrint('$FLUTTER_SEND_ANDROID_DATA_NOTICE --- Result：$value');
    }).catchError((e) {
      if (e is MissingPluginException) {
        debugPrint('$FLUTTER_SEND_ANDROID_DATA_NOTICE --- Error：notImplemented --- 未找到Android端具体实现函数');
      } else {
        debugPrint('$FLUTTER_SEND_ANDROID_DATA_NOTICE --- Error：$e');
      }
    });
  }

  ///  Flutter端 获取 Android端 数据，GET 操作
  flutterGetAndroidData() {
    channel.invokeMethod(FLUTTER_GET_ANDROID_DATA_NOTICE).then((value) {
      debugPrint('$FLUTTER_GET_ANDROID_DATA_NOTICE --- Result：$value');
      countBean.getAndroidNum.value = value ?? 0;
    }).catchError((e) {
      if (e is MissingPluginException) {
        debugPrint('$FLUTTER_GET_ANDROID_DATA_NOTICE --- Error：notImplemented --- 未找到Android端具体实现函数');
      } else {
        debugPrint('$FLUTTER_GET_ANDROID_DATA_NOTICE --- Error：$e');
      }
    });
  }

  /// 累计点击次数
  computeCount() {
    countBean.curNum.value += 1;
  }

  Widget computeWidget() {
    final ButtonStyle btnStyle = ElevatedButton.styleFrom(
        elevation: 0,
        padding: const EdgeInsets.symmetric(horizontal: 12),
        backgroundColor: Colors.white,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(35)));
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Flutter页面',
            style: TextStyle(
                color: Color(0xff0066ff),
                fontSize: 20,
                fontWeight: FontWeight.bold),
          ),
          Padding(
            padding: const EdgeInsets.only(top: 16, bottom: 8),
            child: Row(
              children: [
                ValueListenableBuilder<int>(
                    valueListenable: countBean.curNum,
                    builder: (context, count, _) {
                      return Text('点击次数：$count',
                          style: const TextStyle(fontSize: 16));
                    }),
                Padding(
                  padding: const EdgeInsets.only(left: 16, right: 8),
                  child: ElevatedButton(
                    style: btnStyle,
                    onPressed: computeCount,
                    child: const Text('+1'),
                  ),
                ),
                ElevatedButton(
                  style: btnStyle,
                  onPressed: flutterSendAndroidData,
                  child: const Text('发送给Android端'),
                )
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: Row(
              children: [
                ValueListenableBuilder(
                    valueListenable: countBean.getAndroidNum,
                    builder: (context, count, _) {
                      return Text('获取Android页面点击次数：$count',
                          style: const TextStyle(fontSize: 16));
                    }),
                Padding(
                  padding: const EdgeInsets.only(left: 16, right: 3),
                  child: ElevatedButton(
                    style: btnStyle,
                    onPressed: flutterGetAndroidData,
                    child: const Text('获取Android端数据'),
                  ),
                ),
              ],
            ),
          ),
          ValueListenableBuilder(
              valueListenable: countBean.androidNum,
              builder: (context, count, _) {
                return Text('接收Android端发送的点击次数：$count',
                    style: const TextStyle(fontSize: 16));
              }),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xffA4D3EE),
      body: SizedBox(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        child: SafeArea(
          top: true,
          child: Column(
            children: [
              Expanded(
                  flex: 1,
                  child: AndroidView(
                    viewType: viewType, // Android原生View 在Flutter引擎上注册的唯一标识，在Flutter端使用时必须一样
                    creationParams: {'flutterNum': countBean.curNum.value}, // Flutter端 初始化时 向Android端 传递的参数
                    creationParamsCodec: const StandardMessageCodec(), // 消息编解码器
                    onPlatformViewCreated: (viewId) {
                      initChannel(viewId);
                      // 使用 viewId 构建不同名称的 MethodChannel，
                      // 主要应用于 多个相同AndroidView一起使用时，避免消息冲突
                      // List<MethodChannel> mChannels = [];
                      // mChannels.add(MethodChannel('flutter.mix.android/compute/$viewId'));
                      // mChannels[0].invokeMethod(method)
                      // mChannels[0].setMethodCallHandler((call) => null)
                    },
                  )),
              Expanded(flex: 1, child: computeWidget()),
            ],
          ),
        ),
      ),
    );
  }

}
