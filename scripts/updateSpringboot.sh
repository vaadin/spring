#!/bin/bash
set -o pipefail

getCurrentVersion() {
  pom=`curl -s "https://raw.githubusercontent.com/vaadin/spring/master/pom.xml"`
  springbootVersion=`echo "$pom" | grep '<spring-boot.version>' | cut -d '>' -f2 |cut -d '<' -f1` 
  echo $springbootVersion
}

getBaseVersion() {
  echo $1 | tr - . | cut -d . -f1,2;
}

getLatestVersion() {
  base=$1
  springbootReleases=`curl -s "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/maven-metadata.xml"`
  
  version=`echo "$springbootReleases" | grep '<version>' | cut -d '>' -f2 | cut -d '<' -f1 | grep "^$base" | tail -1`
  echo $version
}

currentVersion=`getCurrentVersion`
baseVersion=`getBaseVersion $currentVersion`
latestVersion=`getLatestVersion $baseVersion`

echo project is using Spring-boot version $currentVersion
echo the latest Spring-boot version under this minor is $latestVersion

if [ $currentVersion != $latestVersion ]
then
  echo "Updating the project to use the latest"
  updateBranch=update-vaadin-$latestVersion-$(date +%s)
  git checkout -b $updateBranch
  mvn org.codehaus.mojo:versions-maven-plugin:2.7:set-property -Dproperty=spring-boot.version -DnewVersion=$latestVersion -DgenerateBackupPoms=false
  git add pom.xml
  git commit -m "chore: update spring-boot to $latestVersion"
  git push -u origin HEAD 
  hub pull-request -b vaadin:$baseBranch -h vaadin:$updateBranch -m "Update Vaadin $latestVersion"
else
  echo "project is using the latest Spring-Boot version, no need to update"
  echo "##teamcity[buildStatus status='SUCCESS' text='Spring boot $currentVersion is the latest. If necessary, you can check the major/minor update manually']"
fi
