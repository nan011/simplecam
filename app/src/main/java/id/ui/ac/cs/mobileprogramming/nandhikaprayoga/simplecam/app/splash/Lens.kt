package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.splash

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.sin

class Lens {
    companion object {
        private const val RADIUS = 0.6f
        private const val MOVE = 0.5f

        private var coords: ArrayList<FloatArray>? = null
        private fun createCircleCoords(radius: Float, move: Float = 0.2f): ArrayList<FloatArray> {
            if (this.coords == null) {
                var angle = 0.0
                val coords = ArrayList<FloatArray>()
                while (angle < 360) {
                    coords.addAll(
                        arrayListOf(
                            floatArrayOf(
                                0f,
                                0f,
                                0f,
                            ),
                            floatArrayOf(
                                (radius * sin(Math.toRadians(angle))).toFloat(),
                                (radius * kotlin.math.cos(Math.toRadians(angle))).toFloat(),
                                0f,
                            ),
                            floatArrayOf(
                                (radius * sin(Math.toRadians(angle + move))).toFloat(),
                                (radius * kotlin.math.cos(Math.toRadians(angle + move))).toFloat(),
                                0f,
                            )
                        ),
                    )
                    angle += move
                }
                this.coords = coords
            }

            return this.coords!!
        }

        fun getCoords(): ArrayList<FloatArray> {
            return createCircleCoords(RADIUS, MOVE)
        }

        private var buffer: FloatBuffer? = null
        fun getBuffer(): FloatBuffer? {
            if (buffer == null) {
                val rawCoords = getCoords()
                val temp = ArrayList<Float>()
                for (coords in rawCoords) {
                    temp.addAll(Array(3) { coords[it] })
                }
                val flatCoords = temp.toFloatArray()

                this.buffer = ByteBuffer.allocateDirect(flatCoords.size * 4).run {
                    // use the device hardware's native byte order
                    order(ByteOrder.nativeOrder())

                    // create a floating point buffer from the ByteBuffer
                    asFloatBuffer().apply {
                        // add the coordinates to the FloatBuffer
                        put(flatCoords)
                        // set the buffer to read the first coordinate
                        position(0)
                    }
                }
            }

            return this.buffer
        }

        val color = floatArrayOf(1f, 1f, 1f, 1f)
    }
}