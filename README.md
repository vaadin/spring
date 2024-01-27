[![Build Status](https://travis-ci.org/vaadin/spring.svg?branch=master)](https://travis-ci.org/vaadin/spring)

Vaadin Spring
======================

Vaadin Spring is the official Spring integration for [Vaadin Framework](https://github.com/vaadin/framework).
This branch is Vaadin Framework 8.2+ compatible. See other branches for other framework versions:
* master for Vaadin 10+
* 4.x for Vaadin 8 with Jakarta EE
* 3.X for Vaadin 8.2+
* 2.0 and 2.1 for Vaadin Framework 8.0...8.1 versions 
* 1.2 for Vaadin Framework V7 versions 


Download release
----

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to https://vaadin.com/addon/vaadin-spring.


Building the project
----
Execute `mvn clean install` in the root directory to build vaadin-spring and vaadin-spring-boot.


Vaadin Spring 4.0
----
This version of Vaadin Spring is made to be compatible with Wildfly 27 and Jakarta EE 10,
featuring Spring 6.1, Spring Security 6.1 and Spring Boot 3.2. Due to technical limitations,
the Push implementation of Vaadin 8 is not (yet) compatible with this stack. A future
version may remedy this.


Experimental release
----
Vaadin Spring 4.0 is considered experimental until further notice.


Vaadin community addons and extensions in Vaadin Spring 4.x
----
Vaadin Spring 4.0 contains Vaadin 8 compatible versions of code in the Vaadin4Spring and
Spring Data Provider community addons, and retains their `org.vaadin.*` namespaces.
These are available through a new vaadin-spring-addons package, which is version-
synced with the main Spring add-on. This has been done for compatibility reasons,
as minor changes (such as moving to the Jakarta namespace) have to have been made.

At the time of the Vaadin Spring 4.0 release, these add-ons had been largely
unmaintained since 2018.
See the Vaadin 8 branches of [Vaadin4Spring](https://github.com/peholmst/vaadin4spring/tree/v8)
and [spring-data-provider](https://github.com/Artur-/spring-data-provider/tree/vaadin8) for
more information.

The `org.vaadin.*` code is available in the form of `vaadin-spring-extensions-` and
`vaadin-spring-addons-` packages. Use of these packages is discouraged for new code,
as they are provided for compatibility reasons only when moving to newer versions of
Java, Spring, Spring Boot and Spring Security.


Making your application compatible with Vaadin Spring 4.0
----

* Replace all instances of `javax.` with `jakarta.` and import the relevant
  Jakarta APIs, for example
  ```xml
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.0.2</version>
    </dependency>	

    <dependency>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
        <version>3.1.0</version>
    </dependency>
  ```
  to replace javax.validation-api and JPA.
* Replace all references to `vaadin-server` dependencies with
  `vaadin-server-mpr-jakarta`, the Jakarta compatibility version.
  This dependency is imported transitively from any `vaadin-spring` artifact,
  however you might need to exclude it from other dependencies, such as
  ```xml
    <dependency>
        <groupId>com.vaadin</groupId>
        <artifactId>vaadin-charts</artifactId>
        <version>4.0.4</version>
        <exclusions>
            <exclusion>
                <groupId>com.vaadin</groupId>
                <artifactId>vaadin-server</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
  ```
* If you're inhereiting from spring-boot-starter-parent, remember to update the
  version to a relevant one (at the time of writing this is 3.2.2).
  ```xml
  	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.2</version>
	</parent>
  ```
* Rewrite your Spring Security configuration the Spring 6 way. You no longer
  extend a security configuration adapter type, rather you annotate a class with
  `@Configuration` and expose configuration beans. You can read more about it
  in [this article](https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter).


Contributions
----
Contributions to the project can be done using pull requests.
You will be asked to sign a contribution agreement after creating the first one.


Copyright 2015-2024 Vaadin Ltd.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
