/*
 *
 *  * The MIT License (MIT)
 *  *
 *  * Copyright (c) 2018. Lorem XiaoMiSum (mi_xiao@qq.com)
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
 */

package function.xyz.migoo;

import com.alibaba.fastjson.JSONObject;
import core.xyz.migoo.function.CompoundParameter;
import core.xyz.migoo.function.Function;

public class Value implements Function {

    /**
     * 从传入的JSON对象中获取指定key的值，支持两个参数，且两个参数都不允许为空
     * 参数：
     *      key: 指定的key，非空
     *      json: 指定json对象，非空
     */
    @Override
    public Object execute(CompoundParameter parameters) throws Exception {
        if (parameters.isEmpty()){
            throw new Exception("parameters con not be null");
        }
        if (parameters.getString("key").isEmpty()) {
            throw new Exception("key con not be null");
        }
        JSONObject object = parameters.isNullKey("json")?
                parameters.getJSONObject("object") : parameters.getJSONObject("json");
        if (object == null){
            throw new Exception("json con not be null");
        }
        return object.get(parameters.getString("key"));
    }
}