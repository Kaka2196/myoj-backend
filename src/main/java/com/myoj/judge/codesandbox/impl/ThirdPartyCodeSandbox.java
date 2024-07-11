package com.myoj.judge.codesandbox.impl;

import com.myoj.judge.codesandbox.CodeSandbox;
import com.myoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.myoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方沙箱
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("ThirdParty...");

        return null;
    }
}
