package com.zgt.gtoj.judge;

import com.zgt.gtoj.judge.codesandbox.model.JudgeInfo;
import com.zgt.gtoj.judge.strategy.DefaultJudgeStrategy;
import com.zgt.gtoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.zgt.gtoj.judge.strategy.JudgeContext;
import com.zgt.gtoj.judge.strategy.JudgeStrategy;
import com.zgt.gtoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     * 策略模式中，对策略的选择进行管理
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
