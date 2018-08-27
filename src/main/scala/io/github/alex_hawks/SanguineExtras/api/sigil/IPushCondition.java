package io.github.alex_hawks.SanguineExtras.api.sigil;

import io.github.alex_hawks.SanguineExtras.common.util.sigils.interdiction.push_handlers.Default;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IPushCondition
{
    /**
     * @param pushedEntity  The entity that might be pushed
     * @param pusher        The player holding the Sigil
     * @return <ul>
     *     <li>{@link Push#FORCE FORCE}: will push the entity, regardless of other conditions</li>
     *     <li>{@link Push#ALLOW ALLOW}: will push the entity, if none of the other conditions {@link Push#DENY DENY} it. This is the default to return</li>
     *     <li>{@link Push#IGNORE IGNORE}: use this if your handler doesn't {@link Push#ALLOW push} or {@link Push#DENY deny} the entity</li>
     *     <li>{@link Push#DENY   DENY}: will not push the entity, unless one of the other conditions {@link Push#FORCE FORCEs} it</li>
     * </ul>
     * @see Default#canPush(Entity, EntityPlayer)
     */
    Push canPush(Entity pushedEntity, EntityPlayer pusher);

    /**
     *  Same as {@link #canPush(Entity, EntityPlayer)}, except the you have a UUID instead of an entity. You'll get this in the case of a Ritual
     */
    Push canPush(Entity pushedEntity, UUID player);

    /**
     * Helper method to add comments for Packmakers to refer to when configuring the Interdiction Overrides.cfg. Not set in stone, but for simplicities sake, don't crash the game if you return true here
     * @param entityID the Entity's ID in {@link net.minecraftforge.fml.common.registry.ForgeRegistries#ENTITIES ForgeRegistries#ENTITIES}
     * @return true if it does, false if it doesn't
     */
    boolean handlesEntity(ResourceLocation entityID);

    default String getName()
    {
        return this.getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

    enum Push // ignores results declared earlier in the list
    {
        IGNORE(false),  //  Do nothing
        ALLOW(true),    //  Push
        DENY(false),    //  Do nothing
        FORCE(true);    //  Push

        public final boolean push;

        Push(Boolean b) {push = b;}
    }
}
