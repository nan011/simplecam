package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.app.splash

import android.opengl.GLES20
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.lang.RuntimeException
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SplashAnimationRenderer: GLSurfaceView.Renderer {
    companion object {
        private var mProgram: Int
        private const val COORDS_PER_VERTEX = 3
        private const val vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
            // the coordinates of objects that use this vertex shader.
            "uniform mat4 uMVPMatrix;   \n" +
                    "attribute vec4 vPosition;  \n" +
                    "void main(){               \n" +
                    // The matrix must be included as part of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    " gl_Position = uMVPMatrix * vPosition; \n" +

                    "}  \n"

        private const val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"
        init {
            val vertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderCode)

            checkGLError("After create shaders")
            // create empty OpenGL ES Program
            mProgram = GLES31.glCreateProgram().also {

                // add the vertex shader to program
                GLES31.glAttachShader(it, vertexShader)

                // add the fragment shader to program
                GLES31.glAttachShader(it, fragmentShader)

                // creates OpenGL ES program executables
                GLES31.glLinkProgram(it)
                checkGLError("After link shaders")
            }
        }

        private fun loadShader(type: Int, shaderCode: String): Int {
            val shader = GLES31.glCreateShader(type)
            GLES31.glShaderSource(shader, shaderCode)
            GLES31.glCompileShader(shader)

            val message = GLES31.glGetShaderInfoLog(shader);
            if (message.isNotEmpty()) {
                /* message may be an error or a warning */
                throw RuntimeException(message);
            }

            return shader
        }

        private fun checkGLError(glOperation: String) {
            val error: Int = GLES31.glGetError()
            if (error != GLES31.GL_NO_ERROR) {
//                throw RuntimeException("$glOperation -> glError: $error")
            }
        }
    }
    private var mMVPMatrix = FloatArray(16)
    private var mProjectionMatrix = FloatArray(16)
    private var mViewMatrix = FloatArray(16)

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES31.glClearColor(0.165f, 0f, 0.165f, 1f)
        GLES31.glUseProgram(mProgram)
        checkGLError("After start to use the program")

        // Prepare the triangle coordinate data
        GLES31.glGetAttribLocation(mProgram, "vPosition").also {
            GLES31.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES31.GL_FLOAT,
                false,
                COORDS_PER_VERTEX * 4,
                Lens.getBuffer(),
            )
            checkGLError("After setting location")

            // Enable a handle to the triangle vertices
            GLES31.glEnableVertexAttribArray(it)
            checkGLError("After enable location")
        }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 0f, 0f)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Add program to OpenGL ES environment
        GLES31.glUseProgram(mProgram)

        // get handle to fragment shader's vColor member
        GLES31.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

            // Set color for drawing the triangle
            GLES31.glUniform4fv(colorHandle, 1, Lens.color, 0)
        }
        checkGLError("After set color")

        // get handle to shape's transformation matrix
        GLES31.glGetUniformLocation(mProgram, "uMVPMatrix").apply {
            // Pass the projection and view transformation to the shader
            GLES31.glUniformMatrix4fv(this, 1, false, mMVPMatrix, 0)
        }
        checkGLError("After set matrix")

        // Draw the triangle
        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, Lens.getCoords().size)
        checkGLError("After drawing")
    }
}