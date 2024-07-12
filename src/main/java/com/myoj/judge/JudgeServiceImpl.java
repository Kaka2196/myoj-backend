package com.myoj.judge;

import cn.hutool.json.JSONUtil;
import com.myoj.common.ErrorCode;
import com.myoj.exception.BusinessException;
import com.myoj.judge.codesandbox.CodeSandbox;
import com.myoj.judge.codesandbox.CodeSandboxFactory;
import com.myoj.judge.codesandbox.CodeSandboxProxy;
import com.myoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.myoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.myoj.judge.strategy.*;
import com.myoj.model.dto.question.JudgeCase;
import com.myoj.judge.codesandbox.model.JudgeInfo;
import com.myoj.model.entity.Question;
import com.myoj.model.entity.QuestionSubmit;
import com.myoj.model.enums.JudgeInfoMsgEnum;
import com.myoj.model.enums.QuestionSubmitStatusEnum;
import com.myoj.service.QuestionService;
import com.myoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Value("${codesandbox.type}")
    private String type;
    @Resource
    private QuestionService questionService;
    @Resource
    private JudgeManager judgeManager;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Question question = questionService.getById(questionSubmit.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 若为非等待状态
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "正在判题中...");
        }

        // 设置为判题状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        questionSubmitUpdate.setId(questionSubmitId);
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 调用代码沙箱
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest req = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(inputList)
                .build();
        ExecuteCodeResponse res = codeSandbox.executeCode(req);
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        Integer status = res.getStatus();
        if (QuestionSubmitStatusEnum.SUCCEED.getValue().equals(status)){ // 沙箱编译错误
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(JudgeInfoMsgEnum.COMPILE_ERROR.getValue());
            judgeInfo.setTime(0L);
            judgeInfo.setMemory(0L);
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionSubmitService.updateById(questionSubmitUpdate);  //修改数据库判题结果
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            return questionSubmitService.getById(questionSubmitId);
        } else if(QuestionSubmitStatusEnum.FAILED.getValue().equals(status)){ // 沙箱调用错误
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(JudgeInfoMsgEnum.SYSTEM_ERROR.getValue());
            judgeInfo.setTime(0L);
            judgeInfo.setMemory(0L);
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionSubmitService.updateById(questionSubmitUpdate);  //修改数据库判题结果
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            return questionSubmitService.getById(questionSubmitId);
        }
        List<String> outputList = res.getOutputList();
        JudgeInfo judgeInfo = res.getJudgeInfo();

        // 根据沙箱返回结果设置判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        // 比对答案
        JudgeInfo doneJudge = judgeManager.doJudge(judgeContext);
        //修改数据库判题结果

        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(doneJudge));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionSubmitService.getById(questionSubmitId);
    }
}
