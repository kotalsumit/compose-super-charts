# Compose Super Charts

Compose Super Charts is a Kotlin Multiplatform charting library built with Compose. It keeps chart APIs readable, puts styling into configuration objects, and shares chart components across Android, iOS, and desktop targets.

The library is being prepared for release. The sample app is the quickest way to explore the available charts, compare behavior across platforms, and find useful areas to contribute.

## What's Included

| Chart | Supports |
| --- | --- |
| Line chart | Multiple series, solid/dashed lines, solid/hollow points, optional area fill, tappable legends, tooltips, scrolling, label rotation, value formatting |
| Area chart | Filled line chart behavior with the same data model and styling approach as line charts |
| Column chart | Standard, clustered, and stacked columns, top/bottom x-axis, tappable legends, value labels, scrolling, rotated labels, tooltips |
| Bar chart | Standard, clustered, and stacked horizontal bars, left/right y-axis, tappable legends, value labels, scrolling, tooltips |
| Pie chart | Pie and doughnut styles, center label/value, slice spacing, active slice offset, legends, labels, tooltips |
| Combined chart | Column and line values in one view for comparing two related measures |
| Range chart | Horizontal start/end ranges for timelines, schedules, and interval data |
| Pyramid chart | Pyramid and funnel layouts, segment labels, legends, tooltips |
| Gauge chart | Configurable arc range, value arc animation, needle, labels, tooltip support |
| Bubble chart | Bubble position, size, color, zoom, scroll, grid lines, rotated labels, tooltips |
| Scatter chart | Multiple series, per-point styling, zoom, scroll, grid lines, crosshair, tooltips |
| Heatmap | Row/column cells, color interpolation, zoom, scroll, rotated column labels, tooltips |
| Radar chart | Single or multiple series, polygon/circle web, animated drawing, point tooltips |
| Candlestick chart | OHLC rendering, bullish/bearish styling, optional scrolling, configurable candle width, OHLC tooltips |
| Venn diagram | Set circles, intersections, labels, animated rendering, tooltips |

## Shared Features

- Android, iOS, and desktop JVM targets.
- Canvas-based chart rendering.
- Per-chart data and style configuration objects.
- Shared tooltip component with edge clamping, optional close button, and auto-dismiss timing.
- Shared legends with top, bottom, and hidden positions.
- Optional legend item toggling for supported multi-series charts.
- Optional value labels and formatter callbacks where the chart supports them.
- Empty, loading, and error state helpers through `ChartDisplayState` and `ChartStateView`.
- Accessibility description helpers for chart semantics.
- Sample screens for every chart type, including controls for common configuration options.

## Project Layout

```text
compose-super-charts/
  compose-super-charts/   Core chart library module
  sample-app/             Shared Android/iOS sample app
  sample-desktop/         Desktop sample launcher
  iosApp/                 Xcode project for the iOS sample
```

Library code is grouped by responsibility:

```text
components/atoms/         Small reusable UI pieces
components/molecules/     Shared chart pieces such as legends, tooltips, and state views
components/organisms/     Complete chart composables
models/                   Data and style configuration objects
utils/                    Accessibility, formatting, modifiers, and export helpers
domain/                   Chart math helpers
```

## Running The Project

From the repository root:

```bash
./gradlew :compose-super-charts:compileDebugKotlinAndroid
./gradlew :sample-app:compileDebugKotlinAndroid
./gradlew :sample-app:compileKotlinDesktop
```

Run the desktop sample:

```bash
./gradlew :sample-desktop:run
```

Install the Android sample:

```bash
./gradlew :sample-app:installDebug
```

Open the iOS sample from `iosApp/iosApp.xcodeproj` after the Kotlin Multiplatform project has synced.

## Installation

After the first Maven Central release is available, add:

```kotlin
dependencies {
    implementation("io.github.kotalsumit:compose-super-charts:1.0.0")
}
```

Until the Maven Central publication is live, add the module locally:


```kotlin
include(":compose-super-charts")
```

Then depend on it from your Compose module:

```kotlin
dependencies {
    implementation(project(":compose-super-charts"))
}
```

## Publishing

The library is configured for Maven Central publishing through the Central Portal.

Before publishing, create and verify the `io.github.kotalsumit` namespace in Maven Central, then add these GitHub Actions secrets:

```text
MAVEN_CENTRAL_USERNAME
MAVEN_CENTRAL_PASSWORD
SIGNING_KEY_ID
SIGNING_PASSWORD
GPG_KEY_CONTENTS
```

The release workflow runs when a GitHub release is published. It uploads artifacts to Maven Central, where the deployment can be reviewed and released.

## Example

```kotlin
val points = listOf(
    ChartPointData(
        xLabel = "Mon",
        yValues = listOf(24f),
        highlightLabels = listOf(TooltipBubbleData("Sales", "24"))
    ),
    ChartPointData(
        xLabel = "Tue",
        yValues = listOf(42f),
        highlightLabels = listOf(TooltipBubbleData("Sales", "42"))
    ),
    ChartPointData(
        xLabel = "Wed",
        yValues = listOf(31f),
        highlightLabels = listOf(TooltipBubbleData("Sales", "31"))
    )
)

LineChart(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp),
    points = points,
    maxY = 5,
    yAxisLabel = "Sales",
    legendLabels = listOf("Revenue"),
    config = ChartStyleConfig(
        lines = listOf(
            ChartLineConfig(
                lineStyle = SolidLine(color = Color(0xFF2563EB)),
                pointStyle = SolidPoint()
            )
        ),
        legendPosition = LegendPosition.BOTTOM,
        xAxisLabelRotation = -35f,
        showTooltipCloseButton = true,
        tooltipAutoDismissMs = 2500
    )
)
```

## Contributing

Small, focused pull requests are easiest to review. Before opening one:

1. Run the relevant compile commands.
2. Test the changed chart in the sample app.
3. Include screenshots or a short recording for visual changes.
4. Mention Android, desktop, or iOS testing in the PR.
5. Keep public API changes intentional and easy to spot.

When adding a chart, follow the existing shape:

1. Add data models in `models/`.
2. Add a style config in `models/`.
3. Add the composable in `components/organisms/`.
4. Reuse `TooltipBubble`, `UniversalLegend`, `ChartText`, and `ChartDivider` when possible.
5. Add accessibility semantics through `ChartAccessibility`.
6. Add a sample screen with controls for the important options.
7. Update this README and the changelog.

## Current Priorities

- Keep tooltip behavior consistent near chart edges.
- Keep sample screens useful for manual testing.
- Improve accessibility coverage as interactions become richer.
- Add tests around chart math and configuration edge cases.
- Prepare publishing and release packaging.

## License

Apache License 2.0. See [LICENSE](LICENSE).
