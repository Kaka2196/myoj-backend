package com.myoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.myoj.model.dto.question.JudgeCase;
import com.myoj.model.dto.question.JudgeConfig;
import com.myoj.judge.codesandbox.model.JudgeInfo;
import com.myoj.model.entity.Question;
import com.myoj.model.enums.JudgeInfoMsgEnum;

import java.util.List;

public class JavaJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        Long time = judgeInfo.getTime();
        Long memory = judgeInfo.getMemory();

        JudgeInfo judgeInfoRes = new JudgeInfo();
        judgeInfoRes.setTime(time);
        judgeInfoRes.setMemory(memory);

        JudgeInfoMsgEnum judgeInfoMsgEnum = JudgeInfoMsgEnum.ACCEPTED;
        if(outputList.size() != inputList.size()){
            judgeInfoMsgEnum = JudgeInfoMsgEnum.WRONG_ANSWER;
            judgeInfoRes.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoRes;
        }
        // 判断每项输出
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if(!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoMsgEnum = JudgeInfoMsgEnum.WRONG_ANSWER;
                judgeInfoRes.setMessage(judgeInfoMsgEnum.getValue());
                return judgeInfoRes;
            }
        }
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();

        // Java 程序额外执行时间
        long JavaTimeCost = 1000L;
        if((time - JavaTimeCost) > timeLimit){
            judgeInfoMsgEnum = JudgeInfoMsgEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoRes.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoRes;
        }
        if(memory > memoryLimit){
            judgeInfoMsgEnum = JudgeInfoMsgEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoRes.setMessage(judgeInfoMsgEnum.getValue());
            return judgeInfoRes;
        }

        judgeInfoRes.setMessage(judgeInfoMsgEnum.getValue());
        return judgeInfoRes;
    }
}
