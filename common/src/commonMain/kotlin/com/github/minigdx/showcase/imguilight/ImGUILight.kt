package com.github.minigdx.showcase.imguilight

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Game
import com.github.minigdx.imgui.ImGui

class ImGUILight(override val gameContext: GameContext) : Game {

    override fun createEntities(entityFactory: EntityFactory) = Unit

    override fun createSystems(engine: Engine): List<System> {
        return listOf(NopeSystem())
    }
}

class NopeSystem : System() {

    override fun update(delta: Seconds, entity: Entity) = Unit

    private var counter = 0

    override fun update(delta: Seconds) {
        with(ImGui) {
            container("Demo ImGUI") {
                label("Example of label")
                if(button("Counter: $counter")) {
                    counter = (counter + 1) % 10
                }
                checkbox("Counter above 5", counter > 5)
            }
        }
    }
}
