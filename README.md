[![Build Status](https://travis-ci.org/vaadin/spring.svg?branch=master)](https://travis-ci.org/vaadin/spring)

Vaadin Spring
======================

Vaadin Spring is the official Spring integration for [Vaadin Framework](https://github.com/vaadin/framework).
This branch is Vaadin Framework 8.2+ compatible. See other branches for other framework versions:
* master for Vaadin 22 (no longer developed)
* 14.x for Vaadin 14 (default)
* 4.x for Vaadin 8 with Jakarta EE
* 3.x for Vaadin 8.2+
* 2.0 and 2.1 for Vaadin 8.0...8.1 versions
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
Execute `mvn clean install` in the root directory to build vaadin-spring and vaadin-spring-boot.

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
