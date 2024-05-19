package com.zgt.gtoj;

import com.zgt.gtoj.judge.codesandbox.CodeSandbox;
import com.zgt.gtoj.judge.codesandbox.CodeSandboxFactory;
import com.zgt.gtoj.judge.codesandbox.CodeSandboxProxy;
import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.zgt.gtoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Value("${codesandbox.type:example}")
    String type;


    @Test
    void contextLoads() {
    }


    @Test
    public void codeSandText() {
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code("hello wrold")
                .language("java")
                .inputList(Arrays.asList("1 2", "3 4"))
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
//        Assertions.assertNotNull(executeCodeResponse);
    }

}
