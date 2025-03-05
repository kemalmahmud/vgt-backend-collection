package com.vgt.collections.Enum;

public enum InitialCollectionEnum {
    WANT_TO_PLAY("Want to Play"),
    NOW_PLAYING("Now Playing"),
    FINISHED("Finished"),
    ARCHIVED("Archived");

    private String name;

    InitialCollectionEnum(String name) { this.name = name; }
    public String getName() { return this.name; }
}
