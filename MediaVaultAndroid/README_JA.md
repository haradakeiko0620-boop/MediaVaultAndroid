# MediaVault Android

元のPythonコードの「保存先管理・URLからの取得・動画/音声再生/変換」という役割を参考に、Android向けに作り直した土台です。

## 現在入っている機能
- アプリ内の `MediaVault` フォルダー
- 初回起動時に `Music` / `Video` / `Downloads` を自動作成
- フォルダー作成
- ファイル/フォルダーの名前変更
- ファイル/フォルダーの移動
- ファイル/フォルダーの削除
- MP3 / M4A / AAC / WAV / FLAC / OGG 再生
- MP4 / MKV / WebM / MOV / AVI 再生（端末/デコーダ対応範囲）
- 1曲リピート
- 全曲リピート
- シャッフル
- HTTP/HTTPSの直接URLからDownloadManagerで保存

## 重要
- YouTube等のサービス固有URLを解析して保存する機能は入れていません。
- DRM回避機能は入れていません。
- URL保存は、利用者が保存権限を持つファイルや、配布側がダウンロードを許可している直接URL向けです。
- 元Python版のFFmpeg変換機能は、このAndroid版v1には未実装です。

## Android Studio
1. Android Studioで `MediaVaultAndroid` フォルダーを開く
2. JDK 17を指定
3. SDK 36をインストール
4. Gradle Sync
5. Android端末をUSB接続
6. Run

## 技術構成
- Kotlin
- Android Views + ViewBinding
- AndroidX Media3 / ExoPlayer
- Android DownloadManager
- アプリ専用外部ストレージ

## 次に追加しやすい機能
- バックグラウンド再生 + 通知コントロール
- プレイリスト保存
- アルバムアート/動画サムネイル
- 再生位置の記憶
- SAFを使った端末内の任意フォルダー取り込み
- FFmpegKit代替またはMedia3 Transformerによる変換
