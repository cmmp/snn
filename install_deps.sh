#!/bin/bash

echo "Instalando dependencias no repositorio local em lib/"

BASEDIR='C:\Users\Cássio\tools\javaml-0.1.7\'

mvn install:install-file -Dfile="${BASEDIR}\javaml-0.1.7.jar" -DgroupId=javaml -DartifactId=javaml -Dversion=0.1.7 -Dpackaging=jar -DlocalRepositoryPath=lib/

mvn install:install-file -Dfile="${BASEDIR}\lib\ajt-2.9.jar" -DgroupId=ajt -DartifactId=ajt -Dversion=2.9 -Dpackaging=jar -DlocalRepositoryPath=lib/

mvn install:install-file -Dfile="${BASEDIR}\lib\commons-math-1.2.jar" -DgroupId=commons -DartifactId=math -Dversion=1.2 -Dpackaging=jar -DlocalRepositoryPath=lib/

mvn install:install-file -Dfile="${BASEDIR}\lib\Jama-1.0.2.jar" -DgroupId=jama -DartifactId=jama -Dversion=1.0.2 -Dpackaging=jar -DlocalRepositoryPath=lib/

mvn install:install-file -Dfile="${BASEDIR}\lib\libsvm.jar" -DgroupId=libsvm -DartifactId=libsvm -Dversion=0.0 -Dpackaging=jar -DlocalRepositoryPath=lib/

mvn install:install-file -Dfile="${BASEDIR}\lib\weka.jar" -DgroupId=weka -DartifactId=weka -Dversion=0.0 -Dpackaging=jar -DlocalRepositoryPath=lib/

