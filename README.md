[![Build Status](https://travis-ci.org/vaadin/spring.svg?branch=master)](https://travis-ci.org/vaadin/spring)

Vaadin Spring
======================

Vaadin Spring is the official Spring integration for [Vaadin Framework](https://github.com/vaadin/framework).
This branch is Vaadin Framework 8.2+ compatible. See other branches for other framework versions:
* master for Vaadin 10
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


Experimental release
----
Vaadin Spring 4.0 is an experimental release.


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
