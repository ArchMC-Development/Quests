package lol.arch.quests.sync

import lol.arch.quests.models.Quest
import java.util.*

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
data class ActiveQuestHolder(
    val cache: MutableMap<UUID, Quest> = mutableMapOf(),
    val active: MutableMap<UUID, Int> = mutableMapOf(),
)
