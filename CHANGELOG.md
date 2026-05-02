# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-alpha01] - 2026-03-12

### Added
- Initial release of **Compose Super Charts**.
- Support for **Android**, **iOS**, and **Desktop** (JVM).
- Fundamental Chart Types:
    - Line Chart (Multi-line, animated, scrollable).
    - Pie Chart (Doughnut, Exploded, Spie).
    - Column & Bar Charts (Clustered, Stacked).
    - Pyramid & Funnel.
    - Bubble & Scatter Charts.
    - Venn Diagram.
    - Gauge Chart.
    - Heatmap.
    - Radar Chart.
    - Candlestick Chart.
- Global Light/Dark theme support.
- Atomic Design structure (Atoms, Molecules, Organisms).
- Customization hooks for colors, fonts, sizes, labels, legends, tooltips, and animation timing.

### Fixed
- Linkage issues for iOS framework distribution.
- Desktop sample compilation errors.
- Tooltip clipping in edge cases.

## Unreleased

### Added
- Area chart wrapper for filled line charts.
- Combined column and line chart.
- Range chart for interval-style data.
- Shared chart state view for empty, loading, and error states.
- Optional value labels for Bar and Column charts.
- Optional legend item toggling for Line, Bar, and Column charts.
- Pie chart center label and center value.
- Formatter callbacks for supported chart values and tooltips.
- Scatter chart crosshair rendering for selected points.
- Android system back handling in the sample app.

### Improved
- Shared tooltip edge clamping across chart implementations.
- Candlestick width calculation when a fixed y-axis is present.
