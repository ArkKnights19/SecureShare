@echo off
"E:\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HE:\\DOCUMENTSZ\\Securesharev1\\openCV\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=30" ^
  "-DANDROID_PLATFORM=android-30" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=E:\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=E:\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=E:\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=E:\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=E:\\DOCUMENTSZ\\Securesharev1\\openCV\\build\\intermediates\\cxx\\RelWithDebInfo\\3e833w42\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=E:\\DOCUMENTSZ\\Securesharev1\\openCV\\build\\intermediates\\cxx\\RelWithDebInfo\\3e833w42\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BE:\\DOCUMENTSZ\\Securesharev1\\openCV\\.cxx\\RelWithDebInfo\\3e833w42\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
