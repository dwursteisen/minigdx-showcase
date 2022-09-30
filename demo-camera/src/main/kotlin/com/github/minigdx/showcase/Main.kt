package com.github.minigdx.showcase

import com.github.minigdx.showcase.cameras.CamerasGame
import com.github.minigdx.showcase.js.Showcase

fun main() = Showcase.startGame({ CamerasGame(it) }, "demo-camera")
