package com.composesupercharts.utils

/**
 * Common entry point reserved for chart export work.
 *
 * Chart capture needs platform-specific code because Android, iOS, and desktop
 * expose different rendering surfaces. Keep callers behind this utility while
 * the expect/actual implementation is designed.
 */
object ExportUtils {
    
    /**
     * Android: Use `GraphicsLayer` or `DrawingCache`.
     * iOS: Use `UIView.drawHierarchy` or `UIGraphicsImageRenderer` on the Compose host view.
     * Desktop: Use `ComposeScene.render` or `SKImage`.
     */
    fun captureChartAsImage(
        fileName: String = "chart_export.png"
    ) {
        println("Exporting chart to $fileName...")
    }
}
