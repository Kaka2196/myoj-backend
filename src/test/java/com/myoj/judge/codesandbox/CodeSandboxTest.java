package com.myoj.judge.codesandbox;

import com.github.mustachejava.Code;
import com.myoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.myoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.myoj.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.myoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.myoj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodeSandboxTest {
    @Value("${codesandbox.type}")
    private String type;
    @Test
    void f(){
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = "public class Main {\n" +
                "    public static void main(String[] args){\n" +
                "\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.print(\"result结果: \" + (a + b));\n" +
                "\n" +
                "    }\n" +
                "}\n";
        List<String> list = new ArrayList<>();
        list.add("1 2");
        list.add("77 88");
        list.add("8 999");

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(QuestionSubmitLanguageEnum.JAVA.getValue())
                .inputList(list)
                .build();
        codeSandbox.executeCode(executeCodeRequest);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str = null;
        if(scanner.hasNextInt()){
            str = scanner.next();
        }
        System.out.println(str);
    }

}