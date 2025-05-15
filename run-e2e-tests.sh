#!/bin/bash

# E2E テスト実行スクリプト

echo "Starting E2E tests..."

# Chrome driver のインストール確認
if ! command -v chromedriver &> /dev/null; then
    echo "Chrome driver is not installed. Installing..."
    wget https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip
    unzip chromedriver_linux64.zip
    sudo mv chromedriver /usr/local/bin/
    sudo chmod +x /usr/local/bin/chromedriver
    rm chromedriver_linux64.zip
fi

# PostgreSQL コンテナが起動しているか確認
if ! docker ps | grep -q postgres; then
    echo "Starting PostgreSQL container..."
    docker run -d --name postgres-test \
        -e POSTGRES_DB=productmgr_test \
        -e POSTGRES_USER=testuser \
        -e POSTGRES_PASSWORD=testpass \
        -p 5433:5432 \
        postgres:16
    sleep 10
fi

# E2E テストの実行
mvn clean verify -Pe2e -DskipTests

# テスト結果の表示
echo "E2E test results are available in target/failsafe-reports/"