# pt-api

## Plugins

This project contains the following plugins:

* **HMCTS Java plugin**

  Applies code analysis tools with HMCTS default settings. See the [project repository](https://github.com/hmcts/gradle-java-plugin) for details.

  Analysis tools include:

    * **checkstyle**

      https://docs.gradle.org/current/userguide/checkstyle_plugin.html

      Performs code style checks on Java source files using Checkstyle and generates reports from these checks.
      The checks are included in gradle's *check* task (you can run them by executing `./gradlew check` command).

    * **org.owasp.dependencycheck**

      https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html

      Provides monitoring of the project's dependent libraries and creating a report
      of known vulnerable components that are included in the build. To run it
      execute `gradle dependencyCheck` command.

* **jacoco**

  https://docs.gradle.org/current/userguide/jacoco_plugin.html

  Provides code coverage metrics for Java code via integration with JaCoCo.
  You can create the report by running the following command:

  ```bash
    ./gradlew jacocoTestReport
  ```

  The report will be created in build/reports subdirectory in your project directory.

* **io.spring.dependency-management**

  https://github.com/spring-gradle-plugins/dependency-management-plugin

  Provides Maven-like dependency management. Allows you to declare dependency management
  using `dependency 'groupId:artifactId:version'`
  or `dependency group:'group', name:'name', version:version'`.

* **org.springframework.boot**

  http://projects.spring.io/spring-boot/

  Reduces the amount of work needed to create a Spring application


* **com.github.ben-manes.versions**

  https://github.com/ben-manes/gradle-versions-plugin

  Provides a task to determine which dependencies have updates. Usage:

  ```bash
    ./gradlew dependencyUpdates -Drevision=release
  ```

---

## Building and deploying the application

### Building the application

The project uses [Gradle](https://gradle.org) as a build tool. It already contains
`./gradlew` wrapper script, so there's no need to install gradle.

To build the project execute the following command:

```bash
  ./gradlew build
```

### Running the application with local CCD

To start PT API, CCD and all dependencies, run the following command:

```bash
  ./gradlew buildWithCCD
```

This will start several containers, including CCD and PostgreSQL. Here is a list of containers and the ports they are accessible from:

| Container | Port |
| --- | --- |
| PT API | 4550 |
| XUI (UI Access point) | 3000 |
| Postgres Database | 6432 |
| Elastic search | 9200 |
| IDAM Simulator | 5062 |
| XUI Manage org | 3001 |

Once successfully loaded open XUI at http://localhost:3000. See CftlibConfig.java for users and login details.

### Decentralised CCD
The application uses decentralised CCD. This means the service stores its own case data and CCD calls back to pt-api for case persistence instead of writing to CCD directly.

### Generating the CCD definition XLS
We can generate the xlsx spreadsheet by running the script:

```bash
  ./bin/create-xlsx.sh
```

This spreadsheet can be found in: `/build/definitions/CCD_Definition_PT_ENVIRONMENT.xlsx`

You can specify which environment to generate the spreadsheet for by passing it in when calling the script. E.g `./bin/create-xlsx.sh preview`.

### Running the tests

The Functional tests use [Rest Assured](https://rest-assured.io) and [Serenity](https://serenity-bdd.github.io) for reporting, and are located in the `/src/functionalTest` directory.

The following environment variables are needed to run the tests:

- PT_API_S2S_SECRET
- TEST_URL
- IDAM_S2S_AUTH_URL
- IDAM_API_URL
- IDAM_SYSTEM_USERNAME
- IDAM_SYSTEM_USER_PASSWORD
- PT_API_IDAM_SECRET
- IDAM_PT_USER_PASSWORD
- PT_CASEWORKER_AUTOMATION_UID
- IDAM_EXPIRED_USER_TOKEN
- S2S_EXPIRED_TOKEN
- IDAM_TESTING_SUPPORT_URL
- CCD_ENABLED

To run the tests, use:
```bash
./gradlew functional
````

To run tests based on tags, use the following command (replace `tagName` with the desired tag):
```bash
./gradlew functional -Dtags="tagName"
````

Additionally, you can configure the tags to run in the pipeline by editing the functional task in the `build.gradle`, line:

`includeTags System.getProperty("tags", "Functional")`

After the tests run, the report will be available under the /[report-for-functional-tests](report-for-functional-tests) folder, in a file named `index.html`.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

