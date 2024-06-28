### Pre-requisites:

- Générer une clé ssh depuis le dossier dans lequel se trouve votre docker-compose. (créer le dossier .ssh vide si il n'existe pas).
  ``ssh-keygen -f .ssh/id_rsa``
- Remplacer la valeur de 'JENKINS_AGENT_SSH_PUBKEY' dans le fichier docker-compose.yml par la clé ssh générée dans le fichier 'id_rsa.pub' (tout le fichier meme le '= arthur@PC_Portable_Art'. Attention, ne pas mettre de guillemets)

### Start command:

* executer sans débugage: ``docker-compose up -d``
* executer :


### Stop command:

``docker-compose down``

### Find first password:

``docker exec jenkins-master cat /var/jenkins_home/secrets/initialAdminPassword``
