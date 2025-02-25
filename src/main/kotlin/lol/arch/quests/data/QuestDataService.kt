package lol.arch.quests.data

import com.mongodb.client.model.Filters
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import lol.arch.quests.models.Quest

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
object QuestDataService
{
    fun configure()
    {
        with(DataStoreObjectControllerCache.create<Quest>())
        {
            mongo().withCustomCollection("QuestData")
            serializer = QuestGsonSerializer
        }
    }

    fun getAll() = DataStoreObjectControllerCache
        .findNotNull<Quest>()
        .loadAll(DataStoreStorageType.MONGO)

    fun byVisualId(id: String) = DataStoreObjectControllerCache
        .findNotNull<Quest>()
        .mongo()
        .loadWithFilter(
            Filters.eq("id", id),
        )

    fun save(data: Quest) = DataStoreObjectControllerCache
        .findNotNull<Quest>()
        .save(data, DataStoreStorageType.MONGO)

    fun delete(uniqueId: Quest) = DataStoreObjectControllerCache
        .findNotNull<Quest>()
        .delete(uniqueId.identifier, DataStoreStorageType.MONGO)
}