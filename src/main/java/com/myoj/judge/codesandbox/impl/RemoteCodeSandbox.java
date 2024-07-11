package com.myoj.judge.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.myoj.common.ErrorCode;
import com.myoj.exception.BusinessException;
import com.myoj.judge.codesandbox.CodeSandbox;
import com.myoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.myoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.http.auth.AUTH;

/**
 * 远程沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {
    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "ukpkmkk";
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("调用远程代码沙箱中......");
        String url = "http://117.72.70.116:8082/exe";
        String jsonStr = JSONUtil.toJsonStr(executeCodeRequest);
        String res = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(jsonStr)
                .execute()
                .body();
        if(StrUtil.isBlank(res)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,res);
        }
        return JSONUtil.toBean(res, ExecuteCodeResponse.class);
    }
}
