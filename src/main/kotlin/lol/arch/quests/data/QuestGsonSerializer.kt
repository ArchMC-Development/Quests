package lol.arch.quests.data

import gg.scala.store.serializer.DataStoreSerializer
import net.evilblock.cubed.serializers.Serializers
import kotlin.reflect.KClass

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
object QuestGsonSerializer : DataStoreSerializer
{
    private var supplier = { Serializers.gson }

    override fun serialize(`object`: Any): String
    {
        return supplier.invoke()
            .toJson(`object`)
    }

    override fun <T : Any> deserialize(
        `class`: KClass<T>, input: String
    ): T
    {
        return supplier.invoke()
            .fromJson(input, `class`.java)
    }
}