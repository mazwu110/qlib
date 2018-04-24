package com.qlib.qhttputils;

import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.qjsonlib.JSON;
import com.qlib.baseBean.BaseBean;
import com.qlib.fileutils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by mzw on 2018/3/24.
 */

public class QHttpUtils {
    public static void get(String url, Map<String, Object> params, final Class<?> clazz, final int what, final onHttpListener listener) {
        final String myTag = "myTag" + Utils.getCurrentTime();
        OkGo.get(buildGetParamsRequest(url, params))
                .tag(myTag)
                .execute(new JsonCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Object obj = httpHandleResult(s, clazz);
                        if ((obj == null || ((BaseBean) obj).getCode() == 0) && ((BaseBean) obj).getSuccess() != true) {
                            listener.httpFailure(what, obj, response);
                        } else {
                            listener.httpSuccess(what, obj, response);
                        }

                        OkGo.getInstance().cancelTag(myTag);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.httpFailure(what, "网络请求失败或服务器异常", response);
                        OkGo.getInstance().cancelTag(myTag);
                    }
                });
    }

    public static void post(String url, Map<String, Object> params, final Class<?> clazz, final int what, final onHttpListener listener) {
        final String myTag = "myTag" + Utils.getCurrentTime();
        OkGo.post(url)
                .tag(myTag)
                .upJson(buildPostParamsRequest(params))
                .execute(new JsonCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Object obj = httpHandleResult(s, clazz);
                        if ((obj == null || ((BaseBean) obj).getCode() == 0) && ((BaseBean) obj).getSuccess() != true) {
                            listener.httpFailure(what, obj, response);
                        } else {
                            listener.httpSuccess(what, obj, response);
                        }

                        OkGo.getInstance().cancelTag(myTag);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.httpFailure(what, "网络请求失败或服务器异常", response);
                        OkGo.getInstance().cancelTag(myTag);
                    }
                });
    }

    // 上传文件
    public static void uploadFiles(String url, Map<String, Object> params, List<File> files, final Class<?> clazz, final int what, final onHttpListener listener) {
        final String myTag = "myTag" + Utils.getCurrentTime();
        OkGo.post(url)
                .tag(myTag)
                .upJson(buildPostParamsRequest(params))
                .addFileParams("", files)
                .execute(new JsonCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Object obj = httpHandleResult(s, clazz);
                        if ((obj == null || ((BaseBean) obj).getCode() == 0) && ((BaseBean) obj).getSuccess() != true) {
                            listener.httpFailure(what, obj, response);
                        } else {
                            listener.httpSuccess(what, obj, response);
                        }

                        OkGo.getInstance().cancelTag(myTag);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        listener.httpFailure(what, "网络请求失败或服务器异常", response);
                        OkGo.getInstance().cancelTag(myTag);
                    }
                });
    }

    private static String buildGetParamsRequest(String url, Map<String, Object> params) {
        StringBuilder encodedParams = new StringBuilder();
        if (params != null)
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                encodedParams.append(entry.getKey());
                encodedParams.append('=');
                encodedParams.append(entry.getValue() == null ? "" : entry.getValue());
                encodedParams.append('&');
            }

        if (!TextUtils.isEmpty(encodedParams))
            url += "?" + encodedParams;

        return url;
    }

    private static JSONObject buildPostParamsRequest(Map<String, Object> params) {
        JSONObject obj = new JSONObject();
        StringBuilder encodedParams = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                try {
                    obj.put(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return obj;
    }

    public static Object httpHandleResult(String result, Class<?> clazz) {
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        try {
            return (BaseBean) JSON.parseObject(result, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface onHttpListener {
        public void httpSuccess(int what, Object result, Response response);

        public void httpFailure(int what, Object result, Response response);
    }

    //    //data 是多层嵌套的时候用, 此时需要去修改BaseBean 中data 对应的JSON字段名，要保证和后台返回的一致
//    //比如'data':{'list':[{},{}],'pageIndex':1, 'pageSize':10}
//    public static Object httpMultiHandleResult(String result, Class<?> clazz) {
//        if (TextUtils.isEmpty(result)) {
//            return null;
//        }
//        try {
//            BaseMultiBean bean = JSON.parseObject(result, BaseMultiBean.class);
//            BaseBean resultBean = null;
//            if (clazz == null) {
//                resultBean = new BaseBean();
//            }else if (bean.getData() == null
//                    || bean.getData().length() == 0) {
//                resultBean = (BaseBean) clazz.newInstance();
//            } else {
//                resultBean = (BaseBean) JSON.parseObject(bean.getData(), clazz);
//            }
//            resultBean.setCode(bean.getCode());
//            resultBean.setMessage(bean.getMessage());
//            return resultBean;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
