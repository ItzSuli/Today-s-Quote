# Changelog

All notable changes to Today's Quote are recorded here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and versions follow
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] — 2026-08-14

### Added

- **A Strategy category, and 30 more quotes to fill it.** Nineteen are Sun Tzu, quoted from
  Lionel Giles' translation of *The Art of War*, alongside Clausewitz, Thucydides, Vegetius,
  more Musashi, Hagakure and Patton. The library is now 161 quotes across ten themes, and a
  widget can be pointed at Strategy alone.
- A test asserting every category has real quotes behind it, so a filter chip can never appear
  that silently matches nothing.

### Changed

- Two Sun Tzu lines already in the library — "In the midst of chaos, there is also
  opportunity" and "Opportunities multiply as they are seized" — are widely circulated but
  appear nowhere in the standard translation. Both were replaced with passages that are
  actually in the text.

## [1.0.2] — 2026-08-14

### Fixed

- **Widgets could not be added to the home screen.** Dropping one left the cell reserved with
  "couldn't create widget" in it. The author line's accent rule was a plain `<View>`, and
  RemoteViews will only inflate view classes annotated `@RemoteView` — `android.view.View`
  is not one, so the launcher rejected the entire layout with
  `Class not allowed to be inflated android.view.View`. The rule is now an `ImageView`, which
  looks identical and is permitted.

### Added

- Widget inflation tests that apply the real RemoteViews through the framework's own
  `apply()`, across every skin, typeface, alignment, tap action, source and both ends of the
  resize range. A RemoteViews only fails when the *launcher* inflates it, so building one in a
  test proved nothing — these actually inflate it, and they also exercise every runtime action
  the widget performs, which catches non-remotable setters as well as illegal view classes.

## [1.0.1] — 2026-08-14

### Fixed

- **The app crashed immediately on open and could not be used at all.** Every widget skin is
  a `<shape>` drawable — a gradient fill with a hairline stroke and rounded corners — and the
  Today screen drew one through Compose's `painterResource`, which accepts only vector
  drawables and bitmaps and throws `IllegalArgumentException` on anything else. Because the
  Today screen is the start destination, the exception landed before the first frame. Skin
  backgrounds are now handed to the platform to draw onto the Compose canvas, which also makes
  the preview pixel-identical to the widget rather than merely similar.
- The theme swatches in the Widgets tab hit the same fault and would have crashed that screen
  independently.
- Preview opacity now uses the widget's own 0–255 alpha maths instead of a separate float, so
  the opacity slider and the real widget agree exactly.

### Added

- Instrumented launch tests running on the JVM, composing every screen and every skin at a
  range of opacities. The 1.0.0 crash was invisible to the existing logic tests; this class of
  failure can no longer reach a release.

## [1.0.0] — 2026-08-14

Initial release. **Withdrawn — crashes on launch, see 1.0.1.**

### Added

- A home-screen widget showing one quote a day, derived from the date rather than stored, so
  it stays put through redraws and resizes and rolls over at midnight. Rollover is driven by
  an alarm, the system date-changed broadcast and a periodic backstop.
- 131 curated built-in quotes across nine themes: Discipline, Adversity, Solitude, Mortality,
  Power, Truth, Mind, Craft and Freedom.
- Your own quotes, with add, edit and delete, plus favourites and the ability to hide built-in
  quotes from the rotation without losing them.
- Nine widget skins — Dark Glass, Obsidian, Ash, Ember, Sage, Frost, Paper, Ink and Dynamic —
  each with adjustable opacity, text size, typeface, alignment, author line, shuffle button
  and tap action, configurable per widget.
- Per-widget source filters: everything, built-in only, your own only, or favourites only,
  further narrowed by any mix of themes. An over-tight filter widens rather than going blank.
- A four-tab app: Today, Library, Widgets and Settings.
- An optional once-a-day notification at a time you choose.
- Backup: export your quotes as JSON, import JSON back, or paste plain
  `The quote itself — Author` lines.

[1.1.0]: https://github.com/ItzSuli/Today-s-Quote/releases/tag/v1.1.0
[1.0.2]: https://github.com/ItzSuli/Today-s-Quote/releases/tag/v1.0.2
[1.0.1]: https://github.com/ItzSuli/Today-s-Quote/releases/tag/v1.0.1
[1.0.0]: https://github.com/ItzSuli/Today-s-Quote/releases/tag/v1.0.0
