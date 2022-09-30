package com.github.minigdx.showcase

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.particles.ParticleConfiguration
import com.github.dwursteisen.minigdx.ecs.components.particles.ParticleEmitterComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graph.GraphScene
import com.github.dwursteisen.minigdx.input.Key
import kotlin.math.cos

class EmitterSystem : System(EntityQuery.of(ParticleEmitterComponent::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        if (input.isKeyJustPressed(Key.SPACE)) {
            entity.get(ParticleEmitterComponent::class).emit()
        }
    }
}

class Plane : Component

class MoveSystem : System(EntityQuery.Companion.of(Plane::class)) {

    var t = 0f
    override fun update(delta: Seconds, entity: Entity) {
        t += delta
        entity.position.setLocalTranslation(x = cos(t) * 2f)
        entity.position.addLocalRotation(z = 90f, delta = delta)
    }
}

class Particles(override val gameContext: GameContext) : Game {

    private val particles by gameContext.fileHandler.get<GraphScene>("particles.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        particles.nodes.forEach { node ->
            if (node.name == "Plane") {
                val entity = entityFactory.createFromNode(node)
                    .add(Plane())

                val emitter = entityFactory.createParticles(
                    ParticleConfiguration.spark(
                        factory = {
                            entityFactory.createFromNode(node)
                        },
                        time = -1,
                        duration = 0.3f
                    )
                )

                emitter.attachTo(entity)


            } else {
                entityFactory.createFromNode(node)
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(EmitterSystem(), MoveSystem())
    }
}
