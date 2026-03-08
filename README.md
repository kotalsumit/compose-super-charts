# 📊 Compose Super Charts

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-orange.svg?style=flat&logo=jetpack-compose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A polished, highly customizable charting library for **Compose Multiplatform** (Android, iOS, Desktop). Built with love and Atomic Design principles.

> **Note**: This library is currently in **Alpha**. We're actively refining the API and adding more chart types. Feedback is very welcome!

---

## Why Super Charts?

Most charting libraries are either too rigid or too complex to style properly for different platforms. **Super Charts** was born from a need for a unified API that feels native everywhere but remains "dead-simple" to customize.

- **🚀 Multiplatform First**: Single codebase for UI, logic, and math.
- **🎨 Ultimate Customizability**: Control every pixel—colors, fonts, sizes, and animations.
- **⚛️ Atomic Design**: Built using the Atomic Design hierarchy (Atoms → Molecules → Organisms).
- **🪶 Lightweight**: Minimal dependencies, focus on performance and `Canvas` rendering.

---

## 📸 Sneak Peek

| Line Chart | Pie Chart | Column Chart |
| :---: | :---: | :---: |
| ![Line](/docs/images/line_sample.png) | ![Pie](/docs/images/pie_sample.png) | ![Column](/docs/images/column_sample.png) |

---

## 🏁 Quick Start

### 1. Add Dependency (Coming Soon)

For now, you can clone the repository and include the `:compose-super-charts` module in your project.

### 2. Basic Usage (Line Chart)

```kotlin
val points = listOf(
    ChartPointData("Mon", listOf(20f)),
    ChartPointData("Tue", listOf(45f)),
    ChartPointData("Wed", listOf(30f))
)

LineChart(
    modifier = Modifier.fillMaxWidth().height(300.dp),
    points = points,
    maxY = 50,
    yAxisLabel = "Sales",
    config = ChartStyleConfig(
        lines = listOf(ChartLineConfig(lineStyle = SolidLine(color = Color.Blue)))
    )
)
```

---

## 🛠 Project Structure

- `compose-super-charts/`: The core library.
- `sample-app/`: Shared sample application for Android/iOS/Desktop.
- `sample-desktop/`: Desktop-specific launcher.
- `iosApp/`: iOS-specific Xcode project and SwiftUI wrappers.

---

## 🤝 Contributing

We love contributions! Whether it's a bug fix, a new chart type (Radar? Bubble?), or just a typo correction in the docs, feel free to open a PR.

Check out our [Contributing Guide](CONTRIBUTING.md) to get started.

---

## 📄 License

```text
Copyright 2026 Sumit Kotal

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
```
*See full [LICENSE](LICENSE) for details.*

---

*Built with ❤️ by the Super Charts Community.*
