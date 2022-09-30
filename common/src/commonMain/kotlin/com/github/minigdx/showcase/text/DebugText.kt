package com.github.minigdx.showcase.text

import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.components.HorizontalAlignment
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graph.GraphScene

class DebugText(override val gameContext: GameContext) : Game {

    val scene by gameContext.fileHandler.get<GraphScene>("debug-text.protobuf")
    val font by gameContext.fileHandler.get<Font>("pt_font")

    override val clearColor: Color = Color(0f, 0f, 0f, 1f)

    override fun createEntities(entityFactory: EntityFactory) {
        scene.nodes.forEach { node ->
            if(node.type == ObjectType.BOX) {
                val entity = entityFactory.createText("AA", font, node)
                entity.get(TextComponent::class).horizontalAlign = HorizontalAlignment.Left
                entity.get(TextComponent::class).lineWith = 4
            } else {
                entityFactory.createFromNode(node)
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return emptyList()
    }
}
