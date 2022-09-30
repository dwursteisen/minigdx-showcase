package com.github.minigdx.showcase.text

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.HorizontalAlignment
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.text.TypeWriterEffect
import com.github.dwursteisen.minigdx.ecs.components.text.WaveEffect
import com.github.dwursteisen.minigdx.ecs.components.text.WriteText
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graph.GraphScene
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.input.TouchSignal
import kotlin.math.sin

class HorizontalAlignmentSystem : System(EntityQuery.of(TextComponent::class)) {

    private val alignments = listOf(HorizontalAlignment.Left, HorizontalAlignment.Center, HorizontalAlignment.Right)

    private var time = 0f

    override fun update(delta: Seconds, entity: Entity) {
        val txt = entity.get(TextComponent::class)

        time += delta
        entity.position.setLocalRotation(y = sin(time * 0.5f) * 25f)

        if (input.isKeyJustPressed(Key.SPACE) || input.isJustTouched(TouchSignal.TOUCH1) != null) {
            val current = alignments.indexOf(txt.horizontalAlign)
            txt.horizontalAlign = alignments[(current + 1) % alignments.size]
        }
    }
}

class SampleText(override val gameContext: GameContext) : Game {

    private val scene: GraphScene by gameContext.fileHandler.get("sampleText.protobuf")

    private val font: Font by gameContext.fileHandler.get("pt_font")

    override fun createEntities(entityFactory: EntityFactory) {

        scene.nodes.forEach { node ->
            if (node.name == "wave") {
                val textEffect = WaveEffect(WriteText(" 1234567890"), 0.05f, 10)
                entityFactory.createText(textEffect, font, node)
                    .also {
                        val textComponent = it.get(TextComponent::class)
                        textComponent.lineWith = 30
                        textComponent.horizontalAlign = HorizontalAlignment.Center
                    }
            } else if (node.name == "typewriter") {
                val textEffect = TypeWriterEffect(WriteText("This is a type writer effect   "), 0.05f, loop = true)
                entityFactory.createText(textEffect, font, node)
                    .also {
                        val textComponent = it.get(TextComponent::class)
                        textComponent.lineWith = 30
                        textComponent.horizontalAlign = HorizontalAlignment.Center
                    }
            } else {
                entityFactory.createFromNode(node)
            }

        }
    }

    override fun createSystems(engine: Engine): List<System> = listOf(HorizontalAlignmentSystem())
}
