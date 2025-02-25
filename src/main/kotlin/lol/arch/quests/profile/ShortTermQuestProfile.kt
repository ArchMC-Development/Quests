package lol.arch.quests.profile

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import lol.arch.quests.models.Quest
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
class ShortTermQuestProfile(
    override val identifier: UUID,

    val dailyCompletedQuests: MutableSet<UUID> = mutableSetOf(),
    var currentQuestId: String = "",
    var currentQuestProgress: Int = 0
) : IDataStoreObject {
    fun incrementQuestProgress(amount: Int)
    {
        this.currentQuestProgress += amount
    }

    fun setCurrentQuestIdAsCompleted()
    {
        dailyCompletedQuests.add(UUID.fromString(this.currentQuestId))
        optOutOfCurrentQuest()
        QuestPlayerDataService.byId(identifier).thenAccept {
            var profile = it
            if (profile == null) profile = QuestProfile(identifier)
            profile.completedQuests += 1
            profile.save()
        }
    }

    fun optOutOfCurrentQuest()
    {
        currentQuestId = ""
        currentQuestProgress = 0
    }

    fun reset()
    {
        DataStoreObjectControllerCache
            .findNotNull<ShortTermQuestProfile>()
            .delete(this.identifier, DataStoreStorageType.REDIS)
    }

    fun getCurrentQuestProgress(quest: Quest): Int
    {
        if (quest.identifier.toString() == currentQuestId)
        {
            return currentQuestProgress
        }

        return 0
    }

    fun save(): CompletableFuture<Void> =
        DataStoreObjectControllerCache
            .findNotNull<ShortTermQuestProfile>()
            .save(this, DataStoreStorageType.REDIS)
}