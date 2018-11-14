package io.data2viz.viz

import android.content.*
import android.graphics.*
import io.data2viz.timer.*

typealias AView = android.view.View

/**
 * Create an AndroidVizView from the viz and the android context.
 */
fun Viz.toView(context: Context): AndroidVizView = AndroidVizView(this, context)



class AndroidVizView(val viz: Viz, context: Context) : AView(context) {

    private val renderer: AndroidCanvasRenderer = AndroidCanvasRenderer(viz, context)
    private val timers = mutableListOf<Timer>()

    fun startAnimations() {
        if (viz.animations.isNotEmpty()) {
            viz.animations.forEach { anim ->
                timers += timer { time ->
					anim(time)
				}
            }
            timers += timer {
				invalidate()
			}
        }
    }

    fun stopAnimations(){
        for (timer in timers) {
            timer.stop()
        }
        timers.clear()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viz.resize(w.toDouble(), h.toDouble())
        updateScale()
    }

    fun updateScale() {
        renderer.scale = (width / viz.width).toFloat()
    }

    var drawCount = -1
    private var startTime = System.currentTimeMillis()

    override fun onDraw(canvas: Canvas) {
        drawCount++
        if (drawCount == 100){
            val delta = System.currentTimeMillis() - startTime
            val fps = 100_000 / delta
            startTime = System.currentTimeMillis()
            drawCount = -1
        }

        renderer.canvas = canvas
        renderer.render()
    }

}
