package com.atstudio.volatileweatherbot.services.updateprocessors;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProcessor {

    /**
     * If applicable for update, will perform all required actions and return <i>true</i>,
     * if not applicable - <i>false</i> .
     * It's implied that if <i>true</i> value was returned, no additional processing required
     */
    boolean willTakeCareOf(Update update);
}
