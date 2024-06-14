package com.zgt.gtoj.judge.codesandbox.model.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zgt.gtoj.common.ErrorCode;
import com.zgt.gtoj.exception.BusinessException;
import com.zgt.gtoj.judge.codesandbox.CodeSandbox;
import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.zgt.gtoj.judge.codesandbox.model.JudgeInfo;
import org.dom4j.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 要实现的代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {

    // 远程调用的鉴权请求头中放入密钥
    public static final String AUTH_REQUEST_HEADER = "JIAO";
    public static final String AUTH_REQUEST_SECRET = "TIAN";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        // System.out.println("远程沙箱");
        // ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        // executeCodeResponse.setOutputList(executeCodeRequest.getInputList());
        // executeCodeResponse.setMessage("远程执行完成");
        // JudgeInfo judgeInfo = new JudgeInfo();
        // judgeInfo.setMemory(10000L);
        // judgeInfo.setTime(1000L);
        // judgeInfo.setMessage("ok");
        // executeCodeResponse.setJudgeInfo(judgeInfo);
        // return executeCodeResponse;

        System.out.println("远程代码沙箱");
        // 上线地址
        // String url = "http://121.37.154.99:2375/execCode";
        // 开发地址
        String url = "http://121.37.154.99:2376/execCode";
        // String url = "http://localhost:9999/execCode";
        String execReq = JSONUtil.toJsonStr(executeCodeRequest);
        String response = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(execReq)
                .execute()
                .body();

        if (StrUtil.isBlank(response)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message =" + response);
        }
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(response, ExecuteCodeResponse.class);
        return executeCodeResponse;
    }
}
