# 商品管理システム要件定義書

## 1. はじめに

### 1.1 目的
本文書は、商品管理システムの開発に関する要件を定義するものです。商品の登録、更新、検索、および在庫管理を効率的に行うためのシステム要件を明確にします。また、pgAuditによるデータベース監査機能および関連するテスト要件も含みます。

### 1.2 対象範囲
本システムは以下の機能を対象とします：
- 商品データの登録・更新・削除
- 商品カテゴリ管理
- 在庫管理
- 商品検索
- レポート生成
- ユーザー管理
- データベース監査（pgAudit）

## 2. システム概要

### 2.1 システム構成
本システムはWebアプリケーションとして実装され、以下のコンポーネントで構成されます：
- フロントエンド：HTML, CSS, JavaScript (Thymeleaf)
- バックエンド：Java (Spring Boot)
- データベース：PostgreSQL 15（pgAudit拡張機能あり）
- 監視機能：pgAuditによるデータベース操作の監査
- テスト環境：Playwright（E2Eテストフレームワーク）

### 2.2 利用者
本システムの利用者は以下の通りです：
- 管理者：システム全体の管理権限を持ちます
- 商品管理者：商品情報の登録・更新権限を持ちます
- 在庫管理者：在庫状況の確認・更新権限を持ちます
- 閲覧者：情報の閲覧のみ可能です

## 3. 機能要件

### 3.1 商品管理機能

| 機能ID | 機能名 | 説明 | 優先度 |
|--------|--------|------|--------|
| F-001 | 商品登録 | 新規商品をシステムに登録する機能 | 高 |
| F-002 | 商品検索 | 商品名、カテゴリ、JANコードなどで商品を検索する機能 | 高 |
| F-003 | 商品詳細表示 | 商品の詳細情報を表示する機能 | 高 |
| F-004 | 商品情報更新 | 既存の商品情報を更新する機能 | 高 |
| F-005 | 商品削除 | 登録済みの商品を削除（論理削除）する機能 | 中 |
| F-006 | 商品一括登録 | CSVファイルから複数の商品を一括登録する機能 | 低 |

### 3.2 カテゴリ管理機能

| 機能ID | 機能名 | 説明 | 優先度 |
|--------|--------|------|--------|
| F-007 | カテゴリ登録 | 新規カテゴリをシステムに登録する機能 | 高 |
| F-008 | カテゴリ一覧表示 | カテゴリの一覧を表示する機能 | 高 |
| F-009 | カテゴリ編集 | 既存のカテゴリ情報を更新する機能 | 中 |
| F-010 | カテゴリ削除 | カテゴリを削除する機能 | 中 |
| F-011 | 階層カテゴリ管理 | 親子関係を持つ階層カテゴリを管理する機能 | 中 |

### 3.3 在庫管理機能

| 機能ID | 機能名 | 説明 | 優先度 |
|--------|--------|------|--------|
| F-012 | 在庫確認 | 商品の現在の在庫状況を確認する機能 | 高 |
| F-013 | 在庫追加 | 商品の在庫を増やす機能 | 高 |
| F-014 | 在庫減少 | 商品の在庫を減らす機能 | 高 |
| F-015 | 在庫履歴表示 | 商品ごとの在庫変動履歴を表示する機能 | 中 |
| F-016 | 在庫アラート設定 | 在庫が指定数量を下回った場合にアラートを出す機能 | 低 |

### 3.4 レポート機能

| 機能ID | 機能名 | 説明 | 優先度 |
|--------|--------|------|--------|
| F-017 | 在庫レポート | 現在の在庫状況をレポート形式で出力する機能 | 高 |
| F-018 | カテゴリ別商品数レポート | カテゴリごとの商品数をグラフ化して表示する機能 | 中 |
| F-019 | 日次在庫変動レポート | 日ごとの在庫変動をレポート形式で出力する機能 | 中 |
| F-020 | 月次在庫変動レポート | 月ごとの在庫変動をレポート形式で出力する機能 | 中 |
| F-021 | 在庫回転率レポート | 商品ごとの在庫回転率をレポート形式で出力する機能 | 低 |

### 3.5 ユーザー管理機能

| 機能ID | 機能名 | 説明 | 優先度 |
|--------|--------|------|--------|
| F-022 | ユーザー登録 | 新規ユーザーをシステムに登録する機能 | 高 |
| F-023 | ユーザー一覧表示 | 登録済みユーザーの一覧を表示する機能 | 高 |
| F-024 | ユーザー編集 | 既存ユーザー情報を更新する機能 | 中 |
| F-025 | ユーザー削除 | ユーザーを削除（無効化）する機能 | 中 |
| F-026 | 権限管理 | ユーザーごとの権限を設定する機能 | 高 |

### 3.6 システム管理機能

| 機能ID | 機能名 | 説明 | 優先度 |
|--------|--------|------|--------|
| F-027 | ログイン・認証 | ユーザー認証を行う機能 | 高 |
| F-028 | パスワード変更 | ユーザーのパスワードを変更する機能 | 高 |
| F-029 | システム設定 | システム全体の設定を管理する機能 | 中 |
| F-030 | ログ管理 | システムの操作ログを記録・閲覧する機能 | 中 |
| F-031 | データバックアップ | データベースのバックアップを作成する機能 | 低 |

### 3.7 データベース監査機能

| 機能ID | 機能名 | 説明 | 優先度 |
|--------|--------|------|--------|
| F-032 | pgAudit設定 | PostgreSQLのpgAudit拡張機能を設定する機能 | 高 |
| F-033 | 監査ログ記録 | データベース操作の監査ログを記録する機能 | 高 |
| F-034 | 監査ログ閲覧 | 記録された監査ログを閲覧する機能 | 高 |
| F-035 | 監査ログフィルタリング | 特定条件での監査ログの絞り込み機能 | 中 |
| F-036 | 監査レポート生成 | 監査ログを基にしたレポート生成機能 | 低 |

## 4. 非機能要件

### 4.1 性能要件

| 要件ID | 項目 | 説明 | 優先度 |
|--------|------|------|--------|
| NF-001 | レスポンス時間 | 通常の操作は3秒以内に応答すること | 高 |
| NF-002 | 同時接続ユーザー数 | 最大50人の同時ユーザーに対応可能であること | 中 |
| NF-003 | データベース容量 | 最大10万件の商品データを扱えること | 高 |
| NF-004 | バックアップ時間 | バックアップ処理は30分以内に完了すること | 低 |
| NF-005 | 監査ログ記録の影響 | pgAuditによる監査ログ記録がシステム全体のパフォーマンスに10%以上の低下をもたらさないこと | 中 |

### 4.2 セキュリティ要件

| 要件ID | 項目 | 説明 | 優先度 |
|--------|------|------|--------|
| NF-006 | ユーザー認証 | ID/パスワードによる認証を行うこと | 高 |
| NF-007 | パスワード管理 | パスワードはハッシュ化して保存すること | 高 |
| NF-008 | 権限制御 | 機能ごとに利用権限を制御できること | 高 |
| NF-009 | 操作ログ | 重要な操作はログに記録すること | 中 |
| NF-010 | データ暗号化 | 通信データはSSL/TLSで暗号化すること | 中 |
| NF-011 | 監査ログアクセス制御 | 監査ログへのアクセスは管理者権限を持つユーザーのみに制限すること | 高 |
| NF-012 | 監査ログの完全性 | 監査ログの改ざんや削除ができないよう保護すること | 高 |

### 4.3 運用要件

| 要件ID | 項目 | 説明 | 優先度 |
|--------|------|------|--------|
| NF-013 | 稼働時間 | 24時間365日の稼働が可能であること | 高 |
| NF-014 | バックアップ | 1日1回の自動バックアップを行うこと | 高 |
| NF-015 | データ復旧 | 障害発生時にデータを復旧できること | 高 |
| NF-016 | メンテナンス | 計画的なメンテナンスが実施可能であること | 中 |
| NF-017 | 監査ログローテーション | 監査ログは日次でローテーションし、30日間保持すること | 中 |

### 4.4 互換性要件

| 要件ID | 項目 | 説明 | 優先度 |
|--------|------|------|--------|
| NF-018 | ブラウザ対応 | 主要ブラウザ（Chrome, Firefox, Safari, Edge）に対応すること | 高 |
| NF-019 | レスポンシブ対応 | PC, タブレット, スマートフォンに対応したレイアウトであること | 中 |
| NF-020 | 外部システム連携 | 将来的に外部システムとのデータ連携が可能な設計とすること | 低 |

## 5. 画面要件

### 5.1 共通要件
- すべての画面で共通のヘッダー・フッターを表示する
- ログイン状態とユーザー名を表示する
- メニューからは主要機能に簡単にアクセスできるようにする

### 5.2 画面一覧

| 画面ID | 画面名 | 説明 | 優先度 |
|--------|--------|------|--------|
| S-001 | ログイン画面 | ユーザー認証を行うための画面 | 高 |
| S-002 | メインメニュー画面 | 各機能へのアクセスを提供するメニュー画面 | 高 |
| S-003 | 商品一覧画面 | 登録されている商品の一覧を表示する画面 | 高 |
| S-004 | 商品詳細画面 | 商品の詳細情報を表示する画面 | 高 |
| S-005 | 商品登録・編集画面 | 商品情報を登録・編集するための画面 | 高 |
| S-006 | カテゴリ管理画面 | カテゴリの一覧・登録・編集を行う画面 | 高 |
| S-007 | 在庫一覧画面 | 在庫状況の一覧を表示する画面 | 高 |
| S-008 | 在庫操作画面 | 在庫の増減を行うための画面 | 高 |
| S-009 | 在庫履歴画面 | 商品ごとの在庫履歴を表示する画面 | 中 |
| S-010 | レポート一覧画面 | 各種レポートへのアクセスを提供する画面 | 中 |
| S-011 | ユーザー管理画面 | ユーザーの一覧・登録・編集を行う画面 | 高 |
| S-012 | システム設定画面 | システム全体の設定を行う画面 | 中 |
| S-013 | 監査ログ一覧画面 | データベース監査ログの一覧を表示する画面 | 高 |
| S-014 | 監査ログ詳細画面 | 特定の監査ログの詳細を表示する画面 | 中 |

## 6. 外部インターフェース要件

### 6.1 ユーザーインターフェース
- Webブラウザ上で動作するHTML/CSS/JavaScriptのUI
- Bootstrap等のフレームワークを利用し、統一感のあるデザインとする
- 直感的な操作が可能なUI設計とする

### 6.2 ソフトウェアインターフェース
- PostgreSQLデータベースとの連携
- Spring Bootによるバックエンド処理
- Thymeleafによるテンプレート処理
- pgAuditとの連携

### 6.3 通信インターフェース
- HTTPSによる安全な通信
- JSONによるデータ交換（将来的なAPI連携用）

## 7. データ移行要件

### 7.1 移行対象データ
- 商品マスタ
- カテゴリマスタ
- 在庫データ
- ユーザーデータ

### 7.2 移行方式
- CSVデータによる一括登録
- 管理画面からのデータインポート機能

## 8. テスト要件

### 8.1 テスト種類
- 単体テスト
- 統合テスト
- システムテスト
- 受入テスト
- E2Eテスト（Playwright）

### 8.2 テスト環境
- 開発環境：開発者のPC環境
- テスト環境：専用のテスト用サーバー
- ステージング環境：本番に近い構成の検証環境
- 本番環境：実際のサービス提供環境

### 8.3 pgAudit監査機能テスト

#### 8.3.1 pgAudit設定テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-001 | pgAudit拡張機能の確認 | PostgreSQLにpgAudit拡張機能がインストールされていることを確認する。 | pg_extensionテーブルでpgAuditの存在が確認できる。 | 高 |
| PA-002 | pgAudit設定の確認 | pgAuditの設定パラメータ（pgaudit.log）を確認する。 | 設定値が意図したとおりに設定されている（write, ddl, role, misc）。 | 高 |
| PA-003 | ログファイル設定の確認 | PostgreSQLのログファイル設定を確認する。 | ログファイルがCSV形式で保存され、適切なディレクトリに出力されている。 | 高 |

#### 8.3.2 データ操作監査テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-004 | SELECT操作の監査 | 商品情報の参照操作（SELECT）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、SELECT操作、対象テーブル、実行ユーザーの情報が記録されている。 | 中 |
| PA-005 | INSERT操作の監査 | 新規商品の追加操作（INSERT）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、INSERT操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |
| PA-006 | UPDATE操作の監査 | 商品情報の更新操作（UPDATE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、UPDATE操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |
| PA-007 | DELETE操作の監査 | 商品の削除操作（DELETE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、DELETE操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |
| PA-008 | 在庫数変更操作の監査 | 商品の在庫数増減操作を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、UPDATE操作、対象テーブル、実行ユーザーの情報が記録されている。 | 高 |

#### 8.3.3 スキーマ操作監査テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-009 | CREATE操作の監査 | テスト用テーブルの作成操作（CREATE TABLE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、CREATE操作、作成されたテーブル名、実行ユーザーの情報が記録されている。 | 中 |
| PA-010 | ALTER操作の監査 | テスト用テーブルの変更操作（ALTER TABLE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、ALTER操作、対象テーブル、実行ユーザーの情報が記録されている。 | 中 |
| PA-011 | DROP操作の監査 | テスト用テーブルの削除操作（DROP TABLE）を実行し、監査ログを確認する。 | AUDITタグを含むログエントリがあり、DROP操作、対象テーブル、実行ユーザーの情報が記録されている。 | 中 |

#### 8.3.4 ログ管理テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-012 | ログローテーションの確認 | ログローテーションの設定を確認し、実際にローテーションが発生するか検証する。 | 日次でログファイルがローテーションされ、新しいログファイルが作成される。 | 中 |
| PA-013 | 古いログの削除確認 | 設定された保存期間（30日）を超えた古いログファイルが自動的に削除されるか検証する。 | 30日より古いログファイルが自動的に削除される。 | 低 |

#### 8.3.5 アクセス制御テスト

| ID | テスト項目 | テスト内容 | 期待結果 | 優先度 |
|----|-----------|-----------|----------|--------|
| PA-014 | 管理者の監査ログアクセス | 管理者権限を持つユーザーが監査ログにアクセスできるか検証する。 | 管理者は監査ログを閲覧できる。 | 高 |
| PA-015 | 一般ユーザーの監査ログアクセス制限 | 一般ユーザー権限を持つユーザーが監査ログにアクセスできないことを検証する。 | 一般ユーザーは監査ログの閲覧を試みると、アクセス拒否される。 | 高 |

### 8.4 E2Eテスト手順

E2Eテストは以下の手順で実行します：

1. テスト環境の準備
   - PostgreSQLコンテナをテスト用設定で起動
   - テストデータベーススキーマを初期化
   - pgAudit拡張機能を有効化

2. テストスクリプトの実行
   - 全てのテストを実行する場合：
     ```bash
     ./run-e2e-tests.sh
     ```
   - 特定のテストクラスのみを実行する場合：
     ```bash
     ./run-single-e2e-test.sh PgAuditPlaywrightTest
     ```
   - 特定のテストメソッドのみを実行する場合：
     ```bash
     ./run-single-e2e-test.sh PgAuditPlaywrightTest#testInsertOperationAudit
     ```

3. テスト結果の確認
   - テスト結果レポートを確認（`target/surefire-reports/`）
   - PostgreSQLログファイルでAUDITエントリを確認

### 8.5 監査ログ確認手順

監査ログの確認は以下のコマンドを使用して実行します：

```bash
# PostgreSQLログディレクトリを確認
docker exec product-mgr-postgres ls -la /var/log/postgresql/

# 最新のログファイルを確認
docker exec product-mgr-postgres tail -n 50 /var/log/postgresql/postgresql-*.csv | grep AUDIT

# ログファイルをCSV形式で読み込み
docker exec product-mgr-postgres cat /var/log/postgresql/postgresql-*.csv | grep AUDIT
```

## 9. 制約条件

### 9.1 技術的制約
- Spring Boot 3.x系を使用すること
- PostgreSQL 15.x系を使用すること
- Java 17以上で開発すること
- pgAuditによる監査機能を実装すること

### 9.2 業務的制約
- 商品数が将来的に増加しても対応可能な設計とすること
- 在庫管理の履歴は最低5年間保持すること
- 操作性を重視し、ユーザートレーニングが最小限になるよう考慮すること
- 監査ログは法令遵守およびセキュリティ監査のために適切に保持すること

## 10. その他要件

### 10.1 ドキュメント
- システム設計書
- 操作マニュアル
- 管理者マニュアル
- データベース定義書
- 監査ログ管理マニュアル

### 10.2 保守・運用
- 障害発生時の対応フロー
- バックアップと復元の手順
- セキュリティアップデートの適用方法
- 監査ログの確認・分析手順

## 付録

### A. pgAudit設定パラメータ

| パラメータ | 説明 | 設定値 |
|------------|------|--------|
| pgaudit.log | 監査対象の操作タイプを指定 | write, ddl, role, misc |
| pgaudit.log_catalog | カタログ操作の監査対象を指定 | off |
| pgaudit.log_client | クライアントに監査ログを送信するかを指定 | off |
| pgaudit.log_level | 監査ログのログレベルを指定 | log |
| pgaudit.log_parameter | SQLパラメータを記録するかを指定 | on |
| pgaudit.log_relation | 各ステートメントでアクセスされる関連をログに記録するかを指定 | on |
| pgaudit.log_statement_once | 一回のステートメントで一つのログエントリを生成するかを指定 | off |

### B. 参考リンク
- [pgAudit公式ドキュメント](https://github.com/pgaudit/pgaudit)
- [PostgreSQL 15 ドキュメント](https://www.postgresql.org/docs/15/index.html)
- [Spring Boot ドキュメント](https://spring.io/projects/spring-boot)
- [Playwright for Java ドキュメント](https://playwright.dev/java/)

---

文書作成日: 2025年5月17日  
文書管理番号: REQ-002  
バージョン: 1.0