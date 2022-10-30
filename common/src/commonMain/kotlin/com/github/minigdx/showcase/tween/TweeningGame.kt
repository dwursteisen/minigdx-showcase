package com.github.minigdx.showcase.tween

import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.position.Tween
import com.github.dwursteisen.minigdx.ecs.components.position.TweenFactoryComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graph.GraphScene
import com.github.dwursteisen.minigdx.math.Interpolation
import com.github.dwursteisen.minigdx.math.Interpolations
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.minigdx.imgui.ImGui

class TweenScaleComponent(val tween: Tween<Vector3>) : Component
class TweenRotationComponent(val tween: Tween<Float>) : Component

class TweenScaleSystem : System(EntityQuery.of(TweenScaleComponent::class)) {

    lateinit var current: Interpolation

    override fun onEntityAdded(entity: Entity) {
        current = entity.get(TweenScaleComponent::class).tween.interpolation
    }

    override fun update(delta: Seconds, entity: Entity) {
        val tweenComponent = entity.get(TweenScaleComponent::class)
        entity.position.setLocalScale(tweenComponent.tween.current.value)

        with(ImGui) {
            container("Scale") {
                label("Current Interpolation: $current")
                if (button("Next interpolation")) {
                    val index = Interpolations.all.indexOf(current)
                    val newIndex = (index + 1) % Interpolations.all.size
                    current = Interpolations.all[newIndex]
                    tweenComponent.tween.interpolation = current
                }
            }
        }
    }
}

class TweenRotationSystem : System(EntityQuery.Companion.of(TweenRotationComponent::class)) {

    lateinit var current: Interpolation

    override fun onEntityAdded(entity: Entity) {
        current = entity.get(TweenScaleComponent::class).tween.interpolation
    }

    override fun update(delta: Seconds, entity: Entity) {
        val tweenComponent = entity.get(TweenRotationComponent::class)
        entity.position.setLocalRotation(x = 0, y = tweenComponent.tween.current.value, z = 0)

        with(ImGui) {
            container("Rotation") {
                label("Current Interpolation: $current")
                if (button("Next interpolation")) {
                    val index = Interpolations.all.indexOf(current)
                    val newIndex = (index + 1) % Interpolations.all.size
                    current = Interpolations.all[newIndex]
                    tweenComponent.tween.interpolation = current
                }
            }
        }
    }
}

class TweeningGame(override val gameContext: GameContext) : Game {

    private val scene by gameContext.fileHandler.get<GraphScene>("light.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        scene.getAll(ObjectType.MODEL).forEach {
            entityFactory.createFromNode(it).also { entity ->
                with(entity) {
                    val tweenFactory = TweenFactoryComponent()
                    val tweenPosition = tweenFactory.vector3(
                        start = entity.position.scale.mutable(),
                        end = Vector3(2f, 2f, 2f),
                        duration = 2f,
                        interpolation = Interpolations.elastic,
                        pingpong = true
                    )
                    val tweenRotation = tweenFactory.float(
                        0f, 360f, 4f, Interpolations.pow2
                    )
                    add(tweenFactory)
                    add(TweenScaleComponent(tweenPosition))
                    add(TweenRotationComponent(tweenRotation))
                }
            }
        }
        scene.getAll(ObjectType.CAMERA).forEach {
            entityFactory.createFromNode(it)
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(TweenScaleSystem(), TweenRotationSystem())
    }
}
