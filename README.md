This initial version of `README.md` is to help with setting up the project in **EPO CBDP**. Please, use the instructions below to:
* upload source code to this repository,
* to update pom.xml to match with its SCM location and
* to update build jobs of the project

When done, replace content of this file with instructions for building, testing your project, its installation and configuration.

# Configure Git for the first time

You can find help for preparing your workstation to work with Git on [Prepare Workstation for working with GIT ](http://confluence-p.internal.epo.org/x/14e3BQ)
page in Confluence.

# Working with new repository

If you want to simply **clone this empty repository** then run this command in your terminal.

````
git clone ssh://git@bitbucket-p.internal.epo.org:7999/padd/kafka-fast-consumer-poc.git
````

If you already have **code ready to be pushed** to this repository then run this in your terminal.

````
cd existing-project
git init
git remote add origin ssh://git@bitbucket-p.internal.epo.org:7999/padd/kafka-fast-consumer-poc.git
git pull origin master
git add --all
git commit -m "Initial Commit"
git push -u origin master
````

If you have either README.md or .gitignore files, `git pull` command above will fail. Skip it and add `--force` option to
the last `git push` command. However you need to ensure presence of `.gitignore` with proper content in your sources

If your **code is already tracked by Git** then 
* set this repository as your "origin" to push to
* use `--force` for the first push in order to overwrite commit, which added this README file

````
cd existing-project
git remote set-url origin ssh://git@bitbucket-p.internal.epo.org:7999/padd/kafka-fast-consumer-poc.git
git push -u --force origin master
````

# Preparing POM

Update SCM section in POM by copy/pasting the fragment below.

````
<scm>
    <connection>scm:git:ssh://git@bitbucket-p.internal.epo.org:7999/padd/kafka-fast-consumer-poc.git</connection>
    <developerConnection>scm:git:ssh://git@bitbucket-p.internal.epo.org:7999/padd/kafka-fast-consumer-poc.git</developerConnection>
    <url>https://bitbucket-p.internal.epo.org/projects/PADD/repos/kafka-fast-consumer-poc</url>
    <tag>master</tag>
</scm>
````

Update definition of EPO CI plugin for Maven 

* Ensure the project is inherited from Corporate POM `4.3.1` or higher
* Pay attention to [selection of Git workflow](http://confluence-p.internal.epo.org/x/8463BQ) for this repository.
Does your project use Gitflow or Trunk Based Development? This choice influences the way the project build jobs are created

Check description in [Confluence](http://confluence-p.internal.epo.org/x/cpDwAw)
for details. The XML fragment below is just an example. Do **NOT** copy it into your project

````
<plugin>
    <groupId>org.epo.common.maven.plugins</groupId>
    <artifactId>epo-ci-maven-plugin</artifactId>
    <configuration>
        <jobLayoutDescriptor>basic</jobLayoutDescriptor>
        <overwriteExistingJobs>true</overwriteExistingJobs>
        <useGitflow>false</useGitflow>
        <jobLayoutDescriptorParameters>
            <sonar>true</sonar>
        </jobLayoutDescriptorParameters>
    </configuration>
</plugin>
````
  
# Recreate build jobs

The build jobs can be updated without loosing history of the builds
````
mvn epo-ci:create-ci-jobs -Depo-ci.username=<XX12345> -Depo-ci.password=<Jenkins API token>
````

If you need to delete old build jobs

````
mvn epo-ci:remove-ci-jobs -Depo-ci.removeAllByScm=true -Depo-ci.username=<XX12345> -Depo-ci.password=<Jenkins API token>
````
