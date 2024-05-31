package com.zgt.gtoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgt.gtoj.annotation.AuthCheck;
import com.zgt.gtoj.common.BaseResponse;
import com.zgt.gtoj.common.ErrorCode;
import com.zgt.gtoj.common.ResultUtils;
import com.zgt.gtoj.constant.UserConstant;
import com.zgt.gtoj.exception.BusinessException;
import com.zgt.gtoj.model.dto.question.QuestionQueryRequest;
import com.zgt.gtoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.zgt.gtoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.zgt.gtoj.model.entity.Question;
import com.zgt.gtoj.model.entity.QuestionSubmit;
import com.zgt.gtoj.model.entity.User;
import com.zgt.gtoj.model.vo.QuestionSubmitVO;
import com.zgt.gtoj.service.QuestionSubmitService;
import com.zgt.gtoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param questionsubmitAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionsubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionsubmitAddRequest == null || questionsubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才可以做题
        final User loginUser = userService.getLoginUser(request);
        long questionId = questionsubmitAddRequest.getQuestionId();
        Long result = questionSubmitService.doQuestionSubmit(questionsubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/list/page")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

}
