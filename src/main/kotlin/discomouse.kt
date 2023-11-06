import commands.AnonMsgCommand
import commands.FxURLCommand
import dev.kord.core.Kord
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import java.util.ResourceBundle

fun main() = runBlocking {
    val bundle = ResourceBundle.getBundle("Strings")
    val kord = Kord(System.getenv("DISCO_MOUSE_KEY"))

    val fxUrlCommand = FxURLCommand(bundle, kord)
    val anonMsgCommand = AnonMsgCommand(bundle, kord)

    fxUrlCommand.register()
    anonMsgCommand.register()

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        when(interaction.command.rootName) {
            COMMAND_FXURL, COMMAND_FXURL2 -> fxUrlCommand.execute(interaction)
            COMMAND_ANONMSG -> anonMsgCommand.execute(interaction)
        }
    }

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on

        with(message.content) {
            when {
                startsWith("/${COMMAND_FXURL}") || startsWith("/${COMMAND_FXURL2}") ->
                    fxUrlCommand.fxUrlTextBased(message)

                else -> return@on
            }
        }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents = Intents(Intent.MessageContent, Intent.GuildMembers)
    }
}
