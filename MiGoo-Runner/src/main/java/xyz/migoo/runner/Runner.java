package xyz.migoo.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import xyz.migoo.parser.CaseParser;
import xyz.migoo.report.Report;
import xyz.migoo.utils.EmailUtil;

import java.io.File;
import java.util.List;

/**
 * @author xiaomi
 * @date 2018/7/24 14:24
 */
public class Runner {

    private boolean isMain = false;

    public Runner(){
    }

    public Runner(boolean isMain){
        this.isMain = isMain;
    }

    public TestResult run(String testSet){
        CaseSuite caseSuite = this.initTestSuite(testSet);
        TestResult result = new TestRunner().run(caseSuite);
        Report.generateReport(result.report(), caseSuite.name(), true, isMain);
        return result;
    }

    public void execute(String caseSetOrPath){
        try {
            JSON.parse(caseSetOrPath);
            this.run(caseSetOrPath);
        }catch (Exception e){
            this.runByPath(caseSetOrPath);
        }
    }

    private void runByPath(String caseSetOrPath){
        File file = new File(caseSetOrPath);
        if (file.isDirectory()){
            for (String f: file.list()){
                runByPath(file.getPath() + File.separator + f);
            }
        }else {
            CaseSuite caseSuite = this.initTestSuite(caseSetOrPath);
            TestResult result = new TestRunner().run(caseSuite);
            Report.generateReport(result.report(), caseSuite.name(), false, isMain);
            EmailUtil.sendEmail(isMain);
        }
    }

    private CaseSuite initTestSuite(String path){
        List<JSONObject> caseSets = new CaseParser().loadCaseSets(path);
        return new CaseSuite(caseSets);
    }
}