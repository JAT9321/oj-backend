package com.zgt.gtoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.zgt.gtoj.judge.codesandbox.model.JudgeInfo;
import com.zgt.gtoj.model.dto.question.JudgeCase;
import com.zgt.gtoj.model.dto.question.JudgeConfig;
import com.zgt.gtoj.model.entity.Question;
import com.zgt.gtoj.model.enums.JudgeInfoMessageEnum;

import java.util.List;

public class DefaultJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {

        // 这个judgeInfo是沙盒执行代码真正消耗的内存和时间
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeResponse = new JudgeInfo();
        judgeResponse.setMemory(memory);
        judgeResponse.setTime(time);

        // 判断内存和时间限制是否满足
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long maxMemory = judgeConfig.getMemoryLimit();
        Long maxTime = judgeConfig.getTimeLimit();
        if (memory > maxMemory) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeResponse;
        }
        if (time > maxTime) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeResponse;
        }


        //先判断输出结果的数量和输入用例的个数是否相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeResponse;
        }

        // 判断每个输入用例对应的真正输出用例和沙盒执行用户代码得到的输出是否一致
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeResponse;
            }
        }

        judgeResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeResponse;

    }
}
