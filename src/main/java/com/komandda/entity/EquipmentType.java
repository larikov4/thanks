package com.komandda.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EquipmentType {
    @JsonProperty("camera")
    CAMERA,
    @JsonProperty("lens")
    LENS,
    @JsonProperty("light")
    LIGHT,
    @JsonProperty("sound")
    SOUND,
    @JsonProperty("accessory")
    ACCESSORY;

}