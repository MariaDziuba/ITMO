#!/bin/bash

cleanAndCreateDirectories() {
rm -rf out
rm -rf info.kgeorgiy.ja.dziuba.jar
mkdir -p out
}

compile() {
  javac -d out --module-path ../../java-advanced-2021/artifacts/:../../java-advanced-2021/lib/ \
  --add-modules info.kgeorgiy.java.advanced.implementor \
  $(find . -type f \( -name 'Implementor.java' -o -name 'ImplementorCodeGenerator.java' \))
}

createJar() {
  jar -c --file=info.kgeorgiy.ja.dziuba.jar --manifest "MANIFEST.MF" -C out/ .
}

cleanAndCreateDirectories
compile
createJar