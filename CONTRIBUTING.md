# Contributing

Thanks for improving Compose Super Charts. The best contributions make chart behavior clearer, more consistent, and easier to test across screen sizes.

## Before You Start

For larger changes, open an issue or discussion first. Small bug fixes, docs updates, and sample app improvements can go straight to a pull request.

By submitting a contribution, you agree that it will be licensed under the same Apache License 2.0 terms as the rest of the project.

A good PR usually includes:

- A short explanation of the chart or behavior being changed.
- Screenshots or a short recording for visual changes.
- Notes about Android, desktop, or iOS testing.
- A focused diff. Avoid mixing formatting, refactors, and feature work.

## Local Checks

Run the checks that match your change:

```bash
./gradlew :compose-super-charts:compileDebugKotlinAndroid
./gradlew :sample-app:compileDebugKotlinAndroid
./gradlew :sample-app:compileKotlinDesktop
```

For the desktop sample:

```bash
./gradlew :sample-desktop:run
```

For Android device testing:

```bash
./gradlew :sample-app:installDebug
```

If you touch iOS-specific code, open `iosApp/iosApp.xcodeproj` and build the sample from Xcode.

## Code Guidelines

- Keep chart data in `models/`.
- Keep chart-level styling in a style config data class.
- Reuse shared pieces such as `TooltipBubble`, `UniversalLegend`, `ChartText`, `ChartDivider`, and `ChartStateView`.
- Keep heavy math out of composables when it can live in `domain/` or a small utility.
- Prefer a configuration option over hardcoded visual behavior.
- Keep public API changes intentional and call them out in the PR description.
- Keep comments useful and short. Prefer readable code over explaining every line.

## Chart Behavior Checklist

When changing a chart, check these before opening a PR:

- Tooltips stay inside the chart bounds at the left, right, top, and bottom edges.
- Legends wrap cleanly and still work when labels are long.
- Empty data does not crash the chart.
- Label text does not overlap badly on small screens.
- Scroll and zoom still work if the chart supports them.
- Light and dark themes both look readable.
- Accessibility semantics describe the chart at a useful high level.

## Adding A Chart

1. Add the data model.
2. Add the style config.
3. Add the chart composable under `components/organisms/`.
4. Add or extend accessibility semantics in `ChartAccessibility`.
5. Reuse shared tooltip, legend, text, divider, and state components where possible.
6. Add a sample screen with controls for the important states.
7. Update the README chart table.
8. Add a changelog entry.

## Pull Request Notes

In the PR description, include:

- What changed.
- Why it changed.
- How you tested it.
- Anything reviewers should look at carefully.

Clear and boring is good here.
