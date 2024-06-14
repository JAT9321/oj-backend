package com.zgt.gtoj.judge;

import cn.hutool.json.JSONUtil;
import com.zgt.gtoj.common.ErrorCode;
import com.zgt.gtoj.exception.BusinessException;
import com.zgt.gtoj.judge.codesandbox.CodeSandbox;
import com.zgt.gtoj.judge.codesandbox.CodeSandboxFactory;
import com.zgt.gtoj.judge.codesandbox.CodeSandboxProxy;
import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.zgt.gtoj.judge.codesandbox.model.JudgeInfo;
import com.zgt.gtoj.judge.strategy.JudgeContext;
import com.zgt.gtoj.model.dto.question.JudgeCase;
import com.zgt.gtoj.model.entity.Question;
import com.zgt.gtoj.model.entity.QuestionSubmit;
import com.zgt.gtoj.model.enums.QuestionSubmitStatusEnum;
import com.zgt.gtoj.service.QuestionService;
import com.zgt.gtoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.zgt.gtoj.model.enums.JudgeInfoMessageEnum.ACCEPTED;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    // 策略模型，选择不同语言的限定条件，例如，运行时间、内存
    @Resource
    private JudgeManager judgeManager;

    // 配置文件选择代码沙盒
    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 根据id获得提交的记录信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
        }

        // 根据题号，获得题目的信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 重复提交，判断当前的questionSubmit提交状态，如果不是等待中，就不用再执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已处于判题状态");
        }

        // 更改当前的提交，为正在判题（”判题中“），避免重复判题
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新异常");
        }

        // 调用沙盒，执行代码，获得结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获得当前题目的输入用例
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        // 只要输入用例，对应的输出用例不用传递给代码沙盒
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 构造沙盒需要的输入
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .language(language)
                .code(code)
                .inputList(inputList)
                .build();
        // 沙盒执行代码
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 获得执行完输入用例的结果
        List<String> executeCodeResponseOutputList = executeCodeResponse.getOutputList();
        // 由沙盒执行的结果和输出用例进行对比，以及内存之类的限制，给定这次提交的判决信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(executeCodeResponseOutputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        // 使用策略模式调用沙盒执行结果的判断，例如结果正确与否以及内存时间的约束是否满足当前语言的要求
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "提交题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        // 更新当前题目的提交数目，以及通过数目
        Question updateQuestion = new Question();
        updateQuestion.setId(questionId);
        updateQuestion.setAcceptedNum(ACCEPTED.getText().equals(judgeInfo.getMessage()) ? question.getAcceptedNum() + 1 : question.getAcceptedNum());
        updateQuestion.setSubmitNum(question.getSubmitNum() + 1);
        update = questionService.updateById(updateQuestion);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交数目更新错误");
        }

        return questionSubmitResult;
    }
}
