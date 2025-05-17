# 商品管理 E2Eテスト仕様書

本ドキュメントでは、商品管理システムにおける商品管理機能のEnd-to-End（E2E）テスト仕様を定義します。

## 1. テスト概要

商品管理機能のE2Eテストでは、以下の機能が正常に動作することを確認します。

- 商品一覧の表示
- 商品の新規登録
- 商品の詳細表示
- 商品情報の編集
- 商品検索

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
POSTGRES_HOST=localhost POSTGRES_PORT=5434 POSTGRES_DB=productmgr_test POSTGRES_USER=testuser POSTGRES_PASSWORD=testpass mvn -DskipClean=true test -Dtest=com.example.productmgr.e2e.playwright.ProductPlaywrightTest
```

> **注意：** テスト実行前にDockerが起動していることを確認してください。

## 3. テストケース

### 3.1 商品一覧表示テスト（testProductList）

| テスト項目 | 商品一覧ページの表示確認 |
|------------|--------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 商品一覧ページ（/products）にアクセスする<br>2. ページのロードが完了するまで待機する<br>3. ページタイトルが表示されるまで待機する |
| 期待結果 | - ページタイトルが「商品一覧」と表示されること<br>- 商品テーブルが表示されること |
| 自動化ポイント | - セレクタ `h2` でページタイトルを検証<br>- `table` 要素の存在確認 |

### 3.2 商品新規登録テスト（testCreateProduct）

| テスト項目 | 新規商品の登録機能確認 |
|------------|------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 商品登録ページ（/products/new）にアクセスする<br>2. フォームタイトルが表示されるまで待機する<br>3. 一意の商品名を生成する<br>4. 商品名、価格、在庫数量を入力する<br>5. カテゴリを選択する<br>6. 送信ボタンをクリックする |
| 期待結果 | - 商品詳細ページにリダイレクトされること<br>- 作成した商品情報が表示されること |
| 自動化ポイント | - 一意の商品名生成: `String uniqueProductName = "テスト" + System.currentTimeMillis() % 10000;`<br>- フォーム入力: `page.fill("input[name=name]", uniqueProductName);`<br>- カテゴリ選択: `categorySelect.selectOption(new SelectOption().setIndex(1));`<br>- URL遷移確認: `page.waitForURL("**/products/*", ...);` |

### 3.3 商品編集テスト（testEditProduct）

| テスト項目 | 商品情報編集機能確認 |
|------------|----------------------|
| 前提条件 | - ユーザーがログイン済みであること |
| テスト手順 | 1. 商品登録ページで新しい商品を作成する（データ独立性確保）<br>2. 商品詳細ページに遷移したことを確認する<br>3. 編集ボタンをクリックする<br>4. 商品名を更新する<br>5. 更新ボタンをクリックする |
| 期待結果 | - 商品詳細ページにリダイレクトされること<br>- 更新された商品名が表示されること |
| 自動化ポイント | - 編集ボタンの特定: `page.waitForSelector("a.btn-primary:has-text('編集')", ...);`<br>- 更新前後の商品名を変数で保持して比較 |

### 3.4 商品検索テスト（testSearchProduct）

| テスト項目 | 商品検索機能確認 |
|------------|------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 検索用の特徴的な名前の商品を新規作成する<br>2. 商品一覧ページに移動する<br>3. 検索フォームに特徴的なキーワードを入力する<br>4. 検索ボタンをクリックする |
| 期待結果 | - 検索結果ページにリダイレクトされること（URLにkeywordパラメータが含まれる）<br>- 検索結果に作成した商品が表示されること |
| 自動化ポイント | - 特徴的な商品名の生成: `String uniqueSearchName = "XYZ検索用商品" + System.currentTimeMillis() % 10000;`<br>- 検索結果の確認: `page.waitForSelector("td:has-text('" + uniqueSearchName + "')", ...);`<br>- URL検証: `page.waitForURL("**/products?keyword=*", ...);` |

### 3.5 商品詳細表示テスト（testProductDetail）

| テスト項目 | 商品詳細表示機能確認 |
|------------|----------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. 詳細表示用の商品を新規作成する<br>2. 商品詳細ページにリダイレクトされるのを待つ<br>3. ページタイトルが表示されるまで待機する |
| 期待結果 | - ページタイトルが「商品詳細」と表示されること<br>- 作成した商品名が表示されていること<br>- 商品説明が表示されていること<br>- 基本情報セクションが表示されること<br>- 在庫情報セクションが表示されること<br>- 在庫数が正しく表示されていること<br>- 編集ボタンが表示されること<br>- 入出庫処理ボタンが表示されること |
| 自動化ポイント | - セクション見出しの確認: `page.isVisible("h5.card-title:has-text('基本情報')")`<br>- 在庫数の確認: `page.isVisible("span:has-text('25 個')")`<br>- ボタンの確認: `page.isVisible("a:has-text('編集')")` |

## 4. テストデータ設計

各テストは独立して実行できるよう、テストデータを自動生成します。

### 4.1 商品データ生成方法

```java
// 一意の商品名生成（タイムスタンプ利用）
String uniqueProductName = "テスト" + System.currentTimeMillis() % 10000;

// 商品情報入力
page.fill("input[name=name]", uniqueProductName);
page.fill("input[name=price]", "100");
page.fill("input[name=stockQuantity]", "10");
```

## 5. テスト実装サンプル

以下は商品登録テストの実装サンプルです。

```java
@Test
public void testCreateProduct() {
    // 商品登録ページにアクセス
    page.navigate(baseUrl + "/products/new");
    page.waitForLoadState();
    
    // フォームタイトルが表示されるまで待機
    page.waitForSelector("h5.card-title", 
                       new Page.WaitForSelectorOptions()
                           .setState(WaitForSelectorState.VISIBLE)
                           .setTimeout(5000));
    
    // フォームタイトルを確認
    Locator title = page.locator("h5.card-title");
    assertEquals("新規商品登録", title.textContent().trim());
    
    // 短めの一意の商品名
    String uniqueProductName = "テスト" + System.currentTimeMillis() % 10000;
    
    // 最小限の情報だけ入力
    page.fill("input[name=name]", uniqueProductName);
    page.fill("input[name=price]", "100");
    page.fill("input[name=stockQuantity]", "10");
    
    // カテゴリを選択（最初のオプション以外を選択）
    Locator categorySelect = page.locator("select[name=categoryId]");
    int optionsCount = categorySelect.locator("option").count();
    if (optionsCount > 1) {
        // 2番目のオプションを選択（インデックスは0から始まるため、1を指定）
        categorySelect.selectOption(new SelectOption().setIndex(1));
    }
    
    // 送信ボタンをクリック
    page.click("button[type=submit]");
    
    // 商品詳細ページにリダイレクトされることを確認
    page.waitForURL("**/products/*", new Page.WaitForURLOptions().setTimeout(5000));
}
```

## 6. テスト実行結果

テスト結果は `target/surefire-reports/` ディレクトリに出力されます。

> **注意事項：**
> - E2Eテストはアプリケーションの仕様変更に敏感です。UIの変更があった場合は、テストコードも同時に更新してください。
> - 特にセレクタやテキスト検証部分は、HTMLやUIテキストの変更に伴って修正が必要になります。

## 7. トラブルシューティング

### 7.1 よくある問題と解決策

| 問題 | 考えられる原因 | 解決策 |
|------|---------------|--------|
| セレクタタイムアウトエラー | 要素が見つからない、またはページの読み込みが遅い | タイムアウト値を増やす、またはセレクタを見直す |
| アサーションエラー | 期待値と実際の値が一致しない | UIの実際のテキストを確認し、期待値を更新する |
| テストデータの競合 | テスト間でデータが干渉している | 各テストでユニークなデータを生成する |

### 7.2 デバッグ方法

テスト実行中の問題を診断するには、以下の情報を確認してください：

- コンソール出力
- surefire-reportsディレクトリのレポート
- 各テストメソッドの`System.out.println()`によるデバッグメッセージ

```java
// デバッグ情報の出力例
System.out.println("Current URL: " + page.url());
System.out.println("Page content: " + page.content());
```