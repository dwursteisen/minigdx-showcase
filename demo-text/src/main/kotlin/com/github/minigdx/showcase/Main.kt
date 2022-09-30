package com.github.minigdx.showcase

import com.github.dwursteisen.minigdx.GameApplicationBuilder
import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameScreenConfiguration
import com.github.minigdx.showcase.imguilight.ImGUILight
import com.github.minigdx.showcase.js.Showcase
import com.github.minigdx.showcase.text.SampleText
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.asList
import org.w3c.dom.get

fun main() = Showcase.startGame({ SampleText(it) }, "demo-text")
