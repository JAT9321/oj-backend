package com.zgt.gtoj.judge.codesandbox;

import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("\n代码沙箱请求信息：" + executeCodeRequest.toString() + "\n");
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("\n代码沙箱响应信息：" + executeCodeResponse.getMessage() + "\n");
        return executeCodeResponse;
    }
}
