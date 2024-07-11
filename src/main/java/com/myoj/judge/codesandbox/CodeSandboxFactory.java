package com.myoj.judge.codesandbox;

import com.myoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.myoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.myoj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码沙箱静态工厂
 */
public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type) {
        Map<String, CodeSandbox> map = new HashMap<>();
        map.put("example",new ExampleCodeSandbox());
        map.put("remote",new RemoteCodeSandbox());
        map.put("third",new ThirdPartyCodeSandbox());
        if(map.get(type) == null){
            return new ExampleCodeSandbox();
        }
        return map.get(type);
    }

}
