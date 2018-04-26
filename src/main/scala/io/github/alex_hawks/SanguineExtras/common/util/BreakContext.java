package io.github.alex_hawks.SanguineExtras.common.util;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class BreakContext
{
    public final int        fortune;
    public final boolean    silk_touch;
    public final boolean    crusher;

    public BreakContext(int fortune, boolean silk_touch, boolean crusher)
    {
        this.fortune = fortune;
        this.silk_touch = silk_touch;
        this.crusher = crusher;
    }
}
