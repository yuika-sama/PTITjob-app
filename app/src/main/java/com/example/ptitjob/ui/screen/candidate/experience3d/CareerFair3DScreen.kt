package com.example.ptitjob.ui.screen.candidate.experience3d

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.rendering.Color as SceneformColor
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.Node
// no lookAt extension in this setup; compute rotation manually
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CompletableFuture
import kotlin.math.cos
import kotlin.math.sin

/**
 * Career fair 3D screen: allows the candidate to freely explore a static 3D hall.
 * The assets are stored locally (no backend calls) and the user can orbit/zoom around.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerFair3DScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sceneView = remember { SceneView(context) }

    val orbitYaw = remember { mutableFloatStateOf(0f) }
    val orbitPitch = remember { mutableFloatStateOf(35f) }
    val orbitRadius = remember { mutableFloatStateOf(6f) }

    fun updateCamera() {
        val radius = orbitRadius.floatValue
        val pitchRadians = Math.toRadians(orbitPitch.floatValue.toDouble())
        val yawRadians = Math.toRadians(orbitYaw.floatValue.toDouble())
        val x = (radius * cos(pitchRadians) * sin(yawRadians)).toFloat()
        val y = (radius * sin(pitchRadians)).toFloat()
        val z = (radius * cos(pitchRadians) * cos(yawRadians)).toFloat()

        val camera = sceneView.scene.camera
        val position = Vector3(x, y, z)
        val target = Vector3(0f, 0.5f, 0f)
        val forward = Vector3.subtract(target, position).normalized()
        val up = Vector3.up()
        camera.worldPosition = position
        camera.worldRotation = Quaternion.lookRotation(forward, up)
    }

    DisposableEffect(sceneView, lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> sceneView.resume()
                Lifecycle.Event.ON_PAUSE -> sceneView.pause()
                Lifecycle.Event.ON_DESTROY -> sceneView.destroy()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            sceneView.destroy()
        }
    }

    LaunchedEffect(sceneView) {
        setupScene(sceneView)
        updateCamera()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Sảnh việc làm 3D", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { sceneView },
                modifier = Modifier.fillMaxSize()
            ) { view ->
                attachGestureControls(
                    view = view,
                    orbitYaw = orbitYaw,
                    orbitPitch = orbitPitch,
                    orbitRadius = orbitRadius,
                    onCameraUpdated = { updateCamera() }
                )
            }

            InstructionOverlay(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }
    }
}

private suspend fun setupScene(sceneView: SceneView) {

    // -------- Materials palette --------
    val wallMat = MaterialFactory.makeOpaqueWithColor(
        sceneView.context,
        SceneformColor(0.92f, 0.94f, 0.97f)
    ).await()
    val floorTileMat = MaterialFactory.makeOpaqueWithColor(
        sceneView.context,
        SceneformColor(0.86f, 0.86f, 0.90f)
    ).await()
    val accents = listOf(
        SceneformColor(0.18f, 0.46f, 0.88f), // blue
        SceneformColor(0.93f, 0.45f, 0.23f), // orange
        SceneformColor(0.21f, 0.70f, 0.54f), // green
        SceneformColor(0.74f, 0.36f, 0.84f)  // purple
    ).map { MaterialFactory.makeOpaqueWithColor(sceneView.context, it).await() }

    // -------- Procedural booth kit (sizes are in meters) --------
    data class BoothKit(
        val floor: ModelRenderable,
        val backWall: ModelRenderable,
        val sideWall: ModelRenderable,
        val counter: ModelRenderable,
        val header: ModelRenderable
    )

    fun makeBoothKit(
        wall: com.google.ar.sceneform.rendering.Material,
        accent: com.google.ar.sceneform.rendering.Material
    ): BoothKit {
        // Floor 3x3m, thin slab with top at y=0
        val floor = ShapeFactory.makeCube(
            Vector3(3.0f, 0.05f, 3.0f),
            Vector3(0f, -0.025f, 0f),
            floorTileMat
        )
        // Back wall 3m wide, 2.2m high, thin
        val backWall = ShapeFactory.makeCube(
            Vector3(3.0f, 2.2f, 0.08f),
            Vector3(0f, 1.1f, -1.46f),
            wall
        )
        // Side wall 2.2m high, 3m deep (right side by default)
        val sideWall = ShapeFactory.makeCube(
            Vector3(0.08f, 2.2f, 3.0f),
            Vector3(1.46f, 1.1f, 0f),
            wall
        )
        // Counter front
        val counter = ShapeFactory.makeCube(
            Vector3(1.2f, 1.0f, 0.5f),
            Vector3(0f, 0.5f, 0.9f),
            accent
        )
        // Header panel (banner) near the back top
        val header = ShapeFactory.makeCube(
            Vector3(2.2f, 0.28f, 0.08f),
            Vector3(0f, 2.35f, -1.46f),
            accent
        )
        return BoothKit(floor, backWall, sideWall, counter, header)
    }

    val boothKits = accents.map { makeBoothKit(wallMat, it) }

    val boothPositions = listOf(
        Vector3(-4.5f, 0f, -3.5f),
        Vector3(-1.5f, 0f, -3.5f),
        Vector3(1.5f, 0f, -3.5f),
        Vector3(4.5f, 0f, -3.5f),
        Vector3(-4.5f, 0f, 3.5f),
        Vector3(-1.5f, 0f, 3.5f),
        Vector3(1.5f, 0f, 3.5f),
        Vector3(4.5f, 0f, 3.5f)
    )

    boothPositions.forEachIndexed { index, position ->
        val kit = boothKits[index % boothKits.size]
        val boothRoot = Node().apply {
            name = "Booth #${index + 1}"
            localPosition = position
        }
        // Floor
        boothRoot.addChild(Node().apply {
            name = "BoothFloor"
            renderable = kit.floor.makeCopy()
        })
        // Back wall
        boothRoot.addChild(Node().apply {
            name = "BackWall"
            renderable = kit.backWall.makeCopy()
        })
        // Left wall (mirror the right wall by shifting -2.92 on X)
        boothRoot.addChild(Node().apply {
            name = "LeftWall"
            renderable = kit.sideWall.makeCopy()
            localPosition = Vector3(-2.92f, 0f, 0f)
        })
        // Right wall (original sideWall is already positioned +X)
        boothRoot.addChild(Node().apply {
            name = "RightWall"
            renderable = kit.sideWall.makeCopy()
        })
        // Counter
        boothRoot.addChild(Node().apply {
            name = "Counter"
            renderable = kit.counter.makeCopy()
        })
        // Header
        boothRoot.addChild(Node().apply {
            name = "Header"
            renderable = kit.header.makeCopy()
        })

        sceneView.scene.addChild(boothRoot)
    }

    val floorMaterial = MaterialFactory.makeOpaqueWithColor(
        sceneView.context,
        SceneformColor(0.85f, 0.85f, 0.92f)
    ).await()

    val floorRenderable = ShapeFactory.makeCube(
        Vector3(10f, 0.05f, 10f),
        Vector3(0f, -0.025f, 0f),
        floorMaterial
    )

    sceneView.scene.addChild(Node().apply {
        localPosition = Vector3.zero()
        renderable = floorRenderable
        name = "Floor"
    })

    val stageMaterial = MaterialFactory.makeOpaqueWithColor(
        sceneView.context,
        SceneformColor(0.25f, 0.27f, 0.32f)
    ).await()

    // Stage platform
    val stageRenderable = ShapeFactory.makeCube(
        Vector3(6f, 0.4f, 3f),
        Vector3(0f, 0.2f, 0f),
        stageMaterial
    )
    // Backdrop behind stage
    val backdropMaterial = MaterialFactory.makeOpaqueWithColor(
        sceneView.context,
        SceneformColor(0.18f, 0.18f, 0.22f)
    ).await()
    val backdropRenderable = ShapeFactory.makeCube(
        Vector3(6.2f, 2.4f, 0.08f),
        Vector3(0f, 1.4f, -1.6f),
        backdropMaterial
    )

    val stageRoot = Node().apply { name = "Stage" }
    stageRoot.addChild(Node().apply { renderable = stageRenderable })
    stageRoot.addChild(Node().apply { renderable = backdropRenderable })
    sceneView.scene.addChild(stageRoot)
}

private fun attachGestureControls(
    view: SceneView,
    orbitYaw: androidx.compose.runtime.MutableFloatState,
    orbitPitch: androidx.compose.runtime.MutableFloatState,
    orbitRadius: androidx.compose.runtime.MutableFloatState,
    onCameraUpdated: () -> Unit
) {
    var previousX = 0f
    var previousY = 0f
    var isRotating = false

    val scaleDetector = ScaleGestureDetector(view.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val newRadius = (orbitRadius.floatValue / detector.scaleFactor).coerceIn(3f, 12f)
            orbitRadius.floatValue = newRadius
            onCameraUpdated()
            return true
        }
    })

    view.setOnTouchListener { _, event ->
        scaleDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
                isRotating = true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                isRotating = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isRotating && event.pointerCount == 1 && !scaleDetector.isInProgress) {
                    val dx = event.x - previousX
                    val dy = event.y - previousY
                    orbitYaw.floatValue = (orbitYaw.floatValue - dx * 0.2f) % 360f
                    val newPitch = (orbitPitch.floatValue + dy * 0.15f).coerceIn(10f, 80f)
                    orbitPitch.floatValue = newPitch
                    previousX = event.x
                    previousY = event.y
                    onCameraUpdated()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isRotating = false
            }
        }
        true
    }
}

@Composable
private fun InstructionOverlay(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dùng một ngón tay để xoay, hai ngón để phóng to/thu nhỏ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Không gian ảo được dựng sẵn, dữ liệu cố định để bạn tham quan hội chợ nghề nghiệp.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

private suspend fun <T> CompletableFuture<T>.await(): T = suspendCancellableCoroutine { continuation ->
    whenComplete { result, exception ->
        if (exception != null) {
            if (!continuation.isCancelled) {
                continuation.resumeWith(Result.failure(exception))
            }
        } else {
            continuation.resume(result) {}
        }
    }
    continuation.invokeOnCancellation {
        if (!isDone) {
            cancel(true)
        }
    }
}
