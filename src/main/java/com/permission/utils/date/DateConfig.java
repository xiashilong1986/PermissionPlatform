package com.permission.utils.date;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * **********************
 * * Author: xiashilong *
 * * Date: 2018-03-16   *
 * * Time: 18:07        *
 * * to: lz&xm          *
 * **********************
 **/
@Configuration
public class DateConfig {
    @Bean
    public Formatter<LocalDate> localDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale) {
                if (!StringUtils.isEmpty(text)) {
                    return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                return null;
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(object);
            }

        };
    }

    @Bean
    public Formatter<LocalTime> localTimeFormatter() {
        return new Formatter<LocalTime>() {
            @Override
            public LocalTime parse(String text, Locale locale) {
                if (!StringUtils.isEmpty(text)) {
                    return LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm:ss"));
                }
                return null;
            }

            @Override
            public String print(LocalTime object, Locale locale) {
                return DateTimeFormatter.ofPattern("HH:mm:ss").format(object);
            }
        };
    }

    @Bean
    public Formatter<LocalDateTime> localDateTimeFormatter() {
        return new Formatter<LocalDateTime>() {
            @Override
            public LocalDateTime parse(String text, Locale locale) {
                if (!StringUtils.isEmpty(text)) {
                    return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                return null;
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(object);
            }
        };
    }

    /**
     * 默认日期时间格式
     */
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);//序列化
        objectMapper.registerModule(hibernate5Module());
        return objectMapper;
    }

    /**
     * 解决jpa懒加载
     */
    @Bean
    public Hibernate5Module hibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        module.enable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        return module;
    }
}
