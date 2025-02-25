package lol.arch.quests.sync

import gg.scala.commons.persist.datasync.DataSyncKeys
import gg.scala.commons.persist.datasync.DataSyncService
import net.kyori.adventure.key.Key

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
object ActiveQuestsDataSync : DataSyncService<ActiveQuestHolder>()
{
    object QuestKeys : DataSyncKeys
    {
        override fun newStore() = "activequests-sync"

        override fun store() = Key.key("quests", "activequests-store")
        override fun sync() = Key.key("quests", "activequests-sync")
    }

    override fun keys() = QuestKeys
    override fun type() = ActiveQuestHolder::class.java
}