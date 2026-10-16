# Auto Call Recorder (Android & Web Companion)

A professional, privacy-first **Auto Call Recorder** project featuring:
1. **Responsive Web Application** (`index.html`, `style.css`, `app.js`): Mobile-first, dark-themed offline player and call audio manager using IndexedDB and Web Audio API, deployable to **GitHub Pages for 100% free hosting**.
2. **Native Android Application** (`Kotlin + Jetpack Compose + Room + Scoped Storage`): Device-level telephony state listener, foreground recording service, voice memo engine, audio visualizer, offline library, bilingual localization (English & Bengali), and hardware diagnostics.

---

## 🎨 Theme & Color Palette
- **Main Background**: `#080B0A`
- **Card Background**: `#111713`
- **Primary Accent**: `#20D68A`
- **Secondary Accent**: `#12A76B`
- **Main Text**: `#FFFFFF`
- **Secondary Text**: `#A8B0AA`
- **Warning**: `#F4C542`
- **Error / Record Pulse**: `#F05D5E`

---

## 🏗️ Architecture & Component Design

### 1. Web Application (`index.html`, `style.css`, `app.js`)
- **Storage Layer**: Uses **IndexedDB** (`AutoCallRecorderDB`) to store audio Blobs (`audio/webm`, `audio/mp4`, `audio/wav`, `audio/aac`) and metadata locally in the browser sandbox. No large files in LocalStorage.
- **Microphone & Memo Recorder**: Employs `MediaRecorder` and `AudioContext` with an `AnalyserNode` to draw live frequency waveforms on an HTML5 `<canvas>`.
- **Audio Player Bar**: Persistent bottom player featuring scrubber timeline, volume, ±5s skip, playback speed (1.0x, 1.25x, 1.5x, 2.0x), and auto-stop.
- **Localization Engine**: Dynamic runtime switching between English (`en`) and Bengali (`bn`).
- **Android WebView Bridge**: Communicates bidirectional events via `window.AndroidBridge`.

### 2. Native Android Application
- **Telephony Monitoring (`CallBroadcastReceiver`)**: Listens for cellular call state changes (`RINGING`, `OFFHOOK`, `IDLE`) via `android.intent.action.PHONE_STATE` and `NEW_OUTGOING_CALL`.
- **Foreground Service (`CallRecordService`)**: Runs with `android:foregroundServiceType="microphone"` displaying a non-dismissible notification while active.
- **Recording Engine (`AudioRecorderManager`)**: Employs `MediaRecorder` with `AudioSource.VOICE_COMMUNICATION` (hardware Acoustic Echo Cancellation) and fallback to `AudioSource.MIC`.
- **Offline Database (`Room`)**:
  - `RecordingEntity`: `id`, `title`, `filePath`, `phoneNumber`, `contactName`, `callType`, `timestamp`, `durationMs`, `fileSizeBytes`, `format`, `isStarred`, `note`.
  - `RecordingDao`: Reactive `Flow<List<RecordingEntity>>` queries.
- **Scoped Storage Compliance (`StorageHelper`)**: Saves files locally to `Recordings/AutoCallRecorder/` (or app-specific fallback).
- **Audio Playback (`AudioPlayerManager`)**: Native `MediaPlayer` wrapper with fine-grained seek and speed control.
- **Contact Lookup (`ContactHelper`)**: Safely resolves local caller IDs from `ContactsContract.PhoneLookup` without external telemetry.
- **Device Diagnostics (`DiagnosticsHelper`)**: Transparent inspection of Android version, API level, and audio HAL compatibility.

---

## ⚖️ Android OS Call Recording Policy & Restrictions

> **Truthful Disclaimer**:
> Starting with Android 9 (Pie) and Android 10, Google intentionally blocked third-party applications from directly tapping into raw two-way cellular voice call audio (`AudioSource.VOICE_CALL`). In 2022, Google updated Play Store policies to restrict third-party apps from utilizing the Accessibility API to bypass this restriction.
>
> **Our Legitimate & Supported Approach**:
> 1. We use authorized Android APIs: `AudioSource.VOICE_COMMUNICATION` and `AudioSource.MIC`.
> 2. On modern Android versions (10, 11, 12, 13, 14, 15), turning on the **Speakerphone** during a call enables the hardware microphone to cleanly record both the local speaker and the remote participant.
> 3. Native dialer recordings from manufacturers (Samsung OneUI, Xiaomi MIUI, OnePlus, Vivo) saved under `/Recordings/` can be imported instantly into the offline library.

---

## 🌐 Deploy Web Companion to GitHub Pages (Free)

1. Create a public repository on GitHub named:
   ```
   auto-call-recorder
   ```
2. Push your project files:
   ```bash
   git init
   git add index.html style.css app.js README.md
   git commit -m "Deploy Auto Call Recorder Web Companion"
   git branch -M main
   git remote add origin https://github.com/<your-username>/auto-call-recorder.git
   git push -u origin main
   ```
3. In your GitHub repository:
   - Navigate to **Settings** > **Pages** (in the left sidebar).
   - Under **Build and deployment** > **Source**, choose **Deploy from a branch**.
   - Select **Branch**: `main` / `/(root)` and click **Save**.
4. In ~1 minute, your website will be live at:
   ```
   https://<your-username>.github.io/auto-call-recorder/
   ```

---

## 📱 Build Android APK with Android Studio

### Prerequisites:
- Android Studio Ladybug / Meerkat (or newer)
- Android SDK 35/36 installed
- Java JDK 17 or 21

### Build Steps:
1. Open Android Studio and choose **Open an Existing Project**.
2. Select the root folder of this project.
3. Allow Gradle to sync dependencies (`Room`, `Compose BOM`, `Navigation`).
4. To build the Debug APK:
   - Go to **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
   - Or run in terminal:
     ```bash
     gradle assembleDebug
     ```
5. The generated APK will be located at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Installing on Device:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Realistic Testing Checklist

| Test Item | Action | Expected Result |
| :--- | :--- | :--- |
| **1. First Launch** | Open the application | Prompted for Microphone, Phone State, and Contacts permissions. |
| **2. Diagnostic Screen** | Tap "Diagnostics" tab | Hardware model, Android API, and permission statuses display accurately. |
| **3. Call Memo Recording** | Tap "Record Memo" on Dashboard | Red recording pulse appears, timer increments, live audio wave visualizes. |
| **4. Memo Save & Offline Play** | Tap "Stop Recording" | Audio file saved into `Recordings/AutoCallRecorder/`, playable in bottom bar. |
| **5. Incoming Call Detection** | Place a test cellular call to device | Notification channel triggers; recorder starts when call answered. |
| **6. Language Switch** | Change language to Bengali (`বাংলা`) in Settings | All menus, buttons, labels, and dialogs translate to Bengali immediately. |
| **7. Security PIN Lock** | Enable 4-digit PIN in Settings | Re-opening app requires PIN entry before displaying call history. |
| **8. Web Companion Sync** | Open "Web & Export" tab | Embedded HTML5 web app loads offline from assets with IndexedDB. |
