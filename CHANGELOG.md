# Changelog

This file tracks release-ready changes in Compose Super Charts. Keep entries short, specific, and useful for people deciding what to test or contribute next.

## 1.1.0 - 2026-05-30

### Charts
- Added area chart support using the line chart data model with fill enabled by default.
- Added combined chart support for column and line values in one view.
- Added range chart support for start/end interval data.
- Completed sample screens for line, area, column, bar, pie, combined, range, pyramid, gauge, bubble, scatter, heatmap, radar, candlestick, and Venn charts.

### Interaction
- Added shared tooltip edge clamping across chart implementations.
- Improved tooltip placement near right edges and lower chart rows.
- Added optional tooltip close buttons and auto-dismiss timing.
- Added custom tooltip content hooks for line-style charts.
- Added optional legend item toggling for supported multi-series charts.
- Added custom legend item and marker rendering hooks for line-style charts.
- Added optional value labels for supported bar, column, and combined chart views.
- Added scatter chart crosshair rendering for selected points.
- Added pie chart center label and center value options.
- Added formatter callbacks for supported chart values, labels, and tooltips.

### Layout And Accessibility
- Added shared chart state helpers for empty, loading, and error states.
- Added accessibility descriptions for chart semantics, including area, combined, and range charts.
- Added null-gap handling for line and area charts so missing values do not fall to zero.
- Added separate axis, tooltip, x-axis, and accessibility formatter hooks for line-style charts.
- Added explicit null point and area fill behavior policies for line and area charts.
- Improved candlestick width and y-axis spacing when the y-axis is visible.
- Improved range chart tooltip anchoring for bottom rows and reversed intervals.
- Improved combined chart value label placement near the top edge.
- Fixed bar chart tooltip clipping near the bottom of the chart.

### Sample App
- Added Android system back handling so chart screens return to the gallery.
- Updated the chart gallery with descriptions and distinct chart icons.
- Added one-line descriptions to chart demo screens.
- Moved chart descriptions below the screen title and before chart controls.
- Fixed the light/dark theme toggle layout on the home screen.
- Replaced deprecated auto-mirrored icon usage in sample navigation.

### Documentation
- Refreshed the README to match the current chart list and project structure.
- Updated contributor notes with chart behavior checks and testing guidance.
- Fixed outdated wording and stale release notes.
- Added Maven Central publishing setup and installation notes.

## Earlier Work

### Charts
- Added line chart support with multiple series, animation, tooltips, and scrolling.
- Added pie and doughnut chart support.
- Added column and bar charts with clustered and stacked modes.
- Added pyramid and funnel chart support.
- Added bubble and scatter chart support.
- Added Venn diagram support.
- Added gauge chart support.
- Added heatmap support.
- Added radar chart support.
- Added candlestick chart support.

### Platform
- Added Android, iOS, and desktop JVM target support.
- Added light and dark theme support in the sample app.
- Organized the library around atoms, molecules, organisms, models, utilities, and chart math helpers.
- Added customization hooks for colors, fonts, sizes, labels, legends, tooltips, and animation timing.

### Fixes
- Fixed iOS framework linkage issues.
- Fixed desktop sample compilation issues.
- Fixed early tooltip clipping cases.
