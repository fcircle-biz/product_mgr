# 在庫管理 E2Eテスト仕様書

本ドキュメントでは、商品管理システムにおける在庫管理機能のEnd-to-End（E2E）テスト仕様を定義します。

## 1. テスト概要

在庫管理機能のE2Eテストでは、以下の機能が正常に動作することを確認します。

- 在庫一覧の表示
- 在庫追加（入庫処理）
- 在庫削減（出庫処理）
- 在庫履歴の表示

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
POSTGRES_HOST=localhost POSTGRES_PORT=5434 POSTGRES_DB=productmgr_test POSTGRES_USER=testuser POSTGRES_PASSWORD=testpass mvn -DskipClean=true test -Dtest=com.example.productmgr.e2e.playwright.InventoryPlaywrightTest
```

> **注意：** テスト実行前にDockerが起動していることを確認してください。

## 3. テストケース

### 3.1 在庫一覧表示テスト（testInventoryList）

| テスト項目 | 在庫一覧ページの表示確認 |
|------------|--------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 在庫一覧ページ（/inventory）にアクセスする<br>2. ページのロードが完了するまで待機する<br>3. ページタイトルが表示されるまで待機する |
| 期待結果 | - ページタイトルが「在庫管理」と表示されること<br>- 在庫テーブルが表示されること |
| 自動化ポイント | - セレクタ `h2` でページタイトルを検証<br>- `table` 要素の存在確認 |

### 3.2 在庫追加テスト（testAddInventory）

| テスト項目 | 在庫追加（入庫処理）機能確認 |
|------------|------------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 在庫追加用の商品を新規作成する（初期在庫数は5個）<br>2. 商品詳細ページから入出庫処理ボタンをクリックする<br>3. 在庫追加ボタンをクリックする<br>4. 在庫追加フォームに追加数（10個）と理由を入力する<br>5. 送信ボタンをクリックする |
| 期待結果 | - 商品詳細ページにリダイレクトされること<br>- 成功メッセージが表示されること<br>- 在庫数が正しく更新されていること（5個 + 10個 = 15個） |
| 自動化ポイント | - 商品作成: `page.fill("input[name=stockQuantity]", "5");`<br>- 在庫追加: `page.fill("input[name=quantityChange]", "10");`<br>- 在庫確認: `page.waitForSelector("span:has-text('15 個')", ...);` |

### 3.3 在庫削減テスト（testSubtractInventory）

| テスト項目 | 在庫削減（出庫処理）機能確認 |
|------------|------------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 在庫削減用の商品を新規作成する（初期在庫数は20個）<br>2. 商品詳細ページから入出庫処理ボタンをクリックする<br>3. 在庫削減ボタンをクリックする<br>4. 在庫削減フォームに削減数（5個）と理由を入力する<br>5. 送信ボタンをクリックする |
| 期待結果 | - 商品詳細ページにリダイレクトされること<br>- 成功メッセージが表示されること<br>- 在庫数が正しく更新されていること（20個 - 5個 = 15個） |
| 自動化ポイント | - 商品作成: `page.fill("input[name=stockQuantity]", "20");`<br>- 在庫削減: `page.fill("input[name=quantityChange]", "5");`<br>- 在庫確認: `page.waitForSelector("span:has-text('15 個')", ...);` |

### 3.4 在庫履歴表示テスト（testInventoryHistory）

| テスト項目 | 在庫履歴表示機能確認 |
|------------|----------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 在庫履歴用の商品を新規作成する（初期在庫数は10個）<br>2. 商品詳細ページから入出庫処理ボタンをクリックする<br>3. 在庫追加を行い履歴を作成する（+5個）<br>4. 商品詳細ページに戻る<br>5. 在庫履歴ボタンをクリックする |
| 期待結果 | - 在庫履歴ページが表示されること<br>- ページタイトルが「在庫履歴」と表示されること<br>- 在庫履歴テーブルが表示されること<br>- 先ほど追加した履歴（+5個）が表示されていること |
| 自動化ポイント | - 履歴確認: `page.waitForSelector("td:has-text('履歴テスト用在庫追加')", ...);`<br>- ページタイトル確認: `assertEquals("在庫履歴", historyTitle.textContent().trim());` |

## 4. フロー図

### 在庫追加テストのフロー

```
+-------------------+    +------------------------+    +----------------------+
| 商品新規作成       | -> | 入出庫処理ボタンクリック | -> | 在庫追加ボタンクリック |
+-------------------+    +------------------------+    +----------------------+
         |                                                       |
         v                                                       v
+-------------------+    +------------------------+    +----------------------+
| 商品詳細確認       | <- | 成功メッセージ確認      | <- | 在庫追加情報入力       |
+-------------------+    +------------------------+    +----------------------+
         |
         v
+-------------------+
| 更新された在庫確認  |
+-------------------+
```

## 5. テストデータ設計

各テストは独立して実行できるよう、テストデータを自動生成します。

### 5.1 商品データ生成方法

```java
// 特徴的な商品名を生成
String productName = "在庫追加テスト用商品" + System.currentTimeMillis() % 10000;

// 最小限の情報を入力
page.fill("input[name=name]", productName);
page.fill("input[name=price]", "2000");
page.fill("input[name=stockQuantity]", "5");  // 初期在庫数
```

### 5.2 在庫処理データ

```java
// 在庫追加情報
page.fill("input[name=quantityChange]", "10");  // 追加数
page.fill("textarea[name=reason]", "Playwrightテスト用在庫追加");  // 追加理由

// 在庫削減情報
page.fill("input[name=quantityChange]", "5");  // 削減数
page.fill("textarea[name=reason]", "Playwrightテスト用在庫削減");  // 削減理由
```

## 6. テスト実装サンプル

以下は在庫追加テストの実装サンプルです。

```java
@Test
public void testAddInventory() {
    // まず追加するための商品を作成する
    // 商品登録ページにアクセス
    page.navigate(baseUrl + "/products/new");
    page.waitForLoadState();
    
    // フォームタイトルが表示されるまで待機
    page.waitForSelector("h5.card-title", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    
    // 特徴的な商品名を生成
    String productName = "在庫追加テスト用商品" + System.currentTimeMillis() % 10000;
    
    // 最小限の情報を入力
    page.fill("input[name=name]", productName);
    page.fill("input[name=price]", "2000");
    page.fill("input[name=stockQuantity]", "5");
    
    // カテゴリを選択
    Locator categorySelect = page.locator("select[name=categoryId]");
    int optionsCount = categorySelect.locator("option").count();
    if (optionsCount > 1) {
        categorySelect.selectOption(new com.microsoft.playwright.options.SelectOption().setIndex(1));
    }
    
    // 送信ボタンをクリック
    page.click("button[type=submit]");
    
    // 商品詳細ページにリダイレクトされるのを待つ
    page.waitForURL("**/products/*", new Page.WaitForURLOptions().setTimeout(5000));
    page.waitForLoadState();
    
    // ページタイトルが表示されるまで待機
    page.waitForSelector("h2", 
                       new Page.WaitForSelectorOptions()
                           .setState(WaitForSelectorState.VISIBLE)
                           .setTimeout(5000));
        
    // 在庫追加ボタンをクリック
    page.waitForSelector("a:has-text('入出庫処理')", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    page.click("a:has-text('入出庫処理')");
    
    // 入出庫メニューページが表示されるまで待つ
    page.waitForLoadState();
    
    // 在庫追加ボタンをクリック
    page.waitForSelector("a:has-text('在庫追加')", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    page.click("a:has-text('在庫追加')");
    
    // 在庫追加フォームが表示されるまで待つ
    page.waitForLoadState();
    
    // 在庫追加フォームが表示されることを確認
    page.waitForSelector("h5.card-title:has-text('在庫追加')", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    
    // 在庫追加情報を入力
    page.fill("input[name=quantityChange]", "10");
    page.fill("textarea[name=reason]", "Playwrightテスト用在庫追加");
    
    // 追加ボタンをクリック
    page.click("button[type=submit]");
    
    // 商品詳細ページにリダイレクトされることを確認
    page.waitForURL("**/products/*", new Page.WaitForURLOptions().setTimeout(5000));
    page.waitForLoadState();
    
    // 成功メッセージが表示されることを確認
    page.waitForSelector(".alert-success", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    
    // 在庫数が15になっていることを確認 (5 + 10)
    page.waitForSelector("span:has-text('15 個')", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
}
```

## 7. テスト実行結果

テスト結果は `target/surefire-reports/` ディレクトリに出力されます。

> **注意事項：**
> - 在庫数が0未満になるようなテストは避けてください。数量チェックが行われるため、テストが失敗します。
> - 在庫数が大きすぎる値（Integer.MAX_VALUE近く）を指定すると、オーバーフローの可能性があります。

## 8. トラブルシューティング

### 8.1 よくある問題と解決策

| 問題 | 考えられる原因 | 解決策 |
|------|---------------|--------|
| セレクタタイムアウトエラー | 要素が見つからない、またはページの読み込みが遅い | タイムアウト値を増やす、またはセレクタを見直す |
| 在庫数の検証失敗 | 在庫数の表示形式が変更された | 現在のHTML構造を確認し、セレクタを更新する |
| 入出庫ボタンが見つからない | 商品詳細ページのレイアウト変更 | 最新のHTMLを確認して正しいセレクタを使用する |

### 8.2 デバッグ方法

在庫関連のテスト問題を診断するには、以下の情報を確認してください：

- 商品詳細ページの在庫表示部分のHTML構造
- 在庫追加/削減フォームのHTMLレイアウト
- 在庫履歴テーブルの構造

```java
// ページ構造のデバッグ出力例
System.out.println("在庫表示要素: " + page.locator("span:has-text('個')").count() + "個見つかりました");
System.out.println("在庫表示テキスト: " + page.locator("span:has-text('個')").first().textContent());
```

## 9. まとめ

在庫管理機能のE2Eテストでは、商品在庫の追加・削減・履歴表示の一連の流れをテストします。
各テストは独立して実行できるように設計され、テストデータを自動生成することで再現性を確保しています。

テストの改善点や新機能の追加に応じて、このテスト仕様は継続的に更新されるべきです。