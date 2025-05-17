# pgAudit監査機能テスト仕様書

## 1. はじめに

### 1.1 目的
本文書は、商品管理システムにおけるpgAudit監査機能のテスト仕様を定義するものです。データベース操作の監査ログが正確に記録され、適切に管理されることを確認するためのテスト計画と手順を記述します。

### 1.2 対象範囲
本テストは以下の機能を対象とします：
- pgAudit拡張機能のインストールと設定
- 各種データベース操作（SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP）の監査ログ記録
- 監査ログの保存と管理
- 監査ログのアクセス制御

## 2. テスト環境

### 2.1 システム構成
テスト環境は、以下のコンポーネントで構成されます：
- PostgreSQL 15（pgAudit拡張機能あり）
- Spring Boot 3.2アプリケーション
- テスト用Dockerコンテナ
- Playwright（E2Eテストフレームワーク）

### 2.2 テスト用アカウント
テストには以下のアカウントを使用します：
- 管理者アカウント：
  - ユーザー名: `admin`
  - パスワード: `admin`
- テストユーザーアカウント：
  - ユーザー名: `testuser`
  - パスワード: `test123`

## 3. テスト内容

### 3.1 pgAudit設定テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-001 | pgAudit拡張機能の確認 | PostgreSQLにpgAudit拡張機能がインストールされていることを確認する。 | pg_extensionテーブルでpgAuditの存在が確認できる。 | 高 |
| PA-002 | pgAudit設定の確認 | pgAuditの設定パラメータ（pgaudit.log）を確認する。 | 設定値が意図したとおりに設定されている（write, ddl, role, misc）。 | 高 |
| PA-003 | ログファイル設定の確認 | PostgreSQLのログファイル設定を確認する。 | ログファイルがCSV形式で保存され、適切なディレクトリに出力されている。 | 高 |

### 3.2 データ操作監査テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-004 | SELECT操作の監査 | 商品情報の参照操作（SELECT）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、SELECT操作、対象テーブル、実行ユーザーの情報が記録されている。 | 中 |
| PA-005 | INSERT操作の監査 | 新規商品の追加操作（INSERT）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、INSERT操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |
| PA-006 | UPDATE操作の監査 | 商品情報の更新操作（UPDATE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、UPDATE操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |
| PA-007 | DELETE操作の監査 | 商品の削除操作（DELETE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、DELETE操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |
| PA-008 | 在庫数変更操作の監査 | 商品の在庫数増減操作を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、UPDATE操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |

### 3.3 スキーマ操作監査テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-009 | CREATE操作の監査 | テスト用テーブルの作成操作（CREATE TABLE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、CREATE操作、作成されたテーブル名、実行ユーザーの情報が記録されている。 | 中 |
| PA-010 | ALTER操作の監査 | テスト用テーブルの変更操作（ALTER TABLE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、ALTER操作、対象テーブル、実行ユーザーの情報が記録されている。 | 中 |
| PA-011 | DROP操作の監査 | テスト用テーブルの削除操作（DROP TABLE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、DROP操作、対象テーブル、実行ユーザーの情報が記録されている。 | 中 |

### 3.4 ログ管理テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-012 | ログローテーションの確認 | ログローテーションの設定を確認し、実際にローテーションが発生するか検証する。 | 日次でログファイルがローテーションされ、新しいログファイルが作成される。 | 中 |
| PA-013 | 古いログの削除確認 | 設定された保存期間（30日）を超えた古いログファイルが自動的に削除されるか検証する。 | 30日より古いログファイルが自動的に削除される。 | 低 |

### 3.5 アクセス制御テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-014 | 管理者の監査ログアクセス | 管理者権限を持つユーザーが監査ログにアクセスできるか検証する。 | 管理者は監査ログを閲覧できる。 | 高 |
| PA-015 | 一般ユーザーの監査ログアクセス制限 | 一般ユーザー権限を持つユーザーが監査ログにアクセスできないことを検証する。 | 一般ユーザーは監査ログの閲覧を試みると、アクセス拒否される。 | 高 |

## 4. E2Eテスト手順

### 4.1 テスト実行手順
E2Eテストは以下の手順で実行します：

1. テスト環境の準備
   - PostgreSQLコンテナをテスト用設定で起動
   - テストデータベーススキーマを初期化
   - pgAudit拡張機能を有効化

2. テストスクリプトの実行
   - 全てのpgAudit監査テストを実行する場合：
     ```
     ./run-e2e-tests.sh
     ```
   - 特定のテストクラスのみを実行する場合：
     ```
     ./run-single-e2e-test.sh PgAuditPlaywrightTest
     ```
   - 特定のテストメソッドのみを実行する場合：
     ```
     ./run-single-e2e-test.sh PgAuditPlaywrightTest#testInsertOperationAudit
     ```

3. テスト結果の確認
   - テスト結果レポートを確認（`target/surefire-reports/`）
   - PostgreSQLログファイルでAUDITエントリを確認

### 4.2 監査ログ確認手順
監査ログの確認は以下のコマンドを使用して実行します：

```bash
# PostgreSQLログディレクトリを確認
docker exec product-mgr-postgres ls -la /var/log/postgresql/

# 最新のログファイルを確認
docker exec product-mgr-postgres tail -n 50 /var/log/postgresql/postgresql-*.csv | grep AUDIT

# ログファイルをCSV形式で読み込み
docker exec product-mgr-postgres cat /var/log/postgresql/postgresql-*.csv | grep AUDIT
```

## 5. トラブルシューティング

### 5.1 一般的な問題と解決策

| 問題 | 考えられる原因 | 解決策 |
|------|--------------|--------|
| pgAuditログが記録されない | - pgAudit拡張機能が正しくインストールされていない<br>- 設定パラメータが正しく設定されていない | - PostgreSQL拡張機能の確認:<br>`docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "SELECT * FROM pg_extension WHERE extname = 'pgaudit';"`<br>- 設定の確認:<br>`docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "SHOW pgaudit.log;"` |
| ログローテーションが動作しない | - logrotate設定が正しくない<br>- 権限の問題 | - logrotate設定の確認:<br>`docker exec product-mgr-postgres cat /etc/logrotate.conf`<br>- 手動でのlogrotate実行:<br>`docker exec product-mgr-postgres logrotate -f /etc/logrotate.conf` |
| E2Eテストが失敗する | - テスト環境の問題<br>- タイムアウト設定が不適切<br>- テストデータの不整合 | - テスト環境のクリーンアップと再起動:<br>`docker compose down -v && docker compose up -d`<br>- タイムアウト設定の調整:<br>`BasePlaywrightTest.javaのタイムアウト値を増加`<br>- デバッグログの有効化:<br>`src/test/resources/application-e2e.ymlでログレベルをDEBUGに設定` |

## 6. テスト完了基準
以下の条件を満たした場合、pgAudit監査機能のテストは完了とみなします：

1. すべての高優先度テストケースが成功していること
2. 中優先度テストケースの90%以上が成功していること
3. 監査ログが正しく記録され、適切に管理されていることが確認できること
4. アクセス制御が適切に機能していることが確認できること

## 7. 付録

### 7.1 pgAudit設定パラメータ

| パラメータ | 説明 | 設定値 |
|------------|------|--------|
| pgaudit.log | 監査対象の操作タイプを指定 | write, ddl, role, misc |
| pgaudit.log_catalog | カタログ操作の監査対象を指定 | off |
| pgaudit.log_client | クライアントに監査ログを送信するかを指定 | off |
| pgaudit.log_level | 監査ログのログレベルを指定 | log |
| pgaudit.log_parameter | SQLパラメータを記録するかを指定 | on |
| pgaudit.log_relation | 各ステートメントでアクセスされる関連をログに記録するかを指定 | on |
| pgaudit.log_statement_once | 一回のステートメントで一つのログエントリを生成するかを指定 | off |

### 7.2 参考リンク
- [pgAudit公式ドキュメント](https://github.com/pgaudit/pgaudit)
- [PostgreSQL 15 ドキュメント](https://www.postgresql.org/docs/15/index.html)
- [Playwright for Java ドキュメント](https://playwright.dev/java/)

---

文書作成日: 2025年5月17日  
文書管理番号: TEST-PGAUDIT-001  
バージョン: 1.0