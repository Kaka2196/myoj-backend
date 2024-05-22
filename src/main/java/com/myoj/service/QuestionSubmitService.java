package com.myoj.service;

import com.myoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.myoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myoj.model.entity.User;

/**
* @author Administrator
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-05-16 19:02:07
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 提交问题
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);
}
