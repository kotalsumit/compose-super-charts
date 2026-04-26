# Contributing

Thanks for taking the time to improve Compose Super Charts. The project is still alpha, so the most useful contributions are the ones that make behavior clearer, more consistent, and easier to test.

## Before You Start

For larger changes, open an issue or discussion first. A small bug fix, documentation cleanup, or sample improvement can go straight to a pull request.

Good contributions usually include:

- A short description of the chart or behavior being changed.
- Screenshots or screen recordings for visual changes.
- Notes about Android, desktop, or iOS testing if you were able to run them.
- A focused diff. Avoid mixing formatting, refactors, and feature work in one PR.

## Local Checks

Run these from the repository root:

```bash
./gradlew :compose-super-charts:compileDebugKotlinAndroid
./gradlew :sample-app:compileDebugKotlinAndroid
```

If you change desktop-specific behavior, also run:

```bash
./gradlew :sample-desktop:run
```

## Code Guidelines

- Keep chart data in `models/`.
- Keep chart-level styling in a style config data class.
- Reuse shared pieces such as `TooltipBubble`, `UniversalLegend`, `ChartText`, and `ChartDivider`.
- Keep heavy math out of composables when it can live in `domain/` or a small utility.
- Prefer a configuration option over hardcoded visual behavior.
- Be careful with public API changes; this library is alpha, but people may still be testing against it.

## Adding A Chart

1. Add the data model.
2. Add the style config.
3. Add the chart composable under `components/organisms/`.
4. Add accessibility semantics or extend `ChartAccessibility`.
5. Add a sample screen with enough controls to test the important states.
6. Update the README feature table.

## Pull Request Notes

In the PR description, include:

- What changed.
- Why it changed.
- How you tested it.
- Anything reviewers should look at carefully.

That is enough. Clear and boring is good here.
