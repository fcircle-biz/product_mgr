# システム設定 E2Eテスト仕様書

本ドキュメントでは、商品管理システムにおけるシステム設定機能のEnd-to-End（E2E）テスト仕様を定義します。

## 1. テスト概要

システム設定機能のE2Eテストでは、以下の機能が正常に動作することを確認します。

- 設定ページへのアクセス制御（管理者権限確認）
- 基本設定の表示と更新
- 通知設定の表示と更新
- バックアップ設定の表示と更新
- タブ切り替え機能

## 2. テスト環境

### 2.1 必要環境

- Java 17以上
- Maven 3.6以上
- Docker および Docker Compose
- Playwright（自動的にダウンロードされます）

### 2.2 テスト実行方法

テストは以下のコマンドで実行できます：

```bash
./run-e2e-tests.sh
```

設定画面のテストのみを実行する場合：

```bash
POSTGRES_HOST=localhost POSTGRES_PORT=5434 POSTGRES_DB=productmgr_test POSTGRES_USER=testuser POSTGRES_PASSWORD=testpass mvn -DskipClean=true test -Dtest=com.example.productmgr.e2e.playwright.SettingsPlaywrightTest
```

> **注意：** テスト実行前にDockerが起動していることを確認してください。

## 3. テストケース

### 3.1 設定ページアクセステスト（testSettingsPageAccess）

| テスト項目 | 設定ページへのアクセス確認 |
|------------|---------------------------|
| 前提条件 | 管理者ユーザーでログイン済みであること |
| テスト手順 | 1. 設定ページ（/settings）にアクセスする<br>2. ページのロードが完了するまで待機する |
| 期待結果 | - 設定ページに正常にアクセスできること<br>- 設定フォームが表示されていること |
| 自動化ポイント | - URLの確認: `assertTrue(page.url().contains("/settings"), "設定ページに正しく移動していること");`<br>- フォームの表示確認: `assertTrue(page.isVisible("form"), "設定フォームが表示されていること");` |

### 3.2 アクセス制御テスト（testSettingsAccessControl）

| テスト項目 | 非管理者ユーザーのアクセス制御確認 |
|------------|----------------------------------|
| 前提条件 | ログインしていない状態であること |
| テスト手順 | 1. ログインせずに設定ページ（/settings）に直接アクセスする<br>2. ページのロードが完了するまで待機する |
| 期待結果 | - ログインページにリダイレクトされること |
| 自動化ポイント | - リダイレクト先の確認: `assertTrue(page.url().contains("/login"), "認証なしのアクセスは認証ページへリダイレクトされること");` |

### 3.3 基本設定更新テスト（testBasicSettingsUpdate）

| テスト項目 | 基本設定の更新機能確認 |
|------------|----------------------|
| 前提条件 | 管理者ユーザーでログイン済みであること |
| テスト手順 | 1. 設定ページ（/settings）にアクセスする<br>2. 基本設定タブが表示されていることを確認する<br>3. 会社名フィールドに新しい値を入力する<br>4. 在庫少警告しきい値に新しい値を入力する<br>5. 保存ボタンをクリックする<br>6. ページがリロードされるまで待機する |
| 期待結果 | - 成功メッセージが表示されること<br>- 会社名フィールドに更新した値が表示されていること<br>- 在庫少警告しきい値フィールドに更新した値が表示されていること |
| 自動化ポイント | - フォーム入力: `page.fill("#companyName", "テスト会社名" + System.currentTimeMillis() % 10000);`<br>- 数値入力: `page.fill("#lowStockThreshold", "15");`<br>- 保存ボタンクリック: `page.click("button[type=submit]");`<br>- 成功メッセージ確認: `assertTrue(page.isVisible(".alert-success"), "成功メッセージが表示されること");`<br>- 更新値の確認: `assertEquals("15", page.locator("#lowStockThreshold").inputValue(), "更新した値が保存されていること");` |

### 3.4 通知設定更新テスト（testNotificationSettingsUpdate）

| テスト項目 | 通知設定の更新機能確認 |
|------------|----------------------|
| 前提条件 | 管理者ユーザーでログイン済みであること |
| テスト手順 | 1. 設定ページ（/settings）にアクセスする<br>2. 通知設定タブをクリックする<br>3. 通知を有効にするチェックボックスを切り替える<br>4. 通知メールアドレスに新しい値を入力する<br>5. 保存ボタンをクリックする<br>6. ページがリロードされるまで待機する |
| 期待結果 | - 成功メッセージが表示されること<br>- 通知タブを再度クリックすると、設定が更新されていること |
| 自動化ポイント | - タブ切り替え: `page.click("#notification-tab");`<br>- チェックボックス操作: `page.click("#enableNotifications");`<br>- メールアドレス入力: `page.fill("#notificationEmail", "test" + System.currentTimeMillis() % 10000 + "@example.com");`<br>- 保存後の確認: `page.click("#notification-tab"); assertTrue(page.isChecked("#enableNotifications"), "チェックボックスの状態が保存されていること");` |

### 3.5 バックアップ設定更新テスト（testBackupSettingsUpdate）

| テスト項目 | バックアップ設定の更新機能確認 |
|------------|------------------------------|
| 前提条件 | 管理者ユーザーでログイン済みであること |
| テスト手順 | 1. 設定ページ（/settings）にアクセスする<br>2. バックアップタブをクリックする<br>3. 自動バックアップを有効にするチェックボックスを切り替える<br>4. バックアップ間隔に新しい値を入力する<br>5. 保存ボタンをクリックする<br>6. ページがリロードされるまで待機する |
| 期待結果 | - 成功メッセージが表示されること<br>- バックアップタブを再度クリックすると、設定が更新されていること |
| 自動化ポイント | - タブ切り替え: `page.click("#backup-tab");`<br>- チェックボックス操作: `page.click("#autoBackup");`<br>- バックアップ間隔入力: `page.fill("#backupIntervalDays", "14");`<br>- 保存後の確認: `page.click("#backup-tab"); assertEquals("14", page.locator("#backupIntervalDays").inputValue(), "バックアップ間隔が正しく保存されていること");` |

### 3.6 タブ切り替えテスト（testTabSwitching）

| テスト項目 | 設定タブ切り替え機能確認 |
|------------|------------------------|
| 前提条件 | 管理者ユーザーでログイン済みであること |
| テスト手順 | 1. 設定ページ（/settings）にアクセスする<br>2. 通知設定タブをクリックする<br>3. 通知設定フォームが表示されるまで待機する<br>4. バックアップタブをクリックする<br>5. バックアップ設定フォームが表示されるまで待機する<br>6. 基本設定タブをクリックする<br>7. 基本設定フォームが表示されるまで待機する |
| 期待結果 | - 各タブクリック後に対応するフォームが表示されること<br>- アクティブなタブが視覚的に区別されること |
| 自動化ポイント | - 通知タブ切り替え: `page.click("#notification-tab");`<br>- タブコンテンツ表示確認: `assertTrue(page.isVisible("#notification form"), "通知設定フォームが表示されること");`<br>- タブのアクティブ状態確認: `assertTrue(page.locator("#notification-tab").hasClass("active"), "通知タブがアクティブ状態であること");` |

## 4. テストデータ設計

各テストは独立して実行できるよう、テストデータを動的に生成します。

### 4.1 テストデータ生成方法

```java
// システム時刻を使った一意の値の生成
String uniqueCompanyName = "テスト会社" + System.currentTimeMillis() % 10000;
String uniqueEmail = "test" + System.currentTimeMillis() % 10000 + "@example.com";

// 設定値の更新
page.fill("#companyName", uniqueCompanyName);
page.fill("#notificationEmail", uniqueEmail);
```

## 5. テスト実装サンプル

以下は設定更新テストの実装サンプルです。

```java
@Test
public void testBasicSettingsUpdate() {
    // 管理者としてログイン
    login();
    
    // 設定ページに移動
    page.navigate(baseUrl + "/settings");
    page.waitForLoadState();
    
    // ページが正しく読み込まれていることを確認
    assertTrue(page.url().contains("/settings"), "設定ページに正しく移動していること");
    
    // 一意の会社名を生成
    String uniqueCompanyName = "テスト会社" + System.currentTimeMillis() % 10000;
    
    // フォームに値を入力
    page.fill("#companyName", uniqueCompanyName);
    page.fill("#lowStockThreshold", "15");
    
    // 保存ボタンをクリック
    page.click("button[type=submit]");
    
    // 成功メッセージを待機
    page.waitForSelector(".alert-success");
    
    // 値が保存されていることを確認
    assertEquals(uniqueCompanyName, page.inputValue("#companyName"));
    assertEquals("15", page.inputValue("#lowStockThreshold"));
}
```

## 6. テスト実行結果

テスト結果は `target/surefire-reports/` ディレクトリに出力されます。

> **注意事項：**
> - 設定更新テストは実際のシステム設定を変更するため、テスト環境でのみ実行してください。
> - 設定変更後のシステム動作確認も考慮すると良いでしょう。
> - セッション管理やCSRF対策が正しく機能しているか確認するため、複数ブラウザでの同時操作テストも検討してください。

## 7. トラブルシューティング

### 7.1 よくある問題と解決策

| 問題 | 考えられる原因 | 解決策 |
|------|---------------|--------|
| 管理者アクセスが必要なテストの失敗 | テストユーザーに管理者権限がない | ログイン前にテストユーザーが管理者であることを確認する |
| 設定の保存後にリダイレクトされない | フォーム送信の失敗またはバリデーションエラー | フォーム入力値を確認し、エラーメッセージを検証する |
| タブ切り替えが機能しない | JavaScriptの実行エラーまたはCSSセレクタの不一致 | ページのJavaScriptエラーを確認し、正しいセレクタを使用する |

### 7.2 デバッグ方法

設定画面テストのデバッグには以下のアプローチが有効です：

- コンソール出力を活用する
- スクリーンショットを取得する
- タイムアウト値を調整する

```java
// デバッグ情報の出力例
System.out.println("Current form values - Company Name: " + page.inputValue("#companyName"));
System.out.println("Active tab: " + page.locator(".list-group-item.active").textContent());

// スクリーンショットの取得
page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("settings-debug.png")));
```