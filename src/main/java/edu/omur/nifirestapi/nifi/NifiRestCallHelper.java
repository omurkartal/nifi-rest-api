package edu.omur.nifirestapi.nifi;

import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class NifiRestCallHelper {
    private static final Logger logger = LoggerFactory.getLogger(NifiRestCallHelper.class);

    public static <T, R> T doRestCall(String url, String token, HttpMethod type, MediaType mediaType, R requestBody, Class<T> clazz) {
        try {
            HttpEntity<R> httpEntity = new HttpEntity<>(requestBody, getHttpHeaders(token, mediaType));
            RestTemplate restTemplate = new RestTemplateBuilder().build();
            restTemplate.setMessageConverters(configureMessageConverters());

            ResponseEntity<T> response = restTemplate.exchange(url, type, httpEntity, clazz);
            logger.debug("Response text: {}", response.toString());

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            logger.error("HttpClientErrorException -> ResponseBody:" + ex.getResponseBodyAsString());
            throw ex;
        }
    }

    private static HttpHeaders getHttpHeaders(String token, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setAccept(Collections.singletonList(mediaType));
        headers.add("Authorization", token);
        return headers;
    }

    private static List<HttpMessageConverter<?>> configureMessageConverters() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(createGsonHttpMessageConverter());
        messageConverters.add(converter);
        return messageConverters;
    }

    private static GsonHttpMessageConverter createGsonHttpMessageConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(java.util.Date.class, new NifiDateAdapter());
        gsonBuilder.setPrettyPrinting().disableHtmlEscaping();
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setGson(gsonBuilder.create());
        return gsonHttpMessageConverter;
    }
}