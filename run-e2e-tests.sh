#!/bin/bash

# E2E テスト実行スクリプト - pgAudit監視と統合したPlaywright E2Eテスト

echo "Starting E2E tests with Playwright..."

# ターゲットディレクトリの問題に対処するための緊急対応（開発環境のみ）
echo "WARNING: パーミッション問題を一時的に回避します - 開発環境専用"
# targetのバックアップはスキップ
if [ -d "$(pwd)/target" ]; then
  echo "targetディレクトリを再利用します..."
fi

# 新しいtargetディレクトリを作成
echo "新しいtargetディレクトリを作成します..."
mkdir -p "$(pwd)/target"
chmod -R 777 "$(pwd)/target" 2>/dev/null || true

# 既存のPostgreSQLコンテナがあれば停止して削除
if docker ps -a | grep -q postgres-e2e-test; then
    echo "既存のテスト用PostgreSQLコンテナを停止・削除します..."
    docker stop postgres-e2e-test
    docker rm postgres-e2e-test
fi

# PostgreSQLコンテナの起動
echo "テスト用のPostgreSQLコンテナを起動します..."
docker run -d --name postgres-e2e-test \
    -e POSTGRES_DB=productmgr_test \
    -e POSTGRES_USER=testuser \
    -e POSTGRES_PASSWORD=testpass \
    -v "$(pwd)/src/test/resources/e2e-schema.sql:/docker-entrypoint-initdb.d/01-schema.sql" \
    -p 5434:5432 \
    postgres:16
echo "PostgreSQLの起動を待機しています..."
sleep 10

# テストの実行
echo "Playwright E2Eテストを実行します..."
# 環境変数を設定してテストを実行
POSTGRES_HOST=localhost \
POSTGRES_PORT=5434 \
POSTGRES_DB=productmgr_test \
POSTGRES_USER=testuser \
POSTGRES_PASSWORD=testpass \
mvn -DskipClean=true test -Dtest=com.example.productmgr.e2e.playwright.*PlaywrightTest

# テスト終了後のクリーンアップ
echo "クリーンアップを実行します..."
docker stop postgres-e2e-test
docker rm postgres-e2e-test

echo "E2Eテスト結果は target/surefire-reports/ にあります"