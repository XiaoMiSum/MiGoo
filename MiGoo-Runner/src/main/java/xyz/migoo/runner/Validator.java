package xyz.migoo.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import junit.framework.Assert;
import xyz.migoo.config.Dict;
import xyz.migoo.exception.ValidatorException;
import xyz.migoo.http.Response;
import xyz.migoo.utils.Function;
import xyz.migoo.utils.StringUtil;

import java.lang.reflect.Method;

import static xyz.migoo.config.Platform.*;

/**
 * @author xiaomi
 * @date 2018/7/24 20:58
 */
public class Validator extends Assert {

    private static Method[] methods = null;
    private static String function;

    private Validator() {
    }

    public static void validation(Response response, JSON validate) throws ValidatorException {
        if (response.isError()) {
            throw new ValidatorException("request error. please check your network .\n"
                    + "\t" + response.error());
        }

        if (response.isNotFound()) {
            throw new ValidatorException("error 404. please check your url: \n"
                    + "\t" + response.request().url());
        }
        if (validate instanceof JSONObject) {
            validation(response, (JSONObject) validate);
        }
        if (validate instanceof JSONArray) {
            validation(response, (JSONArray) validate);
        }

    }

    private static void validation(Response response, JSONArray validate) throws ValidatorException {
        for (int i = 0; i < validate.size(); i++) {
            validation(response, validate.getJSONObject(i));
        }
    }

    private static void validation(Response response, JSONObject validate) throws ValidatorException {
        String types = validate.getString(Dict.VALIDATE_TYPE);
        String check = validate.getString(Dict.VALIDATE_CHECK);
        Object expect = validate.get(Dict.VALIDATE_EXPECT);

        if (methods == null) {
            methods = Function.functionLoader();
        }
        evalValidate(response, validate);

        Object actual = validate.get(Dict.VALIDATE_ACTUAL);

        try {
            validation(check, types, actual, expect);
        } catch (Exception e) {
            throw new ValidatorException(StringUtil.getStackTrace(e));
        }
    }

    /**
     * 重新运算 求出参数 validate 的内容
     * 将 response 的 body 或 status 放到 validate.actual 中
     * @param response
     * @param validate
     * @throws ValidatorException
     */
    private static void evalValidate(Response response, JSONObject validate) throws ValidatorException {
        function(validate.getString(Dict.VALIDATE_CHECK));
        try {
            for (Method method : methods) {
                if (method.getName().equals(function)) {
                    method.invoke(null, response, validate);
                }
            }
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage());
        }
    }

    private synchronized static void validation(String check, String types, Object actual, Object expect) throws ValidatorException {
        function(types);
        boolean result = false;
        try {
            for (Method method : methods) {
                if (method.getName().equals(function)) {
                    result = (boolean) method.invoke(null, actual, expect);
                }
            }
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage());
        }
        if (!result) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("check result: " + false + "\n")
                    .append("\tcheck point: ").append(check).append("\n")
                    .append("\tcheck actual: ").append(actual).append("\n")
                    .append("\tcheck expect: ").append(expect).append("\n")
                    .append("\tcheck type: ").append(types).append("\n");
            throw new ValidatorException(errorMsg.toString());
        }
    }

    private static void function(String searchChar){
        if (CHECK_BODY.contains(searchChar)) {
            function = Dict.EVAL_ACTUAL_BODY;
            return;
        }
        if (CHECK_CODE.contains(searchChar)) {
            function = Dict.EVAL_ACTUAL_STATUS;
            return;
        }
        if (FUNCTION_EQUALS.contains(searchChar)) {
            function = Dict.VALIDATE_TYPE_EQUALS;
            return;
        }
        if (FUNCTION_CONTAINS.contains(searchChar)) {
            function = Dict.VALIDATE_TYPE_CONTAINS;
            return;
        }
        if (FUNCTION_IS_EMPTY.contains(searchChar)) {
            function = Dict.VALIDATE_TYPE_IS_EMPTY;
            return;
        }
        if (FUNCTION_IS_NOT_EMPTY.contains(searchChar)) {
            function = Dict.VALIDATE_TYPE_IS_NOT_EMPTY;
            return;
        }
        if (isJson(searchChar)) {
            function = Dict.EVAL_ACTUAL_JSON;
            return;
        }
        if (isHtml(searchChar)) {
            function = Dict.EVAL_ACTUAL_HTML;
            return;
        }
        if (function == null){
            throw new ValidatorException("'function' is null");
        }
    }

}