package com.github.minigdx.showcase

import com.github.minigdx.showcase.dance.DanceGame
import com.github.minigdx.showcase.js.Showcase

fun main() = Showcase.startGame({ DanceGame(it) }, "demo-dance")
