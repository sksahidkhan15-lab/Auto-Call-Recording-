/**
 * Auto Call Recorder - Web Companion & Offline Player
 * Technology: HTML5, CSS3, Vanilla JavaScript, IndexedDB
 * Dual Language: English & Bengali
 */

(function () {
  'use strict';

  // --- Localization Dictionary ---
  const I18N = {
    en: {
      app_title: "Auto Call Recorder",
      app_tagline: "Offline Library & Web Companion",
      nav_dashboard: "Dashboard",
      nav_library: "Library",
      nav_record: "Record / Import",
      nav_diagnostics: "Compatibility",
      nav_settings: "Settings",
      status_ready_title: "Audio Engine: Standby (Ready)",
      badge_ready: "Ready",
      status_ready_desc: "Web client storage active via IndexedDB. Connect to native Android app for automated cellular call interception.",
      status_rec_title: "Recording in Progress...",
      status_rec_badge: "Active",
      status_rec_desc: "Microphone audio stream active. Audio data is being buffered locally.",
      stat_total: "Total Recordings",
      stat_total_sub: "Saved locally",
      stat_today: "Today's Calls",
      stat_today_sub: "Logged today",
      stat_duration: "Total Duration",
      stat_duration_sub: "Recorded audio",
      stat_storage: "Storage Usage",
      btn_quick_record: "Record Voice Memo",
      btn_import_audio: "Import Audio File",
      btn_view_library: "View All Recordings",
      recent_title: "Recent Recordings",
      empty_recordings_msg: "No recordings in library. Record a call memo or import an audio file to get started.",
      search_placeholder: "Search by contact, phone number, note...",
      filter_all: "All",
      filter_incoming: "Incoming",
      filter_outgoing: "Outgoing",
      filter_memo: "Memos",
      filter_imported: "Imported",
      filter_starred: "★ Starred",
      sort_by: "Sort:",
      sort_newest: "Newest First",
      sort_oldest: "Oldest First",
      sort_longest: "Longest First",
      sort_largest: "Largest First",
      recorder_tool_title: "Voice & Call Memo Recorder",
      recorder_tool_desc: "Record a voice memo, conversation note, or microphone audio directly into your offline library using the browser Web Audio API.",
      label_contact_name: "Contact / Title",
      label_phone_number: "Phone Number (Optional)",
      label_call_type: "Type",
      opt_incoming: "Incoming Call",
      opt_outgoing: "Outgoing Call",
      opt_memo: "Voice Memo / Note",
      ph_contact: "e.g. John Doe / Support Call",
      ph_phone: "+1 555-0199",
      btn_start_record: "Start Recording",
      btn_stop_record: "Stop & Save",
      btn_pause: "Pause",
      btn_resume: "Resume",
      btn_cancel: "Cancel",
      import_section_title: "Import External Audio / System Call Recording",
      import_section_desc: "Have recordings from Samsung, Xiaomi, or a third-party dialer? Import them into your offline IndexedDB library for instant playback, search, and categorization.",
      drop_title: "Click or drag audio file here",
      drop_support: "Supports MP3, M4A, AAC, WAV, AMR, OGG",
      compat_title: "Android & Browser Compatibility Engine",
      compat_subtitle: "Transparent diagnostic report explaining audio source constraints and legal privacy standards.",
      diag_platform: "Active Platform",
      diag_platform_sub: "Environment detection",
      diag_cellular: "Direct 2-Way Cellular Audio",
      diag_cellular_sub: "Google Android 9/10+ Security Sandbox",
      diag_cellular_val: "Restricted by Android OS",
      diag_voice_comm: "Voice Communication AudioSource",
      diag_voice_comm_sub: "Hardware acoustic echo cancelled source",
      diag_voice_comm_val: "Supported (Native App)",
      diag_speaker: "Speakerphone 2-Way Recording",
      diag_speaker_sub: "Acoustic capture of remote + local caller",
      diag_speaker_val: "Supported (100% Reliable)",
      diag_privacy: "Privacy Indicators & Notification",
      diag_privacy_sub: "Android 12+ green mic pill & foreground status",
      diag_privacy_val: "Strictly Compliant",
      why_notice_title: "Why doesn't any third-party app record direct cellular audio on modern Android?",
      why_notice_p1: "Starting with Android 9 (Pie) and finalized in Android 10, Google removed the permission for third-party applications to tap into raw cellular call audio (VOICE_CALL). In 2022, Google further restricted the Accessibility API to prevent apps from covertly bypassing this security barrier.",
      why_notice_p2: "Our Ethical & Supported Approach: We utilize Android's authorized AudioSource.VOICE_COMMUNICATION and AudioSource.MIC with telephony state monitoring. Turning on speakerphone during calls allows capturing both voices cleanly without exploiting system vulnerabilities or violating privacy laws.",
      settings_preferences_title: "Application Preferences",
      settings_lang_title: "Language / ভাষা",
      settings_lang_label: "Interface Language",
      settings_lang_desc: "Switch all application text between English and Bengali",
      settings_storage_title: "Storage & Backup",
      settings_storage_mode: "Storage Engine",
      settings_storage_desc: "Offline IndexedDB (Web) / Scoped Storage Recordings (Android)",
      badge_local_only: "100% Local",
      settings_export_backup: "Export Recordings (JSON & Metadata)",
      settings_export_desc: "Download a backup file containing all recording entries",
      btn_export: "Export Backup",
      settings_clear_all: "Clear All Local Recordings",
      settings_clear_desc: "Permanently remove all audio files stored in this browser's IndexedDB",
      btn_clear_db: "Clear Data",
      settings_privacy_title: "Privacy & Security",
      privacy_desc: "This application has NO server, NO tracking, and NO cloud upload. All audio recordings remain strictly on your physical device. We never sell, transmit, or inspect your personal call logs.",
      modal_rename_title: "Rename Recording",
      btn_cancel: "Cancel",
      btn_save: "Save",
      confirm_delete: "Are you sure you want to delete this recording?",
      confirm_clear_all: "WARNING: This will permanently delete ALL recordings from this browser's local storage. Proceed?",
      unknown_caller: "Unknown Caller",
      format_duration: "Duration",
      call_incoming: "Incoming",
      call_outgoing: "Outgoing",
      call_memo: "Memo",
      call_imported: "Imported"
    },
    bn: {
      app_title: "অটো কল রেকর্ডার",
      app_tagline: "অফলাইন লাইব্রেরি ও ওয়েব সহকারী",
      nav_dashboard: "ড্যাশবোর্ড",
      nav_library: "লাইব্রেরি",
      nav_record: "রেকর্ড / ইমপোর্ট",
      nav_diagnostics: "সামঞ্জস্যতা",
      nav_settings: "সেটিংস",
      status_ready_title: "অডিও ইঞ্জিন: স্ট্যান্ডবাই (প্রস্তুত)",
      badge_ready: "প্রস্তুত",
      status_ready_desc: "IndexedDB মাধ্যমে ওয়েব স্টোরেজ সক্রিয়। সেলুলার কল রেকর্ডিংয়ের জন্য নেটিভ অ্যান্ড্রয়েড অ্যাপ ব্যবহার করুন।",
      status_rec_title: "রেকর্ডিং চলছে...",
      status_rec_badge: "সক্রিয়",
      status_rec_desc: "মাইক্রোফোন অডিও স্ট্রীম সক্রিয়। অডিও লোকাল মেমরিতে জমা হচ্ছে।",
      stat_total: "মোট রেকর্ডিং",
      stat_total_sub: "স্থানীয়ভাবে সংরক্ষিত",
      stat_today: "আজকের কল",
      stat_today_sub: "আজকের লগ",
      stat_duration: "মোট সময়কাল",
      stat_duration_sub: "রেকর্ডকৃত অডিও",
      stat_storage: "ব্যবহৃত স্টোরেজ",
      btn_quick_record: "ভয়েস মেমো রেকর্ড করুন",
      btn_import_audio: "অডিও ফাইল ইমপোর্ট করুন",
      btn_view_library: "সমস্ত রেকর্ডিং দেখুন",
      recent_title: "সাম্প্রতিক রেকর্ডিং",
      empty_recordings_msg: "লাইব্রেরিতে কোনো রেকর্ডিং নেই। শুরু করতে একটি ভয়েস মেমো রেকর্ড করুন বা ফাইল ইমপোর্ট করুন।",
      search_placeholder: "নাম, ফোন নম্বর বা নোট দিয়ে খুঁজুন...",
      filter_all: "সমস্ত",
      filter_incoming: "আগত কল",
      filter_outgoing: "বহির্গামী কল",
      filter_memo: "মেমো ও নোট",
      filter_imported: "ইমপোর্টকৃত",
      filter_starred: "★ পছন্দের",
      sort_by: "সাজান:",
      sort_newest: "নতুনগুলো আগে",
      sort_oldest: "পুরানোগুলো আগে",
      sort_longest: "দীর্ঘ সময় আগে",
      sort_largest: "বড় সাইজ আগে",
      recorder_tool_title: "ভয়েস ও কল মেমো রেকর্ডার",
      recorder_tool_desc: "ব্রাউজার Web Audio API ব্যবহার করে সরাসরি আপনার অফলাইন লাইব্রেরিতে ভয়েস নোট বা কথোপকথন রেকর্ড করুন।",
      label_contact_name: "পরিচিতি / শিরোনাম",
      label_phone_number: "ফোন নম্বর (ঐচ্ছিক)",
      label_call_type: "ধরন",
      opt_incoming: "আগত কল",
      opt_outgoing: "বহির্গামী কল",
      opt_memo: "ভয়েস মেমো / নোট",
      ph_contact: "যেমন: করিম ভাই / সাপোর্ট কল",
      ph_phone: "+880 1700-000000",
      btn_start_record: "রেকর্ডিং শুরু করুন",
      btn_stop_record: "বন্ধ ও সংরক্ষণ করুন",
      btn_pause: "বিরতি",
      btn_resume: "পুনরায় চালান",
      btn_cancel: "বাতিল",
      import_section_title: "বাহ্যিক অডিও বা সিস্টেম কল রেকর্ডিং ইমপোর্ট",
      import_section_desc: "স্যামসাং বা শাওমি ডায়ালারের রেকর্ডিং ফাইল আছে? তাৎক্ষণিক প্লেব্যাক ও অনুসন্ধানের জন্য IndexedDB লাইব্রেরিতে যুক্ত করুন।",
      drop_title: "এখানে অডিও ফাইল ড্র্যাগ করুন বা ক্লিক করুন",
      drop_support: "MP3, M4A, AAC, WAV, AMR, OGG ফাইল সমর্থিত",
      compat_title: "অ্যান্ড্রয়েড ও ব্রাউজার সামঞ্জস্যতা ইঞ্জিন",
      compat_subtitle: "অডিও সীমাবদ্ধতা এবং আইনি গোপনীয়তার বাস্তবসম্মত প্রতিবেদন।",
      diag_platform: "সক্রিয় প্ল্যাটফর্ম",
      diag_platform_sub: "পরিবেশ সনাক্তকরণ",
      diag_cellular: "উভয় পাশের সরাসরি সেলুলার অডিও",
      diag_cellular_sub: "অ্যান্ড্রয়েড ৯/১০+ নিরাপত্তা রেস্ট্রিকশন",
      diag_cellular_val: "অ্যান্ড্রয়েড ওএস দ্বারা সীমাবদ্ধ",
      diag_voice_comm: "ভয়েস কমিউনিকেশন অডিও সোর্স",
      diag_voice_comm_sub: "হার্ডওয়্যার ইকো ক্যান্সেলেশন সোর্স",
      diag_voice_comm_val: "সমর্থিত (নেটিভ অ্যাপ)",
      diag_speaker: "স্পিকারফোন উভয় পাশের রেকর্ডিং",
      diag_speaker_sub: "স্পিকারের মাধ্যমে উভয় পক্ষের কথা ক্যাপচার",
      diag_speaker_val: "সমর্থিত (১০০% কার্যকর)",
      diag_privacy: "গোপনীয়তা নির্দেশক ও নোটিফিকেশন",
      diag_privacy_sub: "অ্যান্ড্রয়েড ১২+ গ্রিন মাইক এবং ফোরগ্রাউন্ড সার্ভিস",
      diag_privacy_val: "সম্পূর্ণ নীতিসম্মত",
      why_notice_title: "আধুনিক অ্যান্ড্রয়েডে সরাসরি সেলুলার অডিও কেন রেকর্ড হয় না?",
      why_notice_p1: "অ্যান্ড্রয়েড ৯ এবং ১০ সংস্করণ থেকে গুগল ব্যবহারকারীর গোপনীয়তা রক্ষায় নন-সিস্টেম অ্যাপের জন্য সরাসরি উভয় পাশের সেলুলার অডিও (VOICE_CALL) ব্লক করেছে। ২০২২ সালে অ্যাক্সেসিবিলিটি এপিআই ব্যবহারে আরও নিষেধাজ্ঞা যুক্ত হয়।",
      why_notice_p2: "আমাদের বৈধ ও অনুমোদিত সমাধান: আমরা অফিসিয়াল AudioSource.VOICE_COMMUNICATION এবং AudioSource.MIC ব্যবহার করি। কলের সময় স্পিকারফোন অন রাখলে উভয় দিকের কথা চমৎকার ও পরিষ্কারভাবে রেকর্ড হয়।",
      settings_preferences_title: "অ্যাপ্লিকেশন পছন্দসমূহ",
      settings_lang_title: "ভাষা / Language",
      settings_lang_label: "ইন্টারফেস ভাষা",
      settings_lang_desc: "ইংরেজি ও বাংলার মধ্যে অ্যাপ্লিকেশন টেক্সট পরিবর্তন করুন",
      settings_storage_title: "স্টোরেজ ও ব্যাকআপ",
      settings_storage_mode: "স্টোরেজ ইঞ্জিন",
      settings_storage_desc: "অফলাইন IndexedDB (ওয়েব) / স্কোপড স্টোরেজ (অ্যান্ড্রয়েড)",
      badge_local_only: "১০০% লোকাল",
      settings_export_backup: "রেকর্ডিং এক্সপোর্ট (JSON ও মেটাডাটা)",
      settings_export_desc: "সমস্ত রেকর্ডিং এন্ট্রির ব্যাকআপ ফাইল ডাউনলোড করুন",
      btn_export: "ব্যাকআপ এক্সপোর্ট",
      settings_clear_all: "সমস্ত লোকাল রেকর্ডিং মুছুন",
      settings_clear_desc: "এই ব্রাউজারের IndexedDB-তে সংরক্ষিত সমস্ত অডিও স্থায়ীভাবে মুছে ফেলুন",
      btn_clear_db: "ডাটা মুছুন",
      settings_privacy_title: "নিরাপত্তা ও গোপনীয়তা",
      privacy_desc: "এই অ্যাপে কোনো সার্ভার, ট্র্যাকিং বা ক্লাউড আপলোড নেই। সমস্ত অডিও সম্পূর্ণ আপনার ডিভাইসে সংরক্ষিত থাকে। আপনার ব্যক্তিগত তথ্য কখনোই শেয়ার করা হয় না।",
      modal_rename_title: "রেকর্ডিংয়ের নাম পরিবর্তন",
      btn_cancel: "বাতিল",
      btn_save: "সংরক্ষণ",
      confirm_delete: "আপনি কি নিশ্চিতভাবে এই রেকর্ডিংটি মুছে ফেলতে চান?",
      confirm_clear_all: "সতর্কতা: এটি এই ব্রাউজারের লোকাল স্টোরেজ থেকে সমস্ত রেকর্ডিং স্থায়ীভাবে মুছে ফেলবে। এগিয়ে যাবেন?",
      unknown_caller: "অজানা কলার",
      format_duration: "সময়কাল",
      call_incoming: "আগত কল",
      call_outgoing: "বহির্গামী কল",
      call_memo: "মেমো",
      call_imported: "ইমপোর্টকৃত"
    }
  };

  // --- State Variables ---
  let currentLang = localStorage.getItem('acr_lang') || 'en';
  let db = null;
  let allRecordings = [];
  let currentFilter = 'all';
  let searchQuery = '';
  let currentSort = 'newest';
  let activePlayingId = null;
  let playbackSpeed = 1.0;
  let renameTargetId = null;

  // Audio Recording State
  let mediaRecorder = null;
  let audioChunks = [];
  let audioContext = null;
  let analyser = null;
  let visualizerAnimId = null;
  let recordStartTime = 0;
  let recordTimerInterval = null;
  let isRecordingPaused = false;

  // --- IndexedDB Setup ---
  const DB_NAME = 'AutoCallRecorderDB';
  const DB_VERSION = 1;
  const STORE_NAME = 'recordings';

  function initDB() {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(DB_NAME, DB_VERSION);
      request.onerror = (e) => reject(e);
      request.onsuccess = (e) => {
        db = e.target.result;
        resolve(db);
      };
      request.onupgradeneeded = (e) => {
        const database = e.target.result;
        if (!database.objectStoreNames.contains(STORE_NAME)) {
          const store = database.createObjectStore(STORE_NAME, { keyPath: 'id' });
          store.createIndex('timestamp', 'timestamp', { unique: false });
          store.createIndex('callType', 'callType', { unique: false });
          store.createIndex('isStarred', 'isStarred', { unique: false });
        }
      };
    });
  }

  function dbGetAll() {
    return new Promise((resolve, reject) => {
      if (!db) return resolve([]);
      const tx = db.transaction(STORE_NAME, 'readonly');
      const store = tx.objectStore(STORE_NAME);
      const req = store.getAll();
      req.onsuccess = () => resolve(req.result || []);
      req.onerror = (e) => reject(e);
    });
  }

  function dbPut(record) {
    return new Promise((resolve, reject) => {
      if (!db) return reject("DB not initialized");
      const tx = db.transaction(STORE_NAME, 'readwrite');
      const store = tx.objectStore(STORE_NAME);
      const req = store.put(record);
      req.onsuccess = () => resolve(req.result);
      req.onerror = (e) => reject(e);
    });
  }

  function dbDelete(id) {
    return new Promise((resolve, reject) => {
      if (!db) return reject("DB not initialized");
      const tx = db.transaction(STORE_NAME, 'readwrite');
      const store = tx.objectStore(STORE_NAME);
      const req = store.delete(id);
      req.onsuccess = () => resolve();
      req.onerror = (e) => reject(e);
    });
  }

  function dbClearAll() {
    return new Promise((resolve, reject) => {
      if (!db) return reject("DB not initialized");
      const tx = db.transaction(STORE_NAME, 'readwrite');
      const store = tx.objectStore(STORE_NAME);
      const req = store.clear();
      req.onsuccess = () => resolve();
      req.onerror = (e) => reject(e);
    });
  }

  // --- Localization Function ---
  function applyLanguage(lang) {
    currentLang = lang;
    localStorage.setItem('acr_lang', lang);
    const dict = I18N[lang] || I18N.en;

    document.querySelectorAll('[data-i18n]').forEach((el) => {
      const key = el.getAttribute('data-i18n');
      if (dict[key]) el.textContent = dict[key];
    });

    document.querySelectorAll('[data-i18n-ph]').forEach((el) => {
      const key = el.getAttribute('data-i18n-ph');
      if (dict[key]) el.setAttribute('placeholder', dict[key]);
    });

    const langSel = document.getElementById('languageSelect');
    if (langSel) langSel.value = lang;
    const settingsLangSel = document.getElementById('settingsLanguageSelect');
    if (settingsLangSel) settingsLangSel.value = lang;

    // Detect if inside Android WebView
    if (window.AndroidBridge && window.AndroidBridge.setAppLanguage) {
      try {
        window.AndroidBridge.setAppLanguage(lang);
      } catch (err) {
        console.warn("Bridge language sync error:", err);
      }
    }

    renderDashboard();
    renderLibrary();
  }

  // --- Format Utilities ---
  function formatDuration(sec) {
    if (!sec || isNaN(sec)) sec = 0;
    sec = Math.floor(sec);
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }

  function formatTimeHHMMSS(sec) {
    if (!sec || isNaN(sec)) sec = 0;
    sec = Math.floor(sec);
    const h = Math.floor(sec / 3600);
    const m = Math.floor((sec % 3600) / 60);
    const s = sec % 60;
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  }

  function formatBytes(bytes) {
    if (!bytes || isNaN(bytes)) return '0 KB';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  }

  function formatDate(ts) {
    const d = new Date(ts);
    const locale = currentLang === 'bn' ? 'bn-BD' : 'en-US';
    return d.toLocaleDateString(locale, { month: 'short', day: 'numeric', year: 'numeric' }) + ' ' + d.toLocaleTimeString(locale, { hour: '2-digit', minute: '2-digit' });
  }

  // --- Dashboard Renderer ---
  function renderDashboard() {
    const totalCount = allRecordings.length;
    document.getElementById('statTotalCount').textContent = totalCount;
    document.getElementById('libraryCountBadge').textContent = totalCount;

    // Today's count
    const startOfToday = new Date();
    startOfToday.setHours(0, 0, 0, 0);
    const todayCount = allRecordings.filter(r => r.timestamp >= startOfToday.getTime()).length;
    document.getElementById('statTodayCount').textContent = todayCount;

    // Total Duration
    const totalSec = allRecordings.reduce((sum, r) => sum + (r.duration || 0), 0);
    document.getElementById('statTotalDuration').textContent = formatTimeHHMMSS(totalSec);

    // Total Storage
    const totalBytes = allRecordings.reduce((sum, r) => sum + (r.size || 0), 0);
    document.getElementById('statStorageUsed').textContent = formatBytes(totalBytes);

    // Recent list (latest 4)
    const recent = [...allRecordings].sort((a, b) => b.timestamp - a.timestamp).slice(0, 4);
    const recentContainer = document.getElementById('recentRecordingsList');
    document.getElementById('recentCountText').textContent = `${recent.length} / ${totalCount}`;

    if (recent.length === 0) {
      const dict = I18N[currentLang] || I18N.en;
      recentContainer.innerHTML = `
        <div class="empty-state">
          <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="#A8B0AA" stroke-width="1.5"><path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path><path d="M19 10v2a7 7 0 0 1-14 0v-2"></path><line x1="12" y1="19" x2="12" y2="23"></line></svg>
          <p>${dict.empty_recordings_msg}</p>
        </div>`;
    } else {
      recentContainer.innerHTML = '';
      recent.forEach(item => {
        recentContainer.appendChild(createRecordingElement(item));
      });
    }
  }

  // --- Create Recording DOM Element ---
  function createRecordingElement(item) {
    const dict = I18N[currentLang] || I18N.en;
    const div = document.createElement('div');
    div.className = 'recording-item';
    div.dataset.id = item.id;

    let typeClass = item.callType || 'memo';
    let typeIcon = '';
    if (typeClass === 'incoming') {
      typeIcon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>';
    } else if (typeClass === 'outgoing') {
      typeIcon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><polyline points="8 7 3 12 8 17"></polyline><line x1="3" y1="12" x2="15" y2="12"></line></svg>';
    } else if (typeClass === 'imported') {
      typeIcon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>';
    } else {
      typeIcon = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"></path><path d="M19 10v2a7 7 0 0 1-14 0v-2"></path></svg>';
    }

    const title = item.title || dict.unknown_caller;
    const phone = item.phoneNumber ? `• ${item.phoneNumber}` : '';
    const dateStr = formatDate(item.timestamp);
    const durStr = formatDuration(item.duration);
    const sizeStr = formatBytes(item.size);

    div.innerHTML = `
      <div class="rec-main">
        <div class="rec-type-icon ${typeClass}">
          ${typeIcon}
        </div>
        <div class="rec-details">
          <div class="rec-title-row">
            <span class="rec-title">${escapeHtml(title)}</span>
          </div>
          <div class="rec-meta">
            <span>${dateStr}</span>
            <span>${phone}</span>
            <span>⏱ ${durStr}</span>
            <span>💾 ${sizeStr}</span>
          </div>
        </div>
      </div>
      <div class="rec-actions">
        <button class="icon-btn btn-play-item" title="Play">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><polygon points="5 3 19 12 5 21 5 3"></polygon></svg>
        </button>
        <button class="icon-btn btn-star-item ${item.isStarred ? 'starred' : ''}" title="Star">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="${item.isStarred ? '#F4C542' : 'none'}" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
        </button>
        <button class="icon-btn btn-rename-item" title="Rename">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
        </button>
        <button class="icon-btn btn-download-item" title="Download">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
        </button>
        <button class="icon-btn btn-delete-item" title="Delete">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="#F05D5E" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
        </button>
      </div>
    `;

    // Hook listeners
    div.querySelector('.btn-play-item').onclick = () => playRecording(item);
    div.querySelector('.btn-star-item').onclick = () => toggleStar(item);
    div.querySelector('.btn-rename-item').onclick = () => openRenameModal(item);
    div.querySelector('.btn-download-item').onclick = () => downloadRecording(item);
    div.querySelector('.btn-delete-item').onclick = () => deleteRecording(item.id);

    return div;
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, function (m) {
      return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' })[m];
    });
  }

  // --- Library Renderer ---
  function renderLibrary() {
    let filtered = allRecordings.filter(item => {
      // Filter tab
      if (currentFilter === 'starred' && !item.isStarred) return false;
      if (currentFilter !== 'all' && currentFilter !== 'starred' && item.callType !== currentFilter) return false;

      // Search Query
      if (searchQuery) {
        const q = searchQuery.toLowerCase();
        const t = (item.title || '').toLowerCase();
        const p = (item.phoneNumber || '').toLowerCase();
        const n = (item.notes || '').toLowerCase();
        if (!t.includes(q) && !p.includes(q) && !n.includes(q)) return false;
      }
      return true;
    });

    // Sort
    if (currentSort === 'newest') {
      filtered.sort((a, b) => b.timestamp - a.timestamp);
    } else if (currentSort === 'oldest') {
      filtered.sort((a, b) => a.timestamp - b.timestamp);
    } else if (currentSort === 'duration') {
      filtered.sort((a, b) => (b.duration || 0) - (a.duration || 0));
    } else if (currentSort === 'size') {
      filtered.sort((a, b) => (b.size || 0) - (a.size || 0));
    }

    const container = document.getElementById('recordingsList');
    document.getElementById('resultsCount').textContent = `${filtered.length} recordings`;

    if (filtered.length === 0) {
      const dict = I18N[currentLang] || I18N.en;
      container.innerHTML = `
        <div class="empty-state">
          <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="#A8B0AA" stroke-width="1.5"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          <p>${dict.empty_recordings_msg}</p>
        </div>`;
    } else {
      container.innerHTML = '';
      filtered.forEach(item => {
        container.appendChild(createRecordingElement(item));
      });
    }
  }

  // --- Audio Player Controller ---
  const nativeAudio = document.getElementById('nativeAudio');
  const playerBar = document.getElementById('audioPlayerBar');
  const playerTitle = document.getElementById('playerTitle');
  const playerSub = document.getElementById('playerSub');
  const playerPlayBtn = document.getElementById('playerBtnPlay');
  const playIcon = document.getElementById('playIcon');
  const pauseIcon = document.getElementById('pauseIcon');
  const playerSeekSlider = document.getElementById('playerSeekSlider');
  const playerCurrentTime = document.getElementById('playerCurrentTime');
  const playerTotalTime = document.getElementById('playerTotalTime');
  const playerBtnSpeed = document.getElementById('playerBtnSpeed');
  let currentAudioUrl = null;

  function playRecording(item) {
    if (!item.audioBlob) {
      alert("Audio file data is missing or corrupted.");
      return;
    }

    if (currentAudioUrl) {
      URL.revokeObjectURL(currentAudioUrl);
    }

    activePlayingId = item.id;
    currentAudioUrl = URL.createObjectURL(item.audioBlob);
    nativeAudio.src = currentAudioUrl;
    nativeAudio.playbackRate = playbackSpeed;
    nativeAudio.play().then(() => {
      updatePlayerUI(true, item);
    }).catch(err => {
      console.error("Playback failed:", err);
      alert("Unable to play audio format: " + err.message);
    });

    playerBar.classList.add('active');
  }

  function updatePlayerUI(isPlaying, item) {
    if (item) {
      playerTitle.textContent = item.title || "Voice Recording";
      playerSub.textContent = (item.phoneNumber ? item.phoneNumber + " • " : "") + formatDate(item.timestamp);
    }
    if (isPlaying) {
      playIcon.style.display = 'none';
      pauseIcon.style.display = 'block';
    } else {
      playIcon.style.display = 'block';
      pauseIcon.style.display = 'none';
    }
  }

  playerPlayBtn.onclick = () => {
    if (nativeAudio.paused) {
      nativeAudio.play();
      updatePlayerUI(true);
    } else {
      nativeAudio.pause();
      updatePlayerUI(false);
    }
  };

  nativeAudio.ontimeupdate = () => {
    const cur = nativeAudio.currentTime;
    const dur = nativeAudio.duration || 0;
    playerCurrentTime.textContent = formatDuration(cur);
    if (!isNaN(dur) && dur > 0) {
      playerTotalTime.textContent = formatDuration(dur);
      playerSeekSlider.value = (cur / dur) * 100;
    }
  };

  nativeAudio.onended = () => {
    updatePlayerUI(false);
    playerSeekSlider.value = 0;
    playerCurrentTime.textContent = "00:00";
  };

  playerSeekSlider.oninput = () => {
    const dur = nativeAudio.duration || 0;
    if (!isNaN(dur) && dur > 0) {
      nativeAudio.currentTime = (playerSeekSlider.value / 100) * dur;
    }
  };

  document.getElementById('playerBtnRewind').onclick = () => {
    nativeAudio.currentTime = Math.max(0, nativeAudio.currentTime - 5);
  };

  document.getElementById('playerBtnForward').onclick = () => {
    nativeAudio.currentTime = Math.min(nativeAudio.duration || 0, nativeAudio.currentTime + 5);
  };

  playerBtnSpeed.onclick = () => {
    const speeds = [1.0, 1.25, 1.5, 2.0];
    let nextIdx = (speeds.indexOf(playbackSpeed) + 1) % speeds.length;
    playbackSpeed = speeds[nextIdx];
    playerBtnSpeed.textContent = playbackSpeed.toFixed(1) + 'x';
    nativeAudio.playbackRate = playbackSpeed;
  };

  document.getElementById('playerBtnClose').onclick = () => {
    nativeAudio.pause();
    playerBar.classList.remove('active');
    if (currentAudioUrl) {
      URL.revokeObjectURL(currentAudioUrl);
      currentAudioUrl = null;
    }
    activePlayingId = null;
  };

  // --- Star, Rename, Download, Delete ---
  async function toggleStar(item) {
    item.isStarred = !item.isStarred;
    await dbPut(item);
    renderDashboard();
    renderLibrary();
  }

  function openRenameModal(item) {
    renameTargetId = item.id;
    const modal = document.getElementById('renameModal');
    const input = document.getElementById('renameInput');
    input.value = item.title || '';
    modal.classList.add('open');
    input.focus();
  }

  document.getElementById('btnCancelRename').onclick = () => {
    document.getElementById('renameModal').classList.remove('open');
    renameTargetId = null;
  };

  document.getElementById('btnSaveRename').onclick = async () => {
    if (!renameTargetId) return;
    const input = document.getElementById('renameInput');
    const newTitle = input.value.trim();
    const item = allRecordings.find(r => r.id === renameTargetId);
    if (item && newTitle) {
      item.title = newTitle;
      await dbPut(item);
      renderDashboard();
      renderLibrary();
    }
    document.getElementById('renameModal').classList.remove('open');
    renameTargetId = null;
  };

  function downloadRecording(item) {
    if (!item.audioBlob) return;
    const url = URL.createObjectURL(item.audioBlob);
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    const cleanTitle = (item.title || 'recording').replace(/[^a-zA-Z0-9_-]/g, '_');
    const ext = item.format || 'm4a';
    a.download = `ACR_${cleanTitle}_${item.id}.${ext}`;
    document.body.appendChild(a);
    a.click();
    setTimeout(() => {
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    }, 1000);
  }

  async function deleteRecording(id) {
    const dict = I18N[currentLang] || I18N.en;
    if (confirm(dict.confirm_delete)) {
      if (activePlayingId === id) {
        nativeAudio.pause();
        playerBar.classList.remove('active');
      }
      await dbDelete(id);
      allRecordings = allRecordings.filter(r => r.id !== id);
      renderDashboard();
      renderLibrary();
    }
  }

  // --- Voice Memo Live Audio Recorder ---
  const btnRecordStart = document.getElementById('btnRecordStart');
  const recordBtnText = document.getElementById('recordBtnText');
  const btnRecordPause = document.getElementById('btnRecordPause');
  const btnRecordCancel = document.getElementById('btnRecordCancel');
  const recordTimer = document.getElementById('recordTimer');
  const canvas = document.getElementById('visualizerCanvas');
  const canvasCtx = canvas.getContext('2d');
  const statusCard = document.getElementById('statusCard');
  const statusIcon = document.getElementById('statusIcon');
  const statusTitle = document.getElementById('statusTitle');
  const statusBadge = document.getElementById('statusBadge');
  const statusDesc = document.getElementById('statusDesc');

  function setEngineStatus(isRecording) {
    const dict = I18N[currentLang] || I18N.en;
    if (isRecording) {
      statusIcon.className = 'status-icon-wrap recording';
      statusTitle.textContent = dict.status_rec_title;
      statusBadge.className = 'badge';
      statusBadge.style.backgroundColor = 'var(--error)';
      statusBadge.textContent = dict.status_rec_badge;
      statusDesc.textContent = dict.status_rec_desc;
    } else {
      statusIcon.className = 'status-icon-wrap ready';
      statusTitle.textContent = dict.status_ready_title;
      statusBadge.className = 'status-badge-ready';
      statusBadge.style.backgroundColor = '';
      statusBadge.textContent = dict.badge_ready;
      statusDesc.textContent = dict.status_ready_desc;
    }
  }

  btnRecordStart.onclick = async () => {
    const dict = I18N[currentLang] || I18N.en;
    if (!mediaRecorder || mediaRecorder.state === 'inactive') {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        audioContext = new (window.AudioContext || window.webkitAudioContext)();
        analyser = audioContext.createAnalyser();
        const source = audioContext.createMediaStreamSource(stream);
        source.connect(analyser);
        analyser.fftSize = 256;

        let mimeType = 'audio/webm';
        if (!MediaRecorder.isTypeSupported('audio/webm')) {
          if (MediaRecorder.isTypeSupported('audio/mp4')) mimeType = 'audio/mp4';
          else if (MediaRecorder.isTypeSupported('audio/aac')) mimeType = 'audio/aac';
          else mimeType = '';
        }

        mediaRecorder = new MediaRecorder(stream, mimeType ? { mimeType } : undefined);
        audioChunks = [];

        mediaRecorder.ondataavailable = (e) => {
          if (e.data && e.data.size > 0) audioChunks.push(e.data);
        };

        mediaRecorder.onstop = async () => {
          clearInterval(recordTimerInterval);
          cancelAnimationFrame(visualizerAnimId);
          drawFlatVisualizer();
          stream.getTracks().forEach(t => t.stop());
          if (audioContext && audioContext.state !== 'closed') audioContext.close();

          setEngineStatus(false);
          btnRecordStart.classList.remove('recording');
          recordBtnText.textContent = dict.btn_start_record;
          btnRecordPause.disabled = true;
          btnRecordCancel.disabled = true;

          if (audioChunks.length > 0) {
            const mime = mediaRecorder.mimeType || 'audio/webm';
            const audioBlob = new Blob(audioChunks, { type: mime });
            const durationSec = (Date.now() - recordStartTime) / 1000;
            const contactTitle = document.getElementById('recordContactName').value.trim() || ("Voice Memo " + new Date().toLocaleTimeString());
            const phoneNum = document.getElementById('recordPhoneNumber').value.trim();
            const callType = document.getElementById('recordCallType').value || 'memo';

            const newRecord = {
              id: 'acr_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
              title: contactTitle,
              phoneNumber: phoneNum,
              callType: callType,
              timestamp: Date.now(),
              duration: Math.round(durationSec),
              size: audioBlob.size,
              audioBlob: audioBlob,
              format: mime.includes('mp4') ? 'm4a' : (mime.includes('webm') ? 'webm' : 'wav'),
              isStarred: false,
              notes: ""
            };

            await dbPut(newRecord);
            allRecordings.unshift(newRecord);
            renderDashboard();
            renderLibrary();

            // Clear inputs
            document.getElementById('recordContactName').value = '';
            document.getElementById('recordPhoneNumber').value = '';
            recordTimer.textContent = '00:00.0';

            // Switch to library tab to show recorded item
            switchTab('library');
          }
        };

        mediaRecorder.start(250);
        recordStartTime = Date.now();
        setEngineStatus(true);
        btnRecordStart.classList.add('recording');
        recordBtnText.textContent = dict.btn_stop_record;
        btnRecordPause.disabled = false;
        btnRecordCancel.disabled = false;
        isRecordingPaused = false;

        recordTimerInterval = setInterval(() => {
          if (!isRecordingPaused) {
            const elapsed = (Date.now() - recordStartTime) / 1000;
            const m = Math.floor(elapsed / 60).toString().padStart(2, '0');
            const s = Math.floor(elapsed % 60).toString().padStart(2, '0');
            const ms = Math.floor((elapsed % 1) * 10);
            recordTimer.textContent = `${m}:${s}.${ms}`;
          }
        }, 100);

        startVisualizer();
      } catch (err) {
        alert("Microphone permission denied or device not supported: " + err.message);
      }
    } else if (mediaRecorder.state === 'recording' || mediaRecorder.state === 'paused') {
      mediaRecorder.stop();
    }
  };

  btnRecordPause.onclick = () => {
    const dict = I18N[currentLang] || I18N.en;
    if (!mediaRecorder) return;
    if (mediaRecorder.state === 'recording') {
      mediaRecorder.pause();
      isRecordingPaused = true;
      btnRecordPause.textContent = dict.btn_resume;
    } else if (mediaRecorder.state === 'paused') {
      mediaRecorder.resume();
      isRecordingPaused = false;
      btnRecordPause.textContent = dict.btn_pause;
    }
  };

  btnRecordCancel.onclick = () => {
    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      audioChunks = [];
      mediaRecorder.stop();
      recordTimer.textContent = '00:00.0';
    }
  };

  function startVisualizer() {
    const bufferLength = analyser.frequencyBinCount;
    const dataArray = new Uint8Array(bufferLength);

    function draw() {
      visualizerAnimId = requestAnimationFrame(draw);
      analyser.getByteFrequencyData(dataArray);

      canvasCtx.fillStyle = '#060807';
      canvasCtx.fillRect(0, 0, canvas.width, canvas.height);

      const barWidth = (canvas.width / bufferLength) * 2.5;
      let barHeight;
      let x = 0;

      for (let i = 0; i < bufferLength; i++) {
        barHeight = (dataArray[i] / 255) * canvas.height;
        canvasCtx.fillStyle = '#20D68A';
        canvasCtx.fillRect(x, canvas.height - barHeight, barWidth, barHeight);
        x += barWidth + 1;
      }
    }
    draw();
  }

  function drawFlatVisualizer() {
    canvasCtx.fillStyle = '#060807';
    canvasCtx.fillRect(0, 0, canvas.width, canvas.height);
    canvasCtx.strokeStyle = '#26332A';
    canvasCtx.lineWidth = 2;
    canvasCtx.beginPath();
    canvasCtx.moveTo(0, canvas.height / 2);
    canvasCtx.lineTo(canvas.width, canvas.height / 2);
    canvasCtx.stroke();
  }

  // --- External Audio File Import ---
  async function handleAudioFileImport(file) {
    if (!file || !file.type.startsWith('audio/')) {
      alert("Please select a valid audio file.");
      return;
    }

    // Determine duration
    const tempUrl = URL.createObjectURL(file);
    const tempAudio = new Audio(tempUrl);

    tempAudio.onloadedmetadata = async () => {
      const dur = Math.round(tempAudio.duration) || 0;
      URL.revokeObjectURL(tempUrl);

      const fileName = file.name.replace(/\.[^/.]+$/, "");
      const newRecord = {
        id: 'acr_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
        title: fileName,
        phoneNumber: "",
        callType: 'imported',
        timestamp: Date.now(),
        duration: dur,
        size: file.size,
        audioBlob: file,
        format: file.name.split('.').pop() || 'audio',
        isStarred: false,
        notes: "Imported from " + file.name
      };

      await dbPut(newRecord);
      allRecordings.unshift(newRecord);
      renderDashboard();
      renderLibrary();
      switchTab('library');
    };

    tempAudio.onerror = async () => {
      URL.revokeObjectURL(tempUrl);
      const fileName = file.name.replace(/\.[^/.]+$/, "");
      const newRecord = {
        id: 'acr_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
        title: fileName,
        phoneNumber: "",
        callType: 'imported',
        timestamp: Date.now(),
        duration: 0,
        size: file.size,
        audioBlob: file,
        format: file.name.split('.').pop() || 'audio',
        isStarred: false,
        notes: "Imported from " + file.name
      };
      await dbPut(newRecord);
      allRecordings.unshift(newRecord);
      renderDashboard();
      renderLibrary();
      switchTab('library');
    };
  }

  // Import Drop Zone & File inputs
  const importFileInput = document.getElementById('importFileInput');
  const dropFileInput = document.getElementById('dropFileInput');
  const dropZone = document.getElementById('dropZone');

  importFileInput.onchange = (e) => {
    if (e.target.files && e.target.files[0]) handleAudioFileImport(e.target.files[0]);
  };

  dropZone.onclick = () => dropFileInput.click();
  dropFileInput.onchange = (e) => {
    if (e.target.files && e.target.files[0]) handleAudioFileImport(e.target.files[0]);
  };

  dropZone.ondragover = (e) => {
    e.preventDefault();
    dropZone.classList.add('dragover');
  };

  dropZone.ondragleave = () => {
    dropZone.classList.remove('dragover');
  };

  dropZone.ondrop = (e) => {
    e.preventDefault();
    dropZone.classList.remove('dragover');
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleAudioFileImport(e.dataTransfer.files[0]);
    }
  };

  // --- Export JSON Backup & Clear DB ---
  document.getElementById('btnExportJson').onclick = () => {
    const metaList = allRecordings.map(r => ({
      id: r.id,
      title: r.title,
      phoneNumber: r.phoneNumber,
      callType: r.callType,
      timestamp: r.timestamp,
      duration: r.duration,
      size: r.size,
      format: r.format,
      isStarred: r.isStarred,
      notes: r.notes
    }));
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(metaList, null, 2));
    const dlAnchor = document.createElement('a');
    dlAnchor.setAttribute("href", dataStr);
    dlAnchor.setAttribute("download", `ACR_Backup_${new Date().toISOString().split('T')[0]}.json`);
    dlAnchor.click();
  };

  document.getElementById('btnClearDatabase').onclick = async () => {
    const dict = I18N[currentLang] || I18N.en;
    if (confirm(dict.confirm_clear_all)) {
      await dbClearAll();
      allRecordings = [];
      renderDashboard();
      renderLibrary();
    }
  };

  // --- Tab Navigation ---
  function switchTab(tabName) {
    document.querySelectorAll('.nav-tab').forEach(b => {
      b.classList.toggle('active', b.dataset.tab === tabName);
      b.setAttribute('aria-selected', b.dataset.tab === tabName ? 'true' : 'false');
    });
    document.querySelectorAll('.tab-view').forEach(v => {
      v.classList.toggle('active', v.id === `view-${tabName}`);
    });
  }

  document.querySelectorAll('.nav-tab').forEach(btn => {
    btn.onclick = () => switchTab(btn.dataset.tab);
  });

  document.getElementById('btnQuickRecord').onclick = () => switchTab('recorder');
  document.getElementById('btnGoLibrary').onclick = () => switchTab('library');

  // --- Search & Filter Listeners ---
  const searchInput = document.getElementById('searchInput');
  const clearSearchBtn = document.getElementById('clearSearchBtn');

  searchInput.oninput = () => {
    searchQuery = searchInput.value.trim();
    clearSearchBtn.style.display = searchQuery ? 'block' : 'none';
    renderLibrary();
  };

  clearSearchBtn.onclick = () => {
    searchInput.value = '';
    searchQuery = '';
    clearSearchBtn.style.display = 'none';
    renderLibrary();
  };

  document.querySelectorAll('.chip').forEach(ch => {
    ch.onclick = () => {
      document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
      ch.classList.add('active');
      currentFilter = ch.dataset.filter;
      renderLibrary();
    };
  });

  document.getElementById('sortSelect').onchange = (e) => {
    currentSort = e.target.value;
    renderLibrary();
  };

  // --- Language Selectors ---
  document.getElementById('languageSelect').onchange = (e) => applyLanguage(e.target.value);
  document.getElementById('settingsLanguageSelect').onchange = (e) => applyLanguage(e.target.value);

  // --- Check Android Bridge (when running inside Android WebView) ---
  function checkAndroidBridge() {
    if (window.AndroidBridge) {
      document.getElementById('diagPlatformText').textContent = "Android App WebView (Native Bridge Connected)";
      try {
        const info = JSON.parse(window.AndroidBridge.getDeviceInfo());
        if (info && info.model) {
          document.getElementById('diagPlatformText').textContent = `Android ${info.release} (API ${info.sdkInt}) • ${info.manufacturer} ${info.model}`;
        }
      } catch (e) {
        console.warn("Could not parse AndroidBridge device info:", e);
      }
    }
  }

  // --- Initializer ---
  async function init() {
    drawFlatVisualizer();
    try {
      await initDB();
      allRecordings = await dbGetAll();
    } catch (e) {
      console.warn("IndexedDB init failure, using memory fallback", e);
      allRecordings = [];
    }

    applyLanguage(currentLang);
    checkAndroidBridge();
  }

  init();
})();
