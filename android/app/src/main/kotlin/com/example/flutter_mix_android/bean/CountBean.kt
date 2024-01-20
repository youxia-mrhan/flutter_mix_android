package com.example.flutter_mix_android.bean

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountBean : ViewModel() {

    var curNum: MutableLiveData<Int> = MutableLiveData<Int>() // Android端点击次数

    var flutterNum: MutableLiveData<Int> = MutableLiveData<Int>() // Flutter端点击次数（接收到的）

    var getFlutterNum: MutableLiveData<Int> = MutableLiveData<Int>() // Flutter端点击次数（主动获取的）

}