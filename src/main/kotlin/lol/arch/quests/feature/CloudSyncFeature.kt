package lol.arch.quests.feature

import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.commons.agnostic.sync.ServerSync

/**
 * Created on 25/02/2025
 *
 * @author Preva1l
 */
object CloudSyncFeature
{
    fun configure()
    {
        CloudSyncDiscoveryService.discoverable.assets
            .add("lol.arch.quests:quests:Quests${
                if ("dev" in ServerSync.getLocalGameServer().groups) ":gradle-dev" else ""
            }")
    }
}