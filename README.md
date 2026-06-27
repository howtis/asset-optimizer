# asset-optimizer

> 🚧 **Work in Progress** — v1.0.0 development ongoing

A Gradle plugin for static asset optimization in Spring Boot / JVM web applications.
Minifies CSS and JavaScript using [esbuild](https://esbuild.github.io/) with zero runtime dependencies — binaries are bundled inside the plugin JAR.

## Quick Start

Place your assets under `src/main/resources/static/` and run:

```bash
./gradlew build
```

CSS (`**/*.css`) and JavaScript (`**/*.js`) files are automatically minified and included in the output JAR.

## Features

- [x] CSS minification via esbuild
- [x] JavaScript minification via esbuild
- [x] Automatic `src/main/resources/static/` scanning
- [x] Hooks into `processResources` — no extra task needed
- [x] Platform-specific native binary bundling (Windows, Linux, macOS — x64 & arm64)
- [ ] PNG lossless optimization via oxipng
- [ ] Groovy DSL extension for customization

## Supported Platforms

| Platform    | Architecture |
|-------------|-------------|
| Windows     | x64         |
| Linux       | x64, arm64  |
| macOS       | x64, arm64  |

## How It Works

1. At runtime, the plugin detects the current OS and architecture, extracts the matching binary to a temp directory, and uses it for minification.
2The `processResources` task is extended with `MinifyCssTask` and `MinifyJsTask` — no configuration required for basic use.

## Requirements

- Java 17+

## License

MIT — see [LICENSE](LICENSE) for details.

---