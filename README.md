# commons-wrap 
[![deprecated](https://img.shields.io/badge/deprecated-youthful%20inexperience-red.svg)](https://img.shields.io/badge/deprecated-youthful%20inexperience-red.svg)
[![Build Status](https://travis-ci.org/furplag/commons-wrap.svg?branch=master)](https://travis-ci.org/furplag/commons-wrap)
[![codebeat badge](https://codebeat.co/badges/0acfc653-585a-42ed-af6a-d12f26b225d1)](https://codebeat.co/projects/github-com-furplag-commons-wrap)

Wrapped [Apache Commns](http://commons.apache.org/) Maven Repository.

## Getting Start
Add the following snippet to any project's pom that depends on your project
```xml
<repositories>
  ...
  <repository>
    <id>commons-wrap</id>
    <url>https://raw.github.com/furplag/commons-wrap/mvn-repo/</url>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
  </repository>
</repositories>
...
<dependencies>
  ...
  <dependency>
    <groupId>jp.furplag.sandbox.java.util</groupId>
    <artifactId>commons-wrap</artifactId>
    <version>[1.0,)</version>
  </dependency>
</dependencies>
```

## License
Code is under the [Apache Licence v2](LICENCE).
