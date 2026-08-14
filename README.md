# Today's Quote

An Android home-screen widget that shows one quote a day — and only one. No feed, no
streaks, no account, no network. You open your phone, you read one line, you get on with it.

It ships with 131 curated quotes, takes as many of your own as you want to add, and comes
with nine widget skins. The default, **Dark Glass**, is a smoked translucent panel with a
hairline edge, built to sit on a dark wallpaper without shouting.

---

## Install it

Grab the APK from **[Releases](../../releases/latest)** — open that page in your phone's
browser, tap `app-release.apk` under *Assets*, and open it once it's downloaded. Android will
ask you to allow installing from your browser; allow it, then tap *Install*.

Requires Android 8.0 or newer. The app is signed with a personal key, not published on the
Play Store, so Play Protect may show a "unknown app" notice the first time; tap
*Install anyway*.

> **Updating later:** as long as new APKs are signed with the same key, they install straight
> over the top and your quotes, favourites and widget settings are kept. If you ever build
> with a *different* key, Android will refuse the update and you'd have to uninstall first —
> so export your quotes from Settings before doing that.

---

## Put a widget on your home screen

1. Long-press an empty area of your home screen.
2. Tap **Widgets**, find **Today's Quote**, and drag it where you want it.
3. The setup screen opens straight away — pick a skin, opacity, size and so on, then tap
   **Add widget**.
4. Drag the widget's handles to resize it. The type resizes with it: a short aphorism gets
   big and bold, a long passage steps down so it still fits.

To restyle a widget later, long-press it and tap the edit (pencil) affordance — that changes
just that widget. Changing anything on the app's **Widgets** tab restyles all of them at once.

---

## The four tabs

**Today** — the day's quote, large. Favourite it, copy it, share it, or tap shuffle to see
another without disturbing what your widget shows.

**Library** — every quote in one list. Search by words or author, filter by theme, and switch
between *All*, *Built-in*, *Mine* and *Favourites*. The **+** button adds your own. Built-in
quotes you don't like can be hidden from the rotation (they're never deleted, so you can
bring them back from Settings).

**Widgets** — the styling surface, with a live preview that renders using the widget's own
backgrounds and type maths. What you see there is what lands on the home screen.

**Settings** — the optional daily notification, library stats, and backup.

---

## Skins

| | |
|---|---|
| **Dark Glass** | Smoked panel, hairline edge. The default, made for a dark wallpaper. |
| **Obsidian** | Near-solid black that disappears into an AMOLED screen. |
| **Ash** | Warm graphite with a soft edge. |
| **Ember** | Dark glass, low amber burn on the author line. |
| **Sage** | Dark glass, muted green accent. |
| **Frost** | Pale glass for light wallpapers. |
| **Paper** | Off-white stock, ink-dark type. |
| **Ink** | No panel at all — just the words on your wallpaper. |
| **Dynamic** | Takes its colour from your wallpaper (Android 12+). |

Each one is adjustable: background opacity, text size, typeface (sans / serif / mono),
left or centred alignment, whether the author line and shuffle button show, and what a tap
on the widget does (open the app, shuffle, or nothing).

---

## Choosing what appears

A widget can draw from everything, the built-in quotes only, your own quotes only, or your
favourites only — and on top of that you can restrict it to any mix of nine themes:
Discipline, Adversity, Solitude, Mortality, Power, Truth, Mind, Craft and Freedom.

If a filter ends up matching nothing, the widget quietly widens it rather than going blank.

---

## Your own quotes

Add them from the **+** button in Library: the text, an author (leave it blank if the words
are yours), and a theme. They're editable and deletable at any time, and they sit in the same
rotation as everything else — or you can point a widget at *My quotes only*.

**Backup** lives in Settings:

- **Export** hands your quotes to any app as JSON — mail them to yourself, drop them in
  Files, whatever. Built-in quotes aren't included; they always come with the app.
- **Import file** reads a JSON backup back in.
- **Paste** takes plain text, one quote per line, in the form `The quote itself — Author`.

Importing skips anything whose text you already have, so re-importing the same backup is
safe.

---

## How the daily quote is chosen

The quote isn't stored, it's *derived* from the date. The day number, the widget's id and how
many times you've shuffled it are combined into a seed, and the seed picks the quote. Two
consequences worth knowing:

- A widget redrawn at any point during the day shows the same quote — resizing it, rebooting,
  or the launcher repainting won't change it.
- Two widgets on the same screen show different quotes, because the widget's id is part of
  the seed.

Rollover is handled three ways so it can't be missed: an alarm just after midnight, the
system's date-changed broadcast, and a slow periodic refresh as a backstop. All three agree
on the same answer, so firing more than once is harmless.

---

## Privacy

There is no network code in the app. No analytics, no accounts, no ads. Everything — your
quotes, favourites and widget settings — lives in the app's own storage on your phone and
goes nowhere else. The only permissions are notifications (which you opt into, and only if
you turn on the daily reminder) and receiving the boot broadcast, so widgets come back
correctly after a restart.

---

## Building it yourself

Requires JDK 17+ and the Android SDK (platform 35).

```bash
./gradlew testDebugUnitTest     # the logic tests
./gradlew assembleDebug         # app/build/outputs/apk/debug/
./gradlew assembleRelease       # app/build/outputs/apk/release/
```

Release builds are signed if a `keystore.properties` sits in the project root:

```properties
storeFile=todays-quote.jks
storePassword=…
keyAlias=…
keyPassword=…
```

Without it the release build falls back to the local debug key, so the APK still installs.
The keystore and `keystore.properties` are both git-ignored — keep them somewhere safe, since
losing the key means future builds can no longer update an installed copy.

GitHub Actions builds every push and uploads the APK as a workflow artifact. Push a `v*` tag
and it attaches the APK to a GitHub release instead. To have CI sign with your key, add four
repository secrets: `SIGNING_KEYSTORE_BASE64` (`base64 -w0 todays-quote.jks`),
`SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS` and `SIGNING_KEY_PASSWORD`.

### Layout

```
app/src/main/java/com/itzsuli/todaysquote/
  data/        quotes, storage, the daily picker, text sizing
  widget/      the app widget: provider, renderer, per-widget settings, config screen
  notify/      the optional daily notification
  ui/          the Compose app — Today, Library, Widgets, Settings
```

The widget is drawn with `RemoteViews` rather than Glance, which is what makes the glass
skins possible: the backgrounds are ordinary shape drawables whose opacity is set at runtime,
and the in-app preview renders those same drawables so the two can't drift apart.

---

## About the quotes

The built-in set was chosen for weight rather than popularity — Marcus Aurelius, Seneca,
Epictetus, Nietzsche, Kafka, Camus, Rilke, Baldwin, Jung, Musashi, Gracián, Bukowski,
Le Guin, Morrison, Dostoevsky and others. Poster-slogan material and lines worn smooth by
overuse were deliberately left out, as was anything whose attribution wouldn't survive
checking. Where a quote is a well-known translation or a condensed passage, the wording
follows the standard English rendering.

If one of them still doesn't earn its place, hide it — Library → the quote → *Hide from
rotation*.
