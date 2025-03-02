import jenkins.model.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
def instance = Jenkins.getInstance()
def pluginManager = instance.getPluginManager()
def updateCenter = instance.getUpdateCenter()

// Überprüfen, ob das 'role-strategy' Plugin installiert ist
def plugin = pluginManager.getPlugin("role-strategy")
if (plugin == null) {
    logger.info("Installing 'role-strategy' plugin...")
    def pluginDeployment = updateCenter.getPlugin("role-strategy").deploy()
    pluginDeployment.get()
    logger.info("'role-strategy' plugin installed.")
    instance.save()
    instance.restart()
} else {
    logger.info("'role-strategy' plugin is already installed.")
}