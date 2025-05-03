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

- **フロントエンド**: HTML, CSS, JavaScript
- **バックエンド**: 未定（実装時に決定）
- **データベース**: PostgreSQL

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

```bash
# リポジトリのクローン
git clone https://github.com/yourusername/product_mgr.git
cd product_mgr

# 開発環境のセットアップ（未定）
# TODO: 実装時に更新
```

## ライセンス

当プロジェクトのライセンスは未定です。

## 貢献

貢献に関するガイドラインは、実装フェーズで追加される予定です。