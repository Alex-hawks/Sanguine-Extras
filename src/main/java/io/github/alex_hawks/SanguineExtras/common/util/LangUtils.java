package io.github.alex_hawks.SanguineExtras.common.util;

import net.minecraft.util.StatCollector;

public class LangUtils
{
    public static String translate(String s)
    {
        return StatCollector.translateToLocal(s);
    }
}
