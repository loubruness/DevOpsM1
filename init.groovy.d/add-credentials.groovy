#!groovy
import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret



global_domain = Domain.global()
credentials_store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

creds = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL,
                                    'jenkins-ssh-key', // credentialsId
                                    'jenkins', // userId
                                    new BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource('/usr/share/jenkins/ref/.ssh/id_rsa'), // privateKeySource
                                    '', // passphrase
                                    '') // description




def slackCredentials = new StringCredentialsImpl(
                                    CredentialsScope.GLOBAL,
                                    'slack-token', // ID des credentials
                                    'Slack API token', // Description des credentials
                                    Secret.fromString('xoxb-7350845836756-7348539158866-9RTJTHRJl4EWWCVFSRFZF56z') // Token Slack (remplacer par votre vrai token)
)
credentials_store.addCredentials(global_domain, slackCredentials)
credentials_store.addCredentials(global_domain, creds)
