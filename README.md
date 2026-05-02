# Compose Super Charts

Compose Super Charts is a Kotlin Multiplatform charting library built with Compose. The goal is simple: keep the chart APIs easy to read, keep styling in configuration objects, and make the same chart components usable from Android, iOS, and desktop targets.

The project is still moving quickly, so treat the API as alpha. The sample app is the best place to check current behavior before depending on a chart in production.

## What Is Included

### Chart Types

| Chart | Current support |
| --- | --- |
| Line chart | Multiple series, solid/dashed lines, solid/hollow points, optional gradient fill, tappable legends, tooltips, horizontal scrolling, x-axis label rotation, value unit formatting |
| Area chart | Line chart API with area fill enabled by default |
| Combined chart | Column and line values in one chart for quick comparison |
| Range chart | Horizontal interval/range bars for timelines, schedules, and min/max values |
| Column chart | Standard, clustered, and stacked columns, top/bottom x-axis, tappable legends, value labels, scrolling, rotated x-axis labels, tooltips |
| Bar chart | Standard, clustered, and stacked horizontal bars, left/right y-axis, tappable legends, value labels, scrolling, rotated labels, tooltips |
| Pie chart | Pie and doughnut styles, center label/value, slice spacing, active slice offset, legends, labels, slice tooltips |
| Gauge chart | Configurable arc range, value arc animation, needle, range labels, tooltip support |
| Pyramid chart | Pyramid and funnel layouts, labels, legends, segment tooltips |
| Radar chart | Single or multiple series, polygon/circle web, animated drawing, point tooltips |
| Bubble chart | Bubble size/color data, scroll and zoom, grid lines, rotated x-axis labels, tooltips |
| Scatter chart | Multiple series, per-point styling, scroll and zoom, grid lines, rotated x-axis labels, tooltips |
| Heatmap | Row/column cell data, color interpolation, scroll and zoom, column label rotation, cell tooltips |
| Candlestick chart | OHLC rendering, bullish/bearish styles, optional scrolling, configurable candle width, rotated x-axis labels, OHLC tooltips |
| Venn diagram | Set circles, intersections, labels, animated rendering, tooltips |

### Shared Features

- Compose Multiplatform targets: Android, iOS, and desktop JVM.
- Canvas-based drawing for the chart bodies.
- Per-chart style config data classes for colors, text styles, sizing, labels, legends, tooltips, and animation duration.
- Shared tooltip component with optional close button and auto-dismiss timing.
- Shared legend positioning for charts that support legends: `TOP`, `BOTTOM`, and `HIDDEN`.
- Optional legend item toggling for supported multi-series charts.
- Optional value formatter callbacks for supported tooltips and labels.
- Empty, loading, and error state view helpers.
- Accessibility semantics helpers for chart descriptions.
- Scroll and zoom support where the chart type benefits from it.
- Alpha-stage sample screens for each chart type.

## Project Layout

```text
compose-super-charts/
  compose-super-charts/   Core library module
  sample-app/             Shared sample app used by Android and iOS entry points
  sample-desktop/         Desktop launcher
  iosApp/                 Xcode project for the iOS sample
```

The library code is grouped by rough UI responsibility:

```text
components/atoms/         Small reusable UI pieces such as text and dividers
components/molecules/     Shared chart UI pieces such as legends and tooltips
components/organisms/     Complete chart composables
models/                   Data and style configuration objects
utils/                    Accessibility, modifiers, formatting, and helper utilities
domain/                   Chart math helpers
```

## Running The Project

From the repository root:

```bash
./gradlew :compose-super-charts:compileDebugKotlinAndroid
./gradlew :sample-app:compileDebugKotlinAndroid
```

Desktop sample:

```bash
./gradlew :sample-desktop:run
```

Android sample:

```bash
./gradlew :sample-app:installDebug
```

iOS can be opened from `iosApp/iosApp.xcodeproj` after Gradle has synced the Kotlin Multiplatform project.

## Using The Library Locally

There is no published Maven artifact yet. For now, include the module in your project:

```kotlin
include(":compose-super-charts")
```

Then depend on it from your Compose module:

```kotlin
dependencies {
    implementation(project(":compose-super-charts"))
}
```

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
    maxY = 50,
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
        animationDuration = 500,
        showTooltipCloseButton = true,
        tooltipAutoDismissMs = 2500
    )
)
```

## Contributing

Contributions are welcome, especially fixes that make the charts more predictable across different screen sizes. Small, focused pull requests are easiest to review.

Before opening a PR:

1. Run the library and sample compile commands above.
2. Test the chart you changed in the sample app.
3. Keep public API changes intentional and mention them clearly in the PR.
4. Prefer adding options to a chart's style config over hardcoding visual behavior.
5. Keep chart math in `domain/` or small helpers when it starts to crowd a composable.

When adding a chart, follow the existing shape:

1. Add data models in `models/`.
2. Add a style config in `models/`.
3. Add the composable in `components/organisms/`.
4. Reuse `TooltipBubble`, `UniversalLegend`, `ChartText`, and `ChartDivider` where possible.
5. Add a sample screen so the chart can be tested manually.

## Current Priorities

- Keep tooltip and legend behavior consistent across chart types.
- Make sample screens useful for testing real options, not just showing happy paths.
- Improve accessibility descriptions as chart interactions become richer.
- Add tests around chart math and configuration edge cases.
- Prepare a stable publishing setup once the API settles.

## License

Apache License 2.0. See [LICENSE](LICENSE).
