# spring-data-jpa-jee-web-integration
[![No Maintenance Intended](http://unmaintained.tech/badge.svg)](http://unmaintained.tech/)
Module that adds some nice features when using Spring Data JPA with CDI.

Currently, the only feature is an additional ELResolver which delivers default EL names for Spring Data JPA repositories because the @Named annotation does not work when using Spring Data JPA with CDI.
