package com.myoj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myoj.common.ErrorCode;
import com.myoj.constant.CommonConstant;
import com.myoj.exception.BusinessException;
import com.myoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.myoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.myoj.model.entity.Question;
import com.myoj.model.entity.QuestionSubmit;
import com.myoj.model.entity.User;
import com.myoj.model.enums.QuestionSubmitLanguageEnum;
import com.myoj.model.enums.QuestionSubmitStatusEnum;
import com.myoj.model.vo.QuestionSubmitVO;
import com.myoj.model.vo.QuestionVO;
import com.myoj.model.vo.UserVO;
import com.myoj.service.QuestionService;
import com.myoj.service.QuestionSubmitService;
import com.myoj.mapper.QuestionSubmitMapper;
import com.myoj.service.UserService;
import com.myoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-05-16 19:02:07
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;

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
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
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
        BeanUtils.copyProperties(questionSubmitAddRequest, questionSubmit);
        questionSubmit.setUserId(userId);
        //todo 设置初始状态
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQuerySubmitWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long userId = questionSubmitQueryRequest.getUserId();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        // 脱敏 仅限查看自己提交的题目(管理员除外)
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        Long userId = questionSubmit.getUserId();
        // todo 修改为所有人可查看
        if (!loginUser.getId().equals(userId) && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode("无权限查看");
        }
        // 1. 关联提交用户信息
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionSubmitVO.setUserVO(userVO);
        // 2.关联提交题目信息
        Long questionId = questionSubmitVO.getQuestionId();
        Question question = null;
        if (questionId != null && questionId > 0) {
            question = questionService.getById(questionId);
        }
        QuestionVO questionVO = questionService.getQuestionVO(question, null);
        questionSubmitVO.setQuestionVO(questionVO);
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize()
                , questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> this.getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());

        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




