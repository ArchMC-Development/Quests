package lol.arch.quests.commands.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Add support for command cooldowns? (or will another plugin do this? maybe internally forced
// is better?)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String label();

    String[] aliases() default {};

    String permission() default "";

    String description() default "";

    /**
     * The command itself isn't needed on the usage, as it's implied
     */
    String usage() default "";

    CommandArgument[] commandArgumentList() default {};

    boolean player() default false;
}
