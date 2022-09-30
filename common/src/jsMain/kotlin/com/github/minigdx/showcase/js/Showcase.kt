package com.github.minigdx.showcase.js

import com.github.dwursteisen.minigdx.GameApplicationBuilder
import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.GameScreenConfiguration
import com.github.dwursteisen.minigdx.game.Game
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.asList
import org.w3c.dom.get

object Showcase {
    fun startGame(game: (gameContext: GameContext) -> Game, resource: String) {
        println("Start looking for canvas..")
        val canvas = document.getElementsByTagName("canvas")[0]!!
        println("Canvas found!")
        // Get the root path by getting the path of the JS Script and removing the script name
        println("Start looking for root URL")
        val root = document.getElementsByTagName("script").asList()
            .first { it.hasAttribute("src") && it.getAttribute("src")?.contains(resource) == true }
            .getAttribute("src")!!
            .replace("${resource.removeSuffix(".js")}.js", "")
        println("Root URL found $root")

        GameApplicationBuilder(
            gameConfigurationFactory = {
                GameConfiguration(
                    gameName = "Showcase game",
                    debug = false,
                    canvas = canvas as HTMLCanvasElement,
                    gameScreenConfiguration = GameScreenConfiguration.WithRatio(16f / 9f),
                    rootPath = root
                )
            },
            gameFactory = game
        ).start()
    }
}
