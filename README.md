# AUDIT
Application de conservation et de consultation des événements métier d'un système.

## BUILD DU PROJET
Si vous ne possédez pas l'exécutable du projet, il est possible de le construire de la manière suivante :
À la racine du projet, éxécuter : mvn clean package
Cette commande permettera de créer d'une part le fichier :
  `./audit-api/target/audit.jar` (qui est l'exécutable du projet).
et d'autre part le fichier
  `./audit-client/target/audit-client-[version].jar

## ÉXÉCUTER L'APPLICATION
Pour lancer le server de journalisation :
`java -jar audit.jar server {path/config.yml}`
le fichier config.yml est le fichier de configuration de l'application. Un exemple est fournit à `./audit-api/package/audit.yml.exemple`

Une fois le serveur en fonctionnement, une documentation embarquée est disponible à l'adresse : `/docs`

