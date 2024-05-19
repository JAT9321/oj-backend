package com.zgt.gtoj.judge.codesandbox;

import com.zgt.gtoj.judge.codesandbox.enums.CodeSandTypeEnum;
import com.zgt.gtoj.judge.codesandbox.model.impl.ExampleCodeSandbox;
import com.zgt.gtoj.judge.codesandbox.model.impl.RemoteCodeSandbox;
import com.zgt.gtoj.judge.codesandbox.model.impl.ThirdPartyCodeSandbox;

import java.util.Objects;

public class CodeSandboxFactory {

    public static CodeSandbox newInstance(String type) {
        CodeSandTypeEnum codeSandType = CodeSandTypeEnum.getEnumByValue(type);
        try {
            switch (Objects.requireNonNull(codeSandType)) {
                case EXAMPLE:
                    return new ExampleCodeSandbox();
                case REMOTE:
                    return new RemoteCodeSandbox();
                case THIRDPARTY:
                    return new ThirdPartyCodeSandbox();
                default:
                    return new ExampleCodeSandbox();
            }
        } catch (NullPointerException e) {
            return new ExampleCodeSandbox();
        }
    }
}
