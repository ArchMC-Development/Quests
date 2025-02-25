package lol.arch.quests.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
object QuestPlayerDataService
{
    private val cache: MutableMap<UUID, QuestProfile> = mutableMapOf()

    fun configure()
    {
        with(DataStoreObjectControllerCache.create<QuestProfile>())
        {
            mongo().withCustomCollection("QuestProfile")
        }
    }

    fun cache(uuid: UUID) {
        cache[uuid] = byId(uuid).join() ?: QuestProfile(uuid)
    }

    fun invalidate(uuid: UUID) {
        cache.remove(uuid)
    }

    fun byId(uniqueId: UUID): CompletableFuture<QuestProfile?> {
        val cached = cache[uniqueId]
        if (cached != null) return CompletableFuture.completedFuture(cached)
        return DataStoreObjectControllerCache
            .findNotNull<QuestProfile>()
            .load(uniqueId, DataStoreStorageType.MONGO)
    }
}