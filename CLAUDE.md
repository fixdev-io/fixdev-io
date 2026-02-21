# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Static single-page portfolio website for FixDev (backend engineering services). The entire site is contained in one file: `index-v6.html` (~51KB) with inline CSS and JavaScript.

## Development

No build system, package manager, or dependencies. To preview locally:

```bash
# Python
python3 -m http.server 8080

# Node (if http-server is installed)
npx http-server .
```

Open `index-v6.html` directly in a browser or via a local server.

## Architecture

**Single-file structure** — all HTML, CSS (~650 lines), and JavaScript (~160 lines) are inline in `index-v6.html`.

### CSS
- CSS Custom Properties in `:root` for theming (dark theme, accent colors `#00e5a0` green, `#0099ff` blue)
- Fonts: JetBrains Mono (monospace), Syne (display), Instrument Serif (accent) via Google Fonts
- Responsive breakpoint at `max-width: 1024px` with `clamp()` for fluid typography
- CSS Grid + Flexbox layouts; `@keyframes` animations for fade/reveal effects

### JavaScript (Vanilla ES6+, no frameworks)
Three modules:
1. **Translation system** — i18n for RU (default) and EN using `data-t` attributes on DOM elements; language switcher with fade transition
2. **Custom cursor** — dot + ring following mouse via `requestAnimationFrame`; ring expands on interactive element hover
3. **Scroll system** — `IntersectionObserver` for reveal animations, nav blur/styling on scroll, smooth anchor scrolling

### Page Sections
Navigation → Hero (with animated SVG visualization) → About → Services (3-column grid) → Cases (2-column grid with metrics) → Blog (asymmetric grid) → Contact (form + links) → Footer

### Key Patterns
- Translation keys: add `data-t="keyName"` attribute to elements; define translations in the JS `translations` object for both `ru` and `en`
- Reveal animations: elements with `.reveal` class get `.visible` added when they enter viewport via IntersectionObserver
- State is managed through CSS class toggling (`.active`, `.visible`, `.scrolled`, `.lang-switching`)
