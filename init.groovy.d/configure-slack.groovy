import jenkins.model.*
import jenkins.plugins.slack.*

// Obtenez l'instance de Jenkins
def instance = Jenkins.getInstance()

// Configurez le plugin Slack
def slack = Jenkins.instance.getDescriptorByType(jenkins.plugins.slack.SlackNotifier.DescriptorImpl)

// Définissez les paramètres de configuration Slack

slack.teamDomain = "devopsm1efrei"
slack.tokenCredentialId = "slack-token"
slack.room = "#projet-jenkins"
slack.botUser = true

// Sauvegardez la configuration Slack
slack.save()

// Sauvegardez la configuration de Jenkins
instance.save()
