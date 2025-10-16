#!/bin/bash
set -e

# Цвета
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Запуск релиза...${NC}"

# Проверка: нет ли незакоммиченных изменений
if ! git diff-index --quiet HEAD --; then
    echo -e "${RED}Ошибка: есть незакоммиченные изменения. Закоммитьте или отмените их.${NC}"
    exit 1
fi

# Ввод версии
read -p "Введите версию релиза (например, 1.1.0): " VERSION

# Обновление версии в gradle.properties
sed -i.bak "s/version=.*/version=$VERSION/" gradle.properties
rm gradle.properties.bak

# Коммит и тег
git add gradle.properties
git commit -m "chore: release v$VERSION"
git tag -a "v$VERSION" -m "Release version $VERSION"

# Сборка
echo -e "${GREEN}Сборка проекта...${NC}"
./gradlew clean bootJar

# Проверка наличия JAR-файлов
if [ ! -f "gateway-service/build/libs/gateway-service-$VERSION.jar" ]; then
    echo -e "${RED}Ошибка: JAR-файлы не созданы.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Релиз v$VERSION подготовлен!${NC}"
echo -e "${GREEN}Выполните: git push && git push origin v$VERSION${NC}"