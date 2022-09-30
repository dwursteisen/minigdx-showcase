package com.github.minigdx.showcase

import com.github.minigdx.showcase.js.Showcase
import com.github.minigdx.showcase.treed.PlatformerGame3D

fun main() = Showcase.startGame({ PlatformerGame3D(it) }, "demo-threed")
