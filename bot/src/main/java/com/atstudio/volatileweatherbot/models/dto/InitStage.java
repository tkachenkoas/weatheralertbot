package com.atstudio.volatileweatherbot.models.dto;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public enum InitStage {
    SPECIFY_CITY,
    SPECIFY_TIME,
    READY_TO_SAVE,
    CREATED;

    private static Map<InitStage, InitStage> transitions = ImmutableMap.<InitStage, InitStage>builder()
            .put(SPECIFY_CITY, SPECIFY_TIME)
            .put(SPECIFY_TIME, READY_TO_SAVE)
            .put(READY_TO_SAVE, CREATED)
            .build();

    public static InitStage next(InitStage stage) {
        return stage == null ? SPECIFY_CITY : transitions.get(stage);
    }

}