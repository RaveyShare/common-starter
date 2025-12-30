package com.ravey.common.utils.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ravey.common.api.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Ravey
 * @since 1.0.0
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper OBJECT_MAPPER = null;

    /**
     * Long自动转String(js对于long类型接受回丢失精度，需要转string)
     */
    private static ObjectMapper OBJECT_MAPPER_LONG2STRING = null;

    private JsonUtil() {
    }

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OBJECT_MAPPER_LONG2STRING = new ObjectMapper();
        OBJECT_MAPPER_LONG2STRING.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER_LONG2STRING.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER_LONG2STRING.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        OBJECT_MAPPER_LONG2STRING.registerModule(simpleModule);
    }

    /**
     * bean2Json（Long自动转String）
     *
     * @param obj
     * @return
     */
    public static String bean2JsonAndLong2String(Object obj) {
        StringWriter sw = new StringWriter();
        JsonGenerator gen;
        try {
            if (obj != null) {
                gen = new JsonFactory().createGenerator(sw);
                OBJECT_MAPPER_LONG2STRING.writeValue(gen, obj);
                gen.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("JSON转换异常");
        }
        return sw.toString();
    }

    /**
     * bean2Json
     *
     * @param obj
     * @return
     */
    public static String bean2Json(Object obj) {
        StringWriter sw = new StringWriter();
        JsonGenerator gen;
        try {
            if (obj != null) {
                gen = new JsonFactory().createGenerator(sw);
                OBJECT_MAPPER.writeValue(gen, obj);
                gen.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("JSON转换异常");
        }
        return sw.toString();
    }

    /**
     * json2Bean
     *
     * @param jsonStr
     * @param objClass
     * @param <T>
     * @return
     */
    public static <T> T json2Bean(String jsonStr, Class<T> objClass) {
        try {
            if (jsonStr != null && jsonStr.length() > 0 && objClass != null) {
                return OBJECT_MAPPER.readValue(jsonStr, objClass);
            }
        } catch (Exception e) {
            log.info("JSON解析异常,请检查数据格式;data:[{}],error:{}", jsonStr, e.getMessage());
            throw new ServiceException("JSON解析异常");
        }
        return null;
    }

    /**
     * 将json array反序列化为对象
     *
     * @param jsonStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T json2Bean(String jsonStr, com.fasterxml.jackson.core.type.TypeReference<T> typeReference) {
        try {
            if (jsonStr != null && typeReference != null) {
                return (T) OBJECT_MAPPER.readValue(jsonStr, typeReference);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("JSON解析异常");
        }
        return null;
    }

    /**
     * json2ListBean
     *
     * @param jsonStr
     * @param elementClasses
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T json2ListBean(String jsonStr, Class<?> elementClasses) {
        try {
            if (jsonStr != null && jsonStr.length() > 0  && elementClasses != null) {
                JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
                return OBJECT_MAPPER.readValue(jsonStr, javaType);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("JSON解析异常");
        }
        return null;
    }

    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }

}
