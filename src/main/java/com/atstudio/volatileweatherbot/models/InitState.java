package com.atstudio.volatileweatherbot.models;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public enum InitState {
    CITY,
    DONE;

    private static Map<InitState, InitState> transitions = ImmutableMap.<InitState, InitState>builder()
            .put(CITY, DONE)
            .build();

    public InitState next() {
        return transitions.get(this);
    }

}