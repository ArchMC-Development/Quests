package com.codepunisher.quests.commands.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandArgument {
    int index();

    /**
     * @return Simple string list that will auto
     * tab complete based on the index
     */

    String[] values() default {};

    /**
     * This will tab complete all the players
     * on the network based on the associated index
     */
    boolean players() default false;

    /**
     * This is only relevant if there's a dynamic object
     * that was registered with this class, because then
     * this id will use that object to retrieve the tab
     * complete list (only on commands that register the id)
     * <p>
     * This should really only be necessary if the list being
     * pulled is "dynamic" which just means it changes. A static
     * list shouldn't need a dynamic object hardly ever
     */
    String dynamicId() default "";

    /**
     * Because enums are static and don't change, an enum
     * class can be added here, and will auto tab complete
     * fully based on the configured index
     */
    Class<? extends Enum> enumClass() default NoEnumClass.class;
}
