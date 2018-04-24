package com.qlib.qhttputils;

import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by mzw on 2018/3/24.
 */
public abstract class JsonCallback extends AbsCallback<String> {

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现

    }

    @Override
    public String convertSuccess(Response response) throws Exception {
        return response.body().string();
    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
        e.printStackTrace();
    }
}
