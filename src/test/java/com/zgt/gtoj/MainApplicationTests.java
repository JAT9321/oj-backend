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
                .code("class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        int a = Integer.valueOf(args[0]);\n" +
                        "        int b = Integer.valueOf(args[1]);\n" +
                        "        System.out.println(\"结果：\" + (a + b));\n" +
                        "    }\n" +
                        "}")
                .language("java")
                .inputList(Arrays.asList("1 2", "3 4"))
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
//        Assertions.assertNotNull(executeCodeResponse);
    }

}
