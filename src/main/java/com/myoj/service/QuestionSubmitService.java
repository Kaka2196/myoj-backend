package com.myoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myoj.model.dto.question.QuestionQueryRequest;
import com.myoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.myoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.myoj.model.entity.Question;
import com.myoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myoj.model.entity.User;
import com.myoj.model.vo.QuestionSubmitVO;
import com.myoj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQuerySubmitWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目提交封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
