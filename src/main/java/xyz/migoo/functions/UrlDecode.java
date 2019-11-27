package xyz.migoo.functions;

import xyz.migoo.exception.ExtenderException;
import xyz.migoo.framework.functions.AbstractFunction;
import xyz.migoo.framework.functions.CompoundVariable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author xiaomi
 * @date 2019/11/18 17:22
 */
public class UrlDecode extends AbstractFunction {

    @Override
    public String execute(CompoundVariable parameters) throws ExtenderException {
        try {
            return URLDecoder.decode(parameters.getAsString("string"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new ExtenderException("url decode exception", e);
        }
    }
}