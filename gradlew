#!/usr/bin/env sh
exec gradle wrapper logic - using gradle wrapper jar
# This is a stub - GitHub Actions will have gradle installed via wrapper task, 
# but we need real wrapper. We will generate wrapper via gradle/wrapper files.
# For now use system gradle if wrapper jar missing
if [ -f gradle/wrapper/gradle-wrapper.jar ]; then
  exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
else
  echo "Wrapper jar missing, installing..."
  # fallback - action will provide gradle
  gradle "$@"
fi
