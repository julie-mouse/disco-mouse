package commands

import kotlinx.serialization.Serializable

@Serializable
data class OdesliResponse(
    val entitiesByUniqueId: Map<String, Entity>
)

@Serializable
data class Entity(
    val linksByPlatform: LinksByPlatform = LinksByPlatform(),
    val title: String = "",
    val artistName: String = ""
)

@Serializable
data class LinksByPlatform(
    val appleMusic: LinkInfo? = null,
    val spotify: LinkInfo? = null,
    val youtubeMusic: LinkInfo? = null
)

@Serializable
data class LinkInfo(
    val url: String = ""
)