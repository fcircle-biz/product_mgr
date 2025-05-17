# 認証機能 E2Eテスト仕様書

本ドキュメントでは、商品管理システムにおける認証機能のEnd-to-End（E2E）テスト仕様を定義します。

## 1. テスト概要

認証機能のE2Eテストでは、以下の機能が正常に動作することを確認します。

- ログイン機能
- ログアウト機能
- 認証エラー処理

> **セキュリティ上の注意点：** 認証テストはシステムのセキュリティに関わる重要なテストです。特に不正なログイン試行や認証バイパスのテストには注意が必要です。テスト環境でのみ実施し、本番環境に対して実行しないでください。

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

特定のテストクラスのみを実行する場合：

```bash
POSTGRES_HOST=localhost POSTGRES_PORT=5434 POSTGRES_DB=productmgr_test POSTGRES_USER=testuser POSTGRES_PASSWORD=testpass mvn -DskipClean=true test -Dtest=com.example.productmgr.e2e.playwright.AuthPlaywrightTest
```

> **注意：** テスト実行前にDockerが起動していることを確認してください。

### 2.3 テスト用アカウント

E2Eテストでは以下のテスト用アカウントを使用します。これらのアカウントはテスト初期化時に自動的に作成されます。

| ユーザー名 | パスワード | 権限 | 用途 |
|------------|------------|------|------|
| admin | admin | 管理者 | 管理者ログインテスト |
| testuser | test123 | 一般ユーザー | 一般ユーザーログインテスト（必要に応じて） |

## 3. テストケース

### 3.1 ログイン成功テスト（testSuccessfulLogin）

| テスト項目 | ログイン機能の動作確認 |
|------------|----------------------|
| 前提条件 | ユーザーがログアウト状態であること |
| テスト手順 | 1. ログインページ（/login）にアクセスする<br>2. ページのロードが完了するまで待機する<br>3. ページタイトルを確認する<br>4. ログインフォームが表示されることを確認する<br>5. ユーザー名「admin」とパスワード「admin」を入力する<br>6. ログインボタンをクリックする |
| 期待結果 | - メインメニューページにリダイレクトされること<br>- ウェルカムバナーが表示されること |
| 自動化ポイント | - 認証情報入力: `page.fill("input[name=username]", ADMIN_USERNAME);`<br>- ナビゲーション待機: `page.waitForNavigation(() -> { page.click("button[type=submit]"); });`<br>- メインメニュー確認: `page.waitForSelector(".welcome-banner", ...);` |

### 3.2 ログイン失敗テスト（testFailedLogin）

| テスト項目 | 不正なログイン認証情報の処理確認 |
|------------|------------------------------|
| 前提条件 | ユーザーがログアウト状態であること |
| テスト手順 | 1. ログインページ（/login）にアクセスする<br>2. ページのロードが完了するまで待機する<br>3. 入力フィールドが表示されるまで待機する<br>4. ユーザー名「admin」と不正なパスワード「wrong-password」を入力する<br>5. ログインボタンをクリックする |
| 期待結果 | - ログインページに留まること<br>- エラーメッセージが表示されること |
| 自動化ポイント | - 不正パスワード設定: `page.fill("input[name=password]", "wrong-password-" + System.currentTimeMillis() % 10000);`<br>- エラーメッセージ確認: `page.waitForSelector(".alert-danger", ...);` |

### 3.3 ログアウトテスト（testLogout）

| テスト項目 | ログアウト機能の動作確認 |
|------------|------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. login()ヘルパーメソッドを使用してログインする<br>2. メインメニューページが表示されることを確認する<br>3. ナビゲーションバーが表示されることを確認する<br>4. ログアウトリンクが表示されることを確認する<br>5. ログアウトリンクをクリックする |
| 期待結果 | - ログインページにリダイレクトされること<br>- ログインフォームが表示されること<br>- ログアウト成功メッセージが表示されること |
| 自動化ポイント | - ログアウトリンク特定: `page.waitForSelector("a:has-text('ログアウト')", ...);`<br>- ナビゲーション待機: `page.waitForNavigation(() -> { page.click("a:has-text('ログアウト')"); });`<br>- 成功メッセージ確認: `page.waitForSelector(".alert-success", ...);` |

## 4. フロー図

### 認証テストのフロー

```
+-------------------+    +------------------------+    +----------------------+
| ログインページアクセス | -> | 認証情報入力          | -> | ログインボタンクリック |
+-------------------+    +------------------------+    +----------------------+
         |                                                       |
         |                                                       v
         |                                         +------------------------+
         |                                         | 認証成功／失敗の分岐    |
         |                                         +------------------------+
         |                                           /                  \
         |                                          /                    \
         |                                         v                      v
+-------------------+    +------------------------+    +----------------------+
| ログアウト処理     | <- | メインメニュー表示      |    | エラーメッセージ表示  |
+-------------------+    +------------------------+    +----------------------+
         |
         v
+-------------------+
| ログインページ確認  |
+-------------------+
```

## 5. テスト実装サンプル

以下はログイン成功テストの実装サンプルです。

```java
@Test
public void testSuccessfulLogin() {
    // ログインページへアクセス
    page.navigate(baseUrl + "/login");
    page.waitForLoadState();
    
    // ページタイトルを確認
    assertEquals("ログイン | 商品管理システム", page.title());
    
    // ログインフォームが表示されるまで待機
    page.waitForSelector("form", 
                       new Page.WaitForSelectorOptions()
                           .setState(WaitForSelectorState.VISIBLE)
                           .setTimeout(5000));
    
    // ログインフォームが表示されることを確認
    assertTrue(page.isVisible("form"));
    
    // 入力フィールドが表示されるまで待機
    page.waitForSelector("input[name=username]", 
                        new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(5000));
    page.waitForSelector("input[name=password]", 
                        new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(5000));
    
    // 認証情報を入力
    page.fill("input[name=username]", ADMIN_USERNAME);
    page.fill("input[name=password]", ADMIN_PASSWORD);
    
    // ログインボタンをクリックしてナビゲーションを待機
    page.waitForNavigation(() -> {
        page.click("button[type=submit]");
    });
    
    // メインメニューページにリダイレクトされるのを待機
    page.waitForURL("**/menu", new Page.WaitForURLOptions().setTimeout(10000));
    page.waitForLoadState();
    
    // メインメニューのタイトルが表示されるまで待機
    page.waitForSelector(".welcome-banner", 
                       new Page.WaitForSelectorOptions()
                           .setState(WaitForSelectorState.VISIBLE)
                           .setTimeout(10000));
    
    // メインメニューが表示されることを確認
    assertTrue(page.isVisible(".welcome-banner"));
}
```

## 6. セキュリティテスト考慮事項

認証機能のテストでは、以下のセキュリティ観点も考慮すべきです。ただし、これらの高度なセキュリティテストは、通常のE2Eテストとは別に専用のセキュリティテストスイートとして実装することを推奨します。

1. **ブルートフォース攻撃対策**: 連続した不正ログイン試行に対するアカウントロックやCAPTCHA表示などの対策
2. **セッション管理**: ログアウト後のセッション無効化、セッションタイムアウト
3. **CSRF対策**: ログインフォームにCSRFトークンが含まれているか
4. **HTTPSリダイレクト**: HTTP接続がHTTPSにリダイレクトされるか
5. **パスワードポリシー**: 強度の低いパスワードが拒否されるか

> **セキュリティテスト注意点：** セキュリティテストは専門知識が必要です。テスト用のアカウントのみを使用し、本番環境に対しては実行しないでください。また、テスト実行前には関係者に通知することを推奨します。

## 7. テスト実行結果

テスト結果は `target/surefire-reports/` ディレクトリに出力されます。

> **注意事項：**
> - タイムアウト値は環境に応じて調整が必要な場合があります。特にネットワーク環境や負荷状況によって変動します。
> - 認証情報（ユーザー名、パスワード）はテスト環境のものを使用し、ソースコード内でハードコードしないことを推奨します。可能であれば環境変数から読み込むようにしてください。

## 8. トラブルシューティング

### 8.1 よくある問題と解決策

| 問題 | 考えられる原因 | 解決策 |
|------|---------------|--------|
| ログインボタンクリック後のナビゲーションタイムアウト | サーバー側の認証処理に時間がかかっている | タイムアウト値を増やす: `page.waitForURL("**/menu", new Page.WaitForURLOptions().setTimeout(20000));` |
| ログイン成功後にメインメニューの要素が見つからない | 要素のセレクタが変更された、またはページ構造が変わった | 最新のHTML構造を確認し、正しいセレクタを使用する |
| ログアウト後のリダイレクトが期待通りに動作しない | リダイレクト先が変更された | リダイレクト先のURLパターンを確認し、待機条件を更新する |

### 8.2 デバッグ方法

認証関連のテスト問題を診断するには、以下の情報を確認してください：

- 現在のURL: `page.url()`
- ページ内容: `page.content()`
- エラーメッセージの有無: `page.locator(".alert-danger").count()`

```java
// デバッグ情報の出力例
System.err.println("Navigation or element wait timed out: " + e.getMessage());
System.err.println("Current URL: " + page.url());
System.err.println("Page content: " + page.content());
```

## 9. まとめ

認証機能のE2Eテストでは、ユーザーログイン・ログアウトの基本的なフローと、認証エラー処理を検証します。
これにより、ユーザーが実際に使用する際の認証プロセスが正常に機能することを確認できます。

認証機能はセキュリティに直結する重要な機能であるため、テストの綿密な設計と実行が必要です。
また、実際のユーザー体験に近い形でテストを実施することで、認証プロセスの使いやすさも同時に検証できます。