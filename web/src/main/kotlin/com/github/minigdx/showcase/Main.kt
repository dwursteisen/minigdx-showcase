package com.github.minigdx.showcase

import com.github.dwursteisen.minigdx.GameApplicationBuilder
import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameScreenConfiguration
import com.github.minigdx.showcase.cameras.CamerasGame
import com.github.minigdx.showcase.dance.DanceGame
import com.github.minigdx.showcase.imguilight.ImGUILight
import com.github.minigdx.showcase.physic.SatTank
import com.github.minigdx.showcase.shader.GhostShader
import com.github.minigdx.showcase.text.SampleText
import com.github.minigdx.showcase.treed.PlatformerGame3D
import com.github.minigdx.showcase.twod.PlatformerGame2D
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.asList
import org.w3c.dom.get

fun main() {
    val canvas = document.getElementsByTagName("canvas")[0]!!
    val root = document.getElementsByTagName("script").asList()
        .first { it.hasAttribute("src") && it.getAttribute("src")?.contains("minigdx") == true }
        .getAttribute("src")!!
        .replace("minigdx-showcase.js", "")

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
        gameFactory = {
            val gameName = canvas.getAttribute("property").toString()
            when (gameName) {
                "2d-platformer" -> PlatformerGame2D(it)
                "3d-platformer" -> PlatformerGame3D(it)
                "dance" -> DanceGame(it)
                "tank" -> SatTank(it)
                "cameras" -> CamerasGame(it)
                "sampleText" -> SampleText(it)
                "ghostShader" -> GhostShader(it)
                "imgguilight" -> ImGUILight(it)
                else -> TODO("$gameName seems to be an invalid game name. check again.")
            }
        }
    ).start()
}
