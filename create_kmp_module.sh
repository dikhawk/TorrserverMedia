#!/bin/bash

# Проверяем, есть ли аргументы командной строки
if [ $# -lt 3 ]; then
    echo "Использование: $0 <директория> <название_модуля> <название_пакета> [<платформы: android ios jvm>] "
    exit 1
fi

# Считываем аргументы
parent_directory=$1
module_name=$2
package_name=$3
shift 3
selected_platforms=("$@")

# Поддерживаемые платформы
supported_platforms=("android" "ios" "jvm")

# Проверяем выбранные платформы
for platform in "${selected_platforms[@]}"; do
    if [[ ! " ${supported_platforms[@]} " =~ " ${platform} " ]]; then
        echo "Платформа '$platform' не поддерживается."
        exit 1
    fi
done

# Создаем директорию для модуля
module_directory="$parent_directory/$module_name"
mkdir -p "$module_directory"

# Создаем директории только для выбранных платформ
for platform in "${selected_platforms[@]}"; do
    case $platform in
    android)
        platform_dir="androidMain"
        ;;
    ios)
        platform_dir="iosMain"
        ;;
    jvm)
        platform_dir="jvmMain"
        ;;
    *)
        platform_dir="$platform"
        ;;
    esac
    mkdir -p "$module_directory/src/$platform_dir/kotlin"
    mkdir -p "$module_directory/src/$platform_dir/resources"
done

# Создаем commonMain по умолчанию
mkdir -p "$module_directory/src/commonMain/kotlin"
mkdir -p "$module_directory/src/commonMain/resources"

# Создаем структуру для пакета
mkdir -p "$module_directory/src/commonMain/kotlin/$(echo $package_name | tr '.' '/')"

# Создаем файлы для выбранных платформ
for platform in "${selected_platforms[@]}"; do
    case $platform in
    android)
        platform_dir="androidMain"
        ;;
    ios)
        platform_dir="iosMain"
        ;;
    jvm)
        platform_dir="jvmMain"
        ;;
    *)
        platform_dir="$platform"
        ;;
    esac
    mkdir -p "$module_directory/src/$platform_dir/kotlin/$(echo $package_name | tr '.' '/')"
done

# Создаем файл build.gradle.kts
cat >"$module_directory/build.gradle.kts" <<EOF
plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
EOF

# Добавляем блоки настроек для выбранных платформ
for platform in "${selected_platforms[@]}"; do
    case $platform in
    android)
        platform_dir="android()"
        ;;
    ios)
        platform_dir="iosX64(\"ios\")"
        ;;
    jvm)
        platform_dir="jvm()"
        ;;
    *)
        platform_dir="$platform()"
        ;;
    esac
    echo "    $platform_dir" >>"$module_directory/build.gradle.kts"
done

cat >>"$module_directory/build.gradle.kts" <<EOF

    sourceSets {
        commonMain.dependencies {

        }
    }
}
EOF

# Выводим сообщение об успешном создании модуля
echo "Модуль $module_name успешно создан в директории $module_directory с платформами: ${selected_platforms[*]}"
