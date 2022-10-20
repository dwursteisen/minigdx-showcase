package com.github.minigdx.showcase.tween

import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.position.Tween
import com.github.dwursteisen.minigdx.ecs.components.position.Tweening
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graph.GraphScene
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.minigdx.imgui.ImGui

class TweenComponent(var name: String, var active: Boolean, val tween: Tween, val values: Vector3) : Component

class TweenSystem : System(EntityQuery.of(TweenComponent::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        val tweenComponents = entity.findAll(TweenComponent::class)
        tweenComponents.forEach { tweenComponent ->
            val percent = tweenComponent.tween.update(delta)
            if (tweenComponent.active) {
                entity.position.setLocalScale(tweenComponent.values)
            }
            if (percent >= 1.0f) {
                tweenComponent.tween.reset()
                tweenComponent.tween.reverse = !tweenComponent.tween.reverse
            }

            with(ImGui) {
                container("Tweening") {
                    label("Interpolation: ${tweenComponent.name}")
                    if (button("Active interpolation")) {
                        tweenComponents.forEach {
                            it.active = false
                            tweenComponent.active = true
                        }
                    }

                    checkbox("Is Active", tweenComponent.active)
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
                    val fields = Vector3(1f, 1f, 1f)
                    val tween = Tweening.linear(1f)
                        .fields(
                            fields::x to 2f,
                            fields::y to 2f,
                            fields::z to 2f,
                        )
                        .build()

                    add(TweenComponent("linear", false, tween, fields))
                }

                with(entity) {
                    val fields = Vector3(1f, 1f, 1f)
                    val tween = Tweening.elastic(1f)
                        .fields(
                            fields::x to 2f,
                            fields::y to 2f,
                            fields::z to 2f,
                        )
                        .build()

                    add(TweenComponent("elastic", true, tween, fields))
                }

                with(entity) {
                    val fields = Vector3(1f, 1f, 1f)
                    val tween = Tweening.pow(1f)
                        .fields(
                            fields::x to 2f,
                            fields::y to 2f,
                            fields::z to 2f,
                        )
                        .build()

                    add(TweenComponent("pow", false, tween, fields))
                }
            }
        }
        scene.getAll(ObjectType.CAMERA).forEach {
            entityFactory.createFromNode(it)
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(TweenSystem())
    }
}
