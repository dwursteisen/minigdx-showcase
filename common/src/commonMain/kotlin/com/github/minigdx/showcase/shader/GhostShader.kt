package com.github.minigdx.showcase.shader

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Resolution
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graphics.FrameBuffer
import com.github.dwursteisen.minigdx.graphics.TextureFrameBuffer
import com.github.dwursteisen.minigdx.math.Vector2
import com.github.dwursteisen.minigdx.render.ClearBufferRenderStage
import com.github.dwursteisen.minigdx.render.ModelComponentRenderStage
import com.github.dwursteisen.minigdx.shaders.ShaderParameter
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader

private val shader = """
        void main() {
              float xOffset = inputUV.x + cos(inputUV.y * uAmplitude.y + uTime * uAmplitude.x) * 0.1;
              vec2 offset = vec2(clamp(0.0, 1.0, xOffset), inputUV.y);
              gl_FragColor = texture2D(uSprite, offset);
        }
"""
class GhostFragmentShader : FragmentShader(shader) {

    val uSprite = ShaderParameter.UniformSample2D("uSprite")
    val uAmplitude = ShaderParameter.UniformVec2("uAmplitude")
    val uTime = ShaderParameter.UniformFloat("uTime")
    private val inputUV = ShaderParameter.VaryingVec2("inputUV")

    override val parameters: List<ShaderParameter> = listOf(uSprite, uAmplitude, uTime, inputUV)
}

class GhostShader(override val gameContext: GameContext) : Game {

    private val txt: Texture by gameContext.fileHandler.get("2d-platformer-spr.png")

    override fun createEntities(entityFactory: EntityFactory) {
        txt.hasAlpha = true

        val player = entityFactory.createSprite(txt, 16, 16, Mat4.identity()) {
            addAnimation("idle", 0, 1) { _, frameRelative ->
                if(frameRelative == 0) {
                    500
                } else {
                    100
                }
            }
            addAnimation("jump_up", 4, 4, 100)
            addAnimation("jump_down", 5, 5, 100)
            addAnimation("run", 2, 3, 100)
        }

        player.position.setLocalScale(x = 0.5f, y = 1f)
    }

    override fun createFrameBuffers(gameContext: GameContext): List<FrameBuffer> {
        val fragmentBuffer = FrameBuffer(
            name = "sprite",
            gameContext = gameContext,
            resolution = Resolution(gameContext.frameBufferScreen.width, gameContext.frameBufferScreen.height),
            stages = listOf(
                ClearBufferRenderStage(gameContext, Color(0f, 0f, 0f, 1f)),
                ModelComponentRenderStage(gameContext)
            )
        )
        val ghostEffect = object : TextureFrameBuffer<GhostFragmentShader>(
            name ="ghostEffect",
            gameContext = gameContext,
            resolution =Resolution(gameContext.frameBufferScreen.width, gameContext.frameBufferScreen.height),
            fragmentShader = GhostFragmentShader(),
            dependencies = listOf(fragmentBuffer),
            renderOnScreen = true
        ) {

            var time = 0f

            override fun updateFragmentShader(delta: Seconds) {
                time += delta
                fragmentShader.uAmplitude.apply(program, Vector2(8f, 4f))
                fragmentShader.uSprite.apply(program, fragmentBuffer.texture.textureReference!!)
                fragmentShader.uTime.apply(program, time)
            }
        }
        return listOf(ghostEffect)
    }

    override fun createSystems(engine: Engine): List<System> {
        return emptyList()
    }
}
