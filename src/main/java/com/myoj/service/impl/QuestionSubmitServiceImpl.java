package com.myoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myoj.common.ErrorCode;
import com.myoj.exception.BusinessException;
import com.myoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.myoj.model.entity.Question;
import com.myoj.model.entity.QuestionSubmit;
import com.myoj.model.entity.User;
import com.myoj.model.enums.QuestionSubmitLanguageEnum;
import com.myoj.model.enums.QuestionSubmitStatusEnum;
import com.myoj.service.QuestionService;
import com.myoj.service.QuestionSubmitService;
import com.myoj.mapper.QuestionSubmitMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Administrator
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-05-16 19:02:07
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{
    @Resource
    private QuestionService questionService;

    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //判断语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if(enumByValue==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编程语言错误");
        }
        // 判断实体是否存在，根据类别获取实体
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        //每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitAddRequest,questionSubmit);
        questionSubmit.setUserId(userId);
        //todo 设置初始状态
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据插入失败");
        }
        return questionSubmit.getId();
    }
}




