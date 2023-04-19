package com.pixelwave.graphsimulatorcompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Node(value: Int, nodeColor: Color, position: Offset = Offset.Zero) {
    Canvas(modifier = Modifier.size(50.dp)) {
        drawCircle(
            color = nodeColor,
            radius = size.minDimension / 2,
            center = position
        )
        drawIntoCanvas { canvas ->
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLUE
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 50f
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

@Composable
@Preview
fun NodePreview() {
    Node(1, Color.White)
}