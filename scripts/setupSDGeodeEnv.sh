#!/usr/bin/env bash

#if anything errors, bail.
set -e

#Variables
sdgPath="spring-data-geode"
branch=""
version=""
pomFile="pom.xml"

Clone(){
  # Clone SDG repo
  rm -rf "$sdgPath"
  git clone https://github.com/spring-projects/spring-data-geode.git $sdgPath
  cd "$sdgPath" || exit
  CURRENT_DIR=`pwd`
  pomFile=$CURRENT_DIR"/spring-data-geode/pom.xml"
}

Checkout(){
  # Checkout correct branch
  git checkout $branch
}

ModifyPom(){
  newPomFile=$CURRENT_DIR"/spring-data-geode/pom2.xml"
  rm -rf "$newPomFile"
  touch "$newPomFile"
  while IFS= read -r line; do
      if [[ $line == *\<plugin\>* ]];
      then
        echo "<plugin>
                             <groupId>org.apache.maven.plugins</groupId>
                             <artifactId>maven-jar-plugin</artifactId>
                             <version>3.2.2</version>

                             <executions>
                                 <execution>
                                     <goals>
                                         <goal>test-jar</goal>
                                     </goals>
                                 </execution>
                             </executions>
                         </plugin>" >> "$newPomFile"
      fi
      echo "$line" >> "$newPomFile"
  done < "$pomFile"

  rm "$pomFile"
  mv "$newPomFile" "$pomFile"
}

Build(){
  ./mvnw clean install test -Dtest="GemFireDataSourceUsingNonSpringConfiguredGemFireServerIntegrationTests"
  version=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
}

# Get the options
Help() {
  # Display Help
  echo "Add description of the script functions here."
  echo
  echo "Syntax: setupSDGEnv [-h|l]"
  echo "options:"
  echo "-h     Print this Help."
  echo "-l     Provide directory path where the SDG code to be cloned into"
  echo "-b     Provide the branch that is to extracted"
  echo
}

while getopts ":hlb:" option; do
  case $option in
  h) # display Help
    Help
    exit
    ;;
  l) # Enter a directory path
    sdgPath=$OPTARG ;;
  b) # Enter branch name 2.5.x
    branch=$OPTARG ;;
  \?) # Invalid option
    echo "Error: Invalid option"
    exit
    ;;
  esac
done

Clone
Checkout
#ModifyPom
Build
cd "$CURRENT_DIR"
echo "$version"
