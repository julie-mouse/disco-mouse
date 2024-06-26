package commands

import COMMAND_FXURL
import COMMAND_FXURL2
import FXURL_ARG1
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.rest.builder.interaction.string
import java.net.HttpURLConnection
import java.net.URL
import java.util.ResourceBundle

class FxURLCommand(
    override val bundle: ResourceBundle,
    override val bot: Kord
): Command {
    override val name = COMMAND_FXURL
    override val description: String = bundle.getString("fx_url_description")

    private val nameForJohn = COMMAND_FXURL2
    private val descForJohn = bundle.getString("fx_url_johns_description")

    override suspend fun register() {
        bot.createGlobalChatInputCommand(name, description) {
            string(FXURL_ARG1, bundle.getString("fx_url_link_description")) {
                required = true
            }
        }
        // John complained about "/fx" being dumb, so I made a second: "/fix"
        bot.createGlobalChatInputCommand(nameForJohn, descForJohn) {
            string(FXURL_ARG1, bundle.getString("fx_url_link_description")) {
                required = true
            }
        }
    }

    override suspend fun execute(interaction: GuildChatInputCommandInteraction) {

        val url = tryToFix(interaction.command.strings[FXURL_ARG1]!!)

        if (url == "Can't fix that, sorry") {
            interaction.deferEphemeralResponse().respond { content = url }
        } else {
            interaction.deferPublicResponse().respond { content = url }
        }
    }

    private fun tryToFix(url: String): String {
        return with(url) {
            when {
                contains("instagram.com/reel") -> url.replace("instagram", "instagramez")
                contains("twitter.com") -> url.replace("twitter", "twittpr")
                contains("x.com") -> url.replace("x.com", "twittpr.com")
                contains("tiktok.com") -> url.replace("tiktok", "tnktok")
                contains("reddit.com") -> url.replace("reddit", "rxddit")
                contains("v.redd.it") -> getFinalUrl(url, 10).replace("reddit", "rxddit")
                else -> "Can't fix that, sorry"
            }
        }
    }

    suspend fun fxUrlTextBased(message: Message) {
        println("Message Received: ${message.content}")

        val cleanUrl: String = sanitize(message.content)

        val response = tryToFix(cleanUrl)

        message.channel.createMessage("${message.author?.mention}: $response")
        message.delete()
    }

    private fun sanitize(message: String): String {
        val cleanMessage = message
            .removePrefix("/${COMMAND_FXURL}")
            .trim()

        val end = cleanMessage.indexOfFirst{ it == ' ' } + 1

        return if (end == 0) {
            cleanMessage
        } else {
            cleanMessage.substring(0, end)
        }
    }

    private fun getFinalUrl(url: String, maxRedirects: Int): String {
        var redirects = 0
        var currentUrl = url
        while (redirects <= maxRedirects) {
            val connection = URL(currentUrl).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false

            try {
                connection.connect()

                if (connection.responseCode in 300..399) {
                    val redirectedUrl = connection.getHeaderField("Location")
                    if (redirectedUrl != null) {
                        val newUrl = URL(URL(currentUrl), redirectedUrl).toString()
                        println("Redirected to: $newUrl")
                        currentUrl = newUrl
                        redirects++
                    } else {
                        println("No location header for redirect")
                        return "failed"
                    }
                } else {
                    println("Final URL: $currentUrl")
                    return currentUrl
                }
            } finally {
                connection.disconnect()
            }
        }

        println("Too many redirects")
        return "failed"
    }
}