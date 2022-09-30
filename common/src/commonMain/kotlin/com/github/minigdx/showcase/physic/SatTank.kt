package com.github.minigdx.showcase.physic

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.BoundingBoxComponent
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.ScriptComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.physics.SATCollisionResolver
import com.github.dwursteisen.minigdx.ecs.script.ScriptContext
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graph.GraphScene
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.math.Interpolations
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.max
import kotlin.math.min

class Tank(
    var currentSpeed: Float = 0f,
    var currentDirection: Vector3 = Vector3(0f, 0f, 1f)
) : Component

class Cube : Component

class Energie : Component

class TankSystem : System(EntityQuery(Tank::class)) {

    private val satCollisionResolver = SATCollisionResolver()

    private val energies by interested(EntityQuery(Energie::class))

    private val buildings by interested(EntityQuery.of(Cube::class))

    override fun update(delta: Seconds, entity: Entity) {
        val tank = entity.get(Tank::class)
        if (input.isKeyPressed(Key.ARROW_UP)) {
            tank.currentSpeed = min(tank.currentSpeed + 2f * delta, 2f)
        } else if (input.isKeyPressed(Key.ARROW_DOWN)) {
            tank.currentSpeed = max(tank.currentSpeed - 2f * delta, -1f)
        } else {
            tank.currentSpeed = Interpolations.lerp(0f, tank.currentSpeed)
        }

        val position = entity.position
        val key = position.simulation<Key?> {
            val key = if (input.isKeyPressed(Key.ARROW_LEFT)) {
                addLocalRotation(y = 45f * delta)
                Key.ARROW_LEFT
            } else if (input.isKeyPressed(Key.ARROW_RIGHT)) {
                addLocalRotation(y = -45f * delta)
                Key.ARROW_RIGHT
            } else {
                null
            }
            if (buildings.any { satCollisionResolver.collide(it, entity) }) {
                rollback(null)
            } else {
                commit(key)
            }
        }

        if (key == Key.ARROW_LEFT) {
            tank.currentDirection.rotate(0f, 1f, 0f, 45f * delta)
        } else if (key == Key.ARROW_RIGHT) {
            tank.currentDirection.rotate(0f, 1f, 0f, -45f * delta)
        }

        // Copy the list as removing an entity might lead to a concurrent modification exception.
        energies.toMutableList().forEach { cube ->
            if (satCollisionResolver.collide(entity, cube)) {
                cube.get(BoundingBoxComponent::class).touch = true
                entity.get(BoundingBoxComponent::class).touch = true
                cube.remove(Energie::class)
                cube.add(ScriptComponent(
                    script = {
                        destroyBox(cube)
                    }
                ))
            }
        }
        val speed = tank.currentDirection.copy().scale(-tank.currentSpeed * delta)

        entity.position.simulation<Unit> {
            addLocalTranslation(speed.x, speed.y, speed.z)
            if (buildings.any { satCollisionResolver.collide(it, entity) }) {
                rollback()
            } else {
                commit()
            }
        }
    }

    private suspend fun ScriptContext.destroyBox(target: Entity) {
        var duration = 1f
        while (duration > 0f) {
            duration -= delta
            target.position.addLocalRotation(y = 180f, delta = delta)
            yield()
        }
        executeInGameLoop {
            target.destroy()
        }
    }
}

class SatTank(override val gameContext: GameContext) : Game {

    private val scene by gameContext.fileHandler.get<GraphScene>("sat-collision.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        scene.nodes.forEach {
            val e = entityFactory.createFromNode(it)
            if (it.name == "tank") {
                e.add(Tank())
            }
            if (it.name.startsWith("Cube")) {
                e.add(Cube())
            }
            if (it.name.startsWith("enj")) {
                e.add(Energie())
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf<System>(TankSystem())
    }
}
