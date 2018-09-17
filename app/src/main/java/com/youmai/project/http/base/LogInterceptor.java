package com.youmai.project.http.base;


import android.text.TextUtils;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.Login;
import com.youmai.project.http.HttpApi;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.ParameterUtils;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by lyn on 2017/4/13.
 */

public class LogInterceptor implements Interceptor {

    private final int ACCESS_TOKEN_TIME_OUT_CODE = 403;
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        LogUtils.e(String.format("request %s on %s%n%s", request.url(), request.method(), request.headers()));
        if (request.method().equals("POST")) {
            request = addParameter(request);
        }
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        String body = response.body().string();
        //如果ACCESS_TOKEN失效，自动重新获取一次
        final int code = getCode(body);
        if(code==ACCESS_TOKEN_TIME_OUT_CODE){
            Login login=getAccessToken();
            if(null!=login && login.isSussess()){
                MyApplication.spUtil.addString(SPUtil.AUTH_TOKEN,login.getData().getAuth_token());
                MyApplication.spUtil.addString(SPUtil.ACCESS_TOKEN,login.getData().getAccess_token());
                request = addParameter(request);
                response = chain.proceed(request);
                body = response.body().string();
            }
        }
        LogUtils.e(String.format("response %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, body));
        return response.newBuilder().body(ResponseBody.create(response.body().contentType(), body)).build();
    }


    /***
     * 添加公共参数
     */
    public Request addParameter(Request request) throws IOException {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        FormBody formBody;
        Map<String, String> requstMap = new HashMap<>();
        if (request.body().contentLength() > 0 && request.body() instanceof FormBody) {
            formBody = (FormBody) request.body();
            //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
            for (int i = 0; i < formBody.size(); i++) {
                  requstMap.put(formBody.name(i), formBody.value(i).replace("+","").replace(" ",""));
                  LogUtils.e(request.url() + "参数:" + formBody.name(i) + "=" + formBody.value(i));
            }
        }
        requstMap = ParameterUtils.getInstance().getParameter(requstMap);
        //添加公共参数
        for (String key : requstMap.keySet()) {
              bodyBuilder.addEncoded(key, requstMap.get(key));
        }
        formBody = bodyBuilder.build();
        request = request.newBuilder().removeHeader("User-Agent").addHeader("User-Agent",getUserAgent()).post(formBody).build();
        return request;
    }

    public int getCode(String json) {
        int code = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("code")) {
                code = jsonObject.getInt("code");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }


    /**
     * 获取AccessToken
     */
    public Login getAccessToken() throws IOException {
        Login login = null;
        String auth_token = MyApplication.spUtil.getString(SPUtil.AUTH_TOKEN);
        if (!TextUtils.isEmpty(auth_token)) {
            Map<String, String> map = new HashMap<>();
            map.put("auth_token", auth_token);
            map = ParameterUtils.getInstance().getParameter(map);
            login = Http.getRetrofitNoInterceptor().create(HttpApi.class).getAccessToken(map).execute().body();
        }
        return login;
    }


    /**
     * 获取 User Agent
     *
     * @return
     */
    private static String getUserAgent() {
        StringBuffer sb = new StringBuffer();
        sb.append("youmai/"+Util.getVersionName()+"(Android;"+android.os.Build.VERSION.RELEASE+")");
        return sb.toString();
    }
}
