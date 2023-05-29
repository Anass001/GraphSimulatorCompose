# GraphSimulatorCompose
This project is an Android application developed in Kotlin using Jetpack Compose that simulates graph theory algorithms. The application allows users to draw graphs on a canvas, run different algorithms on them, and visualize the results.

# Architechture
<img src="https://miro.medium.com/v2/resize:fit:1200/1*ol7iY_f4OiFSxO7qhfGqiw.png" align="left" width="450">
This application uses the Jetpack Compose Architecture with ViewModel. It is designed to create interactive and reactive applications. The architecture leverages the ViewModel component to manage UI state and handle user interactions. The UIState data class (GraphState) represents the current state of the user interface. The MainActivity contains a composable function defining the UI structure. An event emitter triggers UI events, which are processed by the ViewModel for state updates.
<br>
<br>
<br>
<br>

# Canvas API
<img src="https://developer.android.com/static/images/jetpack/compose/graphics/introduction/compose_coordinate_system_drawing.png" align="right" width="300">
This application heavily relies on the Canvas API for drawing graph components and animating graph algorithms. Combining the Canvas API with Jetpack Compose presents challenges due to the differing UI rendering approaches.
<br>
<br>
The Android coordinate system follows a Cartesian grid, with the top-left corner serving as the origin (0,0). In this system, the X-axis extends horizontally to the right, while the Y-axis extends vertically downward. This coordinate system is essential for accurately positioning and rendering graph components within the application's graphical space.
<br>
<br>
Here are some code snippets demonstrating the drawing of different graph components:

### Drawing a node
```kotlin
@Composable
fun Node(
    value: Int,
    nodeColor: Color,
    position: Offset = Offset.Zero,
    isHighlighted: Boolean = false
) {
    Canvas(modifier = Modifier.size(50.dp)) {
        drawCircle(
            color = nodeColor,
            radius = size.minDimension / 2,
            center = position
        )
        drawIntoCanvas { canvas ->
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 50f
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
            }
            canvas.nativeCanvas.drawText(
                value.toString(),
                position.x,
                position.y - ((textPaint.descent() + textPaint.ascent()) / 2),
                textPaint
            )
        }
    }
}
```

### Connecting two nodes
```kotlin
fun DrawScope.drawLineBetweenNodes(start: Offset, end: Offset, color: Color, strokeWidth: Float) {
    val theta = atan((end.y - start.y) / (end.x - start.x))
    val linePointStart = start + Offset(
        angleSign(start, end)*(25.dp.toPx() * cos(theta)),
        angleSign(start, end)*(25.dp.toPx() * sin(theta))
    )
    val linePointEnd = end - Offset(
        angleSign(start, end)*(25.dp.toPx() * cos(theta)),
        angleSign(start, end)*(25.dp.toPx() * sin(theta))
    )
    drawLine(
        color = color,
        strokeWidth = strokeWidth,
        start = linePointStart,
        end = linePointEnd,
    )
    //...
}

fun angleSign(start: Offset, end: Offset): Float {
    return if (end.x < start.x) -1f else 1f
}
```

# Demo
<img src="https://github.com/Anass001/GraphSimulatorCompose/blob/master/demo.gif" align="left" width="360">
