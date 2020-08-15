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


package components.xyz.migoo.readers;

import com.alibaba.fastjson.JSONObject;
import components.xyz.migoo.reports.Report;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author xiaomi
 */
public class PropertiesReader extends AbstractReader implements Reader {


    private JSONObject json;

    public PropertiesReader(String path) throws ReaderException {
        super.stream(ReaderFactory.PROS_SUFFIX, path);
    }

    public PropertiesReader(File file) throws ReaderException {
        super.stream(ReaderFactory.PROS_SUFFIX, file);
    }

    @Override
    public JSONObject read() throws ReaderException {
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            Report.log(e.getMessage(), e);
            throw new ReaderException("file read exception: " + e.getMessage());
        }
        json = (JSONObject)JSONObject.toJSON(props);
        return json;
    }

    @Override
    public String get(String key) throws ReaderException {
        if (json == null){
            read();
        }
        return json.getString(key);
    }
}