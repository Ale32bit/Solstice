plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.1"

stonecutter parameters {
    replacements {
        string(current.parsed >= "26.1") {
            replace("accessWidener v2 named", "classTweaker v2 official")
        }
        string(current.parsed >= "1.21.11") {
            replace("player.getServer()", "player.level().getServer()")
            replace("sourcePlayer.getServer()", "sourcePlayer.level().getServer()")
            replace("dimension().location()", "dimension().identifier()")
        }

        string(current.parsed >= "26.2") {
            replace("Placeholders.register(", "Placeholders.registerCommon(")
            replace("dev.emi.trinkets", "eu.pb4.trinkets")
        }
    }
}
