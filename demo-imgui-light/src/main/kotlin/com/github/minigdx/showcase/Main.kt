package com.github.minigdx.showcase

import com.github.minigdx.showcase.imguilight.ImGUILight
import com.github.minigdx.showcase.js.Showcase

fun main() = Showcase.startGame({ ImGUILight(it) }, "demo-imgui-light")
