# Where Is It — Android App

A simple Android app (Kotlin + Jetpack Compose + Room) to track where you put things
like keys, spectacles, wallet, hammer, etc.

## Features
1. **Categories** — Create / Edit / Delete categories (e.g. "Tools", "Electronics", "Personal").
2. **Items** — Create / Edit / Delete items, each optionally tagged with a category.
3. **Capture** — Take a photo of the storage spot (drawer, shelf, box…) and pick which
   item(s) from your list live there. You can revisit a saved place later to change the
   photo's label or which items are assigned to it.
4. **Search** — Type an item's name and instantly see the photo of where it's stored,
   plus its category.

## Getting an installable APK via GitHub (no Android Studio needed)

This project includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`)
that builds a debug APK automatically and lets you download it.

1. Create a new **empty** repository on [github.com](https://github.com) (any name, e.g. `where-is-it`).
2. Upload this whole `ItemFinder` folder's contents to that repo. Easiest way with no
   command line:
   - On the repo page, click **Add file → Upload files**.
   - Drag the *contents* of the `ItemFinder` folder in (including the hidden `.github`
     folder — if your OS hides it, use "show hidden files", or just drag the whole
     `ItemFinder` folder if your upload tool supports folders).
   - Commit directly to the `main` branch.
3. Go to the **Actions** tab of your repo. A workflow run called "Build APK" should
   start automatically (it triggers on every push to `main`).
4. Wait for it to finish (a few minutes, green checkmark).
5. Click into the finished run, scroll to **Artifacts**, and download
   `where-is-it-debug-apk` — it's a zip containing `app-debug.apk`.
6. Transfer `app-debug.apk` to your phone (email it to yourself, Google Drive, USB, etc.),
   open it on the phone, and tap install. Android will prompt you to allow "Install
   unknown apps" for whichever app you use to open it — allow it, then install.

This is a **debug-signed** APK — perfectly fine for installing on your own device, just
not for publishing to the Play Store (that needs a separate release signing step).

## How to open it in Android Studio instead
1. Install **Android Studio** (Koala or newer) if you don't have it.
2. Choose **Open** and select this project's root folder (`ItemFinder`).
3. Let Android Studio sync Gradle (it will download the Gradle distribution and
   dependencies automatically the first time — this needs internet access).
4. Run the `app` configuration on an emulator or a physical device (minSdk 24 / Android 7.0+).

No API keys or backend needed — everything (photos + data) is stored locally on the
device using a Room (SQLite) database and the app's private file storage.

## Project structure
```
app/src/main/java/com/itemfinder/app/
  data/            Room entities, DAOs, database, repository
  ui/
    ItemFinderViewModel.kt   Single ViewModel exposing StateFlows + actions
    navigation/NavGraph.kt   Bottom-nav with 4 tabs (Find, Items, Categories, Add Place)
    screens/                 One Composable screen per tab
    theme/                   Basic Material 3 theme
  MainActivity.kt
```

## Notes / things you may want to customize
- App icon is a simple placeholder vector — swap `ic_launcher_foreground.xml` /
  `colors.xml` for your own branding if you like.
- Camera capture uses the system camera app via `ActivityResultContracts.TakePicture()`
  (no extra camera permission dance needed beyond the `CAMERA` permission already
  declared, which some devices require even when delegating to the system camera app).
- Each storage photo (a "place") can have multiple items assigned to it — handy when
  several things live in the same drawer.
- To add multi-photo history per item (e.g. "last 3 places this was seen"), you'd
  extend `StoragePlace` to keep every capture instead of overwriting the assignment —
  the current design keeps only the *current* location per item, which matches the
  "where is it right now" use case.
