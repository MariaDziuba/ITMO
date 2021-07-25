#!/bin/bash

cd ../
REPOSITORY_PATH=$PWD
cd ../
ROOT=$PWD
SRC_PATH=${REPOSITORY_PATH}/
KGEORGIY_PATH=${ROOT}/java-advanced-2021/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor
SOLUTIONS_PATH=${REPOSITORY_PATH}/java-solutions
MY_CLASSES_PATH=info/kgeorgiy/ja/dziuba/implementor

createJavadoc() {
  javadoc \
  -link https://docs.oracle.com/en/java/javase/11/docs/api/ \
  -private -author -version \
  -d "${SRC_PATH}"/_javadoc \
  "${SOLUTIONS_PATH}"/"${MY_CLASSES_PATH}"/*.java \
  "${KGEORGIY_PATH}"/Impler.java \
  "${KGEORGIY_PATH}"/ImplerException.java \
  "${KGEORGIY_PATH}"/JarImpler.java
}

createJavadoc
