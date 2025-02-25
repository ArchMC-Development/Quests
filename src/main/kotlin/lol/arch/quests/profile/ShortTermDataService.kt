package lol.arch.quests.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.impl.RedisDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
object ShortTermDataService
{
    private val cache: MutableMap<UUID, ShortTermQuestProfile> = mutableMapOf()

    fun configure()
    {
        with(DataStoreObjectControllerCache.create<ShortTermQuestProfile>())
        {
            useLayerWithReturn<RedisDataStoreStorageLayer<ShortTermQuestProfile>, RedisDataStoreStorageLayer<ShortTermQuestProfile>>(
                DataStoreStorageType.REDIS
            ) {
               this
            }.withCustomSection { this.append("ShortTermQuestProfile") }
        }
    }

    fun cache(uuid: UUID) {
        cache[uuid] = byId(uuid).join() ?: ShortTermQuestProfile(uuid)
    }

    fun invalidate(uuid: UUID) {
        cache.remove(uuid)
    }

    fun getAll() = DataStoreObjectControllerCache
        .findNotNull<ShortTermQuestProfile>()
        .loadAll(DataStoreStorageType.REDIS)

    fun byId(uniqueId: UUID) = DataStoreObjectControllerCache
        .findNotNull<ShortTermQuestProfile>()
        .load(uniqueId, DataStoreStorageType.REDIS)
}