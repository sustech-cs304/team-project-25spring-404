package com.training.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Jackson配置
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册自定义日期反序列化器
        SimpleModule dateModule = new SimpleModule();
        dateModule.addDeserializer(Date.class, new CustomDateDeserializer());
        objectMapper.registerModule(dateModule);
        
        // 设置默认日期格式（用于序列化）
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 设置时区
        objectMapper.setTimeZone(java.util.TimeZone.getDefault());
        // 忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许空对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 日期不转为时间戳
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        return objectMapper;
    }
    
    /**
     * 自定义日期反序列化器，支持多种日期格式
     */
    public static class CustomDateDeserializer extends StdDeserializer<Date> {
        private static final long serialVersionUID = 1L;
        
        private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd"
        };

        public CustomDateDeserializer() {
            this(null);
        }

        public CustomDateDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String dateStr = jp.getText().trim();
            if (dateStr.isEmpty()) {
                return null;
            }
            
            // 尝试多种日期格式
            for (String format : DATE_FORMATS) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.parse(dateStr);
                } catch (ParseException e) {
                    // 继续尝试下一种格式
                }
            }
            
            // 如果所有格式都失败，抛出异常
            throw new IOException("Unable to parse date: " + dateStr);
        }
    }
} 