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

package core.xyz.migoo.testelement;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiGooProperty extends JSONObject {

    public MiGooProperty() {
        super(true);
    }

    public MiGooProperty(String name) {
        super(true);
    }

    public MiGooProperty(Map<String, Object> map) {
        super(true);
        super.putAll(map);
    }

    public MiGooProperty(String key, Object value) {
        super(true);
        super.put(key, value);
    }

    @Override
    public Object putIfAbsent(String name, Object value) {
        if (this.get(name) == null && value != null) {
            return super.put(name, value);
        }
        return null;
    }

    @Override
    public MiGooProperty clone() {
        return (MiGooProperty) super.clone();
    }

    @Override
    public String toString(){
        JSONObject newMap = new JSONObject(size());
        keySet().forEach(key -> newMap.put(key, convertObject(get(key))));
        return newMap.toString();
    }

    private Object convertObject(Object obj){
        return obj instanceof String || obj instanceof Number || obj instanceof Boolean
                || obj instanceof Map || obj instanceof List || obj == null ? obj : obj.toString();
    }
}