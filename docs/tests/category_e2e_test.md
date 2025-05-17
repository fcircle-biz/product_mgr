# カテゴリ管理 E2Eテスト仕様書

本ドキュメントでは、商品管理システムにおけるカテゴリ管理機能のEnd-to-End（E2E）テスト仕様を定義します。

## 1. テスト概要

カテゴリ管理機能のE2Eテストでは、以下の機能が正常に動作することを確認します。

- カテゴリ一覧の表示
- カテゴリの新規登録
- カテゴリ情報の編集
- カテゴリの削除

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
POSTGRES_HOST=localhost POSTGRES_PORT=5434 POSTGRES_DB=productmgr_test POSTGRES_USER=testuser POSTGRES_PASSWORD=testpass mvn -DskipClean=true test -Dtest=com.example.productmgr.e2e.playwright.CategoryPlaywrightTest
```

> **注意：** テスト実行前にDockerが起動していることを確認してください。

## 3. テストケース

### 3.1 カテゴリ一覧表示テスト（testCategoryList）

| テスト項目 | カテゴリ一覧ページの表示確認 |
|------------|------------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. カテゴリ一覧ページ（/categories）にアクセスする<br>2. ページのロードが完了するまで待機する<br>3. ページタイトルが表示されるまで待機する |
| 期待結果 | - ページタイトルが「カテゴリー一覧」と表示されること<br>- カテゴリテーブルが表示されること |
| 自動化ポイント | - セレクタ `.d-flex h2` でページタイトルを検証<br>- `table` 要素の存在確認 |

### 3.2 カテゴリ新規登録テスト（testCreateCategory）

| テスト項目 | 新規カテゴリの登録機能確認 |
|------------|---------------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. カテゴリ作成ページ（/categories/new）にアクセスする<br>2. フォームタイトルが表示されるまで待機する<br>3. 一意のカテゴリ名を生成する<br>4. カテゴリ名と説明を入力する<br>5. 送信ボタンをクリックする |
| 期待結果 | - カテゴリ一覧ページにリダイレクトされること<br>- 成功メッセージが表示されること<br>- 作成したカテゴリが一覧に表示されること |
| 自動化ポイント | - 一意のカテゴリ名生成: `String uniqueCategoryName = "テストカテゴリ" + System.currentTimeMillis() % 10000;`<br>- フォーム入力: `page.fill("input[name=name]", uniqueCategoryName);`<br>- 作成カテゴリの確認: `page.waitForSelector("td:has-text('" + uniqueCategoryName + "')", ...);` |

### 3.3 カテゴリ編集テスト（testEditCategory）

| テスト項目 | カテゴリ情報編集機能確認 |
|------------|------------------------|
| 前提条件 | - ユーザーがログイン済みであること |
| テスト手順 | 1. カテゴリ作成ページで新しいカテゴリを作成する（データ独立性確保）<br>2. カテゴリ一覧ページに遷移したことを確認する<br>3. 作成したカテゴリの行を特定し、編集ボタンをクリックする<br>4. カテゴリ名と説明を更新する<br>5. 更新ボタンをクリックする |
| 期待結果 | - カテゴリ一覧ページにリダイレクトされること<br>- 成功メッセージが表示されること<br>- 更新されたカテゴリ名が一覧に表示されること |
| 自動化ポイント | - 編集ボタンの特定: `page.locator("tr:has-text('" + originalName + "') a[title='編集']").first().click();`<br>- フォーム値の検証: `assertEquals(originalName, page.locator("input[name=name]").inputValue());`<br>- 更新カテゴリの確認: `page.waitForSelector("td:has-text('" + updatedName + "')", ...);` |

### 3.4 カテゴリ削除テスト（testDeleteCategory）

| テスト項目 | カテゴリ削除機能確認 |
|------------|----------------------|
| 前提条件 | ユーザーがログイン済みであること |
| テスト手順 | 1. カテゴリ作成ページで新しいカテゴリを作成する（データ独立性確保）<br>2. カテゴリ一覧ページに遷移したことを確認する<br>3. 作成したカテゴリの行を特定し、削除ボタンをクリックする<br>4. 確認ダイアログで「OK」をクリックする |
| 期待結果 | - カテゴリ一覧ページが再読み込みされること<br>- 成功メッセージが表示されること<br>- 削除したカテゴリが一覧から消えていること |
| 自動化ポイント | - 削除ボタンの特定: `page.locator("tr:has-text('" + categoryName + "') a[title='削除']").first().click();`<br>- 確認ダイアログ処理: `page.onDialog(dialog -> dialog.accept());`<br>- 削除確認: `assertFalse(page.locator("td:has-text('" + categoryName + "')").count() > 0);` |

## 4. フロー図

### カテゴリCRUD操作のフロー

```
+-------------------+    +------------------------+    +----------------------+
| カテゴリ一覧表示   | -> | 新規カテゴリ作成       | -> | カテゴリ編集          |
+-------------------+    +------------------------+    +----------------------+
         |                          |                           |
         |                          v                           v
         |                +------------------------+    +----------------------+
         |                | 一覧ページへ戻る       | <- | 一覧ページへ戻る      |
         |                +------------------------+    +----------------------+
         v                          |                           
+-------------------+               |                           
| カテゴリ削除      | <-------------+                           
+-------------------+                                           
         |                                                      
         v                                                      
+-------------------+                                           
| 削除確認          |                                           
+-------------------+                                           
```

## 5. テストデータ設計

各テストは独立して実行できるよう、テストデータを自動生成します。

### 5.1 カテゴリデータ生成方法

```java
// 一意のカテゴリ名生成（タイムスタンプ利用）
String uniqueCategoryName = "テストカテゴリ" + System.currentTimeMillis() % 10000;

// カテゴリ情報入力
page.fill("input[name=name]", uniqueCategoryName);
page.fill("textarea[name=description]", "これはテスト用カテゴリの説明です。");
```

## 6. テスト実装サンプル

以下はカテゴリ削除テストの実装サンプルです。

```java
@Test
public void testDeleteCategory() {
    // まず削除するためのカテゴリを新規作成する
    // カテゴリ作成ページにアクセス
    page.navigate(baseUrl + "/categories/new");
    page.waitForLoadState();
    
    // フォームタイトルが表示されるまで待機
    page.waitForSelector("h5.card-title", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    
    // ユニークなカテゴリ名を生成
    String categoryName = "削除用カテゴリ-" + System.currentTimeMillis() % 10000;
    
    // 最小限の情報を入力
    page.fill("input[name=name]", categoryName);
    page.fill("textarea[name=description]", "これは削除テスト用のカテゴリです。");
    
    // 送信ボタンをクリック
    page.click("button[type=submit]");
    
    // カテゴリ一覧ページにリダイレクトされるのを待つ
    page.waitForURL("**/categories", new Page.WaitForURLOptions().setTimeout(5000));
    page.waitForLoadState();
    
    // 成功メッセージが表示されることを確認
    page.waitForSelector(".alert-success", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    
    // 作成したカテゴリの行を見つける
    page.waitForSelector("td:has-text('" + categoryName + "')", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    
    // ダイアログハンドラを設定（クリック前に設定することが重要）
    page.onDialog(dialog -> dialog.accept());
    
    // 同じ行の削除ボタンをクリック
    page.locator("tr:has-text('" + categoryName + "') a[title='削除']").first().click();
    
    // カテゴリ一覧ページを再読み込み
    page.waitForLoadState();
    
    // 成功メッセージが表示されることを確認
    page.waitForSelector(".alert-success", 
                      new Page.WaitForSelectorOptions()
                          .setState(WaitForSelectorState.VISIBLE)
                          .setTimeout(5000));
    
    // 削除したカテゴリが一覧に表示されなくなったことを確認（数秒待機）
    try {
        Thread.sleep(2000); // 画面の更新を待つ
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    
    // 削除したカテゴリを検索
    boolean categoryExists = page.locator("td:has-text('" + categoryName + "')").count() > 0;
    
    // カテゴリが存在しないことを確認
    assertFalse(categoryExists, "削除したはずのカテゴリが一覧に表示されています: " + categoryName);
}
```

## 7. テスト実行結果

テスト結果は `target/surefire-reports/` ディレクトリに出力されます。

> **注意事項：**
> - 商品に関連付けられているカテゴリは削除できないため、必ず新しいカテゴリを作成してからテストします。
> - 確認ダイアログの処理は、クリック前にハンドラを設定する必要があります。

## 8. トラブルシューティング

### 8.1 よくある問題と解決策

| 問題 | 考えられる原因 | 解決策 |
|------|---------------|--------|
| ダイアログが操作できない | ダイアログハンドラの設定タイミングが不適切 | クリック前にダイアログハンドラを設定する: `page.onDialog(dialog -> dialog.accept());` |
| 削除ボタンが見つからない | セレクタが不正確または要素構造が変更された | 実際のHTML構造を確認し、セレクタを修正する |
| カテゴリ削除後も一覧に表示される | 削除操作の完了前に検証している | 短い待機時間を追加して画面更新を待つ |

### 8.2 デバッグ方法

カテゴリ関連のテスト問題を診断するには、以下の情報を確認してください：

- カテゴリ一覧テーブルのHTML構造
- 実際に表示されているボタンのタイトル属性値
- 確認ダイアログの発生と処理状況

```java
// カテゴリ一覧テーブルのデバッグ
System.out.println("テーブル行数: " + page.locator("table tbody tr").count());
System.out.println("削除ボタン数: " + page.locator("a[title='削除']").count());
System.out.println("対象カテゴリ表示確認: " + page.locator("td:has-text('" + categoryName + "')").count());
```

## 9. まとめ

カテゴリ管理機能のE2Eテストでは、カテゴリのCRUD操作（作成・読み取り・更新・削除）が正常に機能することを確認します。
各テストは独立して実行できるように設計され、テストデータを自動生成することで再現性を確保しています。

特に削除操作では、確認ダイアログの処理が重要なポイントとなります。クリック前にダイアログハンドラを設定することで、
自動テストでもダイアログの操作を確実に行うことができます。