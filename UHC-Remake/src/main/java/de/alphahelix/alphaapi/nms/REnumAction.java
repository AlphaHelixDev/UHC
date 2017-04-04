package de.alphahelix.alphaapi.nms;

import de.alphahelix.alphaapi.reflection.ReflectionUtil;

public enum REnumAction {

    INTERACT(0),
    ATTACK(1),
    INTERACT_AT(2);

    private int c;

    REnumAction(int c) {
        this.c = c;
    }

    public Object getEnumAction() {
        return ReflectionUtil.getNmsClass("PacketPlayInUseEntity$EnumEntityUseAction").getEnumConstants()[c];
    }
}
