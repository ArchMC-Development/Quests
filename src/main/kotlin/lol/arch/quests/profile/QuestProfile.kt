package lol.arch.quests.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
class QuestProfile(
    override val identifier: UUID,

    var language: String = "en",
    var completedQuests: Int = 0,
) : IDataStoreObject
{
    fun save(): CompletableFuture<Void> =
        DataStoreObjectControllerCache
            .findNotNull<QuestProfile>()
            .save(this, DataStoreStorageType.MONGO)
}