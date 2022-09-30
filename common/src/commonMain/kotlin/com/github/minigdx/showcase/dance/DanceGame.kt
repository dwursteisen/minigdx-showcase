package com.github.minigdx.showcase.dance

import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graph.GraphScene
import com.github.dwursteisen.minigdx.input.Key

class Armature : Component

class TurnSystem : System(EntityQuery(Armature::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        if (input.isKeyPressed(Key.ARROW_LEFT)) {
            entity.position.addLocalRotation(z = -90f, delta = delta)
        } else if (input.isKeyPressed(Key.ARROW_RIGHT)) {
            entity.position.addLocalRotation(z = 90f, delta = delta)
        }
    }
}

class DanceGame(override val gameContext: GameContext) : Game {

    private val scene: GraphScene by gameContext.fileHandler.get("dance.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        scene.nodes.forEach {
            val e = entityFactory.createFromNode(it)
            if (it.type == ObjectType.LIGHT) {
                e.position.setGlobalTranslation(1.4f, 1.75f, 2.8f)
            } else if (it.type == ObjectType.ARMATURE) {
                e.add(Armature())
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(TurnSystem())
    }
}
