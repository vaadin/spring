Vaadin Spring
======================

Vaadin Spring is the official Spring integration for [Vaadin Framework](https://github.com/vaadin/framework).
This branch is Vaadin Framework 7.7.44+ compatible. See other branches for other framework versions:
* master for Vaadin 22 (no longer developed)
* 14.x for Vaadin 14 (default)
* 4.x for Vaadin 8.21+ with Spring 6, Spring Boot 3, Java 17+ and Jakarta EE10+
* 3.x for Vaadin 8.2+
* 2.0 and 2.1 for Vaadin 8.0...8.1 versions
* 1.4 for Vaadin 7.7.47+ with Spring 6, Spring Boot 3, Java 17+ and Jakarta EE10+
* 1.3 for Vaadin 7.7.44+ with Java 8
* 1.2 for Vaadin 7 versions

Since Vaadin 23, the code for the Spring Boot add-on has been migrated to the 
[Flow](https://github.com/vaadin/flow) repository.

Download release
----

Official releases of this add-on are available at
[Vaadin Directory](https://vaadin.com/directory/component/vaadin-spring).

Building the project
----
Execute `mvn clean install` in the root directory to build all modules.

Vaadin Spring 1.4
----
This version of Vaadin Spring is for Vaadin 7.7.47+ and supports Java 17+,
Spring Framework 6.x, Spring Boot 3.x, and Jakarta EE10.

Due to technical limitations, Vaadin Spring 1.4 does not support Java 8 and
contains some API changes compared to Vaadin Spring 1.3 (for Vaadin 7).

Note: Vaadin 7.7.47 requires Vaadin 7 Extended Maintenance subscription.
This version of Vaadin Spring is not compatible with free Vaadin 7 versions.

The initial release of Vaadin Spring 1.4 was built with the following versions:
* Vaadin 7.7.47
* Spring 6.2.7
* Spring Security 6.4.6
* Spring Boot 3.4.6
* Jakarta API 3.0.0
* Jakarta Servlet 6.0.0

Contributions
----
Contributions to the project can be done using pull requests.
You will be asked to sign a contribution agreement after creating the first one.

Maintenance instructions
----
Update license headers yearly. Update the ending year below, in `checkstyle/header`
and in `checkstyle/headerBase`, and then run `license:format` (or a local
script) on the root project to automatically update the license headers on all
the Java files.

If Checkstyle fails to recognise the new year range, make a temporary change to
`checkstyle/vaadin-checkstyle.xml` (e.g. one extra whitespace) and then clean
and rebuild the project. Remember to revert the temporary change afterwards.

License
----

Copyright 2015-2025 Vaadin Ltd.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
