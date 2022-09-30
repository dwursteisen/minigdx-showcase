package com.github.minigdx.showcase.cameras

import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.CameraComponent
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
import com.github.dwursteisen.minigdx.math.Interpolations
import kotlin.math.min

class CameraSpot(val camera: CameraComponent) : Component

class CamerasSystem : System(EntityQuery.Companion.of(CameraComponent::class)) {

    private val camerasSpot by interested(EntityQuery.Companion.of(CameraSpot::class))

    private var percent = 0f
    private lateinit var targetEntity: Entity

    private var currentIndex = 0

    override fun onGameStarted(engine: Engine) {
        targetEntity = camerasSpot.first()
    }

    override fun update(delta: Seconds, entity: Entity) {
        val camera = entity.get(CameraComponent::class)

        // Interpolation of the camera's transform (ie: position)
        val transformation = Interpolations.interpolate(
            targetEntity.position.localTransformation,
            entity.position.localTransformation,
            percent
        )
        entity.position.setLocalTransform(transformation)

        // Interpolation of the Field Of View (fov) of the camera
        val targetFov = targetEntity.get(CameraSpot::class).camera.fov
        camera.fov = Interpolations.interpolate(targetFov, camera.fov, percent)

        percent = min(1f, percent + delta)

        if (input.isKeyJustPressed(Key.SPACE) || input.isKeyJustPressed(Key.ARROW_RIGHT)) {
            percent = 0f
            currentIndex = (currentIndex + 1) % camerasSpot.size
            targetEntity = camerasSpot[currentIndex]
        } else if (input.isKeyJustPressed(Key.ARROW_LEFT)) {
            percent = 0f
            currentIndex = (camerasSpot.size + (currentIndex - 1)) % camerasSpot.size
            targetEntity = camerasSpot[currentIndex]
        }
    }
}

class CamerasGame(override val gameContext: GameContext) : Game {

    private val scene: GraphScene by gameContext.fileHandler.get("cameras.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        scene.nodes.forEach { node ->
            val entity = entityFactory.createFromNode(node)

            if (node.type == ObjectType.CAMERA) {
                val camera = entity.get(CameraComponent::class)
                entity.remove(camera)
                entity.add(CameraSpot(camera))
            }
        }

        scene.nodes.first { it.type == ObjectType.CAMERA }
            .let { entityFactory.createFromNode(it) }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(CamerasSystem())
    }
}
