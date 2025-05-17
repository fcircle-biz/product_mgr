#!/bin/bash

# E2E単体テスト実行用スクリプト
# 使用例: ./run-single-e2e-test.sh InventoryPlaywrightTest#testInventoryList
# 使用例: ./run-single-e2e-test.sh InventoryPlaywrightTest (クラス内の全テスト実行)

# デフォルト値の設定
TEST_CLASS=${1:-""}
DB_HOST=${POSTGRES_HOST:-"localhost"}
DB_PORT=${POSTGRES_PORT:-"5434"}
DB_NAME=${POSTGRES_DB:-"productmgr_test"}
DB_USER=${POSTGRES_USER:-"testuser"}
DB_PASS=${POSTGRES_PASSWORD:-"testpass"}

if [ -z "$TEST_CLASS" ]; then
  echo "エラー: テストクラスを指定してください"
  echo "使用例: ./run-single-e2e-test.sh InventoryPlaywrightTest#testInventoryList"
  exit 1
fi

# フルパスを構築
FULL_TEST_PATH="com.example.productmgr.e2e.playwright.$TEST_CLASS"
echo "実行するテスト: $FULL_TEST_PATH"

# 環境変数を設定してMavenテストを実行
POSTGRES_HOST=$DB_HOST \
POSTGRES_PORT=$DB_PORT \
POSTGRES_DB=$DB_NAME \
POSTGRES_USER=$DB_USER \
POSTGRES_PASSWORD=$DB_PASS \
mvn -DskipClean=true test -Dtest=$FULL_TEST_PATH