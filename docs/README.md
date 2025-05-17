# 製品管理システム ドキュメント

このページでは製品管理システムの各種ドキュメントへのリンクを提供しています。

## 仕様書

- [要件定義書](specifications/integrated_requirements.md)  
  システムの要件と機能仕様について詳細に記述しています。

- [pgAudit監査機能テスト仕様書](specifications/pgaudit_e2e_test.md)  
  pgAudit監査機能のテスト仕様と実行手順について記述しています。

## データベース設計

- [テーブル定義書](database/table_definition.md)  
  各テーブルのカラム定義とデータ型の詳細です。

- [テーブル定義書 (PostgreSQL向け)](database/table_definition_postgresql.md)  
  PostgreSQL向けのテーブル定義と特有の機能について記載しています。

## システム設計図

- [クラス図 (Mermaid)](diagrams/class_diagram.md)  
  システムのクラス構造を図示しています。

- [クラス図 (日本語版, Mermaid)](diagrams/class_diagram_japanese.md)  
  日本語表記のクラス図です。

- [画面遷移図 (Mermaid)](diagrams/screen_transition.md)  
  システムの画面遷移を図示しています。

## シーケンス図

- [商品作成フロー (Mermaid)](diagrams/sequence_product_create.md)  
  商品作成時の処理フローを示しています。

- [商品一覧表示フロー (Mermaid)](diagrams/sequence_product_list.md)  
  商品一覧表示時の処理フローを示しています。

- [商品更新フロー (Mermaid)](diagrams/sequence_product_update.md)  
  商品情報更新時の処理フローを示しています。

- [在庫追加フロー (Mermaid)](diagrams/sequence_inventory_add.md)  
  在庫追加処理のフローを示しています。

- [在庫減少フロー (Mermaid)](diagrams/sequence_inventory_subtract.md)  
  在庫減少処理のフローを示しています。

## 画面モックアップ

- [モックアップ一覧](mockups/README.md)  
  すべての画面モックアップを表示します。

- [ログイン画面](mockups/login.html)
- [メインメニュー](mockups/main_menu.html)
- [商品一覧画面](mockups/product_list.html)
- [商品詳細画面](mockups/product_detail.html)
- [商品作成画面](mockups/product_create.html)
- [在庫管理画面](mockups/inventory.html)

## 実装ビュー

- [ビュー一覧](views/README.md)  
  すべての実装ビューを表示します。

- [ログイン画面](views/login.md)
- [メインメニュー](views/main_menu.md)
- [商品一覧画面](views/product_list.md)
- [商品詳細画面](views/product_detail.md)
- [商品作成画面](views/product_create.md)
- [商品編集画面](views/product_edit.md)
- [在庫管理画面](views/inventory.md)
- [在庫履歴画面](views/inventory_history.md)
- [レポート画面](views/report.md)

## テスト仕様書

- [E2Eテスト仕様書](tests/README.md)  
  End-to-Endテストの詳細仕様とテストケースを記載しています。

- [認証機能テスト](tests/auth_e2e_test.md)  
  ログイン・ログアウト機能のテスト仕様です。

- [商品管理テスト](tests/product_e2e_test.md)  
  商品の登録・編集・検索機能のテスト仕様です。

- [在庫管理テスト](tests/inventory_e2e_test.md)  
  在庫の追加・削減・履歴機能のテスト仕様です。

- [カテゴリ管理テスト](tests/category_e2e_test.md)  
  商品カテゴリのCRUD操作のテスト仕様です。

- [E2Eテスト実行結果](tests/results/README.md)  
  E2Eテスト実行の結果レポートです。

- [E2Eテスト改善ドキュメント](operation/e2e_test_improvements.md)  
  E2Eテストの改善と最適化についての詳細です。

- [E2Eテストデバッグガイド](operation/e2e_test_debug.md)  
  テスト実行時の問題解決と修正履歴です。

---

© 2025 製品管理システム - ドキュメント