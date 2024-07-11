package com.myoj.judge.strategy;

import com.myoj.model.dto.question.JudgeCase;
import com.myoj.judge.codesandbox.model.JudgeInfo;
import com.myoj.model.entity.Question;
import com.myoj.model.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 判题上下文,定义在策略中传递的参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
