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


package functions.xyz.migoo.functions;

import core.xyz.migoo.functions.AbstractFunction;
import core.xyz.migoo.functions.CompoundVariable;
import core.xyz.migoo.functions.FunctionsException;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author xiaomi
 * @date 2019/11/18 17:22
 */
public class RandomString extends AbstractFunction {

    @Override
    public String execute(CompoundVariable parameters) throws FunctionsException {
        if (parameters.isEmpty()){
            throw new FunctionsException("parameters con not be null");
        }
        Integer length = parameters.getInteger("length");
        if (length == null){
            throw new FunctionsException("length con not be null");
        }
        String charsToUse = parameters.getString("string").trim();
        if (charsToUse.isEmpty()){
            return RandomStringUtils.randomAlphabetic(length);
        }
        return RandomStringUtils.random(length, charsToUse);
    }
}
