# familyDoc
familyDoc

###How to start in Heroku:

```bash
mvn clean package

heroku create --no-remote f-doctor

heroku ps:scale worker=1 --app f-doctor

heroku deploy:jar target/family-doctor-1.0-SNAPSHOT-jar-with-dependencies.jar --app f-doctor
```
