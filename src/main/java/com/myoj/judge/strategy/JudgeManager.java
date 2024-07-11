package com.myoj.judge.strategy;

import com.myoj.judge.codesandbox.model.JudgeInfo;
import com.myoj.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 判题管理
 */
@Service
public class JudgeManager {

     public JudgeInfo doJudge(JudgeContext judgeContext){
        Map<String, JudgeStrategy> map = new HashMap<>();
        map.put(QuestionSubmitLanguageEnum.CPP.getValue(),new DefaultJudgeStrategy());
        map.put(QuestionSubmitLanguageEnum.JAVA.getValue(),new JavaJudgeStrategy());
        String language = judgeContext.getQuestionSubmit().getLanguage();
        JudgeStrategy judgeStrategy = map.get(language);
        if(judgeStrategy == null){
            return new DefaultJudgeStrategy().doJudge(judgeContext);
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
