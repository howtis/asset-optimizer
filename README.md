# asset-optimizer

A Gradle plugin for static asset optimization in Spring Boot / JVM web applications.

Minifies CSS, JavaScript, and HTML. Optimizes PNG, JPEG, and SVG files. Converts images to WebP.
Uses [esbuild](https://esbuild.github.io/), [oxipng](https://github.com/shssoichiro/oxipng), and [cwebp](https://developers.google.com/speed/webp) with zero runtime dependencies — native binaries are bundled inside the plugin JAR.

## Quick Start

Apply the plugin and place your assets under `src/main/resources/static/`:

```bash
./gradlew build
```

All supported file types are automatically optimized and included in the output JAR.

## Features

- CSS minification via esbuild (`**/*.css`)
- JavaScript minification via esbuild (`**/*.js`)
- HTML minification with Thymeleaf support (`**/*.html`) — pure Java, no native binary
- PNG lossless optimization via oxipng (`**/*.png`)
- JPEG metadata stripping (`**/*.jpg`, `**/*.jpeg`) — pure Java
- SVG optimization — removes comments, metadata, empty groups (`**/*.svg`) — pure Java
- WebP conversion via cwebp (converts PNG, JPEG to WebP)
- Automatic `src/main/resources/static/` scanning
- Hooks into `processResources` — no extra task needed
- Platform-specific native binary bundling

## Configuration

```groovy
assetOptimizer {
    sourceDirs = files("src/main/resources/static")
    outputDir = layout.buildDirectory.dir("asset-optimizer")
}
```

## Tasks

| Task            | Description                          | Input                  |
|-----------------|--------------------------------------|------------------------|
| `minifyCss`     | Minifies CSS files                   | `**/*.css`             |
| `minifyJs`      | Minifies JavaScript files            | `**/*.js`              |
| `minifyHtml`    | Minifies HTML files                  | `**/*.html`            |
| `optimizePng`   | Lossless PNG optimization            | `**/*.png`             |
| `optimizeSvg`   | SVG optimization                     | `**/*.svg`             |
| `optimizeJpeg`  | JPEG metadata stripping              | `**/*.jpg`, `**/*.jpeg`|
| `convertWebp`   | Converts PNG/JPEG to WebP            | `**/*.png`, `**/*.jpg` |

All tasks run automatically as dependencies of `processResources`.

## How It Works

1. At runtime, the plugin detects the current OS and architecture, extracts the matching native binary (esbuild, oxipng, or cwebp) to a temp directory, and uses it for optimization.
2. The `processResources` task is extended with all optimization tasks — no extra configuration required for basic use.
3. HTML, SVG, and JPEG optimizations use pure Java implementations — no native binaries required for these.

## Requirements

- Java 17+

## License

MIT — see [LICENSE](LICENSE) for details.