package com.github.minigdx.showcase

import com.github.minigdx.showcase.js.Showcase
import com.github.minigdx.showcase.twod.PlatformerGame2D

fun main() = Showcase.startGame({ PlatformerGame2D(it) }, "demo-twod")
