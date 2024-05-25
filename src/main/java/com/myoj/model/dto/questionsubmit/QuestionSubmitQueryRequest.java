package com.myoj.model.dto.questionsubmit;

import com.myoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * 查询请求
 *
 * @author yl
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 提交用户 id
     */
    private Long userId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String language;


    /**
     * 判题状态(0-待判题 1-判题中 2-成功 3-失败)
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}