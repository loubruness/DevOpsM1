### Pre-requisites:
  - Générer une clé ssh
  ```ssh-keygen -f ~/.ssh/id_rsa```
  - Remplacer la valeur de 'JENKINS_AGENT_SSH_PUBKEY' dans le fichier docker-compose.yml par la clé ssh générée

### Start command:
```docker-compose up --build```

### Stop command:
```docker-compose down```

### Find first password:
```docker exec jenkins-master cat /var/jenkins_home/secrets/initialAdminPassword```