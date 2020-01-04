package com.atstudio.volatileweatherbot.services.scheduled.service

import com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert.ChatAlertContext
import org.testng.annotations.Test

class ChatAlertContextTest {

    @Test(expectedExceptions = IllegalStateException)
    void mustInit() {
        ChatAlertContext.getResultChatMessageMap()
    }

    @Test
    void targetUsage() {
        ChatAlertContext.init()

        ChatAlertContext.addChatMessageForLocation(1L, "city 1", "message 1")
        ChatAlertContext.addChatMessageForLocation(1L, "city 1","message 2")
        ChatAlertContext.addChatMessageForLocation(2L, "city 2","message 3")

        def resultMap = ChatAlertContext.getResultChatMessageMap()

        assert resultMap.get(1L).get('city 1') == ["message 1", "message 2"]
        assert resultMap.get(2L).get('city 2') == ["message 3"]

        assert resultMap.keySet().size() == 2

        ChatAlertContext.clear()
    }

}
