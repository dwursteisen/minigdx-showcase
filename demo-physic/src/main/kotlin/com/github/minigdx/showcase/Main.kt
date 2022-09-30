package com.github.minigdx.showcase

import com.github.minigdx.showcase.js.Showcase
import com.github.minigdx.showcase.physic.SatTank

fun main() = Showcase.startGame({ SatTank(it) }, "demo-physic")
