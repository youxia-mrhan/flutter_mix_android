import 'package:flutter/cupertino.dart';

class CountBean {

  ValueNotifier<int> curNum = ValueNotifier<int>(10); // Flutter端点击次数

  ValueNotifier<int> androidNum = ValueNotifier<int>(0); // Android端点击次数（接收到的）

  ValueNotifier<int> getAndroidNum = ValueNotifier<int>(0); // Android端点击次数（主动获取的）

}
