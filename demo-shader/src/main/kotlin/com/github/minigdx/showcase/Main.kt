package com.github.minigdx.showcase

import com.github.minigdx.showcase.js.Showcase
import com.github.minigdx.showcase.shader.GhostShader

fun main() = Showcase.startGame({ GhostShader(it) }, "demo-shader")
