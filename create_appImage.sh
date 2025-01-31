#!/bin/sh

HERE="$(dirname "$(readlink -f "${0}")")"
PATH_BUILD="${HERE}/build/"
DIR_APP='torrservermedia.AppDir/'
DIR_USR="usr/"
APP_IMAGE_FILE_NAME="TorrServerMedia_x86_64.AppImage"

cd "$HERE" || { echo "Wrong script directory"; exit 1; }
./gradlew generateBuildKonfig
./gradlew packageAppImage

echo "$PATH_BUILD"

if [ -d "${PATH_BUILD}${DIR_APP}" ]; then
  rm -rf "${PATH_BUILD}${DIR_APP}"
fi

mkdir -p "${PATH_BUILD}${DIR_APP}${DIR_USR}"
cp -R "${HERE}/composeApp/build/compose/binaries/main/app/com.dik.torrservermedia.desktopApp/." "${PATH_BUILD}${DIR_APP}${DIR_USR}"
cp "${HERE}/ic_app.svg" "${PATH_BUILD}${DIR_APP}torrservermedia-256x256.svg"

cat <<EOF > "${PATH_BUILD}${DIR_APP}AppRun"
#!/bin/sh

HERE="\$(dirname \"\$(readlink -f \"\${0}\")\")"
EXEC="\${HERE}/usr/bin/com.dik.torrservermedia.desktopApp"

exec "\$EXEC"
EOF

sed -i 's/\\//g' "${PATH_BUILD}${DIR_APP}AppRun"
chmod +x "${PATH_BUILD}${DIR_APP}AppRun"


cat <<EOF > "${PATH_BUILD}${DIR_APP}TorrServerMedia.desktop"
[Desktop Entry]
Categories=Utility
Comment[ru_RU]=Проигрывание торрент файлов
Comment=Play Torrent
Exec=com.dik.torrservermedia.desktopApp
GenericName[ru_RU]=TorrServerMedia
GenericName=TorrServerMedia
Icon=torrservermedia-256x256
MimeType=
Name[ru_RU]=TorrServerMedia
Name=TorrServerMedia
Path=
StartupNotify=true
Terminal=false
TerminalOptions=
Type=Application
X-KDE-SubstituteUID=false
X-KDE-Username=
EOF

BUILD_TOOL_FILE="appimagetool-x86_64.AppImage"

if [ ! -f "${PATH_BUILD}${BUILD_TOOL_FILE}" ]; then
  wget -P "$PATH_BUILD" https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage
  chmod +x "${PATH_BUILD}${BUILD_TOOL_FILE}"
fi

cd "$PATH_BUILD" || exit 1

if [ -f "${PATH_BUILD}${APP_IMAGE_FILE_NAME}" ]; then
  rm "$APP_IMAGE_FILE_NAME"
fi

"${PATH_BUILD}${BUILD_TOOL_FILE}" "${PATH_BUILD}${DIR_APP}" "$APP_IMAGE_FILE_NAME"

if [ -f "${PATH_BUILD}${APP_IMAGE_FILE_NAME}" ]; then
  chmod +x "$APP_IMAGE_FILE_NAME"
else
  echo "$APP_IMAGE_FILE_NAME not created"
  exit 1
fi

exit 0
