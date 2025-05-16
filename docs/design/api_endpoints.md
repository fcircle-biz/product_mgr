# API エンドポイント設計書

このドキュメントは、在庫管理システムの各コントローラーとそのエンドポイントの一覧を提供します。システムのルーティング設計と利用可能なエンドポイントを理解するためのリファレンスとして使用できます。

## 目次

1. [カテゴリー管理](#カテゴリー管理)
2. [ホーム](#ホーム)
3. [在庫管理](#在庫管理)
4. [ログイン](#ログイン)
5. [メニュー](#メニュー)
6. [商品管理](#商品管理)
7. [レポート](#レポート)
8. [設定](#設定)

## カテゴリー管理

**ベースパス**: `/categories`

| HTTPメソッド | エンドポイント         | 機能                      | メソッド名        |
|-----------|---------------------|-------------------------|----------------|
| GET       | `/categories`       | カテゴリー一覧を表示           | listCategories  |
| GET       | `/categories/new`   | 新規カテゴリー作成フォームを表示   | newCategoryForm |
| POST      | `/categories`       | 新規カテゴリーを作成           | createCategory  |
| GET       | `/categories/{id}/edit` | カテゴリー編集フォームを表示    | editCategoryForm |
| POST      | `/categories/{id}`  | カテゴリーを更新              | updateCategory  |
| POST      | `/categories/{id}/delete` | カテゴリーを削除          | deleteCategory  |

## ホーム

**ベースパス**: なし

| HTTPメソッド | エンドポイント | 機能         | メソッド名 |
|-----------|------------|------------|---------|
| GET       | `/`        | ホーム画面表示  | home    |

## 在庫管理

**ベースパス**: `/inventory`

| HTTPメソッド | エンドポイント                  | 機能                  | メソッド名             |
|-----------|----------------------------|---------------------|---------------------|
| GET       | `/inventory`               | 在庫一覧表示            | listInventory       |
| GET       | `/inventory/history/{productId}` | 商品の在庫履歴を表示      | viewInventoryHistory |
| GET       | `/inventory/add`           | 在庫追加フォーム表示       | addInventoryForm    |
| GET       | `/inventory/subtract`      | 在庫減少フォーム表示       | subtractInventoryForm |
| POST      | `/inventory/process`       | 在庫処理実行            | processInventory    |
| POST      | `/inventory/quick-process` | クイック在庫処理(REST API) | quickProcess        |

## ログイン

**ベースパス**: なし

| HTTPメソッド | エンドポイント | 機能         | メソッド名 |
|-----------|------------|------------|---------|
| GET       | `/login`   | ログイン画面表示 | login   |

## メニュー

**ベースパス**: なし

| HTTPメソッド | エンドポイント | 機能           | メソッド名 |
|-----------|------------|--------------|---------|
| GET       | `/menu`    | メインメニュー表示  | menu    |

## 商品管理

**ベースパス**: `/products`

| HTTPメソッド | エンドポイント           | 機能                | メソッド名        |
|-----------|---------------------|-------------------|----------------|
| GET       | `/products`         | 商品一覧表示           | listProducts    |
| GET       | `/products/{id}`    | 商品詳細表示           | viewProduct     |
| GET       | `/products/new`     | 新規商品作成フォーム表示    | newProductForm  |
| POST      | `/products`         | 新規商品作成           | createProduct   |
| GET       | `/products/{id}/edit` | 商品編集フォーム表示      | editProductForm |
| POST      | `/products/{id}`    | 商品更新              | updateProduct   |
| POST      | `/products/{id}/delete` | 商品削除           | deleteProduct   |

## レポート

**ベースパス**: `/reports`

| HTTPメソッド | エンドポイント                       | 機能                  | メソッド名                    |
|-----------|----------------------------------|---------------------|----------------------------|
| GET       | `/reports`                       | レポートダッシュボード表示     | reportDashboard             |
| GET       | `/reports/stock-warning`         | 在庫警告レポート表示        | stockWarningReport          |
| GET       | `/reports/inventory-summary`     | 在庫サマリーレポート表示      | inventorySummaryReport      |
| GET       | `/reports/category-distribution` | カテゴリー分布レポート表示     | categoryDistributionReport  |
| GET       | `/reports/daily`                 | 日次レポート表示          | dailyReport                 |
| GET       | `/reports/monthly`               | 月次レポート表示          | monthlyReport               |
| GET       | `/reports/product/{id}`          | 商品別レポート表示         | productReport               |
| GET       | `/reports/export/daily`          | 日次レポートのエクスポート     | exportDailyReport           |
| GET       | `/reports/export/monthly`        | 月次レポートのエクスポート     | exportMonthlyReport         |
| GET       | `/reports/export/stock-warning`  | 在庫警告レポートのエクスポート   | exportStockWarningReport    |
| GET       | `/reports/export/category-distribution` | カテゴリー分布レポートのエクスポート | exportCategoryDistribution  |
| GET       | `/reports/inventory-turnover`    | 在庫回転率レポート表示       | inventoryTurnoverReport     |
| GET       | `/reports/export/inventory-turnover` | 在庫回転率レポートのエクスポート | exportInventoryTurnoverReport |

## 設定

**ベースパス**: `/settings`

| HTTPメソッド | エンドポイント               | 機能              | メソッド名                |
|-----------|-----------------------|-----------------|----------------------|
| GET       | `/settings`           | 設定画面表示          | showSettings           |
| POST      | `/settings/basic`     | 基本設定の保存         | saveBasicSettings      |
| POST      | `/settings/notification` | 通知設定の保存      | saveNotificationSettings |
| POST      | `/settings/backup`    | バックアップ設定の保存    | saveBackupSettings     |

---

*このドキュメントは自動生成されました。更新日: 2025/5/17*