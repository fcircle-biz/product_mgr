# 商品管理システム

商品と在庫を効率的に管理するためのウェブシステムです。

## 概要

このシステムは小売業や卸売業向けの商品管理システムで、商品情報の登録・編集、在庫管理、入出庫処理、各種レポート出力などの機能を提供します。

## 主な機能

- **商品管理**: 商品の登録、詳細表示、編集、削除
- **在庫管理**: 在庫状況の確認、入出庫処理
- **在庫履歴**: 商品ごとの入出庫履歴の記録と表示
- **レポート**: 在庫推移、入出庫サマリー、在庫警告などの各種レポート

## システム要件

- **フロントエンド**: HTML, CSS, JavaScript, Bootstrap 5, Thymeleaf, Chart.js
- **バックエンド**: Java 17, Spring Boot 3.2, Spring Security, Spring Data JDBC
- **データベース**: PostgreSQL
- **ビルドツール**: Maven
- **コンテナ化**: Docker, Docker Compose

## ドキュメント

主要なドキュメントへのリンク：

- [要件定義書](docs/requirements.html)
- [画面仕様書一覧](docs/view/index.html)
- [画面遷移図](docs/screen_transition.drawio)
- [データベース設計図](docs/database_design.drawio)
- [テーブル定義書](docs/table_definition.html)

## 画面一覧

| カテゴリ | 画面名 | 概要 |
|---------|-------|------|
| 共通 | [ログイン画面](docs/view/login.html) | システムへのログイン認証を行う |
| 共通 | [メインメニュー](docs/view/main_menu.html) | システムの主要機能へのナビゲーション |
| 商品管理 | [商品一覧](docs/view/product_list.html) | 登録されている商品の一覧表示と検索 |
| 商品管理 | [商品登録](docs/view/product_create.html) | 新規商品の登録 |
| 商品管理 | [商品詳細](docs/view/product_detail.html) | 商品の詳細情報表示 |
| 商品管理 | [商品編集](docs/view/product_edit.html) | 商品情報の編集 |
| 在庫管理 | [在庫管理](docs/view/inventory.html) | 商品の在庫状況管理と入出庫処理 |
| 在庫管理 | [在庫履歴](docs/view/inventory_history.html) | 商品の入出庫履歴の表示 |
| レポート | [レポート画面](docs/view/report.html) | 各種統計レポートの表示 |

## 開発環境のセットアップ

### 前提条件

- Docker と Docker Compose がインストールされていること
- Java 17 (開発用、Dockerを使用する場合は不要)
- Maven (開発用、Dockerを使用する場合は不要)

### Dockerを使用したセットアップ

```bash
# リポジトリのクローン
git clone https://github.com/yourusername/product_mgr.git
cd product_mgr

# Docker Composeでアプリケーションを起動
docker compose up -d

# ブラウザで http://localhost:8080 にアクセス
# ログイン: admin / admin
```

### ローカル開発環境でのセットアップ

```bash
# リポジトリのクローン
git clone https://github.com/yourusername/product_mgr.git
cd product_mgr

# PostgreSQLのみDockerで起動
docker compose up -d postgres

# Mavenでビルドして実行
./mvnw spring-boot:run

# または
mvn spring-boot:run
```

## 主要機能の使い方

### 商品管理

- 商品一覧: `/products` - 登録されている全商品の一覧を表示・検索できます
- 商品登録: `/products/create` - 新規商品を登録します
- 商品詳細: `/products/{id}` - 商品の詳細情報を表示します

### 在庫管理

- 在庫一覧: `/inventory` - 全商品の在庫状況を確認できます
- 入庫処理: `/inventory/add` - 商品の入庫処理を行います
- 出庫処理: `/inventory/subtract` - 商品の出庫処理を行います
- 在庫履歴: `/inventory/history/{productId}` - 特定商品の入出庫履歴を表示します

### レポート機能

- レポートダッシュボード: `/reports` - 各種レポートメニューを表示します
- 日次レポート: `/reports/daily` - 日単位の入出庫状況を表示します
- 月次レポート: `/reports/monthly` - 月単位の入出庫状況を表示します
- 商品レポート: `/reports/product/{id}` - 商品ごとの在庫推移を表示します
- 在庫警告: `/reports/stock-warning` - 在庫切れ・在庫少の商品を表示します
- カテゴリ分布: `/reports/category-distribution` - カテゴリ別の商品分布を表示します

## データベース構造

システムは以下の主要テーブルで構成されています：

- **products**: 商品情報（商品名、説明、価格、カテゴリなど）
- **categories**: 商品カテゴリ
- **inventory_history**: 在庫の入出庫履歴
- **system_settings**: システム設定値

## プロジェクト構造

```
product_mgr/
├── src/main/
│   ├── java/com/example/productmgr/
│   │   ├── config/          # アプリケーション設定
│   │   ├── controller/      # MVCコントローラー
│   │   ├── model/           # データモデル
│   │   ├── repository/      # データアクセスレイヤー
│   │   ├── service/         # ビジネスロジック
│   │   └── ProductMgrApplication.java
│   └── resources/
│       ├── db/              # データベース初期化スクリプト
│       ├── static/          # 静的リソース
│       ├── templates/       # Thymeleafテンプレート
│       └── application.yml  # アプリケーション設定
└── pom.xml                  # Mavenプロジェクト設定
```

## ライセンス

当プロジェクトはMITライセンスの下で提供されています。

## テストの実行

### 単体テスト
```bash
mvn test
```

### E2Eテスト

E2Eテストは Selenium WebDriver を使用し、実際のブラウザでアプリケーションを操作してテストを行います。

#### 前提条件
- Chrome または Firefox ブラウザがインストールされていること
- WebDriverManagerが自動的に適切なドライバーをダウンロードします

#### 実行方法
```bash
# Chrome を使用してE2Eテストを実行（デフォルト）
mvn -Pe2e verify

# Firefox を使用してE2Eテストを実行
mvn -Pe2e verify -Dselenium.browser=firefox

# 個別のE2Eテストクラスを実行
mvn -Pe2e verify -Dit.test=AuthE2ETest

# CI環境でE2Eテストをスキップ
CI=true mvn verify
```

#### ヘッドレスモード
デフォルトでE2Eテストはヘッドレスモードで実行されます。これにより、GUIのないサーバー環境でも実行可能です。

#### トラブルシューティング
- Chrome が見つからない場合: `CHROME_BIN` 環境変数を設定してChromeのパスを指定してください
- WSL環境の場合: Chrome for Linux をインストールするか、Firefox を使用してください

## 貢献

1. このリポジトリをフォークします
2. 新しいブランチを作成します: `git checkout -b my-new-feature`
3. 変更をコミットします: `git commit -am 'Add some feature'`
4. ブランチにプッシュします: `git push origin my-new-feature`
5. プルリクエストを送信します