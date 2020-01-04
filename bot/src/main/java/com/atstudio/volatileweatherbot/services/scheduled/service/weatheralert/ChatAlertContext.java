package com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert;

import java.util.*;

import static java.util.Collections.unmodifiableMap;

public class ChatAlertContext {

    private static final ThreadLocal<ChatAlertContext> THREAD_LOCAL = new ThreadLocal<>();

    public static void init() {
        THREAD_LOCAL.set(new ChatAlertContext());
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

    private Map<Long, Map<String, List<String>>> chatCityMessageMap = new HashMap<>();

    public static void addChatMessageForLocation(Long chatId, String cityLabel, String message) {
        Map<Long, Map<String, List<String>>> chatMessageMap = getContext().chatCityMessageMap;
        chatMessageMap.putIfAbsent(chatId, new HashMap<>());

        Map<String, List<String>> cityToMessagesMap = chatMessageMap.get(chatId);
        cityToMessagesMap.putIfAbsent(cityLabel, new ArrayList<>());


        cityToMessagesMap.get(cityLabel).add(message);
    }

    public static Map<Long, Map<String, List<String>>> getResultChatMessageMap() {
        return unmodifiableMap(getContext().chatCityMessageMap);
    }

    private static ChatAlertContext getContext() {
        ChatAlertContext result = THREAD_LOCAL.get();
        if (result == null) {
            throw new IllegalStateException("Context must be initialized via init method");
        }
        return result;
    }

}
