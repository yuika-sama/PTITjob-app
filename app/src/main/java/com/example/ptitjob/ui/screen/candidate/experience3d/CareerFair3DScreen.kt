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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner // <-- dùng import này (compose UI)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.rendering.Color as SceneformColor
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.HitTestResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.util.concurrent.CompletableFuture
import kotlin.math.cos
import kotlin.math.sin

data class SelectionInfo(
    val title: String,
    val description: String,
    val focus: Vector3
)

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

    // --- CHANGED: dùng mutableStateOf<Float> và .value everywhere ---
    val orbitYaw = remember { mutableStateOf(0f) }
    val orbitPitch = remember { mutableStateOf(35f) }
    val orbitRadius = remember { mutableStateOf(6f) }
    var focusTarget by remember { mutableStateOf(Vector3(0f, 0.5f, 0f)) }
    var selection by remember { mutableStateOf<SelectionInfo?>(null) }

    val scope = rememberCoroutineScope()

    fun updateCamera() {
        val radius = orbitRadius.value
        val pitchRadians = Math.toRadians(orbitPitch.value.toDouble())
        val yawRadians = Math.toRadians(orbitYaw.value.toDouble())
        val x = (radius * cos(pitchRadians) * sin(yawRadians)).toFloat()
        val y = (radius * sin(pitchRadians)).toFloat()
        val z = (radius * cos(pitchRadians) * cos(yawRadians)).toFloat()

        val camera = sceneView.scene.camera
        val position = Vector3(focusTarget.x + x, focusTarget.y + y, focusTarget.z + z)
        val target = focusTarget
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
        setupScene(sceneView) { info ->
            scope.launch{
                selection = info

                val startRadius = orbitRadius.value
                val endRadius = 3.2f
                val steps = 14

                val startFocus = focusTarget
                val targetFocus = info.focus

                for (i in 1..steps) {
                    val t = i / steps.toFloat() // 0 → 1

                    // Zoom camera dần
                    orbitRadius.value = startRadius + (endRadius - startRadius) * t

                    // Move focus mượt
                    focusTarget = Vector3(
                        startFocus.x + (targetFocus.x - startFocus.x) * t,
                        startFocus.y + (targetFocus.y - startFocus.y) * t,
                        startFocus.z + (targetFocus.z - startFocus.z) * t
                    )

                    updateCamera()
                    delay(14) // đúng: kotlinx.coroutines.delay
                }
            }

            updateCamera()
        }

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
                    onCameraUpdated = { updateCamera() },
                    onItemTapped = { info ->
                        // Reuse same selection animation handler used in setupScene
                        scope.launch {
                            selection = info
                            val startRadius = orbitRadius.value
                            val endRadius = 3.2f
                            val steps = 14
                            val startFocus = focusTarget
                            val targetFocus = info.focus
                            for (i in 1..steps) {
                                val t = i / steps.toFloat()
                                orbitRadius.value = startRadius + (endRadius - startRadius) * t
                                focusTarget = Vector3(
                                    startFocus.x + (targetFocus.x - startFocus.x) * t,
                                    startFocus.y + (targetFocus.y - startFocus.y) * t,
                                    startFocus.z + (targetFocus.z - startFocus.z) * t
                                )
                                updateCamera()
                                delay(14)
                            }
                            updateCamera()
                        }
                    }
                )
            }

            // Selection info overlay
            selection?.let { sel ->
                SelectionOverlay(
                    title = sel.title,
                    description = sel.description,
                    onClose = {
                        selection = null
                        focusTarget = Vector3(0f, 0.5f, 0f)
                        orbitRadius.value = 6f
                        updateCamera()
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
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

private suspend fun setupScene(
    sceneView: SceneView,
    onItemTapped: (SelectionInfo) -> Unit
) {

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

    // Hotspot indicator for tappable parts
    val hotspotMaterial = MaterialFactory.makeOpaqueWithColor(
        sceneView.context,
        SceneformColor(1.0f, 0.85f, 0.15f)
    ).await()
    val hotspotRenderable = ShapeFactory.makeCube(
        Vector3(0.2f, 0.2f, 0.2f),
        Vector3.zero(),
        hotspotMaterial
    )

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
        // Counter (interactive)
        boothRoot.addChild(Node().apply {
            name = "Counter"
            renderable = kit.counter.makeCopy()
            setOnTapListener { _, _ ->
                // pulse effect
                try { val s = localScale; localScale = Vector3(s.x*1.08f, s.y*1.08f, s.z*1.08f) } catch (_: Throwable) {}
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    try { val s = localScale; localScale = Vector3(s.x/1.08f, s.y/1.08f, s.z/1.08f) } catch (_: Throwable) {}
                }, 140)
                val wp = worldPosition
                onItemTapped(
                    SelectionInfo(
                        title = "Quầy thông tin",
                        description = "Thông tin mô phỏng: giới thiệu doanh nghiệp, tài liệu, liên hệ.",
                        focus = Vector3(wp.x, wp.y + 0.5f, wp.z)
                    )
                )
            }
            // Hotspot marker above counter
            addChild(Node().apply {
                name = "Hotspot_Counter"
                localPosition = Vector3(0f, 0.7f, 0f)
                renderable = hotspotRenderable.makeCopy()
                setOnTapListener { _, _ ->
                    // delegate same action as counter
                    val wp = this@apply.worldPosition
                    onItemTapped(
                        SelectionInfo(
                            title = "Quầy thông tin",
                            description = "Thông tin mô phỏng: giới thiệu doanh nghiệp, tài liệu, liên hệ.",
                            focus = Vector3(wp.x, wp.y + 0.5f, wp.z)
                        )
                    )
                }
            })
        })
        // Header (interactive)
        boothRoot.addChild(Node().apply {
            name = "Header"
            renderable = kit.header.makeCopy()
            setOnTapListener { _, _ ->
                val wp = worldPosition
                onItemTapped(
                    SelectionInfo(
                        title = "Banner gian hàng",
                        description = "Mô phỏng: slogan, ưu đãi tuyển dụng, vị trí nổi bật.",
                        focus = Vector3(wp.x, wp.y, wp.z)
                    )
                )
            }
            // Hotspot marker just above header panel
            addChild(Node().apply {
                name = "Hotspot_Header"
                localPosition = Vector3(0f, 0.24f, 0f)
                renderable = hotspotRenderable.makeCopy()
                setOnTapListener { _, _ ->
                    val wp = this@apply.worldPosition
                    onItemTapped(
                        SelectionInfo(
                            title = "Banner gian hàng",
                            description = "Mô phỏng: slogan, ưu đãi tuyển dụng, vị trí nổi bật.",
                            focus = Vector3(wp.x, wp.y, wp.z)
                        )
                    )
                }
            })
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
    orbitYaw: androidx.compose.runtime.MutableState<Float>,
    orbitPitch: androidx.compose.runtime.MutableState<Float>,
    orbitRadius: androidx.compose.runtime.MutableState<Float>,
    onCameraUpdated: () -> Unit,
    onItemTapped: (SelectionInfo) -> Unit
) {
    var previousX = 0f
    var previousY = 0f
    var isRotating = false

    val scaleDetector = ScaleGestureDetector(view.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val newRadius = (orbitRadius.value / detector.scaleFactor).coerceIn(3f, 12f)
            orbitRadius.value = newRadius
            onCameraUpdated()
            return true
        }
    })

    var downX = 0f
    var downY = 0f
    var downTime = 0L
    val tapSlopPx = 24f
    val tapTimeMs = 350L

    // Use Scene peek listener so Node taps are accessible
    view.scene.addOnPeekTouchListener { hit: HitTestResult, event: MotionEvent ->
        scaleDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
                isRotating = true
                downX = event.x
                downY = event.y
                downTime = event.eventTime
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                isRotating = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isRotating && event.pointerCount == 1 && !scaleDetector.isInProgress) {
                    val dx = event.x - previousX
                    val dy = event.y - previousY
                    orbitYaw.value = (orbitYaw.value - dx * 0.2f) % 360f
                    val newPitch = (orbitPitch.value + dy * 0.15f).coerceIn(10f, 80f)
                    orbitPitch.value = newPitch
                    previousX = event.x
                    previousY = event.y
                    onCameraUpdated()
                }
            }
            MotionEvent.ACTION_UP -> {
                isRotating = false
                // Detect tap
                val dt = event.eventTime - downTime
                val dx = event.x - downX
                val dy = event.y - downY
                val isTap = !scaleDetector.isInProgress &&
                        dt <= tapTimeMs &&
                        (dx*dx + dy*dy) <= tapSlopPx*tapSlopPx

                if (isTap) {
                    val node = hit.node as? Node
                    // Walk up to a known interactive node
                    var current: Node? = node
                    while (current != null) {
                        when (current.name) {
                            "Hotspot_Counter", "Counter" -> {
                                val wp = current.worldPosition
                                onItemTapped(
                                    SelectionInfo(
                                        title = "Quầy thông tin",
                                        description = "Thông tin mô phỏng: giới thiệu doanh nghiệp, tài liệu, liên hệ.",
                                        focus = Vector3(wp.x, wp.y + 0.5f, wp.z)
                                    )
                                )
                                current = null
                                continue
                            }
                            "Hotspot_Header", "Header" -> {
                                val wp = current.worldPosition
                                onItemTapped(
                                    SelectionInfo(
                                        title = "Banner gian hàng",
                                        description = "Mô phỏng: slogan, ưu đãi tuyển dụng, vị trí nổi bật.",
                                        focus = Vector3(wp.x, wp.y, wp.z)
                                    )
                                )
                                current = null
                                continue
                            }
                        }
                        current = current?.parent as? Node
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                isRotating = false
            }
        }
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

@Composable
private fun SelectionOverlay(
    title: String,
    description: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Đóng")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
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
            // CHANGED: gọi resume đúng cách (không truyền lambda trống)
            continuation.resume(result)
        }
    }
    continuation.invokeOnCancellation {
        if (!isDone) {
            cancel(true)
        }
    }
}

