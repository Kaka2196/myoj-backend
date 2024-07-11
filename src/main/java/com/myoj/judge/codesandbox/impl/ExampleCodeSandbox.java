package com.myoj.judge.codesandbox.impl;

import com.myoj.judge.codesandbox.CodeSandbox;
import com.myoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.myoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.myoj.judge.codesandbox.model.JudgeInfo;
import com.myoj.model.enums.JudgeInfoMsgEnum;
import com.myoj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例沙箱
 */
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("Example...");
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        executeCodeResponse.setMessage(JudgeInfoMsgEnum.ACCEPTED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMsgEnum.ACCEPTED.getValue());
        judgeInfo.setTime(1000L);
        judgeInfo.setMemory(1000L);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
}
