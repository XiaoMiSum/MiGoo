/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 XiaoMiSum (mi_xiao@qq.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.migoo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import components.xyz.migoo.readers.YamlReader;
import components.xyz.migoo.readers.ReaderException;
import components.xyz.migoo.reports.Report;
import core.xyz.migoo.*;
import core.xyz.migoo.report.IReport;
import core.xyz.migoo.utils.DateUtil;
import core.xyz.migoo.utils.StringUtil;

/**
 * @author xiaomi
 * @date 2018/7/24 14:24
 */
public class Migoo {

    public static void main(String[] args) {
        String file = StringUtil.isEmpty(args[0]) ? "./case/migoo-default.yml" : args[0];
        new Migoo().run(file);
    }

    public void run(String file) {
        try {
            TestSuite suite = new TestSuite(processor(file));
            IResult result = suite.run();
            this.report(suite.getReportConfig(), suite.getEmailConfig(), result);
        } catch (Exception e) {
            Report.log("run error", e);
        }
    }

    public JSONObject processor(String file) throws ReaderException {
        // 1. 解析文件 获取解析到的 json 对象
        JSONObject fileJson = (JSONObject) new YamlReader(file).read();
        JSONObject config = fileJson.getJSONObject("config");
        JSONObject dataset = fileJson.getJSONObject("dataset");
        JSONArray files = fileJson.getJSONArray("files");
        // 判断是否为导入文件
        if (config != null && config.get("file") != null) {
            fileJson.put("config", new YamlReader(config.getString("file")).read());
        }
        if (dataset != null && dataset.get("file") != null) {
            fileJson.put("dataset", new YamlReader(dataset.getString("file")).read());
        }
        JSONArray sets = files != null ? new JSONArray() : fileJson.getJSONArray("sets");
        if (files != null) {
            for (Object filePath : files) {
                sets.add(new YamlReader(filePath.toString()).read());
            }
            fileJson.remove("files");
        }
        fileJson.put("sets", sets);
        return fileJson;
    }

    private void report(JSONObject reportConfig, JSONObject emailConfig, IResult result) {
        String output = reportConfig != null && reportConfig.get("output") != null ?
                reportConfig.getString("output") : "./out-put/" + DateUtil.TODAY_DATE ;
        String listener = emailConfig != null && !StringUtil.isEmpty(emailConfig.getString("listener")) ?
                emailConfig.getString("listener") : "components.migoo.xyz.reports.Report";
        IReport report;
        try {
            report = (IReport) Class.forName(listener).newInstance();
        } catch (Exception e) {
            report = new Report();
        }
        report.generateReport(result, output);
        report.sendReport(emailConfig, "");
    }

}