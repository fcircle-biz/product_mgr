#\!/bin/bash
# E2Eテストファイルのタイムアウトをすべて60秒に更新するスクリプト

# 対象ファイル
files=(
  "src/test/java/com/example/productmgr/e2e/playwright/InventoryPlaywrightTest.java"
  "src/test/java/com/example/productmgr/e2e/playwright/AuthPlaywrightTest.java"
)

for file in "${files[@]}"; do
  echo "Updating timeouts in $file"
  # タイムアウト値を5000から60000に変更
  sed -i '' 's/\.setTimeout(5000)/\.setTimeout(60000)/g' "$file"
  # タイムアウト値を10000から60000に変更
  sed -i '' 's/\.setTimeout(10000)/\.setTimeout(60000)/g' "$file"
  # タイムアウト値を15000から60000に変更
  sed -i '' 's/\.setTimeout(15000)/\.setTimeout(60000)/g' "$file"
  # タイムアウト値を30000から60000に変更
  sed -i '' 's/\.setTimeout(30000)/\.setTimeout(60000)/g' "$file"
done

echo "All timeouts updated to 60000ms (60 seconds)"
