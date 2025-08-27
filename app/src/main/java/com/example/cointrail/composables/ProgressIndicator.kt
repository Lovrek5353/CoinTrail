package com.example.cointrail.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cointrail.ui.theme.CoinTrailTheme

@Composable
fun ProgressIndicator(
    progress: Double,
    maxProgress: Double,
    modifier: Modifier = Modifier
) {
    val strokeWidth = 12.dp

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Calculate dimensions
    val width = screenWidth * 0.8f
    val horizontalPadding = screenWidth * 0.1f
    val height = screenHeight / 5

    val progressFraction = if (maxProgress > 0) (progress / maxProgress).coerceIn(0.0, 1.0) else 0.0

    // Get colors inside the composable body
    val backgroundArcColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val progressArcColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val shape = MaterialTheme.shapes.medium

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(start = horizontalPadding, end = horizontalPadding)
            .background(surfaceColor, shape = shape),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = (canvasWidth - strokeWidth.toPx()) / 2
            val center = Offset(x = canvasWidth / 2, y = canvasHeight)

            // Draw background arc (half circle)
            drawArc(
                color = backgroundArcColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Draw progress arc
            drawArc(
                color = progressArcColor,
                startAngle = 180f,
                sweepAngle = 180f * progressFraction.toFloat(),
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        // Centered value text, moved down slightly
        Text(
            text = "${progress.toInt()}",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = height / 5) // moved down slightly more than before
        )
    }
}

@Preview
@Composable
fun ProgressIndicatorPreview() {
    CoinTrailTheme {
        ProgressIndicator(progress = 30.0, maxProgress = 100.0)
    }
}
