/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2018 XiaoMiSum (mi_xiao@qq.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * 'Software'), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package components.xyz.migoo.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import core.xyz.migoo.IResult;
import core.xyz.migoo.ISuiteResult;
import core.xyz.migoo.ITestResult;
import core.xyz.migoo.Validator;
import core.xyz.migoo.report.IReport;
import core.xyz.migoo.utils.DateUtil;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author xiaomi
 * @date 2020/7/31 13:47
 */
public class Report implements IReport {

    private static final Logger logger = LoggerFactory.getLogger("migoo");

    public static void log(String msg) {
        logger.info(msg);
    }

    public static void log(String msg, Object... args) {
        logger.info(msg, args);
    }

    public static void log(String msg, Throwable t) {
        logger.info(msg, t);
    }

    private ExtentReports extent;

    private String outputDirectoryName;

    private IResult result;

    private void initReport(IResult result, String outputDirectoryName) {
        this.outputDirectoryName = outputDirectoryName;
        this.result = result;
        ExtentSparkReporter reporter = new ExtentSparkReporter(outputDirectoryName + "/index.html");
        reporter.config().setDocumentTitle(result.getTestName() + " Reports - Created by MiGoo");
        reporter.config().setReportName(result.getTestName() + " Reports"
                + "</span></a></li>\n" +
                "<li><a href='https://github.com/XiaoMiSum/MiGoo' target=\"_blank\"><span>" +
                "<img src=\"https://img.shields.io/badge/MiGoo-By Mi.xiao-yellow.svg?style=social&amp;logo=github\">");
        reporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        reporter.config().setTheme(Theme.DARK);
        reporter.config().enableOfflineMode(true);
        reporter.config().setTimelineEnabled(false);
        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.getReport().setStartTime(result.getStartTime());
        extent.getReport().setEndTime(result.getEndTime());
    }

    @Override
    public void generateReport(IResult result, String outputDirectoryName) {
        this.initReport(result, outputDirectoryName);
        for (IResult iSuiteResult : ((ISuiteResult) result).getTestResults()) {
            ISuiteResult suiteResult = (ISuiteResult) iSuiteResult;
            ExtentTest feature = extent.createTest(iSuiteResult.getTestName(),
                    String.format("用例总数：%s，成功：%s，失败：%s，错误：%s，跳过：%s", suiteResult.size(),
                            suiteResult.getSuccessCount(), suiteResult.getFailureCount(), suiteResult.getErrorCount(),
                            suiteResult.getSkipCount()));
            feature.getModel().setStartTime(iSuiteResult.getStartTime());
            feature.getModel().setEndTime(iSuiteResult.getEndTime());
            for (IResult testResult : suiteResult.getTestResults()) {
                ITestResult iTestResult = (ITestResult) testResult;
                String title = "" + testResult.getTestName() + String.format("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                "Started:&nbsp;&nbsp;%s&nbsp;&nbsp;-&nbsp;&nbsp;Ended:&nbsp;&nbsp;%s",
                        DateUtil.format(testResult.getStartTime()), DateUtil.format(testResult.getEndTime()));
                ExtentTest node = feature.createNode(title);
                if (iTestResult.getRequest() != null) {
                    node.info(iTestResult.getRequest().uriNotContainsParam());
                    StringBuilder sb = new StringBuilder();
                    if (iTestResult.getRequest().headers() != null && iTestResult.getRequest().headers().length > 0) {
                        sb.append("<br/>").append("Headers：").append(Arrays.toString(iTestResult.getRequest().headers()));
                    }
                    if (!iTestResult.getRequest().query().isEmpty()) {
                        sb.append("<br/>").append("Query：").append(iTestResult.getRequest().query());
                    }
                    if (!iTestResult.getRequest().body().isEmpty()) {
                        sb.append("<br/>").append("Body：").append(iTestResult.getRequest().body());
                    } else if (!iTestResult.getRequest().data().isEmpty()) {
                        sb.append("<br/>").append("Data：").append(iTestResult.getRequest().data());
                    }
                    if (sb.length() > 0) {
                        node.info("<span class=\"badge badge-primary\">REQUEST INFO</span>" + sb.toString());
                    }
                }
                if (iTestResult.getResponse() != null) {
                    StringBuilder sb = new StringBuilder();
                    if (iTestResult.getResponse().headers() != null && iTestResult.getResponse().headers().length > 0) {
                        sb.append("<br/>").append("Headers：").append(Arrays.toString(iTestResult.getResponse().headers()));
                    }
                    if (!iTestResult.getResponse().text().isEmpty()) {
                        sb.append("<br/>").append("Body：").append(iTestResult.getResponse().text());
                    }
                    if (sb.length() > 0) {
                        node.info("<span class=\"badge badge-primary\">RESPONSE INFO</span>" + sb.toString());
                    }
                    node.info(String.format("Duration：%s ms", iTestResult.getResponse().duration()));
                }
                if (!testResult.isError()) {
                    for (Validator validator : iTestResult.getValidators()) {
                        Markup m = MarkupHelper.createCodeBlock(validator.toString(), CodeLanguage.JSON);
                        node = validator.isSuccess() ? node.pass(m) : validator.isSkipped() ? node.skip(m)
                                : validator.getThrowable() != null ? node.fail(m).fail(validator.getThrowable())
                                : node.fail(m);
                    }
                } else {
                    node.fail(testResult.getThrowable());
                }
            }
        }
        extent.flush();
    }

    @Override
    public void sendReport(Map<String, Object> config, String message) {
        if (config != null) {
            message = getMessage();
            HtmlEmail email = new HtmlEmail();
            email.setAuthentication((String) config.get("user"), (String) config.get("password"));
            email.setHostName((String) config.get("host"));
            email.setSmtpPort(Integer.parseInt(config.get("port").toString()));
            email.setCharset("UTF-8");
            File zip = zipFile(outputDirectoryName);
            try {
                email.setFrom((String) config.get("user"));
                for (String to : (List<String>) config.get("tolist")) {
                    email.addTo(to);
                }
                email.setSubject("测试执行完毕通知");
                email.setMsg(message);
                email.attach(zip);
                email.send();
            } catch (EmailException e) {
                Report.log("email send error.", e);
            }
            zip.delete();
        }
    }

    private File zipFile(String path) {
        FileSet fileSet = new FileSet();
        fileSet.setExcludes("*.zip");
        fileSet.setDir(new File(path));
        Zip zip = new Zip();
        zip.setProject(new Project());
        zip.setDestFile(new File(path + ".reports" + ".zip"));
        zip.addFileset(fileSet);
        zip.execute();
        return new File(path + ".reports" + ".zip");
    }

    private String getMessage() {
        String template = "<body style=\"color: #666; font-size: 14px; font-family: 'Open Sans',Helvetica,Arial,sans-serif;\">\n" +
                        "<div class=\"box-content\" style=\"width: 80%%; margin: 5px auto;\">\n" +
                        "    <div class=\"info-wrap\" style=\"border-bottom-left-radius: 10px;\n" +
                        "                                  border-bottom-right-radius: 10px;\n" +
                        "                                  border-top-left-radius: 10px;\n" +
                        "                                  border-top-right-radius: 10px;\n" +
                        "                                  border:1px solid #ddd;\n" +
                        "                                  overflow: hidden;\n" +
                        "                                  padding: 15px 15px 20px;\">\n" +
                        "            <p style=\" list-style: 160%%; margin: 10px 0;\">Hi,\n" +
                        "            <br/>以下为本次测试执行结果，更多内容请查阅附件.</p>\n" +
                        "        %s" +
                        "    </div>\n" +
                        "    <div class=\"header-tip\" style=\"font-size: 12px;\n" +
                        "                                   color: #aaa;\n" +
                        "                                   text-align: right;\n" +
                        "                                   padding-right: 25px;\n" +
                        "                                   padding-bottom: 10px;\">\n" +
                        "        <a href=\"https://github.com/XiaoMiSum/MiGoo\" target=\"_blank\">MiGoo - Copyright (c) 2018 mi_xiao@qq.com</a>\n" +
                        "     </div>\n" +
                        "</div>\n" +
                        "</body>";
        StringBuilder sb = new StringBuilder()
                .append("<table class=\"list\" style=\"width: 100%%; border-collapse: collapse; border-top:1px solid #eee; font-size:12px;\">\n")
                .append("<thead>\n")
                .append("<tr style=\"background: #fafafa; color: #333; border-bottom: 1px solid #eee;\">\n")
                .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                .append("TITLE").append("</td>\n")
                .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                .append("COUNT").append("</td>\n")
                .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                .append("PASSED").append("</td>\n")
                .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                .append("FAILED").append("</td>\n")
                .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                .append("ERRORED").append("</td>\n")
                .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                .append("SKIPPED").append("</td>\n")
                .append("</tr>\n")
                .append("</thead>\n")
                .append("<tbody>\n");
        for (IResult iSuiteResult : ((ISuiteResult) result).getTestResults()) {
            ISuiteResult suiteResult = (ISuiteResult) iSuiteResult;
            sb.append("<tr style=\"border-bottom: 1px solid #eee; color:#666;\">\n")
                    .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                    .append(iSuiteResult.getTestName()).append("</td>\n")
                    .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                    .append(suiteResult.size()).append("</td>\n")
                    .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                    .append(suiteResult.getSuccessCount()).append("</td>\n")
                    .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                    .append(suiteResult.getFailureCount()).append("</td>\n")
                    .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                    .append(suiteResult.getErrorCount()).append("</td>\n")
                    .append("<td style=\"padding:6px 10px; line-height: 150%%;\">\n")
                    .append(suiteResult.getSkipCount()).append("</td>\n")
                    .append("</tr>\n");
        }
        sb.append("</tbody>\n").append("</table>\n");
        return String.format(template, sb.toString());
    }
}